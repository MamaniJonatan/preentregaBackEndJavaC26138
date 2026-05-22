package com.techlab;

import com.techlab.productos.Producto;
import com.techlab.pedidos.Pedido;
import com.techlab.pedidos.LineaPedido;
import com.techlab.excepciones.StockInsuficienteException;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static ArrayList<Producto> listaProductos = new ArrayList<>();
    private static ArrayList<Pedido> listaPedidos = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in); // <-- ARREGLADO ACÁ

    public static void main(String[] args) {
        // Carga inicial de constructor: (Nombre, Precio, Stock)
        // El ID se maneja internamente en la clase Producto
        listaProductos.add(new Producto("Café Premium", 2500.0, 10));
        listaProductos.add(new Producto("Medialuna", 600.0, 20));

        int opcion = 0;
        do {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1) Agregar producto");
            System.out.println("2) Listar productos");
            System.out.println("3) Buscar/Actualizar producto");
            System.out.println("4) Eliminar producto");
            System.out.println("5) Crear un pedido");
            System.out.println("6) Listar pedidos");
            System.out.println("7) Salir");
            System.out.print("Elija una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1:
                        agregarProducto();
                        esperarEnter();
                        break;
                    case 2:
                        listarProductos();
                        esperarEnter();
                        break;
                    case 3:
                        actualizarProducto();
                        esperarEnter();
                        break;
                    case 4:
                        eliminarProducto();
                        esperarEnter();
                        break;
                    case 5:
                        crearPedido();
                        esperarEnter();
                        break;
                    case 6:
                        listarPedidos();
                        esperarEnter();
                        break;
                    case 7:
                        System.out.println("Saliendo del sistema... ¡Gracias!");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente entre 1 y 7.");
                        esperarEnter();
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Por favor, ingrese un número válido.");
                esperarEnter();
            }
        } while (opcion != 7);
    }

    private static void agregarProducto() {
        System.out.println("\n--- AGREGAR NUEVO PRODUCTO ---");

        System.out.print("Ingrese Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese Precio: ");
        double precio = Double.parseDouble(scanner.nextLine());
        System.out.print("Ingrese Stock Inicial: ");
        int stock = Integer.parseInt(scanner.nextLine());

        // Se crea el producto con el constructor de 3 parámetros
        listaProductos.add(new Producto(nombre, precio, stock));
        System.out.println("Producto agregado con éxito.");
    }

    private static void listarProductos() {
        System.out.println("\n--- LISTA DE PRODUCTOS ---");
        if (listaProductos.isEmpty()) {
            System.out.println("No hay productos registrados en el inventario.");
            return;
        }
        for (Producto p : listaProductos) {
            System.out.println("ID: " + p.getId() + " | " + p.getNombre() + " | Precio: $" + p.getPrecio() + " | Stock: " + p.getStock());
        }
    }

    private static void actualizarProducto() {
        System.out.println("\n--- BUSCAR / ACTUALIZAR PRODUCTO ---");
        System.out.print("Ingrese el ID del producto a buscar: ");
        int id = Integer.parseInt(scanner.nextLine());
        Producto p = buscarPorId(id);

        if (p != null) {
            System.out.println("Producto encontrado: " + p.getNombre() + " | Stock actual: " + p.getStock());
            System.out.print("Ingrese nuevo precio (o Enter para mantener $" + p.getPrecio() + "): ");
            String nuevoPrecioStr = scanner.nextLine();
            if (!nuevoPrecioStr.isEmpty()) {
                p.setPrecio(Double.parseDouble(nuevoPrecioStr));
            }

            System.out.print("Ingrese nuevo stock (o Enter para mantener " + p.getStock() + "): ");
            String nuevoStockStr = scanner.nextLine();
            if (!nuevoStockStr.isEmpty()) {
                p.setStock(Integer.parseInt(nuevoStockStr));
            }
            System.out.println("Producto actualizado correctamente.");
        } else {
            System.out.println("Error: Producto no encontrado.");
        }
    }

    private static void eliminarProducto() {
        System.out.println("\n--- ELIMINAR PRODUCTO ---");
        System.out.print("Ingrese el ID del producto a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine());
        Producto p = buscarPorId(id);

        if (p != null) {
            listaProductos.remove(p);
            System.out.println("Producto '" + p.getNombre() + "' eliminado del inventario.");
        } else {
            System.out.println("Error: Producto no encontrado.");
        }
    }

    private static void crearPedido() {
        listarProductos();
        if (listaProductos.isEmpty()) return;

        Pedido nuevoPedido = new Pedido();
        System.out.print("\n¿Cuántos productos distintos va a llevar?: ");
        int cantidadItems = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < cantidadItems; i++) {
            System.out.print("Ingrese ID del producto: ");
            int id = Integer.parseInt(scanner.nextLine());
            Producto p = buscarPorId(id);

            if (p != null) {
                System.out.print("Cantidad para " + p.getNombre() + ": ");
                int cant = Integer.parseInt(scanner.nextLine());

                try {
                    if (cant > p.getStock()) {
                        throw new StockInsuficienteException("Stock insuficiente. Disponible: " + p.getStock());
                    }

                    boolean yaExisteEnPedido = false;
                    for (LineaPedido linea : nuevoPedido.getLineas()) {
                        if (linea.getProducto().getId() == p.getId()) {
                            linea.setCantidad(linea.getCantidad() + cant);
                            yaExisteEnPedido = true;
                            break;
                        }
                    }

                    if (!yaExisteEnPedido) {
                        nuevoPedido.agregarLinea(new LineaPedido(p, cant));
                    }

                    p.setStock(p.getStock() - cant);
                    System.out.println("Agregado al pedido.");

                } catch (StockInsuficienteException e) {
                    System.out.println("Error: " + e.getMessage());
                    i--;
                }
            } else {
                System.out.println("ID no válido. Intente de nuevo.");
                i--;
            }
        }
        listaPedidos.add(nuevoPedido);
        System.out.println("\n¡Pedido creado con éxito!");
        nuevoPedido.mostrarPedido();
    }

    private static void listarPedidos() {
        System.out.println("\n--- HISTORIAL DE PEDIDOS PROCESADOS ---");
        if (listaPedidos.isEmpty()) {
            System.out.println("No se ha registrado ningún pedido todavía.");
            return;
        }
        for (Pedido ped : listaPedidos) {
            ped.mostrarPedido();
            System.out.println("--------------------------------");
        }
    }

    private static Producto buscarPorId(int id) {
        for (Producto p : listaProductos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    private static void esperarEnter() {
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
}