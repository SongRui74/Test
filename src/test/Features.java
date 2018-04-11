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
        this.MarkWordFeature("lack ");
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
    //    this.MarkWordFeature("help");
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
        
    */    this.MarkWordFeature("easy to use");
        this.MarkWordFeature("easy to");
        //Specific
        this.MarkWordFeature("prefer");
        this.MarkWordFeature("_prefer");
        this.MarkWordFeature("can ");
        this.MarkWordFeature("make");
        this.MarkWordFeature("made");
        this.MarkWordFeature("better");
        this.MarkWordFeature("prettier");
        this.MarkWordFeature("worse");
        this.MarkWordFeature("old");
        this.MarkWordFeature("new");
        this.MarkWordFeature("slow");
        this.MarkWordFeature("before");
        this.MarkWordFeature("_before");
        this.MarkWordFeature("new version");
        this.MarkWordFeature("user friendly");
        this.MarkWordFeature("glad that");
        this.MarkWordFeature("beautiful");
        this.MarkWordFeature("Helpful to");
        this.MarkWordFeature("helpfs me");
        this.MarkWordFeature("is easy to do");
        //Invalid
        this.MarkInvalidFeature("IsInvalid");
        //Tree
        //Overview
    //    this.MarkTreeFeature("VP..( NN..JJ | ..JJR | ..JJS ) | ..( NN $+ JJ | $+ JJR | $+ JJS )");
    //    this.MarkDepFeature("{tag:/VB.*/} > nsubj {tag:/NN.*/} = A  : {tag:/NN.*/} = A > amod {tag:/JJ.*/}");
    /*    this.MarkTreeFeature("NP < ( JJ $+ NN | $+ NNS | $+ NNP | $+ NNPS )");
        this.MarkTreeFeature("( NP < PRP ) $+ ( VP < ( ADJP < JJ))");
        this.MarkTreeFeature("JJ $++ CC $++ JJ");
        this.MarkTreeFeature("RB $+ JJ");
        this.MarkDepFeature("{tag:/JJ./} > advmod {tag:RB}"); 
        this.MarkTreeFeature("NN..( DT $++ NN )"); 
    */    //Specific
        this.MarkTreeFeature("JJ..( CC..JJ )");
        this.MarkTreeFeature("JJ .. ( TO $++ VP )");
        this.MarkTreeFeature("VBG ( .. VBZ | ..VBP | ..VBD ) .. JJ");
        this.MarkDepFeature("{tag:VBG} >> ccomp {tag:/JJ.*/} = A : {tag:/JJ.*/} = A > cop {tag:/VB.*/}");
        this.MarkDepFeature("{tag:/JJ.*/} = A > cop {tag:/VB.*/}");
        this.MarkTreeFeature("( NP < PRP ) $+ ( VP < ( ADJP < (JJ $+ PP | $+ SBAR)))");
        this.MarkTreeFeature("NN..VBD..JJ | ..JJR | ..JJS");
        this.MarkDepFeature("{word:old} < amod {tag:NN}");
        this.MarkDepFeature("{word:old} < amod {tag:NN} : {tag:/JJ.*/} > cop {word:was}");
        this.MarkTreeFeature("VB < use & $+ NP");
        this.MarkTreeFeature("for .. VBG | ..@/NN.?/");
        this.MarkDepFeature("{word:use} > dobj {tag:/NN.*/}");
        this.MarkDepFeature("{tag:JJ} > nmod {tag:/NN.*/} | > advcl {tag:VBG}");
        this.MarkDepFeature("{word:for} < mark {tag:VBG} | < case {tag:/NN.*/}");
        this.MarkDepFeature("{word:can} < aux {tag:VB}");
        this.MarkTreeFeature("MD < can & $+ VP");
    //    this.MarkTreeFeature("VP < VBZ & << ((NP < PRP) $+ VP)");
    //    this.MarkTreeFeature("VBZ ..((NP < PRP) $+ VP)");
    //    this.MarkDepFeature("{tag:VBZ} > ccomp {tag:/VB.*/}");
        this.MarkTreeFeature("(VBP < love | < like) .. (NP !<< app)");
        this.MarkTreeFeature("(VB < love | < like) .. (NP !<< app)");
        this.MarkDepFeature("{word:love} > dobj {tag:/NN.*/} & !> dobj {word:app} | < case {tag:/NN.*/}");
        this.MarkDepFeature("{word:like} > dobj {tag:/NN.*/} & !> dobj {word:app} | < case {tag:/NN.*/}");
        this.MarkTreeFeature("JJ $++ (NN !<< app)");
        this.MarkTreeFeature("NP < ( JJ $+ @/NN.?/) : @/NN.?/ !<< app");
        this.MarkDepFeature("{tag:JJ} < amod {tag:/NN.*/} & !< amod {word:app}");
        this.MarkDepFeature("{tag:/VB.*/} < ccomp {word:helps} | < ccomp {word:help}");
        this.MarkTreeFeature("VBZ < helps $+ S");
        this.MarkTreeFeature("VB < help $+ S");
        //Demand
        this.MarkTreeFeature("NP $+ (VP < ( RB [ $- MD | $- VBZ] ) & << VB)");
        this.MarkTreeFeature("( NP < PRP ) $+ ( VP << (VBG $+ SBAR))");
        this.MarkDepFeature("{word:no} < neg {tag:NN}");
        /*
        //ok
         Helpful videos to help get started and a powerful simulator
        This App really helps me keep up with my bills
          Helpful to check values throughout day
        It helps me manage my bills
         This helps me track every account and card I have
         Helps me keep track of my spendings, great union 
        Very helpful for checking balances 
        This App really helps me keep up with my bills
        //ok
         Nice graphs
         Great customer service
         This app is real-time with useful technical analysis
         What they had previously was actually a great UI
        Good UI
         Good customer support
         Nice interface, really easy and simple to use
         This app is real-time with useful technical analysis
        //ok
         Love being able to pay by photo
         Works really well, I love the TouchID login 
          Love the finger scan access and easy to pay bills
            Love the TouchID functionality on my iPhone to login
         I really like the presentation and ease of use
         Love the customization features and the interface itself
         I love the interaction with Penny
        
        
         Check Deposit is frustrating and disappointing
         The instant balance feature makes everything so much easier
         The check deposit function is bulletproof
         Use it for work and makes accountants job easy
         Screen resolution on ipad is way too big
         Addition of mobile deposits was a plus
         The new scrolling interface is awesome
        More features but more confusing display
        Transferring between personal and other accounts are easy
        The new version is really good works perfect
        New update is terrible
         The new UI is horrible
        Old app was way better
         Buttons are also not good anymore
        
         Expenses easy to characterize
         So easy to track mileage for work
        Easy to use UI
        So easy to pay/request with friends :)
         So easy to navigate through 
        Very easy to invest
        Easy to set up
          It's so easy to keep on top of my credit
        It's awesome and easy to navigate
         Really cool app, Very easy to navigate and user friendly
        
        
         Great for quick informative articles and stock quotes
        Good way to watch your credit daily and weekly
        
        
        
        
         I can view my ID cards even without phone service
         So glad that I can now deposit checks from anywhere
        
        
        
        I'm so excited no more one time password 
         I just use my fingerprint to sign in
         Clear design and overall a smooth journey for the customer
         It's slick and user friendly with many great new features
         I'd prefer to sort it by pay period
         Not user friendly and visually embarrassing
        
        */
    }
    
    /**
     * 创建预测集并标记所有文本特征
     * @param num  预测集数量
     */
    public void RemarkAll(int num){
        SQL s = new SQL();
        s.CreatePre(num);
        String n = Integer.toString(num);
        table_name = "test" + n;
        treemap = s.RecordTreeMap(table_name);//解析语法树 
        this.MarkALLFeature();
    }
    
    public void RemarkTrainAll(){
        SQL s = new SQL();
        s.CreateTrain();
        table_name = "train";
        treemap = s.RecordTreeMap(table_name);//解析语法树 
        this.MarkALLFeature();
    }
    
}
