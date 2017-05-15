package evan.wang;

import static com.mongodb.client.model.Filters.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * 教程：https://docs.mongodb.com/getting-started/java/import-data/
 * 导入示例程序：D:\MongoDB\mongodb-3.2.7\bin>mongoimport --db test --collection
 * restaurants --drop --file D:\MongoDB\primer-dataset.json
 *
 */
public class MongoQueryTest {
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
	public void testQuery() {
		FindIterable<Document> iterable = db.getCollection("restaurants")
				.find(new Document("borough", "Manhattan")).limit(10);
		printResultSet(iterable);
	}

	/**
	 * > 条件查询
	 */
	@Test
	public void testQueryGt() {
/*		FindIterable<Document> iterable = db.getCollection("restaurants")
				.find(new Document("grades.score", new Document("$gt", 30)));*/
		//等价于使用Filters
		FindIterable<Document> iterable = collection.find(gt("grades.score", 30)).limit(10);
		printResultSet(iterable);
	}

	/**
	 * and添加查询
	 * 
	 * {"cuisine":"Italian","address.zipcode":"10075"}
	 */
	// @Test
	public void testQueryAnd() {
		FindIterable<Document> iterable = collection
				.find(new Document("cuisine", "Italian").append("address.zipcode", "10075"));
		printResultSet(iterable);
	}

	/**
	 * or条件查询 {"$or": [{"cuisine":"Italian"},{"address.zipcode":"10075"}]}
	 */
	@Test
	public void testQueryOr() {
		FindIterable<Document> iterable = collection.find(new Document("$or",
				asList(new Document("cuisine", "Italian"), new Document("address.zipcode", "10075"))));
		printResultSet(iterable);
	}

	/**
	 * sort排序
	 */
	@Test
	public void testQuerySort() {
		FindIterable<Document> iterable = collection.find()
				.sort(new Document("borough", 1).append("address.zipcode", 1));
		printResultSet(iterable);
	}
	
	/**
	 * count计数
	 */
	@Test
	public void testCount(){
		long count = collection.count();
		System.out.println(count);
	}
	
	/**
	 * 对集合查询
	 */
	@Test
	public void queryCollection(){
		//查询有两个评论的数据
		//FindIterable<Document> iterable = collection.find(size("grades", 2));
		
		//查询评论数大于2的数据
		//FindIterable<Document> iterable = collection.find(exists("grades.1", true));
		
		//查询评论数小于5的数据
		//FindIterable<Document> iterable = collection.find(exists("grades.5", false));
		
		//查询有2-5个评论数的数据,包含2个和5个
		FindIterable<Document> iterable = collection.find(
				and(exists("grades.1", true),exists("grades.5", false)));
		
		printResultSet(iterable);
	}
	
	
	/**
	 * 查询数组不为null
	 */
	@Test
	public void notNull() {
		client = new MongoClient(new ServerAddress("192.168.10.115",10000));
		db = client.getDatabase("shake");
		// 获取集合
		collection = db.getCollection("wxuser");
		long count = collection.count(and(eq("original_id","gh_c23eed399388"),ne("tag_list", null)));
		System.out.println(count);
	}
	
	@Test
	public void testDistinct(){
		List<String> list = collection.distinct("cuisine", String.class).into(new ArrayList<>());
		System.out.println(list);
	}
	

	private Object asList(Object... d) {
		return Arrays.asList(d);
	}

	private void printResultSet(FindIterable<Document> iterable) {
		iterable.forEach(new Block<Document>() {
			public void apply(Document document) {
				System.out.println(document);
			}
		});
/*		MongoCursor<Document> cursor =iterable.iterator();
		while (cursor.hasNext()) {
			Document document = cursor.next();
			
		}*/
		endTime = System.currentTimeMillis();
		System.out.println("操作耗时: " + (endTime - startTime) + "ms");
	}

}
