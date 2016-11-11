package evan.wang;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.script.Script;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * 作者：wangsy
 * 日期：2016/7/22 14:00
 * 描述：
 */
public class UpdateIndexDemo {

    /**
     * 更新方法一
     */
    private static void update1(Client client) {
        try {
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index("twitter");
            updateRequest.type("tweet");
            updateRequest.id("1");
            updateRequest.doc(jsonBuilder()
                    .startObject()
                    .field("message", "update message111111------")
                    .endObject());
            client.update(updateRequest).get();
            System.out.println("1 update success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新方法二
     * The update API also support passing a partial document, which will be merged into the existing document
     * (simple recursive merge, inner merging of objects, replacing core "keys/values" and arrays)
     */
    private static void update2(Client client) {
        try {
            client.prepareUpdate("twitter", "tweet", "2")
                    .setDoc(jsonBuilder()
                            .startObject()
                            .field("message", "update message222222------")
                            .endObject()
                    ).get();
            System.out.println("2 update success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新方法三
     * The update API allows to update a document based on a script provided
     */
    private static void update3(Client client) {
        try {
            UpdateRequest updateRequest = new UpdateRequest("twitter", "tweet", "3")
                    .script(new Script("ctx._source.message = \"update message333333------\""));
            client.update(updateRequest).get();
            System.out.println("3 update success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新或者插入
     * @param client
     */
    public static void updateOrInsert(Client client){
        try {
            IndexRequest indexRequest = new IndexRequest("twitter", "tweet", "13")
                    .source(jsonBuilder()
                            .startObject()
                            .field("user", "Joe Smith")
                            .field("gender", "male")
                            .endObject());
            UpdateRequest updateRequest = new UpdateRequest("twitter", "tweet", "13")
                    .doc(jsonBuilder()
                            .startObject()
                            .field("gender", "male")
                            .endObject())
                    .upsert(indexRequest);
            client.update(updateRequest).get();
            System.out.println("update or insert success!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        EsClient esClient = new EsClient();
        Client client = esClient.openClient();
        update1(client);
        update2(client);
       // update3(client);
        updateOrInsert(client);
        esClient.closeClient(client);
    }

}
