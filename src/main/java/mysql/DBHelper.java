/**
 * 数据库操作类
 */
package mysql;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBHelper
{
    //数据库连接对象
    private static Connection conn = null;
    //返回数据库连接对象
    private static Connection getConnection()
    {
        String url = "jdbc://mysql://localhost:3306/bilibili";
        String username = "root";
        String password = "zz123456";
        if(conn!=null)
            return conn;
        try {
            Class.forName("com.mysql.cj.jdbc.Drivers");
            conn = DriverManager.getConnection(url,username,password);

        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void main(String[] args) {
        Connection conn = getConnection();
    }
}
