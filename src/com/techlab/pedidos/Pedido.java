package com.techlab.pedidos;

import java.util.ArrayList;

public class Pedido {
    private static int contadorPedido = 1;
    private int idPedido;
    private ArrayList<LineaPedido> lineas;

    public Pedido() {
        this.idPedido = contadorPedido++;
        this.lineas = new ArrayList<>();
    }

    public void agregarLinea(LineaPedido linea) {
        lineas.add(linea);
    }

    public double calcularTotal() {
        double total = 0;
        for (LineaPedido linea : lineas) {
            total += linea.getSubtotal();
        }
        return total;
    }

    public void mostrarPedido() {
        System.out.println("\n--- Pedido N° " + idPedido + " ---");
        for (LineaPedido linea : lineas) {
            System.out.println("- " + linea.getProducto().getNombre() + " x" + linea.getCantidad() + " ($" + linea.getSubtotal() + ")");
        }
        System.out.println("TOTAL: $" + calcularTotal());
    }

    // Método Getter agregado para que el Main pueda agrupar productos repetidos
    public ArrayList<LineaPedido> getLineas() {
        return lineas;
    }
}