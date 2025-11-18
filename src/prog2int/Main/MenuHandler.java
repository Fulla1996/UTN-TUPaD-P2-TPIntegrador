package prog2int.Main;

import java.util.Date;
import java.util.List;
import java.util.Scanner;
import prog2int.Config.DatabaseConnection;

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
    
    //Método mostrarProductos
    private void mostrarProductos(List<Producto> lista) {
        System.out.println("\n===== RESULTADOS =====");

        for (Producto p : lista) {
            System.out.println("-----------------------");
            System.out.println("ID: " + p.getId());
            System.out.println("Nombre: " + p.getNombre());
            System.out.println("Marca: " + p.getMarca());
            System.out.println("Precio: " + p.getPrecio());
        }

        System.out.println("-----------------------\n");
    }
    
    // Opcion 1
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
    
    //Opcion 2
    public void crearProducto() {
        try {
            System.out.println("\n--- Crear Código de Barras ---");
            long idCB;
            
            do{
                System.out.print("ID: ");
                idCB = Long.parseLong(scanner.nextLine());
                if (cbService.getById(idCB) != null)
                    System.out.println("El ID del código de barras ya existe. Intente otro.");
            }while(cbService.getById(idCB) != null);

            System.out.print("Tipo (EAN13/EAN8/UPC): ");
            String tipo = scanner.nextLine().trim().toUpperCase();

            System.out.print("Valor: ");
            String valor = scanner.nextLine().trim();

            System.out.print("Observaciones: ");
            String obs = scanner.nextLine().trim();

            CodigoBarras cb = new CodigoBarras(idCB, tipo, valor, new Date(), obs);
            
            System.out.println("\n--- Crear Producto ---");
            long idP;
            do{
            System.out.print("ID: ");
            idP = Long.parseLong(scanner.nextLine());
            
            if (productoService.getById(idP) != null){
                System.out.println("Id ya existente, intente otro.");
            }
            }while(productoService.getById(idP) != null);
            
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

            Producto p = new Producto(idP, nombre, marca, categoria, precio, peso, cb);
            
            productoService.insertarTx(p, cb, DatabaseConnection.getConnection());

            System.out.println("✔ Producto creado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al crear producto: " + e.getMessage());
        }
    }
    
    //Opcion 3
        
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
    public void buscarProductoPorId(long id) {
        try {
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
            System.err.println("Error al buscar el codigo de barras: " + e.getMessage());
        }
    }
    
    //Opcion 4
     public void buscarProductoPorNombre() {
    System.out.print("Ingrese el nombre a buscar: ");
    String nombre = scanner.nextLine();

    try {
        List<Producto> productos = productoService.getListByName(nombre);

        if (productos.isEmpty()) {
            System.out.println("No se encontraron productos con ese nombre.");
        } else {
            mostrarProductos(productos);
        }
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}

     // Opción 5 
public void buscarProductoPorMarca() {
    System.out.print("Ingrese la marca a buscar: ");
    String marca = scanner.nextLine();

    try {
        List<Producto> productos = productoService.getListByBrand(marca);

        if (productos.isEmpty()) {
            System.out.println("No se encontraron productos con esa marca.");
        } else {
            mostrarProductos(productos);
        }
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}
     
     //Opcion 6
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
     
     //Option 7
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
     
     // opcion 8
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
     
     //Opcion 9
     public void buscarCodigoPorId() {
        try {
            System.out.print("ID a buscar: ");
            long id = Long.parseLong(scanner.nextLine());

            CodigoBarras cb = cbService.getById(id);
            if (cb == null) {
                System.out.println("No se encontró el código.");
                return;
            }

            buscarProductoPorId(cb.getIdProducto());
        } catch (Exception e) {
            System.err.println("Error al buscar código: " + e.getMessage());
        }
    }
     
     //Opcion 10
     
     public void buscarCodigoBarrasPorValor() {
    System.out.print("Ingrese el valor del código de barras: ");
    String valor = scanner.nextLine();

    try {
        CodigoBarras cb = cbService.getByValor(valor);

        if (cb == null) {
            System.out.println("No se encontró un código de barras con ese valor.");
        } else {
            System.out.println(cb);
        }
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}
     
     //Opcion 11
     public void actualizarCodigo() {
        try {
            System.out.print("ID del código a actualizar: ");
            long id = Long.parseLong(scanner.nextLine());

            CodigoBarras cb = cbService.getById(id);
            if (cb == null) {
                System.out.println("Código no encontrado.");
                return;
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
     
    // --- CÓDIGO DE BARRAS 

    public void crearCodigo() {
        try {
            System.out.println("\n--- Crear Código de Barras ---");
            System.out.print("ID: ");
            long id;
            
            do{
                id = Long.parseLong(scanner.nextLine());
                if (cbService.getById(id) != null)
                    System.out.println("El ID del código de barras ya existe. Intente otro.");
                
            }while(cbService.getById(id) != null);

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
    
}