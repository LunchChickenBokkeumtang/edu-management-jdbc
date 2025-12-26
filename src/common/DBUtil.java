package common;

import java.sql.*;

public class DBUtil {
    {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("mariadb Driver 오류!");
        }
    }


    public static Connection getConnection() {
        Connection connect = null;
        try {
            String url = System.getenv("DB_URL");
            String user = System.getenv("DB_USER");
            String pw = System.getenv("DB_PASSWORD");

            connect = DriverManager.getConnection(url, user, pw);
        } catch (SQLException e) {
            System.out.println("Mariadb 로그인 오류!");
        }
        return connect;
    }

    public static void closeConnect(Connection connect){
        try {
           connect.close();
        } catch (SQLException e) {
            System.out.println("MairaDB연결 해제 오류!");
        }
    }

}
