package evan.wang;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author: wangsy
 * @date: 2017年6月16日
 */
@Controller
@RequestMapping("/upload")
public class UploadAction {
	
	@RequestMapping("/jsp")
	public String hello(){
		System.out.println("page");
		return "index";
	}

	/**
	 * 上传文件
	 * 
	 * @param file
	 * @param request
	 */
	@ResponseBody
	@RequestMapping("/csv")
	public JSONObject csv(@RequestParam(value = "file", required = false) MultipartFile file,
			HttpServletRequest request) {
		JSONObject result = new JSONObject();
		result.put("code", -1);
		System.out.println(file);
		if (file != null && !file.isEmpty()) {// 如果有上传文件
			try {
				System.out.println("文件原始名字: " + file.getOriginalFilename());
				System.out.println("文件大小: " + (file.getBytes().length / 1024.0) + "kb");
				String fileName = new ObjectId().toString() + ".csv";
				String path = request.getServletContext().getRealPath("/upload");
				File targetFile = new File(path + File.separator + fileName);
				/*if (!targetFile.exists()) {
					targetFile.mkdirs();
				}*/
				file.transferTo(targetFile);

				result.put("code", 0);
				result.put("fileName", fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
