package evan.wang;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

import java.util.Map;

/**
 * 作者：wangsy
 * 日期：2016/7/29 18:12
 * 描述：
 */
public class SpringReceiver {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:applicationContext-*.xml");
        JmsTemplate jmsTemplate = (JmsTemplate) ctx.getBean("jmsTemplate");
        System.out.println("spring receiver start-----------------------");
        while (true) {
            Map<String, Object> map = (Map<String, Object>) jmsTemplate.receiveAndConvert();
            System.out.println("收到消息：" + map.get("message"));
        }
    }

}
