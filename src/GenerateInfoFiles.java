import java.io.PrintWriter;
import java.util.Random;

public class GenerateInfoFiles {

    // Arrays con datos de prueba para generar información aleatoria
    private static final String[] NOMBRES = {"Carlos", "Ana", "Luis", "Maria", "Juan"};
    private static final String[] APELLIDOS = {"Gomez", "Perez", "Rodriguez", "Martinez"};
    private static final String[] TIPOS_DOC = {"CC", "CE", "TI"};
    private static final String[] PRODUCTOS_NOMBRES = {"Laptop", "Mouse", "Teclado", "Monitor"};
    private static final double[] PRODUCTOS_PRECIOS = {2500000.50, 80000.00, 150000.99, 950000.00};

    public static void main(String[] args) {
        try {
            System.out.println("Iniciando generación de archivos...");
            createProductsFile(PRODUCTOS_NOMBRES.length);
            createSalesManInfoFile(3); // Crear 3 vendedores
            System.out.println("¡Archivos generados exitosamente!");
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    public static void createProductsFile(int productsCount) throws Exception {
        try (PrintWriter writer = new PrintWriter("productos.csv", "UTF-8")) {
            for (int i = 0; i < productsCount; i++) {
                writer.println((i + 1) + ";" + PRODUCTOS_NOMBRES[i] + ";" + PRODUCTOS_PRECIOS[i]);
            }
        }
    }

    public static void createSalesManInfoFile(int salesmanCount) throws Exception {
        Random rand = new Random();
        try (PrintWriter writer = new PrintWriter("vendedores.csv", "UTF-8")) {
            for (int i = 0; i < salesmanCount; i++) {
                long id = 100000000 + rand.nextInt(900000000);
                String nombre = NOMBRES[rand.nextInt(NOMBRES.length)];
                writer.println(TIPOS_DOC[rand.nextInt(TIPOS_DOC.length)] + ";" + id + ";" + nombre + ";" + APELLIDOS[rand.nextInt(APELLIDOS.length)]);
                createSalesMenFile(rand.nextInt(4) + 2, nombre, id);
            }
        }
    }

    public static void createSalesMenFile(int randomSalesCount, String name, long id) throws Exception {
        Random rand = new Random();
        String fileName = "vendedor_" + id + ".csv";
        try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
            writer.println(TIPOS_DOC[rand.nextInt(TIPOS_DOC.length)] + ";" + id);
            for (int i = 0; i < randomSalesCount; i++) {
                writer.println((rand.nextInt(PRODUCTOS_NOMBRES.length) + 1) + ";" + (rand.nextInt(10) + 1) + ";");
            }
        }
    }
}
