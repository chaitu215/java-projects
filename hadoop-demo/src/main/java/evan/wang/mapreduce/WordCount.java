package evan.wang.mapreduce;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 统计单词示例
 *
 * @author: wangshengyong
 * @date: 2016年11月10日
 */
public class WordCount {
	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		@Override
		protected void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context)
				throws IOException, InterruptedException {
			System.out.println("key: " + key);
			System.out.println("value: " + value);

			// 分隔字符串
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken());
				context.write(word, one);
			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Reducer<Text, IntWritable, Text, IntWritable>.Context context)
						throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			System.out.println("key: " + key);
			System.out.println("value: " + sum);
			result.set(sum);
			context.write(key, result);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String inputPath = "/user/wangsy/data/input";
		String outputPath = "/user/wangsy/data/output";

		// if (args.length < 1) {
		// System.err.println("please set input path");
		// System.exit(1);
		// } else {
		// inputPath = args[0];
		// }
		// if (args.length < 2) {
		// System.err.println("please set output path");
		// System.exit(1);
		// } else {
		// outputPath = args[1];
		// }

		Configuration conf = new Configuration();

		// 先删除output目录
		MrUtil.deleteDir(conf, args[1]);

		Job job = Job.getInstance(conf);
		job.setJarByClass(WordCount.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

		// 提交后，运行：hadoop jar hadoop-demo.jar evan.wang.mapreduce.WordCount input
		// output

	}

}
