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
import edu.stanford.nlp.parser.lexparser.TreeBinarizer;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
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
import java.util.logging.Level;
import java.util.logging.Logger;


public class Standfordnlp {
    private final String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //加载JDBC驱动
    private final String dbURL = "jdbc:sqlserver://localhost:1433; DatabaseName=mypro"; //连接服务器和数据库mypro
    private final String userName = "song"; 
    private final String userPwd = "123456"; 
    private Connection conn; 
    public List<Map<Tree, Integer>> node_hash_list = new ArrayList<>();    
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
     * @param table_name 表名
     * @param col 所添加的列名
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
     * @param str 一条用户评论
     * @return  返回句法树
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
        }       
        return tree;
    }
    
    /**
     * 利用Standfordnlp分析用户评论,得到依存关系
     * @param str 一条用户评论
     * @return  返回依存关系list
     */
    public List FeedbacktoDep(String str){  
        Properties props = new Properties();
        //分词（tokenize）、分句（ssplit）、词性标注（pos）、词形还原（lemma）、命名实体识别（ner）、语法解析（parse）、情感分析（sentiment）、指代消解（coreference resolution）
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(str);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        List list = new ArrayList(); //只有一句话 暂且定义一个list
        for(CoreMap sentence: sentences) {
            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            list = dependencies.edgeListSorted(); //依存关系list
            System.out.println(dependencies.toList());
        }
        return list;
    }
    
    public boolean SameAncestor(String str, String dep, String gov){
        Tree tree = this.FeedbacktoTree(str);
        return true;
    }
    
    /**
     * 计算两评论相似度
     * @param str1 评论一
     * @param str2 评论二
     * @return 两条评论的相似度
     */
    public List CalSimi(String str1, String str2){        
        List list1 = this.FeedbacktoDep(str1);
        List list2 = this.FeedbacktoDep(str2);
        
        List<String[]> simi = new ArrayList();
        
        for(int i = 0; i < list1.size(); i++){
            SemanticGraphEdge s1 = (SemanticGraphEdge) list1.get(i);
            String deppos1 = s1.getDependent().tag();
            String deplem1 = s1.getDependent().lemma();
            String govpos1 = s1.getGovernor().tag();
            String govlem1 = s1.getGovernor().lemma();
            String relation1 = s1.getRelation().toString();
            String dep1 = s1.getDependent().word();
            String gov1 = s1.getGovernor().word();
            for(int j = 0; j < list2.size(); j++){
                SemanticGraphEdge s2 = (SemanticGraphEdge) list2.get(j);
                String deppos2 = s2.getDependent().tag();
                String deplem2 = s2.getDependent().lemma();
                String govpos2 = s2.getGovernor().tag();
                String govlem2 = s2.getGovernor().lemma();
                String relation2 = s2.getRelation().toString();
                String dep2 = s2.getDependent().word();
                String gov2 = s2.getGovernor().word();
                if(relation1.equals(relation2)){ //关系相同时
                    if(deplem1.equals(deplem2)){ //dependency原形相同
                        if(govlem1.equals(govlem2)){ //governor原形相同
                            String[] rela_value = {relation1,"5"};
                            simi.add(rela_value);
                        }
                        else if(govpos1.equals(govpos2)){ //governor原形不同，词性相同
                            String[] rela_value = {relation1,"4"};
                            simi.add(rela_value);
                        }
                        else{  //governor不同
                            String[] rela_value = {relation1,"3"};
                            simi.add(rela_value);
                        }
                    } 
                    else if(govlem1.equals(govlem2)){ //governor原形相同
                        if(deppos1.equals(deppos2)){ //dependency原形不同，词性相同
                            String[] rela_value = {relation1,"4"};
                            simi.add(rela_value);
                        }
                        else{ //dependency不同
                            String[] rela_value = {relation1,"3"};
                            simi.add(rela_value);
                        }
                    }
                }
            }
        }
        return simi;
    }
 
    public void lout(List<String[]> a){
        for(int i = 0; i < a.size(); i++) {
            String[] s = a.get(i);
            System.out.println(s[0] + "\t" + s[1]);
        }
    }
    
   /**
     * 判断某节点的兄弟节点hash值是否均已知
     * @param node 所判断的节点
     * @param root 所判断节点所在树的根节点
     * @return  均已知则返回ture，否则为false
     */
    public boolean isInMap(Tree node,Tree root){
        List s = node.siblings(root);
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
     * 计算该节点的父节点hash值
     * @param node 待计算节点的子节点
     * @param root 待计算节点所在树的根节点
     * @return  待计算节点hash值
     */
    public int CalHash(Tree node,Tree root){
        int value = 1;
        List s = node.siblings(root);
        if(s != null){
            for(int i = 0; i < s.size(); i++){
                Tree temp = (Tree) s.get(i);
                value *= map.get(temp);
            }
        }
        value *= map.get(node);
        return value;
    }    
    /**
     * 将语法树解析为hash值数组并转化为字符串
     * @param tree  评论的句法树
     * @return  经解析后转化的字符串
     */
    public String TreetoString(Tree tree){
        /*树的各节点hash值计算*/        
        List leaves = tree.getLeaves(); //获取树的叶子节点
        /*对词性节点进行hash值标注*/
        for (Iterator it = leaves.iterator(); it.hasNext();) {
            Tree left = (Tree) it.next();
            switch (left.parent(tree).label().value()) {
                case "JJ":
                case "JJR":
                case "JJS":
                    map.put(left.parent(tree), 3);
                    break;
                case "MD":
                    map.put(left.parent(tree), 5);
                    break;
                case "NN":
                case "NNS":
                case "NNP":
                case "NNPS":
                    map.put(left.parent(tree), 7);
                    break;
                case "POS":
                    map.put(left.parent(tree), 11);
                    break;
                case "RB":
                case "RBR":
                case "RBS":
                    map.put(left.parent(tree), 13);
                    break;
                case "VB":
                case "VBD":
                case "VBG":
                case "VBN":
                case "VBP":
                case "VBZ":
                    map.put(left.parent(tree), 17);
                    break;
                default:
                    map.put(left.parent(tree), 1);
                    break;
            }
        }
        leaves.clear(); //释放叶子节点list 
        
        List list = this.GetAllNode(tree); //存放待计算hash值节点list
        /*计算其他未标注的节点并记录*/
        while(!list.isEmpty()){
            for (Iterator it = list.iterator(); it.hasNext();) {
                Tree node = (Tree) it.next();
                if(map.containsKey(node)){
                    if(this.isInMap(node,node.parent(tree))){//若left所有兄弟节点都存在于map中，则计算他们的父节点的hash值并记录
                        int value = this.CalHash(node,node.parent(tree));
                        map.put(node.parent(tree), value);
                        it.remove();  //计算完成后从list中删除node节点
                    }
                }
            }        
        } 
        /*将结果记录存放于list中*/
        node_hash_list.add(map);
        /*将hash值依次取出放入数组中*/
        String[] ast = new String[map.size()]; //节点对应的hash值数组
        int i = 0;
        Set entries = map.entrySet();
        if(entries != null){
            Iterator iterator = entries.iterator();
            while(iterator.hasNext()) {
                Map.Entry entry =(Map.Entry) iterator.next();
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
     * 获取树中除叶子节点外的其他所有有父亲的节点
     * @param tree
     * @return 树中除叶子节点和Root外的其他节点list
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
     * @param a 语法树所代表的字符串a
     * @param b 语法树所代表的字符串b
     * @return 两棵语法树的相似度
     */
    public Float Similarity(String a, String b){
        float sim = 0;
        String[] aa = a.split("\\,");
        String[] bb = b.split("\\,");
        int count = 0;
        for(int i = 0; i < aa.length; i++){
            String m = aa[i];
            if(m.equals("1")){
                continue;
            }
            for(int j = 0; j < bb.length; j++){
                String n = bb[j];
                if(m.equals(n)){
                    count++;
                }
            }
        }
        int same = count;
        int sum = aa.length + bb.length;
        sim = (float)same/sum;
        System.out.println("相似度为："+same +"/" +sum + "=" +sim);      
        return sim;
    }
}
