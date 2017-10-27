package evan.wang.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import evan.wang.mapreduce.MrUtil;

/**
 * 
 * @author: wangsy
 * @date: 2017年7月6日
 */
public class AnalyzeData {

	public static final String NAME = "AnalyzeData";

	public enum Counters {
		ROWS, COLS, ERROR, VALID
	}

	static class AnalyzeMapper extends TableMapper<Text, IntWritable> {
		private JSONParser parser = new JSONParser();
		private IntWritable ONE = new IntWritable(1);

		@Override
		protected void map(ImmutableBytesWritable row, Result columns,
				Mapper<ImmutableBytesWritable, Result, Text, IntWritable>.Context context)
				throws IOException, InterruptedException {
			context.getCounter(Counters.ROWS).increment(1);
			String value = null;
			try {
				for (Cell cell : columns.listCells()) {
					context.getCounter(Counters.COLS).increment(1);
					value = Bytes.toStringBinary(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
					JSONObject json = (JSONObject) parser.parse(value);
					String author = (String) json.get("author");
					if (context.getConfiguration().get("conf.debug") != null) {
						System.out.println("Author: " + author);
					}
					context.write(new Text(author), ONE);
					context.getCounter(Counters.VALID).increment(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Row: " + Bytes.toStringBinary(row.get()) + ", JSON: " + value);
				context.getCounter(Counters.ERROR).increment(1);
			}
		}
	}

	static class AnalyzeReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			int count = 0;
			for (IntWritable one : values){
				count++;
			}
			if (context.getConfiguration().get("conf.debug") != null) {
				System.out.println("Author: " + key.toString() + ", Count: " + count);
			}
			context.write(key, new IntWritable(count));
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		conf.set("hbase.zookeeper.quorum", "192.168.10.134,192.168.10.135,192.168.10.136");
		conf.set("conf.debug", "true");
		// hbase表
		String table = "testtable";
		// 列族:列名
		String column = "data:json";
		// 输入文件
		String output = "/user/wangsy/hbase-book/analyze_data";
		
		MrUtil.deleteDir(conf, output);

		Scan scan = new Scan();
		if (column != null) {
			byte[][] colkey = KeyValue.parseColumn(Bytes.toBytes(column));
			if (colkey.length > 1) {
				scan.addColumn(colkey[0], colkey[1]);
			} else {
				scan.addFamily(colkey[0]);
			}
		}

		Job job = Job.getInstance(conf, "Analyze data in " + table);
		job.setJarByClass(AnalyzeData.class);
		TableMapReduceUtil.initTableMapperJob(table, scan, AnalyzeMapper.class, Text.class, IntWritable.class, job);
		job.setReducerClass(AnalyzeReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setNumReduceTasks(1);
		FileOutputFormat.setOutputPath(job, new Path(output));

		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}
}
