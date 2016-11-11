package evan.wang;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 作者：wangsy
 * 日期：2016/7/29 11:54
 * 描述：Sender
 */
public class MessageSender {
    // 发送次数
    private static final int SEND_NUM = 5;
    // tcp 地址
    private static final String BROKER_URL = "tcp://localhost:61616";
    // 目标
    private static final String DESTINATION = "MessageQueue";

    public static void main(String[] args) {
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
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            //创建目标
            Destination destination = session.createQueue(DESTINATION);
            //创建消息生产者
            MessageProducer producer = session.createProducer(destination);
            // 设置不持久化，此处学习，实际根据项目决定
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            for (int i = 0; i < SEND_NUM; i++) {
                TextMessage message = session.createTextMessage("ActiveMq 发送的消息");
                System.out.println("发送消息: ActiveMq 发送的消息");
                //发送消息到目的地方
                producer.send(message);
            }
            //提交会话
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭释放资源
                if (session != null) {
                    session.close();
                }
                if (connection != null)
                    connection.close();
            } catch (JMSException je) {
                je.printStackTrace();
            }
        }
    }

}
