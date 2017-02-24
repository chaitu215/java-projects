package evan.wang.concurrent;

import java.util.concurrent.Semaphore;

/**
 * 信号量，控制并发数量
 */
public class TestSemaphore {

	public void test() {
		Semaphore semaphore = new Semaphore(10);
		for (int i = 0; i < 100; i++) {
			new Thread(new Worker(semaphore, i)).start();
		}
		System.out.println("main finished");

	}

	class Worker implements Runnable {
		Semaphore semaphore;
		int index;

		public Worker(Semaphore semaphore, int index) {
			this.semaphore = semaphore;
			this.index = index;
		}

		@Override
		public void run() {
			try {
				semaphore.acquire();
				System.out.println("work" + index + " working-----");
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				semaphore.release();
			}
		}
	}

	public static void main(String[] args) {
		new TestSemaphore().test();
	}

}
