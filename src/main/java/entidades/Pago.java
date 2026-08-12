package entidades;

public class Pago {
    public int id_pago;
    public int id_cliente;
    public int anio;
    public int mes;
    public int monto;
    public String fecha_pago;

    public Pago(int id_cliente, int anio, int mes, int monto, String fecha_pago) {
        this.id_cliente = id_cliente;
        this.anio = anio;
        this.mes = mes;
        this.monto = monto;
        this.fecha_pago = fecha_pago;
    }

    public Pago(int id_pago, int id_cliente, int anio, int mes, int monto, String fecha_pago) {
        this.id_pago = id_pago;
        this.id_cliente = id_cliente;
        this.anio = anio;
        this.mes = mes;
        this.monto = monto;
        this.fecha_pago = fecha_pago;
    }
}
