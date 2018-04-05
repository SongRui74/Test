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
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class Standfordnlp {    
/*    private final String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //加载JDBC驱动
    private final String dbURL = "jdbc:sqlserver://localhost:1433; DatabaseName=mypro"; //连接服务器和数据库mypro
    private final String userName = "song"; 
    private final String userPwd = "123456"; 
    private Connection conn; 
    public List<Map<Tree, Integer>> node_hash_list = new ArrayList<>();    
    private final Map<Tree,Integer> map = new HashMap<>(); //存放需记录的树节点和对应的hash值  
*/    
    
    /**
     * 利用Standfordnlp分析用户评论,得到语法树
     * @param str 一条用户评论
     * @return  返回句法树
     */
    public Tree FeedbacktoTree(String str){  
        Tree tree = null;
        Properties props = new Properties();
    //    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        props.put("annotators", "tokenize, ssplit,pos, lemma, ner, parse");
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
        //    tree.pennPrint();
            break;
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
    //    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        props.put("annotators", "tokenize, ssplit,pos, lemma, ner, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(str);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        List list = new ArrayList(); //只有一句话 暂且定义一个list
        for(CoreMap sentence: sentences) {
            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            list = dependencies.edgeListSorted(); //依存关系list
        //    System.out.println(dependencies.toList());
            break;//只取第一条评论内容，防止存在省略号覆盖list
        }
        return list;
    }
    
    /**
     * 获取单词对应的节点
     * @param tree 评论对应的语法树
     * @param word 单词 
     * @return 树节点
     */
    public Tree GetNode(Tree tree,String word){
        List list = tree.getLeaves();
        Tree t = null;
        Tree temp;
        for(int i = 0 ; i < list.size() ; i++) {
            temp = (Tree) list.get(i);
            if(temp.label().value().equals(word)){
                t = temp;
            }
        }
        return t;
    }
      
    /**
     * 获取两单词节点的LCA（处理评论和单词，将其转化为树节点）
     * @param root 一条评论的语法树
     * @param dep 一个依存关系中dep的节点label
     * @param gov 一个依存关系中gov的节点label
     * @return 
     */
    public String LCA(Tree root,String dep, String gov){
        //找到单词对应的节点
        Tree d = this.GetNode(root, dep);
        Tree g = this.GetNode(root, gov);     
        //找到LCA
        String ancestor = null;
        List path1 = root.dominationPath(d);
        List path2 = root.dominationPath(g);
        if(path1 == null || path2 == null){
            return null;
        }
        else{
            for(int i = 0; i < path1.size();i++){
                Tree t1 = (Tree) path1.get(i);
                for(int j = 0; j < path2.size(); j++){
                    Tree t2 = (Tree) path2.get(j);
                    if(t1.toString().equals(t2.toString())){
                        ancestor = t1.label().value();
                        break;
                    }
                }
            }
        }
    //    System.out.println(ancestor+"\t"+ dep +"\t"+gov);
        return ancestor;
    }
    
    /**
     * 获取两单词节点到LCA的距离之和
     * @param root 一条评论的语法树
     * @param dep 一个依存关系中dep的节点label
     * @param gov 一个依存关系中gov的节点label
     * @return 
     */
    public int Path(Tree root,String dep, String gov){
        //找到单词对应的节点
        Tree d = this.GetNode(root, dep);
        Tree g = this.GetNode(root, gov);     
        
        List path1 = root.dominationPath(d);
        List path2 = root.dominationPath(g);
        int count = 0;//记录重复的祖先节点个数
        if(path1 == null || path2 == null){
            return 0;
        }
        else{
            for(int i = 0; i < path1.size(); i++){
                Tree t1 = (Tree) path1.get(i);
                for(int j = 0; j < path2.size(); j++){
                    Tree t2 = (Tree) path2.get(j);
                    if(t1.toString().equals(t2.toString())){
                        count++;
                        break;
                    }
                }
            }
        }
        //删除重复的祖先节点计算路径
        int path = path1.size()-1 + path2.size()-1 - 2*(count-1);//路径为节点数-1
        return path;
    }
    
    /**
     * 计算两条评论相似度
     * @param list1  评论一的依存关系列表
     * @param list2  评论二的依存关系列表
     * @param tree1  评论一的语法树
     * @param tree2  评论二的语法树
     * @return 两条评论的相似度
     */
    public List CalSimi(List list1, List list2,Tree tree1,Tree tree2){ 
                
        List<String[]> simi = new ArrayList();  //存放依存关系相似度
        
        for(int i = 0; i < list1.size(); i++){
            SemanticGraphEdge s1 = (SemanticGraphEdge) list1.get(i);
            String deppos1 = s1.getDependent().tag();  //评论一的dependent词性
            String deplem1 = s1.getDependent().lemma(); //评论一dependent原形
            String govpos1 = s1.getGovernor().tag();
            String govlem1 = s1.getGovernor().lemma();
            String relation1 = s1.getRelation().toString();  //评论一dependent与governor关系
            String dep1 = s1.getDependent().word(); //评论一dependent单词
            String gov1 = s1.getGovernor().word();
                        
            String[] rela_value = {relation1,"0"};  //记录关系及数值，初始为0
            double temp = -99; //记录相同关系中的最大值，即匹配度最高的关系
            
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
                            //分别获取LCA节点
                            String anc1 = this.LCA(tree1, dep1, gov1);
                            String anc2 = this.LCA(tree2, dep2, gov2);
                            if(anc1.equals(anc2)){ //LCA相同
                                //分别获取距离LCA的路径之和
                                int path1 = this.Path(tree1, dep1, gov1);
                                int path2 = this.Path(tree2, dep2, gov2);
                                int dist = Math.abs(path1-path2); //计算两路径距离差
                                if(dist == 0){ //距离相同
                                    rela_value[1] = "7";
                                }
                                else if(dist <= 5){ //距离相近，降低关系值
                                    rela_value[1] = "6";
                                }
                                else{ //距离较远，降低关系值
                                    rela_value[1] = "5";
                                }
                            }
                            else{ //LCA不同
                                rela_value[1] = "3";
                            }
                        }
                        else if(govpos1.equals(govpos2)){ //governor原形不同，词性相同
                            //分别获取LCA节点
                            String anc1 = this.LCA(tree1, dep1, gov1);
                            String anc2 = this.LCA(tree2, dep2, gov2);
                            if(anc1.equals(anc2)){ //LCA相同
                                //分别获取距离LCA的路径之和
                                int path1 = this.Path(tree1, dep1, gov1);
                                int path2 = this.Path(tree2, dep2, gov2);
                                int dist = Math.abs(path1-path2); //计算两路径距离差
                                if(dist == 0){
                                    rela_value[1] = "6";
                                }
                                else if(dist <= 5){
                                    rela_value[1] = "5";
                                }
                                else{
                                    rela_value[1] = "4";
                                }
                            }
                            else{
                                rela_value[1] = "2";
                            }
                        }
                        else{  //governor不同
                            rela_value[1] = "0.5";
                        }
                    } 
                    else if(govlem1.equals(govlem2)){ //governor原形相同
                        if(deppos1.equals(deppos2)){ //dependency原形不同，词性相同
                            //分别获取LCA节点
                            String anc1 = this.LCA(tree1, dep1, gov1);
                            String anc2 = this.LCA(tree2, dep2, gov2);
                            if(anc1.equals(anc2)){
                                //分别获取距离LCA的路径之和
                                int path1 = this.Path(tree1, dep1, gov1);
                                int path2 = this.Path(tree2, dep2, gov2);
                                int dist = Math.abs(path1-path2); //计算两路径距离差
                                if(dist == 0){
                                    rela_value[1] = "6";
                                }
                                else if(dist <= 5){
                                    rela_value[1] = "5";
                                }
                                else{
                                    rela_value[1] = "4";
                                }
                            }
                            else{
                                rela_value[1] = "2";
                            }
                        }
                        else{ //dependency不同
                            rela_value[1] = "0.5";
                        }
                    }
                    //仅两词词性相同
                    else if(deppos1.equals(deppos2)&&govpos1.equals(govpos2)){  
                        //分别获取LCA节点
                        String anc1 = this.LCA(tree1, dep1, gov1);
                        String anc2 = this.LCA(tree2, dep2, gov2);
                        if(anc1.equals(anc2)){
                            //分别获取距离LCA的路径之和
                            int path1 = this.Path(tree1, dep1, gov1);
                            int path2 = this.Path(tree2, dep2, gov2);
                            int dist = Math.abs(path1-path2); //计算两路径距离差
                            if(dist == 0){
                                rela_value[1] = "5";
                            }
                            else if(dist <= 5){
                                rela_value[1] = "4";
                            }
                            else{
                                rela_value[1] = "3";
                            }
                        }  
                        else{ //LCA不同
                            rela_value[1] = "1";
                        }
                    }
                }
                //保存匹配度最高的关系数值
            //    int val = Integer.parseInt(rela_value[1]);
                double val = Double.parseDouble(rela_value[1]);
                if(val > temp){
                    temp = val;
                }              
                rela_value[1] = Double.toString(val);
            }
            simi.add(rela_value); //添加到相似度列表中
        }       
    /*    
        for(String[] a :simi){
            System.out.println(a[0] + "\t" + a[1]);
        }
    */    
        return simi;
    }
     
    /**
     * 计算依存关系数值向量（重复的关系值均值？）
     * @param simi 依存关系和对应数值列表
     * @return 仅含数值的列表
     */
    public List SimiVector(List simi){
        List<Double> vector = new ArrayList();
        Map<String,Double> simi_value = new HashMap(); //存储数值        
        for(int i = 0; i < simi.size(); i++){
            String[] rela_value = (String[]) simi.get(i);
            String key = rela_value[0];
            double value = Double.parseDouble(rela_value[1]);
            if(!simi_value.containsKey(key)){
                simi_value.put(key, value);
            }
            else{
                double temp = simi_value.get(key);
                double new_val = 0.5*(value + temp);
                simi_value.put(key, new_val);
            }
        }
        
        for(String k : simi_value.keySet()){ 
            double val = simi_value.get(k);
            vector.add(val);
        }        
    /*    for(int a :vector){
            System.out.println(a);
        }
        */
        return vector;
    }
    
    /**
     * (舍)获取评论解析后语法树的每个节点及其hash值
     * @return 
     */
