package evan.wang.zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooDefs.Ids;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * zkClient客户端 https://github.com/sgroschupf/zkclient
 * 
 * @author: wangsy
 * @date: 2017年6月14日
 */
public class zookeeperClientZkclient {
	ZkClient zkClient;

	@Before
	public void before() {
		zkClient = new ZkClient("192.168.10.134:2181", 5000);
	}

	@After
	public void after() {
		if (zkClient != null)
			zkClient.close();
	}

	@Test
	public void connect_sample() {
		System.out.println("zookeeper session estableished.");
		System.out.println(zkClient.countChildren("/"));
	}

	@Test
	public void create() {
		zkClient.createEphemeral("/zk-book/zkclient", "init", Ids.OPEN_ACL_UNSAFE);
		System.out.println(zkClient.readData("/zk-book/zkclient"));
		zkClient.createPersistent("/zk-book/rr/tt/tt/tt", true); // true递归创建
	}

	@Test
	public void delete() {
		// 递归删除
		zkClient.deleteRecursive("/zk-book/rr");
	}

}
