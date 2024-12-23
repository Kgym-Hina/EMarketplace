package Services;

import Models.Goods;
import Models.User;
import Utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class OrderService {
    public String createOrder(User user, Map<String, Integer> cart, String consignee,
                              String address, String phone) {
        String orderId = UUID.randomUUID().toString().replace("-", "");
        String serialNumber = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) +
                "-" + System.currentTimeMillis() + "-" +
                (int)(Math.random() * 1000);
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
}

