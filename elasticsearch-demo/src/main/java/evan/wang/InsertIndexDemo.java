package evan.wang;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：wangsy
 * 日期：2016/7/22 11:07
 * 描述：
 */
public class InsertIndexDemo {

    /**
     * 插入方法一
     */
    private static void insert1(Client client) {
        for (int i = 0; i < 10; i++) {
            Map<String, Object> json = new HashMap<>();
            json.put("user", "jetty" + i);
            json.put("postDate", new Date());
            json.put("message", "trying out Elasticsearch");
            IndexResponse response = client.prepareIndex("twitter", "tweet", String.valueOf(i))
                    .setSource(json)
                    .get();
            System.out.printf("_index: %s, _type: %s, _id: %s, _version: %d, created: %b \n", response.getIndex(),
                    response.getType(), response.getId(), response.getVersion(), response.isCreated());
        }
    }

    /**
     * 批量插入
     */
    private static void insert2(Client client) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (int i = 50; i < 60; i++) {
            try {
                Map<String, Object> json = new HashMap<>();
                json.put("user", "smoke" + i);
                json.put("postDate", new Date());
                json.put("message", "你放假；啊了多少积分；就付款啦发生不加了");
                IndexRequest indexRequest = client.prepareIndex("twitter", "tweet", String.valueOf(i))
                        .setSource(json)
                        .request();
                bulkRequest.add(indexRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            // process failures by iterating through each bulk response item
            System.out.println("process failures by iterating through each bulk response item");
        }
        System.out.println("insert finish!");
    }

    public static void main(String[] args) {
        EsClient esClient = new EsClient();
        Client client = esClient.openClient();
        // insert1(client);
        insert2(client);
        esClient.closeClient(client);
    }


}
