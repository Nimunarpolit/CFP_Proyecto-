import java.io.PrintWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Representa un producto del catálogo cargado desde el archivo de productos.
 * Cada objeto almacena el identificador, el nombre, el precio unitario y la
 * cantidad total vendida durante el procesamiento de los archivos de ventas.
 */
class Producto {

    /** Identificador único del producto. */
    String id;

    /** Nombre del producto. */
    String nombre;

    /** Precio unitario del producto. */
    double precio;

    /** Cantidad total vendida del producto durante el procesamiento. */
    int cantidadVendida = 0;

    /**
     * Construye un nuevo producto con sus datos básicos.
     *
     * @param id identificador del producto.
     * @param n nombre del producto.
     * @param p precio unitario del producto.
     */
    Producto(String id, String n, double p) {
        this.id = id;
        this.nombre = n;
        this.precio = p;
    }

    /**
     * Retorna el identificador del producto.
     *
     * @return identificador del producto.
     */
    public String getId() {
        return id;
    }
}

/**
 * Representa un vendedor cargado desde el archivo maestro de vendedores.
 * Cada objeto conserva sus datos personales básicos y acumula el valor total
 * de las ventas registradas en sus archivos individuales.
 */
class Vendedor {

    /** Tipo de documento del vendedor. */
    String tipoDoc;

    /** Número de documento del vendedor. */
    String numDoc;

    /** Nombres del vendedor. */
    String nombres;

    /** Apellidos del vendedor. */
    String apellidos;

    /** Total de dinero recaudado por el vendedor. */
    double ventasTotales = 0.0;

    /**
     * Construye un nuevo vendedor con sus datos básicos.
     *
     * @param td tipo de documento.
     * @param nd número de documento.
     * @param n nombres del vendedor.
     * @param a apellidos del vendedor.
     */
    Vendedor(String td, String nd, String n, String a) {
        this.tipoDoc = td;
        this.numDoc = nd;
        this.nombres = n;
        this.apellidos = a;
    }

    /**
     * Retorna el número de documento del vendedor.
     *
     * @return número de documento del vendedor.
     */
    public String getNumDoc() {
        return numDoc;
    }
}

/**
 * Clase principal encargada de procesar los archivos CSV generados previamente.
 * <p>
 * Su responsabilidad es cargar la información de productos y vendedores,
 * recorrer los archivos individuales de ventas, calcular el total vendido por
 * cada vendedor y la cantidad total vendida por cada producto, y finalmente
 * generar los reportes solicitados por la actividad.
 * </p>
 */
public class main {

