package evan.wang;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;

/**   
 * @author: wangshengyong
 * @date: 2016年8月16日
 * @description: 
 */
public class MongoMapReduce {
	private MongoClient client;
	private MongoDatabase db;
	private MongoCollection<Document> collection;

	@Before
	public void testConn() {
		client = LocalMongoClient.getMongoClient();
		// 获取数据库
		db = client.getDatabase("test");
		// 获取集合
		collection = db.getCollection("restaurants");
	}

	@After
	public void testClose() {
		if (client != null) {
			client.close();
		}
		db = null;
	}
	
	/**
//查询集合存在哪些键,以及对应数量
//map函数
var map = function () {
    for(var key in this){
       emit(key, {count : 1});
    }
}
//reduce函数
var reduce = function (key, values) {
    var total = 0;
    for(var i in values){
      total += values[i].count;
    }
    return {"count" : total};
}
//调用mapReduce, 结果保存到新生成countTotal集合
var res =  db.restaurants.mapReduce(map,reduce,{out: "countTotal"});
//查询集合数据
db.countTotal.find();
	 */
	@Test
	public void listCollectionKey(){
		long startTime = System.currentTimeMillis();
		String mapFunction = "function () {for(var key in this){ emit(key, {count : 1}); }}";
		String reduceFunction = "function (key, values) { var total = 0; for(var i in values){ total += values[i].count; } return {'count' : total};}";
		MapReduceIterable<Document> iterable = collection.mapReduce(mapFunction, reduceFunction).filter(eq("cuisine","American "));
		System.out.println("11111111");
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(Document t) {
				System.out.println(t);
			}
		});
		long endTime = System.currentTimeMillis();
		System.out.println("操作耗时: " + (endTime - startTime) + "ms");
	}
	
	
	

}
