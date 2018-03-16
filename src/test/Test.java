/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import edu.stanford.nlp.trees.Tree;
import java.util.List;

/**
 *
 * @author dell-pc
 */
public class Test {
    
    private static final String table_name = "test50";
         
    public static void main(String[] args) throws Exception{
              
        KMeansCluster km = new KMeansCluster();
        km.ImportData(table_name);
        km.ChooseCenter(table_name);
        km.Iteration();
        km.ResultOut(table_name);
       
    /*    SQL s = new SQL();
        s.SqltoShort(table_name);//批量将长文本化为单句
        s.DealNullData(table_name);//删除无英文字母的无效评论
        String col = "ast";
        String type = "varchar(max)";
        s.DelColumn(table_name, col);
        s.AddColumn(table_name,col, type);//添加语法树列
        Standfordnlp t = new Standfordnlp();
        t.RemarkFeedbackTree(table_name,col);//标记ast
    
    /*    SQL s = new SQL();
        String col = "num";
        String type = "int";
        s.AddColumn(table_name,col, type);//添加单词数目列
        s.RemarkNumberofWords(table_name, col);//标记单词数
    */
        //Your happy passer-by all knows, my distressed there is no place hides.
    /*    Standfordnlp t = new Standfordnlp(); 
        String s1 = "App don't work..";        
        String s2 = "App can't open and don't work!"; 
        List l1 = t.FeedbacktoDep(s1);
        List l2 = t.FeedbacktoDep(s2);
        Tree t1 = t.FeedbacktoTree(s1);
        Tree t2 = t.FeedbacktoTree(s2);
        List a = t.CalSimi(l1,l2,t1,t2);
        t.SimiVector(a);
    */            
    //    t.getNodeHashList();
    /*    String a = t.TreetoString(tree);
        
        s = "App don't work.";
        tree = t.FeedbacktoTree(s);
        System.out.println("2）\"great app\"的语法树");
        String b = t.TreetoString(tree);
        t.Similarity(a, b);
        t.getNodeHashList();
    */   
     //  new MyPanel();        
    //    s.AppsToDB("D:\\aaMyPRo\\data\\apps.dat","Apps",5);
    //    s.ReviewsToDB("D:\\aaMyPRo\\data\\reviews.dat","Reviews",5);
        
    }
}
 
   
 
 
