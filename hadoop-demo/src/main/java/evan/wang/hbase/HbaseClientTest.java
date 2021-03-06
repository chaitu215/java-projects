package evan.wang.hbase;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * hbase操作
 *
 * @author: wangshengyong
 * @date: 2016年11月16日
 */
public class HbaseClientTest {
	// 表名
	private static final String TABLE_NAME = "blog";
	// 列族
	private static final String CF_ARTICLE = "article";
	// new列族
	private static final String CF_AUTH = "auth";

	public static Configuration configuration;

	@BeforeClass
	public static void before() {
		configuration = HBaseConfiguration.create();
		configuration.set("hbase.zookeeper.property.clientPort", "2181");
		configuration.set("hbase.zookeeper.quorum", "192.168.10.134,192.168.10.135,192.168.10.136");
		//configuration.set("hbase.master", "192.168.10.132:600000");
	}

	/**
	 * 创建表
	 * 
	 * @throws IOException
	 */
	@Test
	public void createTable() throws IOException {
		Connection connection = ConnectionFactory.createConnection(configuration);
		System.out.println("start create table ......");
		try (Admin admin = connection.getAdmin()) {
			HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(TABLE_NAME));
			tableDescriptor.addFamily(new HColumnDescriptor(CF_ARTICLE).setCompressionType(Algorithm.NONE));
			if (admin.tableExists(tableDescriptor.getTableName())) {
				admin.disableTable(tableDescriptor.getTableName());
				admin.deleteTable(tableDescriptor.getTableName());
			}
			admin.createTable(tableDescriptor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end create table ......\n");
	}

	/**
	 * 操作表
	 * 
	 * @throws IOException
	 */
	@Test
	public void modifySchema() throws IOException {
		Connection connection = ConnectionFactory.createConnection(configuration);
		System.out.println("start modify table ......");
		try (Admin admin = connection.getAdmin()) {
			TableName tableName = TableName.valueOf(TABLE_NAME);
			if (!admin.tableExists(tableName)) {
				System.out.println("Table does not exist.");
				System.exit(-1);
			}

			HTableDescriptor tableDescriptor = admin.getTableDescriptor(tableName);

			// Update existing table
			HColumnDescriptor newColumn = new HColumnDescriptor(CF_AUTH);
			newColumn.setCompactionCompressionType(Algorithm.GZ);
			newColumn.setMaxVersions(HConstants.ALL_VERSIONS);
			tableDescriptor.addFamily(newColumn);
			admin.modifyTable(tableName, tableDescriptor);

			// Update existing column family
			HColumnDescriptor existingColumn = new HColumnDescriptor(CF_ARTICLE);
			existingColumn.setCompactionCompressionType(Algorithm.GZ);
			existingColumn.setMaxVersions(HConstants.ALL_VERSIONS);
			tableDescriptor.modifyFamily(existingColumn);
			admin.modifyTable(tableName, tableDescriptor);

			// Disable an existing table
			// admin.disableTable(tableName);

			// Delete an existing column family
			// admin.deleteColumn(tableName, CF_DEFAULT.getBytes("UTF-8"));

			// Delete a table (Need to be disabled first)
			// admin.deleteTable(tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end modify table ......\n");
	}

	/**
	 * 添加数据 tableName+RowKey+ColumnKey+Timestamp=>value
	 * 
	 * @throws IOException
	 */
	@Test
	public void putData() throws IOException {
		Connection connection = ConnectionFactory.createConnection(configuration);
		System.out.println("start put data ......");
		TableName tableName = TableName.valueOf(TABLE_NAME);
		try (Table table = connection.getTable(tableName);) {
			// row key
			Put put1 = new Put(Bytes.toBytes("0000000000000001"));
			put1.addColumn(Bytes.toBytes(CF_ARTICLE), Bytes.toBytes("title"), Bytes.toBytes("Head First Hbase"));
			put1.addColumn(Bytes.toBytes(CF_ARTICLE), Bytes.toBytes("tags"), Bytes.toBytes("Hadoop Habse NoSql"));
			put1.addColumn(Bytes.toBytes(CF_ARTICLE), Bytes.toBytes("content"), Bytes.toBytes("Use Apache HBase™ when you need random, realtime read/write access to your Big Data"));
			put1.addColumn(Bytes.toBytes(CF_AUTH), Bytes.toBytes("name"), Bytes.toBytes("wangsy"));
			put1.addColumn(Bytes.toBytes(CF_AUTH), Bytes.toBytes("enname"), Bytes.toBytes("evan wang"));

			// row key
			Put put2 = new Put(Bytes.toBytes("0000000000000002"));
			put2.addColumn(Bytes.toBytes(CF_ARTICLE), Bytes.toBytes("title"), Bytes.toBytes("Apache Spark"));
			put2.addColumn(Bytes.toBytes(CF_ARTICLE), Bytes.toBytes("tags"), Bytes.toBytes("Spark bigData Hadoop"));
			put2.addColumn(Bytes.toBytes(CF_ARTICLE), Bytes.toBytes("content"),Bytes.toBytes("Apache Spark™ is a fast and general engine for large-scale data processing"));
			put2.addColumn(Bytes.toBytes(CF_AUTH), Bytes.toBytes("name"), Bytes.toBytes("zhangsan"));
			put2.addColumn(Bytes.toBytes(CF_AUTH), Bytes.toBytes("enname"), Bytes.toBytes("san er"));

			table.put(put1);
			table.put(put2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end put data ......\n");
	}

	/**
	 * 获取一条数据
	 *
	 * @param connection
	 * @throws IOException
	 */
	@Test
	public void getData() throws IOException {
		Connection connection = ConnectionFactory.createConnection(configuration);
		System.out.println("start get data ......");
		TableName tableName = TableName.valueOf(TABLE_NAME);
		try (Table table = connection.getTable(tableName);) {
			Get get = new Get(Bytes.toBytes("0000000000000001"));
			Result result = table.get(get);
			List<Cell> list = result.listCells();
			printList(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end get data ......\n");
	}

	/**
	 * 扫描数据
	 *
	 * @param connection
	 * @throws IOException
	 */
	@Test
	public void scanData() throws IOException {
		Connection connection = ConnectionFactory.createConnection(configuration);
		System.out.println("start scan table ......");
		TableName tableName = TableName.valueOf(TABLE_NAME);
		try (Table table = connection.getTable(tableName); ResultScanner rs = table.getScanner(new Scan());) {
			for (Result result : rs) {
				List<Cell> list = result.listCells();
				printList(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end scan table ......\n");
	}

	/**
	 * 更新数据
	 *
	 * @param connection
	 * @throws IOException
	 */
	@Test
	public void updateData() throws IOException {
		Connection connection = ConnectionFactory.createConnection(configuration);
		System.out.println("start update data ......");
		TableName tableName = TableName.valueOf(TABLE_NAME);
		try (Table table = connection.getTable(tableName);) {
			Put put = new Put(Bytes.toBytes("0000000000000001"));
			put.addColumn(Bytes.toBytes(CF_ARTICLE), Bytes.toBytes("title"),
					Bytes.toBytes("Head First Hbase Second Edition"));
			table.put(put);
			getData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end update data ......\n");
	}

	/**
	 * 删除数据
	 *
	 * @param connection
	 * @throws IOException
	 */
	@Test
	public void deleteData() throws IOException {
		Connection connection = ConnectionFactory.createConnection(configuration);
		System.out.println("start delete data ......");
		TableName tableName = TableName.valueOf(TABLE_NAME);
		try (Table table = connection.getTable(tableName);) {
			Delete delete = new Delete(Bytes.toBytes("0000000000000001"));
			table.delete(delete);
			getData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end delete data ......\n");
	}

	public static void printList(List<Cell> list) {
		if (list != null) {
			for (Cell cell : list) {
				// 列族名
				String columnFamily = new String(CellUtil.cloneFamily(cell));
				// 列名
				String column = new String(CellUtil.cloneQualifier(cell));
				// 值
				String value = new String(CellUtil.cloneValue(cell));
				System.out.printf("%s:%s : %s \n", columnFamily, column, value);
			}
		} else {
			System.out.println("no data!");
		}
	}

}
