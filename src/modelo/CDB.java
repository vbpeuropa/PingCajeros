package modelo;

 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CDB {

      public Statement statment;
  
      public CDB(String Usuario, String Pass) throws SQLException, ClassNotFoundException
      {
          Class.forName("oracle.jdbc.driver.OracleDriver");
      // DESARROLLO
     /* String url = "jdbc:oracle:thin:"+Usuario+"/"+ Pass + "@" +
                  "(DESCRIPTION = (LOAD_BALANCE=on)(CONNECT_TIMEOUT=5)(RETRY_COUNT=2)" +
                  "(ADDRESS = (PROTOCOL = TCP)(HOST = bancadesa.risa)(PORT = 2521))" +
                  "(CONNECT_DATA =" +
                  "(SERVICE_NAME = BANCAD)" +
                  "(SERVER = DEDICATED)" +
                  ")" +
                  ")";*/
      //PRODUCCION
          
          String url = "jdbc:oracle:thin:"+Usuario+"/"+ Pass + "@" +
                      "(DESCRIPTION = (LOAD_BALANCE=on)(CONNECT_TIMEOUT=5)(RETRY_COUNT=2)" +
                      "(ADDRESS = (PROTOCOL = TCP)(HOST = ruralpro.risa)(PORT = 2521))" +
                      "(CONNECT_DATA =" +
                      "(SERVICE_NAME = BANCAP_BDP)" +
                      "(SERVER = DEDICATED)" +
                      ")" +
                      ")";
          Connection con = DriverManager.getConnection(url);
          statment = con.createStatement();
      }
      
      //RESULTSET = DEVOLUCIÓN SQL.
      public ResultSet ejecutarSelect(String accion) {
        ResultSet rs = null;
        try {    
          rs = statment.executeQuery(accion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
      }  
      public void cerrarConexion() {
          try{
              statment.close();
          }catch(Exception e){
              e.printStackTrace();
          }
      }
}