/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import edu.stanford.nlp.trees.Tree;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dell-pc
 */
public class Features {
    private static String table_name;
    private static Map<String,Tree> treemap = new HashMap<>(); //存储评论对应的语法树
    
     /**
     * 增加列名并标记文本属性特征对应的值（单词类）
     * @param str 文本属性特征
     */
    public void MarkWordFeature(String str){ 
        SQL s = new SQL();
        //设置列名
        String col = "[" + str +"]"; 
        if(str.contains("_")){
            str = str.substring(1);
        }
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
        s.RemarkInvaildFeature(table_name, col,treemap); //在对应评论特征列标记数值，1表示符合该特征，0表示不符合
    }
    
    /**
     * 增加列名并标记文本属性特征对应的值（句法树类）
     * @param str 匹配的表达式
     */
    public void MarkTreeFeature(String str){ 
        SQL s = new SQL();
        String col = null;
        String type = "int";
        if(!str.contains("[")){
            col = "["+str+"]";
        }
        else{
            col = str.replace("]", "_");
            col = "["+ col.replace("[", "_") + "]";
        }
        s.AddColumn(table_name,col, type);//添加特征对应的列
        s.RemarkTreeFeature(table_name, col, treemap, str);//在对应评论特征列标记数值，1表示符合该特征，0表示不符合
    }
    
    /**
     * 增加列名并标记文本属性特征对应的值（依存关系类）
     * @param str 匹配的表达式
     */
    public void MarkDepFeature(String str){ 
        SQL s = new SQL();
        String col = null;
        String type = "int";
        if(!str.contains("[")){
            col = "["+str+"]";
        }
        else{
            col = str.replace("]", "_");
            col = "["+ col.replace("[", "_") + "]";
        }
        s.AddColumn(table_name,col, type);//添加特征对应的列
        s.RemarkDepFeature(table_name, col, treemap, str);//在对应评论特征列标记数值，1表示符合该特征，0表示不符合
    }
    
