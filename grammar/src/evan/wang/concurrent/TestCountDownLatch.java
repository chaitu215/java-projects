package evan.wang.concurrent;

import java.util.concurrent.CountDownLatch;

/**
 * 
 * CountDownLatch : 倒计数锁存器 是一次性的障碍，允许一个或者多个线程等待一个或者多个其它线程来做某件事情；
 * 构造器带有一个int类型参数，这个int参数是指允许所有在等待的线程被处理之前，必须在锁存器对象上调用countDown()方法的次数。
 */
public class TestCountDownLatch {

	public void test() throws Exception {
		int number = 5;
		CountDownLatch latch = new CountDownLatch(number);
		for (int i = 0; i < number; i++) {
			new Thread(new Worker(latch, i)).start();
		}
		System.out.println("wait for all work done!");
		latch.await(); // 等待
		System.out.println("main finished");
	}

	class Worker implements Runnable {
		CountDownLatch latch;
		int index;

		public Worker(CountDownLatch latch, int index) {
			this.latch = latch;
			this.index = index;
		}

		@Override
		public void run() {
			try {
				System.out.println("work" + index + " working-----");
				Thread.sleep(5 * 1000);
				latch.countDown();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new TestCountDownLatch().test();
	}

}
