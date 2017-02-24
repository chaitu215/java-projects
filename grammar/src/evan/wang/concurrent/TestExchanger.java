package evan.wang.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * 两个线程之间进行数据交换;
 * 线程会阻塞在Exchanger的exchanger()方法上，直到另一个线程也达到了exchanger()方法上，两个线程交换数据，然后继续执行各自任务。
 */
public class TestExchanger {

	public void test() {
		final Exchanger<List<Integer>> exchanger = new Exchanger<>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<Integer> list = new ArrayList<>(2);
				list.add(1);
				list.add(2);
				try {
					System.out.println("thread1: " + list);
					Thread.sleep(1000);
					list = exchanger.exchange(list);
					System.out.println("thread1: " + list);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				List<Integer> list = new ArrayList<>(2);
				list.add(4);
				list.add(5);
				try {
					System.out.println("thread2: " + list);
					Thread.sleep(5000);
					list = exchanger.exchange(list);
					System.out.println("thread2: " + list);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public static void main(String[] args) {
		new TestExchanger().test();
	}

}
