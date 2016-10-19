package evan.wang;

import java.util.Arrays;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * @author: wangshengyong
 * @date: 2016年8月15日
 * @description:
 */
public class MongoAggregateTest {
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
	public void testGroupByBorough() {
		//根据borough分组，并统计每组个数
		AggregateIterable<Document> iterable = collection
				.aggregate(Arrays.asList(new Document("$group", 
						new Document("_id", "$borough")
						.append("count", new Document("$sum", 1)))));
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		        System.out.println(document.toJson());
		    }
		});
	}
	
	@Test
	public void testMatchAndGroup() {
		//查询borough为Queens，并且cuisine为Brazilian，然后根据address.zipcode分组，并统计每组个数
		AggregateIterable<Document> iterable = collection.aggregate(Arrays.asList(
		        new Document("$match", new Document("borough", "Queens").append("cuisine", "Brazilian")),
		        new Document("$group", new Document("_id", "$address.zipcode")
		        		     .append("count", new Document("$sum", 1))
		        		     .append("restaurant_id", new Document("$max", "$restaurant_id")))));
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		        System.out.println(document.toJson());
		    }
		});
	}
	
	
}
