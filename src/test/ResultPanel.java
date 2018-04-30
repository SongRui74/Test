/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.awt.Dimension;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author dell-pc
 */
public class ResultPanel {
    
    public static final int WIDTH = 500;
    public static final int HEIGHT = 300;
    
    private JTextArea txtArea; //输出区
    private JLabel out_label; //输出label
    private JScrollPane js; //滚动条
          
    public JPanel infoPanel(String classname){        
    //    JPanel a = new JPanel(new GridLayout(2,1));
        JPanel a = new JPanel();
        a.setPreferredSize(new Dimension(300,350));      
        // 设置窗口的内容
        String cname = "";
        if(classname.equals("综合评价")){
            cname = "Overview";
        }
        if(classname.equals("无效评价")){
            cname = "Invalid";
        }
        if(classname.equals("需求评价")){
            cname = "Demand";
        }
        if(classname.equals("具体评价")){
            cname = "Specific";
        }
        
    //    out_label = new JLabel("关键信息");
    //    out_label.setPreferredSize(new Dimension(10,20));        
        txtArea = new JTextArea(19,20);
        txtArea.setPreferredSize(null);
        js = new JScrollPane(txtArea);
        js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        
    //    a.add(out_label,BorderLayout.NORTH);
        a.add(js);
        a.setVisible(true);
        
        //显示信息
        SQL s = new SQL();
        List list = s.GetContentwithClass(cname);
        for(int i = 0; i< list.size(); i++){
            txtArea.append(list.get(i).toString()+"\n");
            txtArea.paintImmediately(txtArea.getBounds());
        }
        txtArea.append("\n显示完毕！");
        txtArea.paintImmediately(txtArea.getBounds());
        list.clear();
        
        return a;
    }
    
}
