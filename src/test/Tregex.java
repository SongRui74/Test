/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import edu.stanford.nlp.parser.lexparser.EnglishTreebankParserParams;  
import edu.stanford.nlp.parser.lexparser.TreebankLangParserParams;  
import edu.stanford.nlp.semgraph.SemanticGraph;  
import edu.stanford.nlp.semgraph.SemanticGraphFactory;  
import edu.stanford.nlp.semgraph.semgrex.SemgrexMatcher;  
import edu.stanford.nlp.semgraph.semgrex.SemgrexPattern;  
import edu.stanford.nlp.trees.GrammaticalStructure;  
import edu.stanford.nlp.trees.GrammaticalStructureFactory;  
import edu.stanford.nlp.trees.Tree;  
import edu.stanford.nlp.trees.tregex.TregexMatcher;  
import edu.stanford.nlp.trees.tregex.TregexPattern;  
import edu.stanford.nlp.util.logging.Redwood;
/**
 *
 * @author dell-pc
 */
public class Tregex {
    
    public int TregexInvalid(Tree t){
        int count = 0;//记录单词符合表达式的数目
        String s = "NP < NN | < NNP | < NNS | < NNPS | < DT ";  //定义匹配表达式
        
        //找到匹配的内容  
        TregexPattern p = TregexPattern.compile(s);  
        TregexMatcher m = p.matcher(t);  
        
        while (m.find()) { 
            count++;
            //    System.out.println("**************");
            //    m.getMatch().pennPrint(); 
        }
        return count;
    }
    /**
     * 是否匹配该表达式
     * @param t 句法树
     * @param str 表达式
     * @return 
     */
    public boolean TregexIsMatch(Tree t,String str){
        int flag = 0;//记录是否匹配的标志       
        //找到匹配的内容  
        TregexPattern p = TregexPattern.compile(str);  
        TregexMatcher m = p.matcher(t);  
        
        while (m.find()) { 
            flag = 1;
            break;
        }
        
        if(flag == 0)
            return false;
        else
            return true;
    }
    
    /**
     * 是否匹配关系的表达式
     * @param tree 句法树
     * @param str 匹配表达式
     */
    public boolean SemgrexIsMatch(Tree tree,String str){
        int flag = 0;//标记是否匹配成功
        Redwood.RedwoodChannels log = Redwood.channels(Tregex.class);
        SemanticGraph graph = SemanticGraphFactory.generateUncollapsedDependencies(tree);

        TreebankLangParserParams params = new EnglishTreebankParserParams();
        GrammaticalStructureFactory gsf = params.treebankLanguagePack().grammaticalStructureFactory(params.treebankLanguagePack().punctuationWordRejectFilter(), params.typedDependencyHeadFinder());

        GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);

        log.info(graph);

        SemgrexPattern semgrex = SemgrexPattern.compile(str);
        SemgrexMatcher matcher = semgrex.matcher(graph);
        
        while (matcher.find()) {
            flag = 1;
            break;
        }
        if(flag == 0)
            return false;
        else
            return true;
    }
    
    public void Tregextest(){
        Tree t = Tree.valueOf("(ROOT(NP(NP (DT An) (NNP NP))(PP (IN over)(NP(NP (DT an) (NNP NN))(SBAR(WHNP (WDT that))(S(VP (VBZ is)(PP (IN over)(NP (NN dog))))))))(. .)))");  
        String s = "NP < NN | < NNS";  
        Tree argTree =Tree.valueOf("(NN arg0)");  
        //输出匹配的内容  
        TregexPattern p = TregexPattern.compile(s);  
        TregexMatcher m = p.matcher(t);  
         
        while (m.find()) {  
            System.out.println("源树型结构！\n");  
            t.pennPrint();//打印原树形  
            System.out.println("匹配的部分：");  
            m.getMatch().pennPrint();//打印匹配后的树型  
            m.getMatch().removeChild(0);//剔除匹配后的树叶  
            System.out.println("剔除匹配后的子叶：");  
            m.getMatch().pennPrint();  
            System.out.println("增加匹配后的树:");  
            m.getMatch().addChild(argTree);//增加匹配后的树，完成替换  
              //m.getMatch().pennPrint();  
            t.pennPrint();  
              //System.out.println(" \n");  
           // m.getMatch().pennPrint();  
            String le=m.getMatch().toString();  
           // System.out.println(le);//替换后匹配的东西  
        //TsurgeonPattern surgery = Tsurgeon.parseOperation("ok");  
        //.processPattern(p, surgery, t).pennPrint();  
        //Tsurgeon.processPattern(m.getMatch(), p, t);  
        //TsurgeonPattern surgery = Tsurgeon.parseOperation("");  
        //m.getMatch().removeChild(0);  
        //m.getMatch().addChild(argTree);  
        //m.getNode(le);  
           //System.out.println("OK");  
        }  
    } 
    
    public void Tregextest2(Tree tree,String st){
        int count = 0;        
        //输出匹配的内容  
        TregexPattern p = TregexPattern.compile(st);  
        TregexMatcher m = p.matcher(tree);  
                                        
        while (m.find()) {              
            System.out.println("**************");
            m.getMatch().pennPrint(); 
            count++;
        }  
        System.out.println(count);
    }
    
    
    public void Semgrextest(Tree tree,String str){        
        Redwood.RedwoodChannels log = Redwood.channels(Tregex.class);
        SemanticGraph graph = SemanticGraphFactory.generateUncollapsedDependencies(tree);

        TreebankLangParserParams params = new EnglishTreebankParserParams();
        GrammaticalStructureFactory gsf = params.treebankLanguagePack().grammaticalStructureFactory(params.treebankLanguagePack().punctuationWordRejectFilter(), params.typedDependencyHeadFinder());

        GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);

        log.info(graph);

        SemgrexPattern semgrex = SemgrexPattern.compile(str);
        SemgrexMatcher matcher = semgrex.matcher(graph);
    // This will produce two results on the given tree: "likes" is an
    // ancestor of both "dog" and "my" via the nsubj relation
        while (matcher.find()) {
            System.out.println(matcher.getMatch().toString());
        }

    }
    
}
