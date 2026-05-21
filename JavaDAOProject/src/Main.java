import service.*;
import controller.MenuController;
import server.AppServer;
import util.DBConnection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Web server mode
        if (args.length > 0 && "--web".equals(args[0])) {
            try {
                AppServer appServer = new AppServer(8080);
                appServer.start();
            } catch (Exception e) {
                System.err.println("[FATAL] Failed to start web server: " + e.getMessage());
                e.printStackTrace();
            }
            return;
        }

        // Console mode
        Scanner scanner = new Scanner(System.in);
        try {
            // Initialize services
            ProductService productService = new ProductService();
            UserService userService = new UserService();
            CartService cartService = new CartService();
            CheckoutService checkoutService = new CheckoutService();
            BillService billService = new BillService();

            // Create and run the menu controller
            MenuController menuController = new MenuController(
                scanner, productService, userService, cartService, checkoutService, billService
            );
            menuController.run();

        } catch (Exception e) {
            System.err.println("[FATAL] Failed to initialize the application: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
            DBConnection.closeConnection();
        }
    }
}
