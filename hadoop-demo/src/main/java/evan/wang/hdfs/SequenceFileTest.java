package evan.wang.hdfs;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.SequenceFile.Reader;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

/**
 * SequenceFile示例
 * 
 * @author: wangshengyong
 * @date: 2016年11月15日
 */
public class SequenceFileTest {
	private static final String[] DATA = { "One, two, buckle my shoe", 
			                               "Three, four, shut the door",
			                               "Five, six, pick up sticks", 
			                               "Seven, eight, lay them straight", 
			                               "Nine, ten, a big fat hen"
			                               };

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("please set output file path");
			System.exit(1);
		}
		String uri = args[0];
		if (args.length < 2) {
			System.out.println("please set compress type");
			System.exit(1);
		}
		String compressType = args[1];
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(uri), conf);
		Path path = new Path(uri);
		IntWritable key = new IntWritable();
		Text value = new Text();
		SequenceFile.Writer writer = null;
		SequenceFile.Reader reader = null;
		try {
			// Writer : Uncompressed records.
			if (compressType.equals("1")) {
				System.out.println("compress none");
				writer = SequenceFile.createWriter(conf, Writer.file(path), Writer.keyClass(key.getClass()),
						Writer.valueClass(value.getClass()), Writer.compression(CompressionType.NONE));
			} else if (compressType.equals("2")) {
				System.out.println("compress record");
				// RecordCompressWriter : Record-compressed files, only compress
				// values.
				writer = SequenceFile.createWriter(conf, Writer.file(path), Writer.keyClass(key.getClass()),
						Writer.valueClass(value.getClass()), Writer.compression(CompressionType.RECORD));
			} else if (compressType.equals("3")) {
				System.out.println("compress block");
				// BlockCompressWriter : Block-compressed files, both keys &
				// values are collected in 'blocks' separately and compressed.
				// The size of the 'block' is configurable.
				writer = SequenceFile.createWriter(conf, Writer.file(path), Writer.keyClass(key.getClass()),
						Writer.valueClass(value.getClass()), Writer.compression(CompressionType.BLOCK));
			}

			// 写顺序文件
			for (int i = 0; i < 100; i++) {
				key.set(100 - i);
				value.set(DATA[i % DATA.length]);
				System.out.printf("[%s]\t%s\t%s\n", writer.getLength(), key, value);
				writer.append(key, value);
			}
			IOUtils.closeStream(writer);

			// 读顺序文件
			reader = new SequenceFile.Reader(conf, Reader.file(path));
			Writable rKey = new IntWritable();
			Writable rValue = new Text();
			reader.seek(2);
			reader.sync(0);//读取同步点
			long position = reader.getPosition();
			while (reader.next(rKey, rValue)) {
				System.out.printf("[%s]\t%s\t%s\n", position, rKey, rValue);
				position = reader.getPosition();
			}
			IOUtils.closeStream(reader);
		} finally {
			IOUtils.closeStream(fs);
		}

	}

}
