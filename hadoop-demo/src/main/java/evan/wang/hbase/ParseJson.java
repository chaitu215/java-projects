package evan.wang.hbase;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.IdentityTableReducer;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ParseJson {

	private static final Log LOG = LogFactory.getLog(ParseJson.class);

	public static final String NAME = "ParseJson";

	public enum Counters {
		ROWS, COLS, ERROR, VALID
	}

	/**
	 * Implements the <code>Mapper</code> that reads the data and extracts the
	 * required information.
	 */
	static class ParseMapper extends TableMapper<ImmutableBytesWritable, Mutation> {

		private JSONParser parser = new JSONParser();
		private byte[] columnFamily = null;

		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			columnFamily = Bytes.toBytes(context.getConfiguration().get("conf.columnfamily"));
		}

		/**
		 * Maps the input.
		 *
		 * @param row
		 *            The row key.
		 * @param columns
		 *            The columns of the row.
		 * @param context
		 *            The task context.
		 * @throws java.io.IOException
		 *             When mapping the input fails.
		 */
		@Override
		public void map(ImmutableBytesWritable row, Result columns, Context context) throws IOException {
			context.getCounter(Counters.ROWS).increment(1);
			String value = null;
			try {
				Put put = new Put(row.get());
				for (Cell cell : columns.listCells()) {
					context.getCounter(Counters.COLS).increment(1);
					value = Bytes.toStringBinary(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
					JSONObject json = (JSONObject) parser.parse(value);
					for (Object key : json.keySet()) {
						Object val = json.get(key);
						put.addColumn(columnFamily, Bytes.toBytes(key.toString()), Bytes.toBytes(val.toString()));
					}
				}
				context.write(row, put);
				context.getCounter(Counters.VALID).increment(1);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Error: " + e.getMessage() + ", Row: " + Bytes.toStringBinary(row.get()) + ", JSON: " + value);
				context.getCounter(Counters.ERROR).increment(1);
			}
		}
		
		
		 /*
		    {
		      "updated": "Mon, 14 Sep 2009 17:09:02 +0000",
		      "links": [{
		        "href": "http://www.webdesigndev.com/",
		        "type": "text/html",
		        "rel": "alternate"
		      }],
		      "title": "Web Design Tutorials | Creating a Website | Learn Adobe
		          Flash, Photoshop and Dreamweaver",
		      "author": "outernationalist",
		      "comments": "http://delicious.com/url/e104984ea5f37cf8ae70451a619c9ac0",
		      "guidislink": false,
		      "title_detail": {
		        "base": "http://feeds.delicious.com/v2/rss/recent?min=1&count=100",
		        "type": "text/plain",
		        "language": null,
		        "value": "Web Design Tutorials | Creating a Website | Learn Adobe
		            Flash, Photoshop and Dreamweaver"
		      },
		      "link": "http://www.webdesigndev.com/",
		      "source": {},
		      "wfw_commentrss": "http://feeds.delicious.com/v2/rss/url/
		          e104984ea5f37cf8ae70451a619c9ac0",
		      "id": "http://delicious.com/url/
		          e104984ea5f37cf8ae70451a619c9ac0#outernationalist"
		    }
		 */

	}



	/**
	 * Main entry point.
	 *
	 * @param args
	 *            The command line parameters.
	 * @throws Exception
	 *             When running the job fails.
	 */
	public static void main(String[] args) throws Exception {
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		conf.set("hbase.zookeeper.quorum", "192.168.10.134,192.168.10.135,192.168.10.136");
		conf.set("conf.debug", "false");
		String input =  "testtable";
		String output = "testtable2";
		String column = "data:json";
		
		ImportFromFile.createTable(conf, output, column.split(":")[0]);

		Scan scan = new Scan();
		if (column != null) {
			byte[][] colkey = KeyValue.parseColumn(Bytes.toBytes(column));
			if (colkey.length > 1) {
				scan.addColumn(colkey[0], colkey[1]);
				conf.set("conf.columnfamily", Bytes.toStringBinary(colkey[0]));
				conf.set("conf.columnqualifier", Bytes.toStringBinary(colkey[1]));
			} else {
				scan.addFamily(colkey[0]);
				conf.set("conf.columnfamily", Bytes.toStringBinary(colkey[0]));
			}
		}

		Job job = Job.getInstance(conf, "Parse data in " + input + ", write to " + output);
		job.setJarByClass(ParseJson.class);
		TableMapReduceUtil.initTableMapperJob(input, scan, ParseMapper.class, ImmutableBytesWritable.class, Put.class, job);
		TableMapReduceUtil.initTableReducerJob(output, IdentityTableReducer.class, job);
		job.setNumReduceTasks(0);
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
