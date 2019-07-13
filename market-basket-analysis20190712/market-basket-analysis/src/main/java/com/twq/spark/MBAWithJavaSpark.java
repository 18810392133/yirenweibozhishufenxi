package com.twq.spark;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.twq.util.Combination;
import com.twq.util.Utils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;
import scala.Tuple4;

/**
 * 使用Spark Core 实现 Market Basket Analysis
 */
public class MBAWithJavaSpark {

    public static void main(String[] args) throws Exception {
        final double minSupport = 0.00;//
        final double minConfidence = 0.0;//某个规则的频率
        String delimiter = ",";
        String transactionsFileName = "C:\\Users\\25417\\Desktop\\market-basket-analysis20190712\\market-basket-analysis\\dataset\\all_pinglun_text-seg-result.csv";
        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("Market-Basket-Analysis");
        sparkConf.setMaster("local[*]");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

        JavaRDD<String> transactions = sparkContext.textFile(transactionsFileName, 1);

        long txnCount = transactions.count();
        double minSupportCount = txnCount * minSupport;

        // 生成了频繁项集
        JavaPairRDD<List<String>, Integer> itemSetCountRDD =
                transactions.flatMapToPair(new PairFlatMapFunction<String, List<String>, Integer>() {
                    @Override
                    public Iterator<Tuple2<List<String>, Integer>> call(String transaction) {
                        List<String> items = Utils.toList(transaction, delimiter);
                        List<List<String>> subItemSets = Combination.findSortedCombinations(items);
                        List<Tuple2<List<String>, Integer>> result = new ArrayList<Tuple2<List<String>, Integer>>();
                        for (List<String> subItemSet : subItemSets) {
                            if (subItemSet.size() > 0) {
                                result.add(new Tuple2<List<String>, Integer>(subItemSet, 1));
                            }
                        }
                        return result.iterator();
                    }
                });

        JavaPairRDD<List<String>, Integer> frequentItemSetRDD =
                itemSetCountRDD.reduceByKey(new Function2<Integer, Integer, Integer>() {
            public Integer call(Integer i1, Integer i2) {
                return i1 + i2;
            }
        }).filter(new Function<Tuple2<List<String>, Integer>, Boolean>() {
            @Override
            public Boolean call(Tuple2<List<String>, Integer> v1) throws Exception {
                return v1._2() >= minSupportCount;
            }
        });
        // 根据频繁项集生成关联规则
        JavaPairRDD<List<String>, Tuple2<List<String>, Integer>> subItemSetRDD = frequentItemSetRDD.flatMapToPair(
                new PairFlatMapFunction<Tuple2<List<String>, Integer>, List<String>, Tuple2<List<String>, Integer>>() {
                    @Override
                    public Iterator<Tuple2<List<String>, Tuple2<List<String>, Integer>>> call(
                            Tuple2<List<String>, Integer> frequentItemSet) {
                        List<Tuple2<List<String>, Tuple2<List<String>, Integer>>> result = new ArrayList<Tuple2<List<String>, Tuple2<List<String>, Integer>>>();
                        List<String> itemSet = frequentItemSet._1;
                        Integer itemSetSupportCount = frequentItemSet._2;
                        result.add(new Tuple2(itemSet, new Tuple2(null, itemSetSupportCount))); // 主要是为了后面区分开，好拿到父集出现的次数
                        if (itemSet.size() == 1) {
                            return result.iterator();
                        }
                        List<List<String>> allSubItemSets = Combination.findSortedCombinations(itemSet);
                        allSubItemSets.remove(0);
                        allSubItemSets.remove(allSubItemSets.size() - 1);
                        for(List<String> subItemSet : allSubItemSets) {
                            result.add(new Tuple2<List<String>, Tuple2<List<String>, Integer>>(subItemSet, new Tuple2(itemSet, itemSetSupportCount)));
                        }
                        return result.iterator();
                    }
                });

        JavaPairRDD<List<String>, Iterable<Tuple2<List<String>, Integer>>> rules = subItemSetRDD.groupByKey();
        JavaRDD<String> associationRulesRDD =
                rules.flatMap(new FlatMapFunction<Tuple2<List<String>, Iterable<Tuple2<List<String>, Integer>>>, String>() {
            @Override
            public Iterator<String> call(Tuple2<List<String>, Iterable<Tuple2<List<String>, Integer>>> rule) throws Exception {
                List<String> result = new ArrayList<String>();
                List<String> antecedent = rule._1;
                Iterable<Tuple2<List<String>, Integer>> inSuperItemSets = rule._2;
                List<Tuple2<List<String>, Integer>> superItemSets = new ArrayList<Tuple2<List<String>, Integer>>();
                Integer antecedentSupportCount = 0;
                for (Tuple2<List<String>, Integer> t2 : inSuperItemSets) {
                    // find the "count" object
                    if (t2._1 == null) {
                        antecedentSupportCount = t2._2;
                    } else {
                        superItemSets.add(t2);
                    }
                }
                if (superItemSets.isEmpty()) {
                    return result.iterator();
                }
                for (Tuple2<List<String>, Integer> t2 : superItemSets) {
                    // 计算 后件 = superItemSet - 前件
                    List<String> consequent = new ArrayList<String>(t2._1);
                    consequent.removeAll(antecedent);
                    double ruleSupport = (double) t2._2 / (double)txnCount;
                    double confidence =  (double) t2._2 / (double)antecedentSupportCount;
                    if (confidence >= minConfidence && !antecedent.isEmpty() && !consequent.isEmpty()) {
                        result.add("(" + antecedent + "=>" + consequent + "," + ruleSupport + "," + confidence + ")");
                        System.out.println("(" + antecedent + "=>" + consequent + "," + ruleSupport + "," + confidence + ")");
                    }
                }
                return result.iterator();
            }
        });

        FileSystem.get(sparkContext.hadoopConfiguration())
                .delete(new Path("C:\\Users\\25417\\Desktop\\market-basket-analysis20190712\\market-basket-analysis\\output-java\\association_rules_with_conf\\part-00000"), true);
        associationRulesRDD.saveAsTextFile("C:\\Users\\25417\\Desktop\\market-basket-analysis20190712\\market-basket-analysis\\output-java\\association_rules_with_conf\\part-00000");
        sparkContext.stop();
    }
}
