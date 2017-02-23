package evan.wang.sqoop;

import org.apache.sqoop.client.SqoopClient;
import org.apache.sqoop.model.MConnector;
import org.apache.sqoop.model.MLink;
import org.apache.sqoop.model.MLinkConfig;
import org.apache.sqoop.validation.Status;

import java.util.Iterator;

/**
 * @auth evan
 * @date 2017/1/9 18:46
 */
public class TestSqoop {

    public static void main(String[] args) {
        String url = "http://192.168.10.121:12000/sqoop/";
        SqoopClient client = new SqoopClient(url);
        long connectorId = 1;
        Iterator<MConnector>  it = client.getConnectors().iterator();
        while (it.hasNext()){
            MConnector connector = it.next();
            System.out.println(connector.getClassName());
        }

        MLink link = client.createLink("generic-jdbc-connector");
        link.setName("testFromMysql");
        link.setCreationUser("Evan");

        MLinkConfig linkConfig = link.getConnectorLinkConfig();
        linkConfig.getStringInput("linkConfig.connectionString")
                .setValue("jdbc:mysql://192.168.10.22/test");
        linkConfig.getStringInput("linkConfig.jdbcDriver").setValue("com.mysql.jdbc.Driver");
        linkConfig.getStringInput("linkConfig.username").setValue("shake");
        linkConfig.getStringInput("linkConfig.password").setValue("pw_shake");
        Status status = client.saveLink(link);
        if (status.canProceed()) {
            System.out.println("Created Link with Link Id : " + link.getPersistenceId());
        } else {
            System.out.println("Something went wrong creating the link");
        }
    }

}
