/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author dell-pc
 */
public class Test {
    
    private static String table_name;
    
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
         
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws Exception{
        
    /*    Features f = new Features();
        f.MarkAllInfo();
    */
    /*    Panel a = new Panel();
        a.myPanel();
    */
        MyPanel myPanel = new MyPanel();
        myPanel.MainPanel();
        
    //    new ResultPanel("Specific");
        
    /*    Standfordnlp s = new Standfordnlp();
        String str = "No way to register payments, stupid omission ";
        str = str.toLowerCase();
        Tree tree = s.FeedbacktoTree(str);
        tree.pennPrint();
        List list = s.FeedbacktoDep(str);
        System.out.println(list.toString());
        String re = "NP < (DT < no)";
    //    String sm = "{word:easy} >> xcomp {tag:/VB./} & !>> xcomp {word:do} & !>> xcomp {word:use}";
        Tregex t = new Tregex();
        System.out.println(t.Tregexinfo(tree,re));     
    /*    boolean c = t.SemgrexIsMatch(tree, sm);
        System.out.println(c);
        
        /*          
        Test k = new Test();
        k.KMeans();
        
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
 
   
 
 