    /**
     * Método principal del programa.
     * Ejecuta la lectura de archivos de entrada, procesa las ventas y crea los
     * archivos finales de reporte.
     *
     * @param args argumentos de línea de comandos, no utilizados en este programa.
     */
    public static void main(String[] args) {

        try {
            System.out.println("Iniciando...");

            Map<String, Producto> mapaProductos = cargarDatos("productos.csv", linea -> {
                String[] datosProducto = linea.split(";");
                return new Producto(datosProducto[0], datosProducto[1], Double.parseDouble(datosProducto[2]));
            }, Producto::getId);

            Map<String, Vendedor> mapaVendedores = cargarDatos("vendedores.csv", linea -> {
                String[] datosVendedor = linea.split(";");
                return new Vendedor(datosVendedor[0], datosVendedor[1], datosVendedor[2], datosVendedor[3]);
            }, Vendedor::getNumDoc);

            Files.walk(Paths.get("."))
                    .filter(path -> path.getFileName().toString().startsWith("vendedor_"))
                    .forEach(path -> procesarArchivoVenta(path, mapaProductos, mapaVendedores));

            generarReportes(mapaVendedores, mapaProductos);

            System.out.println("¡Reportes generados!");

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Carga un archivo CSV y transforma cada línea en un objeto del tipo indicado.
     * El resultado se almacena en un mapa, usando una clave obtenida a partir de
     * cada objeto creado.
     *
     * @param archivo nombre del archivo que se va a cargar.
     * @param constructor función que transforma una línea en un objeto.
     * @param getKey función que obtiene la clave con la que se almacenará el objeto.
     * @param <T> tipo de objeto que se construirá al leer cada línea.
     * @return mapa con los datos cargados desde el archivo.
     * @throws IOException si ocurre un error al leer el archivo.
     */
    private static <T> Map<String, T> cargarDatos(
            String archivo,
            java.util.function.Function<String, T> constructor,
            java.util.function.Function<T, String> getKey) throws IOException {

        return Files.lines(Paths.get(archivo))
                .filter(linea -> !linea.trim().isEmpty())
                .map(constructor)
                .collect(Collectors.toMap(getKey, item -> item));
    }

    /**
     * Procesa el archivo de ventas de un vendedor.
     * <p>
     * El método identifica al vendedor, recorre cada línea de ventas, valida el
     * formato básico y acumula tanto el valor total vendido por el vendedor como
     * la cantidad total vendida de cada producto.
     * </p>
     *
     * @param archivo archivo individual de ventas del vendedor.
     * @param prods mapa de productos cargados desde el catálogo.
     * @param vends mapa de vendedores cargados desde el archivo maestro.
     */
    private static void procesarArchivoVenta(
            Path archivo,
            Map<String, Producto> prods,
            Map<String, Vendedor> vends) {

        try {
            List<String> lineas = Files.readAllLines(archivo);

            String idVendedor = lineas.get(0).split(";")[1];
            Vendedor vendedor = vends.get(idVendedor);

            if (vendedor == null) {
                return;
            }

            for (int i = 1; i < lineas.size(); i++) {
                String linea = lineas.get(i).trim();

                if (linea.isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";");

                if (datos.length < 2) {
                    System.err.println("  [AVISO] " + archivo.getFileName() + " línea " + (i + 1)
                            + ": formato incorrecto, se omite.");
                    continue;
                }

                String idProducto = datos[0].trim();
                Producto producto = prods.get(idProducto);

                if (producto == null) {
                    System.err.println("  [AVISO] " + archivo.getFileName() + " línea " + (i + 1)
                            + ": ID de producto '" + idProducto + "' no existe en el catálogo.");
                    continue;
                }

                int cantidad;
                try {
                    cantidad = Integer.parseInt(datos[1].trim());
                } catch (NumberFormatException e) {
                    System.err.println("  [AVISO] " + archivo.getFileName() + " línea " + (i + 1)
                            + ": cantidad no numérica '" + datos[1] + "'.");
                    continue;
                }

                if (cantidad <= 0) {
                    System.err.println("  [AVISO] " + archivo.getFileName() + " línea " + (i + 1)
                            + ": cantidad no positiva (" + cantidad + ").");
                    continue;
                }

                vendedor.ventasTotales += producto.precio * cantidad;
                producto.cantidadVendida += cantidad;
            }

        } catch (Exception e) {
            System.err.println("ADVERTENCIA: " + archivo.getFileName());
        }
    }

    /**
     * Genera los dos reportes finales solicitados por la actividad.
     * <p>
     * El primer reporte organiza a los vendedores de mayor a menor según el valor
     * total recaudado. El segundo reporte organiza los productos de mayor a menor
     * según la cantidad vendida.
     * </p>
     *
     * @param mapaVendedores mapa con los vendedores y sus ventas acumuladas.
     * @param mapaProductos mapa con los productos y sus cantidades vendidas.
     * @throws IOException si ocurre un error al escribir los archivos de salida.
     */
    private static void generarReportes(
            Map<String, Vendedor> mapaVendedores,
            Map<String, Producto> mapaProductos) throws IOException {

        List<Vendedor> vendedoresOrdenados = mapaVendedores.values().stream()
                .sorted(Comparator.comparingDouble(vendedor -> -vendedor.ventasTotales))
                .collect(Collectors.toList());

        try (PrintWriter writer = new PrintWriter("reporte_vendedores.csv")) {
            for (Vendedor vendedor : vendedoresOrdenados) {
                writer.printf("%s %s;%.2f%n", vendedor.nombres, vendedor.apellidos, vendedor.ventasTotales);
            }
        }

        List<Producto> productosOrdenados = mapaProductos.values().stream()
                .sorted(Comparator.comparingInt(producto -> -producto.cantidadVendida))
                .collect(Collectors.toList());

        try (PrintWriter writer = new PrintWriter("reporte_productos.csv")) {
            for (Producto producto : productosOrdenados) {
                writer.printf("%s;%.2f;%d%n", producto.nombre, producto.precio, producto.cantidadVendida);
            }
        }
    }
}
