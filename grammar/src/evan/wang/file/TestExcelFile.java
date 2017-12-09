package evan.wang.file;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * 
 * @author: wangsy
 * @date: 2017年5月15日
 */
public class TestExcelFile {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String fileName = "E:/spring-sts-workspace/java-examples/grammar/src/evan/wang/file/ttt.xls";
		File file = new File("E:/spring-sts-workspace/java-examples/grammar/src/evan/wang/file/2010-2015.xls");
		List<String[]> list = ExcelUtil.readExcel(file);
		System.out.println(Arrays.toString(list.get(0)));
		System.out.println(list.size());
		// 创建新的excle
		HSSFWorkbook wb = new HSSFWorkbook();
		String sheetName = "结果";
		HSSFSheet sheet = wb.createSheet(sheetName);
		insert(wb, sheet, sheet.createRow(0), "code", "num", "year", "利润总额", "贷款总额", "投资收益", "固定资产价格", "人力资本价格", "资金价格");

		int rowNum = 1;
		for (int i = 0; i < list.size(); i++) {
			if(i==119){
				break;
			}
			String[] value = list.get(i);
			System.out.println(value.length + ",   " + i);
			if(value.length<=76){
				continue;
			}
			String[] value1 = new String[] { value[1], String.valueOf(i+1), "2010", value[4], value[58], value[34], value[69], value[76], value[82]};
			String[] value2 = new String[] { value[1], String.valueOf(i+1), "2011", value[5], value[59], value[35], value[68], value[75], value[81]};
			String[] value3 = new String[] { value[1], String.valueOf(i+1), "2012", value[6], value[60], value[36], value[67], value[74], value[80]};
			String[] value4 = new String[] { value[1], String.valueOf(i+1), "2013", value[7], value[61], value[37], value[66], value[73], value[79]};
			String[] value5 = new String[] { value[1], String.valueOf(i+1), "2014", value[8], value[62], value[38], value[65], value[72], value[78]};
			String[] value6 = new String[] { value[1], String.valueOf(i+1), "2015", value[9], value[63], value[39], value[64], value[70], value[77]};
			insert(wb, sheet, sheet.createRow(rowNum++), value1);
			insert(wb, sheet, sheet.createRow(rowNum++), value2);
			insert(wb, sheet, sheet.createRow(rowNum++), value3);
			insert(wb, sheet, sheet.createRow(rowNum++), value4);
			insert(wb, sheet, sheet.createRow(rowNum++), value5);
			insert(wb, sheet, sheet.createRow(rowNum++), value6);
			//insert(wb, sheet, row, list.get(0));
		}
		ExcelUtil.outputHSSFWorkbook(wb, fileName);
	}

	public static void insert(HSSFWorkbook wb, HSSFSheet sheet, HSSFRow row, String... values) {
		for (int i = 0; i < values.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(values[i]);
		}
	}

}
