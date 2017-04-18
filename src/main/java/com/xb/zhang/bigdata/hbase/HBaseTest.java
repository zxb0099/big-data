package com.xb.zhang.bigdata.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
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
import org.apache.hadoop.hbase.util.Bytes;

/**
 * hbase测试类
 * @author zhangxianbin
 *
 */
public class HBaseTest {

	public static Configuration configuration;
	public static Connection connection;
	public static Admin admin;

	public static void main(String[] args) throws IOException {
		println("Start...");
		// 第一步 建表		
		createTable("STUDENT_1", new String[]{ "INFO", "COURSE" });
		
		// 第二步 插入数据		
		// 添加第一行数据
		insert("STUDENT_1", "龙千言", "INFO", "AGE", "20");
        insert("STUDENT_1", "龙千言", "INFO", "SEX", "boy");
        insert("STUDENT_1", "龙千言", "COURSE", "CHINA", "97");
        insert("STUDENT_1", "龙千言", "COURSE", "MATH", "128");
        insert("STUDENT_1", "龙千言", "COURSE", "ENGLISH", "85");
        // 添加第二行数据
        insert("STUDENT_1", "高亚男", "INFO", "AGE", "19");
        insert("STUDENT_1", "高亚男", "INFO", "SEX", "boy");
        insert("STUDENT_1", "高亚男", "COURSE", "CHINA","90");
        insert("STUDENT_1", "高亚男", "COURSE", "MATH","120");
        insert("STUDENT_1", "高亚男", "COURSE", "ENGLISH","90");
        // 添加第三行数据
        insert("STUDENT_1", "万山红", "INFO", "AGE", "18");
        insert("STUDENT_1", "万山红", "INFO", "SEX","girl");
        insert("STUDENT_1", "万山红", "COURSE", "CHINA","100");
        insert("STUDENT_1", "万山红", "COURSE", "MATH","100");
        insert("STUDENT_1", "万山红", "COURSE", "ENGLISH","99");

        // 第三步：获取一条数据
        System.out.println("**************获取一条(龙千言)数据*************");
        getData("STUDENT_1", "龙千言");
        
        // 第四步：获取所有数据
        System.out.println("**************获取所有数据***************");
        getAllData("STUDENT_1");
		
//        // 第五步：删除一条数据
//        System.out.println("************删除一条(龙千言)数据************");
//        delete("STUDENT_1", "龙千言", null, null);
//        getAllData("STUDENT_1");
//        
//        // 第六步：删除多条数据
//        System.out.println("**************删除多条数据***************");
//        String rows[] = new String[] { "qingqing","xiaoxue" };
//        delMultiRows("STUDENT_1", rows);
//        getAllData("STUDENT_1");
//        
//        // 第七步：删除数据库
//        System.out.println("***************删除数据库表**************");
//        deleteTable("STUDENT_1");
//        System.out.println("表"+"STUDENT_1"+"存在吗？"+isExist("STUDENT_1"));
     
		listTables();
		
		println("End...");
	}

