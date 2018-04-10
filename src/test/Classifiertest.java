/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier; 
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.*;
import weka.core.converters.*;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.Remove;
/**
 *
 * @author dell-pc
 */
public class Classifiertest {
    
    public JTextArea txtJ48 = new JTextArea();
    public JTextArea txtSMO = new JTextArea();
    
    
    /**
     * SMO算法训练模型并分类
     * @param table_name 预测集表名
     * @throws Exception 
     */
    public void SMO(String table_name) throws Exception{
        //从数据库读取训练集
        InstanceQuery query = new InstanceQuery();
        query.setUsername("song");
        query.setPassword("123456");
        query.setQuery("select * from train");
    //    txtSMO.append("数据库连接成功！\n");
        Instances traindata = query.retrieveInstances(); 
        //数据预处理
        //1.remove无用的文本特征
        String[] re_option = new String[2];
        re_option[0] = "-R";
        re_option[1] = "1-5,7"; //移除评论作者，标题，内容，词汇数目等属性
        Remove remove = new Remove();
        remove.setOptions(re_option);
        remove.setInputFormat(traindata);
        Instances newdata = Filter.useFilter(traindata, remove);        
        //2.转化类型
        NumericToNominal transtype = new NumericToNominal();
        String[] ty_option = new String[2];
        ty_option[0] = "-R";
        ty_option[1] = "first-last";
        transtype.setOptions(ty_option);        
        transtype.setInputFormat(newdata);
        newdata = Filter.useFilter(newdata, transtype); 
               
        //调用SMO算法
        Classifier smo = new SMO();    
        Instances d_Train = newdata;        
        //设置分类属性所在行号（第一行为0号）
        d_Train.setClassIndex(0);  
        //训练模型
        smo.buildClassifier(d_Train);  
        Evaluation eval = new Evaluation(d_Train);
        eval.evaluateModel(smo, d_Train);//测试&评价算法        
        txtSMO.append("Classifier model:\tSMO\n");
        txtSMO.append(eval.toSummaryString("\n=== Summary ===\n",false)+"\n");
        txtSMO.append(eval.toClassDetailsString()+"\n");
        txtSMO.append(eval.toMatrixString()+"\n"); 
        
    /*    System.out.println(eval.toSummaryString("\n=== Summary ===\n",false)+"\n");
        System.out.println(eval.toClassDetailsString()+"\n");
        System.out.println(eval.toMatrixString()+"\n");
    */    
        //从数据库读入预测文件
        query.setUsername("song");
        query.setPassword("123456");
        query.setQuery("select * from " + table_name);
        Instances predata = query.retrieveInstances();     
        //转化类型
        transtype.setOptions(ty_option);        
        transtype.setInputFormat(predata);
        predata = Filter.useFilter(predata, transtype); 
        
        //保存为arff文件（备份便于查找评论信息）
        ArffSaver saver = new ArffSaver();  
        saver.setInstances(predata);  
        saver.setFile(new File("./data/pre_info.arff")); 
        saver.writeBatch();  
        
        //打开保存好的arff文件，数据预处理（类型转化、删除无关特征、增加classes属性）
        //1.读入预测集
        File inputFile = new File("./data/pre_info.arff");  
        ArffLoader atf = new ArffLoader();   
        atf.setFile(inputFile);  
        Instances Pre = atf.getDataSet();         
        //2.remove无用的文本特征
        re_option = new String[2];
        re_option[0] = "-R";
        re_option[1] = "1-6"; //移除评论作者，标题，内容，词汇数目等属性
        remove.setOptions(re_option);
        remove.setInputFormat(Pre);
        Instances newpre = Filter.useFilter(Pre, remove); 
        //3.添加classes属性使训练集与预测集一致
        Add add = new Add();   
        add.setAttributeIndex("1");  
        add.setNominalLabels("Overview,Invalid,Demand,Specific");  
        add.setAttributeName("classes");  
        add.setInputFormat(newpre);  
        newpre = Filter.useFilter(newpre,add);        
        
        //对预测集进行分类
        Instances d_Pre = newpre; 
        d_Pre.setClassIndex(0);//设置分类属性
        int sum = d_Pre.numInstances();
        for(int i = 0; i < sum; i++){
            double clslabel = smo.classifyInstance(d_Pre.instance(i));
            d_Pre.instance(i).setClassValue(clslabel);
        }     
        
        //将结果保存为arff文件（便于查找预测结果信息）
        saver.setInstances(d_Pre);  
        saver.setFile(new File("./data/pre_result.arff")); 
        saver.writeBatch();         
        
    }
    
    /**
     * 运行SMO算法并读取模型评价结果
     * @param table_name 预测集表名
     * @return
     * @throws Exception 
     */
    public String getSMOResult(String table_name) throws Exception{
        this.SMO(table_name);
        String str = txtSMO.getText();
        return str;
    }
    
