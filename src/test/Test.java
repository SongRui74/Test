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
    
    private static final String table_name = "t12";
    
    //t4,
    /**
     * 对给定表的评论数据聚类
     */
    public void KMeans(){
        KMeansCluster km = new KMeansCluster();
        km.ImportData(table_name);
        km.ChooseCenter(table_name);
        km.Iteration();
        km.ResultOut(table_name);
    }
    
    /*
    public void MarkTextNum(){ //增加列名并标记
        SQL s = new SQL();
    /*    String col = "ast";
        String type = "varchar(max)";
        s.DelColumn(table_name, col);
        s.AddColumn(table_name,col, type);//添加语法树列
        Standfordnlp t = new Standfordnlp();
        */
    /*    String col = "num";
        String type = "int";
        s.AddColumn(table_name,col, type);//添加单词数目列
        s.RemarkNumberofWords(table_name, col);//标记单词数
    }
    */
    
    /**
     * 增加列名并标记文本属性特征对应的值
     * @param str 文本属性特征
     */
    public void MarkOneFeature(String str){ 
        SQL s = new SQL();
        String col = str;
        str = str.toLowerCase();
        String type = "int";
        s.AddColumn(table_name,col, type);//添加特征对应的列
        s.RemarkTextFeature(table_name, col, str); //在对应特征列标记数值，1表示符合该特征，0表示不符合
    }
    
    /**
     * 标记所有定义好的文本属性特征
     */
    public void MarkALLFeature(){
        this.MarkOneFeature("add");
        this.MarkOneFeature("could");
        this.MarkOneFeature("improvement");
        this.MarkOneFeature("instead of");
        this.MarkOneFeature("lack");
        this.MarkOneFeature("miss");
        this.MarkOneFeature("need");
        this.MarkOneFeature("must");
        this.MarkOneFeature("please");
        this.MarkOneFeature("prefer");
        this.MarkOneFeature("should");
        this.MarkOneFeature("suggest");
        this.MarkOneFeature("want");
        this.MarkOneFeature("wish");
        this.MarkOneFeature("hope");
        this.MarkOneFeature("will");
        this.MarkOneFeature("would");
        this.MarkOneFeature("old version");
        this.MarkOneFeature("lost");
        
        this.MarkOneFeature("not");
        this.MarkOneFeature("doesn’t");
        this.MarkOneFeature("won’t");
        this.MarkOneFeature("does not");
        this.MarkOneFeature("can’t");
        this.MarkOneFeature("could not");
        this.MarkOneFeature("couldn’t");
        this.MarkOneFeature("wrong");
        this.MarkOneFeature("broken");
        this.MarkOneFeature("fix");
        this.MarkOneFeature("unable to");
        this.MarkOneFeature("tired");
        this.MarkOneFeature("incorrect");
        this.MarkOneFeature("update");
        
        this.MarkOneFeature("won’t open");
        this.MarkOneFeature("does not work");
        this.MarkOneFeature("Please fix");
        this.MarkOneFeature("needs to add");
        this.MarkOneFeature("Please add");
        this.MarkOneFeature("I was really hoping that");
        this.MarkOneFeature("Please update with");
        
        this.MarkOneFeature("great app");
        this.MarkOneFeature("thanks");
        this.MarkOneFeature("amazing");
        this.MarkOneFeature("useless");
        this.MarkOneFeature("bad");
        this.MarkOneFeature("delete");
        this.MarkOneFeature("love it");
        this.MarkOneFeature("Convenient");
        this.MarkOneFeature("Thx");
        this.MarkOneFeature("use");
        this.MarkOneFeature("easy");
        this.MarkOneFeature("friendly");
        this.MarkOneFeature("useful");
        this.MarkOneFeature("recommended");
        this.MarkOneFeature("safe");
        this.MarkOneFeature("like");
        this.MarkOneFeature("excellent");
        this.MarkOneFeature("good");
        this.MarkOneFeature("fooled");
        this.MarkOneFeature("free");
        this.MarkOneFeature("Terrible");
        this.MarkOneFeature("Horrible");
        
        this.MarkOneFeature("Preferred the old display");
        this.MarkOneFeature("new version");
        this.MarkOneFeature("Helpful to");
        this.MarkOneFeature("is easy to do");
        this.MarkOneFeature("it's very convenient for");
        this.MarkOneFeature("I am glad");
    }
         
    public static void main(String[] args) throws Exception{
              
        SQL s = new SQL();
        s.SqltoShort(table_name);//批量将长文本化为单句
    //    s.DealNullData(table_name);//删除无英文字母的无效评论
    /*    String col = "num";
        String type = "int";
        s.AddColumn(table_name,col, type);//添加单词数目列
        s.RemarkNumberofWords(table_name, col);//标记单词数
    
        //Your happy passer-by all knows, my distressed there is no place hides.
    /*    Standfordnlp t = new Standfordnlp(); 
        String s1 = "App don't work.";        
        String s2 = "App can't open."; 
        List l1 = t.FeedbacktoDep(s1);
        List l2 = t.FeedbacktoDep(s2);
        Tree t1 = t.FeedbacktoTree(s1);
        Tree t2 = t.FeedbacktoTree(s2);
        List a = t.CalSimi(l1,l2,t1,t2);
        t.SimiVector(a);
    */  
        
     //  new MyPanel();        
    //    s.AppsToDB("D:\\aaMyPRo\\data\\apps.dat","Apps",5);
    //    s.ReviewsToDB("D:\\aaMyPRo\\data\\reviews.dat","Reviews",5);
        
    }
}
 
   
 
 
