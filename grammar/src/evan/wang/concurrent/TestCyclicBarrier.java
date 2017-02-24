package evan.wang.concurrent;

import java.util.concurrent.CyclicBarrier;

/**
 * 循环屏障, 协同多个线程，让多个线程在这个屏障前等待，直到所有线程都达到了这个屏障时，再分别执行后面的动作。
 *
 */
public class TestCyclicBarrier {
	public void test() throws Exception {
		int number = 5;
		// 调用await方法的线程数必须和CyclicBarrier构造器参数一致，否则会阻塞等待
		CyclicBarrier barrier = new CyclicBarrier(number + 1);
		for (int i = 0; i < number; i++) {
			new Thread(new Worker(barrier, i)).start();
		}
		System.out.println("main wait!");
		barrier.await();
		System.out.println("main finished");
	}

	class Worker implements Runnable {
		CyclicBarrier barrier;
		int index;

		public Worker(CyclicBarrier barrier, int index) {
			this.barrier = barrier;
			this.index = index;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(index * 1000);
				System.out.println("work" + index + " wait-----");
				barrier.await();
				System.out.println("work" + index + " finished-----");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new TestCyclicBarrier().test();
	}

}
