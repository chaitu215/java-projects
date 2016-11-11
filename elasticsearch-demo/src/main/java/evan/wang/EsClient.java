package evan.wang;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 作者：wangsy
 * 日期：2016/7/22 10:58
 * 描述：elasticsearch client
 */
public class EsClient {
    private String ip="172.16.1.214";
    private int port = 9300;

    /**
     * 打开连接
     * @return
     */
    public Client openClient(){
        Client client = null;
        try {
            //TCP连接地址
            InetSocketTransportAddress address = new InetSocketTransportAddress(InetAddress.getByName(ip), port);
            //如果使用不是默认"elasticsearch"集群名，需要添加Settings设置集群名
            Settings settings = Settings.settingsBuilder().put("cluster.name", "wangshengyong").build();
            client = TransportClient.builder().settings(settings).build()
                    .addTransportAddress(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return client;
    }

    /**
     * 关闭连接
     * @param client
     */
    public void closeClient(Client client){
        if(client !=null){
            client.close();
        }
    }



}
