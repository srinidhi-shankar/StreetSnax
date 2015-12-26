package com.streetsnax.srinidhi.streetsnax;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by I15230 on 12/26/2015.
 */
public class DBHelper {

    public static Boolean ValidateUser(String email, String userPassword) {
        Boolean isSuccess = false;
        Log.i("Android", " SqlServer Connect.");
        try {
            Connection conn = CreateSQLDBConnection();
            Statement stmt = conn.createStatement();
            StringBuilder qryBuilder = new StringBuilder();
            qryBuilder.append("select * from Users where ");
            qryBuilder.append("Email='");
            qryBuilder.append(email);
            qryBuilder.append("' and password='");
            qryBuilder.append(userPassword);
            qryBuilder.append("'");
            ResultSet reset = stmt.executeQuery(qryBuilder.toString());
            if (reset.next()) {
                isSuccess = true;
            }
////Print the data to the console
//            while(reset.next()){
//                Log.w("Data:",reset.getString(3));
////              Log.w("Data",reset.getString(2));
//            }
            conn.close();

        } catch (Exception e) {
            Log.w("Error connection", "" + e.toString() + " + " + e.getMessage());
            e.printStackTrace();
        }
        return isSuccess;
    }

    private static Connection CreateSQLDBConnection() throws Exception {
        Connection conn;
        String driver = "net.sourceforge.jtds.jdbc.Driver";
        try {
            Class.forName(driver).newInstance();
            String connString = "jdbc:jtds:sqlserver://103.21.58.192/srirama;instance=SQLEXPRESS;";
            String username = "sql_admin";
            String password = "l@gAAvein_13";
            conn = DriverManager.getConnection(connString, username, password);
            Log.w("Connection", "open");
        } catch (Exception ex) {
            Log.v("Connection", ex.getMessage());
            throw ex;
        }
        return conn;
    }
}
