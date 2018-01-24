/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import edu.stanford.nlp.trees.Tree;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dell-pc
 */
class point{
    public String t = null; //语法树
    public int flag = -1;  //标记
    
    public String getT(){
        return t;
    }
    public void setT(String t){
        this.t = t;
    }
}

public class KMeansCluster {
    private final String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //加载JDBC驱动
    private final String dbURL = "jdbc:sqlserver://localhost:1433; DatabaseName=mypro"; //连接服务器和数据库mypro
    private final String userName = "song"; 
    private final String userPwd = "123456"; 
    private Connection conn;  
    
    point[] data;//数据集
    point[] old_center = null;//原始聚类中心
    point[] new_center = null;//新的聚类中心
    int IterNum = 1;//迭代次数
    SQL s = new SQL();
    /**
     * 导入数据
     */
    public void ImportData(String table_name){
        try {
            int num = s.GetDataNum(table_name);
            data = new point[num];
            
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            
            //列名记得修改为变量！！！！！！！！！！！！！！！！！！！！！！！！！！
            rs=stmt.executeQuery("SELECT ast FROM "+table_name);
            rs.first();//读取数据库第一行记录
            for(int i = 0;i < num ;i++){   
                data[i] = new point();// 对象创建
                String ast = rs.getString("ast");
                data[i].setT(ast);
                rs.next();
            }
            rs.close();
            stmt.close();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KMeansCluster.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(KMeansCluster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * 选择聚类中心
     */
    public void ChooseCenter(String table_name){
        int num = s.GetDataNum(table_name);//数据集数量
        Scanner cin = new Scanner(System.in);
    //    System.out.print("请输入初始化聚类中心个数（随机产生）：");
    //    int center = cin.nextInt();
        int center = 4;
        this.old_center = new point[center]; //存放聚类中心
        this.new_center = new point[center];
        
        Random rand = new Random();
        //产生不重复的随机数
        int[] temp = new int[center];
        int count = 0;
        while(count < center){
            int thistemp = rand.nextInt(num);
            boolean flag = true;
            for(int i = 0;i < center; i++){
                if(thistemp == temp[i]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                temp[count] = thistemp;
                count++;
            }
        }
        //生成聚类中心      
        for(int i = 0; i < center;i++){
            int thistemp = temp[i];
            old_center[i] = new point();
            old_center[i].t = data[thistemp].t;
            old_center[i].flag = 0; //0表示聚类中心
        }
        
        System.out.println("初始聚类中心：");
        for (int i = 0; i < old_center.length; i++) {
            System.out.println(old_center[i].t);
        }
    }
    
    /**
     * 判断每个点属于哪个聚类中心
     */
    public void Classified(){
        for(int i =0;i<data.length;i++){
            float dist = 999;
            int lable = -1;
            for(int j = 0;j < old_center.length;j++){
                float distance = Similarity(data[i],old_center[j]);
                if(distance < dist){
                    dist = distance;
                    lable = j;
                }
            }
            data[i].flag = lable+1;
        }
    }
    
    /**
     * 重新计算聚类中心  ？？？？相似度最小的两棵树取交集
     */
    public void CalCenter(){
        for(int i = 0;i < old_center.length;i++){
            point newcore = new point();
           // 新的聚类中心
            new_center[i] = new point();
            new_center[i].t = newcore.t;
            new_center[i].flag = 0; 
        }
        
    }
    /**
     * 相似度计算，两棵树之间的距离函数
     */
    
    public Float Similarity(point a, point b){
        float sim = 0;
        String t1 = a.t;
        String t2 = b.t;
        String[] aa = t1.split("\\,");
        String[] bb = t2.split("\\,");
        int count = 0;
        for(int i = 0; i < aa.length; i++){
            String m = aa[i];
            for(int j = 0; j < bb.length; j++){
                String n = bb[j];
                if(m.equals(n)){
                    count++;
                }
            }
        }
        int same = count;
        int min = Math.min(aa.length, bb.length);
        sim = (float)same/min;
        return sim;
    }
    
    /**
     * 更新原始的聚类中心   
     */
    public void RenewOldCenter(point[] old, point[] news) {
        for (int i = 0; i < old.length; i++) {
            old[i].t = news[i].t;
            old[i].flag = 0;// 表示为聚类中心的标志。
        }
    }
    
    /**
     * 迭代
     */
    public void Iteration(){
        for(int i = 0;i < IterNum;i++){
            this.Classified();//各数据归类
     //       this.CalCenter();//重新计算聚类中心
     //       this.RenewOldCenter(old_center, new_center);//更新聚类中心
        }
        System.out.println("聚类结束！！！");
    }
    /**
     * 输出聚类中心
     */
    public void ResultOut(String table_name){
        int num = s.GetDataNum(table_name);
        int[] count = new int[old_center.length];
        for(int i =0;i < old_center.length ; i++){
            System.out.println("聚类中心："+old_center[i].t);
            for (int j = 0; j < data.length; j++) {
                if (data[j].flag == (i + 1)) {
                    count[i]++;
                //    System.out.println(data[j].t);                    
                }
            }
            float per = (float)100*count[i]/num;
            System.out.println("Cluster"+i+"：  "+ count[i] +"    所占比例： "+ per +"%");
        }
    }
}
