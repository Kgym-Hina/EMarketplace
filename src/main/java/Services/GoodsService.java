package Services;

import Models.Goods;
import Utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoodsService {
    public List<Goods> getAllGoods() {
        List<Goods> goodsList = new ArrayList<>();
        String sql = "SELECT * FROM goods WHERE isDel = '0'";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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
}