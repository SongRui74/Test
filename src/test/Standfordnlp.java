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
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class Standfordnlp {
    private String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //加载JDBC驱动
    private String dbURL = "jdbc:sqlserver://localhost:1433; DatabaseName=mypro"; //连接服务器和数据库mypro
    private String userName = "song"; 
    private String userPwd = "123456"; 
    private Connection conn;
    private Statement stmt;
    
    //将评论ast标记到数据库表中
    public void RemarkFeedbackTree() throws ClassNotFoundException, SQLException{
        Class.forName(driverName);
        conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
        stmt = conn.createStatement();
        Statement stmt2 = conn.createStatement();
      //  System.out.println("Connection Successful!");
        
        ResultSet rs=stmt.executeQuery("SELECT Review_Content,old_ast FROM test"); 
        rs.next();//读取数据库第一行记录
        
        int i = 0;//测试写入数据数值
        String sql;
        sql="UPDATE test SET old_ast = " + i + " where Review_Content = '"+rs.getString("Review_Content")+"'";        
        stmt2.executeUpdate(sql);   //执行sql语句标记ast     
   //     System.out.println(rs.getString("Review_Content")+'\t'+rs.getString("old_ast"));
        
        //循环标记ast
        while(rs.next()){ 
            ++i;
            sql = UpdateSql(i,rs);
            stmt2.executeUpdate(sql); 
        //    System.out.println(sql);
        //    System.out.println(rs.getString("Review_Content")+'\t'+rs.getString("old_ast")+'\t'+i);
        } 
        rs.close(); 
        stmt.close(); 
        conn.close(); 
    }
    //更新数据库语句
    public String UpdateSql(int i,ResultSet rs) throws SQLException{
        String sql;
        if (rs.getString("Review_Content").contains("'"))
            sql = "UPDATE test SET old_ast = " + i + " where Review_Content = '"+rs.getString("Review_Content").replace("'", "''")+"'";
        else
            sql = "UPDATE test SET old_ast = " + i + " where Review_Content = '"+rs.getString("Review_Content")+"'";
        return sql;
    }
    
    //利用Standfordnlp分析用户评论,得到语法树
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
}