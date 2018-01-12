/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.*;
import java.sql.*;
/**
 *
 * @author dell-pc
 */
public class Test {
    private String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //加载JDBC驱动
    private String dbURL = "jdbc:sqlserver://localhost:1433; DatabaseName=mypro"; //连接服务器和数据库mypro
    private String userName = "song"; 
    private String userPwd = "123456"; 
    private Connection conn;
    private Statement stmt;
    
    
    public static void main(String[] args) throws Exception{
        
          Standfordnlp t = new Standfordnlp();
          t.RemarkFeedbackTree();
       // new MyPanel(); 
      //  Test data = new Test();        
    //    data.AppsToDB("D:\\aaMyPRo\\data\\apps.dat","Apps",5);
    //    data.FileToDB("D:\\aaMyPRo\\data\\reviews.dat","Reviews",5);
        
    }
    
    
    //bat格式文件写入数据库
    public String FileToDB(String readpath,String table,int n) throws IOException, ClassNotFoundException, SQLException{    //文件路径，数据库表名，属性个数
        Class.forName(driverName);
        conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
        stmt = conn.createStatement();
        String sql; //SQL语句
        String table_name=table; //数据库表名        
     //   System.out.println("Connection Successful!"); //如果连接成功 控制台输出Connection Successful!                 
        File readfile=new File(readpath);
        if(!readfile.exists()||readfile.isDirectory())
            throw new FileNotFoundException();
        BufferedReader br=new BufferedReader(new FileReader(readfile));
        String temp=null;
        StringBuffer sb=new StringBuffer();
        temp=br.readLine();//按行读入 ,第一行为int，不存在引号等特殊字符
        while(temp != null){
            if(temp.length()!=0){                
                sql = "INSERT INTO "+table_name+" VALUES ('"+temp;
                for(int i=0;i<n-1;i++){       //按属性个数读取文件行数插入数值
                    temp = br.readLine();
                    if(!temp.contains("'"))//该行不存在单引号
                        temp = temp;
                    else{        //该行存在单引号
                        temp = temp.replace("'", "''");  //将单引号替换成两个，便于在SQL语句中使用
                    }
                    sql += "','"+ temp;
                }
                sql += "')";
            //    System.out.println(sql);                 
                stmt.executeUpdate(sql);   //执行SQL语句 
                temp=br.readLine();  //继续读入
            }
            else //遇到空行继续读入文本
                temp=br.readLine();  
        }
           return sb.toString();
     }
      
    public String AppsToDB(String readpath,String table,int n) throws IOException, ClassNotFoundException, SQLException{    //文件路径，数据库表名，属性个数
        Class.forName(driverName);
        conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
        stmt = conn.createStatement();
        String sql; //SQL语句
        String table_name=table; //数据库表名        
     //   System.out.println("Connection Successful!"); //如果连接成功 控制台输出Connection Successful!                 
        File readfile=new File(readpath);
        if(!readfile.exists()||readfile.isDirectory())
            throw new FileNotFoundException();
        BufferedReader br=new BufferedReader(new FileReader(readfile));
        String temp=null;
        StringBuffer sb=new StringBuffer();
        temp=br.readLine();//按行读入       
        while(temp!=null){  
            if(temp.length()!=0){   
                //写SQL语句
                sql = "INSERT INTO "+table_name+" VALUES ('"+temp;    
                for(int i=0;i<n-1;i++){       //按属性个数读取文件行数插入数值
                    temp = br.readLine();
                    sql = sql+"','"+ temp;
                }
                sql = sql+"')";
                stmt.executeUpdate(sql);//执行SQL语句
                temp=br.readLine();  //继续读入
            }
            else //遇到空行继续读入文本
                temp=br.readLine();  
         }
      //   System.out.println(sb);
         return sb.toString();
     }
   
}
 
   
 
 
