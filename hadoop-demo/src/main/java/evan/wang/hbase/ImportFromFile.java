package evan.wang.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import evan.wang.ObjectId;
import evan.wang.mapreduce.MrUtil;

/**
 * 
 * @author: wangsy
 * @date: 2017年6月29日
 */
public class ImportFromFile {
	public static final String NAME = "ImportFromFile";

	public enum Counters {
		LINES
	}

	static class ImportMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Mutation> {
		private byte[] family = null;
		private byte[] qualifier = null;

		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			String column = context.getConfiguration().get("conf.column");
			System.out.println("**********column************* " + column);
			byte[][] colkey = KeyValue.parseColumn(Bytes.toBytes(column));
			family = colkey[0];
			if (family.length > 1) {
				qualifier = colkey[1];
			}
		}

		@Override
		protected void map(LongWritable offset, Text line, Context context) throws IOException, InterruptedException {
			try {
				String lineString = line.toString();
				// byte[] rowkey = DigestUtils.md5(lineString);
				byte[] rowkey = new ObjectId().toByteArray();
				Put put = new Put(rowkey);
				put.addColumn(family, qualifier, Bytes.toBytes(lineString));
				context.write(new ImmutableBytesWritable(rowkey), put);
				context.getCounter(Counters.LINES).increment(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void createTable(Configuration conf, String table, String columnFamily) {
		try (Connection connection = ConnectionFactory.createConnection(conf); Admin admin = connection.getAdmin()) {
			HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(table));
			tableDescriptor.addFamily(new HColumnDescriptor(columnFamily).setCompressionType(Algorithm.NONE));
			if (admin.tableExists(tableDescriptor.getTableName())) {
				admin.disableTable(tableDescriptor.getTableName());
				admin.deleteTable(tableDescriptor.getTableName());
			}
			admin.createTable(tableDescriptor);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * create 'testtable', 'data'
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		conf.set("hbase.zookeeper.quorum", "192.168.10.134,192.168.10.135,192.168.10.136");
		// hbase表
		String table = "testtable";
		// 输入文件
		String input = "/user/wangsy/test-data.txt";
		// 列族:列名
		String column = "data:json";
		conf.set("conf.column", column);

		createTable(conf, table, column.split(":")[0]);

		Job job = Job.getInstance(conf);
		job.setJarByClass(ImportFromFile.class);
		job.setMapperClass(ImportMapper.class);
		job.setOutputFormatClass(TableOutputFormat.class);
		job.getConfiguration().set(TableOutputFormat.OUTPUT_TABLE, table);
		job.setOutputKeyClass(ImmutableBytesWritable.class);
		job.setOutputValueClass(Writable.class);
		job.setNumReduceTasks(0); 
		FileInputFormat.addInputPath(job, new Path(input));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
