package evan.wang.concurrent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 饱汉模式, Holder模式
 * @auth evan
 * @date 2017/11/9 17:12
 * http://www.importnew.com/27233.html
 */
public class Singleton1_3 {
    private Singleton1_3() {
    }
    private static class SingletonHolder{
        private static final Singleton1_3 singleton = new Singleton1_3();
        private SingletonHolder(){
        }
    }
    public synchronized static Singleton1_3 getInstance() {
        return SingletonHolder.singleton;
    }


    private static Set<String> ss = new HashSet<>();
    public static void main(String[] args)  throws  Exception{
        ExecutorService service = Executors.newFixedThreadPool(16);
        final  CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            service.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i=0; i<100; i++) {
                        String info = Singleton1_3.getInstance().toString();
                        ss.add(info);
                    }
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        System.out.println("size+++++"+ss.size());  //size为1则正确
        System.exit(0);
    }

}
