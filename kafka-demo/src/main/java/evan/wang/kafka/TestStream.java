package evan.wang.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStreamBuilder;

/**   
 * 
 * @author: wangsy
 * @date: 2017年3月20日
 */
public class TestStream {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    Map<String, Object> props = new HashMap<>();
	    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-stream-processing-application");
	    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	    props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, StringSerializer.class);
	    props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, StringSerializer.class);
	    StreamsConfig config = new StreamsConfig(props);

	    KStreamBuilder builder = new KStreamBuilder();
	    builder.addSource("SOURCE","my-input-topic");
	    //builder.add.mapValue(value -> value.length().toString()).to("my-output-topic");

	    KafkaStreams streams = new KafkaStreams(builder, config);
	    streams.start();

	}

}
