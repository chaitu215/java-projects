package evan.wang.spark.mongodb;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * 
 * @author: wangsy
 * @date: 2017年3月10日
 */
public class SparkMongoUtil {

	public static SparkConf getSparkConf() {
/*		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("MongoSparkConnectorIntro")
				.set("spark.mongodb.input.uri", "mongodb://system_admin:123456@192.168.1.22/admin.one?readPreference=primaryPreferred")
				.set("spark.mongodb.output.uri", "mongodb://system_admin:123456@192.168.1.22/admin.one");*/
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("MongoSparkConnectorIntro")
				.set("spark.mongodb.input.uri","mongodb://pflm:123456@192.168.1.22/shake.one?readPreference=primaryPreferred")
				.set("spark.mongodb.output.uri", "mongodb://pflm:123456@192.168.1.22/shake.one");
		return conf;
	}

	public static JavaSparkContext getJavaSparkContext() {
		return new JavaSparkContext(getSparkConf());
	}

}
