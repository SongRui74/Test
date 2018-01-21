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
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Standfordnlp {
    private String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //加载JDBC驱动
    private String dbURL = "jdbc:sqlserver://localhost:1433; DatabaseName=mypro"; //连接服务器和数据库mypro
    private String userName = "song"; 
    private String userPwd = "123456"; 
    private Connection conn;    
    
    /**
     * 将评论ast标记到数据库表中
     */
    public void RemarkFeedbackTree(String table_name,String col){
        try {             
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();
            //  System.out.println("Connection Successful!");           
            
            rs=stmt.executeQuery("SELECT Review_Content FROM "+table_name);  
            rs.first();//读取数据库第一行记录  
            
            Tree tree = FeedbacktoTree(rs.getString("Review_Content"));//将评论解析为语法树
            String ast = TreetoString(tree);//将语法树解析为字符串
        //    int i = TreetoInt(tree);
        //    int i = 0;//测试写入数据数值        
            String sql;
            SQL s = new SQL();
            String str = s.SqlSingleQuote(rs.getString("Review_Content")); //处理单引号
            sql="UPDATE "+ table_name +" SET " + col + " = '" + ast + "' where Review_Content = '"+ str +"'";
            stmt2.executeUpdate(sql);   //执行sql语句标记ast
        //    System.out.println(rs.getString("Review_Content")+'\t'+rs.getString("old_ast"));
                
            //循环标记ast
            while(rs.next()){                                            
                /*解析每条文本的AST*/
                tree = FeedbacktoTree(rs.getString("Review_Content"));               
                /*将AST转化为数值形式*/
                ast = TreetoString(tree);               
                /*写入数据库中*/
                sql = s.UpdateSql(rs,table_name,col,ast);
                stmt2.executeUpdate(sql);
            //    System.out.println(sql);
            //    System.out.println(rs.getString("Review_Content")+'\t'+rs.getString("old_ast")+'\t'+i);
            }
            rs.close();
            stmt.close(); 
            stmt2.close();
            conn.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * 利用Standfordnlp分析用户评论,得到语法树
     */
    public Tree FeedbacktoTree(String str){  
        Tree tree = null;
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(str);
        // run all Annotators on this text
        pipeline.annotate(document);
        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
             // traversing the words in the current sentence
             // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class);
                String pos = token.get(PartOfSpeechAnnotation.class);
                String ne = token.get(NamedEntityTagAnnotation.class);
                String lemma = token.get(LemmaAnnotation.class);
             //   System.out.println(word+"\t"+pos+"\t"+lemma+"\t"+ne);
            }
            // this is the parse tree of the current sentence
            tree = sentence.get(TreeAnnotation.class);
        //    tree.pennPrint();
            // this is the Stanford dependency graph of the current sentence
            SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
        //    dependencies.toString();
        }
        // This is the coreference link graph
        // Each chain stores a set of mentions that link to each other,
        // along with a method for getting the most representative mention
        // Both sentence and token offsets start at 1!
        Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);        
        return tree;
    }
    
    /**
     * 将语法树解析为hash值数组并转化为字符串
     * @param tree 
     */
    public String TreetoString(Tree tree){
        Stack s = new Stack();
        Tree t_key = tree; //存放当前节点
        int t_value;  //存放当前节点hash值
        s.add(t_key);
        Map<Tree,Integer> hash = new HashMap<>();
        /*深度优先搜索AST并计算节点hash值存入map中*/
        while(!s.isEmpty()){
            t_key = (Tree) s.pop();
            t_value = t_key.hashCode();
            hash.put(t_key, t_value);
            List children  = t_key.getChildrenAsList();
            if(children != null && !children.isEmpty()){
                for (Object child : children) {
                    s.push(child);
                }
            }
        }
        /*将hash值依次取出放入数组中*/
        String[] ast = new String[hash.size()];
        int i = 0;
        Set entries = hash.entrySet( );
        if(entries != null){
            Iterator iterator = entries.iterator( );
            while(iterator.hasNext( )) {
                Map.Entry entry =(Map.Entry) iterator.next( );
                int value = (int) entry.getValue();
                String val = Integer.toString(value);
                ast[i] = val;
                i++;
            }
        }
        /*数组转化为字符串便于存入数据库中*/
        String ast_str = new String();
        StringBuffer sb = new StringBuffer();
        for(int j = 0;j<ast.length;j++){
            sb.append(ast[j]);
            sb.append(",");
        }
        ast_str = sb.toString();
        return ast_str;
    }
    
    /**
     * 计算语法树相似度
     */
    public Float Similarity(String a, String b){
        float sim = 0;
        String[] aa = a.split("\\,");
        String[] bb = b.split("\\,");
        int count = 0;
        for(int i = 0; i < aa.length; i++){
            String m = aa[i];
            for(int j = 0; j < bb.length; j++){
                String n = bb[j];
                if(m.equals(n)){
                    count++;
                }
            }
        }
        int same = count;
        int min = Math.min(aa.length, bb.length);
        sim = (float)same/min;
        return sim;
    }
    /*测试standfordnlp函数
    public void Nlp() {
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        
        // read some text in the text variable
        String text = "Add your text here:Beijing sings Lenovo.i'm very fine.thx!!!!!!!!!!";
        
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
        
        // run all Annotators on this text
        pipeline.annotate(document);
        
        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        
        System.out.println("word\tpos\tlemma\tner");
        for(CoreMap sentence: sentences) {
             // traversing the words in the current sentence
             // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                String ne = token.get(NamedEntityTagAnnotation.class);
                String lemma = token.get(LemmaAnnotation.class);
                
                System.out.println(word+"\t"+pos+"\t"+lemma+"\t"+ne);
            }
            // this is the parse tree of the current sentence
            Tree tree = sentence.get(TreeAnnotation.class);
            tree.pennPrint();
            // this is the Stanford dependency graph of the current sentence
            SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
            dependencies.toString();
        }
        // This is the coreference link graph
        // Each chain stores a set of mentions that link to each other,
        // along with a method for getting the most representative mention
        // Both sentence and token offsets start at 1!
        Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
    }
    */
}