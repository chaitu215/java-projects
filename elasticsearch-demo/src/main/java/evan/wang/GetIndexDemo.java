package evan.wang;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.client.Client;

/**
 * 作者：wangsy
 * 日期：2016/7/22 13:50
 * 描述：
 */
public class GetIndexDemo {

    private static void simpleGet(Client client) {
        GetResponse response = client.prepareGet("twitter", "tweet", "5").get();
        if (response != null && response.isExists()) {
            String source = response.getSourceAsString();
            System.out.println("simple source: " + source);
        } else {
            System.out.println("source not exists!");
        }
    }

    private static void multiGet(Client client) {
        MultiGetResponse multiGetItemResponses = client.prepareMultiGet()
                .add("twitter", "tweet", "1")
                .add("twitter", "tweet", "2", "3", "4")
                .add("another", "type", "foo")
                .get();
        for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
            GetResponse response = itemResponse.getResponse();
            if (response != null && response.isExists()) {
                String json = response.getSourceAsString();
                System.out.println("---source: " + json);
            }
        }
    }

    public static void main(String[] args) {
        EsClient esClient = new EsClient();
        Client client = esClient.openClient();
        simpleGet(client);
        multiGet(client);
        esClient.closeClient(client);
    }

}
