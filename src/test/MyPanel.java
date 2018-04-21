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
    private JButton button_save;
    private JButton button_pre_result;
//    private final JButton button_submit = new JButton("确定");
//    private final JTextField txtField = new JTextField(5);; //输入区
//    private JLabel in_label; //输出label
    private JTextArea txtArea; //输出区
    private JLabel out_label; //输出label
    private JScrollPane js; //滚动条
    
    public static final int WIDTH = 500;
    public static final int HEIGHT = 300;
    
//    public String n; //预测集数据数目
/*    public String table_name = "test10000"; //预测集表名
    
    public void settablename(String name){
        table_name = name;
    }
    public String gettablename(){
        return table_name;
    }
   */    
    public void MainPanel(){
        //设置文本框和标签
    //    in_label = new JLabel("请输入预测数据集的评论数量（0-10,000）");
        txtArea = new JTextArea(10,40);
        out_label = new JLabel("该算法训练模型评估结果");
        js = new JScrollPane(txtArea);
        //设置按钮
        button_SMO = new JButton("SMO分类");
        button_save = new JButton("保存");
        button_pre_result = new JButton("分类结果");
        
        //布局
        setTitle("Classifier");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //关闭退出进程
        setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - WIDTH)/2,
            (Toolkit.getDefaultToolkit().getScreenSize().height - HEIGHT)/2 , WIDTH, HEIGHT);
        
        js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        setLayout(new FlowLayout());
    //    add(in_label);
    //    add(txtField);
    //    add(button_submit);
        add(out_label);
        add(js);
        add(button_SMO);
        add(button_save);
        add(button_pre_result);
        
        setResizable(false);
        setVisible(true);
        
        //监听
        this.button_SMO.addActionListener(this);
        this.button_save.addActionListener(this);
        this.button_pre_result.addActionListener(this);
    //    this.button_submit.addActionListener(this);
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
        if(e.getSource() == this.button_SMO)
        {
            this.txtArea.append("正在建模并分类...\n");
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
        if(e.getSource() == this.button_pre_result)
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
        }
    }    
}      
