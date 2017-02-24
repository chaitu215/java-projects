package evan.wang.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class MrUtil {

	/**
	 * 删除指定目录
	 *
	 * @param conf
	 * @param dirPath
	 * @throws IOException
	 */
	public static void deleteDir(Configuration conf, String dirPath) throws IOException {
		FileSystem fs = FileSystem.get(conf);
		Path targetPath = new Path(dirPath);
		if (fs.exists(targetPath)) {
			boolean delResult = fs.delete(targetPath, true);
			if (delResult) {
				System.out.println(targetPath + " has been deleted sucessfullly.");
			} else {
				System.out.println(targetPath + " deletion failed.");
			}
		}
	}
}
