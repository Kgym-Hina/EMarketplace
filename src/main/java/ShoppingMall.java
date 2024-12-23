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
    private static final Map<String, Integer> cart = new HashMap<>();
    private static final Scanner scanner = new Scanner(System.in);

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
        System.out.println("\n=== 欢迎来到购物商城 ===");
        System.out.println("1. 登录");
        System.out.println("2. 退出");
        System.out.print("请选择: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // 消耗换行符

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                System.exit(0);
                break;
            default:
                System.out.println("无效选择!");
        }
    }

    private static void login() {
        System.out.print("用户名: ");
        String username = scanner.nextLine();
        System.out.print("密码: ");
        String password = scanner.nextLine();

        UserService userService = new UserService();
        currentUser = userService.login(username, password);

        if (currentUser != null) {
            System.out.println("登录成功! 欢迎, " + currentUser.getUsername());
        } else {
            System.out.println("登录失败! 请检查您的用户名和密码。");
        }
    }

    private static void showMainMenu() {
        System.out.println("\n=== 主菜单 ===");
        System.out.println("1. 查看所有产品");
        System.out.println("2. 查看购物车");
        System.out.println("3. 结账");
        System.out.println("4. 注销");
        System.out.print("请选择: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // 消耗换行符

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
                System.out.println("无效选择!");
        }
    }

    private static void showProducts() {
        GoodsService goodsService = new GoodsService();
        int page = 1;
        int pageSize = 5;
        boolean exit = false;

        while (!exit) {
            List<Goods> goodsList = goodsService.getAllGoods(page, pageSize);

            if (goodsList.isEmpty()) {
                System.out.println("没有更多产品了。");
                break;
            }

            System.out.println("\n=== 产品列表 (第 " + page + " 页) ===");
            System.out.printf("%-20s %-30s %-10s %-10s %-20s\n",
                    "ID", "名称", "价格", "库存", "品牌");
            System.out.println("=".repeat(90));

            for (Goods goods : goodsList) {
                System.out.printf("%-20s %-30s %-10.2f %-10d %-20s\n",
                        goods.getId(), goods.getName(), goods.getPrice(),
                        goods.getNumber(), goods.getBrand());
            }

            System.out.print("\n输入产品ID以添加到购物车 (或输入0返回, n下一页, p上一页): ");
            String input = scanner.nextLine();

            switch (input) {
                case "0":
                    exit = true;
                    break;
                case "n":
                    page++;
                    break;
                case "p":
                    if (page > 1) page--;
                    break;
                default:
                    if (!goodsService.isGoodsExist(input)) {
                        System.out.println("产品不存在!");
                        break;
                    }

                    System.out.print("输入数量: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine(); // 消耗换行符

                    cart.put(input, cart.getOrDefault(input, 0) + quantity);
                    System.out.println("产品已添加到购物车!");
            }
        }
    }

    private static void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("\n您的购物车是空的!");
            return;
        }

        System.out.println("\n=== 您的购物车 ===");
        System.out.printf("%-30s %-10s %-10s %-10s\n",
                "产品", "价格", "数量", "小计");
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
        System.out.printf("总计: $%.2f\n", total);
    }

    private static void checkout() {
        if (cart.isEmpty()) {
            System.out.println("\n您的购物车是空的!");
            return;
        }

        System.out.println("\n=== 结账 ===");
        System.out.print("输入收货人姓名: ");
        String consignee = scanner.nextLine();
        System.out.print("输入收货地址: ");
        String address = scanner.nextLine();
        System.out.print("输入电话号码: ");
        String phone = scanner.nextLine();

        OrderService orderService = new OrderService();
        String serialNumber = orderService.createOrder(currentUser, cart, consignee,
                address, phone);

        if (serialNumber != null) {
            System.out.println("\n订单创建成功!");
            System.out.println("订单号: " + serialNumber);
            cart.clear();
        } else {
            System.out.println("\n订单创建失败。请重试。");
        }
    }

    private static void logout() {
        currentUser = null;
        cart.clear();
        System.out.println("\n注销成功!");
    }
}