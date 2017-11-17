package evan.wang.concurrent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 饱汉模式, 双重检查锁
 * @auth evan
 * @date 2017/11/9 17:12
 * http://www.importnew.com/27233.html
 */
public class Singleton1_2 {
    private static volatile Singleton1_2 singleton = null;
    private Singleton1_2() {
    }
    public static Singleton1_2 getInstance() {
        // may get half object , add volatile  https://monkeysayhi.github.io/2016/11/29/volatile%E5%85%B3%E9%94%AE%E5%AD%97%E7%9A%84%E4%BD%9C%E7%94%A8%E3%80%81%E5%8E%9F%E7%90%86/
        if (singleton == null) {
            synchronized (Singleton1_2.class) {
                if (singleton == null) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    singleton = new Singleton1_2();
                }
            }
        }
        return singleton;
    }


    private static Set<String> ss = new HashSet<>();
    public static void main(String[] args)  throws  Exception{

        ExecutorService service = Executors.newFixedThreadPool(16);
        final  CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            service.submit(new Runnable() {
                @Override
                public void run() {
                    String info = Singleton1_2.getInstance().toString();
                    ss.add(info);
                    System.out.println(info);
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        System.out.println("size+++++"+ss.size());  //size为1则正确
        System.exit(0);
    }

}
