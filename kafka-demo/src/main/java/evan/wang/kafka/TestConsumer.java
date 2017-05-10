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
		props.put("bootstrap.servers", "192.168.10.134:9092,192.168.10.135:9092,192.168.10.136:9092");
		props.put("group.id", "DemoConsumer"); // 订阅的消息只能够被同一个组中的一个消费者消费，可以被多个消费者组订阅
		props.put("enable.auto.commit", "false"); // 自动提交偏移量
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
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
			ConsumerRecords<String, String> records = consumer.poll(100);
			final int minBatchSize = 200;
			List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
			for (ConsumerRecord<String, String> record : records) {
				buffer.add(record);
			}
			if (buffer.size() >= minBatchSize) {
				boolean handled = handleRecord(buffer);
				if (handled)
					consumer.commitAsync();
				buffer.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 处理消息
	private boolean handleRecord(List<ConsumerRecord<String, String>> list) {
		for (ConsumerRecord<String, String> record : list) {
			System.out.println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset());
		}
		return true;
	}

	public static void main(String[] args) {
		TestConsumer consumerThread = new TestConsumer("mytopic0");
		consumerThread.start();
	}

	// copy依赖jar到lib目录下，通过以下启动多个消费者
	// 运行：java -classpath kafka-demo.jar;lib/* evan.wang.kafka.TestConsumer

}