	/**
	 * 初始化链接
	 */
	public static void init() {
		configuration = HBaseConfiguration.create();
		configuration.set("hbase.zookeeper.property.clientPort", "2181");
		configuration.set("hbase.zookeeper.quorum", "centos01");
		configuration.set("hbase.cluster.distributed", "true");
		configuration.set("hbase.rootdir", "hdfs://centos01:9000/hbase");

		try {
			connection = ConnectionFactory.createConnection(configuration);
			admin = connection.getAdmin();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭连接
	 */
	public static void close() {
		try {
			if (null != admin) {
				admin.close();
			}
			if (null != connection) {
				connection.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建表
	 * 
	 * @param tableName 表名
	 * @param family 列族列表
	 * @throws IOException
	 */
	public static void createTable(String tableName, String[] cols) throws IOException {
		init();
		TableName tName = TableName.valueOf(tableName);
		if (admin.tableExists(tName)) {
			println(tableName + " exists.");
		} else {
			HTableDescriptor hTableDesc = new HTableDescriptor(tName);
			for (String col : cols) {
				HColumnDescriptor hColumnDesc = new HColumnDescriptor(col);
				hTableDesc.addFamily(hColumnDesc);
			}
			admin.createTable(hTableDesc);
		}

		close();
	}

	/**
	 * 删除表
	 * 
	 * @param tableName 表名称
	 * @throws IOException
	 */
	public static void deleteTable(String tableName) throws IOException {
		init();
		TableName tName = TableName.valueOf(tableName);
		if (admin.tableExists(tName)) {
			admin.disableTable(tName);
			admin.deleteTable(tName);
		} else {
			println(tableName + " not exists.");
		}
		close();
	}

	/**
	 * 查看已有表
	 * 
	 * @throws IOException
	 */
	public static void listTables() {
		init();
		HTableDescriptor hTableDescriptors[] = null;
		try {
			hTableDescriptors = admin.listTables();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (HTableDescriptor hTableDescriptor : hTableDescriptors) {
			println(hTableDescriptor.getNameAsString());
		}
		close();
	}

	/**
	 * 插入单行
	 * 
	 * @param tableName 表名称
	 * @param rowKey RowKey
	 * @param colFamily 列族
	 * @param col 列
	 * @param value 值
	 * @throws IOException
	 */
	public static void insert(String tableName, String rowKey, String colFamily, String col, String value) throws IOException {
		init();
		Table table = connection.getTable(TableName.valueOf(tableName));
		Put put = new Put(Bytes.toBytes(rowKey));
		put.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col), Bytes.toBytes(value));
		table.put(put);

		/*
		 * 批量插入 List<Put> putList = new ArrayList<Put>(); puts.add(put); table.put(putList);
		 */

		table.close();
		close();
	}
	
	public static void delMultiRows(String tableName, String[] rows) throws IOException {
		init();

		if (!admin.tableExists(TableName.valueOf(tableName))) {
			println(tableName + " not exists.");
		} else {
			Table table = connection.getTable(TableName.valueOf(tableName));
			List<Delete> delList = new ArrayList<Delete>();
	        for (String row : rows) {
	            Delete del = new Delete(Bytes.toBytes(row));
	            delList.add(del);
	        }
			table.delete(delList);
			table.close();
		}
		close();
	}

	public static void delete(String tableName, String rowKey, String colFamily, String col) throws IOException {
		init();

		if (!admin.tableExists(TableName.valueOf(tableName))) {
			println(tableName + " not exists.");
		} else {
			Table table = connection.getTable(TableName.valueOf(tableName));
			Delete del = new Delete(Bytes.toBytes(rowKey));
			if (colFamily != null) {
				del.addFamily(Bytes.toBytes(colFamily));
			}
			if (colFamily != null && col != null) {
				del.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
			}
			/*
			 * 批量删除 List<Delete> deleteList = new ArrayList<Delete>(); deleteList.add(delete); table.delete(deleteList);
			 */
			table.delete(del);
			table.close();
		}
		close();
	}

	/**
	 * 根据RowKey获取数据
	 * 
	 * @param tableName 表名称
	 * @param rowKey RowKey名称
	 * @param colFamily 列族名称
	 * @param col 列名称
	 * @throws IOException
	 */
	public static void getData(String tableName, String rowKey, String colFamily, String col) throws IOException {
		init();
		Table table = connection.getTable(TableName.valueOf(tableName));
		Get get = new Get(Bytes.toBytes(rowKey));
		if (colFamily != null) {
			get.addFamily(Bytes.toBytes(colFamily));
		}
		if (colFamily != null && col != null) {
			get.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
		}
		Result result = table.get(get);
		showCell(result);
		table.close();
		close();
	}
	
	/**
	 * 获取全表数据
	 * 
	 * @param tableName 表名称
	 * @throws IOException
	 */
	public static void getAllData(String tableName) throws IOException {
		init();
		Table table = connection.getTable(TableName.valueOf(tableName));
		Scan scan = new Scan();
		ResultScanner results = table.getScanner(scan);
		if(results != null){
			for (Result result : results) {
				showCell(result);
			}
		}		
		table.close();
		close();
	}

	/**
	 * 根据RowKey获取信息
	 * 
	 * @param tableName
	 * @param rowKey
	 * @throws IOException
	 */
	public static void getData(String tableName, String rowKey) throws IOException {
		getData(tableName, rowKey, null, null);
	}
	
    //判断表是否存在
	public static boolean isExist(String tableName) throws IOException {
		init();
		boolean result = admin.tableExists(TableName.valueOf(tableName));		
		close();
		return result;
    }

	/**
	 * 格式化输出
	 * 
	 * @param result
	 */
	public static void showCell(Result result) {
		Cell[] cells = result.rawCells();
		for (Cell cell : cells) {
			println("RowName: " + new String(CellUtil.cloneRow(cell)) + " ");
			println("Timetamp: " + cell.getTimestamp() + " ");
			println("column Family: " + new String(CellUtil.cloneFamily(cell)) + " ");
			println("row Name: " + new String(CellUtil.cloneQualifier(cell)) + " ");
			println("value: " + new String(CellUtil.cloneValue(cell)) + " ");
		}
	}

	/**
	 * 打印
	 * 
	 * @param obj 打印对象
	 */
	private static void println(Object obj) {
		System.out.println(obj);
	}
}
