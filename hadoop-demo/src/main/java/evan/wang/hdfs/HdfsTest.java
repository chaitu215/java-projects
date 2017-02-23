package evan.wang.hdfs;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * HDFS操作
 *
 * @author: wangshengyong
 * @date: 2016年11月11日
 */
public class HdfsTest {
    private static final String LOCAL_PATH = "D:/test";
    //private static final String HDFS_PATH = "hdfs://192.168.1.222:9000/user/wangsy/temp/";
    private static final String HDFS_PATH = "/user/wangsy/temp/";
    private FileSystem hdfs;

    /**
     * 初始化配置
     *
     * @throws Exception
     */
    @Before
    public void before() throws Exception {
        Configuration conf = new Configuration();
        //指定操作hadoop的用户, 或者HDFS上保证其它用户有写入的权限： hdfs dfs -chmod +w /user/wangsy/temp
        System.setProperty("HADOOP_USER_NAME", "wangsy");
        hdfs = FileSystem.get(conf);
    }

    /**
     * 创建目录
     */
    @Test
    public void createDir() throws Exception {
        hdfs.mkdirs(new Path(HDFS_PATH + "logs"));
        System.out.println("create dir success");
    }


    /**
     * 删除目录
     *
     * @throws Exception
     */
    @Test
    public void deleteDir() throws Exception {
        hdfs.delete(new Path(HDFS_PATH + "logs"), true);
        System.out.println("create dir success");
    }

    /**
     * 上传本地文件
     */
    @Test
    public void uploadFile() throws Exception {
        Path src = new Path(LOCAL_PATH, "log.log");
        Path dst = new Path(HDFS_PATH);
        hdfs.copyFromLocalFile(false, true, src, dst);
        System.out.println("upload success!");
    }

    /**
     * 创建文件
     */
    @Test
    public void createFile() throws Exception {
        byte[] buff = "hello world!".getBytes();
        Path dst = new Path(HDFS_PATH + "hello.txt");
        try (FSDataOutputStream outputStream = hdfs.create(dst)) {
            if (outputStream != null) {
                outputStream.write(buff, 0, buff.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileStatus files[] = hdfs.listStatus(dst);
        for (FileStatus file : files) {
            System.out.println(file.getPath());
        }
        System.out.println("create success!");
    }

    /**
     * 删除文件
     *
     * @throws Exception
     */
    @Test
    public void deleteFile() throws Exception {
        hdfs.delete(new Path(HDFS_PATH, "log.log"), false);
        System.out.println("delete success!");
    }


    /**
     * 查看HDFS文件的最后修改时间
     */
    @Test
    public void testgetModifyTime() throws Exception {
        Path dst = new Path(HDFS_PATH);
        FileStatus files[] = hdfs.listStatus(dst);
        for (FileStatus file : files) {
            System.out.println(file.getPath() + "\t"
                    + new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(new Date(file.getModificationTime())));
        }
    }


    /**
     * 查看某个文件在HDFS集群的位置
     */
    @Test
    public void testFileBlockLocation() throws Exception {
        Path dst = new Path(HDFS_PATH + "log.log");
        FileStatus fileStatus = hdfs.getFileStatus(dst);
        BlockLocation[] blockLocations = hdfs.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
        for (BlockLocation block : blockLocations) {
            System.out.println(Arrays.toString(block.getHosts()) + "\t"
                    + Arrays.toString(block.getNames()));
        }
    }

    /**
     * 获取HDFS集群上所有节点名称
     */
    @Test
    public void testGetHostName() throws Exception {
        DistributedFileSystem dhdfs = (DistributedFileSystem) hdfs;
        DatanodeInfo[] dataNodeStats = dhdfs.getDataNodeStats();
        for (DatanodeInfo dataNode : dataNodeStats) {
            System.out.println(dataNode.getHostName() + "\t"
                    + dataNode.getName());
        }
    }


}
