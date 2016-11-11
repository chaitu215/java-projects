package evan.wang;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHits;

/**
 * 作者：wangsy
 * 日期：2016/7/22 18:55
 * 描述：
 */
public class QueryIndexDemo {

    public static void main(String[] args) {
        EsClient esClient = new EsClient();
        Client client = esClient.openClient();
        SearchResponse response = client.prepareSearch("twitter")
                .setTypes("tweet")
/*              .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery("user", "smoke"))                 // Query
                .setPostFilter(QueryBuilders.rangeQuery("_id").from(55).to(60))     // Filter
                .setFrom(0)
                .setSize(60)
                .setExplain(true)*/
                .get();
        System.out.println("suggest: " + response.getSuggest());
        System.out.println("content: " + response.getContext());
        SearchHits searchHits = response.getHits();
        System.out.println(searchHits.getTotalHits());
        for (int i = 0; i < searchHits.getTotalHits(); i++) {
            System.out.println(searchHits.getAt(i).getSource().toString());
        }
        esClient.closeClient(client);
    }


}
