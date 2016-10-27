package evan.wang.unirest;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import evan.wang.model.Book;

public class UnirestTest {

	@Before
	public void before() {
		// Only one time
		Unirest.setObjectMapper(new ObjectMapper() {
			private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

			public <T> T readValue(String value, Class<T> valueType) {
				try {
					return jacksonObjectMapper.readValue(value, valueType);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			public String writeValue(Object value) {
				try {
					return jacksonObjectMapper.writeValueAsString(value);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}
		});

	}

	/**
	 * 获取json数据并解析
	 */
	@Test
	public void testGetJson() {
		try {
			HttpResponse<JsonNode> response = Unirest.post("http://localhost:8080/UnirestJava/unirest/getInfo")
					.queryString("id", 1).asJson();
			JsonNode jsonNode = response.getBody();
			JSONArray jsonArray = jsonNode.getArray();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				int id = jsonObject.getInt("id");
				String name = jsonObject.getString("name");
				String info = jsonObject.getString("info");
				System.out.println(String.format("[id]: %d, [name]: %s, [info]: %s", id, name, info));
			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取的json数据转化为Object对象 asObject()必须要有before种的setObjectMapper代码
	 */
	@Test
	public void testGetObject() {
		try {
			HttpResponse<Book> response = Unirest.post("http://localhost:8080/UnirestJava/unirest/getBook")
					.queryString("id", 1).asObject(Book.class);
			Book book = response.getBody();
			System.out.println(book);
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 异步获取数据
	 */
	@Test
	public void testAsync() {
		try {
			Unirest.post("http://localhost:8080/UnirestJava/unirest/getBook")
			.header("accept", "application/json")
			.queryString("id", 1)
			.asObjectAsync(Book.class, new Callback<Book>() {

						@Override
						public void completed(HttpResponse<Book> response) {
							Book book = response.getBody();
							System.out.println("completed-----");
							System.out.println(book);
						}

						@Override
						public void failed(UnirestException e) {
							System.out.println("exception-----");
							e.printStackTrace();
						}

						@Override
						public void cancelled() {
							System.out.println("cancelled-----");
						}
					});
			System.out.println("wait---------");
			// 测试中休眠3秒，防止虚拟机退出后得不到异步任务结果。
			Thread.sleep(1000 * 3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("test finish 1");
	}

	@Test
	public void testUpload() throws Exception {
		HttpResponse<String> jsonResponse = 
				 Unirest.post("http://localhost:8080/UnirestJava/unirest/image")
				.header("accept", "application/json")
				.field("name", "headPic")
				.field("userImage", new File("G:\\wechat_order_food\\resources\\Images\\logo_1xiu.jpg"))
				.asString();
		String string = jsonResponse.getBody();
		JSONObject jsonObject = new JSONObject(string);
		int code = jsonObject.getInt("code");
		if(code==1){
			System.out.println("upload success!");
		}else {
			System.out.println("upload field!");
		}
	}

}
