package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by user on 20.02.2016.
 */
public class DB {

    static {
        try {
            DriverManager.registerDriver(new org.firebirdsql.jdbc.FBDriver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    static final String url = "";
    static final String user_name = "SYSDBA";
    static final String user_password = "masterkey";

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(url, user_name, user_password);
        } catch (SQLException e) {
            Logger.getGlobal().severe("can't connect to database (" + url + ")");
            throw e;
        }
    }

}
