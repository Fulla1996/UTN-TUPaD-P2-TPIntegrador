package prog2int.Main;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import prog2int.Models.CodigoBarras;
import prog2int.Models.TipoCB;
import prog2int.Models.Producto;
import prog2int.Service.CodigoBarrasServiceImpl;
import prog2int.Service.ProductoServiceImpl;

/**
 * Controlador de las operaciones del menú (Menu Handler).
 * Gestiona toda la lógica de interacción con el usuario para operaciones CRUD.
 *
 * Responsabilidades:
 * - Capturar entrada del usuario desde consola (Scanner)
 * - Validar entrada básica (conversión de tipos, valores vacíos)
 * - Invocar servicios de negocio (PersonaService, DomicilioService)
 * - Mostrar resultados y mensajes de error al usuario
 * - Coordinar operaciones complejas (crear persona con domicilio, etc.)
 *
 * Patrón: Controller (MVC) - capa de presentación en arquitectura de 4 capas
 * Arquitectura: Main → Service → DAO → Models
 *
 * IMPORTANTE: Este handler NO contiene lógica de negocio.
 * Todas las validaciones de negocio están en la capa Service.
 */
public class MenuHandler {
    /**
     * Scanner compartido para leer entrada del usuario.
     * Inyectado desde AppMenu para evitar múltiples Scanners de System.in.
     */
    private final Scanner scanner;

    /**
     * Servicio de personas para operaciones CRUD.
     * También proporciona acceso a DomicilioService mediante getDomicilioService().
     */
    private final ProductoServiceImpl productoService;
    private final CodigoBarrasServiceImpl cbService;

    /**
     * Constructor con inyección de dependencias.
     * Valida que las dependencias no sean null (fail-fast).
     *
     * @param scanner Scanner compartido para entrada de usuario
     * @param personaService Servicio de personas
     * @throws IllegalArgumentException si alguna dependencia es null
     */
    public MenuHandler(Scanner scanner, ProductoServiceImpl productoService, CodigoBarrasServiceImpl cbService) {
        if (scanner == null ) {
            throw new IllegalArgumentException("Scanner no puede ser null");
        }
        if (productoService == null) {
            throw new IllegalArgumentException("ProductoService no puede ser null");
        }
        if (cbService == null){
            throw new IllegalArgumentException("CodigoBarrasService no puede ser null");
        }
        this.scanner = scanner;
        this.productoService = productoService;
        this.cbService = cbService;
    }

