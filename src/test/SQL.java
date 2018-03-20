/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dell-pc
 */
public class SQL {
    private final String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //加载JDBC驱动
    private final String dbURL = "jdbc:sqlserver://localhost:1433; DatabaseName=mypro"; //连接服务器和数据库mypro
    private final String userName = "song"; 
    private final String userPwd = "123456"; 
    private Connection conn;    
    
    /**
     * 标记文本属性特征
     * @param table_name 表名
     * @param col 列名
     * @param str 特征字符串
     */
    public void RemarkTextFeature(String table_name,String col,String str){
        try {             
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();       
            
            rs=stmt.executeQuery("SELECT * FROM "+table_name);  
                
            int num = 0;//记录评论中是否包含该特征
            //循环标记
            while(rs.next()){                                            
                /*文本特征判断*/
                String content = rs.getString("Review_Content");
                content = content.toLowerCase();//转化为小写
                if(content.contains(str)){
                    num = 1; //存在该特征记为1
                }  
                else{
                    num = 0; 
                }
                /*写入数据库中*/
                String sql = UpdateSql(rs,table_name,col,num);
                stmt2.executeUpdate(sql);
            }
            rs.close();
            stmt.close(); 
            stmt2.close();
            conn.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
    
    /**
     * 获取评论单词数目
     * @param str 一条用户评论
     * @return 返回评论单词数目
     */
    public int NumberofWords(String str){
        int num = 0;
        String s[] = str.split(" |\\.|\\?|,|!|\\\"");
        for(int i = 0;i<s.length;i++){
            if(s[i].length() == 0)
                num++;
        }
        num = s.length-num;
        return num;
    }
    /**
     * 标记评论单词总数
     * @param table_name 表名
     * @param col 列名
     */
    public void RemarkNumberofWords(String table_name,String col){
        try {             
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();       
            
            rs=stmt.executeQuery("SELECT * FROM "+table_name);                  
            //循环标记num
            while(rs.next()){                                            
                /*统计每条文本的单词数目*/
                int num = this.NumberofWords(rs.getString("Review_Content"));          
                /*写入数据库中*/
                String sql = UpdateSql(rs,table_name,col,num);
                stmt2.executeUpdate(sql);
            }
            rs.close();
            stmt.close(); 
            stmt2.close();
            conn.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * 获取表中数据数量
     * @param table 表名
     * @return 返回表中数据数量
     */
    public int GetDataNum(String table){
        int num = 0;
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
            
            Statement stmt = conn.createStatement();
            ResultSet rs;
            String sql = null;
            String tempcol = "a";
            sql = "select count(*) as "+ tempcol + " from "+ table;
            rs = stmt.executeQuery(sql);
            rs.next();
            num = rs.getInt(tempcol);
            
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return num;
    }
    
    /**
     * 获取表中字段数量
     * @param table 表名
     * @return 返回表中字段数量
     */
    public int GetColNum(String table){
        int num = 0;
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
            
            Statement stmt = conn.createStatement();
            ResultSet rs;
            String sql = null;
            String tempcol = "a";            
            sql = "select count(*) as "+ tempcol + " from syscolumns where id = object_id('"+table+"')";
            rs = stmt.executeQuery(sql);
            rs.next();
            num = rs.getInt(tempcol);
            
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return num;
    }
    
    /**
     * 增加列
     * @param table 表名
     * @param col  增加的列名
     * @param type 增加的列类型
     */
    public void AddColumn(String table,String col,String type){
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
            
            Statement stmt = conn.createStatement();
            String sql = null;
            sql = "ALTER TABLE "+ table + " ADD "+ col +" "+ type;
            stmt.executeUpdate(sql);
            
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * 删除列
     * @param table 表名
     * @param col  删除的列名
     */
    public void DelColumn(String table,String col){
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
            
            Statement stmt = conn.createStatement();
            String sql = null;
            sql = "ALTER TABLE "+ table + " drop column "+ col;
            stmt.executeUpdate(sql);
            
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 处理不含英文文本的数据
     * @param table
     */
    public void DealNullData(String table){
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
            
            Statement stmt  = conn.createStatement();
            String sql = null;
            sql = "delete from " + table +" where Review_Content not like '%[a-zA-Z]%' or Review_Content not like '%[a-zA-Z]%'";
            stmt.executeUpdate(sql);
            
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    /**
     * 批量将评论拆分为单句
     * @param table_name
     */
    public void SqltoShort(String table_name){
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            //  System.out.println("Connection Successful!");
            rs=stmt.executeQuery("SELECT Review_Content FROM "+table_name);  
            while(rs.next()){          
                if (rs.getString("Review_Content").contains(".")||rs.getString("Review_Content").contains("!")||rs.getString("Review_Content").contains("?")){
                    String[] numdot = rs.getString("Review_Content").split("\\.|!|\\?");
                    if(numdot.length>1){
                        LongTexttoShort(rs,table_name);
                    }
                    else
                        ;
                }
                else
                    ;
            }
            rs.close();
            stmt.close();
            conn.close();
        }catch (SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 长文本化为单句
     * @param rs
     * @param table_name
     */
    public void LongTexttoShort(ResultSet rs,String table_name){
        try {
            String old_content = SqlSingleQuote(rs.getString("Review_Content"));//分句之前的长文本处理单引号
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            Statement st2 = conn.createStatement();
            String sql = null;
            /*存储该元组的其他信息*/
            sql = "SELECT * FROM "+ table_name +" WHERE Review_Content = '" + old_content + "'"; 
            ResultSet rstemp = st.executeQuery(sql);
            rstemp.first();
            String[] content_info = new String[4];            
            content_info[0] = rstemp.getString("APP_ID");
            content_info[1] = rstemp.getString("Reviewer_Name");
            content_info[2] = rstemp.getString("Rating");
            content_info[3] = rstemp.getString("Review_Title");
        //    content_info[4] = rstemp.getString("Review_Content");
        //    content_info[4] = rstemp.getString("classes");
            //处理单引号
            for (int i = 0;i<content_info.length;i++) {
                content_info[i] = SqlSingleQuote(content_info[i]);
            }            
            /*以句号叹号问号分割评论，并将分割后的评论依次插入数据库中*/
            String[] new_contents = old_content.split("\\.|!|\\?");
            
            sql = "UPDATE "+ table_name +" SET Review_Content = '"+ new_contents[0] +"' WHERE Review_Content = '" + old_content + "'";
            st2.executeUpdate(sql);
            for(int i=1;i<new_contents.length;i++){
                sql = "INSERT INTO "+ table_name +" VALUES ('"+ content_info[0] +"', '" 
                        + content_info[1] + "','"+ content_info[2] +"', '"
                        + content_info[3] + "','"+ new_contents[i] +"')";
                    //    + content_info[3] + "','"+ new_contents[i] +"', '"
                    //    + content_info[4] +"')";
                st2.executeUpdate(sql);
            }                        
            st.close();
            st2.close();
            rstemp.close();
        } catch (SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
    }        
    /**
     * 更改数据库查询语句
     * @param rs
     * @param table_name 表名
     * @param col 列名
     * @param new_value 新值
     * @return 返回一条sql语句
     */
    public String UpdateSql(ResultSet rs,String table_name,String col,Object new_value){
        String sql = null;
        try {
            String str = SqlSingleQuote(rs.getString("Review_Content"));
            String id = rs.getString("APP_ID");
            sql = "UPDATE "+ table_name +" SET " + col + " = '" + new_value + "' where Review_Content = '"+ str +"' and APP_ID = '"+ id +"'";
        } catch (SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sql;
    }
    /**
     * 处理评论中的单引号，便于sql语句顺利执行
     * @param str 一条未处理的评论
     * @return 返回已处理单引号的评论
     */
    public String SqlSingleQuote(String str){
        if(str.contains("'")){
            return str.replace("'", "''");
        }
        else
            return str;
    }
    
    /**
     * bat格式评论文件写入数据库
     * @param readpath  文件路径
     * @param table  数据库表名
     * @param n  属性个数
     * @return 
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public String ReviewsToDB(String readpath,String table,int n) throws IOException, ClassNotFoundException, SQLException{   
        Class.forName(driverName);
        conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
        Statement stmt = conn.createStatement();
        String sql; //SQL语句      
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
                sql = "INSERT INTO "+ table +" VALUES ('"+temp;
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
    /**
     * bat格式Apps描述文件写入数据库
     * @param readpath
     * @param table
     * @param n
     * @throws java.io.IOException
     */  
    public String AppsToDB(String readpath,String table,int n) throws IOException, ClassNotFoundException, SQLException{    //文件路径，数据库表名，属性个数
        Class.forName(driverName);
        conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
        Statement stmt = conn.createStatement();
        String sql; //SQL语句     
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
                sql = "INSERT INTO "+ table +" VALUES ('"+temp;    
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
