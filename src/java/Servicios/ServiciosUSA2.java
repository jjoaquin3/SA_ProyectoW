package Servicios;

import Clases.SQLM;
import Clases.TransaccionUSA;
import Clases.Item;
import GT2.ServiciosGT2_Service;
import SAT2.Arancel_Service;
import java.util.ArrayList;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.ws.WebServiceRef;

/**
 *
 * @author Joaquin
 */
@WebService(serviceName = "ServiciosUSA2")
public class ServiciosUSA2 {
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/SA_GT2/ServiciosGT2.wsdl")
    private ServiciosGT2_Service serviciosGT2;
    
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/192.168.1.4_8080/proyectoSA/arancel.wsdl")
    private Arancel_Service serviciosSAT2;

    @WebMethod(operationName = "ActualizarTracking")    
    public TransaccionUSA ActualizarTracking(@WebParam(name = "tracking") int tracking, @WebParam(name = "ubicacion") String ubicacion) 
    {
        TransaccionUSA transaccion = new TransaccionUSA();
        SQLM sql = new SQLM();
        transaccion.Estado = sql.ActualizarTracking(tracking, ubicacion);
        sql.cerrarConexion();
        return transaccion;
    }

    @WebMethod(operationName = "ObtenerTracking")
    public Item ObtenerTracking(@WebParam(name = "tracking") int tracking) 
    {        
        SQLM sql = new SQLM();
        Clases.Item item= sql.ObtenerTracking(tracking);
        sql.cerrarConexion();
        return item;
    }
        
    @WebMethod(operationName = "GenerarTracking")    
    public int GenerarTracking(@WebParam(name = "tracking") int tracking) 
    {        
        @SuppressWarnings("null")    
        SQLM sql = new SQLM();
        int mytracking = sql.GenerarTracking();
        mytracking += 1;
        sql.cerrarConexion();
        return mytracking;
    }
    
    @WebMethod(operationName = "EnviarManifiesto")    
    public int EnviarManifiesto() 
    {
        SQLM sql = new SQLM();
        TransaccionUSA parametroUSA = sql.CrearManifiesto();
                
        if(parametroUSA==null)
            return 0;
        
        //---------------------------------------------------------->
        //Crear parametro
        SAT2.Master parametroSAT = new SAT2.Master();
        copiarManifiesto_SAT(parametroUSA, parametroSAT);        
        //Consumir servicio de validar manifiesto de la SAT
        SAT2.Arancel portSAT2 = serviciosSAT2.getArancelPort();
        parametroSAT = portSAT2.validarManifiesto(parametroSAT);
        //---------------------------------------------------------->
                
        //---------------------------------------------------------->
        //Crear parametro
        parametroUSA.Monto = parametroSAT.getMonto();
        parametroUSA.NumeroFormulario = parametroSAT.getNumeroFormulario();                        
        GT2.TransaccionGT parametroGT = new GT2.TransaccionGT();
        copiarManifiesto_GT(parametroUSA, parametroGT);
        //Consumir servicio para mandar manifiesto+formulario a CPX GUATE
        GT2.ServiciosGT2 port = serviciosGT2.getServiciosGT2Port();
        int resultadoGT2 = port.recibirFormulario(parametroGT);
        //---------------------------------------------------------->
                
        //Resultado del formulario botenido
        if(resultadoGT2==1)        
        {
            //Actualizar formulario en mi base de datos
            sql.actualizarManifiesto(parametroUSA);
            System.out.println("Manifiesto enviado a guatemala correcto");
        }
        else
            System.out.println("Manifiesto error a guatemala");
        
        //Cerrar conexion y retornar el estado del formulario
        sql.cerrarConexion();
        return resultadoGT2;
    }    
    
    public void copiarManifiesto_SAT(TransaccionUSA o, SAT2.Master f)
    {       
        f.detalleItem = new ArrayList<>();
        for(Item item:o.DetalleItem)
        {
            SAT2.Element nuevo = new SAT2.Element();
            nuevo.setCantidad(item.Cantidad);
            nuevo.setCategoria(item.Categoria);
            nuevo.setDescripcion(item.Descripcion);
            nuevo.setNumeroTracking(item.NumeroTracking);
            nuevo.setPeso(item.Peso);
            nuevo.setPorcentajeArancel(item.PorcentajeArancel);
            nuevo.setPrecio(item.Precio);
            f.detalleItem.add(nuevo);
        }
    }
    
    public void copiarManifiesto_GT(TransaccionUSA o, GT2.TransaccionGT f)
    {
        f.detalleItem = new ArrayList<>();
        for(Item item:o.DetalleItem)
        {
            GT2.Item nuevo = new GT2.Item();
            nuevo.setCantidad(item.Cantidad);
            nuevo.setCategoria(item.Categoria);
            nuevo.setDescripcion(item.Descripcion);
            nuevo.setNumeroTracking(item.NumeroTracking);
            nuevo.setPeso(item.Peso);
            nuevo.setPorcentajeArancel(item.PorcentajeArancel);            
            nuevo.setPrecio(item.Precio);            
            nuevo.setProceso(item.Proceso);
            nuevo.setProducto(item.Producto);
            nuevo.setUbicacion(item.Ubicacion);
            nuevo.setUsuario(item.Usuario);
            f.detalleItem.add(nuevo);
        }
    }
}
