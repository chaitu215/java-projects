package evan.wang.spark.mongodb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;

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
		readOverrides.put("collection", "fans_foot");
		ReadConfig readConfig = ReadConfig.create(jsc).withOptions(readOverrides);

		// spark sql
		SQLContext sqlContext = SQLContext.getOrCreate(jsc.sc());
		DataFrame df = MongoSpark.load(jsc, readConfig).toDF();
		df.registerTempTable("qrcode_subscribe");
		String sql = "select date_time, 0, original_id, subscribe from fans_foot where original_id = 'gh_cf3603d36721' ";
		//String sql = "select max(precision) as table_n_0 from qrcode_location";
		DataFrame result = sqlContext.sql(sql);
		System.out.println("result-------------------");
		result.printSchema();
		//result.show();
		
		//过滤
		result = result.filter(result.col("subscribe").equalTo(0));
		
		String[] columns = result.columns();
		List<Row> rows = result.takeAsList(100);
		JSONArray jsonArray = new JSONArray();
		for (Row row : rows) {
			JSONObject object = new JSONObject();
			for (String name : columns) {
				object.put(name, row.getAs(name));
			}
			jsonArray.put(object);
		}
		System.out.println("rows-------------------"+ jsonArray);
		
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