/*    public List<Map<Tree, Integer>> getNodeHashList(){
        for (Map<Tree, Integer> m : node_hash_list){ 
            for (Tree k : m.keySet()){ 
                System.out.println(k + " ： " + m.get(k)); 
            }  
        }
        return node_hash_list;
    }*/
    /**
     * (舍)将评论ast标记到数据库表中
     * @param table_name 表名
     * @param col 所添加的列名
     */
/*    public void RemarkFeedbackTree(String table_name,String col){
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
                //解析每条文本的AST
                Tree tree = FeedbacktoTree(rs.getString("Review_Content"));               
                //将AST转化为数值形式
                String ast = TreetoString(tree);               
                //写入数据库中
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
*/    
    
   /**
     * (舍)判断某节点的兄弟节点hash值是否均已知
     * @param node 所判断的节点
     * @param root 所判断节点所在树的根节点
     * @return  均已知则返回ture，否则为false
     */
/*    public boolean isInMap(Tree node,Tree root){
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
    */
    /**
     * (舍)计算该节点的父节点hash值
     * @param node 待计算节点的子节点
     * @param root 待计算节点所在树的根节点
     * @return  待计算节点hash值
     */
/*    public int CalHash(Tree node,Tree root){
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
    }    */
    /**
     * (舍)将语法树解析为hash值数组并转化为字符串
     * @param tree  评论的句法树
     * @return  经解析后转化的字符串
     */
/*    public String TreetoString(Tree tree){
        //树的各节点hash值计算        
        List leaves = tree.getLeaves(); //获取树的叶子节点
        //对词性节点进行hash值标注
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
        //计算其他未标注的节点并记录
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
        //将结果记录存放于list中
        node_hash_list.add(map);
        //将hash值依次取出放入数组中
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
       // 数组转化为字符串便于存入数据库中
        String ast_str = new String();
        StringBuffer sb = new StringBuffer();
        for(int j = 0;j<ast.length;j++){
            sb.append(ast[j]);
            sb.append(",");
        }
        ast_str = sb.toString();
        return ast_str;
    }
*/
    /**
     * (舍)获取树中除叶子节点外的其他所有有父亲的节点
     * @param tree
     * @return 树中除叶子节点和Root外的其他节点list
     */
/*    public List GetAllNode(Tree tree){
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
    }*/
    /**
     * (舍)计算语法树相似度
     * @param a 语法树所代表的字符串a
     * @param b 语法树所代表的字符串b
     * @return 两棵语法树的相似度
     */
/*    public Float Similarity(String a, String b){
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
    }*/
}
