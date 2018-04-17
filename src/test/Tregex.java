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
import java.util.List;
/**
 *
 * @author dell-pc
 */
public class Tregex {
    
    public int TregexInvalid(Tree t){
        int count = 0;//记录单词符合表达式的数目
        String s1 = "NP < @/NN.?/ | < DT | < MD | < CC | < FW";
        String s2 = "INTJ < @/NN.?/ | < UH | < FW";
        String s3 = "FRAG < CC";
        String s4 = "VP < CC | < SYM";  //定义匹配表达式
        
        //找到匹配的内容  
        TregexPattern p1 = TregexPattern.compile(s1);  
        TregexMatcher m1 = p1.matcher(t);          
        while (m1.find()) { 
            count++;
        }
        
        TregexPattern p2 = TregexPattern.compile(s2);  
        TregexMatcher m2 = p2.matcher(t);          
        while (m2.find()) { 
            count++;
        }
        
        TregexPattern p3 = TregexPattern.compile(s3);  
        TregexMatcher m3 = p3.matcher(t);         
        while (m3.find()) { 
            count++;
        }
        
        TregexPattern p4 = TregexPattern.compile(s4);  
        TregexMatcher m4 = p4.matcher(t);        
        while (m4.find()) { 
            count++;
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
    
    public String Tregexinfo(Tree t,String s){
        //输出匹配的内容  
        TregexPattern p = TregexPattern.compile(s);  
        TregexMatcher m = p.matcher(t);  
        String str = ""; 
        if(m.find()) {   
            System.out.println("匹配成功");
        //    m.getMatch().pennPrint();//打印匹配后的树型
            List l = m.getMatch().getLeaves();
            for(int i = 0; i < l.size(); i++){
                str += l.get(i).toString() + " ";
            }
        } 
        return str;
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
