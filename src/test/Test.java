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
    
    private static String table_name ;
    
    /**
     * 对给定表的评论数据聚类
     */
    public void KMeans(){
        KMeansCluster km = new KMeansCluster();
        km.setTable_name(table_name);
        km.ImportData();
        km.ChooseCenter();
        km.Iteration();
        km.ResultOut();
    }
         
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws Exception{
        
    /*    MyPanel myPanel = new MyPanel();
        myPanel.MainPanel();
        
    /*    Features f = new Features();
        f.MarkAllInfo();
    */
    /*    MainPartExtractor t = new  MainPartExtractor();
        String str = "App really needs Touch ID added ";
        t.NLP(str);
        t.getmainpart();
    //    t.getmainpart2(str);
    */   
        Pattern p = new Pattern();
        p.MarkAllInfo();
                
    /*              
        Test k = new Test();
        k.KMeans();
        App won't open after new update
    /*    Standfordnlp s = new Standfordnlp();
        String str = "The app is slow to login ";
        str = str.toLowerCase();
        Tree tree = s.FeedbacktoTree(str);
        tree.pennPrint();
        List list = s.FeedbacktoDep(str);
        System.out.println(list.toString());
        String re = "S [<<new | <<old] & <<JJ";
    //    String sm = "{word:easy} >> xcomp {tag:/VB./} & !>> xcomp {word:do} & !>> xcomp {word:use}";
        Tregex t = new Tregex();
        System.out.println(t.Tregexinfo(tree,re));     
    /*    boolean c = t.SemgrexIsMatch(tree, sm);
        System.out.println(c);
        
               
    /*    Standfordnlp t = new Standfordnlp();
        String s1 = "App don't work";
        String s2 = "App can't open";
        String s3 = "Add Touch ID";
        List l1 = t.FeedbacktoDep(s1);
        List l2 = t.FeedbacktoDep(s2);
        List l3 = t.FeedbacktoDep(s3);
        Tree t1 = t.FeedbacktoTree(s1);
        Tree t2 = t.FeedbacktoTree(s2);
        Tree t3 = t.FeedbacktoTree(s3);
        
        List a = t.CalSimi(l1,l2,t1,t2);
        List vec = t.SimiVector(a);
        double sim = 0;
        int sum = 0;
        for(int i = 0; i < vec.size(); i++){
            double val = (double) vec.get(i);
            sum += val*val;
        }        
        sim = Math.sqrt(sum);
        
        List c = t.CalSimi(l1,l3,t1,t3);
        List vecc = t.SimiVector(c);
        double simc = 0;
        int sumc = 0;
        for(int i = 0; i < vecc.size(); i++){
            double val = (double) vecc.get(i);
            sumc += val*val;
        }        
        simc = Math.sqrt(sumc);
        
        System.out.println("评论a的依存关系列表：\n"+l1.toString());
        System.out.println("评论b的依存关系列表：\n"+l2.toString());
        System.out.println("评论a的语法树：");
        t1.pennPrint();
        System.out.println("评论b的语法树：");
        t2.pennPrint();
        System.out.println("\n评论a与b的相似度向量：");
        System.out.println(t.SimiVector(a).toString());
        System.out.println("评论a与b的相似度为：\t"+sim);
        
        System.out.println("\n评论a的依存关系列表：\n"+l1.toString());
        System.out.println("评论c的依存关系列表：\n"+l3.toString());
        System.out.println("评论a的语法树：");
        t1.pennPrint();
        System.out.println("评论c的语法树：");
        t3.pennPrint();
        System.out.println("\n评论a与c的相似度向量：");
        System.out.println(t.SimiVector(c).toString());
        System.out.println("评论a与c的相似度为：\t"+simc);
         
        /*    SQL s = new SQL();
        s.SqltoShort(table_name);//批量将长文本化为单句
        s.DealNullData(table_name);//删除无英文字母的无效评论
        s.DelInvaSymbol(table_name);//删除文本无用的字符
        String col = "num";
        String type = "int";
        s.AddColumn(table_name,col, type);//添加单词数目列
        s.RemarkNumberofWords(table_name, col);//标记单词数
         */
        //    s.AppsToDB("D:\\aaMyPRo\\data\\apps.dat","Apps",5);
        //    s.ReviewsToDB("D:\\aaMyPRo\\data\\reviews.dat","Reviews",5);
    }
}
 
   
 
 
