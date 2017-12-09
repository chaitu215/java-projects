package evan.wang.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @auth evan
 * @date 2017/11/9 18:03
 */
public class TestAtomic {
    private static AtomicLong aln = new AtomicLong(0); //操作原子性

    public static void main(String[] args) throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(16);
        final CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            service.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        aln.addAndGet(1);
                    }
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        System.out.println(aln.get());
        System.exit(0);
    }

}
