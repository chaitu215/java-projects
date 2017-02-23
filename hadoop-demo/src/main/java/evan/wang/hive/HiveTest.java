package evan.wang.hive;

import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @auth evan
 * @date 2017/1/4 10:34
 */
public class HiveTest {

    @Test
    public void createTable(){
        Connection connection = null;
        Statement stmt = null;
        try {
            connection = JdbcUtils.getConnection();
            stmt = connection.createStatement();
            stmt.execute("CREATE TABLE r(a STRING, b INT, c DOUBLE)");
            System.out.println("create table success!");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.release(connection, stmt, null);
        }
    }


    @Test
    public void dropTable(){
        Connection connection = null;
        Statement stmt = null;
        try {
            connection = JdbcUtils.getConnection();
            stmt = connection.createStatement();
            stmt.execute("DROP TABLE IF EXISTS r");
            System.out.println("drop table success!");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.release(connection, stmt, null);
        }
    }


    @Test
    public void testSelect0() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet set = null;
        try {
            connection = JdbcUtils.getConnection();
            stmt = connection.createStatement();
            String tableName = "stocks";
            set = stmt.executeQuery("select symbol, ymd, price_close from " + tableName + " limit 10");
            while (set.next()) {
                String symbol = set.getString(1);
                String ymd = set.getString(2);
                double price_close = set.getDouble(3);
                System.out.println("symbol: " + symbol + " ymd: " + ymd + " price_close: " + price_close);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.release(connection, stmt, set);
        }
    }

    @Test
    public void testSelect1() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet set = null;
        try {
            connection = JdbcUtils.getConnection();
            stmt = connection.createStatement();
            set = stmt.executeQuery("EXPLAIN SELECTdetailid,ip FROM a_14");
            while (set.next()) {
                String str = set.getString(1);
                System.out.println(str);
            }
        } catch (SQLException e) {
           System.out.println(e.getMessage());
        } finally {
            JdbcUtils.release(connection, stmt, set);
        }
    }




}
