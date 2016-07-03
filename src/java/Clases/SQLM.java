package Clases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joaquin
 */
public class SQLM 
{
    private Connection conexion = null;
    private String sconexion = "jdbc:mysql://localhost/SA_USA2";
    private String user = "root";
    private String pass = "12345678";
    
    public SQLM()
    {
        try 
        {
            Class.forName("com.mysql.jdbc.Driver");        
            conexion = DriverManager.getConnection(this.sconexion,user,pass);
        }
        catch (ClassNotFoundException | SQLException ex) 
        {
            Logger.getLogger(SQLM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int GenerarTracking()
    {
        int reto = 0;
        try 
        {
            String scomando = "select tracking from COMPRA";
            PreparedStatement cmd = this.conexion.prepareStatement(scomando);
            ResultSet reader;
            reader = cmd.executeQuery();
            if(reader.first())
                reto = reader.getInt("tracking");
            this.cerrarReader(reader);            
        } catch (SQLException ex) 
        {
            Logger.getLogger(SQLM.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reto;
    }
    
    public int ActualizarTracking(int tracking, String locacion)
    {
        try 
        {
            String scomando = "update COMPRA set ubicacion = ? where tracking = ?";
            PreparedStatement cmd = this.conexion.prepareStatement(scomando);
            cmd.setString(1, locacion);
            cmd.setInt(2, tracking);
            cmd.executeUpdate();
            return 1;
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(SQLM.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }        
    
    public Item ObtenerTracking(int tracking)
    {
        Item reto = null;
        try 
        {
            String scomando = "select * from COMPRA where tracking=?";
            PreparedStatement cmd = this.conexion.prepareStatement(scomando);
            cmd.setInt(1, tracking);
            ResultSet reader;
            reader = cmd.executeQuery();
            if(reader.first())
            {    
                reto = new Item();            
                reto.Ubicacion = reader.getString("ubicacion");
                reto.Proceso = reader.getInt("proceso");
            }           
            this.cerrarReader(reader);
            return reto;
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(SQLM.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public int AgregarPaquete(String []data)
    {
        try 
        {
            String scomando = "insert into COMPRA(idcliente, idproducto, tracking, moneda, cantidad, peso, categoria, arancel, precio, descripcion, ubicacion, proceso)";
            scomando+= "values(?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement cmd = this.conexion.prepareStatement(scomando);
            cmd.setInt(1, Integer.parseInt(data[0]));       //id cliente
            cmd.setInt(2, Integer.parseInt(data[1]));       //id producto
            cmd.setInt(3, Integer.parseInt(data[2]));       //tracking
            cmd.setString(4, data[3]);                      //moneda
            cmd.setInt(5, Integer.parseInt(data[4]));       //cantidad
            cmd.setDouble(6, Double.parseDouble(data[5]));  //peso
            cmd.setInt(7, Integer.parseInt(data[6]));       //categoria
            cmd.setInt(8, Integer.parseInt(data[7]));       //arancel
            cmd.setDouble(9, Double.parseDouble(data[8]));  //precio
            cmd.setString(10, data[9]);                     //descripcion
            cmd.setString(11, data[10]);                    //ubicacion
            cmd.setInt(12, Integer.parseInt(data[11]));     //proceso
            cmd.executeUpdate();
            return 1;
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(SQLM.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    public TransaccionUSA CrearManifiesto()
    {
        try 
        {
            String scomando = "select * from COMPRA where proceso = 1";
            PreparedStatement cmd = this.conexion.prepareStatement(scomando);
            ResultSet reader;
            reader = cmd.executeQuery();
            TransaccionUSA reto = new TransaccionUSA();
            reto.DetalleItem = new ArrayList<>();
            while(reader.next())
            {
                Item i = new Item();
                i.Usuario =reader.getInt("idcliente");
                i.Producto = reader.getInt("idproducto");
                i.NumeroTracking = reader.getInt("tracking");
                i.Cantidad =reader.getInt("cantidad");
                i.Peso = reader.getDouble("peso");                                                
                i.Categoria = reader.getInt("categoria");
                i.PorcentajeArancel = reader.getInt("arancel");
                i.Descripcion = reader.getString("descripcion");
                i.Precio = reader.getDouble("precio");
                i.Ubicacion = reader.getString("ubicacion");
                i.Proceso = reader.getInt("proceso");
                reto.DetalleItem.add(i);
            }
            return reto;
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(SQLM.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public void actualizarManifiesto(TransaccionUSA transaccion)
    {
        String scomando = "update COMPRA set proceso = ? where tracking = ?";
        for(Item item:transaccion.DetalleItem)
        {
            try 
            {
                PreparedStatement cmd = this.conexion.prepareStatement(scomando);
                cmd.setInt(1,item.NumeroTracking);
                cmd.executeUpdate();
            } catch (SQLException ex) 
            {
                Logger.getLogger(SQLM.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
        
    public void cerrarConexion()
    {
        if(this.conexion==null)
            return;
        try 
        {
            if(!this.conexion.isClosed())
                this.conexion.close();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(SQLM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void cerrarReader(ResultSet reader)
    {
        if(reader==null)
            return;
        try 
        {
            reader.close();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(SQLM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
