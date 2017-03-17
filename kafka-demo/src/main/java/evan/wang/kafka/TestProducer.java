package evan.wang.kafka;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * 
 * @author: wangsy
 * @date: 2017年3月17日
 */
public class TestProducer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.10.121:9092,192.168.10.122:9092,192.168.10.123:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		Producer<String, String> producer = new KafkaProducer<>(props);
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			String ip = "192.168.10." + random.nextInt(255);
			long runTime = new Date().getTime();
			String message = runTime + ", www.example.com, " + ip;
			producer.send(new ProducerRecord<>("test", ip, message));
		}
		producer.close();
	}

}


// https://github.com/apache/kafka/tree/trunk/examples/src/main/java/kafka/examples
