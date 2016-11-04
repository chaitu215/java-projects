package evan.wang;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;

/**
 * @author wangshengyong
 * @date 2016年8月8日 下午5:57:22 
 */
public class LocalMongoClient {
	public static final String IP = "127.0.0.1";
	public static final int PORT = 27017;

	public static MongoClient getMongoClient() {
		//单机连接
		MongoClient client = new MongoClient(LocalMongoClient.IP, LocalMongoClient.PORT);
		
		//副本集连接
/*		List<ServerAddress> seeds = new ArrayList<>();
		ServerAddress serverAddress1 = new ServerAddress("192.168.1.177", 27017);
		ServerAddress serverAddress2 = new ServerAddress("192.168.1.177", 27018);
		ServerAddress serverAddress3 = new ServerAddress("192.168.1.177", 27019);
		seeds.add(serverAddress1);
		seeds.add(serverAddress2);
		seeds.add(serverAddress3);
		MongoClient client = new MongoClient(seeds);
		//默认只可以从主库中读写，可以设置从副本中读取数据
		client.setReadPreference(ReadPreference.secondaryPreferred());*/
		return client;
	}

}

/**
 * rs.conf() : 查询副本集配置信息
 * rs.status() : 查询副本集状态
 * db.isMaster() : 查询当前库是否为主节点
 */