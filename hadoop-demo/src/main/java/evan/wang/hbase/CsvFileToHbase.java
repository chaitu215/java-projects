package evan.wang.hbase;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.BufferedMutatorParams;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import com.csvreader.CsvReader;

/**
 * csv文件存入Hbase
 * @author: wangsy
 * @date: 2017年6月20日
 */
public class CsvFileToHbase {
	private static Configuration configuration;
	private static String TABLE_NAME = "stock";
	private static String COLUMN_FAMILY = "cf";

	static {
		configuration = HBaseConfiguration.create();
		configuration.set("hbase.zookeeper.property.clientPort", "2181");
		configuration.set("hbase.zookeeper.quorum", "192.168.10.134,192.168.10.135,192.168.10.136");
	}

	public static void createTable(Connection connection) throws IOException {
		System.out.println("start create table ......");
		try (Admin admin = connection.getAdmin()) {
			HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(TABLE_NAME));
			tableDescriptor.addFamily(new HColumnDescriptor(COLUMN_FAMILY).setCompressionType(Algorithm.GZ));
			if (admin.tableExists(tableDescriptor.getTableName())) {
				admin.disableTable(tableDescriptor.getTableName());
				admin.deleteTable(tableDescriptor.getTableName());
			}
			admin.createTable(tableDescriptor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void putData(Connection connection) throws IOException {
		long startTime = System.currentTimeMillis();
		String filePath = "E:\\spring-sts-workspace\\java-examples\\hadoop-demo\\src\\main\\java\\evan\\wang\\hbase\\股票资产.csv";
		CsvReader csv = new CsvReader(filePath, ',', Charset.forName("GBK"));
		csv.setSafetySwitch(false);
		csv.setUseTextQualifier(true); //读的时候内容是被默认引用符(")包围的
		TableName tableName = TableName.valueOf(TABLE_NAME);
		try (Table table = connection.getTable(tableName);) {
			int index = 0;
			String[] columnTitles = null;
			List<Put> puts_list = new ArrayList<>();
			while (csv.readRecord()) {
				String[] values = csv.getValues();// 读取一行，表头
				if (values == null) {
					continue;
				}
				if (values.length == 0) {
					continue;
				}
				if (index == 0) {
					columnTitles = values;
					System.out.println("表头: " + Arrays.toString(columnTitles));
					System.out.println(new String(Bytes.toBytes(columnTitles[0])));
				} else {
					String rowkey = "000001_" + System.currentTimeMillis() + "_" + String.format("%010d",index ); // <userId>-<date>-<messageId>
					Put put = new Put(Bytes.toBytes(rowkey));
					for (int i = 0; i < values.length; i++) {
						String value = values[i];
						if (i < columnTitles.length) {
							put.addColumn(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(columnTitles[i]),
									Bytes.toBytes(value));
						}
					}
					puts_list.add(put);
				}
				if (index % 200 == 0) {
					System.out.println(String.format("插入第%d条数据", index));
					if(puts_list.size()>0){
						table.put(puts_list);
						puts_list.clear();
					}
				}
				index++;
			}
			if(puts_list.size()>0){
				table.put(puts_list);
				puts_list.clear();
			}
			System.out.println(String.format("总处理%d条数据", index));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("耗时： "+ (System.currentTimeMillis() - startTime) + "ms");
	}
	
	
	/**
	 * 写缓存，
	 * <property>
          <name>hbase.client.write.buffer</name>
          <value>2097152</value>
          <source>hbase-default.xml</source>
       </property>
	 * 
	 */
	public static void writeBuffePutData(Connection connection) throws IOException {
		long startTime = System.currentTimeMillis();
		String filePath = "E:\\spring-sts-workspace\\java-examples\\hadoop-demo\\src\\main\\java\\evan\\wang\\hbase\\股票资产.csv";
		CsvReader csv = new CsvReader(filePath, ',', Charset.forName("GBK"));
		csv.setSafetySwitch(false);
		csv.setUseTextQualifier(false); // 读的时候内容是不被默认引用符(")包围的
		TableName tableName = TableName.valueOf(TABLE_NAME);
		try (BufferedMutator mutator = connection.getBufferedMutator(new BufferedMutatorParams(tableName).writeBufferSize(209715200))) {
			System.out.println("hbase.client.write.buffer : " + mutator.getWriteBufferSize()); // default 2097152  2M
			int index = 0;
			String[] columnTitles = null;
			List<Put> puts_list = new ArrayList<>();
			while (csv.readRecord()) {
				String[] values = csv.getValues();// 读取一行，表头
				if (values == null) {
					continue;
				}
				if (values.length == 0) {
					continue;
				}
				if (index == 0) {
					columnTitles = values;
					System.out.println("表头: " + Arrays.toString(columnTitles));
					System.out.println(new String(Bytes.toBytes(columnTitles[0])));
				} else {
					String rowkey = "000001_" + System.currentTimeMillis() + "_" + String.format("%010d",index ); // <userId>-<date>-<messageId>
					Put put = new Put(Bytes.toBytes(rowkey));
					for (int i = 0; i < values.length; i++) {
						String value = values[i];
						if (i < columnTitles.length) {
							put.addColumn(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(columnTitles[i]),
									Bytes.toBytes(value));
						}
					}
					mutator.mutate(put);
					puts_list.add(put);
				}
				index++;
			}
			System.out.println(String.format("总处理%d条数据", index));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("耗时： "+ (System.currentTimeMillis() - startTime) + "ms");
	}
	
	
	@Test
	public void getData() throws IOException {
		Connection connection = ConnectionFactory.createConnection(configuration);
		System.out.println("start get data ......");
		TableName tableName = TableName.valueOf(TABLE_NAME);
		try (Table table = connection.getTable(tableName);) {
			Get get = new Get(Bytes.toBytes("00000114980333669681"));
			get.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("stock_name")); //查询列
			Result result = table.get(get);
			System.out.println("result: " + result);
            byte[] value = result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("stock_name"));
            System.out.println("value: " + Bytes.toString(value));
			/*
			 List<Cell> list = result.listCells();
			 HbaseClientTest.printList(list);
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end get data ......\n");
	}
	
	
	public static void scanData(Connection connection) throws IOException {
		System.out.println("start scan table ......");
		TableName tableName = TableName.valueOf(TABLE_NAME);
		Scan scan = new Scan();
		//每一个next()调用都会为每行数据生成一个单独的RPC请求，设置每次缓存请求行大小。
		scan.setCaching(200);
		//scan.setBatch(10);
		try (Table table = connection.getTable(tableName); ResultScanner rs = table.getScanner(scan);) {
			for (Result result : rs) {
				List<Cell> list = result.listCells();
				System.out.println(new String(result.getRow()) + " ======================= ");
				HbaseClientTest.printList(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end scan table ......\n");
	}
	
	public static void rowFilter(Connection connection) throws IOException {
		System.out.println("start row filter ......");
		TableName tableName = TableName.valueOf(TABLE_NAME);
		Scan scan1 = new Scan();
		scan1.setCaching(200);
		scan1.addColumn(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes("stock_name"));
		scan1.addColumn(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes("stock_code"));
		scan1.addColumn(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes("total_assets"));
		Filter filter1 = new RowFilter(CompareOp.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes("00000114980346030161802")));
		scan1.setFilter(filter1);
		try(Table table = connection.getTable(tableName); ResultScanner scanner1 = table.getScanner(scan1)) {
			for(Result result : scanner1){
				List<Cell> list = result.listCells();
				System.out.println(new String(result.getRow()) + " ======================= ");
				HbaseClientTest.printList(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end row filter ......\n");
	}
	
	public static void columnFilter(Connection connection) throws IOException {
		System.out.println("start column filter ......");
		TableName tableName = TableName.valueOf(TABLE_NAME);
		Scan scan1 = new Scan();
		scan1.setCaching(200);
		Filter filter1 = new QualifierFilter(CompareOp.EQUAL, new RegexStringComparator("stock.*"));
		scan1.setFilter(filter1);
		try(Table table = connection.getTable(tableName); ResultScanner scanner1 = table.getScanner(scan1)) {
			for(Result result : scanner1){
				List<Cell> list = result.listCells();
				System.out.println(new String(result.getRow()) + " ======================= ");
				HbaseClientTest.printList(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end column filter ......\n");
	}
	

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Connection connection = ConnectionFactory.createConnection(configuration);
		createTable(connection);
		putData(connection);
		//scanData(connection);
		//rowFilter(connection);
		//columnFilter(connection);
		connection.close();
	}

}
