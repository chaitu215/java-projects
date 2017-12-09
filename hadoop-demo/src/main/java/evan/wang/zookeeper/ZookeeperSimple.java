package evan.wang.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * zookeeper api示例
 * @author: wangsy
 * @date: 2017年6月12日
 */
public class ZookeeperSimple implements Watcher {
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

	@Override
	public void process(WatchedEvent event) {
		System.out.println("Receive watched event : " + event);
		if (KeeperState.SyncConnected == event.getState()) {
			connectedSemaphore.countDown();
		}
	}

	public static void main(String[] args) throws IOException {
		ZooKeeper zooKeeper = new ZooKeeper("192.168.10.134:2181,192.168.10.135:2181", 5000,
				new ZookeeperSimple());
		System.out.println(zooKeeper.getState());
		try {
			connectedSemaphore.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Zookeeper session established");
		try {
			createNode(zooKeeper);
			deleteNode(zooKeeper);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建节点
	 */
	public static void createNode(ZooKeeper zooKeeper) throws KeeperException, InterruptedException {
		String path1 = zooKeeper.create("/zk-test-ephemeral-", "".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL); // 临时节点
		System.out.println("Success create znode: " + path1);

		String path2 = zooKeeper.create("/zk-test-ephemeral-", "".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL_SEQUENTIAL); // 临时顺序节点
		System.out.println("Success create znode: " + path2);

		if (zooKeeper.exists("/zk-test-", false) == null) {
			String path3 = zooKeeper.create("/zk-test-", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT); // 临时顺序节点
			System.out.println("Success create znode: " + path3);
		}
	}

	/**
	 * 删除节点
	 */
	private static void deleteNode(ZooKeeper zooKeeper) throws Exception {
		String path = "/zk-book";
		zooKeeper.create(path, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println("success create znode: " + path);
		zooKeeper.create(path + "/c1", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println("success create znode: " + path + "/c1");
		try {
			zooKeeper.delete(path, -1);
		} catch (Exception e) {
			System.out.println("fail to delete znode: " + path);
		}

		zooKeeper.delete(path + "/c1", -1);
		System.out.println("success delete znode: " + path + "/c1");
		zooKeeper.delete(path, -1);
		System.out.println("success delete znode: " + path);
		Thread.sleep(Integer.MAX_VALUE);
	}

}
