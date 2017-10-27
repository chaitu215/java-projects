package evan.wang.zookeeper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.InstanceSpec;
import org.apache.curator.test.TestingCluster;
import org.apache.curator.test.TestingServer;
import org.apache.curator.test.TestingZooKeeperServer;
import org.apache.curator.utils.EnsurePath;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.utils.ZKPaths.PathAndNode;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

/**
 * 开源zookeeper客户端Curator
 * 
 * @author: wangsy
 * @date: 2017年6月13日
 */
public class ZookeeperClientCurator {
	private static final String MASTER = "192.168.10.134:2181";

	@Test
	public void create() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client1 = CuratorFrameworkFactory.newClient(MASTER, 5000, 3000, retryPolicy);
		client1.start();
		System.out.println("Zookeeper session1 established. ");
		CuratorFramework client2 = CuratorFrameworkFactory.builder().connectString(MASTER).sessionTimeoutMs(5000)
				.retryPolicy(retryPolicy).namespace("base").build(); // 相对/base目录进行
		client2.start();
		System.out.println("Zookeeper session2 established. ");
	}

	@Test
	public void createNode() throws Exception {
		String path = "/zk-book/c1";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(MASTER).sessionTimeoutMs(5000)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "init".getBytes());
		System.out.println("success create znode: " + path);
	}

	@Test
	public void deleteNode() throws Exception {
		String path = "/zk-book/c1";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(MASTER).sessionTimeoutMs(5000)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, "init".getBytes());
		Stat stat = new Stat();
		System.out.println(new String(client.getData().storingStatIn(stat).forPath(path)));
		client.delete().deletingChildrenIfNeeded().withVersion(stat.getVersion()).forPath(path);
		System.out.println("success delete znode " + path);
	}

	@Test
	public void getData() throws Exception {
		String path = "/zk-book";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(MASTER).sessionTimeoutMs(5000)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "init".getBytes());
		Stat stat = new Stat();
		System.out.println(new String(client.getData().storingStatIn(stat).forPath(path)));
	}

	@Test
	public void updateData() throws Exception {
		String path = "/zk-book";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(MASTER).sessionTimeoutMs(5000)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "init".getBytes());
		Stat stat = new Stat();
		client.getData().storingStatIn(stat).forPath(path);
		System.out.println("Success set node for : " + path + ", new version: "
				+ client.setData().withVersion(stat.getVersion()).forPath(path, "aaa".getBytes()).getVersion());
		try {
			client.setData().withVersion(stat.getVersion()).forPath(path);
		} catch (Exception e) {
			System.out.println("Fail set node due to " + e.getMessage());
		}
	}

	/**
	 * 异步
	 * 
	 * @throws Exception
	 */
	@Test
	public void asyncCreate() throws Exception {
		String path = "/zk-book";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(MASTER).sessionTimeoutMs(5000)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		final CountDownLatch semaphore = new CountDownLatch(2);
		ExecutorService tp = Executors.newFixedThreadPool(2);
		client.start();
		System.out.println("Main thread: " + Thread.currentThread().getName());

		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
			public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
				System.out.println("1event[code: " + event.getResultCode() + ", type: " + event.getType() + "]"
						+ ", Thread of processResult: " + Thread.currentThread().getName());
				System.out
						.println(new String(client.getData().storingStatIn(event.getStat()).forPath(event.getPath())));
				semaphore.countDown();
			}
		}, tp).forPath(path, "init1".getBytes());

		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
			public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
				System.out.println("2event[code: " + event.getResultCode() + ", type: " + event.getType() + "]"
						+ ", Thread of processResult: " + Thread.currentThread().getName());
				System.out
						.println(new String(client.getData().storingStatIn(event.getStat()).forPath(event.getPath())));
				semaphore.countDown();
			}
		}).forPath(path, "init2".getBytes());

		semaphore.await();
		tp.shutdown();

	}

	/**
	 * 节点监控
	 * 
	 * @throws Exception
	 */
	@Test
	public void nodeListenr() throws Exception {
		String path = "/zk-book/nodecache";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(MASTER).sessionTimeoutMs(5000)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "init".getBytes());
		final NodeCache cache = new NodeCache(client, path);
		cache.start(true);
		cache.getListenable().addListener(new NodeCacheListener() {
			public void nodeChanged() throws Exception {
				System.out.println("Node data update, new data: " + new String(cache.getCurrentData().getData()));
			}
		});
		cache.close();
		client.setData().forPath(path, "u".getBytes());
		Thread.sleep(1000);
		client.delete().deletingChildrenIfNeeded().forPath(path);
		Thread.sleep(Integer.MAX_VALUE);
	}

	/**
	 * Master选举
	 * 
	 * @throws Exception
	 */
	@Test
	public void master() throws Exception {
		String master_path = "/curator_recipes_master_path";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(MASTER).sessionTimeoutMs(5000)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		LeaderSelector selector = new LeaderSelector(client, master_path, new LeaderSelectorListenerAdapter() {
			public void takeLeadership(CuratorFramework client) throws Exception {
				System.out.println("成为Master角色");
				Thread.sleep(3000);
				System.out.println("完成Master操作，释放Master权利");
			}
		});
		selector.autoRequeue();
		selector.start();
		Thread.sleep(Integer.MAX_VALUE);
	}

	final CyclicBarrier cyclicBarrier = new CyclicBarrier(10);

	/**
	 * 分布式锁
	 * 
	 * @throws Exception
	 */
	@Test
	public void Recipes_NoLock() throws Exception {
		final CountDownLatch down = new CountDownLatch(10);
		ExecutorService executor = Executors.newFixedThreadPool(4);
		for (int i = 0; i < 10; i++) {
			System.out.println(i);
			executor.submit(new Runnable() {
				@Override
				public void run() {
					try {
						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
						String orderNo = sdf.format(new Date());
						System.out.println("1、生成的订单号是 : " + orderNo); // 结果会重复
						down.countDown();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		down.await();
		System.out.println(111);

		// 分布式锁
		String lock_path = "/curator_recipes_lock_path";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(MASTER)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		final InterProcessMutex lock = new InterProcessMutex(client, lock_path);
		for (int i = 0; i < 30; i++) {
			new Thread(new Runnable() {
				public void run() {
					try {
						lock.acquire();
					} catch (Exception e) {
					}
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
					String orderNo = sdf.format(new Date());
					System.out.println("2、生成的订单号是 : " + orderNo);
					try {
						lock.release();
					} catch (Exception e) {
					}
				}
			}).start();
		}
		Thread.sleep(10000);
	}

	/**
	 * 分布式计数器
	 * 
	 * @throws Exception
	 */
	@Test
	public void Recipes_DistAtomicInt() throws Exception {
		String lock_path = "/curator_recipes_lock_path";
		String distatomicint_path = "/curator_recipes_distatomicint_path";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(MASTER)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		final InterProcessMutex lock = new InterProcessMutex(client, lock_path);
		final DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(client, distatomicint_path,
				new RetryNTimes(3, 1000));
		final CountDownLatch down = new CountDownLatch(100);
		for (int i = 0; i < 100; i++) {
			new Thread(new Runnable() {
				public void run() {
					try {
						lock.acquire();
						AtomicValue<Integer> rc = atomicInteger.add(1);
						System.out.println("Result: " + rc.succeeded());
						lock.release();
						down.countDown();
					} catch (Exception e) {
					}
				}
			}).start();
		}
		down.await();
		System.out.println(atomicInteger.get().postValue()); // 100
	}

	static DistributedBarrier barrier;

	/**
	 * 分布式Barrier
	 * 
	 * @throws Exception
	 */
	@Test
	public void Recipes_Barrier() throws Exception {
		final String barrier_path = "/curator_recipes_barrier_path";
		for (int i = 0; i < 5; i++) {
			new Thread(new Runnable() {
				public void run() {
					try {
						CuratorFramework client = CuratorFrameworkFactory.builder().connectString(MASTER)
								.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
						client.start();
						barrier = new DistributedBarrier(client, barrier_path);
						System.out.println(Thread.currentThread().getName() + "号barrier设置");
						barrier.setBarrier();
						barrier.waitOnBarrier();
						System.out.println("启动...");
						// System.out.println(barrier);
					} catch (Exception e) {
					}
				}
			}).start();
		}
		Thread.sleep(2000);
		barrier.removeBarrier();
		Thread.sleep(Integer.MAX_VALUE);
	}

	/**
	 * 分布式Barrier
	 * 
	 * @throws Exception
	 */
	@Test
	public void Recipes_Barrier2() throws Exception {
		final String barrier_path = "/curator_recipes_barrier_path1";
		for (int i = 0; i < 5; i++) {
			new Thread(new Runnable() {
				public void run() {
					try {
						CuratorFramework client = CuratorFrameworkFactory.builder().connectString(MASTER)
								.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
						client.start();
						DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, barrier_path, 5);
						Thread.sleep(Math.round(Math.random() * 3000));
						System.out.println(Thread.currentThread().getName() + "号进入barrier");
						// 每个处于准备进入等待，进入成员数达到5个后，所有成员触发继续往下运行
						barrier.enter();
						System.out.println("启动...");
						Thread.sleep(Math.round(Math.random() * 3000));
						// 每个处于准备退出等待，进入成员数达到5个后，所有成员触发继续往下运行
						barrier.leave();
						System.out.println("退出...");
					} catch (Exception e) {
					}
				}
			}).start();
		}
		Thread.sleep(Integer.MAX_VALUE);
	}

	/**
	 * 工具类ZKPaths
	 */
	@Test
	public void zkPath() throws Exception {
		String path = "/curator_zkpath_sample";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(MASTER).sessionTimeoutMs(5000)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		ZooKeeper zookeeper = client.getZookeeperClient().getZooKeeper();

		String dir1 = path + "/child1";
		String dir2 = path + "/child2";
		ZKPaths.mkdirs(zookeeper, dir1);
		ZKPaths.mkdirs(zookeeper, dir2);
		System.out.println(ZKPaths.getSortedChildren(zookeeper, path));

		System.out.println(ZKPaths.fixForNamespace(path, "sub"));
		System.out.println(ZKPaths.makePath(path, "sub"));
		System.out.println(ZKPaths.getNodeFromPath("/curator_zkpath_sample/sub1"));

		PathAndNode pn = ZKPaths.getPathAndNode(path + "/sub1");
		System.out.println(pn.getPath());
		System.out.println(pn.getNode());

		ZKPaths.deleteChildren(client.getZookeeperClient().getZooKeeper(), path, true);

	}

	/**
	 * 工具类EnsurePath 其提供了一种能够确保数据节点存在的机制，当上层业务希望对一个数据节点进行操作时，操作前需要确保该节点存在。
	 * EnsurePath采取了如下节点创建方式，试图创建指定节点，如果节点已经存在，那么就不进行任何操作，也不对外抛出异常，否则正常创建数据节点。
	 */
	@Test
	public void ensurePath() throws Exception {
		String path = "/zk-book/c2";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(MASTER).sessionTimeoutMs(5000)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		client.usingNamespace("zk-book");

		EnsurePath ensurePath = new EnsurePath(path);
		ensurePath.ensure(client.getZookeeperClient());
		ensurePath.ensure(client.getZookeeperClient());

		/*
		 * EnsurePath ensurePath2 = client.newNamespaceAwareEnsurePath("/c2");
		 * ensurePath2.ensure(client.getZookeeperClient());
		 */
	}

	/**
	 * zookeeper测试服务TestingServer
	 * 
	 * @throws Exception
	 */
	@Test
	public void testServer() throws Exception {
		TestingServer server = new TestingServer(2181, new File("E:\\bigdata\\zookeeper\\data"));
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(server.getConnectString())
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		client.create().forPath("/c1", "init".getBytes());
		System.out.println(client.getChildren().forPath("/"));
		server.close();
	}

	/**
	 * zookeeper测试集群服务TestingServer
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCluster() throws Exception {
		TestingCluster cluster = new TestingCluster(3);
/*		TestingCluster cluster = new TestingCluster(
				new InstanceSpec(new File("E:\\bigdata\\zookeeper\\c1"), 2181, -1, -1, true, 1, -1, -1),
				new InstanceSpec(new File("E:\\bigdata\\zookeeper\\c2"), 2182, -1, -1, true, 2, -1, -1),
				new InstanceSpec(new File("E:\\bigdata\\zookeeper\\c3"), 2183, -1, -1, true, 3, -1, -1));*/
		cluster.start();
		Thread.sleep(5000);

		TestingZooKeeperServer leader = null;
		for (TestingZooKeeperServer zs : cluster.getServers()) {
			System.out.println(zs.getInstanceSpec().getServerId() + "-");
			System.out.println(zs.getQuorumPeer().getServerState() + "-");
			System.out.println(zs.getInstanceSpec().getDataDirectory().getAbsolutePath() + "-");
			if (zs.getQuorumPeer().getServerState().equals("leading")) {
				leader = zs;
			}
		}

		leader.kill();

		System.out.println("--After leader kill:");
		for (TestingZooKeeperServer zs : cluster.getServers()) {
			System.out.println(zs.getInstanceSpec().getServerId() + "-");
			System.out.println(zs.getQuorumPeer().getServerState() + "-");
			System.out.println(zs.getInstanceSpec().getDataDirectory().getAbsolutePath() + "-");
		}
		//cluster.stop();
		cluster.close();

	}

}
