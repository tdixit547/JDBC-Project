package server;

import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.net.InetSocketAddress;

/**
 * Food Delivery backend server using Java JDBC.
 * Replaces the Express+MongoDB backend entirely.
 * Serves the React frontend and food images.
 */
public class FoodServer {

    private final HttpServer server;

    public FoodServer(int port) throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Single handler for all API routes
        FoodAppHandler apiHandler = new FoodAppHandler();
        server.createContext("/api/food", apiHandler);
        server.createContext("/api/user", apiHandler);
        server.createContext("/api/cart", apiHandler);
        server.createContext("/api/order", apiHandler);

        // Serve food images from uploads/ directory
        String uploadsDir = System.getProperty("user.dir") + File.separator + "uploads";
        File uploads = new File(uploadsDir);
        if (!uploads.exists()) uploads.mkdirs();
        System.out.println("[FoodServer] Serving images from: " + uploads.getAbsolutePath());
        server.createContext("/images", new StaticFileHandler(uploads.getAbsolutePath()));

        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));
    }

    public void start() {
        server.start();
        System.out.println("============================================");
        System.out.println("  Food Delivery Server (JDBC) started!");
        System.out.println("  http://localhost:" + server.getAddress().getPort());
        System.out.println("============================================");
        System.out.println("Press Ctrl+C to stop.");
    }

    public static void main(String[] args) throws Exception {
        int port = 4000;
        if (args.length > 0) {
            try { port = Integer.parseInt(args[0]); } catch (NumberFormatException ignore) {}
        }
        new FoodServer(port).start();
    }
}
