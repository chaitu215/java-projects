package evan.wang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

		// 获取集合
		collection = db.getCollection("restaurants");
		
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
		// 获取集合
		collection = db.getCollection("restaurants");
		
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
	
	
	@Test
	public void test() {
			// 获取集合
			collection = db.getCollection("sta_original_day");
			
			
			// mongodb聚合
			List<Document> pipeline = new ArrayList<>();
			// 查询条件
			Document match = new Document("$match", new Document("date_time", new Document("$gte", "2016-06-01").append("$lte", "2016-06-07"))
					              .append("original_id",new Document("$in", Arrays.asList("gh_cf3603d36721","gh_94e85ee385e4","gh_c23eed399388","fssss"))));
			// 分组统计
			Document group = new Document("$group", new Document("_id", "$date_time")
					              .append("new_count", new Document("$sum", "$new_count"))
					              .append("cancel_count", new Document("$sum", "$cancel_count"))
							      .append("net_count", new Document("$sum", "$net_count"))
							      .append("bind_count", new Document("$sum", "$bind_count"))
							      .append("new_loc_count", new Document("$sum", "$new_loc_count")));
			
			Document group1 =new Document("$group", new Document("_id", "$date_time")
			          .append("count", new Document("$sum", 1)));
			
			// 排序
			Document sort = new Document("$sort", new Document("_id", 1));
			pipeline.add(match);
			pipeline.add(group1);
			pipeline.add(sort);
			
			System.out.println(pipeline);
			// 查询统计表
			AggregateIterable<Document> iterable = collection
					.aggregate(Arrays.asList(match,group,sort));
			
			for(Document doc: iterable){
				System.out.println(doc);
			}
		}
	
	
	
}
