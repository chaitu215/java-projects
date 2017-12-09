package evan.wang.spark.mongodb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.bson.Document;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;

/**
 * spark-submit --class evan.wang.spark.mongodb.TestSparkSql_1 spark-job.jar
 * @author: wangsy
 * @date: 2017年3月10日
 */
public class TestSparkSql_1 {
	public static void main(String[] args) throws Exception{
		// spark context
		JavaSparkContext jsc = SparkMongoUtil.getJavaSparkContext();

		// 设置读相关配置
		Map<String, String> readOverrides = new HashMap<String, String>();
		readOverrides.put("database", "shake");
		readOverrides.put("collection", "pflm_menu");
		ReadConfig readConfig = ReadConfig.create(jsc).withOptions(readOverrides);

		// spark sql
		SQLContext sqlContext = SQLContext.getOrCreate(jsc.sc());
		JavaMongoRDD<Document>  df = MongoSpark.load(jsc, readConfig);
		//df.registerTempTable("pflm_menu");
		//String sql = "select date_time, 0, original_id, subscribe from fans_foot where original_id = 'gh_cf3603d36721' ";
		String sql = "select _id,type,pid,name,seq,url,key,des,cname from `pflm_menu`";
		Dataset<Row> result = sqlContext.sql(sql);
		System.out.println("result-------------------");
		result.printSchema();
		//result.show();
		
		//过滤
		//result = result.filter(result.col("subscribe").equalTo(0));
		
		String[] columns = result.columns();
		List<Row> rows = result.takeAsList(100);
		
		//写相关配置
/*		Map<String, String> writeOverrides = new HashMap<String, String>();
		writeOverrides.put("database", "shake");
		writeOverrides.put("collection", "myNewColl");
		WriteConfig writeConfig = WriteConfig.create(jsc).withOptions(writeOverrides);
		MongoSpark.save(result, writeConfig);*/
		
		//MongoSpark.write(result).option("collection", "myNewColl").mode("overwrite").save();
        
		jsc.close();
	}
}
