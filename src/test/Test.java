/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import edu.stanford.nlp.trees.Tree;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell-pc
 */
public class Test {
    
    private static final String table_name = "test50";
    private static Map<String,List> listmap = new HashMap<>(); //存储评论对应的依存关系
    private static Map<String,Tree> treemap = new HashMap<>(); //存储评论对应的语法树
    
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
     * 增加列名并标记文本属性特征对应的值（单词类）
     * @param str 文本属性特征
     */
    public void MarkWordFeature(String str){ 
        SQL s = new SQL();
        //设置列名
        String col = "[" + str +"]";
        //标记特征
        str = str.toLowerCase();
        String type = "int";
        s.AddColumn(table_name,col, type);//添加特征对应的列
        s.RemarkWordFeature(table_name, col, str); //在对应单词特征列标记数值，1表示符合该特征，0表示不符合
    }
    
    /**
     * 增加列名并标记文本属性特征对应的值（句法树类-无效评论）
     * @param col 列名
     */
    public void MarkInvalidFeature(String col){ 
        SQL s = new SQL();
        String type = "int";
        s.AddColumn(table_name,col, type);//添加特征对应的列
        s.RemarkInvaildFeature(table_name, col,treemap,listmap); //在对应评论特征列标记数值，1表示符合该特征，0表示不符合
    }
    
    /**
     * 标记所有定义好的文本属性特征
     */
    public void MarkALLFeature(){
        //Demand-advice
        this.MarkWordFeature("add");
        this.MarkWordFeature("could");
        this.MarkWordFeature("improvement");
        this.MarkWordFeature("instead of");
        this.MarkWordFeature("lack");
        this.MarkWordFeature("miss");
        this.MarkWordFeature("need");
        this.MarkWordFeature("must");
        this.MarkWordFeature("please");
        this.MarkWordFeature("prefer");
        this.MarkWordFeature("should");
        this.MarkWordFeature("suggest");
        this.MarkWordFeature("want");
        this.MarkWordFeature("wish");
        this.MarkWordFeature("hope");
        this.MarkWordFeature("will");
        this.MarkWordFeature("would");
        this.MarkWordFeature("old version");
        this.MarkWordFeature("lost");
        //Demand-wrong
        this.MarkWordFeature("not");
        this.MarkWordFeature("doesn't");
        this.MarkWordFeature("won't");
        this.MarkWordFeature("does not");
        this.MarkWordFeature("can't");
        this.MarkWordFeature("could not");
        this.MarkWordFeature("couldn't");
        this.MarkWordFeature("wrong");
        this.MarkWordFeature("broken");
        this.MarkWordFeature("fix");
        this.MarkWordFeature("unable to");
        this.MarkWordFeature("tired");
        this.MarkWordFeature("incorrect");
        this.MarkWordFeature("update");
        //Demand-phrase
        this.MarkWordFeature("won't open");
        this.MarkWordFeature("does not work");
        this.MarkWordFeature("Please fix");
        this.MarkWordFeature("needs to add");
        this.MarkWordFeature("Please add");
        this.MarkWordFeature("I was really hoping that");
        this.MarkWordFeature("Please update with");
        //Overview
        this.MarkWordFeature("great");
        this.MarkWordFeature("great app");
        this.MarkWordFeature("thanks");
        this.MarkWordFeature("amazing");
        this.MarkWordFeature("useless");
        this.MarkWordFeature("bad");
        this.MarkWordFeature("delete");
        this.MarkWordFeature("love it");
        this.MarkWordFeature("Convenient");
        this.MarkWordFeature("Thx");
        this.MarkWordFeature("use");
        this.MarkWordFeature("easy");
        this.MarkWordFeature("friendly");
        this.MarkWordFeature("useful");
        this.MarkWordFeature("recommended");
        this.MarkWordFeature("safe");
        this.MarkWordFeature("like");
        this.MarkWordFeature("excellent");
        this.MarkWordFeature("good");
        this.MarkWordFeature("fooled");
        this.MarkWordFeature("free");
        this.MarkWordFeature("Terrible");
        this.MarkWordFeature("Horrible");
        //Specific
        this.MarkWordFeature("Preferred the old display");
        this.MarkWordFeature("new version");
        this.MarkWordFeature("Helpful to");
        this.MarkWordFeature("is easy to do");
        this.MarkWordFeature("it's very convenient for");
        this.MarkWordFeature("I am glad");
        //Invalid
        this.MarkInvalidFeature("JustNN");
    }
         
    public static void main(String[] args) throws Exception{
              
    /*    SQL s = new SQL();
    //    s.SqltoShort(table_name);//批量将长文本化为单句
    //    s.DealNullData(table_name);//删除无英文字母的无效评论
        String col = "num";
        String type = "int";
    //    s.AddColumn(table_name,col, type);//添加单词数目列
        s.RemarkNumberofWords(table_name, col);//标记单词数
    */
    /*    SQL s = new SQL();
        treemap = s.RecordTreeMap(table_name);//解析语法树
        listmap = s.RecordDepMap(table_name);//解析依存关系
        Test t = new Test();
        t.MarkALLFeature();
        */
    /*            
        Tregex t = new Tregex();
        t.Tregextest2();
      
    /*    Test k = new Test();
        k.KMeans();
    */    
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
        
       new MyPanel();        
    //    s.AppsToDB("D:\\aaMyPRo\\data\\apps.dat","Apps",5);
    //    s.ReviewsToDB("D:\\aaMyPRo\\data\\reviews.dat","Reviews",5);
    }
}
 
   
 
 
