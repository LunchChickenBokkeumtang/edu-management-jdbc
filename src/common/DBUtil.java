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
            String url = "jdbc:mariadb://192.168.0.33:3306/edu_management_team2";
            String user = "hoon6481";
            String pw = "rladudgns1@";
            connect = DriverManager.getConnection(url, user , pw);
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
