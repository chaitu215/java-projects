package evan.wang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

/**
 * 导入示例程序
 * mongoimport --db test --collection restaurants --drop --file ~/downloads/primer-dataset.json
 */
