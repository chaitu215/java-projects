package evan.wang.kafka;

import org.apache.kafka.clients.producer.*;

import java.util.Date;
import java.util.Properties;

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
		final String myTopic = "wangshengyong";

		Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.10.134:9092,192.168.10.135:9092,192.168.10.136:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		Producer<String, String> producer = new KafkaProducer<>(props);
		for (int i = 0; i < 100; i++) {
			long startTime = System.currentTimeMillis();
			String key = "key-" + i;
			String message = new Date(startTime).getTime() + ", www.example.com, ";
			try {
			   //异步发送，回调方法在请求完成时将被回调
			   producer.send(new ProducerRecord<>(myTopic, key, message), new DemoCallBack(startTime, key, message));
			   //同步发送，调用get(),则将阻塞，直到相关请求完成并返回该消息的metadata，或抛出发送异常 
			   //producer.send(new ProducerRecord<>("mytopic0", key, message)).get();
			}catch (Exception e) {
			   e.printStackTrace();
			}
		}
		producer.close();
	}

	static class DemoCallBack implements Callback {

		private final long startTime;
		private final String key;
		private final String message;

		public DemoCallBack(long startTime, String key, String message) {
			this.startTime = startTime;
			this.key = key;
			this.message = message;
		}

		@Override
		public void onCompletion(RecordMetadata metadata, Exception exception) {
			long elapsedTime = System.currentTimeMillis() - startTime; //经历时间
	        if (metadata != null) {
	            System.out.println( "message(" + key + ", " + message + ") sent to partition(" + metadata.partition() + "), " 
	        +"offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
	        } else {
	            exception.printStackTrace();
	        }
		}

	}

}

// https://github.com/apache/kafka/tree/trunk/examples/src/main/java/kafka/examples
