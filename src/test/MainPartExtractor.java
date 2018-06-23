/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author dell-pc
 */
class MainPart {

    String subject = "";//主语    
    String predicate = "";//谓语
    String object = ""; //宾语

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getObject() {
        return predicate;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String mainpartinfo() {
        return subject + " " + predicate + " " + object;
    }
}

public class MainPartExtractor {

    public Tree tree = null;
    public List list = new ArrayList();
    public String center = null;

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    /**
     * 利用Standfordnlp分析用户评论,得到语法树、依存关系列表、中心词
     *
     * @param str 一条用户评论
     */
    public void NLP(String str) {
        Properties props = new Properties();
        props.setProperty("ner.useSUTime", "false");
        //分词（tokenize）、分句（ssplit）、词性标注（pos）、词形还原（lemma）、
        //命名实体识别（ner）、语法解析（parse）、情感分析（sentiment）、指代消解（coreference resolution）
        props.put("annotators", "tokenize, ssplit,pos, lemma, ner, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(str);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            list = dependencies.edgeListSorted(); //依存关系list
            center = dependencies.getFirstRoot().word();
            
            System.out.println("有逗号");
            tree.pennPrint();
            System.out.println(dependencies.toList());
            System.out.println(center);
            break;
        }
    }

    //不含逗号的主干提取
    public String getmainpart() {
        MainPart mainpart = new MainPart();
        //若只有NP，则认为整个句子为主语
        if (tree.firstChild().label().value().equals("NP")) {
            List l = tree.getLeaves();
            for (int i = 0; i < l.size(); i++) {
                String word = l.get(i).toString();
                mainpart.subject += word + " ";
            }
        } //否则找到中心词对应的依存关系
        else {
            for (int i = 0; i < list.size(); i++) {
                //解析每个依存关系的单词和关系名称
                SemanticGraphEdge s = (SemanticGraphEdge) list.get(i);
                String relation = s.getRelation().toString();//评论dependent与governor关系
                String dep = s.getDependent().word(); //评论dependent单词
                String gov = s.getGovernor().word();

                if (center.equals(gov)) { //若找到中心词，判断关系名称，找到主谓宾
                    switch (relation) {
                        case "nsubj":
                            mainpart.subject = dep;
                            mainpart.object = gov;
                            break;
                        case "ccomp":
                        case "advcl":
                        case "dobj":
                            mainpart.predicate = gov;
                            mainpart.object = dep;
                            break;
                        case "xcomp":
                            mainpart.predicate = gov;
                            break;
                        case "aux":
                            mainpart.predicate = dep;
                            break;
                        case "neg":
                            if (dep.equals("n't")) {
                                mainpart.predicate += dep;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            //补全主语 宾语
            for (int i = 0; i < list.size(); i++) {
                //解析每个依存关系的单词和关系名称
                SemanticGraphEdge s = (SemanticGraphEdge) list.get(i);
                String relation = s.getRelation().toString();//评论dependent与governor关系
                String dep = s.getDependent().word(); //评论dependent单词
                String gov = s.getGovernor().word();
                if (mainpart.subject.equals(gov)) {
                    switch (relation) {
                        case "amod":
                        case "compound":
                            mainpart.subject = dep + " " + mainpart.subject;
                            break;
                        default:
                            break;
                    }
                }
                if (mainpart.object.equals(gov)) {
                    switch (relation) {
                        case "compound":
                            mainpart.object = dep + " " + mainpart.object;
                            break;
                        case "dobj":
                            mainpart.object = mainpart.object + " " + dep;
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        String maininfo = mainpart.mainpartinfo();
        System.out.println("无逗号 " + mainpart.mainpartinfo());
        return maininfo;
    }

    //含逗号的主干提取
    public String getmainpart2(String str) {
        String[] temp = str.split(",");
        this.NLP(temp[0]);
        //逗号之前的半句
        String pre = this.getmainpart();
        //提取逗号后的半句
        this.NLP(temp[1]);
        String post = this.getmainpart();
        String maininfo = pre + "," + post;
        System.out.println(pre + "," + post);
        return maininfo;
    }

}
