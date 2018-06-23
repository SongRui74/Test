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
public class Pattern {
    
    private static String table_name;
    private static Map<String,Tree> treemap = new HashMap<>(); //存储评论对应的语法树
    private static Map<String,List> listmap = new HashMap<>(); //存储评论对应的依存关系
    private static Map<String,String> centermap = new HashMap<>(); //存储评论对应的中心词
    /**
     * 标记可以提取关键信息的评论
     * @param str 匹配关系式
     */
    public void MarkInfo(String str){
        SQL s = new SQL();
        s.RemarkPattern(table_name, "info", treemap, str);
    }
    
    /**
     * 完善记录类别的关键信息
     * @param str 匹配关系式
     */
    public void MarkInfoPlus(String str){
        SQL s = new SQL();
        s.InfoExtractorPlus(table_name, "info", treemap, str);
    }
    /**
     * 批量记录类别的关键信息
     */
    public void MarkAllInfo(){
        SQL s = new SQL();
    /*    table_name = "Specific4";
        treemap = s.RecordTreeMap(table_name);//解析语法树 
        listmap = s.RecordDepMap(table_name);
        centermap = s.RecordCenterMap(table_name);
        //Specific
        //标记待提取的评论        
        this.MarkInfo("VP $- (TO ,,(JJ < easy))");
        this.MarkInfo("S > (PP $- (JJ < helpful))");
        this.MarkInfo("S $- (JJ < helpful)");
        this.MarkInfo("VP,,helps");
        this.MarkInfo("NP,,helpful");
        this.MarkInfo("NP $- (VP << helps)");
        this.MarkInfo("NP > (VP << love)");
        this.MarkInfo("S [<<new | <<old] & <<JJ");
        this.MarkInfo("ADJP << JJR & <<before");
        this.MarkInfo("ADJP < (JJ $+ (PP << to))");
        
        //语义依赖模式提取
        this.MarkInfoPlus("VP $- (TO ,,(JJ < easy))");
        this.MarkInfoPlus("S > (PP $- (JJ < helpful))");
        this.MarkInfoPlus("S $- (JJ < helpful)");
        this.MarkInfoPlus("NP,,helpful");
        this.MarkInfoPlus("ADJP < (JJ $+ (PP << to))");
        //依存关系提取主干
        s.InfoExtractor(table_name, "info", treemap, listmap, centermap);
        
        //Demand
    */    table_name = "Demand3";
        treemap = s.RecordTreeMap(table_name);//解析语法树 
        listmap = s.RecordDepMap(table_name);
        centermap = s.RecordCenterMap(table_name);  
        
        this.MarkInfo("VP < (VBD $+ (RB $+ VP))");
        this.MarkInfo("VP < (VBZ $+ (RB $+ VP))");
        this.MarkInfo("VP < (MD $+ (RB $+ VP))");
        this.MarkInfo("VP < (VBZ $+ (RB $+ ADJP))");
        this.MarkInfo("ADJP < (RB $+ VBG)");
        this.MarkInfo("VP < (MD $+ RB ..VP)");
        this.MarkInfo("fix");
        this.MarkInfo("SINV << not");
        this.MarkInfo("need");
        this.MarkInfo("VP,,(VB < wish $+ (NP << it))");
        this.MarkInfo("S,,wish");
        this.MarkInfo("VP,,wish");
    //    this.MarkInfo("please update");
        this.MarkInfo("update");
        this.MarkInfo("crash");
        this.MarkInfo("NP < ((NP !<< app) $+ (ADJP << not))");
        this.MarkInfo("VP < (RB $+ VBG)");
        this.MarkInfo("S < (RB $+ VBG | $+ (VP < VBG))");
        this.MarkInfo("NP $- (VB < add)");
        this.MarkInfo("NP ,, (VB < add)");
        this.MarkInfo("VP $- (VB < add)");
        this.MarkInfo("version");
        this.MarkInfo("NP $- (VB < want)");
        this.MarkInfo("bug");
        this.MarkInfo("NP < (NN < problem)");
        this.MarkInfo("NP,,(MD $+ RB)");
        this.MarkInfo("ADJP < JJ & << to & << log");
    //    this.MarkInfo("fail");
        this.MarkInfo("NP < (DT < no)");   
    
        this.MarkInfoPlus("VP,,(VB < wish $+ (NP << it))");
        this.MarkInfoPlus("S,,wish");
        this.MarkInfoPlus("VP,,wish");
        this.MarkInfoPlus("NP $- (VB < add)");
        this.MarkInfoPlus("NP ,, (VB < add)");
        this.MarkInfoPlus("VP $- (VB < add)");
        this.MarkInfoPlus("fix");
        this.MarkInfoPlus("update");
        
        //依存关系提取主干
        s.InfoExtractor(table_name, "info", treemap, listmap, centermap);        
    }

}
