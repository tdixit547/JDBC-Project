package server;

import com.sun.net.httpserver.HttpServer;
import service.*;
import java.io.File;
import java.net.InetSocketAddress;

public class AppServer {

    private final HttpServer server;

    public AppServer(int port) throws Exception {
        ProductService productService = new ProductService();
        UserService userService = new UserService();
        CartService cartService = new CartService();
        CheckoutService checkoutService = new CheckoutService();
        BillService billService = new BillService();

        server = HttpServer.create(new InetSocketAddress(port), 0);

        // API routes
        server.createContext("/api/products", new ProductApiHandler(productService));
        server.createContext("/api/users", new UserApiHandler(userService));
        server.createContext("/api/cart", new CartApiHandler(cartService));
        server.createContext("/api/bills", new BillApiHandler(billService, checkoutService));
        server.createContext("/api/checkout", new BillApiHandler(billService, checkoutService));

        // Static files - serve from webapp/ directory
        String webRoot = System.getProperty("user.dir") + File.separator + "webapp";
        server.createContext("/", new StaticFileHandler(webRoot));

        server.setExecutor(null);
    }

    public void start() {
        server.start();
        System.out.println("============================================");
        System.out.println("  ShopAdmin Server started!");
        System.out.println("  http://localhost:" + server.getAddress().getPort());
        System.out.println("============================================");
        System.out.println("Press Ctrl+C to stop.");
    }
}
