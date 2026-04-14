import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

class Producto {

    String id, nombre;
    double precio;
    int cantidadVendida = 0;

    Producto(String id, String n, double p) {
        this.id = id;
        this.nombre = n;
        this.precio = p;
    }

    public String getId() {
        return id;
    }
}

class Vendedor {

    String tipoDoc, numDoc, nombres, apellidos;
    double ventasTotales = 0.0;

    Vendedor(String td, String nd, String n, String a) {
        this.tipoDoc = td;
        this.numDoc = nd;
        this.nombres = n;
        this.apellidos = a;
    }

    public String getNumDoc() {
        return numDoc;
    }
}

public class main {

    public static void main(String[] args) {

        try {

            System.out.println("Iniciando...");

            Map<String, Producto> mapaProductos = cargarDatos("productos.csv", linea -> {
                String[] d = linea.split(";");
                return new Producto(d[0], d[1], Double.parseDouble(d[2]));
            }, Producto::getId);

            Map<String, Vendedor> mapaVendedores = cargarDatos("vendedores.csv", linea -> {
                String[] d = linea.split(";");
                return new Vendedor(d[0], d[1], d[2], d[3]);
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

    private static <T> Map<String, T> cargarDatos(
            String archivo,
            java.util.function.Function<String, T> constructor,
            java.util.function.Function<T, String> getKey) throws IOException {

        return Files.lines(Paths.get(archivo))
                .map(constructor)
                .collect(Collectors.toMap(getKey, item -> item));
    }

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
    if (linea.isEmpty()) continue;

    String[] datos = linea.split(";");
    if (datos.length < 2) {
        System.err.println("  [AVISO] " + archivo.getFileName() + " línea " + (i+1) + ": formato incorrecto, se omite.");
        continue;
    }

    String idProducto = datos[0].trim();
    Producto producto = prods.get(idProducto);

    if (producto == null) {
        System.err.println("  [AVISO] " + archivo.getFileName() + " línea " + (i+1) + ": ID de producto '" + idProducto + "' no existe en el catálogo.");
        continue;
    }

    int cantidad;
    try {
        cantidad = Integer.parseInt(datos[1].trim());
    } catch (NumberFormatException e) {
        System.err.println("  [AVISO] " + archivo.getFileName() + " línea " + (i+1) + ": cantidad no numérica '" + datos[1] + "'.");
        continue;
    }

    if (cantidad <= 0) {
        System.err.println("  [AVISO] " + archivo.getFileName() + " línea " + (i+1) + ": cantidad no positiva (" + cantidad + ").");
        continue;
    }

    vendedor.ventasTotales += producto.precio * cantidad;
    producto.cantidadVendida += cantidad;
} 

        } catch (Exception e) {
            System.err.println("ADVERTENCIA: " + archivo.getFileName());
        }
    }

    private static void generarReportes(
            Map<String, Vendedor> mapaVendedores,
            Map<String, Producto> mapaProductos) throws IOException {

        List<Vendedor> vendedoresOrdenados = mapaVendedores.values().stream()
                .sorted(Comparator.comparingDouble(v -> -v.ventasTotales))
                .collect(Collectors.toList());

        try (PrintWriter writer = new PrintWriter("reporte_vendedores.csv")) {
            for (Vendedor v : vendedoresOrdenados) {
                writer.printf("%s %s;%.2f%n", v.nombres, v.apellidos, v.ventasTotales);
            }
        }

        List<Producto> productosOrdenados = mapaProductos.values().stream()
                .sorted(Comparator.comparingInt(p -> -p.cantidadVendida))
                .collect(Collectors.toList());

        try (PrintWriter writer = new PrintWriter("reporte_productos.csv")) {
            for (Producto p : productosOrdenados) {
                writer.printf("%s;%.2f%n", p.nombre, p.precio);
            }
        }
    }
}