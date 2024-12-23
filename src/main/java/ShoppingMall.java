import Models.Goods;
import Models.User;
import Services.GoodsService;
import Services.OrderService;
import Services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ShoppingMall {
    private static User currentUser = null;
    private static Map<String, Integer> cart = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n=== Welcome to Shopping Mall ===");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Please choose: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        UserService userService = new UserService();
        currentUser = userService.login(username, password);

        if (currentUser != null) {
            System.out.println("Login successful! Welcome, " + currentUser.getUsername());
        } else {
            System.out.println("Login failed! Please check your username and password.");
        }
    }

    private static void showMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. View All Products");
        System.out.println("2. View Cart");
        System.out.println("3. Checkout");
        System.out.println("4. Logout");
        System.out.print("Please choose: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                showProducts();
                break;
            case 2:
                viewCart();
                break;
            case 3:
                checkout();
                break;
            case 4:
                logout();
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void showProducts() {
        GoodsService goodsService = new GoodsService();
        List<Goods> goodsList = goodsService.getAllGoods();

        System.out.println("\n=== Products List ===");
        System.out.printf("%-20s %-30s %-10s %-10s %-20s\n",
                "ID", "Name", "Price", "Stock", "Brand");
        System.out.println("=".repeat(90));

        for (Goods goods : goodsList) {
            System.out.printf("%-20s %-30s %-10.2f %-10d %-20s\n",
                    goods.getId(), goods.getName(), goods.getPrice(),
                    goods.getNumber(), goods.getBrand());
        }

        System.out.print("\nEnter product ID to add to cart (or 0 to return): ");
        String id = scanner.nextLine();

        if (!id.equals("0")) {
            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            cart.put(id, cart.getOrDefault(id, 0) + quantity);
            System.out.println("Product added to cart!");
        }
    }

    private static void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("\nYour cart is empty!");
            return;
        }

        System.out.println("\n=== Your Cart ===");
        System.out.printf("%-30s %-10s %-10s %-10s\n",
                "Product", "Price", "Quantity", "Subtotal");
        System.out.println("=".repeat(60));

        double total = 0;
        GoodsService goodsService = new GoodsService();

        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            Goods goods = goodsService.getGoodsById(entry.getKey());
            if (goods != null) {
                double subtotal = goods.getPrice() * entry.getValue();
                total += subtotal;
                System.out.printf("%-30s %-10.2f %-10d %-10.2f\n",
                        goods.getName(), goods.getPrice(),
                        entry.getValue(), subtotal);
            }
        }

        System.out.println("-".repeat(60));
        System.out.printf("Total: $%.2f\n", total);
    }

    private static void checkout() {
        if (cart.isEmpty()) {
            System.out.println("\nYour cart is empty!");
            return;
        }

        System.out.println("\n=== Checkout ===");
        System.out.print("Enter consignee name: ");
        String consignee = scanner.nextLine();
        System.out.print("Enter delivery address: ");
        String address = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();

        OrderService orderService = new OrderService();
        String serialNumber = orderService.createOrder(currentUser, cart, consignee,
                address, phone);

        if (serialNumber != null) {
            System.out.println("\nOrder created successfully!");
            System.out.println("Order number: " + serialNumber);
            cart.clear();
        } else {
            System.out.println("\nFailed to create order. Please try again.");
        }
    }

    private static void logout() {
        currentUser = null;
        cart.clear();
        System.out.println("\nLogout successful!");
    }
}