    /**
     * Opción 1: Crear nueva persona (con domicilio opcional).
     *
     * Flujo:
     * 1. Solicita nombre, apellido y DNI
     * 2. Pregunta si desea agregar domicilio
     * 3. Si sí, captura calle y número
     * 4. Crea objeto Persona y opcionalmente Domicilio
     * 5. Invoca personaService.insertar() que:
     *    - Valida datos (nombre, apellido, DNI obligatorios)
     *    - Valida DNI único (RN-001)
     *    - Si hay domicilio, lo inserta primero (obtiene ID)
     *    - Inserta persona con FK domicilio_id correcta
     *
     * Input trimming: Aplica .trim() a todas las entradas (patrón consistente).
     *
     * Manejo de errores:
     * - IllegalArgumentException: Validaciones de negocio (muestra mensaje al usuario)
     * - SQLException: Errores de BD (muestra mensaje al usuario)
     * - Todos los errores se capturan y muestran, NO se propagan al menú principal
     */
    public void crearCodigo() {
        try {
            System.out.println("\n--- Crear Código de Barras ---");

            System.out.print("ID: ");
            long id = Long.parseLong(scanner.nextLine());

            System.out.print("Tipo (EAN13/EAN8/UPC): ");
            String tipoInput = scanner.nextLine().trim().toUpperCase();
            TipoCB tipo = TipoCB.valueOf(tipoInput);

            System.out.print("Valor: ");
            String valor = scanner.nextLine().trim();

            System.out.print("Observaciones: ");
            String obs = scanner.nextLine().trim();

            CodigoBarras cb = new CodigoBarras(id, tipo.name(), valor, new Date(), obs);
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
    
    /**
     * Opción 2: Listar personas (todas o filtradas por nombre/apellido).
     *
     * Submenú:
     * 1. Listar todas las personas activas (getAll)
     * 2. Buscar por nombre o apellido con LIKE (buscarPorNombreApellido)
     *
     * Muestra:
     * - ID, Nombre, Apellido, DNI
     * - Domicilio (si tiene): Calle Número
     *
     * Manejo de casos especiales:
     * - Si no hay personas: Muestra "No se encontraron personas"
     * - Si la persona no tiene domicilio: Solo muestra datos de persona
     *
     * Búsqueda por nombre/apellido:
     * - Usa PersonaDAO.buscarPorNombreApellido() que hace LIKE '%filtro%'
     * - Insensible a mayúsculas en MySQL (depende de collation)
     * - Busca en nombre O apellido
     */
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

    /**
     * Opción 3: Actualizar persona existente.
     *
     * Flujo:
     * 1. Solicita ID de la persona
     * 2. Obtiene persona actual de la BD
     * 3. Muestra valores actuales y permite actualizar:
     *    - Nombre (Enter para mantener actual)
     *    - Apellido (Enter para mantener actual)
     *    - DNI (Enter para mantener actual)
     * 4. Llama a actualizarDomicilioDePersona() para manejar cambios en domicilio
     * 5. Invoca personaService.actualizar() que valida:
     *    - Datos obligatorios (nombre, apellido, DNI)
     *    - DNI único (RN-001), excepto para la misma persona
     *
     * Patrón "Enter para mantener":
     * - Lee input con scanner.nextLine().trim()
     * - Si isEmpty() → NO actualiza el campo (mantiene valor actual)
     * - Si tiene valor → Actualiza el campo
     *
     * IMPORTANTE: Esta operación NO actualiza el domicilio directamente.
     * El domicilio se maneja en actualizarDomicilioDePersona() que puede:
     * - Actualizar domicilio existente (afecta a TODAS las personas que lo comparten)
     * - Agregar nuevo domicilio si la persona no tenía
     * - Dejar domicilio sin cambios
     */
    public void actualizarCodigo() {
        try {
            System.out.print("ID del código a actualizar: ");
            long id = Long.parseLong(scanner.nextLine());

            CodigoBarras cb = cbService.getById(id);
            if (cb == null) {
                System.out.println("Código no encontrado.");
                return;
            }

            // Mantener valor si usuario presiona Enter
            System.out.print("Nuevo valor (" + cb.getValor() + "): ");
            String nuevoValor = scanner.nextLine().trim();
            if (!nuevoValor.isEmpty()) cb.setValor(nuevoValor);

            System.out.print("Observaciones (" + cb.getObservaciones() + "): ");
            String obs = scanner.nextLine().trim();
            if (!obs.isEmpty()) cb.setObservaciones(obs);

            cbService.actualizar(cb);
            System.out.println("✔ Código actualizado.");

        } catch (Exception e) {
            System.err.println("Error al actualizar código: " + e.getMessage());
        }
    }

    /**
     * Opción 4: Eliminar persona (soft delete).
     *
     * Flujo:
     * 1. Solicita ID de la persona
     * 2. Invoca personaService.eliminar() que:
     *    - Marca persona.eliminado = TRUE
     *    - NO elimina el domicilio asociado (RN-037)
     *
     * IMPORTANTE: El domicilio NO se elimina porque:
     * - Múltiples personas pueden compartir un domicilio
     * - Si se eliminara, afectaría a otras personas
     *
     * Si se quiere eliminar también el domicilio:
     * - Usar opción 10: "Eliminar domicilio de una persona" (eliminarDomicilioPorPersona)
     * - Esa opción primero desasocia el domicilio, luego lo elimina (seguro)
     */
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

    /**
     * Opción 5: Crear domicilio independiente (sin asociar a persona).
     *
     * Flujo:
     * 1. Llama a crearDomicilio() para capturar calle y número
     * 2. Invoca domicilioService.insertar() que:
     *    - Valida calle y número obligatorios (RN-023)
     *    - Inserta en BD y asigna ID autogenerado
     * 3. Muestra ID generado
     *
     * Uso típico:
     * - Crear domicilio que luego se asignará a varias personas (opción 7)
     * - Pre-cargar domicilios en la BD
     */
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

            ///Completar ingreso de información de codigo de barras
            System.out.println("CodigoBarras");
            CodigoBarras cb = new CodigoBarras();
            
            
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
   
    /**
     * Opción 6: Listar todos los domicilios activos.
     *
     * Muestra: ID, Calle Número
     *
     * Uso típico:
     * - Ver domicilios disponibles antes de asignar a persona (opción 7)
     * - Consultar ID de domicilio para actualizar (opción 9) o eliminar (opción 8)
     *
     * Nota: Solo muestra domicilios con eliminado=FALSE (soft delete).
     */
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

    /**
     * Opción 9: Actualizar domicilio por ID.
     *
     * Flujo:
     * 1. Solicita ID del domicilio
     * 2. Obtiene domicilio actual de la BD
     * 3. Muestra valores actuales y permite actualizar:
     *    - Calle (Enter para mantener actual)
     *    - Número (Enter para mantener actual)
     * 4. Invoca domicilioService.actualizar()
     *
     * ⚠️ IMPORTANTE (RN-040): Si varias personas comparten este domicilio,
     * la actualización los afectará a TODAS.
     *
     * Ejemplo:
     * - Domicilio ID=1 "Av. Siempreviva 742" está asociado a 3 personas
     * - Si se actualiza a "Calle Nueva 123", las 3 personas tendrán la nueva dirección
     *
     * Esto es CORRECTO para familias que viven juntas.
     * Si se quiere cambiar la dirección de UNA sola persona:
     * 1. Crear nuevo domicilio (opción 5)
     * 2. Asignar a la persona (opción 7)
     */
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
            if (!nombre.isEmpty()) p.setNombre(nombre);

            System.out.print("Nueva marca (" + p.getMarca() + "): ");
            String marca = scanner.nextLine().trim();
            if (!marca.isEmpty()) p.setMarca(marca);

            System.out.print("Nueva categoría (" + p.getCategoria() + "): ");
            String cat = scanner.nextLine().trim();
            if (!cat.isEmpty()) p.setCategoria(cat);

            System.out.print("Nuevo precio (" + p.getPrecio() + "): ");
            String precioIn = scanner.nextLine().trim();
            if (!precioIn.isEmpty()) p.setPrecio(Double.parseDouble(precioIn));

            System.out.print("Nuevo peso (" + p.getPeso() + "): ");
            String pesoIn = scanner.nextLine().trim();
            if (!pesoIn.isEmpty()) p.setPeso(Double.parseDouble(pesoIn));

            productoService.actualizar(p);
            System.out.println("✔ Producto actualizado.");

        } catch (Exception e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
        }
    }

