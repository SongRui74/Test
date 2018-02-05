/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import edu.stanford.nlp.trees.Tree;
import java.io.*;
import java.sql.*;
/**
 *
 * @author dell-pc
 */
public class Test {
    
    private static String table_name = "test2";
         
    public static void main(String[] args) throws Exception{
        
        SQL s = new SQL();
        s.GetDataNum(table_name);
        KMeansCluster km = new KMeansCluster();
        km.ImportData(table_name);
        km.ChooseCenter(table_name);
        km.Iteration();
        km.ResultOut(table_name);
               
    /*    s.SqltoShort(table_name);//批量将长文本化为单句
        s.DealNullData(table_name);//删除无英文字母的无效评论
        String col = "ast";
        String type = "varchar(max)";
        s.AddColumn(table_name,col, type);//添加列
        Standfordnlp t = new Standfordnlp();
        t.RemarkFeedbackTree(table_name,col);//标记ast
    */
    /*    Standfordnlp t = new Standfordnlp();      
        Tree tree = t.FeedbacktoTree("App doesn't work.");
        System.out.println("1）\"App doesn't work.\"的语法树");
        String a = t.TreetoString(tree);
        tree = t.FeedbacktoTree("The app can't open.");
        System.out.println("2）\"The app can't open.\"的语法树");
        String b = t.TreetoString(tree);
        t.Similarity(a, b);
        t.getNodeHashList();
    */   
     //  new MyPanel();        
    //    s.AppsToDB("D:\\aaMyPRo\\data\\apps.dat","Apps",5);
    //    s.ReviewsToDB("D:\\aaMyPRo\\data\\reviews.dat","Reviews",5);
        
    }   
}
 
   
 
 
