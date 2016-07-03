package Clases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joaquin
 */
public class SQLM 
{
    private Connection conexion = null;
    private String sconexion = "jdbc:mysql://localhost/SA_GT2";
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
    
    public void PagarPaquete(int tracking)
    {
        try 
        {
            String scomando = "update COMPRA set estado = 7 where tracking = ?";
            PreparedStatement cmd = this.conexion.prepareStatement(scomando);
            cmd.setInt(1, tracking);
            cmd.executeUpdate();
            //return 1;
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(SQLM.class.getName()).log(Level.SEVERE, null, ex);
            //return 0;
        }
    }
    
    public boolean ingresarManifiesto(TransaccionGT transaccion)
    {
        int formulario = transaccion.NumeroFormulario;
        double monto = transaccion.Monto;
        
        //llenar formulario
        if(this.llenarFormulario(formulario, monto)==false)
            return false;
        
        //paso ahora lleno mi base con los datos del manifiesto
        String scomando = "insert into COMPRA(idcliente, idproducto, idformulario,"
                + "tracking, moneda, cantidad, peso, categoria, arancel, precio, "
                + "descripcion, ubicacion, proceso) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            
        for(Item item:transaccion.DetalleItem)
        {
            try 
            {
                PreparedStatement cmd = this.conexion.prepareStatement(scomando);
                cmd.setInt(1, item.Usuario);        //id cliente
                cmd.setInt(2, item.Producto);       //id producto
                cmd.setInt(3, formulario);
                cmd.setInt(4, item.NumeroTracking); //tracking
                cmd.setString(5, "Quetzal");        //moneda
                cmd.setInt(6, item.Cantidad);       //cantidad
                cmd.setDouble(7, item.Peso);        //peso
                cmd.setInt(8, item.Categoria);      //categoria
                cmd.setInt(9, item.PorcentajeArancel);   //arancel
                cmd.setDouble(10, item.Precio);             //precio
                cmd.setString(11, item.Descripcion);        //descripcion
                cmd.setString(12, item.Ubicacion);          //ubicacion
                cmd.setInt(13, item.Proceso);               //proceso
                cmd.executeUpdate();
            } 
            catch (SQLException ex) 
            {
                Logger.getLogger(SQLM.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }        
        return true;
    }            
    
    public boolean llenarFormulario(int formulario, double monto)
    {
        try 
        {
            String scomando = "insert into FORMULARIO(idformulario, monto, estado) values(?,?,?)";
            PreparedStatement cmd = this.conexion.prepareStatement(scomando);
            cmd.setInt(1, formulario);  //formulario y ese va ser en id
            cmd.setDouble(2, monto);    //monto a pagar
            cmd.setInt(3, 0);           //no pagado
            cmd.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(SQLM.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public void pagarFormulario(int formulario)
    {
        try 
        {
            String scomando = "update FORMULARIO set estado = 7 where idformulario = ?";
            PreparedStatement cmd = this.conexion.prepareStatement(scomando);
            cmd.setInt(1, formulario);
            cmd.executeUpdate();
            //return 1;
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(SQLM.class.getName()).log(Level.SEVERE, null, ex);
            //return 0;
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