    /**
     * Opción 8: Eliminar domicilio por ID (PELIGROSO - soft delete directo).
     *
     * ⚠️ PELIGRO (RN-029): Este método NO verifica si hay personas asociadas.
     * Si hay personas con FK a este domicilio, quedarán con referencia huérfana.
     *
     * Flujo:
     * 1. Solicita ID del domicilio
     * 2. Invoca domicilioService.eliminar() directamente
     * 3. Marca domicilio.eliminado = TRUE
     *
     * Problemas potenciales:
     * - Personas con domicilio_id apuntando a domicilio "eliminado"
     * - Datos inconsistentes en la BD
     *
     * ALTERNATIVA SEGURA: Opción 10 (eliminarDomicilioPorPersona)
     * - Primero desasocia domicilio de la persona (domicilio_id = NULL)
     * - Luego elimina el domicilio
     * - Garantiza consistencia
     *
     * Uso válido:
     * - Cuando se está seguro de que el domicilio NO tiene personas asociadas
     * - Limpiar domicilios creados por error
     */
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

    /**
     * Opción 7: Actualizar domicilio de una persona específica.
     *
     * Flujo:
     * 1. Solicita ID de la persona
     * 2. Verifica que la persona exista y tenga domicilio
     * 3. Muestra valores actuales del domicilio
     * 4. Permite actualizar calle y número
     * 5. Invoca domicilioService.actualizar()
     *
     * ⚠️ IMPORTANTE (RN-040): Esta operación actualiza el domicilio compartido.
     * Si otras personas tienen el mismo domicilio, también se les actualizará.
     *
     * Diferencia con opción 9 (actualizarDomicilioPorId):
     * - Esta opción: Busca persona primero, luego actualiza su domicilio
     * - Opción 9: Actualiza domicilio directamente por ID
     *
     * Ambas tienen el mismo efecto (RN-040): afectan a TODAS las personas
     * que comparten el domicilio.
     */
     // public void actualizarDomicilioPorPersona() {
        /*try {
            System.out.print("ID de la persona cuyo domicilio desea actualizar: ");
            int personaId = Integer.parseInt(scanner.nextLine());
            Persona p = personaService.getById(personaId);

            if (p == null) {
                System.out.println("Persona no encontrada.");
                return;
            }

            if (p.getDomicilio() == null) {
                System.out.println("La persona no tiene domicilio asociado.");
                return;
            }

            Domicilio d = p.getDomicilio();
            System.out.print("Nueva calle (" + d.getCalle() + "): ");
            String calle = scanner.nextLine().trim();
            if (!calle.isEmpty()) {
                d.setCalle(calle);
            }

            System.out.print("Nuevo numero (" + d.getNumero() + "): ");
            String numero = scanner.nextLine().trim();
            if (!numero.isEmpty()) {
                d.setNumero(numero);
            }

            personaService.getDomicilioService().actualizar(d);
            System.out.println("Domicilio actualizado exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al actualizar domicilio: " + e.getMessage());
        }*/

