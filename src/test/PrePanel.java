/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 *
 * @author dell-pc
 */
public class PrePanel extends JFrame{
    
    public static final int WIDTH = 500;
    public static final int HEIGHT = 300;
          
    public PrePanel(){
        // 设置窗口的内容面板
        Subpanel pre_result = new Subpanel(); 
        setContentPane(pre_result);
        // 设置窗口标题、大小、退出键
        setTitle("分类结果");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - WIDTH)/2,
            (Toolkit.getDefaultToolkit().getScreenSize().height - HEIGHT)/2 , WIDTH, HEIGHT);
        setResizable(false);
        setVisible(true);
    }
}

class Subpanel extends JPanel{  
    protected void paintComponent(Graphics g){  
        setBackground(Color.white);
        
        Classifiertest cls = new Classifiertest();
        int[] distr = cls.StatisticsResult();
        int CenterX,CenterY;  
        int r;  
        CenterX = this.getWidth();  
        CenterY = this.getHeight();  
        r = this.getHeight()/3;  
        super.paintComponent(g);  
        
        int a = 0;
        int b = distr[0]*360/distr[4];
        g.setColor(Color.blue);   
        g.fillArc(CenterX/6, CenterY/6, 2*r, 2*r, a, b);  
        
        a = distr[0]*360/distr[4];
        b = distr[1]*360/distr[4];
        g.setColor(Color.red);   
        g.fillArc(CenterX/6, CenterY/6, 2*r, 2*r, a, b); 
        
        a = (distr[0]+distr[1])*360/distr[4];
        b = distr[2]*360/distr[4];
        g.setColor(Color.yellow);   
        g.fillArc(CenterX/6, CenterY/6, 2*r, 2*r, a, b);
        
        a = (distr[0]+distr[1]+distr[2])*360/distr[4];
        b = distr[3]*360/distr[4];
        g.setColor(Color.green);   
        g.fillArc(CenterX/6, CenterY/6, 2*r, 2*r, a, b); 
        
        //图例
        g.setColor(Color.blue);
        g.fillRect(CenterX/2+80, CenterY/8+100, 20, 10);
        g.setColor(Color.black);
        g.drawString("Overview", CenterX/2+110, CenterY/8+110);  
    }  
}
