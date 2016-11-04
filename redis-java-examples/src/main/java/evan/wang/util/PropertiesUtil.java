package evan.wang.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertiesUtil {
	private static Properties properties = new Properties();

	static {
		final List<File> propertiesFiles = new ArrayList<>();
		// 获取classpath目录下所有的properties文件
		try {
			Files.walkFileTree(
					Paths.get(PropertiesUtil.class.getClassLoader()
							.getResource("").toURI()),
					new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file,
								BasicFileAttributes attrs) throws IOException {
							System.out.println(file);
							if (file.getFileName().toString().endsWith(".properties")) {
								propertiesFiles.add(file.toFile());
							}
							return FileVisitResult.CONTINUE;
						}
					});
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		for (File file : propertiesFiles) {
			try (InputStream fis = new FileInputStream(file)) {
				properties.load(fis);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	// 根据指定key获取值
	public static String getValue(String key) {
		return properties.getProperty(key);
	}

	public static void main(String[] args) {
		System.out.println(PropertiesUtil.getValue("jdbc.url"));
	}

}
