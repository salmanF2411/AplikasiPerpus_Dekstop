/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AplikasiPerpus;
  import java.sql.Connection; 
  import java.sql.DriverManager; 
  import java.sql.Statement;
  import java.sql.ResultSet; 
  import java.sql.SQLException; 
  import javax.swing.JOptionPane; 
  import javax.swing.table.DefaultTableModel;


/**
 *
 * @author Asus
 */
public class koneksiDB {
     public static Connection con;
     public static Statement stm;
     
      public static Connection Getkoneksi() {
            try{
            String url ="jdbc:mysql://localhost/db_perpustakaan";
            String user="root";
            String pass="";
            Class.forName("com.mysql.jdbc.Driver");
            con =DriverManager.getConnection(url,user,pass);
            stm = con.createStatement();
            System.out.println("koneksi berhasil;");
        }//batas try
        catch (ClassNotFoundException | SQLException e) 
        {
          System.err.println("koneksi gagal" +e.getMessage());
        }  
          return con;
      }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Getkoneksi();
        
    }
    
}
