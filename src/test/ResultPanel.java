/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author dell-pc
 */
public class ResultPanel extends JFrame{
    
    public static final int WIDTH = 500;
    public static final int HEIGHT = 300;
    
    private JTextArea txtArea; //输出区
    private JLabel out_label; //输出label
    private JScrollPane js; //滚动条
          
    public ResultPanel(String classname){        
        // 设置窗口标题、大小、退出键
        setTitle("评论关键信息");
        setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - WIDTH)/2,
            (Toolkit.getDefaultToolkit().getScreenSize().height - HEIGHT)/2 , WIDTH, HEIGHT);        
        // 设置窗口的内容
        String chname = "";
        if(classname.equals("Overview")){
            chname = "综合评价";
        }
        if(classname.equals("Invalid")){
            chname = "无效评价";
        }
        if(classname.equals("Demand")){
            chname = "需求评价";
        }
        if(classname.equals("Specific")){
            chname = "具体评价";
        }
        out_label = new JLabel(chname+"类别关键信息");
        txtArea = new JTextArea(10,40);
        js = new JScrollPane(txtArea);
        js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        setLayout(new FlowLayout());
        add(out_label);
        add(js);
        setResizable(false);
        setVisible(true);
        //显示信息
        SQL s = new SQL();
        List list = s.GetContentwithClass(classname);
        for(int i = 0; i< list.size(); i++){
            txtArea.append(list.get(i).toString()+"\n");
            txtArea.paintImmediately(txtArea.getBounds());
        }
        txtArea.append("\n显示完毕！");
        txtArea.paintImmediately(txtArea.getBounds());
        list.clear();
    }
    
}
