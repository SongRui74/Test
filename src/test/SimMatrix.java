/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import edu.stanford.nlp.trees.Tree;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author dell-pc
 */
public class SimMatrix {
    private final String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //加载JDBC驱动
    private final String dbURL = "jdbc:sqlserver://localhost:1433; DatabaseName=mypro"; //连接服务器和数据库mypro
    private final String userName = "song"; 
    private final String userPwd = "123456"; 
    private Connection conn;  
    private String table_name;

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }
    
    SQL s = new SQL();
    Standfordnlp nlp = new Standfordnlp();
    
    private final Map<String,List> listmap = new HashMap<>(); //存储评论对应的依存关系
    private final Map<String,Tree> treemap = new HashMap<>(); //存储评论对应的语法树
    private final Map<Integer,String> indexmap = new HashMap<>(); //存储评论对应的序号
    
    public double[][] Matrix;//相似度存放数组
    /**
     * 从某表中导入数据
     */
    public void ImportData(){
        try {           
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            
            int num = s.GetDataNum(this.getTable_name());
            
            rs=stmt.executeQuery("SELECT * FROM "+table_name);
            rs.first();//读取数据库第一行记录
            for(int i = 0;i < num ;i++){   
                String content = rs.getString("Review_Content");
                //解析语法树和依存关系存入相应的map中
                Tree temptree = nlp.FeedbacktoTree(content);
                List templist = nlp.FeedbacktoDep(content);
                
                indexmap.put(i, content);
                treemap.put(content, temptree);
                listmap.put(content, templist);
                
                rs.next();
            }
            rs.close();
            stmt.close();
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(KMeansCluster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //计算相似度
    public double sim(int i,int j){
        double fsimi = 0;
        String t1 = indexmap.get(i);; //获取两条评论内容
        String t2 = indexmap.get(j);
        Tree tree1 = treemap.get(t1);  //找到对应的树和依存关系
        Tree tree2 = treemap.get(t2);
        
        List list1 = listmap.get(t1);
        List list2 = listmap.get(t2);
        
        List a12 = nlp.CalSimi(list1, list2, tree1, tree2);
        List a21 = nlp.CalSimi(list2, list1, tree2, tree1);
        List v12 = nlp.SimiVector(a12);
        List v21 = nlp.SimiVector(a21);
        List v = nlp.FinalVector(v12, v21);
        double simi = nlp.guiyihua(v);
        fsimi = nlp.fsim(simi, v);
        
        return fsimi;
    }
    
    public void calMatrix(){
        int num = indexmap.size();
        Matrix = new double[num][num];

        //初始化
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                Matrix[i] = new double[num];
                Matrix[i][j] = 0;
                Matrix[i][i] = 1;
            }
        }
        //计算一半的矩阵
        for (int i = 0; i < num; i++) {
            for (int j = i+1; j < num; j++) {
                Matrix[i][j] = this.sim(i, j);
            }
        }
        //矩阵对阵
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                Matrix[j][i] = Matrix[i][j];
            }
        }        
//        //输出
//        for (int i = 0; i < num; i++) {
//            for (int j = 0; j < num; j++) {
//                System.out.print(Matrix[i][j]+"\t");
//            }
//            System.out.print("\n");
//        }       
    }
    
    public void Writetxt() throws IOException{
        File file = new File(".\\Matrix.txt");  //存放数组数据的文件
        FileWriter out = new FileWriter(file);  //文件写入流
        int n = indexmap.size();
        //将数组中的数据写入到文件中。每行各数据之间TAB间隔
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                out.write(Matrix[i][j] + "\t");
            }
            out.write("\r\n");
        }
        out.close();
    }
    
    public void Readtxt() throws IOException{
        File file = new File(".\\a100.txt");
    //    int n = indexmap.size();
        int n=100;
        double[][] arr2 = new double[n][n];  //读取出的数组
        BufferedReader in = new BufferedReader(new FileReader(file));  //
        String line;  //一行数据
        int row = 0;
        //逐行读取，并将每个数组放入到数组中
        while ((line = in.readLine()) != null) {
            String[] temp = line.split("\t");
            for (int j = 0; j < temp.length; j++) {
                arr2[row][j] = Double.parseDouble(temp[j]);
            }
            row++;
        }
        in.close();
        
//        //输出
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < n; j++) {
//                System.out.print(arr2[i][j]+"\t");
//            }
//            System.out.print("\n");
//        } 
    }
}
