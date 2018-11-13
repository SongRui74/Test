/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import edu.stanford.nlp.trees.Tree;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dell-pc
 */
public class SQL {

    private final String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //加载JDBC驱动
    private final String dbURL = "jdbc:sqlserver://localhost:1433; DatabaseName=mypro"; //连接服务器和数据库mypro
    private final String userName = "sa";
    private final String userPwd = "123456";
    private Connection conn;

    public String tablename;

    public void settablename(String name) {
        tablename = name;
    }

    public String gettablename() {
        return tablename;
    }

    /**
     * 显示一条评论的相关信息
     *
     * @param id 10，000条元组中的ID
     * @param table_name 表名
     * @return
     */
    public ResultSet Showinfo(String id, String table_name) throws ClassNotFoundException, SQLException {
        Class.forName(driverName);
        conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库

        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs;
        String sql = "select * from " + table_name + ",new_Apps where " + table_name + ".ID = '" + id + "' and " + table_name + ".APP_ID = new_Apps.APP_ID";
        rs = stmt.executeQuery(sql);

        return rs;
    }

    /**
     * 根据类名获取评论内容
     *
     * @param classname 类名
     * @return 评论内容
     */
    public List<String> GetContentwithClass(String classname) {
        /*    Classifiertest p = new Classifiertest();
        String name = p.gettablename();
        this.settablename(name);
         */
        List list = new ArrayList();
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            ResultSet rs;
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //    tablename = "cpy_"+tablename;
            //    tablename = "Demand";
            String sql = "select * from " + classname;
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String content = rs.getString("info");
                if (!content.equals("")) {
                    if (content.contains(" ")) {
                        list.add(content);
                    }
                }
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * 根据类名获取评论内容
     *
     * @param classname 类名
     * @param topic 类别中的子类别名
     * @return 评论内容
     */
    public List<String> GetContentwithClass(String classname, String topic) {
        List list = new ArrayList();
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            ResultSet rs;
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String sql = "select * from " + classname + " where topic = '" + topic + "'";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String content = rs.getString("info");
                if (!content.equals("")) {
                    if (content.contains(" ")) {
                        list.add(content);
                    }
                }
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    Standfordnlp nlp = new Standfordnlp();

    /**
     * 记录评论解析后的句法树
     *
     * @param table_name
     * @return 评论-句法树Map
     */
    public Map<String, Tree> RecordTreeMap(String table_name) {
        Map<String, Tree> treemap = new HashMap();
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            rs = stmt.executeQuery("SELECT * FROM " + table_name);

            //循环记录
            while (rs.next()) {
                /*文本转化为树*/
                String content = rs.getString("Review_Content");
                /*转化为小写*/
                content = content.toLowerCase();
                Tree tree_value = nlp.FeedbacktoTree(content);
                /*写入map中*/
                treemap.put(content, tree_value);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return treemap;
    }

    /**
     * 记录评论解析后的依存关系列表
     *
     * @param table_name
     * @return 评论-依存关系列表Map
     */
    public Map<String, List> RecordDepMap(String table_name) {
        Map<String, List> listmap = new HashMap();
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            rs = stmt.executeQuery("SELECT * FROM " + table_name);

            //循环记录
            while (rs.next()) {
                /*文本转化为树和依存关系列表*/
                String content = rs.getString("Review_Content");
                content = content.toLowerCase();
                List dep_value = nlp.FeedbacktoDep(content);
                /*写入map中*/
                listmap.put(content, dep_value);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listmap;
    }

    /**
     * 记录评论解析后的中心词
     *
     * @param table_name
     * @return 评论-中心词Map
     */
    public Map<String, String> RecordCenterMap(String table_name) {
        Map<String, String> centermap = new HashMap();
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            rs = stmt.executeQuery("SELECT * FROM " + table_name);

            //循环记录
            while (rs.next()) {
                /*文本找到为中心词*/
                String content = rs.getString("Review_Content");
                content = content.toLowerCase();
                String center = nlp.ContentCenter(content);
                /*写入map中*/
                centermap.put(content, center);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return centermap;
    }

    /**
     * 标记语义依赖模式
     *
     * @param table_name 表名
     * @param col 列名
     * @param treemap 评论-句法树map
     * @param str 匹配关系式
     */
    public void RemarkPattern(String table_name, String col, Map treemap, String str) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();

            rs = stmt.executeQuery("SELECT * FROM " + table_name);

            Tregex regex = new Tregex();
            int num = 0;//记录评论中是否包含该特征
            //循环标记
            while (rs.next()) {
                /*文本特征判断*/
                String content = rs.getString("Review_Content");
                content = content.toLowerCase();
                Tree t = (Tree) treemap.get(content);
                //若已经标记则继续循环
                if (rs.getString("info").equals("1")) {
                    continue;
                }
                //若存在匹配到的表达式
                if (regex.TregexIsMatch(t, str)) {
                    num = 1; //存在该特征记为1
                } else {
                    num = 0;
                }
                /*写入数据库中*/
                String sql = UpdateSql(rs, table_name, col, num);
                stmt2.executeUpdate(sql);
            }
            rs.close();
            stmt.close();
            stmt2.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 提取评论关键信息
     *
     * @param table_name 表名
     * @param col 列名
     * @param treemap 评论-句法树map
     * @param listmap
     * @param centermap
     */
    public void InfoExtractor(String table_name, String col, Map treemap, Map listmap, Map centermap) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + table_name);

            String info = "";
            MainPartExtractor maininfo = new MainPartExtractor();
            //循环标记
            while (rs.next()) {
                String content = rs.getString("Review_Content");
                content = content.toLowerCase();

                maininfo.tree = (Tree) treemap.get(content);
                maininfo.center = (String) centermap.get(content);
                maininfo.list = (List) listmap.get(content);

                //    System.out.println(maininfo.tree.pennString());
                //    System.out.println(maininfo.list.toString());
                //    System.out.println(maininfo.center);
                String infoflag = rs.getString("info");
                if (infoflag.equals("1")) {
                    //获取关键信息
                    if (content.contains(",")) {
                        info = maininfo.getmainpart2(content);
                    } else {
                        info = maininfo.getmainpart();
                    }
                    if (info.equals("  ")) { //未提取主干的标记no_info
                        info = "no_info";
                    }
                } else if (infoflag.equals("0")) {//未提取信息的标记no_info
                    info = "no_info";
                } else { //已有信息的保持原有内容
                    info = infoflag;
                }
                //    System.out.println("info:  " + info + "\n");
                /*写入数据库中*/
                info = SqlSingleQuote(info);
                String sql = UpdateSql(rs, table_name, col, info);
                stmt2.executeUpdate(sql);
            }
            rs.close();
            stmt.close();
            stmt2.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 完善提取评论关键信息
     *
     * @param table_name 表名
     * @param col 列名
     * @param treemap 评论-句法树map
     * @param str 匹配关系式
     */
    public void InfoExtractorPlus(String table_name, String col, Map treemap, String str) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + table_name);

            Tregex regex = new Tregex();
            String info = "";
            //循环标记
            while (rs.next()) {
                String content = rs.getString("Review_Content");
                content = content.toLowerCase();
                Tree t = (Tree) treemap.get(content);
                String infoflag = rs.getString("info");
                if (infoflag.equals("1")) {
                    //获取关键信息
                    info = regex.Tregexinfo(t, str);
                    //Specific
                    if (content.contains("easy to") && !info.equals("")) {
                        info = "easy to " + info;
                    } else if (content.contains("helpful ") && !info.equals("")) {
                        info = "helpful " + info;
                    }//Demand
                    else if (content.contains("wish") && !info.equals("")) {
                        info = "wish " + info;
                    } else if (content.contains("please fix") && !info.equals("")) {
                        info = "please fix";
                    } else if (content.contains("please update") && !info.equals("")) {
                        info = "please update";
                    } else if (content.contains("add ") && !info.equals("")) {
                        info = "add " + info;
                    } else {
                        info = "1";
                    }
                    /*写入数据库中*/
                    info = SqlSingleQuote(info);
                    String sql = UpdateSql(rs, table_name, col, info);
                    stmt2.executeUpdate(sql);
                }
            }
            rs.close();
            stmt.close();
            stmt2.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //可删除
    /**
     * 提取具体评价类的评论特征信息
     *
     * @param table_name 表名
     * @param col 列名
     * @param treemap 评论-句法树map
     * @param str 匹配关系式
     */
    public void ExtractInfo(String table_name, String col, Map treemap, String str) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + table_name);

            Tregex regex = new Tregex();
            String info = "";
            //循环标记
            while (rs.next()) {
                String content = rs.getString("Review_Content");
                content = content.toLowerCase();
                Tree t = (Tree) treemap.get(content);
                String infoflag = rs.getString("info");
                if (infoflag.equals("")) {
                    //获取关键信息
                    info = regex.Tregexinfo(t, str);
                    //Demand
                    if (content.contains("wish") && !info.equals("")) {
                        info = "wish " + info;
                    } else if (content.contains("add ") && !info.equals("")) {
                        info = "add " + info;
                    } else if (content.contains("want ") && content.contains("back") && !info.equals("")) {
                        info = "want " + info + " back";
                    } //Specific
                    else if (content.contains("easy to") && !info.equals("")) {
                        info = "easy to " + info;
                    } else if (content.contains("love") && !info.equals("")) {
                        info = "love " + info;
                    } else if ((content.contains("helps ") || content.contains("helpful ")) && !info.equals("")) {
                        info = "helpful: " + info;
                    } //Demand
                    else if (content.contains("never") && content.contains("problem") && !info.equals("")) {
                        info = "";
                    } else if (content.contains("can't") && !info.equals("")) {
                        info = "can't " + info;
                    }
                    /*写入数据库中*/
                    info = SqlSingleQuote(info);
                    String sql = UpdateSql(rs, table_name, col, info);
                    stmt2.executeUpdate(sql);
                }
            }
            rs.close();
            stmt.close();
            stmt2.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //可删除
    /**
     * 提取具体评价类的评论特征信息
     *
     * @param table_name 表名
     * @param col 列名
     * @param treemap 评论-句法树map
     * @param str 匹配字符串
     */
    public void ExtractWordInfo(String table_name, String col, Map treemap, String str) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + table_name);

            String info = "";
            //循环标记
            while (rs.next()) {
                String content = rs.getString("Review_Content");
                content = content.toLowerCase();
                String infoflag = rs.getString("info");
                if (infoflag.equals("")) {
                    if (str.equals("fix") && content.contains(str)) {
                        info = "please fix";
                        String sql = UpdateSql(rs, table_name, col, info);
                        stmt2.executeUpdate(sql);
                        continue;
                    }
                    if (str.equals("please update") && content.contains(str)) {
                        info = "please update";
                        String sql = UpdateSql(rs, table_name, col, info);
                        stmt2.executeUpdate(sql);
                        continue;
                    }
                    if (str.equals("update") && content.contains(str) && content.contains("please")) {
                        info = "please update";
                        String sql = UpdateSql(rs, table_name, col, info);
                        stmt2.executeUpdate(sql);
                        continue;
                    }
                    if (str.equals("crash") && content.contains(str) && content.contains("open")) {
                        info = "crash at open";
                        String sql = UpdateSql(rs, table_name, col, info);
                        stmt2.executeUpdate(sql);
                        continue;
                    }
                    if (str.equals("crash") && content.contains(str) && content.contains("login")) {
                        info = "crash at login";
                        String sql = UpdateSql(rs, table_name, col, info);
                        stmt2.executeUpdate(sql);
                        continue;
                    }
                    if (str.equals("crash") && content.contains(str)) {
                        info = "the app crashes";
                        String sql = UpdateSql(rs, table_name, col, info);
                        stmt2.executeUpdate(sql);
                        continue;
                    }
                    if (str.equals("need") && content.contains(str) && content.contains("touch id")) {
                        info = "needs touch id";
                        String sql = UpdateSql(rs, table_name, col, info);
                        stmt2.executeUpdate(sql);
                        continue;
                    }
                    if (str.equals("need") && content.contains(str) && content.contains("update")) {
                        info = "needs update";
                        String sql = UpdateSql(rs, table_name, col, info);
                        stmt2.executeUpdate(sql);
                        continue;
                    }
                    if (str.equals("version") && content.contains(str)) {
                        info = "please remove/provide version";
                        String sql = UpdateSql(rs, table_name, col, info);
                        stmt2.executeUpdate(sql);
                        continue;
                    }
                    if (str.equals("bug") && content.contains(str) && !content.contains("no ")) {
                        info = "lots of bugs";
                        String sql = UpdateSql(rs, table_name, col, info);
                        stmt2.executeUpdate(sql);
                        continue;
                    }
                    if (str.equals("fail") && content.contains(str)) {
                        info = "fail";
                        String sql = UpdateSql(rs, table_name, col, info);
                        stmt2.executeUpdate(sql);
                        continue;
                    }
                }
            }
            rs.close();
            stmt.close();
            stmt2.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 标记文本属性特征（句法树类）
     *
     * @param table_name 表名
     * @param col 列名
     * @param treemap 评论-句法树map
     * @param str 匹配关系式
     */
    public void RemarkTreeFeature(String table_name, String col, Map treemap, String str) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();

            rs = stmt.executeQuery("SELECT * FROM " + table_name);

            Tregex regex = new Tregex();
            int num = 0;//记录评论中是否包含该特征
            //循环标记
            while (rs.next()) {
                /*文本特征判断*/
                String content = rs.getString("Review_Content");
                content = content.toLowerCase();
                Tree t = (Tree) treemap.get(content);
                //若存在匹配到的表达式
                if (regex.TregexIsMatch(t, str)) {
                    num = 1; //存在该特征记为1
                } else {
                    num = 0;
                }
                /*写入数据库中*/
                String sql = UpdateSql(rs, table_name, col, num);
                stmt2.executeUpdate(sql);
            }
            rs.close();
            stmt.close();
            stmt2.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 标记文本属性特征（依存关系类）
     *
     * @param table_name 表名
     * @param col 列名
     * @param treemap 评论-句法树map
     * @param str 匹配关系式
     */
    public void RemarkDepFeature(String table_name, String col, Map treemap, String str) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();

            rs = stmt.executeQuery("SELECT * FROM " + table_name);

            Tregex regex = new Tregex();
            int num = 0;//记录评论中是否包含该特征
            //循环标记
            while (rs.next()) {
                /*文本特征判断*/
                String content = rs.getString("Review_Content");
                content = content.toLowerCase();
                Tree t = (Tree) treemap.get(content);
                //若存在匹配到的表达式
                if (regex.SemgrexIsMatch(t, str)) {
                    num = 1; //存在该特征记为1
                } else {
                    num = 0;
                }
                /*写入数据库中*/
                String sql = UpdateSql(rs, table_name, col, num);
                stmt2.executeUpdate(sql);
            }
            rs.close();
            stmt.close();
            stmt2.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 标记文本属性特征（句法树类-无效评论）
     *
     * @param table_name 表名
     * @param col 列名
     * @param treemap 评论-句法树map
     */
    public void RemarkInvaildFeature(String table_name, String col, Map treemap) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();

            rs = stmt.executeQuery("SELECT * FROM " + table_name);

            Tregex regex = new Tregex();
            int num = 0;//记录评论中是否包含该特征
            //循环标记
            while (rs.next()) {
                /*文本特征判断*/
                String tempnum = rs.getString("num");
                int wordnum = Integer.parseInt(tempnum); //评论单词数目
                String content = rs.getString("Review_Content");
                content = content.toLowerCase();
                Tree t = (Tree) treemap.get(content);
                int regexnum = regex.TregexInvalid(t); //单词解析成名词的数目
                //若单词均解析为名词或则视为无效评论
                if ((wordnum == regexnum)) {
                    num = 1; //存在该特征记为1
                } else {
                    num = 0;
                }
                /*写入数据库中*/
                String sql = UpdateSql(rs, table_name, col, num);
                stmt2.executeUpdate(sql);
            }
            rs.close();
            stmt.close();
            stmt2.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 标记文本属性特征（单词类）
     *
     * @param table_name 表名
     * @param col 列名
     * @param str 特征字符串
     */
    public void RemarkWordFeature(String table_name, String col, String str) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();

            rs = stmt.executeQuery("SELECT * FROM " + table_name);

            int num = 0;//记录评论中是否包含该特征
            //循环标记
            while (rs.next()) {
                /*文本特征判断*/
                String content = rs.getString("Review_Content");
                content = content.toLowerCase();//转化为小写
                if (content.contains(str)) {
                    num = 1; //存在该特征记为1
                } else {
                    num = 0;
                }
                /*写入数据库中*/
                String sql = UpdateSql(rs, table_name, col, num);
                stmt2.executeUpdate(sql);
            }
            rs.close();
            stmt.close();
            stmt2.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 标记ID
     *
     * @param table_name 表名
     */
    public void RemarkID(String table_name) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt = conn.createStatement();
            stmt.executeQuery("alter table " + table_name + " add ID int identity(1,1)");
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 标记评论类别
     *
     * @param table_name 表名
     * @param content 评论内容
     * @param label 对应的类别
     */
    public void RemarkClasses(String table_name, String content, String label) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            stmt = conn.createStatement();
            content = SqlSingleQuote(content);
            String sql = "UPDATE " + table_name + " SET classes = '" + label + "' where Review_Content = '" + content + "'";
            stmt.executeUpdate(sql);

            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 获取评论单词数目
     *
     * @param str 一条用户评论
     * @return 返回评论单词数目
     */
    public int NumberofWords(String str) {
        int num = 0;
        String s[] = str.split(" |\\.|\\?|,|!|\\\"");
        for (int i = 0; i < s.length; i++) {
            if (s[i].length() == 0) {
                num++;
            }
        }
        num = s.length - num;
        return num;
    }

    /**
     * 标记评论单词总数
     *
     * @param table_name 表名
     * @param col 列名
     */
    public void RemarkNumberofWords(String table_name, String col) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();

            rs = stmt.executeQuery("SELECT * FROM " + table_name);
            //循环标记num
            while (rs.next()) {
                /*统计每条文本的单词数目*/
                int num = this.NumberofWords(rs.getString("Review_Content"));
                /*写入数据库中*/
                String sql = UpdateSql(rs, table_name, col, num);
                stmt2.executeUpdate(sql);
            }
            rs.close();
            stmt.close();
            stmt2.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //可删除
    /**
     * 创建Gephi预测集  
     *
     * @param num gephi预测集数据数量
     */
    public void CreateGephiTest(int num) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();
            Statement stmt3 = conn.createStatement();
            Statement stmt4 = conn.createStatement();
            rs = stmt.executeQuery("select * from sys.tables");
            //预测集表名
            String table_name = "gephi" + num;
            while (rs.next()) {
                /*获取数据库中表的名称*/
                String name = rs.getString("name");
                /*判断是否存在表，若存在则删除*/
                if (name.equals(table_name)) {
                    String sql = "Drop table " + table_name;
                    stmt2.executeUpdate(sql);
                    break;
                }
            }
            String sql = "select * into " + table_name + " from predata order by newid()";
            stmt3.executeUpdate(sql);

            num = 10000 - num;
            sql = "delete from " + table_name + " where ID in ( select top(" + num + ") ID from " + table_name + "  where ID < 10000 order by newid())";
            stmt4.executeUpdate(sql);

            rs.close();
            stmt.close();
            stmt2.close();
            stmt3.close();
            stmt4.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //可删除
    /**
     * 创建预测集
     *
     * @param num 预测集数据数量
     */
    public void CreatePre(int num) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();
            Statement stmt3 = conn.createStatement();
            Statement stmt4 = conn.createStatement();
            Statement stmt5 = conn.createStatement();
            rs = stmt.executeQuery("select * from sys.tables");
            //预测集表名
            String table_name = "test" + num;
            //备份表名
            String cpy = "cpy_" + table_name;

            while (rs.next()) {
                /*获取数据库中表的名称*/
                String name = rs.getString("name");
                /*判断是否存在表，若存在则删除*/
                if (name.equals(table_name)) {
                    String sql = "Drop table " + table_name;
                    stmt2.executeUpdate(sql);
                    sql = "Drop table " + cpy; //同时删除备份表
                    stmt5.executeUpdate(sql);
                    break;
                }
            }
            String sql = "select top(" + num + ")* into " + table_name + " from predata order by newid()";
            stmt3.executeUpdate(sql);
            //备份表
            sql = "select * into " + cpy + " from " + table_name;
            stmt4.executeUpdate(sql);

            rs.close();
            stmt.close();
            stmt2.close();
            stmt3.close();
            stmt4.close();
            stmt5.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //可删除
    /**
     * 创建训练集
     */
    public void CreateTrain() {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();
            Statement stmt3 = conn.createStatement();
            rs = stmt.executeQuery("select * from sys.tables");

            while (rs.next()) {
                /*获取数据库中表的名称*/
                String name = rs.getString("name");
                /*判断是否存在表，若存在则删除*/
                if (name.equals("train")) {
                    String sql = "Drop table train";
                    stmt2.executeUpdate(sql);
                    break;
                }
            }
            String sql = "select * into train from test1213";
            stmt3.executeUpdate(sql);
            rs.close();
            stmt.close();
            stmt2.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * 获取表中数据数量
     *
     * @param table 表名
     * @return 返回表中数据数量
     */
    public int GetDataNum(String table) {
        int num = 0;
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);

            Statement stmt = conn.createStatement();
            ResultSet rs;
            String sql = null;
            String tempcol = "a";
            sql = "select count(*) as " + tempcol + " from " + table;
            rs = stmt.executeQuery(sql);
            rs.next();
            num = rs.getInt(tempcol);

            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return num;
    }

    /**
     * 获取表中字段数量
     *
     * @param table 表名
     * @return 返回表中字段数量
     */
    public int GetColNum(String table) {
        int num = 0;
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);

            Statement stmt = conn.createStatement();
            ResultSet rs;
            String sql = null;
            String tempcol = "a";
            sql = "select count(*) as " + tempcol + " from syscolumns where id = object_id('" + table + "')";
            rs = stmt.executeQuery(sql);
            rs.next();
            num = rs.getInt(tempcol);

            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return num;
    }