    /**
     * Opción 10: Eliminar domicilio de una persona (MÉTODO SEGURO - RN-029 solucionado).
     *
     * Flujo transaccional SEGURO:
     * 1. Solicita ID de la persona
     * 2. Verifica que la persona exista y tenga domicilio
     * 3. Invoca personaService.eliminarDomicilioDePersona() que:
     *    a. Desasocia domicilio de persona (persona.domicilio = null)
     *    b. Actualiza persona en BD (domicilio_id = NULL)
     *    c. Elimina el domicilio (ahora no hay FKs apuntando a él)
     *
     * Ventaja sobre opción 8 (eliminarDomicilioPorId):
     * - Garantiza consistencia: Primero actualiza FK, luego elimina
     * - NO deja referencias huérfanas
     * - Implementa eliminación segura recomendada en RN-029
     *
     * Este es el método RECOMENDADO para eliminar domicilios en producción.
     */
    // public void eliminarDomicilioPorPersona() {
        /*try {
            System.out.print("ID de la persona cuyo domicilio desea eliminar: ");
            int personaId = Integer.parseInt(scanner.nextLine());
            Persona p = personaService.getById(personaId);

            if (p == null) {
                System.out.println("Persona no encontrada.");
                return;
            }

            if (p.getDomicilio() == null) {
                System.out.println("La persona no tiene domicilio asociado.");
                return;
            }

            int domicilioId = p.getDomicilio().getId();
            personaService.eliminarDomicilioDePersona(personaId, domicilioId);
            System.out.println("Domicilio eliminado exitosamente y referencia actualizada.");
        } catch (Exception e) {
            System.err.println("Error al eliminar domicilio: " + e.getMessage());
        }*/
    

    /**
     * Método auxiliar privado: Crea un objeto Domicilio capturando calle y número.
     *
     * Flujo:
     * 1. Solicita calle (con trim)
     * 2. Solicita número (con trim)
     * 3. Crea objeto Domicilio con ID=0 (será asignado por BD al insertar)
     *
     * Usado por:
     * - crearPersona(): Para agregar domicilio al crear persona
     * - crearDomicilioIndependiente(): Para crear domicilio sin asociar
     * - actualizarDomicilioDePersona(): Para agregar domicilio a persona sin domicilio
     *
     * Nota: NO persiste en BD, solo crea el objeto en memoria.
     * El caller es responsable de insertar el domicilio.
     *
     * @return Domicilio nuevo (no persistido, ID=0)
     */
    /**
     * Método auxiliar privado: Maneja actualización de domicilio dentro de actualizar persona.
     *
     * Casos:
     * 1. Persona TIENE domicilio:
     *    - Pregunta si desea actualizar
     *    - Si sí, permite cambiar calle y número (Enter para mantener)
     *    - Actualiza domicilio en BD (afecta a TODAS las personas que lo comparten)
     *
     * 2. Persona NO TIENE domicilio:
     *    - Pregunta si desea agregar uno
     *    - Si sí, captura calle y número con crearDomicilio()
     *    - Inserta domicilio en BD (obtiene ID)
     *    - Asocia domicilio a la persona
     *
     * Usado exclusivamente por actualizarPersona() (opción 3).
     *
     * IMPORTANTE: El parámetro Persona se modifica in-place (setDomicilio).
     * El caller debe invocar personaService.actualizar() después para persistir.
     *
     * @param p Persona a la que se le actualizará/agregará domicilio
     * @throws Exception Si hay error al insertar/actualizar domicilio
     */
    // private void actualizarDomicilioDePersona(Persona p) throws Exception {
        /*if (p.getDomicilio() != null) {
            System.out.print("¿Desea actualizar el domicilio? (s/n): ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                System.out.print("Nueva calle (" + p.getDomicilio().getCalle() + "): ");
                String calle = scanner.nextLine().trim();
                if (!calle.isEmpty()) {
                    p.getDomicilio().setCalle(calle);
                }

                System.out.print("Nuevo numero (" + p.getDomicilio().getNumero() + "): ");
                String numero = scanner.nextLine().trim();
                if (!numero.isEmpty()) {
                    p.getDomicilio().setNumero(numero);
                }

                personaService.getDomicilioService().actualizar(p.getDomicilio());
            }
        } else {
            System.out.print("La persona no tiene domicilio. ¿Desea agregar uno? (s/n): ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                Domicilio nuevoDom = crearDomicilio();
                personaService.getDomicilioService().insertar(nuevoDom);
                p.setDomicilio(nuevoDom);
            }
        }*/
    
}