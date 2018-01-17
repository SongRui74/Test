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
    
    private static String table_name = "cpy_test";
         
    public static void main(String[] args) throws Exception{
        
    //    Classifiertest test = new Classifiertest();
     //   test.TestCluster();
        SQL s = new SQL();
        s.SqltoShort(table_name);//批量将长文本化为单句
        s.DealNullData(table_name);//删除无英文字母的无效评论
        String col = "old_ast";
        String type = "int null";
     //   s.AddColumn(table_name,col, type);//添加列
        Standfordnlp t = new Standfordnlp();
        t.RemarkFeedbackTree(table_name,col);//标记ast
     //  new MyPanel();        
    //    s.AppsToDB("D:\\aaMyPRo\\data\\apps.dat","Apps",5);
    //    s.ReviewsToDB("D:\\aaMyPRo\\data\\reviews.dat","Reviews",5);
        
    }
    
   
}
 
   
 
 
