/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import edu.stanford.nlp.trees.Tree;
import java.io.IOException;
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
    public void KMeans() throws IOException{
        KMeansCluster km = new KMeansCluster();
        km.setTable_name(table_name);
        km.ImportData();
        km.Readtxt();
        km.ChooseCenter();
        km.Iteration();
        km.ResultOut();
    }
         
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws Exception{
        
      //标记特征
//    Features f = new Features();
//    SQL s = new SQL();
//    Classifiertest c = new Classifiertest();
//    f.RemarkPreAll("data1");
//    s.Cpytable("data1");
//    c.settablename("data1");
//    c.SMO();
//    c.RecordClassifyResult();
     
    
     
        //主程序运行
    /*    MyPanel myPanel = new MyPanel();
        myPanel.MainPanel();
        
    /*    //提取关键信息
        Pattern p = new Pattern();
        p.MarkAllInfo();
    */            
    /*    Classifiertest c = new Classifiertest();
        c.SMOEval();
    */
        //评论信息写入txt文件
//        SQL s = new SQL();
//        s.InfotoTXT("train");//去停用词
//        s.DBtoTXT("Specific4");//没去停用词
        
        //记录相似度矩阵
        SimMatrix s = new SimMatrix();
        s.setTable_name("RoR");
        s.ImportData();
        s.calMatrix();
        s.Writetxt();
        s.Writedatatxt();
//        s.Readtxt();

        //聚类
//        Test k = new Test();
//        table_name = "RoR";
//        k.KMeans();
       
//        Standfordnlp s = new Standfordnlp();
//        String str = "Calculator sometimes doesn't work either ";
//        str = str.toLowerCase();
//        Tree tree = s.FeedbacktoTree(str);
//        tree.pennPrint();
//        List list = s.FeedbacktoDep(str);
//        System.out.println(list.toString());
//        String re = "S [<<new | <<old] & <<JJ";
//    //    String sm = "{word:easy} >> xcomp {tag:/VB./} & !>> xcomp {word:do} & !>> xcomp {word:use}";
//        Tregex t = new Tregex();
//        System.out.println(t.Tregexinfo(tree,re));     
////        boolean c = t.SemgrexIsMatch(tree, sm);
////        System.out.println(c);
        
     
//        Standfordnlp t = new Standfordnlp();
//        String s1 = "Good app";
//        String s2 = "outstanding app";
//        String s3 = "The app otherwise mostly works as promised";
//        t.wordweight();
//        List l1 = t.wordvector(s1);
//        List l2 = t.wordvector(s2);
//        t.totalvector(l1, l2);
//        System.out.println(t.totalvector(l1, l2));
        
        //词向量 
        //语义相似度
//        List l1 = t.FeedbacktoDep(s1);
//        List l2 = t.FeedbacktoDep(s2);
//        List l3 = t.FeedbacktoDep(s3);
//        Tree t1 = t.FeedbacktoTree(s1);
//        Tree t2 = t.FeedbacktoTree(s2);
//        Tree t3 = t.FeedbacktoTree(s3);
//        
//        List a12 = t.CalSimi(l1, l2, t1, t2);
//        List a21 = t.CalSimi(l2, l1, t2, t1);
//        List v12 = t.SimiVector(a12);
//        List v21 = t.SimiVector(a21);
//        List v = t.FinalVector(v12, v21);
//        double simi = t.guiyihua(v);
//        double fsimi = t.fsim(simi, v);
//        System.out.println("评论a的依存关系列表：\n"+l1.toString());
//        System.out.println("评论b的依存关系列表：\n"+l2.toString());
//        System.out.println("评论a的语法树：");
//        t1.pennPrint();
//        System.out.println("评论b的语法树：");
//        t2.pennPrint();
//        System.out.println("v:"+v.toString());
//        System.out.println(fsimi);
//        
//        List a13 = t.CalSimi(l1, l3, t1, t3);
//        List a31 = t.CalSimi(l3, l1, t3, t1);
//        List v13 = t.SimiVector(a13);
//        List v31 = t.SimiVector(a31);
//        List vv = t.FinalVector(v13, v31);
//        Double simii = t.guiyihua(vv);
//        double fsimii = t.fsim(simii, vv);
//        System.out.println("评论a的依存关系列表：\n"+l1.toString());
//        System.out.println("评论c的依存关系列表：\n"+l3.toString());
//        System.out.println("评论a的语法树：");
//        t1.pennPrint();
//        System.out.println("评论c的语法树：");
//        t3.pennPrint();
//        System.out.println("vv:"+vv.toString());
//        System.out.println(fsimii);
        
//        String re = "NP $+ (VP << use & <<NP)";
//    //    String sm = "{word:easy} >> xcomp {tag:/VB./} & !>> xcomp {word:do} & !>> xcomp {word:use}";
//        Tregex tt = new Tregex();
//        System.out.println(tt.Tregexinfo(t1,re));
//        System.out.println(tt.Tregexinfo(t2,re));
        
//        List a = t.CalSimi(l1,l2,t1,t2);
//        List vec = t.SimiVector(a);
//        double sim = 0;
//        int sum = 0;
//        for(int i = 0; i < vec.size(); i++){
//            double val = (double) vec.get(i);
//            sum += val*val;
//        }        
//        sim = Math.sqrt(sum);
//        
//        List c = t.CalSimi(l1,l3,t1,t3);
//        List vecc = t.SimiVector(c);
//        double simc = 0;
//        int sumc = 0;
//        for(int i = 0; i < vecc.size(); i++){
//            double val = (double) vecc.get(i);
//            sumc += val*val;
//        }        
//        simc = Math.sqrt(sumc);
//        
        
//        System.out.println("评论a的依存关系列表：\n"+l1.toString());
//        System.out.println("评论b的依存关系列表：\n"+l2.toString());
//        System.out.println("评论a的语法树：");
//        t1.pennPrint();
//        System.out.println("评论b的语法树：");
//        t2.pennPrint();
//        System.out.println("\n评论a与b的相似度向量：");
//        System.out.println(t.SimiVector(a).toString());
//        System.out.println("评论a与b的相似度为：\t"+sim);
//        
//        System.out.println("\n评论a的依存关系列表：\n"+l1.toString());
//        System.out.println("评论c的依存关系列表：\n"+l3.toString());
//        System.out.println("评论a的语法树：");
//        t1.pennPrint();
//        System.out.println("评论c的语法树：");
//        t3.pennPrint();
//        System.out.println("\n评论a与c的相似度向量：");
//        System.out.println(t.SimiVector(c).toString());
//        System.out.println("评论a与c的相似度为：\t"+simc);
        
        

//        SQL s = new SQL();
//        s.SqltoShort(table_name);//批量将长文本化为单句
//        s.DealNullData(table_name);//删除无英文字母的无效评论
//        s.DelInvaSymbol(table_name);//删除文本无用的字符
//        String col = "num";
//        String type = "int";
//        s.AddColumn(table_name,col, type);//添加单词数目列
//        s.RemarkNumberofWords(table_name, col);//标记单词数
        
//            s.AppsToDB("E:\\Program\\data\\apps.dat","Apps",5);
//            s.ReviewsToDB("E:\\Program\\data\\reviews.dat","Reviews",5);
        
        //    SQL s = new SQL();
    //    s.DBInfo("Specific4");
    //    s.DBInfo("Demand4");
    //    s.InfoDB("E:\\Program\\data\\Specific4data.txt", "data");
       
    }
}
 
   
 
 
