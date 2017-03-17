package evan.wang.kafka;

import java.util.Collections;
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
	public TestConsumer(String topic) {
		super("KafkaConsumerExample", false);
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.10.121:9092,192.168.10.122:9092,192.168.10.123:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");
		consumer = new KafkaConsumer<>(props);
		this.topic = topic;
	}

	@Override
	public void doWork() {
		try {
			consumer.subscribe(Collections.singletonList(this.topic));
			ConsumerRecords<String, String> records = consumer.poll(1000);
			for (ConsumerRecord<String, String> record : records) {
				System.out.println("Received message: (" + record.key() + ", " + record.value() + ") at offset "+ record.offset());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String name() {
		return null;
	}

	@Override
	public boolean isInterruptible() {
		return false;
	}

	public static void main(String[] args) {
		TestConsumer consumerThread = new TestConsumer("test");
		consumerThread.start();
	}

}
