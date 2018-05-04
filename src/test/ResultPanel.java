/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author dell-pc
 */
public class ResultPanel extends JPanel{
    
    public static final int WIDTH = 500;
    public static final int HEIGHT = 300;
    
    private JTextArea txtArea; //输出区
    private JLabel out_label; //输出label
    private JScrollPane js; //滚动条
           
    public JPanel infoList(String classname){    
        setLayout(new FlowLayout());
        setPreferredSize(new Dimension(300,350));  
        setFont(new Font("宋体",1,12));
        setBackground(Color.WHITE);
        
        out_label = new JLabel("该类别所有关键信息");    
        out_label.setFont(new Font("宋体",1,12));
        txtArea = new JTextArea(17,20);
        txtArea.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
        txtArea.setPreferredSize(null);
        js = new JScrollPane(txtArea);
        js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        
        add(out_label);
        add(js);
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
        
        return this;
    }
    
    @Override
    public void paintComponent(Graphics g) {  
        super.paintComponent(g);  
        //下面这行是为了背景图片可以跟随窗口自行调整大小，可以自己设置成固定大小
        //背景
        ImageIcon icon = new ImageIcon("D:\\aaMyPro\\MyPro\\Test\\img\\4.jpg");
        Image img = icon.getImage(); 
        
        g.drawImage(img, 0, 0,this.getWidth(), this.getHeight(), this);  
    }   
}
