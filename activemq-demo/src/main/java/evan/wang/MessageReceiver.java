package evan.wang;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 作者：wangsy
 * 日期：2016/7/29 11:55
 * 描述：Receiver
 */
public class MessageReceiver {
    // tcp 地址
    private static final String BROKER_URL = "tcp://localhost:61616";
    // 目标
    private static final String DESTINATION = "MessageQueue";

    public static void main(String[] args) {
        System.out.println("start receiver---------------------------");
        // 创建链接工厂
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                ActiveMQConnectionFactory.DEFAULT_USER,
                ActiveMQConnectionFactory.DEFAULT_PASSWORD,
                BROKER_URL);
        Connection connection = null;
        Session session = null;
        try {
            //建立连接
            connection = connectionFactory.createConnection();
            //启动连接
            connection.start();
            //创建会话
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建目标
            Destination destination = session.createQueue(DESTINATION);
            //创建消息消费者
            MessageConsumer consumer = session.createConsumer(destination);
            while (true) {
                // 接收数据的时间（等待） 100 ms
                TextMessage message = (TextMessage) consumer.receive(100000);
                if (null != message) {
                    String messageContent = message.getText();
                    System.out.println("收到消息" + messageContent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭释放资源
                if (session != null) {
                    session.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException je) {
                je.printStackTrace();
            }
        }
    }

}
