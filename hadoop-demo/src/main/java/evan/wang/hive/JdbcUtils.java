package evan.wang.hive;

import java.sql.*;


public class JdbcUtils {

    //private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    private static String url = "jdbc:hive2://192.168.10.121:10000/default";
    private static String user = "";
    private static String password = "";

    static {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private int initPoolSize = 100;
    private int maxPoolSize = 1000;
    private int waitTime = 10000;


//	   public static Connection getConnection() throws Exception{
//			DataSource ds = new DataSource();  
//
//	 		try {
////	 			Class.forName(driverName);
//	 			
//	 			PoolProperties pp = new PoolProperties();  
//	 			pp.setDriverClassName(driverName);  
//	 			pp.setUrl(url);  
//	 			pp.setUsername(user);  
//	 			pp.setPassword("password");  
//	 			ds = new DataSource(pp);  
//	 			
//	 		} catch (Exception e) {
//	 			// TODO Auto-generated catch block
//	 			e.printStackTrace();
//	 		}
//	 		return ds.getConnection();
//	 	
//	 	}


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }


    public static void release(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
