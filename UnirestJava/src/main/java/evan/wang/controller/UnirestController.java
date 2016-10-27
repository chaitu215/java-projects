package evan.wang.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import evan.wang.model.Book;

@Controller
@RequestMapping("/unirest")
public class UnirestController {

	@RequestMapping(value = "/getInfo")
	@ResponseBody
	public List<Map<String, Object>> getInfo(@RequestParam(defaultValue="1")int id) {
		System.out.println(String.format("id is %d", id));
		List<Map<String, Object>> list = new ArrayList<>();
		if (id == 1) {
			for (int i = 0; i < 10; i++) {
				Map<String, Object> map = new HashMap<>();
				map.put("id", i);
				map.put("name", "name" + i);
				map.put("info", "this is a test info");
				list.add(map);
			}
		}
		return list;
	}
	

	@RequestMapping(value = "/getBook", method = RequestMethod.POST)
	@ResponseBody
	public Book getBook(int id) {
		System.out.println(String.format("book id is %d", id));
		Book book = new Book(1, "spring in action", "a book about spring", "Rod Johnson");
		return book;
	}
	
	
	/*
	 * 基于commons-fileupload组件的文件上传 1、需要引用commons-fileupload.jar和commons-io.jar
	 * 2、springMVC.xml配置文件中添加CommonsMultipartResolver
	 */
	@RequestMapping(value = "/image", method = RequestMethod.POST)
	@ResponseBody
	public String uploadFile1(@RequestParam("userImage") MultipartFile file, @RequestParam("name") String name,
			HttpServletRequest request) throws Exception {
		if (file != null && !file.isEmpty()) {// 如果有上传文件
			System.out.println("文件原始名字: " + file.getOriginalFilename());
			System.out.println("文件大小: " + (file.getBytes().length / 1024) + "kb");
			System.out.println("附加字段name: " + name);
			String path = request.getServletContext().getRealPath("/uploadFiles");
			file.transferTo(new File(path + File.separator + file.getOriginalFilename()));
			return "{\"code\":1,\"result\":\"ok\"}";
		} else {
			return "{\"code\":0,\"result\":\"error\"}";
		}
	}

}
