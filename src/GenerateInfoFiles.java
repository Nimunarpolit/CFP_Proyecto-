import java.io.PrintWriter;
import java.util.Random;


/**
 * Clase encargada de generar los archivos de entrada del proyecto.
 * <p>
 * Su función principal es crear de manera pseudoaleatoria la información de
 * productos, vendedores y ventas individuales por vendedor. Estos archivos son
 * utilizados posteriormente por la clase {@code main}, la cual procesa los
 * datos y genera los reportes solicitados en la actividad.
 * </p>
 */
public class GenerateInfoFiles {

    /** Lista de nombres usados para generar vendedores de prueba. */
    private static final String[] NOMBRES = {"Nicolas", "Ana", "Luis", "Maria", "Juan"};

    /** Lista de apellidos usados para generar vendedores de prueba. */
    private static final String[] APELLIDOS = {"Munar", "Perez", "Rodriguez", "Martinez"};

    /** Tipos de documento posibles para los vendedores. */
    private static final String[] TIPOS_DOC = {"CC", "CE", "TI"};

    /** Nombres de productos incluidos en el catálogo. */
    private static final String[] PRODUCTOS_NOMBRES = {"Laptop", "Mouse", "Teclado", "Monitor"};

    /** Precios asociados a cada producto del catálogo. */
    private static final double[] PRODUCTOS_PRECIOS = {2500000.50, 80000.00, 150000.99, 950000.00};

    /** Generador pseudoaleatorio reutilizado en toda la clase. */
    private static final Random RANDOM = new Random();

    /**
     * Método principal de la clase.
     * Ejecuta la generación del archivo de productos y del archivo maestro de
     * vendedores. A su vez, por cada vendedor creado también genera su archivo
     * individual de ventas.
     */
    public static void main(String[] args) {
        try {
            //Mensajes generados por consola.
            System.out.println("Iniciando generación de archivos...");
            createProductsFile(PRODUCTOS_NOMBRES.length);
            createSalesManInfoFile(3);
            System.out.println("¡Archivos generados exitosamente!");
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Genera el archivo {@code productos.csv} con el catálogo de productos.
     * Cada línea contiene el identificador del producto, su nombre y su precio.
     *
     * @param productsCount cantidad de productos a escribir en el archivo.
     * @throws Exception si ocurre un error durante la creación del archivo.
     */
    public static void createProductsFile(int productsCount) throws Exception {
        try (PrintWriter writer = new PrintWriter("productos.csv", "UTF-8")) {
            for (int i = 0; i < productsCount; i++) {
                writer.println((i + 1) + ";" + PRODUCTOS_NOMBRES[i] + ";" + PRODUCTOS_PRECIOS[i]);
            }
        }
    }

    /**
     * Genera el archivo {@code vendedores.csv} con la información general de los
     * vendedores y, adicionalmente, crea un archivo individual de ventas para
     * cada uno de ellos.
     * <p>
     * En este método se garantiza la coherencia entre el tipo de documento que
     * aparece en el archivo maestro de vendedores y el que queda registrado en
     * el archivo individual de ventas de cada vendedor.
     * </p>
     *
     * @param salesmanCount cantidad de vendedores que se desea generar.
     * @throws Exception si ocurre un error durante la creación de los archivos.
     */
    public static void createSalesManInfoFile(int salesmanCount) throws Exception {
        try (PrintWriter writer = new PrintWriter("vendedores.csv", "UTF-8")) {
            for (int i = 0; i < salesmanCount; i++) {
                String tipoDocumento = TIPOS_DOC[RANDOM.nextInt(TIPOS_DOC.length)];
                long id = 100000000L + RANDOM.nextInt(900000000);
                String nombre = NOMBRES[RANDOM.nextInt(NOMBRES.length)];
                String apellido = APELLIDOS[RANDOM.nextInt(APELLIDOS.length)];

                writer.println(tipoDocumento + ";" + id + ";" + nombre + ";" + apellido);
                createSalesMenFile(RANDOM.nextInt(4) + 2, nombre, id, tipoDocumento);
            }
        }
    }

    /**
     * Método solicitado en la guía del proyecto para crear el archivo de ventas
     * de un vendedor. Se conserva esta firma por compatibilidad con la especificación.
     * <p>
     * Internamente delega la creación del archivo a una versión sobrecargada del
     * método, la cual también recibe el tipo de documento para mantener coherencia
     * entre los archivos generados.
     * </p>
     *
     * @param randomSalesCount cantidad pseudoaleatoria de ventas a registrar.
     * @param name nombre del vendedor, recibido según la especificación.
     * @param id identificación del vendedor.
     * @throws Exception si ocurre un error durante la creación del archivo.
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) throws Exception {
        createSalesMenFile(randomSalesCount, name, id, TIPOS_DOC[RANDOM.nextInt(TIPOS_DOC.length)]);
    }

    /**
     * Versión interna del método que crea el archivo de ventas del vendedor,
     * garantizando que el tipo de documento coincida con el registrado en el
     * archivo maestro de vendedores.
     *
     * @param randomSalesCount cantidad de ventas a registrar en el archivo.
     * @param name nombre del vendedor.
     * @param id identificación del vendedor.
     * @param tipoDocumento tipo de documento que se escribirá en la cabecera del archivo.
     * @throws Exception si ocurre un error durante la creación del archivo.
     */
    private static void createSalesMenFile(int randomSalesCount, String name, long id, String tipoDocumento)
            throws Exception {
        String fileName = "vendedor_" + id + ".csv";

        try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
            writer.println(tipoDocumento + ";" + id);

            for (int i = 0; i < randomSalesCount; i++) {
                int productoId = RANDOM.nextInt(PRODUCTOS_NOMBRES.length) + 1;
                int cantidadVendida = RANDOM.nextInt(10) + 1;
                writer.println(productoId + ";" + cantidadVendida + ";");
            }
        }
    }
}
