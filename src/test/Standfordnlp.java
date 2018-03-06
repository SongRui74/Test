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
    private Map<Tree,Integer> map = new HashMap<>(); //存放需记录的树节点和对应的hash值  
    
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
        /*    for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class);
                String pos = token.get(PartOfSpeechAnnotation.class);
                String ne = token.get(NamedEntityTagAnnotation.class);
                String lemma = token.get(LemmaAnnotation.class);
             //   System.out.println(word+"\t"+pos+"\t"+lemma+"\t"+ne);
            }
        */    tree = sentence.get(TreeAnnotation.class);
            tree.pennPrint();
        //    SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
            //dependencies.toString();
        }
        Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);        
        return tree;
    }
    
    /**
     * 判断某节点的兄弟节点hash值是否均已知
     * @param tree
     * @return 
     */
    public boolean isInMap(Tree tree,Tree root){
        List s = tree.siblings(root);
        if(s != null ){
            for(int i = 0; i < s.size(); i++){
                Tree temp = (Tree) s.get(i);
                if(!map.containsKey(temp)){
                    return false;
                }
            }
        }
        return true;
    }    
    /**
     * 计算父节点hash值
     * @param tree
     * @return 
     */
    public int CalHash(Tree tree,Tree root){
        int value = 1;
        List s = tree.siblings(root);
        if(s != null){
            for(int i = 0; i < s.size(); i++){
                Tree temp = (Tree) s.get(i);
                value *= map.get(temp);
            }
        }
        value *= map.get(tree);
        return value;
    }    
    /**
     * 将语法树解析为hash值数组并转化为字符串
     * @param tree 
     */
    public String TreetoString(Tree tree){
        /*树的各节点hash值计算*/        
        List leaves = tree.getLeaves(); //获取树的叶子节点
        /*对词性节点进行hash值标注*/
        for (Iterator it = leaves.iterator(); it.hasNext();) {
            Tree left = (Tree) it.next();
            if(left.parent(tree).label().value().equals("JJ")
                    ||left.parent(tree).label().value().equals("JJR")
                    ||left.parent(tree).label().value().equals("JJS")){
                map.put(left.parent(tree), 3);
            }
            else if(left.parent(tree).label().value().equals("MD")){
                map.put(left.parent(tree), 5);
            }
            else if(left.parent(tree).label().value().equals("NN")
                    ||left.parent(tree).label().value().equals("NNS")
                    ||left.parent(tree).label().value().equals("NNP")
                    ||left.parent(tree).label().value().equals("NNPS")){
                map.put(left.parent(tree), 7);
            }
            else if(left.parent(tree).label().value().equals("POS")){
                map.put(left.parent(tree), 11);
            }
            else if(left.parent(tree).label().value().equals("RB")
                    ||left.parent(tree).label().value().equals("RBR")
                    ||left.parent(tree).label().value().equals("RBS")){
                map.put(left.parent(tree), 13);
            }
            else if(left.parent(tree).label().value().equals("VB")
                    ||left.parent(tree).label().value().equals("VBD")
                    ||left.parent(tree).label().value().equals("VBG")
                    ||left.parent(tree).label().value().equals("VBN")
                    ||left.parent(tree).label().value().equals("VBP")
                    ||left.parent(tree).label().value().equals("VBZ")){
                map.put(left.parent(tree), 17);
            }
            else{
                map.put(left.parent(tree), 1);
            }            
        }
        int num = tree.size()-1-leaves.size();//需要计算的节点数目（减去ROOT和所有叶子节点）
        leaves.clear(); //释放叶子节点list 
        
        List list = this.GetAllNode(tree); //存放待计算hash值节点list
        /*计算其他未标注的节点并记录*/
        while(map.size() != num){
            for (int i = 0; i < list.size(); i++) {
                Tree node = (Tree) list.get(i);
                if(map.containsKey(node)){
                    if(this.isInMap(node,node.parent(tree))){//若left所有兄弟节点都存在于map中，则计算他们的父节点的hash值并记录
                        int value = this.CalHash(node,node.parent(tree));
                        map.put(node.parent(tree), value);
                    }
                }
            }        
        } 
        /*将结果记录存放于list中*/
        node_hash_list.add(map);
        /*将hash值依次取出放入数组中*/
        String[] ast = new String[map.size()]; //节点对应的hash值数组
        int i = 0;
        Set entries = map.entrySet( );
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
        map = new HashMap();//清空map中的数值
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
     * 获取树中除叶子节点外的其他所有节点
     * @param tree
     * @return 树中除叶子节点外的其他节点list
     */
    public List GetAllNode(Tree tree){
        List list = tree.subTreeList();
        Tree temp;
        for(int i = 0 ; i < list.size() ; i++) {
            temp = (Tree) list.get(i);
            if(temp.isLeaf()){
                list.remove(i);
            }
        }
        list.remove(tree);
        return list;
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
    //    int min = Math.min(aa.length, bb.length);
    //   int max = Math.max(aa.length, bb.length);
        int sum = aa.length + bb.length;
        sim = (float)same/sum;
        System.out.println("相似度为："+same +"/" +sum + "=" +sim);      
        return sim;
    }
}
