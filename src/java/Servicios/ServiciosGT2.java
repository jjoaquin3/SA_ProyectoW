package Servicios;

import Clases.SQLM;
import Clases.TransaccionGT;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Joaquin
 */
@WebService(serviceName = "ServiciosGT2")
public class ServiciosGT2 
{ 
    //Se paga el paquete al BANCO(Pollo)
    public int PagarPaquete(@WebParam(name = "cuenta") int cuenta, @WebParam(name = "tarjeta") int tarjeta, @WebParam(name = "tipo") int tipo, @WebParam(name = "monto")double monto) 
    {
        return 0;
    }
    
    //Se pagan los impuestos de x contenedor a la SAT(Yaquin)
    @WebMethod(operationName = "PagarImpuestos")
    public int PagarImpuestos(int formulario) 
    {
        return 0;
    }
    
    //Se manda a factura a FACTURACION(Cristian)
    @WebMethod(operationName = "GenerarFactura")
    public int GenerarFactura() 
    {
        //genera la factura consumiendo los servicios de FACTURACION
        return 1;
    }
    
    //Permite que USA(YO) mande el manifiesto
    @WebMethod(operationName = "RecibirFormulario")
    public int RecibirFormulario(@WebParam(name = "transaccion") TransaccionGT transaccion) 
    {        
        int reto = 0;
        SQLM sql = new SQLM();
        if(sql.ingresarManifiesto(transaccion))
            reto = 1;
        sql.cerrarConexion();
        return reto;
    }
    
}
