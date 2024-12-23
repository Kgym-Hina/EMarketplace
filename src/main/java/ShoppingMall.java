import Models.Goods;
import Models.User;
import Services.GoodsService;
import Services.OrderService;
import Services.UserService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ShoppingMall {
    private static User currentUser = null;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                if (currentUser.getRole().equals("ADMIN")) {
                    // 管理员
                    // 选择进入管理员菜单或普通用户菜单
                    System.out.println("\n=== 管理员菜单 ===");
                    System.out.println("1. 进入管理员菜单");
                    System.out.println("2. 进入普通用户菜单");
                    System.out.println("3. 注销");
                    System.out.print("请选择: ");

                    int choice = scanner.nextInt();
                    scanner.nextLine(); // 消耗换行符

                    switch (choice) {
                        case 1:
                            // 进入管理员菜单
                            while (currentUser != null) {
                                showAdminMenu();
                            }
                            break;
                        case 2:
                            // 进入普通用户菜单
                            while (currentUser != null) {
                                showMainMenu();
                            }
                            break;
                        case 3:
                            logout();
                            break;
                        default:
                            System.out.println("无效选择!");
                    }

                } else {
                    // 普通用户
                    showMainMenu();
                }
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

    private static void showAdminMenu() {
        System.out.println("\n=== 管理员菜单 ===");
        System.out.println("1. 查看所有产品");
        System.out.println("2. 添加产品");
        System.out.println("3. 修改产品");
        System.out.println("4. 删除产品");
        System.out.println("5. 查看订单");
        System.out.println("6. 注销");
        System.out.print("请选择: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // 消耗换行符

        switch (choice) {
            case 1:
                showProducts(true);
                break;
            case 2:
                addProduct();
                break;
            case 3:
                // 直接输入产品ID以修改产品
                System.out.print("输入产品ID以修改产品: ");
                String modifyId = scanner.nextLine();
                updateProduct(modifyId);
                break;
            case 4:
                // 直接输入产品ID以修改产品
                System.out.print("输入产品ID以删除产品: ");
                String deleteId = scanner.nextLine();
                deleteProduct(deleteId);
                break;
            case 5:
                viewOrders();
                break;
            case 6:
                logout();
                break;
            default:
                System.out.println("无效选择!");
        }
    }

    private static void viewOrders() {
        OrderService orderService = new OrderService();
        List<Map<String, Object>> orderList = orderService.getAllOrders();

        if (orderList.isEmpty()) {
            System.out.println("没有订单。");
            return;
        }

        System.out.println("\n=== 订单列表 ===");
        System.out.printf("%-20s %-20s %-20s %-20s %-20s %-20s\n",
                "订单号", "用户", "收货人", "收货地址", "电话", "总金额");
        System.out.println("=".repeat(120));

        for (Map<String, Object> order : orderList) {
            // 打印订单信息
            System.out.printf("%-20s %-20s %-20s %-20s %-20s %-20.2f\n",
                    order.get("serialNumber"), order.get("username"),
                    order.get("consignee"), order.get("consigneeAddress"),
                    order.get("phone"), order.get("amount"));
        }

        System.out.print("\n输入订单号以查看订单详情 (或输入0返回): ");
        String serialNumber = scanner.nextLine();

        if (!serialNumber.equals("0")) {
            viewOrderDetail(serialNumber);
        }
    }

    private static void viewOrderDetail(String serialNumber) {
    OrderService orderService = new OrderService();
    Map<String, Object> orderDetail = orderService.getOrderDetail(serialNumber);

    if (orderDetail.isEmpty()) {
        System.out.println("订单不存在。");
        return;
    }

    System.out.println("\n=== 订单详情 ===");
    System.out.printf("订单号: %s\n用户: %s\n收货人: %s\n收货地址: %s\n电话: %s\n总金额: %.2f\n",
            orderDetail.get("serialNumber"), orderDetail.get("username"),
            orderDetail.get("consignee"), orderDetail.get("consigneeAddress"),
            orderDetail.get("phone"), orderDetail.get("amount"));

    List<Map<String, Object>> items = (List<Map<String, Object>>) orderDetail.get("items");
    System.out.println("\n=== 订单商品 ===");
    System.out.printf("%-20s %-30s %-10s %-10s\n", "商品ID", "商品名称", "数量", "价格");
    System.out.println("=".repeat(70));
    for (Map<String, Object> item : items) {
        System.out.printf("%-20s %-30s %-10d %-10.2f\n",
                item.get("goodsId"), item.get("goodTitle"),
                item.get("goodNum"), item.get("price"));
    }
}

    private static void deleteProduct(String id) {
        //二次确认
        System.out.print("确认删除产品吗？(y/n): ");
        String confirm = scanner.nextLine();
        if (!confirm.equalsIgnoreCase("y")) {
            return;
        }

        GoodsService goodsService = new GoodsService();
        if (goodsService.deleteGoods(id)) {
            System.out.println("产品删除成功!");
        } else {
            System.out.println("产品删除失败。请重试。");
        }
    }

    private static void updateProduct(String id) {
        GoodsService goodsService = new GoodsService();
        Goods goods = goodsService.getGoodsById(id);

        if (goods == null) {
            System.out.println("产品不存在!");
            return;
        }

        // 先问操作 1. 修改 2. 删除
        int choice = 0;
        while (choice != 1 && choice != 2) {
            System.out.println("\n=== 修改产品 ===");
            System.out.println("1. 修改产品");
            System.out.println("2. 删除产品");
            System.out.print("请选择: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // 消耗换行符
        }

        if (choice == 2){
            deleteProduct(id);
            return;
        }

        // 打印产品信息
        System.out.println("\n=== 产品信息 ===");
        System.out.printf("%-20s %-30s %-10s %-10s %-20s\n",
                "ID", "名称", "价格", "库存", "品牌");
        System.out.println("=".repeat(90));
        System.out.printf("%-20s %-30s %-10.2f %-10d %-20s\n",
                goods.getId(), goods.getName(), goods.getPrice(),
                goods.getNumber(), goods.getBrand());

        System.out.println("\n=== 修改产品 ===");
        System.out.print("名称(" + goods.getName() + "): ");
        String name = scanner.nextLine();
        if (name.isEmpty()) {
            name = goods.getName();
        }

        System.out.print("价格(" + goods.getPrice() + "): ");
        String priceStr = scanner.nextLine();
        double price = priceStr.isEmpty() ? goods.getPrice() : Double.parseDouble(priceStr);

        System.out.print("库存(" + goods.getNumber() + "): ");
        String numberStr = scanner.nextLine();
        int number = numberStr.isEmpty() ? goods.getNumber() : Integer.parseInt(numberStr);

        System.out.print("品牌(" + goods.getBrand() + "): ");
        String brand = scanner.nextLine();
        if (brand.isEmpty()) {
            brand = goods.getBrand();
        }

        if (goodsService.updateGoods(id, name, price, number, brand)) {
            System.out.println("产品修改成功!");
        } else {
            System.out.println("产品修改失败。请重试。");
        }

    }

    private static void addProduct() {
        System.out.println("\n=== 添加产品 ===");
        System.out.print("名称: ");
        String name = scanner.nextLine();
        System.out.print("价格: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // 消耗换行符
        System.out.print("库存: ");
        int number = scanner.nextInt();
        scanner.nextLine(); // 消耗换行符
        System.out.print("品牌: ");
        String brand = scanner.nextLine();

        GoodsService goodsService = new GoodsService();
        if (goodsService.addGoods(name, price, number, brand)) {
            System.out.println("产品添加成功!");
        } else {
            System.out.println("产品添加失败。请重试。");
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
                showProducts(false);
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

    private static void showProducts(boolean isAdmin) {
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

            if (isAdmin){
                System.out.print("\n输入产品ID以编辑产品 (或输入0返回, n下一页, p上一页): ");
            } else {
                System.out.print("\n输入产品ID以添加到购物车 (或输入0返回, n下一页, p上一页): ");
            }

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

                    if (isAdmin) {
                        // 编辑产品
                        updateProduct(input);
                    } else {
                        System.out.print("输入数量: ");
                        int quantity = scanner.nextInt();
                        scanner.nextLine(); // 消耗换行符

                        goodsService.addToCart(currentUser.getId(), input, quantity);
                        System.out.println("产品已添加到购物车!");
                    }
            }
        }
    }

    private static void viewCart() {
        GoodsService goodsService = new GoodsService();
        Map<String, Integer> cart = goodsService.getCart(currentUser.getId());

        if (cart.isEmpty()) {
            System.out.println("\n您的购物车是空的!");
            return;
        }

        System.out.println("\n=== 您的购物车 ===");
        System.out.printf("%-30s %-10s %-10s %-10s\n",
                "产品", "价格", "数量", "小计");
        System.out.println("=".repeat(60));

        double total = 0;

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
        GoodsService goodsService = new GoodsService();
        Map<String, Integer> cart = goodsService.getCart(currentUser.getId());

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
        String serialNumber = orderService.createOrder(currentUser, cart, consignee, address, phone);

        if (serialNumber != null) {
            System.out.println("\n订单创建成功!");
            System.out.println("订单号: " + serialNumber);
            orderService.clearCart(currentUser.getId());
        } else {
            System.out.println("\n订单创建失败。请重试。");
        }
    }

    private static void logout() {
        currentUser = null;
        System.out.println("\n注销成功!");
    }
}