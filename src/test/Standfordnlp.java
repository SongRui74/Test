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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Standfordnlp {
    private String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //加载JDBC驱动
    private String dbURL = "jdbc:sqlserver://localhost:1433; DatabaseName=mypro"; //连接服务器和数据库mypro
    private String userName = "song"; 
    private String userPwd = "123456"; 
    private Connection conn;    
    private String table_name = "cpy_test";
    
    /**
     * 将评论ast标记到数据库表中
     */
    public void RemarkFeedbackTree(){
        try { 
            SqltoShort();//将所有长文本化为单句
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();
            //  System.out.println("Connection Successful!");           
            
            rs=stmt.executeQuery("SELECT Review_Content,old_ast FROM "+table_name);  
            rs.first();//读取数据库第一行记录  
            
        //    Tree tree = StringtoTree(rs.getString("Review_Content"));
        //    int i = TreetoInt(tree);
            int i = 0;//测试写入数据数值
            String sql;
            String str = SqlSingleQuote(rs.getString("Review_Content")); //处理单引号
            sql="UPDATE "+ table_name +" SET old_ast = " + i + " where Review_Content = '"+ str +"'";
            stmt2.executeUpdate(sql);   //执行sql语句标记ast
        //    System.out.println(rs.getString("Review_Content")+'\t'+rs.getString("old_ast"));
                
            //循环标记ast
            while(rs.next()){                
                             
                /*解析每条文本的AST*/
                
                
                /*将AST转化为数值形式*/
                ++i;
                
                /*写入数据库中*/
                sql = UpdateSql(i,rs);
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
     * 批量将评论拆分为单句
     */
    public void SqltoShort(){
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            //  System.out.println("Connection Successful!");
            rs=stmt.executeQuery("SELECT Review_Content FROM "+table_name);  
            while(rs.next()){          
                if (rs.getString("Review_Content").contains(".")){
                    String[] numdot = rs.getString("Review_Content").split("\\.");
                    if(numdot.length>1){
                        LongTexttoShort(rs);
                    }
                    else
                        ;
                }
                else
                    ;
            }
            rs.close();
            stmt.close();
            conn.close();
        }catch (SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 长文本化为单句
     */
    public void LongTexttoShort(ResultSet rs){
        try {
            String old_content = SqlSingleQuote(rs.getString("Review_Content"));//分句之前的长文本处理单引号
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            Statement st2 = conn.createStatement();
            String sql = null;
            /*存储该元组的其他信息*/
            sql = "SELECT * FROM "+ table_name +" WHERE Review_Content = '" + old_content + "'"; 
            ResultSet rstemp = st.executeQuery(sql);
            rstemp.first();
            String[] content_info = new String[5];            
            content_info[0] = rstemp.getString("APP_ID");
            content_info[1] = rstemp.getString("Reviewer_Name");
            content_info[2] = rstemp.getString("Rating");
            content_info[3] = rstemp.getString("Review_Title");
        //    content_info[4] = rstemp.getString("Review_Content");
            content_info[4] = rstemp.getString("classes");
            content_info[5] = "1";            
            //处理单引号
            for (int i = 0;i<content_info.length;i++) {
                content_info[i] = SqlSingleQuote(content_info[i]);
            }            
            /*以句号分割评论，并将分割后的评论依次插入数据库中*/
            String[] new_contents = old_content.split("\\.");
            
            sql = "UPDATE "+ table_name +" SET Review_Content = '"+ new_contents[0] +"' WHERE Review_Content = '" + old_content + "'";
            st2.executeUpdate(sql);
            for(int i=1;i<new_contents.length;i++){
                sql = "INSERT INTO "+ table_name +" VALUES ('"+ content_info[0] +"', '" 
                        + content_info[1] + "','"+ content_info[2] +"', '"
                        + content_info[3] + "','"+ new_contents[i] +"', '"
                        + content_info[4] + "','"+ content_info[5] +"')";
                st2.executeUpdate(sql);
            }
            st.close();
            st2.close();
            rstemp.close();
        } catch (SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
    }    
    /**
     * 更改数据库查询语句
     */
    public String UpdateSql(int i,ResultSet rs){
        String sql = null;
        try {
            String str = SqlSingleQuote(rs.getString("Review_Content"));
            sql = "UPDATE "+ table_name +" SET old_ast = " + i + " where Review_Content = '"+ str +"'";
        } catch (SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sql;
    }
    /**
     * 处理评论中的单引号，便于sql语句顺利执行
     */
    public String SqlSingleQuote(String str){
        if(str.contains("'")){
            return str.replace("'", "''");
        }
        else
            return str;
    }
    
    /**
     * 利用Standfordnlp分析用户评论,得到语法树
     */
    public Tree StringtoTree(String str){  
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
            tree.pennPrint();
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
    
    public int TreetoInt(Tree tree){
        if(tree.isEmpty())
            return 0;
        return 1;
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