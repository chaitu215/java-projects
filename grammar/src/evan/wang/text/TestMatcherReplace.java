package evan.wang.text;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则提取并替换
 * @author: wangsy
 * @date: 2017年2月21日
 */
public class TestMatcherReplace {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		String sql = "select  t.[symbol], cast(wer.[ymd] as int) + 1 as wert, [price_open], [price_high], [price_low], [price_close], [volumn], [price_adj_close] from  [CSV股票3333] t where [price_open] > 2.3";
//		Pattern pattern = Pattern.compile("([\\w]+\\.)?\\[[^\\[\\]]*\\]"); //t.[name]或者 [name]
		String sql = "SELECT [showPhoto2] AS table_n_0, [isDelete] AS table_n_1 FROM [123456]";
		String tableName = "123456";
		String tableNamePattern = "[`]?\\[123456\\][`]?";
		Pattern pattern = Pattern.compile(tableNamePattern); //t.[name]或者 [name]
		Matcher matcher = pattern.matcher(sql);
		int i=0;
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String string = matcher.group(0);
			matcher.appendReplacement(sb, "rep"+i); //替换
			if (string != null && !"".equals(string)){
				System.out.println(matcher.group(0));
			}
			i++;
		}
		matcher.appendTail(sb); 
		System.out.println(sb.toString());
		System.out.println("总数: " + i);
		
		System.out.println(new Date());

	}

}
