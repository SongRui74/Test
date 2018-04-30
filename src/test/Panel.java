/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.api.Partition;
import org.gephi.appearance.api.PartitionFunction;
import org.gephi.appearance.plugin.PartitionElementColorTransformer;
import org.gephi.appearance.plugin.palette.Palette;
import org.gephi.appearance.plugin.palette.PaletteManager;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.database.drivers.SQLServerDriver;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.plugin.database.EdgeListDatabaseImpl;
import org.gephi.io.importer.plugin.database.ImporterEdgeList;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.preview.api.G2DTarget;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewMouseEvent;
import org.gephi.preview.api.PreviewProperties;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.RenderTarget;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;


class Info{
    public String APP_ID = "";
    public String APP_Name_ = "";
    public String APP_description = "";
    public String APP_category = "";
    public String Reviewer_Name = "";
    public String Rating = "";
    public String Review_Content = "";
    public String info = "";
}
/**
 *
 * @author dell-pc
 */
public class Panel {
    
    private String classid = "";
    private String classname = "";
    
    
    
    public void setClassid(String cid){
        classid = cid;
    }
    
    public String getClassname(){
        if(classid.equals("10001")){
            classname = "综合评价";
        }
        if(classid.equals("10002")){
            classname = "具体评价";
        }
        if(classid.equals("10003")){
            classname = "需求评价";
        }
        if(classid.equals("10004")){
            classname = "无效评价";
        }
        return classname;
    }
    
    public void myPanel(){
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();
        
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        AppearanceController appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
        AppearanceModel appearanceModel = appearanceController.getModel();
         
        //Import database
        EdgeListDatabaseImpl db = new EdgeListDatabaseImpl();
        db.setSQLDriver(new SQLServerDriver());
        db.setPort(1433);
        db.setDBName("mypro");
        db.setHost("localhost");
        db.setUsername("song");
        db.setPasswd("123456");
        db.setNodeQuery("SELECT ID AS id ,info as label ,classes FROM gephi where classid = '"+classid+"' or ID = '"+classid+"'");
        db.setEdgeQuery("SELECT ID AS source, classid AS target, info as label,classes FROM gephi where  classid = '"+classid+"' or ID = '"+classid+"'");
        ImporterEdgeList edgeListImporter = new ImporterEdgeList();
        Container container = importController.importDatabase(db, edgeListImporter);
        container.getLoader().setAllowAutoNode(false);      //Don't create missing nodes
        container.getLoader().setEdgeDefault(EdgeDirectionDefault.UNDIRECTED);   //Force UNDIRECTED

        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);
        
        //See if graph is well imported
        DirectedGraph graph = graphModel.getDirectedGraph();        
    //    System.out.println("Nodes: " + graph.getNodeCount());
    //    System.out.println("Edges: " + graph.getEdgeCount());
        
        
        //节点颜色 Partition with 'classes ' column, which is in the data
        Column column = graphModel.getNodeTable().getColumn("classes");
        Function func = appearanceModel.getNodeFunction(graph, column, PartitionElementColorTransformer.class);
        Partition partition = ((PartitionFunction) func).getPartition();
        Palette palette = PaletteManager.getInstance().generatePalette(partition.size());
        partition.setColors(palette.getColors());
        appearanceController.transform(func);
        
        //布局 Run YifanHuLayout for 100 passes - The layout always takes the current visible view
        YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
        layout.setGraphModel(graphModel);
        layout.resetPropertiesValues();
        layout.setOptimalDistance(200f);
        layout.initAlgo();
  
        for (int i = 0; i < 100 && layout.canAlgo(); i++) {
            layout.goAlgo();
        }
        layout.endAlgo();
        
        //显示Preview configuration
        PreviewModel previewModel = previewController.getModel();
        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.WHITE));
        previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 50);
        previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.BLACK);
    	
        G2DTarget target = (G2DTarget) previewController.getRenderTarget(RenderTarget.G2D_TARGET); 
        final PreviewSketch previewSketch = new PreviewSketch(target);
        previewController.refreshPreview();
        
        //评论数据详细信息显示
        SQL s = new SQL();
        Info info = new Info();
        //鼠标响应
        PreviewProperties properties = new PreviewProperties();
        previewSketch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                previewController.sendMouseEvent(previewSketch.buildPreviewMouseEvent(e, PreviewMouseEvent.Type.CLICKED));                
                PreviewMouseEvent event = previewSketch.buildPreviewMouseEvent(e, PreviewMouseEvent.Type.CLICKED);
                for (Node node : Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace).getGraph().getNodes()) {
                    if (clickingInNode(node, event)) {
                        properties.putValue("display-label.node.id", node.getId());
                        //显示信息
                    //    int id = 5000;
                        String id = node.getId().toString();
                        try {
                            ResultSet rs = s.Showinfo(id);
                            rs.next();
                            info.APP_ID = rs.getString("APP_ID");
                            info.APP_Name_= rs.getString("APP_Name_");
                            info.APP_category = rs.getString("APP_category");
                            info.APP_description = rs.getString("APP_description");
                            info.Reviewer_Name = rs.getString("Reviewer_Name");
                            info.Rating = rs.getString("Rating");
                            info.Review_Content = rs.getString("Review_Content");
                            info.info = rs.getString("info");
                        } catch (ClassNotFoundException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (SQLException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        JOptionPane.showMessageDialog(null, "关键内容：\n" + node.getLabel() +"\n原评论内容：\n"+info.Review_Content);
                        event.setConsumed(true);//So the renderer is executed and the graph repainted
                        return;
                    }
                }
                previewSketch.refreshLoop.refreshSketch();
                
            }
            private boolean clickingInNode(Node node, PreviewMouseEvent event) {
                float xdiff = node.x() - event.x;
                float ydiff = -node.y() - event.y;//Note that y axis is inverse for node coordinates
                float radius = node.size();

                return xdiff * xdiff + ydiff * ydiff < radius * radius;
            }
        });
         
        //JFrame and display
        JFrame frame = new JFrame(this.getClassname() +"类别结果展示");
        frame.setLayout(new BorderLayout());
        frame.setSize(1300, 700);
        
        ResultPanel p = new ResultPanel();
        frame.add(p.infoPanel(this.getClassname()),BorderLayout.EAST);
        frame.add(previewSketch);
        
        //Wait for the frame to be visible before painting, or the result drawing will be strange
        frame.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                previewSketch.resetZoom();
            }
        });
        frame.setVisible(true);
    }
}