package evan.wang.parquet;

import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

import com.sun.mail.handlers.image_gif;

import parquet.column.ParquetProperties;
import parquet.example.data.Group;
import parquet.example.data.simple.SimpleGroupFactory;
import parquet.format.converter.ParquetMetadataConverter;
import parquet.hadoop.ParquetFileReader;
import parquet.hadoop.ParquetReader;
import parquet.hadoop.ParquetWriter;
import parquet.hadoop.example.GroupReadSupport;
import parquet.hadoop.example.GroupWriteSupport;
import parquet.hadoop.metadata.CompressionCodecName;
import parquet.hadoop.metadata.ParquetMetadata;
import parquet.schema.MessageType;
import parquet.schema.PrimitiveType;
import parquet.schema.Types;

/**
 * @auth evan
 * @date 2017/5/3 17:11
 */
public class TestParquetRead {
	public static final MessageType FILE_SCHEMA = Types.buildMessage()
			.required(PrimitiveType.PrimitiveTypeName.BINARY).named("user_name")
			.required(PrimitiveType.PrimitiveTypeName.INT64).named("bookid")
			.required(PrimitiveType.PrimitiveTypeName.INT32).named("bookscore")
			.named("douban");

	/**
	 * 写parquet文件
	 * @throws Exception
	 */
	@Test
	public void testWriteParquet() throws Exception {
		Configuration conf = new Configuration(true);
		conf.set("fs.defaultFS", "hdfs://192.168.10.132:9000");

		String file = "/user/wangsy/parquet/douban.parquet";
		Path path = new Path(file);
		FileSystem fs = path.getFileSystem(conf);
		if (fs.exists(path)) {
			fs.delete(path, true);
		}
		GroupWriteSupport.setSchema(FILE_SCHEMA, conf);
		SimpleGroupFactory f = new SimpleGroupFactory(FILE_SCHEMA);
		ParquetWriter<Group> writer = new ParquetWriter<>(path, new GroupWriteSupport(), CompressionCodecName.GZIP,
				1024, 1024, 512, true, false, ParquetProperties.WriterVersion.PARQUET_1_0, conf);

		for (int i = 0; i < 1000; i++) {
			writer.write(f.newGroup().append("user_name", String.valueOf(i)).append("bookid", new Long(i)).append("bookscore",new Random().nextInt(100)));
		}
		writer.close();
	}
	
	/**
	 * 获取schema信息
	 * @throws IOException
	 */
	@Test
	public void testGetSchema() throws IOException {
	    Configuration configuration = new Configuration(true);
	    configuration.set("fs.defaultFS","hdfs://192.168.10.132:9000");
	    ParquetMetadata readFooter = null;
	    Path parquetFilePath = new Path("/user/wangsy/parquet/douban.parquet");
	    readFooter = ParquetFileReader.readFooter(configuration, parquetFilePath, ParquetMetadataConverter.NO_FILTER);
	    MessageType schema =readFooter.getFileMetaData().getSchema();
	    System.out.println(schema.toString());
	}
	
//	      message douban {
//            required binary user_name;
//            required int64 bookid;
//            required int32 bookscore;
//         }
	     
		
	/**
	 * 读parquet文件
	 * @throws Exception
	 */
	@Test
	public void testReadParquet() throws Exception {
		Configuration configuration = new Configuration(true);
		configuration.set("fs.defaultFS","hdfs://192.168.10.132:9000");
		Path file = new Path("/user/wangsy/parquet/douban.parquet");
		Configuration conf = new Configuration();
		ParquetReader<Group> reader = ParquetReader.builder(new GroupReadSupport(), file).withConf(conf).build();
		Group group;
		while ((group = reader.read()) != null) {
			System.out.print(group);
		}
	}

}
