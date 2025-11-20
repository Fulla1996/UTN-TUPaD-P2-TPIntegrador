package prog2int.Main;

/**
 * Clase utilitaria para mostrar el menú de la aplicación.
 * Solo contiene métodos estáticos de visualización (no tiene estado).
 *
 * Responsabilidades:
 * - Mostrar el menú principal con todas las opciones disponibles
 * - Formatear la salida de forma consistente
 *
 * Patrón: Utility class (solo métodos estáticos, no instanciable)
 *
 * IMPORTANTE: Esta clase NO lee entrada del usuario.
 * Solo muestra el menú. AppMenu es responsable de leer la opción.
 */
public class MenuDisplay {
    /**
    Muestra el menú principal con todas las opciones CRUD. 
    */
    public static void mostrarMenuPrincipal() {
        System.out.println("\n========= MENU PRINCIPAL =========");
        
        System.out.println("---- PRODUCTOS ----");
        System.out.println("1. Listar Productos");
        System.out.println("2. Crear Producto");
        System.out.println("3. Buscar Producto por ID");
        System.out.println("4. Buscar Producto por Nombre");
        System.out.println("5. Buscar Producto por Marca o Categoria");
        System.out.println("6. Editar Producto");
        System.out.println("7. Eliminar Producto");
        
        System.out.println("---- CODIGOS DE BARRA ----");
        System.out.println("8. Listar Codigos de Barra");
        System.out.println("9. Buscar Codigo de Barra por ID");
        System.out.println("10. Buscar Codigo de Barra por Valor");
        System.out.println("11. Agregar observaciones a Codigo de Barra.");
        
        System.out.println("0. Salir");
        System.out.print("Ingrese una opción: ");
    }
}