package evan.wang;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 作者：wangsy
 * 日期：2016/7/6 15:31
 * 描述：If you need to run some specific code once the SpringApplication has started, you can implement
 * the ApplicationRunner or CommandLineRunner interfaces. Both interfaces work in the same
 * way and offer a single run method which will be called just before SpringApplication.run(…)
 * completes.
 */
@Component
@Order(2)
public class MyCommandLineRunner2 implements CommandLineRunner {

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("after springApplication.run() completes 22222222222222222");
    }

}
