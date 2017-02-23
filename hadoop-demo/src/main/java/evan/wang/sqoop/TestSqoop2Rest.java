package evan.wang.sqoop;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @auth evan
 * @date 2017/1/10 16:24
 */
public class TestSqoop2Rest {


    @Test
    public void getConnectorNames() throws Exception {
        List<String> connNames = new ArrayList<>();
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://centos121:12000/sqoop/v1/connectors");
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(instream));
            try {
                StringBuilder sb = new StringBuilder();
                String str = null;
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
                System.out.println(sb.toString());
                JsonParser parser = new JsonParser();
                JsonObject jsonObject  = (JsonObject) parser.parse(sb.toString());
                JsonArray connectors = jsonObject.getAsJsonArray("connectors");
                for (int i=0; i < connectors.size(); i++) {
                    JsonObject connector = (JsonObject) connectors.get(i);
                    String name = connector.get("name").getAsString();
                    connNames.add(name);
                }
            } finally {
                if (instream != null) {
                    instream.close();
                }
            }
        }
        System.out.println(connNames);
    }


    public static void main(String[] args) {


    }


}
