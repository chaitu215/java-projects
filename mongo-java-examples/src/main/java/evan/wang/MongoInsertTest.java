package evan.wang;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

/**
 * 教程：https://docs.mongodb.com/getting-started/java/import-data/
 * 导入示例程序：D:\MongoDB\mongodb-3.2.7\bin>mongoimport --db test --collection
 * restaurants --drop --file D:\MongoDB\primer-dataset.json
 *
 */
public class MongoInsertTest {
	private MongoClient client;
	private MongoDatabase db;
	private MongoCollection<Document> collection;
	private long startTime;
	private long endTime;

	@Before
	public void testConn() {
		startTime = System.currentTimeMillis();
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
	 * 插入测试
	 * 
	 * @throws Exception
	 */
	@Test
	public void insertOne() throws Exception {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
		Document document = new Document("address",
				new Document().append("street", "shen nan ").append("zipcode", "10075").append("building", "888888")
						.append("coord",
								asList(-73.9557413, 40.7720266)))
										.append("borough",
												"Manhattan")
										.append("cuisine", "Italian")
										.append("grades",
												asList(new Document()
														.append("date", format.parse("2014-10-01T00:00:00Z")).append(
																"grade", "A")
														.append("score", 11),
														new Document()
																.append("date", format.parse("2014-01-16T00:00:00Z"))
																.append("grade", "B").append("score", 17)))
										.append("name", "haha").append("restaurant_id", "41704620");

		// 集合中插入一个文档数据
		collection.insertOne(document);
		System.out.println("插入成功!");
	}

	
	@Test
    public void testJsonstrToDb(){
    	String json = "{'name' : 'xxxxx门店', 'address' : { 'province' : '广东', 'city' : '深圳', 'town' : '宝安', 'detail' : 'xxx街道xx栋xx号'}}";
    	Document  object = Document.parse(json);
    	object.put("_id", new ObjectId().toString());
    	collection = db.getCollection("json");
    	collection.insertOne(object);
    }
	

	private Object asList(Object... d) {
		return Arrays.asList(d);
	}



}
