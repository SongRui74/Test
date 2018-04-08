/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JButton button_SMO;
    private JButton button_clear;
    private JButton button_pre_result;
    private JTextArea txtArea;
    private JLabel label;
    private JScrollPane js;
    
    public MyPanel(){
        //设置按钮
        button_SMO = new JButton("SMO分析");
        button_clear = new JButton("清空");
        button_pre_result = new JButton("分类结果");
        txtArea = new JTextArea(10,40);
        label = new JLabel("分析结果");
        js = new JScrollPane(txtArea);
        
        //布局
        setTitle("Classifier");
        setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - 500)/2,
            (Toolkit.getDefaultToolkit().getScreenSize().height - 300)/2 , 500, 300);
        js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        setLayout(new FlowLayout());
        add(label);
        add(js);
        add(button_SMO);
        add(button_clear);
        add(button_pre_result);
        setResizable(false);
        setVisible(true);
        
        //监听
        this.button_SMO.addActionListener(this);
        this.button_clear.addActionListener(this);
        this.button_pre_result.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == this.button_SMO)
        {
            Classifiertest test = new Classifiertest();
            try {
                this.txtArea.append(test.getSMOResult());
                this.txtArea.append("================================================\n");
            } catch (Exception ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(e.getSource() == this.button_clear)
        {
            this.txtArea.setText(null);
        }
        if(e.getSource() == this.button_pre_result)
        {
            //调用结果
        }
    }
    
    public void PreResult(){
    
    }
}