    /**
     * 增加列
     *
     * @param table 表名
     * @param col 增加的列名
     * @param type 增加的列类型
     */
    public void AddColumn(String table, String col, String type) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = conn.createStatement();
            ResultSet rs;
            //获取表中所有列名
            rs = stmt.executeQuery("select name from syscolumns where id = object_id('" + table + "')");
            //判断是否存在该列
            int flag = 0;
            while (rs.next()) {
                String name = rs.getString("name");
                if (name.equals(col)) {
                    flag = 1;
                    break;
                }
            }
            //若不存在,则增加该列
            if (flag == 0) {
                String sql = "ALTER TABLE " + table + " ADD " + col + " " + type;
                stmt2.executeUpdate(sql);
            }

            stmt.close();
            stmt2.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 删除列
     *
     * @param table 表名
     * @param col 删除的列名
     */
    public void DelColumn(String table, String col) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);

            Statement stmt = conn.createStatement();
            String sql = null;
            sql = "ALTER TABLE " + table + " drop column " + col;
            stmt.executeUpdate(sql);

            stmt.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 处理不含英文文本的数据
     *
     * @param table
     */
    public void DealNullData(String table) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);

            Statement stmt = conn.createStatement();
            String sql = null;
            sql = "delete from " + table + " where Review_Content not like '%[a-zA-Z]%' or Review_Content not like '%[a-zA-Z]%'";
            stmt.executeUpdate(sql);

            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 删除文本中无用的符号，如"|?|!|.
     *
     * @param table_name
     */
    public void DelInvaSymbol(String table_name) throws ClassNotFoundException {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);

            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement st2 = conn.createStatement();
            String sql = null;
            /*查找含无用符号的记录*/
            sql = "SELECT * FROM " + table_name + " WHERE Review_Content like '%\"%' or "
                    + "Review_Content like '%?%' or Review_Content like '%!%' or Review_Content like '%.%'";
            ResultSet rstemp = st.executeQuery(sql);
            while (rstemp.next()) {
                String old_content = rstemp.getString("Review_Content"); //存储原记录            
                String content_info = rstemp.getString("Review_Content");
                //替换无用符号
                if (content_info.contains("\"")) {
                    content_info = content_info.replace("\"", " ");
                }
                if (content_info.contains("?")) {
                    content_info = content_info.replace("?", " ");
                }
                if (content_info.contains("!")) {
                    content_info = content_info.replace("!", " ");
                }
                if (content_info.contains(".")) {
                    content_info = content_info.replace(".", " ");
                }
                //处理单引号
                old_content = SqlSingleQuote(old_content);
                content_info = SqlSingleQuote(content_info);
                //写入数据库
                sql = "UPDATE " + table_name + " SET Review_Content = '" + content_info + "' WHERE Review_Content = '" + old_content + "'";
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
     * 批量将评论拆分为单句
     *
     * @param table_name
     */
    public void SqltoShort(String table_name) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //  System.out.println("Connection Successful!");
            rs = stmt.executeQuery("SELECT Review_Content FROM " + table_name);
            while (rs.next()) {
                if (rs.getString("Review_Content").contains(".") || rs.getString("Review_Content").contains("!") || rs.getString("Review_Content").contains("?")) {
                    String[] numdot = rs.getString("Review_Content").split("\\.|!|\\?");
                    if (numdot.length > 1) {
                        LongTexttoShort(rs, table_name);
                    } else
                        ;
                } else
                    ;
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 长文本化为单句
     *
     * @param rs
     * @param table_name
     */
    public void LongTexttoShort(ResultSet rs, String table_name) {
        try {
            String old_content = SqlSingleQuote(rs.getString("Review_Content"));//分句之前的长文本处理单引号
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement st2 = conn.createStatement();
            String sql = null;
            /*存储该元组的其他信息*/
            sql = "SELECT * FROM " + table_name + " WHERE Review_Content = '" + old_content + "'";
            ResultSet rstemp = st.executeQuery(sql);
            rstemp.first();
            String[] content_info = new String[5];
            content_info[0] = rstemp.getString("APP_ID");
            content_info[1] = rstemp.getString("Reviewer_Name");
            content_info[2] = rstemp.getString("Rating");
            content_info[3] = rstemp.getString("Review_Title");
            //    content_info[4] = rstemp.getString("Review_Content");
            content_info[4] = rstemp.getString("classes");
            //处理单引号
            for (int i = 0; i < content_info.length; i++) {
                content_info[i] = SqlSingleQuote(content_info[i]);
            }
            /*以句号叹号问号分割评论，并将分割后的评论依次插入数据库中*/
            String[] new_contents = old_content.split("\\.|!|\\?");

            sql = "UPDATE " + table_name + " SET Review_Content = '" + new_contents[0] + "' WHERE Review_Content = '" + old_content + "'";
            st2.executeUpdate(sql);
            for (int i = 1; i < new_contents.length; i++) {
                sql = "INSERT INTO " + table_name + " VALUES ('" + content_info[0] + "', '"
                        + content_info[1] + "','" + content_info[2] + "', '"
                        //    + content_info[3] + "','"+ new_contents[i] +"')";
                        + content_info[3] + "','" + new_contents[i] + "', '"
                        + content_info[4] + "')";
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
     *
     * @param rs
     * @param table_name 表名
     * @param col 列名
     * @param new_value 新值
     * @return 返回一条sql语句
     */
    public String UpdateSql(ResultSet rs, String table_name, String col, Object new_value) {
        String sql = null;
        try {
            String str = SqlSingleQuote(rs.getString("Review_Content"));
            String id = rs.getString("APP_ID");
            sql = "UPDATE " + table_name + " SET " + col + " = '" + new_value + "' where Review_Content = '" + str + "' and APP_ID = '" + id + "'";
        } catch (SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sql;
    }

    /**
     * 处理评论中的单引号，便于sql语句顺利执行
     *
     * @param str 一条未处理的评论
     * @return 返回已处理单引号的评论
     */
    public String SqlSingleQuote(String str) {
        if (str.contains("'")) {
            return str.replace("'", "''");
        } else {
            return str;
        }
    }

    /**
     * bat格式评论文件写入数据库
     *
     * @param readpath 文件路径
     * @param table 数据库表名
     * @param n 属性个数
     * @return
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public String ReviewsToDB(String readpath, String table, int n) throws IOException, ClassNotFoundException, SQLException {
        Class.forName(driverName);
        conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
        Statement stmt = conn.createStatement();
        String sql; //SQL语句      
        //   System.out.println("Connection Successful!"); //如果连接成功 控制台输出Connection Successful!                 
        File readfile = new File(readpath);
        if (!readfile.exists() || readfile.isDirectory()) {
            throw new FileNotFoundException();
        }
        BufferedReader br = new BufferedReader(new FileReader(readfile));
        String temp = null;
        StringBuffer sb = new StringBuffer();
        temp = br.readLine();//按行读入 ,第一行为int，不存在引号等特殊字符
        while (temp != null) {
            if (temp.length() != 0) {
                sql = "INSERT INTO " + table + " VALUES ('" + temp;
                for (int i = 0; i < n - 1; i++) {       //按属性个数读取文件行数插入数值
                    temp = br.readLine();
                    if (!temp.contains("'"))//该行不存在单引号
                    {
                        temp = temp;
                    } else {        //该行存在单引号
                        temp = temp.replace("'", "''");  //将单引号替换成两个，便于在SQL语句中使用
                    }
                    sql += "','" + temp;
                }
                sql += "')";
                //    System.out.println(sql);                 
                stmt.executeUpdate(sql);   //执行SQL语句 
                temp = br.readLine();  //继续读入
            } else //遇到空行继续读入文本
            {
                temp = br.readLine();
            }
        }
        return sb.toString();
    }

    /**
     * bat格式Apps描述文件写入数据库
     *
     * @param readpath
     * @param table
     * @param n
     * @return
     * @throws java.io.IOException
     */
    public String AppsToDB(String readpath, String table, int n) throws IOException, ClassNotFoundException, SQLException {    //文件路径，数据库表名，属性个数
        Class.forName(driverName);
        conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
        Statement stmt = conn.createStatement();
        String sql; //SQL语句     
        //   System.out.println("Connection Successful!"); //如果连接成功 控制台输出Connection Successful!                 
        File readfile = new File(readpath);
        if (!readfile.exists() || readfile.isDirectory()) {
            throw new FileNotFoundException();
        }
        BufferedReader br = new BufferedReader(new FileReader(readfile));
        String temp = null;
        StringBuffer sb = new StringBuffer();
        temp = br.readLine();//按行读入       
        while (temp != null) {
            if (temp.length() != 0) {
                //写SQL语句
                sql = "INSERT INTO " + table + " VALUES ('" + temp;
                for (int i = 0; i < n - 1; i++) {       //按属性个数读取文件行数插入数值
                    temp = br.readLine();
                    sql = sql + "','" + temp;
                }
                sql = sql + "')";
                stmt.executeUpdate(sql);//执行SQL语句
                temp = br.readLine();  //继续读入
            } else //遇到空行继续读入文本
            {
                temp = br.readLine();
            }
        }
        //   System.out.println(sb);
        return sb.toString();
    }

    /**
     * 数据库评论信息写入txt
     *
     * @param table_name 数据库表名
     */
    public void DBtoTXT(String table_name) throws FileNotFoundException {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            rs = stmt.executeQuery("SELECT * FROM " + table_name);

            //文件名称
            File file = new File(".\\data\\" + table_name + ".txt");
            PrintStream ps = new PrintStream(new FileOutputStream(file));

            //统计评论信息数目
            int num = this.GetDataNum(table_name);
            //写入第一行
            ps.println(num);// 往文件里写入字符串
            //写入评论关键信息
            while (rs.next()) {
                //读评论关键信息
                String info = rs.getString("info");
                //拆分为单词字符串
                String[] temp = info.split(" |,");
                for (int i = 0; i < temp.length; i++) {
                    if (temp[i].equals("")) {
                        continue;
                    } else {
                        ps.append(temp[i] + " ");//写入文件中
                    }
                }
                ps.append("\n");
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 数据库评论信息写入txt(删除no_info)
     *
     * @param table_name 数据库表名
     */
    public void InfotoTXT(String table_name) throws FileNotFoundException, IOException {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery("SELECT * FROM " + table_name);

            //文件名称
        //    File file = new File(".\\data\\" + table_name + "noinfo.txt");
            File file = new File(".\\data\\" + table_name + ".txt");
            PrintStream ps = new PrintStream(new FileOutputStream(file));

            //停用词表
            Map<String,Integer> stop = this.Stopwords();
//            //统计评论信息数目
//            int count = 0;
//            while (rs.next()) {
//                //读评论关键信息
//                String info = rs.getString("info");                
//                if (info.equals("no_info")) {
//                    count++;
//                }
//            }
//            int num = this.GetDataNum(table_name) - count;
            int num = this.GetDataNum(table_name);
            //写入第一行
            ps.println(num);// 往文件里写入字符串

            rs.first();
        //    String info = rs.getString("info");
            String info = rs.getString("Review_Content");
        //    if (!info.equals("no_info")) {
                //拆分为单词字符串
                String[] temp = info.split(" |,");
                for (int i = 0; i < temp.length; i++) {
                    if (temp[i].equals("")) {
                        continue;
                    } else {
                        if(stop.get(temp[i]) == null)
                            ps.append(temp[i] + " ");//写入文件中
                    }
                }
                ps.append("\n");
        //    }
            //写入评论关键信息
            while (rs.next()) {
                //读评论关键信息
            //    info = rs.getString("info");
                info = rs.getString("Review_Content");
            //    if (!info.equals("no_info")) {
                    //拆分为单词字符串
                //    String temp = info.split(" |,");
                    temp = info.split(" |,");
                    for (int i = 0; i < temp.length; i++) {
                        if (temp[i].equals("")) {
                            continue;
                        } else {
                            if(stop.get(temp[i]) == null)
                                ps.append(temp[i] + " ");//写入文件中
                        }
                    }
                    ps.append("\n");
             //   }
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map Stopwords () throws FileNotFoundException, IOException{
        File readfile = new File(".\\data\\stopwords.txt");
        if (!readfile.exists() || readfile.isDirectory()) {
            throw new FileNotFoundException();
        }
        BufferedReader br = new BufferedReader(new FileReader(readfile));
        String temp = null;
        temp = br.readLine();//按行读入  
        Map<String,Integer> map = new HashMap();
        while (temp != null) {
            map.put(temp, 1);
            temp = br.readLine();
        }
        return map;
    }
    
    //备份表
    public void Cpytable(String table) {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
            
            Statement stmt = conn.createStatement();
            String sql = null;
            sql = "select * into cpy_"+table+" from " + table;
            stmt.executeUpdate(sql);   
            
            stmt.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //师姐的数据
    public void InfoDB(String readpath, String table) throws IOException, ClassNotFoundException, SQLException {    //文件路径，数据库表名，属性个数
        Class.forName(driverName);
        conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
        Statement stmt = conn.createStatement();
        String sql; //SQL语句      
        //   System.out.println("Connection Successful!"); //如果连接成功 控制台输出Connection Successful!                 
        File readfile = new File(readpath);
        if (!readfile.exists() || readfile.isDirectory()) {
            throw new FileNotFoundException();
        }
        BufferedReader br = new BufferedReader(new FileReader(readfile));
        String temp = null;
        temp = br.readLine();//按行读入 ,第一行为int，不存在引号等特殊字符
        while (temp != null) {
            if (temp.length() != 0) {
                sql = "INSERT INTO " + table + " VALUES ('" + temp;
                for (int i = 0; i < 7 - 1; i++) {       //按属性个数读取文件行数插入数值
                    temp = br.readLine();
                    if (!temp.contains("'"))//该行不存在单引号
                    {
                        temp = temp;
                    } else {        //该行存在单引号
                        temp = temp.replace("'", "''");  //将单引号替换成两个，便于在SQL语句中使用
                    }
                    sql += "','" + temp;
                }
                sql += "')";
                //    System.out.println(sql);                 
                stmt.executeUpdate(sql);   //执行SQL语句 
                temp = br.readLine();  //继续读入
            } else //遇到空行继续读入文本
            {
                temp = br.readLine();
            }
        }
    }
    
    public void DBInfo(String table_name) throws FileNotFoundException {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);  //连接数据库
            Statement stmt;
            ResultSet rs;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            rs = stmt.executeQuery("select * from "+table_name+" left join Apps on "+table_name+".APP_ID = Apps.APP_ID");

            //文件名称
            File file = new File(".\\data\\"+table_name+"data.txt");
            PrintStream ps = new PrintStream(new FileOutputStream(file));

            Standfordnlp s = new Standfordnlp();
        
            //写入评论关键信息
            while (rs.next()) {
                //读评论关键信息
                String info = rs.getString("info");
                if(!info.equals("no_info")){
                    String APP_ID = rs.getString("APP_ID");
                    String APP_Name_ = rs.getString("APP_Name_");
                    String APP_category = rs.getString("APP_category");
                    String Reviewer_Name = rs.getString("Reviewer_Name");
                    String Rating = rs.getString("Rating");
                    String Review_Content = rs.getString("Review_Content");
                    String sentiment = s.Sentiment(Review_Content);
                
                    ps.append(APP_ID);//写入文件中
                    ps.append("\n");
                    ps.append(APP_Name_);//写入文件中
                    ps.append("\n");
                    ps.append(APP_category);//写入文件中
                    ps.append("\n");
                    ps.append(Reviewer_Name);//写入文件中
                    ps.append("\n");
                    ps.append(Rating);//写入文件中
                    ps.append("\n");
                    ps.append(info);//写入文件中
                    ps.append("\n");
                    ps.append(sentiment);//写入文件中
                    ps.append("\n");
                    ps.append("\n");
                }
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Standfordnlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
