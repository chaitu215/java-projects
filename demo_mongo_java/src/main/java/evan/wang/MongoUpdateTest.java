package evan.wang;

import static java.util.Arrays.asList;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

public class MongoUpdateTest {
	private MongoClient client;
	private MongoDatabase db;
	private long startTime;
	private long endTime;

	@Before
	public void testConn() {
		startTime = System.currentTimeMillis();
		client = LocalMongoClient.getMongoClient();
		// 获取数据库
		db = client.getDatabase("test");
	}

	@After
	public void testClose() {
		if (client != null) {
			client.close();
		}
		db = null;
	}

	@Test
	public void updateTest1() {
		System.out.println("更新前----------------");
		FindIterable<Document> iterable = db.getCollection("restaurants").find(new Document("name", "Juni"));
		printResultSet(iterable);
		db.getCollection("restaurants").updateOne(new Document("name", "Juni"),
				new Document("$set", new Document("cuisine", "American (New)"))
				                        .append("$currentDate", new Document("lastModified", true)));
		System.out.println("更新后----------------");
		iterable = db.getCollection("restaurants").find(new Document("name", "Juni"));
		printResultSet(iterable);
	}

	/**
	 * db.restaurants.update({"restaurant_id": "41156888"}, {"$set" : {"address.street" : "East 31st Street updated"  }})
	 */
	@Test
	public void updateTest2() {
		UpdateResult result = db.getCollection("restaurants").updateOne(new Document("restaurant_id", "41156888"),
				new Document("$set", new Document("address.street", "East 31st Street updated")));
		System.out.println("操作影响条数: " + result.getModifiedCount());
	}

	/**
	 * db.restaurants.update({"address.zipcode": "10016","cuisine", "Other"}
	 *             , {"$set" : {"cuisine" : "Category To Be Determined"}, "$currentDate" : {"lastModified" : true}})
	 */
	//@Test
	public void updateTest3() {
		UpdateResult result = db.getCollection("restaurants")
		          .updateMany(new Document("address.zipcode", "10016").append("cuisine", "Other"),
				    new Document("$set", new Document("cuisine", "Category To Be Determined")).append("$currentDate",
					new Document("lastModified", true)));
		System.out.println("操作影响条数: " + result.getModifiedCount());
	}
	
	/**
	 * Replace a Document
	 */
	@Test
	public void updateTest4(){
		UpdateResult result = db.getCollection("restaurants")
				.replaceOne(new Document("_id" , new ObjectId("57a8b8b959b027b8d539e05e")),
		        new Document("address", new Document()
		                        .append("street", "2 Avenue")
		                        .append("zipcode", "10075")
		                        .append("building", "1480")
		                        .append("coord", asList(-73.9557413, 40.7720266)))
		              .append("name", "Vella 2")
		              .append("restaurant_id", "40361322")
						);
		System.out.println("操作影响条数: " + result.getModifiedCount());
	}
	
    
	private void printResultSet(FindIterable<Document> iterable) {
		iterable.forEach(new Block<Document>() {
			public void apply(Document document) {
				System.out.println(document);
			}
		});
		endTime = System.currentTimeMillis();
		System.out.println("操作耗时: " + (endTime - startTime) + "ms");
	}

}
