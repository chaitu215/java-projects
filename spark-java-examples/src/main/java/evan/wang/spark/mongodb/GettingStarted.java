package evan.wang.spark.mongodb;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.bson.Document;
import scala.Tuple2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @auth evan
 * @date 2017/2/15 11:18
 */
public class GettingStarted {

    public static void main(final String[] args) throws InterruptedException {

        SparkConf conf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("MongoSparkConnectorIntro")
                .set("spark.mongodb.input.uri", "mongodb://192.168.1.22/shake.qrcode_subscribe")
                .set("spark.mongodb.output.uri", "mongodb://192.168.1.22/shake.qrcode_subscribe");

        // Create a JavaSparkContext using the SparkSession's SparkContext object
        JavaSparkContext jsc = new JavaSparkContext(conf);

        //设置读相关配置
        Map<String, String> readOverrides = new HashMap<String, String>();
        readOverrides.put("collection", "qrcode_location");
        readOverrides.put("readPreference.name", "secondaryPreferred");
        ReadConfig readConfig = ReadConfig.create(jsc).withOptions(readOverrides);
        JavaRDD<Document> rdd = MongoSpark.load(jsc, readConfig);
        System.out.println("total count ---------------" + rdd.count());
        System.out.println("first data  ---------------" + rdd.first().toJson());
        System.out.println("take 10 -------------------" + rdd.take(10));
        JavaPairRDD<String, Double> pairRdd = rdd.mapToPair(new PairFunction<Document, String, Double>() {
            @Override
            public Tuple2<String, Double> call(Document document) throws Exception {
                return new Tuple2<String, Double>(document.getString("_id"), Double.parseDouble(String.valueOf(document.get("precision"))));
            }
        });
        JavaPairRDD<String, Double> maxPairRdd = pairRdd.reduceByKey(
                /**
                 * <Double, Double, Double> 相同key对应值1， 相同key对应值2， key对应返回值
                 */
                new Function2<Double, Double, Double>() {
                    @Override
                    public Double call(Double v1, Double v2) throws Exception {
                        return v1 > v2 ? v1 : v2;
                    }
                });
        //获取结果
        List<Tuple2<String, Double>> output = maxPairRdd.collect();
        //输出结果
        for (Tuple2<?, ?> tuple : output) {
            System.out.println(tuple._1() + ": " + tuple._2());
        }

        //spark sql
        SQLContext sqlContext = SQLContext.getOrCreate(jsc.sc());
        DataFrame df = MongoSpark.load(jsc).toDF();
        df.registerTempTable("qrcode_subscribe");
        DataFrame result = sqlContext.sql("select date_time, openid, original_id, qrCode_id, subscribe from" +
                " qrcode_subscribe where original_id = 'gh_cf3603d36721' limit 10");
        System.out.println("result-------------------");
        result.printSchema();
        result.show();

        jsc.close();
    }


}
