package Services;

import Models.Goods;
import Models.User;
import Utils.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class OrderService {
    public String createOrder(User user, Map<String, Integer> cart, String consignee,
                              String address, String phone) {
        String orderId = UUID.randomUUID().toString().replace("-", "");
        String serialNumber = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) +
                "-" + System.currentTimeMillis() + "-" +
                (int) (Math.random() * 1000);
        double totalAmount = 0;

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Create order
            String orderSql = "INSERT INTO `order` (id, serialNumber, isDel, createTime, " +
                    "userId, consignee, consigneeAddress, phone, amount, state) " +
                    "VALUES (?, ?, '0', NOW(), ?, ?, ?, ?, ?, 'WAITING_PAY')";
            PreparedStatement orderStmt = conn.prepareStatement(orderSql);

            // Calculate total amount and create order details
            for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                Goods goods = new GoodsService().getGoodsById(entry.getKey());
                if (goods != null) {
                    totalAmount += goods.getPrice() * entry.getValue();

                    // Create order detail
                    String detailSql = "INSERT INTO order_detail (id, orderId, goodsId, " +
                            "goodTitle, goodNum, price, isDel, createTime) " +
                            "VALUES (?, ?, ?, ?, ?, ?, '0', NOW())";
                    PreparedStatement detailStmt = conn.prepareStatement(detailSql);
                    detailStmt.setString(1, UUID.randomUUID().toString().replace("-", ""));
                    detailStmt.setString(2, orderId);
                    detailStmt.setString(3, goods.getId());
                    detailStmt.setString(4, goods.getName());
                    detailStmt.setInt(5, entry.getValue());
                    detailStmt.setDouble(6, goods.getPrice());
                    detailStmt.executeUpdate();
                }
            }

            orderStmt.setString(1, orderId);
            orderStmt.setString(2, serialNumber);
            orderStmt.setString(3, user.getId());
            orderStmt.setString(4, consignee);
            orderStmt.setString(5, address);
            orderStmt.setString(6, phone);
            orderStmt.setDouble(7, totalAmount);
            orderStmt.executeUpdate();

            conn.commit();
            return serialNumber;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return null;
    }

    public void clearCart(String userId) {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> getAllOrders() {
        List<Map<String, Object>> orderList = new ArrayList<>();
        String sql = "SELECT o.serialNumber, u.username, o.consignee, o.consigneeAddress, o.phone, o.amount " +
                "FROM `order` o JOIN `user` u ON o.userId = u.id WHERE o.isDel = '0'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> order = new HashMap<>();
                order.put("serialNumber", rs.getString("serialNumber"));
                order.put("username", rs.getString("username"));
                order.put("consignee", rs.getString("consignee"));
                order.put("consigneeAddress", rs.getString("consigneeAddress"));
                order.put("phone", rs.getString("phone"));
                order.put("amount", rs.getDouble("amount"));
                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    public Map<String, Object> getOrderDetail(String serialNumber) {
        Map<String, Object> orderDetail = new HashMap<>();
        String orderSql = "SELECT o.serialNumber, u.username, o.consignee, o.consigneeAddress, o.phone, o.amount " +
                "FROM `order` o JOIN `user` u ON o.userId = u.id WHERE o.serialNumber = ? AND o.isDel = '0'";
        String detailSql = "SELECT od.goodsId, od.goodTitle, od.goodNum, od.price " +
                "FROM order_detail od WHERE od.orderId = (SELECT id FROM `order` WHERE serialNumber = ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement orderStmt = conn.prepareStatement(orderSql);
             PreparedStatement detailStmt = conn.prepareStatement(detailSql)) {
            orderStmt.setString(1, serialNumber);
            try (ResultSet rs = orderStmt.executeQuery()) {
                if (rs.next()) {
                    orderDetail.put("serialNumber", rs.getString("serialNumber"));
                    orderDetail.put("username", rs.getString("username"));
                    orderDetail.put("consignee", rs.getString("consignee"));
                    orderDetail.put("consigneeAddress", rs.getString("consigneeAddress"));
                    orderDetail.put("phone", rs.getString("phone"));
                    orderDetail.put("amount", rs.getDouble("amount"));
                }
            }

            detailStmt.setString(1, serialNumber);
            try (ResultSet rs = detailStmt.executeQuery()) {
                List<Map<String, Object>> orderItems = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("goodsId", rs.getString("goodsId"));
                    item.put("goodTitle", rs.getString("goodTitle"));
                    item.put("goodNum", rs.getInt("goodNum"));
                    item.put("price", rs.getDouble("price"));
                    orderItems.add(item);
                }
                orderDetail.put("items", orderItems);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderDetail;
    }
}

