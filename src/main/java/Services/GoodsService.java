package Services;

import Models.Goods;
import Utils.DatabaseUtil;

import java.sql.*;
import java.util.*;

public class GoodsService {
    public List<Goods> getAllGoods(int page, int pageSize) {
        List<Goods> goodsList = new ArrayList<>();
        String sql = "SELECT * FROM goods WHERE isDel = '0' LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, (page - 1) * pageSize);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Goods goods = new Goods();
                goods.setId(rs.getString("id"));
                goods.setName(rs.getString("name"));
                goods.setPrice(rs.getDouble("price"));
                goods.setNumber(rs.getInt("number"));
                goods.setBrand(rs.getString("brand"));
                goodsList.add(goods);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goodsList;
    }

    public Goods getGoodsById(String id) {
        String sql = "SELECT * FROM goods WHERE id = ? AND isDel = '0'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Goods goods = new Goods();
                goods.setId(rs.getString("id"));
                goods.setName(rs.getString("name"));
                goods.setPrice(rs.getDouble("price"));
                goods.setNumber(rs.getInt("number"));
                goods.setBrand(rs.getString("brand"));
                return goods;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isGoodsExist(String id) {
        String sql = "SELECT * FROM goods WHERE id = ? AND isDel = '0'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addToCart(String userId, String goodId, int quantity) {
        String sql = "INSERT INTO cart (id, user_id, good_id, quantity) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, UUID.randomUUID().toString().replace("-", ""));
            stmt.setString(2, userId);
            stmt.setString(3, goodId);
            stmt.setInt(4, quantity);
            stmt.setInt(5, quantity);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getCart(String userId) {
        Map<String, Integer> cart = new HashMap<>();
        String sql = "SELECT good_id, quantity FROM cart WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cart.put(rs.getString("good_id"), rs.getInt("quantity"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cart;
    }
}