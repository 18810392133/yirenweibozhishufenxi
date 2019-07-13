package com.twq.spark

import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

object MBAWithScalaSparkBak {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("market-basket-analysis").setMaster("local")
    val sc = new SparkContext(sparkConf)
    val input = "dataset/test/test.csv"
    val output = "output-scala/association_rules_with_conf"
    val transactions = sc.textFile(input)

    val txnCount = transactions.count()

    val patterns = transactions.flatMap(line => {
      val items = line.split(",").toList
      // Converting to List is required because Spark doesn't partition on Array (as returned by split method)
      (0 to items.size) flatMap items.combinations filter (xs => !xs.isEmpty)
      /*
        combinations(n: Int): Iterator[List[A]] 取列表中的n个元素进行组合，返回不重复的组合列表，结果一个迭代器
       */
      /*
      * 上句话等价于：
      * val list = ListBuffer.empty[List[String]]
        for(i <- 0 to items.size){
          list.++= (items.combinations(i).toBuffer)
        }
        list.toList.filter(xs => !xs.isEmpty)
      * 即对a,b,c
      * 先取0个元素进行组合，得到不重复的组合列表[]，加入list中，list为[[]]
      * 再取1个元素进行组合，得到不重复的组合列表[[a],[b],[c]]，加入list中，list为[[a],[b],[c]]
      * 再取2个元素进行组合，得到不重复的组合列表[[a,b],[a,c],[b,c]]，加入list中,list为[[],[a],[b],[c],[a,b],[a,c],[b,c]]
      * 再取3个元素进行组合，得到不重复的组合列表[[a,b,c]]，加入list中,list为[[],[a],[b],[c],[a,b],[a,c],[b,c],[a,b,c]]
      * 然后对其进行过滤，去掉其中为空的列表
      * list为[[a],[b],[c],[a,b],[a,c],[b,c],[a,b,c]]
      * 最后回到外层的flatMap，会将列表的列表拍扁成列表：
      * [a],[b],[c],[a,b],[a,c],[b,c],[a,b,c]
      * */
    }).map((_, 1))
    //到最外面的map，将列表映射为(列表，1)的键值对

    val combined = patterns.reduceByKey(_ + _)//合并key值相同的键值对

    val subpatterns = combined.flatMap(pattern => {
      //pattern:(List(a, b, c),1)
      val result = ListBuffer.empty[Tuple2[List[String], Tuple2[List[String], Int]]]
      result += ((pattern._1, (Nil, pattern._2)))//即把K作为K2，Tuple(null,V))作为V2

      val sublist = for {
        i <- 0 until pattern._1.size
        xs = pattern._1.take(i) ++ pattern._1.drop(i + 1)
        if xs.size > 0
      } yield (xs, (pattern._1, pattern._2))
      //上段代码等价于：
      /*
      for(i <- 0 to pattern._1.size){
        val sublist = pattern._1.take(i) ++ pattern._1.drop(i + 1)
        if(sublist.size > 0)
          result += new Tuple2(sublist,new Tuple2(pattern._1,pattern._2))
      }
      即每次去掉一个元素，将剩下的元素集合作为K2
      */
      result ++= sublist
      result.toList
    })

    val rules = subpatterns.groupByKey()

    val assocRules = rules.map(in => {
      println("in=" + in)
      //in:(List(b),CompactBuffer((List(),4), (List(b, d),1), (List(a, b),2), (List(b, c),3)))
      val fromCount = in._2.find(p => p._1 == Nil).get//找到[b]的frequency：即(List(),4)
      println("fromCount=" + fromCount)
      val toList = in._2.filter(p => p._1 != Nil).toList//将规则集合去掉空的
      println("toList=" + toList)
      //toList:CompactBuffer((List(b, d),1), (List(a, b),2), (List(b, c),3))
      if (toList.isEmpty) Nil
      else {
        val result =
          for {
            t2 <- toList
            ruleSupport = t2._2.toDouble / txnCount.toDouble
            confidence = ruleSupport / fromCount._2.toDouble
            difference = t2._1 diff in._1
            //diff(that: collection.Seq[A]): List[A] 保存列表中那些不在另外一个列表中的元素，即从集合中减去与另外一个集合的交集
          } yield (((in._1, difference, ruleSupport, confidence)))
        result
      }
      //等价于
      /*if (toList.isEmpty) Nil
      else {
        val result = ListBuffer.empty[Tuple3[List[String],List[String],Double]]
        for(t2 <- toList){
          println("t2=" + t2)
          //t2:(List(b, d),1)
          val confidence = t2._2.toDouble / fromCount._2.toDouble
          val difference = t2._1 diff in._1
          println(Tuple3(in._1, difference, confidence))
          result.+=(Tuple3(in._1, difference, confidence))
        }
        result
      }*/
    })

    val formatResult = assocRules.flatMap(f => {
      f.map(s => (s._1.mkString("[", ",", "]"), s._2.mkString("[", ",", "]"), s._3, s._4))
    })
    /*
    * ([b],[d],0.25)
      ([b],[a],0.5)
      ([b],[c],0.75)
      ([a,b],[c],0.5)
      ([a,b],[d],0.5)
      ([b,d],[a],1.0)
      ([a],[b],1.0)
      ([a],[d],0.5)
      ([a],[c],0.5)
      ([a,d],[b],1.0)
      ([b,c],[a],0.3333333333333333)
      ([a,c],[b],1.0)
      ([c],[b],1.0)
      ([c],[a],0.3333333333333333)
      ([d],[b],1.0)
      ([d],[a],1.0)
    * */
    FileSystem.get(sc.hadoopConfiguration).delete(new Path(output), true)
    formatResult.saveAsTextFile(output)
    sc.stop()
  }
}
