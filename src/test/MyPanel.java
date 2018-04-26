/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
/**
 *
 * @author dell-pc
 */
public class MyPanel extends JFrame implements ActionListener
{
       /**
     * @param args the command line arguments
     */
    private JButton button_SMOCls;
    private JButton button_save;
//    private JButton button_pre_result;
    private JButton button_SMOEval;
    private JComboBox jcombo;
    
    private JTextArea txtArea; //输出区
    private JLabel out_label; //输出label
    private JScrollPane js; //滚动条
    
    public static final int WIDTH = 500;
    public static final int HEIGHT = 300;
    
    
    public void MainPanel(){
        //设置文本框和标签
        txtArea = new JTextArea(10,40);
        out_label = new JLabel("该算法训练模型评估结果");
        js = new JScrollPane(txtArea);
        //设置按钮
        button_SMOEval = new JButton("SMO评估");
        button_SMOCls = new JButton("分类");
        button_save = new JButton("保存");
    //    button_pre_result = new JButton("分类结果");
        
        //设置下拉列表
        String [] c = {"查看结果"," ","结果概览","综合评价","具体评价","需求评价","无效评价"}; //定义字符串
        jcombo = new JComboBox(c);   //实例化下拉列表
        
        //布局
        setTitle("Classifier");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //关闭退出进程
        setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - WIDTH)/2,
            (Toolkit.getDefaultToolkit().getScreenSize().height - HEIGHT)/2 , WIDTH, HEIGHT);
        
        js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        setLayout(new FlowLayout());
    
        add(out_label);
        add(js);
        add(button_SMOEval);
        add(button_SMOCls);
        add(button_save);
    //    add(button_pre_result);
        add(jcombo);
        
        setResizable(false);
        setVisible(true);
        
        //按钮监听
        this.button_SMOEval.addActionListener(this);
        this.button_SMOCls.addActionListener(this);
        this.button_save.addActionListener(this);
    //    this.button_pre_result.addActionListener(this);
        //    this.button_submit.addActionListener(this);
        //下拉框监听
        this.jcombo.addItemListener(new ItemListener()
        {
         @Override
            public void itemStateChanged(ItemEvent e){
                //选中
                if (e.getStateChange() == ItemEvent.SELECTED){
                //事件  
                    
                    String classname =(String) jcombo.getSelectedItem(); 
                    if(classname.equals("查看结果")||classname.equals(" ")){
                        ;
                    }
                    else if(classname.equals("结果概览")){
                        Classifiertest cls = new Classifiertest();            
                        int[] distr = cls.StatisticsResult();
                        txtArea.append("==================预测集分类结果=====================\n");
                        txtArea.append("Overview类别：\t" + distr[0] + "\n");
                        txtArea.append("Invalid类别： \t" + distr[1] + "\n");
                        txtArea.append("Demand类别： \t" + distr[2] + "\n");
                        txtArea.append("Specific类别：\t" + distr[3] + "\n");
                        //调用结果子面板
                        PrePanel prePanel = new PrePanel();
                    }
                    else{
                        txtArea.append("\n正在处理数据...\n");
                        txtArea.paintImmediately(txtArea.getBounds());
                        String classid = "";
                        if(classname.equals("综合评价")){
                            classid = "10001";
                        }
                        if(classname.equals("具体评价")){
                            classid = "10002";
                        }
                        if(classname.equals("需求评价")){
                            classid = "10003";
                        }
                        if(classname.equals("无效评价")){
                            classid = "10004";
                        }
                        //gephi结果
                        Panel a = new Panel();
                        a.setClassid(classid);
                        a.myPanel();
                        txtArea.append("处理完成!\n");
                        txtArea.paintImmediately(txtArea.getBounds());
                    }
                }
            }
        });
    
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
    /*    n = txtField.getText()+"";
        if(n.equals("")){
            n = "10000";
            settablename("test10000");
        }
        else{
            settablename("test" + n);
        }
        if(e.getSource() == this.button_submit)
        {
            this.txtArea.setText("正在创建预测集...\n");
            txtArea.paintImmediately(txtArea.getBounds());            
            int num = Integer.parseInt(n);   
            SQL s = new SQL();
            s.CreatePre(num);
        //    Features fea = new Features();
        //    fea.RemarkAll(num);
        //    this.txtArea.append("该预测集特征标记完成！\n");
                
            this.txtArea.append("该预测集创建完成！\n");
        }*/
        if(e.getSource() == this.button_SMOEval)
        {
            this.txtArea.append("正在建模...\n");
            txtArea.paintImmediately(txtArea.getBounds());
            Classifiertest cls = new Classifiertest();
            try {
                cls.SMOEval();
                this.txtArea.append(cls.getSMOResult());
            } catch (Exception ex) {
                Logger.getLogger(MyPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.txtArea.append("该数据集集训练完成！\n");
            txtArea.paintImmediately(txtArea.getBounds());               
        }
        if(e.getSource() == this.button_SMOCls)
        {
            this.txtArea.append("正在导入训练模型并分类...\n");
            txtArea.paintImmediately(txtArea.getBounds());
            Classifiertest cls = new Classifiertest();
            try {
                cls.SMO();
                this.txtArea.append(cls.getSMOResult());
            } catch (Exception ex) {
                Logger.getLogger(MyPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.txtArea.append("该预测集分类完成！\n");
            txtArea.paintImmediately(txtArea.getBounds());               
        }
        if(e.getSource() == this.button_save)
        {
            Classifiertest cls = new Classifiertest();
            this.txtArea.append("正在存储分类结果...\n");
            txtArea.paintImmediately(txtArea.getBounds());
            cls.RecordClassifyResult();
            this.txtArea.append("数据库分类结果存储完成！\n");
            txtArea.paintImmediately(txtArea.getBounds());
        }
    /*    if(e.getSource() == this.button_pre_result)
        {
            Classifiertest cls = new Classifiertest();            
            int[] distr = cls.StatisticsResult();
            this.txtArea.append("==================预测集分类结果=====================\n");
            this.txtArea.append("Overview类别：\t" + distr[0] + "\n");
            this.txtArea.append("Invalid类别： \t" + distr[1] + "\n");
            this.txtArea.append("Demand类别： \t" + distr[2] + "\n");
            this.txtArea.append("Specific类别：\t" + distr[3] + "\n");
            //调用结果子面板
            PrePanel prePanel = new PrePanel();
        }*/
    } 
}      