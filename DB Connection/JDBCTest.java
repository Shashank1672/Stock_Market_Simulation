import java.sql.*;

public class JDBCTest {
  public static void main(String[] args) {
    try{Class.forName("com.mysql.jdbc.Driver");}
    catch(Exception e){System.out.println(e);}
    String url = "jdbc:mysql://localhost:3306/";
    String username = "root";
    String password = "forgotpassword";

    //Connection conn = DriverManager.getConnection(url, username, password);
    
    try (Connection conn = DriverManager.getConnection(url, username, password);) {
      System.out.println("Database connected");
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM central_rto.rto");
      
      while (rs.next()){
        String state = rs.getString("state_id");
        String name = rs.getString("state");
        System.out.println("\t"+state+" | "+"\t"+name);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
