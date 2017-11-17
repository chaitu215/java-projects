package evan.wang.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Callable 返回线程执行结果
 * @author: wangsy
 * @date: 2017年11月9日
 */
public class TestCallable {

    class MyCallableThread implements Callable<Map> {
        @Override
        public Map call() throws Exception {
            System.out.println("xxxxxxxxxxxxx111111");
            Thread.sleep(5 * 1000);
            System.out.println("xxxxxxxxxxxxx222222");
            Map<String, Integer> result = new HashMap<>();
            result.put("xxx", 111);
            return result;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        System.out.println("main---------------1111111");
        Callable callable = new TestCallable().new MyCallableThread();
        FutureTask future = new FutureTask<>(callable);
        new Thread(future).start();
        if (future.get() == null) {
            Thread.yield();
        } else {
            System.out.println(future.get());
        }
        System.out.println("main---------------2222222");
    }

}






