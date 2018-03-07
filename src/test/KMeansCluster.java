/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dell-pc
 */
class point{
    public String t = null; //语法树
    public String r = null; //用户评论
    public String c = null; //类别
    public int flag = -1;  //标记
    
    public String getT(){
        return t;
    }
    public void setT(String t){
        this.t = t;
    }
    
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
    private final String userName = "song"; 
    private final String userPwd = "123456"; 
    private Connection conn;  
    
    point[] data;//数据集
    point[] old_center = null;//原始聚类中心
    point[] new_center = null;//新的聚类中心
    double stopsim = 0.80; //迭代停止时的新旧质心相似程度
    SQL s = new SQL();
    
    
    point[][] pop;//种群
    int[] count;//种群规模
    int IterNum = 10;//遗传迭代次数
    double crossrate = 0.60;//交叉率
    double mutarate = 0.01;//突变率    
    float[] bestfitness;//最优解，即最大距离和
    point[] best; //最优染色体
    
    /**
     * 从某表中导入数据
     * @param table_name 表名
     */
    public void ImportData(String table_name){
        try {
            int num = s.GetDataNum(table_name);
            data = new point[num];
            
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            
            rs=stmt.executeQuery("SELECT ast,classes,Review_Content FROM "+table_name);
            rs.first();//读取数据库第一行记录
            for(int i = 0;i < num ;i++){   
                data[i] = new point();// 对象创建
                String ast = rs.getString("ast");
                String classes = rs.getString("classes");
                String content = rs.getString("Review_Content");
                data[i].setT(ast);
                data[i].setC(classes);
                data[i].setR(content);
                rs.next();
            }
            rs.close();
            stmt.close();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KMeansCluster.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(KMeansCluster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * 选择聚类中心
     * @param table_name 表名
     */
    public void ChooseCenter(String table_name){
        int num = s.GetDataNum(table_name);//数据集数量
        Scanner cin = new Scanner(System.in);
        System.out.print("请输入初始化聚类中心个数（随机产生）：");
        int center = cin.nextInt();
        this.old_center = new point[center]; //存放聚类中心
        this.new_center = new point[center];
        
        Random rand = new Random();
        //产生不重复的随机数
        int[] temp = new int[center];
        int count = 0;
        while(count < center){
            int thistemp = rand.nextInt(num);
            boolean flag = true;
            for(int i = 0;i < center; i++){
                if(thistemp == temp[i]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                temp[count] = thistemp;
                count++;
            }
        }
        //生成聚类中心      
        for(int i = 0; i < center;i++){
            int thistemp = temp[i];
            old_center[i] = new point();
            old_center[i].t = data[thistemp].t;
            old_center[i].r = data[thistemp].r;
            old_center[i].c = data[thistemp].c;
            old_center[i].flag = 0; //0表示聚类中心
        }
       
        System.out.println("初始聚类中心：");
        for (int i = 0; i < old_center.length; i++) {
            System.out.println(old_center[i].t);
        }
    }
    
    /**
     * 判断每个点属于哪个聚类中心
     */
    public void Classified(){
        for(int i =0;i<data.length;i++){
            float dist = 999;
            int lable = -1;
            for(int j = 0;j < old_center.length;j++){
                if(old_center[j].t == null){ //种群为空导致质心为空
                    continue;
                }
                float distance = Similarity(data[i],old_center[j]);
                if(distance < dist){
                    dist = distance;
                    lable = j;
                }
            }
            data[i].flag = lable+1;
        }
    }
        
    /**
     * 生成新的质心
     */
    public void GenCenter(){
        bestfitness = new float[old_center.length]; //记录最优适应度
        best = new point[old_center.length]; //记录最优染色体
        
        for(int i = 0 ; i < old_center.length; i++){
            bestfitness[i] = -1;
            best[i] = new point();
        }
        
        InitPop();  //初始化种群
        for(int i = 0; i < IterNum; i++){
            Select();
            Cross();
            Mutation();
        //    System.out.println("//////////"+i+"次遗传完成");
        }
        //记录新的质心
        System.out.println("新质心：");
        for(int i = 0;i < old_center.length;i++){
            new_center[i] = new point();
            new_center[i].t = best[i].t;
            new_center[i].r = best[i].r;
            new_center[i].c = best[i].c;
            new_center[i].flag = 0;
            if(new_center[i].t != null)
                System.out.println("第"+i+"个质心为："+new_center[i].t);
        }
    }
    
    /**
     * 相似度计算，两棵树之间的距离函数
     * @param a 染色体a
     * @param b 染色体b
     * @return 两染色体所代表的树的相似度
     */    
    public Float Similarity(point a, point b){
        float sim = 0;
        String t1 = a.t;
        String t2 = b.t;
        String[] aa = t1.split("\\,");
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
    //    int min = Math.min(aa.length, bb.length);
        sim = (float)same/sum;
        return sim;
    }
    
    /**
     * 更新原始的聚类中心
     * @param old
     * @param news   
     */
    public void RenewOldCenter(point[] old, point[] news) {
        for (int i = 0; i < old.length; i++) {
            old[i].t = news[i].t;
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
        float dist = 0;  //新旧质心相似度
        this.Classified();//各数据归类
        this.GenCenter();//重新计算聚类中心
        for(int i = 0;i < old_center.length; i++){
            if( new_center[i].t == null){
                continue;
            }
            dist = this.Similarity(old_center[i], new_center[i]);
            System.out.println("第"+i+"个新旧质心相似度："+dist);
            if(dist >= stopsim){   //每个质心都满足停止条件
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
     * @param table_name
     */
    public void ResultOut(String table_name){
        int num = s.GetDataNum(table_name);
        count = new int[old_center.length];        
        for(int i =0;i < old_center.length ; i++){
            System.out.println("聚类中心："+old_center[i].t);
            int Demand = 0;
            int Invalid = 0;
            int Overview = 0;
            int Specific = 0;
            for (int j = 0; j < data.length; j++) {
                if (data[j].flag == (i + 1)) {
                    count[i]++;
                    if(data[j].getC().equals("Demand"))
                        Demand++;
                    if(data[j].getC().equals("Invalid"))
                        Invalid++;
                    if(data[j].getC().equals("Overview"))
                        Overview++;
                    if(data[j].getC().equals("Specific"))
                        Specific++;
                //    System.out.println(data[j].t);                    
                }
            }
            float per = (float)100*count[i]/num;
            float dem = (float)100*Demand/count[i];
            float inv = (float)100*Invalid/count[i];
            float ove = (float)100*Overview/count[i];
            float spe = (float)100*Specific/count[i];
            System.out.println("Cluster"+i+"：  "+ count[i] +"    所占比例： "+ per +"%"+
                               "\n"+"Demand类别个数及比例："+Demand+"\t"+dem+"%\nInvalid类别个数及比例："+Invalid+"\t"+inv+
                               "%\nOverview类别个数及比例："+Overview+"\t"+ove+"%\nSpecific类别个数及比例："+Specific+"\t"+spe+"%");
            System.out.println("*******************Demand类具体用户评论*****************");
            for (int j = 0; j < data.length; j++) {
                if (data[j].flag == (i + 1)) {
                    if(data[j].getC().equals("Demand"))
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
            System.out.println("*******************Specific类具体用户评论*****************");
            for (int j = 0; j < data.length; j++) {
                if (data[j].flag == (i + 1)) {
                    if(data[j].getC().equals("Specific"))
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
                    pop[i][k].t = data[j].t; 
                    pop[i][k].c = data[j].c; 
                    pop[i][k].flag = data[j].flag;
                    k++;
                }
            }
        }
    //    System.out.println("初始化完成");
    }
    /**
     * 轮盘赌法选择
     */
    
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
                    best[i].t = pop[i][j].t;
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
                    pop[i][j].t = pop[i][0].t;
                    pop[i][j].c = pop[i][0].c;
                    pop[i][j].flag = pop[i][0].flag;
                }
                else{
                    for(int k = 1; k < pop[i].length; k++){
                        if(r < q[i][k]){
                            pop[i][j].t = pop[i][k].t;
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
    /**
     * 交叉
     */
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
    /**
     * 变异
     */ 
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
    
}
