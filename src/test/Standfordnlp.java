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
import java.util.ArrayList;
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
    private final String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //加载JDBC驱动
    private final String dbURL = "jdbc:sqlserver://localhost:1433; DatabaseName=mypro"; //连接服务器和数据库mypro
    private final String userName = "song"; 
    private final String userPwd = "123456"; 
    private Connection conn; 
    public List<Map<Tree, Integer>> node_hash_list = new ArrayList<Map<Tree, Integer>>();    
    
    /**
     * 获取评论解析后语法树的每个节点及其hash值
     * @return 
     */
    public List<Map<Tree, Integer>> getNodeHashList(){
        for (Map<Tree, Integer> m : node_hash_list){ 
            for (Tree k : m.keySet()){ 
                System.out.println(k + " ： " + m.get(k)); 
            }  
        }
        return node_hash_list;
    }
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
            
            rs=stmt.executeQuery("SELECT * FROM "+table_name);      
            String sql;
            SQL s = new SQL();
            //循环标记ast
            while(rs.next()){                                            
                /*解析每条文本的AST*/
                Tree tree = FeedbacktoTree(rs.getString("Review_Content"));               
                /*将AST转化为数值形式*/
                String ast = TreetoString(tree);               
                /*写入数据库中*/
                sql = s.UpdateSql(rs,table_name,col,ast);
                stmt2.executeUpdate(sql);
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
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class);
                String pos = token.get(PartOfSpeechAnnotation.class);
                String ne = token.get(NamedEntityTagAnnotation.class);
                String lemma = token.get(LemmaAnnotation.class);
             //   System.out.println(word+"\t"+pos+"\t"+lemma+"\t"+ne);
            }
            tree = sentence.get(TreeAnnotation.class);
          //  tree.pennPrint();
            SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
            //dependencies.toString();
        }
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
        /*将结果记录存放于list中*/
        node_hash_list.add(hash);
        /*将hash值依次取出放入数组中*/
        String[] ast = new String[hash.size()]; //节点对应的hash值数组
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
        System.out.println("相似度为："+same +"/" +min + "=" +sim);      
        return sim;
    }
}