package Services;

import Models.Goods;
import Utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
}