/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import edu.stanford.nlp.trees.Tree;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dell-pc
 */
class point{
//    public String t = null; //语法树
    public String r = null; //用户评论
    public String c = null; //类别
    public int flag = -1;  //标记
    public int index = 0;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
/*    public String getT(){
        return t;
    }
    public void setT(String t){
        this.t = t;
    }
*/    
    public String getC(){
        return c;
    }
    public void setC(String c){
        this.c = c;
    }
    
    public String getR(){
        return r;
    }
    public void setR(String r){
        this.r = r;
    }
}

public class KMeansCluster {
    private final String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //加载JDBC驱动
    private final String dbURL = "jdbc:sqlserver://localhost:1433; DatabaseName=mypro"; //连接服务器和数据库mypro
    private final String userName = "sa"; 
    private final String userPwd = "123456"; 
    private Connection conn;  
    
    private String table_name;

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }
    
//    private final Map<String,List> listmap = new HashMap<>(); //存储评论对应的依存关系
//    private final Map<String,Tree> treemap = new HashMap<>(); //存储评论对应的语法树
    
    SQL s = new SQL();
    Standfordnlp nlp = new Standfordnlp();
    
    point[] data;//数据集
    point[] old_center = null;//原始聚类中心
    point[] new_center = null;//新的聚类中心
    double stopsim = 0.5; //迭代停止时的新旧质心相似程度
 
    point[][] pop;//种群
    int[] count;//种群规模
