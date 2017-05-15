package evan.wang.kafka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import kafka.utils.ShutdownableThread;

/**
 * 
 * @author: wangsy
 * @date: 2017年3月17日
 */
public class TestConsumer extends ShutdownableThread {
	private final KafkaConsumer<String, String> consumer;
	private final String topic;

	/**
	 * @param name
	 * @param isInterruptible
	 */
	private TestConsumer(String topic) {
		super("KafkaConsumerExample", false);
		this.topic = topic;
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.10.134:9092,192.168.10.135:9092,192.168.10.136:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer"); // 订阅的消息只能够被同一个组中的一个消费者消费，可以被多个消费者组订阅
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // 自动提交偏移量
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		consumer = new KafkaConsumer<>(props);

	}

	@Override
	public void doWork() {
		try {
			// 订阅主题
			consumer.subscribe(Collections.singletonList(this.topic));
			ConsumerRecords<String, String> records = consumer.poll(1000);
			for (ConsumerRecord<String, String> record : records) {
				System.out.println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		TestConsumer consumerThread = new TestConsumer("wangshengyong");
		consumerThread.start();
	}

	// copy依赖jar到lib目录下，通过以下启动多个消费者
	// 运行：java -classpath kafka-demo.jar;lib/* evan.wang.kafka.TestConsumer

}
