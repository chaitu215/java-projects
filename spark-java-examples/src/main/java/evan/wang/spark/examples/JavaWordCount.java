/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package evan.wang.spark.examples;

import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 单词统计
 *
 * @author: wangshengyong
 * @date: 2016年11月4日
 */
public final class JavaWordCount {
    private static final Pattern SPACE = Pattern.compile(" ");


    /**
     * 参数： 输入统计单词的文件路径， 输出结果的文件路径(需要不存在)
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("please set input file");
            System.exit(1);
        }
        if (args.length < 2) {
            System.err.println("please set output file");
            System.exit(1);
        }

        //删除输出目录
        File outputDir = new File(args[1]);
        FileUtils.forceDeleteOnExit(outputDir);

        //运行在spark-submit任务中
        //SparkConf sparkConf = new SparkConf().setAppName("JavaWordCount")

        //运行在驱动程序中,测试
        SparkConf sparkConf = new SparkConf()
                .setAppName("JavaWordCount")
                .setMaster("local[2]")
                .set("spark.testing.memory", String.valueOf(768 * 1024 * 1024)); //768m
        JavaSparkContext ctx = new JavaSparkContext(sparkConf);
        JavaRDD<String> lines = ctx.textFile(args[0], 1);
        JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterable<String> call(String s) {
                return Arrays.asList(SPACE.split(s));
            }
        });
        JavaPairRDD<String, Integer> ones = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) {
                return new Tuple2<>(s, 1);
            }
        });
        JavaPairRDD<String, Integer> counts = ones.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer i1, Integer i2) {
                return i1 + i2;
            }
        });
        counts.saveAsTextFile(args[1]);
        List<Tuple2<String, Integer>> output = counts.collect();
        for (Tuple2<?, ?> tuple : output) {
            System.out.println(tuple._1() + ": " + tuple._2());
        }
        ctx.stop();
    }
}