    /**
     * 统计分类结果
     * @return 各类别的数目（Overview,Invalid,Demand,Specific）
     */
        public int[] StatisticsResult(){
        //用于统计每个类别的个数
        int[] distribution = {0,0,0,0,0};
        try {
            //读取预测集评论信息文件
            File inputFile = new File("./data/pre_info.arff");
            ArffLoader atf = new ArffLoader();
            atf.setFile(inputFile);
            Instances pre_info = atf.getDataSet();
            //读取预测集结果文件
            inputFile = new File("./data/pre_result.arff");
            atf.setFile(inputFile);
            Instances pre_result = atf.getDataSet();
            for(int i = 0; i < pre_info.numInstances(); i++){                
                //统计各类别的结果
                if(pre_result.instance(i).stringValue(0).equals("Overview"))
                    distribution[0]++;
                if(pre_result.instance(i).stringValue(0).equals("Invalid"))
                    distribution[1]++;
                if(pre_result.instance(i).stringValue(0).equals("Demand"))
                    distribution[2]++;
                if(pre_result.instance(i).stringValue(0).equals("Specific"))
                    distribution[3]++;
            }    
            distribution[4] = pre_info.numInstances();
        }catch (IOException ex) {
            Logger.getLogger(Classifiertest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return distribution;
    }    
    
    /**
     * 存储分类结果
     * @param table_name 预测集表名
     */
        public void RecordClassifyResult(String table_name){
        try {
            //读取预测集评论信息文件
            File inputFile = new File("./data/pre_info.arff");
            ArffLoader atf = new ArffLoader();
            atf.setFile(inputFile);
            Instances pre_info = atf.getDataSet();
            //读取预测集结果文件
            inputFile = new File("./data/pre_result.arff");
            atf.setFile(inputFile);
            Instances pre_result = atf.getDataSet();
            //在备份表输出相同索引的评论信息和分类结果
            SQL sql = new SQL();
            table_name = "cpy_" + table_name;
            //添加类别列
            sql.AddColumn(table_name, "classes", "varchar(100)");
            for(int i = 0; i < pre_info.numInstances(); i++){
                String content = pre_info.instance(i).stringValue(4);
                String label = pre_result.instance(i).stringValue(0);
                //标记评论对应的类别
                sql.RemarkClasses(table_name, content, label);
            }
        } catch (IOException ex) {
            Logger.getLogger(Classifiertest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
    
    
    
    public void TestCluster() throws Exception{
        //从数据库读取数据文件
        InstanceQuery query = new InstanceQuery();
        query.setUsername("song");
        query.setPassword("123456");
        query.setQuery("select * from test50");
        Instances data = query.retrieveInstances();
        //实现聚类
        String[] option=new String[6];  //设置相应的参数  
        option[0]="-N"; //聚类数  
        option[1]="4";  
        option[2]="-A"; //距离算法
        option[3]="weka.core.EuclideanDistance";
        option[4]="-I"; //最大迭代次数  
        option[5]="500"; 
        SimpleKMeans kmeans = new SimpleKMeans(); // new instance of clusterer  
        kmeans.setOptions(option); // set the options  
        kmeans.buildClusterer(data); // build the clusterer 
        
        //评价聚类，使用ClusterEvaluation 
        ClusterEvaluation eval = new ClusterEvaluation();  
        eval.setClusterer(kmeans);  
        eval.evaluateClusterer(new Instances(data));  
        System.out.println(eval.clusterResultsToString()); 
    }
    
    //C4.5算法
    public void J48test() throws IOException, Exception{
        Classifier J48 = new J48();  
        File inputFile = new File("D:\\Program Files\\Weka-3-8\\data\\weather.numeric.arff");//训练语料文件  
        ArffLoader atf = new ArffLoader();   
        atf.setFile(inputFile);  
        Instances d_Train = atf.getDataSet(); // 读入训练文件        
        inputFile = new File("D:\\Program Files\\Weka-3-8\\data\\weather.numeric.arff");//测试语料文件  
        atf.setFile(inputFile);            
        Instances d_Test = atf.getDataSet(); // 读入测试文件
        
        d_Train.setClassIndex(0); 
        d_Test.setClassIndex(0); //设置分类属性所在行号（第一行为0号）
        
        J48.buildClassifier(d_Train);//训练
        Evaluation eval = new Evaluation(d_Train);
        eval.evaluateModel(J48, d_Test);//测试
        System.out.println(eval.toSummaryString("\n=== Summary ===\n",false));
        System.out.println(eval.toClassDetailsString());
        System.out.println(eval.toMatrixString());
        
        txtJ48.append("Classifier model:\tJ48\n");
        txtJ48.append(eval.toSummaryString("\n=== Summary ===\n",false)+"\n");
        txtJ48.append(eval.toClassDetailsString()+"\n");
        txtJ48.append(eval.toMatrixString()+"\n");
    }
    //读取C4.5算法结果
    public String getJ48Result() throws Exception{
    //    J48test();
        this.J48test();
        String str = txtJ48.getText();
        return str;
    }
    
}
