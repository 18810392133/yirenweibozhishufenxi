package com.twq.spark

import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

object MBAWithScalaSpark {
  def main(args: Array[String]): Unit = {
    val input = "dataset/test/test.csv"
    val output = "output-scala/association_rules_with_conf"
    val minSupport = 0.00
    val minConfidence = 0.00

    val sparkConf = new SparkConf().setAppName("market-basket-analysis").setMaster("local")
    val sc = new SparkContext(sparkConf)

    val transactions = sc.textFile(input)

    val txnCount = transactions.count()
    val minSupportCount = minSupport * txnCount

    val itemSetCountRDD = transactions.flatMap(line => {
      val items = line.split(",").toList
      (0 to items.size) flatMap items.combinations filter (xs => !xs.isEmpty)
      /*
      上句话等价于：
        val list = ListBuffer.empty[List[String]]
        for(i <- 0 to items.size){
          list.++= (items.combinations(i).toBuffer)
        }
        list.toList.filter(xs => !xs.isEmpty)
       */
    }).map((_, 1))
    val frequentItemSetRDD = itemSetCountRDD.reduceByKey(_ + _).filter(_._2 >= minSupportCount)
    val subItemSetRDD = frequentItemSetRDD.flatMap { case (frequentItemSet, supportCount) =>
      val result = ListBuffer.empty[(List[String], (List[String], Int))]
      result += ((frequentItemSet, (Nil, supportCount)))
      //即把K作为K2，Tuple(null,V))作为V2
      val sublist =
        (0 to frequentItemSet.size - 1) flatMap frequentItemSet.combinations filter (xs => !xs.isEmpty) map((_, (frequentItemSet, supportCount)))
      result ++= sublist
      result.toList
    }
    val rules = subItemSetRDD.groupByKey()
    val associationRulesRDD = rules.flatMap { case (antecedent, allSuperItemSets) =>
      val antecedentSupportCount = allSuperItemSets.find(p => p._1 == Nil).get._2
      val superItemSets = allSuperItemSets.filter(p => p._1 != Nil).toList //将规则集合去掉空的
      if (superItemSets.isEmpty) Nil
      else {
        val result =
          for {
            (superItemSet, superItemSetSupportCount) <- superItemSets
            consequent = superItemSet diff antecedent
            ruleSupport = superItemSetSupportCount.toDouble / txnCount.toDouble
            confidence = superItemSetSupportCount.toDouble / antecedentSupportCount.toDouble if confidence >= minConfidence
          } yield (((antecedent, consequent, ruleSupport, confidence)))
        result
      }
    }
    val formatResult = associationRulesRDD.map(s => {
      (s._1.mkString("[", ",", "]"), s._2.mkString("[", ",", "]"), s._3, s._4)
    })

    FileSystem.get(sc.hadoopConfiguration).delete(new Path(output), true)
    formatResult.saveAsTextFile(output)
    sc.stop()
  }
}
