package evan.wang;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**   
 * @author: wangshengyong
 * @date: 2016年8月15日
 * @description: 
 */
public class MongoCreateIndex {
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
	
	@Test
	public void createIndex1(){
		String indexName = collection.createIndex(new Document("cuisine", 1));
		System.out.println(indexName);
		indexName = collection.createIndex(new Document("cuisine", 1).append("address.zipcode", -1));
		System.out.println(indexName);
	}
	
	
	
}
