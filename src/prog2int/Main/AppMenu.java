package prog2int.Main;

import java.util.Scanner;
import prog2int.Dao.CodigoBarrasDAO;
import prog2int.Dao.ProductoDAO;
import prog2int.Service.CodigoBarrasServiceImpl;
import prog2int.Service.ProductoServiceImpl;

public class AppMenu {

    private final Scanner scanner;
    private final MenuHandler menuHandler;
    private boolean running;
    
    public AppMenu() {
        this.scanner = new Scanner(System.in);
        // Services creados con sus DAOs
        ProductoServiceImpl productoService = createProductoService();
        CodigoBarrasServiceImpl cbService = createCodigoBarrasService();
        // Handler
        this.menuHandler = new MenuHandler(scanner, productoService, cbService);
        this.running = true;
    }

    public static void main(String[] args) {
        AppMenu app = new AppMenu();
        app.run();
    }
    public void run() {
        while (running) {
            try {
                MenuDisplay.mostrarMenuPrincipal();
                int opcion = Integer.parseInt(scanner.nextLine());
                processOption(opcion);
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Por favor, ingrese un numero.");
            }
        }
        scanner.close();
    }
    private void processOption(int opcion) {
        switch (opcion) {
            // ---- PRODUCTOS ----
            case 1 -> menuHandler.listarProductos();     // "Listar Productos"
            case 2 -> menuHandler.crearProducto();       // "Crear Producto"
            case 3 -> menuHandler.buscarProductoPorId(); // "Buscar Producto por ID"
            // case 4 -> menuHandler.buscarProductoPorNombre();
            // case 5 -> menuHandler.buscarProductoPorMarca();
            case 6 -> menuHandler.actualizarProducto();  // "Editar Producto"
            case 7 -> menuHandler.eliminarProducto();    // "Eliminar Producto"
            // ---- CODIGOS DE BARRA ----
            case 8 -> menuHandler.listarCodigo();        // "Listar Codigos de Barra"
            case 9 -> menuHandler.buscarCodigoPorId();   // "Buscar Codigo de Barra por ID"
            // case 10 -> menuHandler.buscarCodigoPorValor();
            case 11 -> menuHandler.actualizarCodigo();   // "Agregar observaciones / editar código"
            // ---- SALIR ----
            case 0 -> {
                System.out.println("Saliendo...");
                running = false;
            }
            default -> System.out.println("Opción no válida.");
        }
    }
    // ---- SERVICES ----
    private ProductoServiceImpl createProductoService() {
        CodigoBarrasDAO cbDAO = new CodigoBarrasDAO();
        ProductoDAO productoDAO = new ProductoDAO(cbDAO);
        CodigoBarrasServiceImpl cbService = new CodigoBarrasServiceImpl(cbDAO);
        return new ProductoServiceImpl(productoDAO, cbService);
    }
    private CodigoBarrasServiceImpl createCodigoBarrasService() {
        CodigoBarrasDAO cbDAO = new CodigoBarrasDAO();
        return new CodigoBarrasServiceImpl(cbDAO);
    }
}
