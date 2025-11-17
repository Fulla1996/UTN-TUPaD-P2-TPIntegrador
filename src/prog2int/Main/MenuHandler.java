package prog2int.Main;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

import prog2int.Models.CodigoBarras;
import prog2int.Models.TipoCB;
import prog2int.Models.Producto;
import prog2int.Service.CodigoBarrasServiceImpl;
import prog2int.Service.ProductoServiceImpl;

public class MenuHandler {

    private final Scanner scanner;
    private final ProductoServiceImpl productoService;
    private final CodigoBarrasServiceImpl cbService;

    public MenuHandler(Scanner scanner, ProductoServiceImpl productoService, CodigoBarrasServiceImpl cbService) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner no puede ser null");
        }
        if (productoService == null) {
            throw new IllegalArgumentException("ProductoService no puede ser null");
        }
        if (cbService == null) {
            throw new IllegalArgumentException("CodigoBarrasService no puede ser null");
        }
        this.scanner = scanner;
        this.productoService = productoService;
        this.cbService = cbService;
    }
    // --- CÓDIGO DE BARRAS 

    public void crearCodigo() {
        try {
            System.out.println("\n--- Crear Código de Barras ---");
            System.out.print("ID: ");
            long id = Long.parseLong(scanner.nextLine());

            System.out.print("Tipo (EAN13/EAN8/UPC): ");
            String tipo = scanner.nextLine().trim().toUpperCase();

            System.out.print("Valor: ");
            String valor = scanner.nextLine().trim();

            System.out.print("Observaciones: ");
            String obs = scanner.nextLine().trim();

            CodigoBarras cb = new CodigoBarras(id, tipo, valor, new Date(), obs);
            cbService.insertar(cb);

            System.out.println("✔ Código de barras creado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al crear código: " + e.getMessage());
        }
    }

    public void buscarCodigoPorId() {
        try {
            System.out.print("ID a buscar: ");
            long id = Long.parseLong(scanner.nextLine());

            CodigoBarras cb = cbService.getById(id);
            if (cb == null) {
                System.out.println("No se encontró el código.");
                return;
            }

            System.out.println(cb);
        } catch (Exception e) {
            System.err.println("Error al buscar código: " + e.getMessage());
        }
    }

    public void listarCodigo() {
        try {
            List<CodigoBarras> lista = cbService.getAll();
            if (lista.isEmpty()) {
                System.out.println("No hay códigos cargados.");
                return;
            }

            lista.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error al listar códigos: " + e.getMessage());
        }
    }

    public void actualizarCodigo() {
        try {
            System.out.print("ID del código a actualizar: ");
            long id = Long.parseLong(scanner.nextLine());

            CodigoBarras cb = cbService.getById(id);
            if (cb == null) {
                System.out.println("Código no encontrado.");
                return;
            }

            System.out.print("Nuevo valor (" + cb.getValor() + "): ");
            String nuevoValor = scanner.nextLine().trim();
            if (!nuevoValor.isEmpty()) {
                cb.setValor(nuevoValor);
            }

            System.out.print("Observaciones (" + cb.getObservaciones() + "): ");
            String obs = scanner.nextLine().trim();
            if (!obs.isEmpty()) {
                cb.setObservaciones(obs);
            }

            cbService.actualizar(cb);
            System.out.println("✔ Código actualizado.");
        } catch (Exception e) {
            System.err.println("Error al actualizar código: " + e.getMessage());
        }
    }

    public void eliminarCodigo() {
        try {
            System.out.print("ID del código a eliminar: ");
            long id = Long.parseLong(scanner.nextLine());

            cbService.eliminar(id);
            System.out.println("✔ Código eliminado (lógico).");
        } catch (Exception e) {
            System.err.println("Error al eliminar código: " + e.getMessage());
        }
    }

    // --- PRODUCTOS 

    public void crearProducto() {
        try {
            System.out.println("\n--- Crear Producto ---");

            System.out.print("ID: ");
            long id = Long.parseLong(scanner.nextLine());

            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Marca: ");
            String marca = scanner.nextLine().trim();

            System.out.print("Categoría: ");
            String categoria = scanner.nextLine().trim();

            System.out.print("Precio: ");
            double precio = Double.parseDouble(scanner.nextLine());

            System.out.print("Peso: ");
            double peso = Double.parseDouble(scanner.nextLine());

            // Por ahora solo creamos el objeto Código de Barras vacío
            System.out.println("Creando producto sin código de barras asociado (por ahora).");
            CodigoBarras cb = null;

            Producto p = new Producto(id, nombre, marca, categoria, precio, peso, cb);
            productoService.insertar(p);

            System.out.println("✔ Producto creado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al crear producto: " + e.getMessage());
        }
    }

    public void buscarProductoPorId() {
        try {
            System.out.print("ID: ");
            long id = Long.parseLong(scanner.nextLine());

            Producto p = productoService.getById(id);
            if (p == null) {
                System.out.println("Producto no encontrado.");
                return;
            }

            System.out.println(p);
            if (p.getCodigoBarras() != null) {
                System.out.println("   → Código asignado: " + p.getCodigoBarras());
            }
        } catch (Exception e) {
            System.err.println("Error al buscar producto: " + e.getMessage());
        }
    }

    public void listarProductos() {
        try {
            List<Producto> lista = productoService.getAll();
            if (lista.isEmpty()) {
                System.out.println("No hay productos cargados.");
                return;
            }

            lista.forEach(p -> {
                System.out.println(p);
                if (p.getCodigoBarras() != null) {
                    System.out.println("   → Código: " + p.getCodigoBarras().getValor());
                }
            });
        } catch (Exception e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
    }

    public void actualizarProducto() {
        try {
            System.out.print("ID del producto a actualizar: ");
            long id = Long.parseLong(scanner.nextLine());

            Producto p = productoService.getById(id);
            if (p == null) {
                System.out.println("Producto no encontrado.");
                return;
            }

            System.out.print("Nuevo nombre (" + p.getNombre() + "): ");
            String nombre = scanner.nextLine().trim();
            if (!nombre.isEmpty()) {
                p.setNombre(nombre);
            }

            System.out.print("Nueva marca (" + p.getMarca() + "): ");
            String marca = scanner.nextLine().trim();
            if (!marca.isEmpty()) {
                p.setMarca(marca);
            }

            System.out.print("Nueva categoría (" + p.getCategoria() + "): ");
            String cat = scanner.nextLine().trim();
            if (!cat.isEmpty()) {
                p.setCategoria(cat);
            }

            System.out.print("Nuevo precio (" + p.getPrecio() + "): ");
            String precioIn = scanner.nextLine().trim();
            if (!precioIn.isEmpty()) {
                p.setPrecio(Double.parseDouble(precioIn));
            }

            System.out.print("Nuevo peso (" + p.getPeso() + "): ");
            String pesoIn = scanner.nextLine().trim();
            if (!pesoIn.isEmpty()) {
                p.setPeso(Double.parseDouble(pesoIn));
            }

            productoService.actualizar(p);
            System.out.println("✔ Producto actualizado.");
        } catch (Exception e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
        }
    }

    public void eliminarProducto() {
        try {
            System.out.print("ID del producto: ");
            long id = Long.parseLong(scanner.nextLine());

            productoService.eliminar(id);
            System.out.println("✔ Producto eliminado (lógico).");
        } catch (Exception e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
        }
    }
}