    /**
     * 标记所有定义好的文本属性特征
     */
    public void MarkALLFeature(){
        //Demand-advice
        this.MarkWordFeature("add ");
        this.MarkWordFeature("could ");
        this.MarkWordFeature("improvement");
        this.MarkWordFeature("lack");
        this.MarkWordFeature("miss");
        this.MarkWordFeature("need");        
        this.MarkWordFeature("_need");
        this.MarkWordFeature("must ");
        this.MarkWordFeature("please");
        this.MarkWordFeature("_please");
        this.MarkWordFeature("should");
        this.MarkWordFeature("suggest");
        this.MarkWordFeature("want");
        this.MarkWordFeature("wish");
        this.MarkWordFeature("hope");
        this.MarkWordFeature("_wish");
        this.MarkWordFeature("_hope");
        this.MarkWordFeature("will");
        this.MarkWordFeature("would");
        this.MarkWordFeature("lost");
        this.MarkWordFeature("_lost");
        this.MarkWordFeature("give");
        this.MarkWordFeature("again");
        this.MarkWordFeature("limit");
        //Demand-wrong
        this.MarkWordFeature("disappear");
        this.MarkWordFeature("problem");
        this.MarkWordFeature("_problem");
        this.MarkWordFeature("error");
        this.MarkWordFeature("bug");
        this.MarkWordFeature("none");
        this.MarkWordFeature("no ");        
        this.MarkWordFeature("_no ");
        this.MarkWordFeature("not ");        
        this.MarkWordFeature("did ");
        this.MarkWordFeature("didn't");
        this.MarkWordFeature("doesn't");
        this.MarkWordFeature("won't");
        this.MarkWordFeature("does not");
        this.MarkWordFeature("can't");
        this.MarkWordFeature("could not");
        this.MarkWordFeature("couldn't");
        this.MarkWordFeature("shouldn't");
        this.MarkWordFeature("wrong");
        this.MarkWordFeature("broken");
        this.MarkWordFeature("fix");
        this.MarkWordFeature("unable");
        this.MarkWordFeature("incorrect");
        this.MarkWordFeature("update");
        this.MarkWordFeature("crash");
        this.MarkWordFeature("inaccurate");
        this.MarkWordFeature("frozen");
        this.MarkWordFeature("even");
        this.MarkWordFeature("fail");
        this.MarkWordFeature("_fail");
        this.MarkWordFeature("back");
        this.MarkWordFeature("login");    
        this.MarkWordFeature("close");
        this.MarkWordFeature("work");
        //Demand-phrase
        this.MarkWordFeature("should be");
        this.MarkWordFeature("have to");
        this.MarkWordFeature("had to");
        this.MarkWordFeature("want to");
        this.MarkWordFeature("log in");
        this.MarkWordFeature("logged in");
        this.MarkWordFeature("not working");
        this.MarkWordFeature("doesn't work");
        this.MarkWordFeature("Please fix");
        this.MarkWordFeature("Please add");
        this.MarkWordFeature("wish it");
        this.MarkWordFeature("Just wish");
        this.MarkWordFeature("Touch ID");
        //Overview
        this.MarkWordFeature("Love this app");
        this.MarkWordFeature("thanks");
        this.MarkWordFeature("Thx");
        this.MarkWordFeature("love it");
    /*    this.MarkWordFeature("love");
        this.MarkWordFeature("use");
        this.MarkWordFeature("great"); 
        this.MarkWordFeature("amazing");
        this.MarkWordFeature("useless");
        this.MarkWordFeature("bad");
        this.MarkWordFeature("delete");
        this.MarkWordFeature("Convenient");
        this.MarkWordFeature("friendly");
        this.MarkWordFeature("useful");
        this.MarkWordFeature("recommend");
        this.MarkWordFeature("safe");
        this.MarkWordFeature("like");
        this.MarkWordFeature("excellent");
        this.MarkWordFeature("good");
        this.MarkWordFeature("fool");
        this.MarkWordFeature("free");
        this.MarkWordFeature("Terrible");
        this.MarkWordFeature("Horrible");
        this.MarkWordFeature("download");  
        this.MarkWordFeature("easy to");
    */    //Specific
    //    this.MarkWordFeature("easy to use");
    //    this.MarkWordFeature("_prefer");
    //    this.MarkWordFeature("is easy to do");
    //    this.MarkWordFeature("_is easy to do");
    //    this.MarkWordFeature("_before");
    //    this.MarkWordFeature("new version");
    //    this.MarkWordFeature("user friendly");
    //    this.MarkWordFeature("glad that");
    //    this.MarkWordFeature("old");
    //    this.MarkWordFeature("new");        
    //    this.MarkWordFeature("Helpful to");
        this.MarkWordFeature("prefer");
        this.MarkWordFeature("can ");
        this.MarkWordFeature("better");
        this.MarkWordFeature("prettier");
        this.MarkWordFeature("worse");
        this.MarkWordFeature("slow");
        this.MarkWordFeature("before");
        this.MarkWordFeature("beautiful");
        this.MarkWordFeature("helps me");
        //Invalid
        this.MarkInvalidFeature("IsInvalid");
        
        //Overview
    //    this.MarkTreeFeature("VP..( NN..JJ | ..JJR | ..JJS ) | ..( NN $+ JJ | $+ JJR | $+ JJS )");
    //    this.MarkDepFeature("{tag:/VB.*/} > nsubj {tag:/NN.*/} = A  : {tag:/NN.*/} = A > amod {tag:/JJ.*/}");
    /*    this.MarkTreeFeature("NP < ( JJ $+ NN | $+ NNS | $+ NNP | $+ NNPS )");
        this.MarkTreeFeature("( NP < PRP ) $+ ( VP < ( ADJP < JJ))");
        this.MarkTreeFeature("JJ $++ CC $++ JJ");
        this.MarkTreeFeature("RB $+ JJ");
        this.MarkDepFeature("{tag:/JJ./} > advmod {tag:RB}"); 
        this.MarkTreeFeature("NN..( DT $++ NN )"); 
    */   
        //Specific
        this.MarkTreeFeature("NP ( << VBG | << NN ) $++ (VP << @/JJ.?/) : NP !<< app");
        this.MarkDepFeature("{tag:VBG} >> ccomp {tag:/JJ.*/} = A : {tag:/JJ.*/} = A > cop {tag:/VB.*/}");
        this.MarkDepFeature("{tag:/NN.*/} << nsubj {tag:/JJ.*/} : !{word:app} << nsubj {tag:/JJ.*/}");
        this.MarkDepFeature("{tag:/NN.*/} >> amod {tag:/JJ.*/} : !{word:app} >> amod {tag:/JJ.*/}");
        this.MarkTreeFeature("( NP < PRP ) $+ ( VP < ( ADJP < (JJ $+ PP | $+ SBAR)))");
        this.MarkTreeFeature("VB < use & $+ NP");
        this.MarkDepFeature("{word:use} > dobj {tag:/NN.*/}");
        this.MarkDepFeature("{word:can} < aux {tag:VB}");
        this.MarkTreeFeature("MD < can & $+ VP");
        this.MarkTreeFeature("(VBP < love | < like) .. (NP !<< app)");
        this.MarkTreeFeature("(VB < love | < like) .. (NP !<< app)");
        this.MarkDepFeature("{word:love} > dobj {tag:/NN.*/} & !> dobj {word:app} | < case {tag:/NN.*/}");
        this.MarkDepFeature("{word:like} > dobj {tag:/NN.*/} & !> dobj {word:app} | < case {tag:/NN.*/}");
        this.MarkTreeFeature("JJ $++ (@/NN.?/ !<< app)");
        this.MarkTreeFeature("NP < ( JJ $+ @/NN.?/) : @/NN.?/ !<< app");
        this.MarkDepFeature("{tag:JJ} < amod {tag:/NN.*/} & !< amod {word:app}");
        this.MarkDepFeature("{tag:/VB.*/} < ccomp {word:helps} | < ccomp {word:help}");
        this.MarkTreeFeature("VBZ < helps $+ S");
        this.MarkTreeFeature("VB < help $+ S");
        this.MarkTreeFeature("JJ < helpful & $+ S");
        this.MarkTreeFeature("JJ < helpful & $+ PP");
        this.MarkTreeFeature("NP (<< new | (<<new & [<< VBG | << NN]) ) $++ (VP << @/JJ.?/)");
        this.MarkDepFeature("{word:new} < amod {tag:/NN.*/}=A : {tag:/NN.*/}=A < nsubj {tag:/JJ.*/}");
        this.MarkDepFeature("{word:new} < nsubj {tag:/VB.*/}=A : {tag:/VB.*/}=A > ccomp {tag:/JJ.*/}");
        this.MarkTreeFeature("NP (<< old | (<<old & [<< VBG | << NN]) ) $++ (VP << @/JJ.?/)");
        this.MarkDepFeature("{word:old} < amod {tag:/NN.*/}=A : {tag:/NN.*/}=A < nsubj {tag:/JJ.*/}");
        this.MarkDepFeature("{word:old} < nsubj {tag:/VB.*/}=A : {tag:/VB.*/}=A > ccomp {tag:/JJ.*/}");
        this.MarkDepFeature("{word:easy} >> xcomp {tag:/VB.*/} & !>> xcomp {word:do} & !>> xcomp {word:use}");
        //部分特征加强
        this.MarkTreeFeature("(@/NN.?/ !<< app) $-- JJ");
        this.MarkDepFeature("{tag:JJ} !< amod {word:app} & < amod {tag:/NN.*/}");
        this.MarkDepFeature("{tag:/VB.*/} < ccomp {word:help} | < ccomp {word:helps}");
        this.MarkTreeFeature("(VBZ < helps) $+ S");
        this.MarkTreeFeature("(VB < help) $+ S");
        this.MarkTreeFeature("(JJ < helpful) $+ S");
        this.MarkTreeFeature("(JJ < helpful) $+ PP");
        this.MarkDepFeature("{word:easy} >> xcomp {tag:/VB.*/} & !>> xcomp {word:use} & !>> xcomp {word:do}");
        this.MarkDepFeature("!{word:app} << nsubj {tag:/JJ.*/} : {tag:/NN.*/} << nsubj {tag:/JJ.*/}");
        this.MarkDepFeature("!{word:app} >> amod {tag:/JJ.*/} : {tag:/NN.*/} >> amod {tag:/JJ.*/}");
        //Demand
        this.MarkTreeFeature("NP $+ (VP < ( RB [ $- MD | $- VBZ] ) & << VB)");
        this.MarkDepFeature("{word:no} < neg {tag:NN}");
    }
    