//    int IterNum = 10;//遗传迭代次数
//    double crossrate = 0.60;//交叉率
 //   double mutarate = 0.01;//突变率    
    double[] bestfitness;//最优解，即最大距离和
    point[] best; //最优染色体
   
    /**
     * 从某表中导入数据
     */
    public void ImportData(){
        try {
            int num = s.GetDataNum(this.getTable_name());
            data = new point[num];
            
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            
            rs=stmt.executeQuery("SELECT * FROM "+table_name);
            rs.first();//读取数据库第一行记录
//            int j = 0;
//            int fe = 0;
//            int im = 0;
//            int ov = 0;
//            int bu = 0;
//            int in = 0;
//            this.old_center = new point[4]; //存放聚类中心
            for(int i = 0;i < num ;i++){   
                data[i] = new point();// 对象创建
            //    String ast = rs.getString("ast");
                String classes = rs.getString("classes");
                String content = rs.getString("Review_Content");
            //    data[i].setT(ast);
                data[i].setC(classes);
                data[i].setR(content);
                data[i].setIndex(i);
//                if(classes.equals("FE") && j < 5 && fe == 1){
//                    old_center[j] = new point();
//                    old_center[j].r = data[i].r;
//                    old_center[j].c = data[i].c;
//                    old_center[j].index = data[i].index;
//                    old_center[j].flag = 0; //0表示聚类中心
//                    fe = 1;
//                    j++;
//                }else if(classes.equals("IM") && j < 5 && im == 0){
//                    old_center[j] = new point();
//                    old_center[j].r = data[i].r;
//                    old_center[j].c = data[i].c;
//                    old_center[j].index = data[i].index;
//                    old_center[j].flag = 0; //0表示聚类中心
//                    im = 1;
//                    j++;
//                }else if(classes.equals("BUG") && j < 5 && bu == 0){
//                    old_center[j] = new point();
//                    old_center[j].r = data[i].r;
//                    old_center[j].c = data[i].c;
//                    old_center[j].index = data[i].index;
//                    old_center[j].flag = 0; //0表示聚类中心
//                    bu =1;
//                    j++;
//                }else if(classes.equals("Overview") && j < 5 && ov == 0){
//                    old_center[j] = new point();
//                    old_center[j].r = data[i].r;
//                    old_center[j].c = data[i].c;
//                    old_center[j].index = data[i].index;
//                    old_center[j].flag = 0; //0表示聚类中心
//                    ov =1;
//                    j++;
//                }else if(classes.equals("Invalid") && j < 5 && in == 0){
//                    old_center[j] = new point();
//                    old_center[j].r = data[i].r;
//                    old_center[j].c = data[i].c;
//                    old_center[j].index = data[i].index;
//                    old_center[j].flag = 0; //0表示聚类中心
//                    in = 1;
//                    j++;
//                }
                //解析语法树和依存关系存入相应的map中
//                Tree temptree = nlp.FeedbacktoTree(content);
//                List templist = nlp.FeedbacktoDep(content);
//                
//                treemap.put(content, temptree);
//                listmap.put(content, templist);
                
                rs.next();
            }
            rs.close();
            stmt.close();
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(KMeansCluster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * 选择聚类中心
     */
    public void ChooseCenter(){
        int num = s.GetDataNum(this.getTable_name());//数据集数量
        Scanner cin = new Scanner(System.in);
        System.out.print("请输入初始化聚类中心个数（随机产生）：");
        int center = cin.nextInt();
//        int center = 2;
        this.old_center = new point[center]; //存放聚类中心
        this.new_center = new point[center];
        
        Random rand = new Random();
        //产生不重复的随机数
        int[] temp = new int[center];
        int rancount = 0;
        while(rancount < center){
            int thistemp = rand.nextInt(num);
            boolean flag = true;
            for(int i = 0;i < center; i++){
                if(thistemp == temp[i]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                temp[rancount] = thistemp;
                rancount++;
            }
        }
        //生成聚类中心      
        for(int i = 0; i < center;i++){
            int thistemp = temp[i];
            old_center[i] = new point();
        //    old_center[i].t = data[thistemp].t;
            old_center[i].r = data[thistemp].r;
            old_center[i].c = data[thistemp].c;
            old_center[i].index = data[thistemp].index;
            old_center[i].flag = 0; //0表示聚类中心
        }
               
        System.out.println("初始聚类中心：");
        for (int i = 0; i < old_center.length; i++) {
        //    System.out.println(old_center[i].t);
            System.out.println(old_center[i].r + "\t" + old_center[i].c);
        }
    }
    
    /**
     * 判断每个点属于哪个聚类中心
     */
    public void Classified(){
        for(int i =0;i<data.length;i++){
            double dist = -999;
            int label = -1;
            for(int j = 0;j < old_center.length;j++){
            /*    if(old_center[j].t == null){ //种群为空导致质心为空
                    continue;
                }*/
                if(old_center[j].r == null){ //种群为空导致质心为空
                    continue;
                }
                double distance = Similarity(data[i],old_center[j]);
                if(distance > dist){
                    dist = distance;
                    label = j;
                }
            }
            data[i].flag = label+1;
        }
    }
        
    /**
     * 生成新的质心
     */
    public void GenCenter(){   
        bestfitness = new double[old_center.length]; //记录最优适应度
        best = new point[old_center.length]; //记录最优染色体
        
        for(int i = 0 ; i < old_center.length; i++){
            bestfitness[i] = -1;
            best[i] = new point();
        }
        
        InitPop();  //初始化种群
        SelectMax(); //选择最优染色体        
    /*    for(int i = 0; i < IterNum; i++){
            Select();
            Cross();
            Mutation();
        //    System.out.println("//////////"+i+"次遗传完成");
        }
        */
        
        //记录新的质心
        System.out.println("新质心：");
        for(int i = 0;i < old_center.length;i++){
            new_center[i] = new point();
        //    new_center[i].t = best[i].t;
            new_center[i].r = best[i].r;
            new_center[i].c = best[i].c;
            new_center[i].flag = 0;
        /*    if(new_center[i].t != null)
                System.out.println("第"+i+"个质心为："+new_center[i].t);*/
            if(new_center[i].r != null)
                System.out.println("第"+i+"个质心为："+new_center[i].r +"\t"+ new_center[i].c);
        }
    }
    
    double[][] Matrix;//相似度矩阵
    public void Readtxt() throws IOException{
        File file = new File(".\\"+table_name+".txt");
        int n = data.length;
        Matrix = new double[n][n];
          //读取出的数组
        BufferedReader in = new BufferedReader(new FileReader(file));  //
        String line;  //一行数据
        int row = 0;
        //逐行读取，并将每个数组放入到数组中
        while ((line = in.readLine()) != null) {
            String[] temp = line.split("\t");
            for (int j = 0; j < temp.length; j++) {
                Matrix[row][j] = Double.parseDouble(temp[j]);
            }
            row++;
        }
        in.close();
    }
    
    public double Getsimi(int a, int b){
        double simi = 0;
        for(int i =0;i<data.length;i++){
            if(i==a){
                for(int j=0;j<data.length;j++){
                    if(j==b){
                        simi = Matrix[i][j];
                    }
                }
            }
        }
        return simi;
    }
    /**
     * 相似度计算，两棵树之间的距离函数
     * @param a 染色体a
     * @param b 染色体b
     * @return 两染色体所代表的树的相似度
     */    
    public Double Similarity(point a, point b){
        double fsimi = 0;      
        
        int i = a.getIndex();
        int j = b.getIndex();
        if(i == j){
            fsimi = 2.0;
        }else{        
            fsimi = this.Getsimi(i, j);
            
            fsimi = fsimi;
        }
        
//        String t1 = a.r; //获取两条评论内容
//        String t2 = b.r;
//        Tree tree1 = treemap.get(t1);  //找到对应的树和依存关系
//        Tree tree2 = treemap.get(t2);
//        
//        List list1 = listmap.get(t1);
//        List list2 = listmap.get(t2);
//        
//        List a12 = nlp.CalSimi(list1, list2, tree1, tree2);
//        List a21 = nlp.CalSimi(list2, list1, tree2, tree1);
//        List v12 = nlp.SimiVector(a12);
//        List v21 = nlp.SimiVector(a21);
//        List v = nlp.FinalVector(v12, v21);
//        double simi = nlp.guiyihua(v);
//        fsimi = nlp.fsim(simi, v);
        
//        List simi = nlp.CalSimi(list1, list2,tree1,tree2); //获取依存关系及对应数值的相似度列表
//        List vec = nlp.SimiVector(simi);
//        
//        int sum = 0;
//        for(int i = 0; i < vec.size(); i++){
//            double val = (double) vec.get(i);
//            sum += val*val;
//        }        
//        sim = Math.sqrt(sum);
    /*    String[] aa = t1.split("\\,");
        String[] bb = t2.split("\\,");
        int temp = 0;
        for(int i = 0; i < aa.length; i++){
            String m = aa[i];
            if(m.equals("1")){
                continue;
            }
            for(int j = 0; j < bb.length; j++){
                String n = bb[j];
                if(m.equals(n)){
                    temp++;
                }
            }
        }
        int same = temp;
        int sum = aa.length + bb.length;
        sim = (float)same/sum;
        */
        return fsimi;
    }
    
    /**
     * 更新原始的聚类中心
     * @param old
     * @param news   
     */
    public void RenewOldCenter(point[] old, point[] news) {
        for (int i = 0; i < old.length; i++) {
        //    old[i].t = news[i].t;
            old[i].r = news[i].r;
            old[i].c = news[i].c;
            old[i].flag = 0;// 表示为聚类中心的标志。
        }
    }
    
    /**
     * 迭代，新旧质心的相似度稳定时则停止迭代
     */
    int clustercount = 1;
    public void Iteration(){
        int stop = -1;  //迭代停止的标志
        double dist = 0;  //新旧质心相似度
        this.Classified();//各数据归类
        this.GenCenter();//重新计算聚类中心
        for(int i = 0;i < old_center.length; i++){
        /*    if( new_center[i].t == null){
                continue;
            }*/
            if( new_center[i].r == null){
                continue;
            }
            dist = this.Similarity(new_center[i],old_center[i]);
            System.out.println("第"+i+"个新旧质心相似度："+dist);
            if(dist >= stopsim ||clustercount > 99){   //每个质心都满足停止条件
                stop = 0;
            }
            else{
                stop = 1;
                break;
            }
        }
        if(stop == 0){
            System.out.println(clustercount+"次聚类完成");
            System.out.println("聚类结束！！！");
        }
        else{        
            this.RenewOldCenter(old_center, new_center);//更新聚类中心
            System.out.println(clustercount+"次聚类完成");
            clustercount++;
            Iteration();
        }
    }
    /**
     * 输出聚类中心
     */
    public void ResultOut(){
        int num = s.GetDataNum(this.getTable_name());
        count = new int[old_center.length];        
        for(int i =0;i < old_center.length ; i++){
        //    System.out.println("聚类中心："+old_center[i].t);
            System.out.println("聚类中心："+old_center[i].r);
            int BUG = 0;
            int Invalid = 0;
            int Overview = 0;
            int FE = 0;
            int IM = 0;
            for (int j = 0; j < data.length; j++) {
                if (data[j].flag == (i + 1)) {
                    count[i]++;
                    if(data[j].getC().equals("BUG"))
                        BUG++;
                    if(data[j].getC().equals("Invalid"))
                        Invalid++;
                    if(data[j].getC().equals("Overview"))
                        Overview++;
                    if(data[j].getC().equals("FE"))
                        FE++;
                    if(data[j].getC().equals("IM"))
                        IM++;
                //    System.out.println(data[j].t);                    
                }
            }
            float per = (float)100*count[i]/num;
            float bug = (float)100*BUG/count[i];
            float inv = (float)100*Invalid/count[i];
            float ove = (float)100*Overview/count[i];
            float fe = (float)100*FE/count[i];
            float im = (float)100*IM/count[i];
            System.out.println("Cluster"+i+"：  "+ count[i] +"    所占比例： "+ per +"%"+
                               "\n"+"BUG类别个数及比例："+BUG+"\t"+bug+"%\nInvalid类别个数及比例："+Invalid+"\t"+inv+
                               "%\nOverview类别个数及比例："+Overview+"\t"+ove+"%\nFE类别个数及比例："+FE+"\t"+fe+"%\nIM类别个数及比例："+IM+"\t"+im+"%");
            System.out.println("*******************BUG类具体用户评论*****************");
            for (int j = 0; j < data.length; j++) {
                if (data[j].flag == (i + 1)) {
                    if(data[j].getC().equals("BUG"))
                        System.out.println(data[j].r);                    
                }
            }
            System.out.println("*******************Invalid类具体用户评论*****************");
            for (int j = 0; j < data.length; j++) {
                if (data[j].flag == (i + 1)) {
                    if(data[j].getC().equals("Invalid"))
                        System.out.println(data[j].r);                    
                }
            }
            System.out.println("*******************Overview类具体用户评论*****************");
            for (int j = 0; j < data.length; j++) {
                if (data[j].flag == (i + 1)) {
                    if(data[j].getC().equals("Overview"))
                        System.out.println(data[j].r);                    
                }
            }
            System.out.println("*******************FE类具体用户评论*****************");
            for (int j = 0; j < data.length; j++) {
                if (data[j].flag == (i + 1)) {
                    if(data[j].getC().equals("FE"))
                        System.out.println(data[j].r);                    
                }
            }
            System.out.println("*******************IM类具体用户评论*****************");
            for (int j = 0; j < data.length; j++) {
                if (data[j].flag == (i + 1)) {
                    if(data[j].getC().equals("IM"))
                        System.out.println(data[j].r);                    
                }
            }
        }
    }
    
/*遗传算法找新质心
    1、初始化种群。种群为当前簇，染色体为字符串所代表的树，基因为树的节点，即字符串中的逗号间隔的一个数字
    2、计算适应度。通过两棵树的相似度计算距离，求一个染色体到各个染色体路径之和
    3、选择。找到和最大的染色体（新质心）。
    4、完成迭代次数结束执行8。否则执行5
    5、交叉。设交叉率为60%，父母间挑选较短的染色体，随机某个节点进行交换。
    6、变异。设变异率为1%，随机挑选两个染色体，找较短一条染色体中某个树节点，进行删除，插入到另一条染色体中。
    7、执行2
    8、记录每个种群中<路径和最大>的染色体作为新质心。
*/
    
    /**
     * 定义初始种群
     */
    
    public void InitPop(){
        count = new int[old_center.length];//存放各种群的规模
        pop = new point[old_center.length][];
        for(int i =0;i < old_center.length ; i++){
            for (int j = 0; j < data.length; j++) {
                if (data[j].flag == (i + 1)) {
                    count[i]++;  //统计各种群的规模                  
                }
            }
            pop[i] = new point[count[i]]; //创建种群
            
            }
        
        for(int i =0;i < old_center.length ; i++){
            int k = 0;
            for (int j = 0; j < data.length; j++) { //初始化种群
                if (data[j].flag == (i + 1)) {
                    pop[i][k] = new point();
                //    pop[i][k].t = data[j].t; 
                    pop[i][k].r = data[j].r; 
                    pop[i][k].c = data[j].c; 
                    pop[i][k].flag = data[j].flag;
                    k++;
                }
            }
        }
    //    System.out.println("初始化完成");
    }
    /**
     * 选择与其他评论相似度最高的个体
     */    
    public void SelectMax(){
        double[][] d = new double[old_center.length][]; //记录种群中每条染色体的适应值        
        for(int i = 0; i < old_center.length ; i++){            
            if(pop[i].length == 0){ //种群为空
                continue;
            }            
            d[i] = new double[pop[i].length]; //记录种群中每条染色体的适应值                        
            for(int j = 0; j < pop[i].length ; j++){
                for(int k = 0; k < pop[i].length; k++){
                    if(pop[i][j] != pop[i][k])
                        d[i][j] += Similarity(pop[i][j],pop[i][k]);//计算适应值，即一个种群中每个染色体到其他染色体的距离并求和
                }
                if(d[i][j] > bestfitness[i]){     //记录最大值
                    bestfitness[i] = d[i][j];
                //    best[i].t = pop[i][j].t;
                    best[i].r = pop[i][j].r;
                    best[i].c = pop[i][j].c;
                    best[i].flag = pop[i][j].flag;
                }
            }           
        }
    }
    /**
     * 轮盘赌法选择
     */
    /*
    public void Select(){
        
        float[][] d = new float[old_center.length][]; //记录种群中每条染色体的适应值
        float[][] p = new float[old_center.length][]; //记录种群中每条染色体的选择概率
        float[][] q = new float[old_center.length][]; //记录种群中每条染色体的累计概率
        float[] sum = new float[old_center.length];//记录种群的适应值之和
        
        for(int i = 0; i < old_center.length ; i++){
            
            if(pop[i].length == 0){ //种群为空
                continue;
            }
            
            d[i] = new float[pop[i].length]; //记录种群中每条染色体的适应值
            p[i] = new float[pop[i].length]; //记录种群中每条染色体的选择概率
            q[i] = new float[pop[i].length]; //记录种群中每条染色体的累计概率
                        
            for(int j = 0; j < pop[i].length ; j++){
                for(int k = 0; k < pop[i].length; k++){
                    if(pop[i][j] != pop[i][k])
                        d[i][j] += Similarity(pop[i][j],pop[i][k]);//计算适应值，即一个种群中每个染色体到其他染色体的距离并求和
                }
                if(d[i][j] > bestfitness[i]){     //记录最大值
                    bestfitness[i] = d[i][j];
                //    best[i].t = pop[i][j].t;
                    best[i].c = pop[i][j].c;
                    best[i].flag = pop[i][j].flag;
                }
                sum[i] += d[i][j]; //累计每个种群中所有个体适应值之和
            }
            
            for(int j = 0; j < pop[i].length; j++){ //轮盘赌概率计算
                p[i][j] = d[i][j] / sum[i];
                if(j == 0){
                    q[i][j] = p[i][j];
                }
                else{
                    q[i][j] = q[i][j-1] + p[i][j];
                }
            }
            for(int j = 0; j < pop[i].length; j++){ //选择概率高的染色体进入下一代
                double r = Math.random();
                if( r <= q[i][0]){
                //    pop[i][j].t = pop[i][0].t;
                    pop[i][j].c = pop[i][0].c;
                    pop[i][j].flag = pop[i][0].flag;
                }
                else{
                    for(int k = 1; k < pop[i].length; k++){
                        if(r < q[i][k]){
                        //    pop[i][j].t = pop[i][k].t;
                            pop[i][j].c = pop[i][k].c;
                            pop[i][j].flag = pop[i][k].flag;
                            break;
                        }
                    }
                }
            }
        }
    //    System.out.println("选择完成");
    }
    */
    /**
     * 交叉
     */
    /*
    public void Cross(){
        Random rand = new Random(); 
        for(int i = 0; i < old_center.length ; i++){
            if(pop[i].length == 0){ //种群判空
                continue;
            }
            for(int j = 0; j < pop[i].length-1; j++){
                if(Math.random() < crossrate){
                    String a = "";
                    String b = "";
                    
                    String[] aa = pop[i][j].t.split("\\,"); //染色体分解为基因
                    String[] bb = pop[i][j+1].t.split("\\,");
                    
                    int ran = Math.min(aa.length, bb.length); //从较短的染色体中选择交叉点
                    int pos = rand.nextInt(ran) + 1;
                    
                    for(int k = 0; k < pos; k++){  //从pos点交叉数组并拼接为字符串
                        String t1 = aa[k] + ",";
                        a = a + t1;
                        String t2 = bb[k] + ",";
                        b = b + t2;
                    }
                    for(int k = pos; k < bb.length; k++){ 
                        String t1 = bb[k] + ",";
                        a = a + t1;
                    }
                    for(int k = pos; k < aa.length; k++){  
                        String t2 = aa[k] + ",";
                        b = b + t2;
                    }
                    pop[i][j].t = a;     //更新染色体
                    pop[i][j+1].t = b;
                }
            }
        }
    //    System.out.println("交叉完成");
    }
    */
    /**
     * 变异
     */ 
    /*
    public void Mutation(){
        Random rand = new Random(); 
        for(int i = 0; i < old_center.length; i++){            
            int r1 = 0;  //待变异的染色体编号
            int r2 = 0;
            
            //种群判空
            if(pop[i].length == 0){  
                continue;
            }
            else{
                r1 = rand.nextInt(pop[i].length); //选出随机两条染色体
                r2 = rand.nextInt(pop[i].length);
            }   
            if(Math.random() < mutarate){
                String a = "";  //变异后的染色体中的字符串
                String b = "";
                
                String[] aa = pop[i][r1].t.split("\\,"); //染色体分解为基因
                String[] bb = pop[i][r2].t.split("\\,"); 
                
                int ran = Math.min(aa.length, bb.length); //从较短的染色体中选择变异点
                int pos = rand.nextInt(ran);
                                
                for(int k = 0; k < pos; k++){  //变异pos位置的节点，将r1染色体pos位置的节点删除，插入r2中的pos位置，得到两条变异染色体
                    String t1 = aa[k] + ",";
                    a = a + t1;
                    String t2 = bb[k] + ",";
                    b = b + t2;
                }
                String temp = aa[pos] + ",";  //删除a中pos位置的节点
                b = b + temp; //插入b中
                
                for(int k = pos+1; k < aa.length; k++){ 
                    String t1 = aa[k] + ",";
                    a = a + t1;
                }
                for(int k = pos; k < bb.length; k++){  
                    String t2 = bb[k] + ",";
                    b = b + t2;
                }
                pop[i][r1].t = a;     //更新染色体
                pop[i][r2].t = b;
            }
        }
    //    System.out.println("变异完成");
    }
    */
    
}