    /**
     * 创建预测集并标记所有文本特征
     * @param num  预测集数量
     */
/*    public void RemarkAll(int num){
        SQL s = new SQL();
        s.CreatePre(num);
        String n = Integer.toString(num);
        table_name = "test" + n;
        treemap = s.RecordTreeMap(table_name);//解析语法树 
        this.MarkALLFeature();
    }
*/    
    /**
     * 记录类别的关键信息
     * @param str 匹配关系式
     */
    public void MarkInfo(String str){
        SQL s = new SQL();
        if(str.contains("<") || str.contains(",,"))
            s.ExtractInfo(table_name, "info", treemap, str);
        else
            s.ExtractWordInfo(table_name, "info", treemap, str);
    }
    
    /**
     * 记录类别的关键信息
     */
    public void MarkAllInfo(){
        SQL s = new SQL();
        table_name = "Specific";
        treemap = s.RecordTreeMap(table_name);//解析语法树 
        //Specific
        
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
        
        //Demand
        table_name = "Demand";
        treemap = s.RecordTreeMap(table_name);//解析语法树   
        
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
        this.MarkInfo("please update");
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
        this.MarkInfo("fail");
        this.MarkInfo("NP < (DT < no)");  
        
    }
    
    //标记训练集特征
    public void RemarkTrainAll(){
        SQL s = new SQL();
        s.CreateTrain();
        table_name = "train";
        treemap = s.RecordTreeMap(table_name);//解析语法树 
        this.MarkALLFeature();
    }
    
    /**
     * 标记预测集特征
     */
    public void RemarkPreAll(){
        SQL s = new SQL();
        table_name = "pre";
        treemap = s.RecordTreeMap(table_name);//解析语法树 
        this.MarkALLFeature();
    }
    
}
