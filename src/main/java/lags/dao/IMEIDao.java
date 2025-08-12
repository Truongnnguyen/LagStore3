/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import lags.entity.IMEI;
import lags.util.XJdbc;
import lags.util.XQuery;

/**
 *
 * @author icebear
 */
public class IMEIDao {

    public List<IMEI> findAll() {
        String sql = "SELECT * FROM IMEI";
        return XQuery.getBeanList(IMEI.class, sql);
    }

    public void create(IMEI entity) {
        String sql = "INSERT INTO IMEI (MaIMEI, MaSPCT, SoIMEI) VALUES (?, ?, ?)";

        Object[] values = {
            entity.getMaIMEI(),
            entity.getMaSPCT(),
            entity.getSoIMEI()
        };
        XJdbc.executeUpdate(sql, values);
    }

    public void update(IMEI entity) {
        String sql = "UPDATE IMEI SET MaSPCT = ?, SoIMEI = ? WHERE MaIMEI = ?";

        Object[] values = {
            entity.getMaSPCT(),
            entity.getSoIMEI(),
            entity.getMaIMEI()
        };
        XJdbc.executeUpdate(sql, values);
    }

    public void deleteByID(String MaIm) {
        String sql = "DELETE FROM IMEI WHERE MaIMEI = ?";

        XJdbc.executeUpdate(sql, MaIm);
    }

    public List<IMEI> findByID(String MaSPCT) {
        String sql = "SELECT * FROM IMEI WHERE MaSPCT = ?";
        return XQuery.getBeanList(IMEI.class, sql, MaSPCT);
    }

    // Thay toàn bộ hàm cũ bằng bản robust này
    public java.util.List<String> findAvailableByMaSPCT_String(String maSPCT) {
    java.util.List<String> list = new java.util.ArrayList<>();
    String sql = "SELECT SoIMEI FROM IMEI WHERE MaSPCT=? AND (TrangThai = 0 OR TrangThai IS NULL) ORDER BY SoIMEI";
    try (java.sql.Connection c = lags.util.XJdbc.openConnection();
         java.sql.PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, maSPCT);
        try (java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}


    /**
     * Giữ chỗ IMEI (TrangThai=2). An toàn nếu bảng không có cột TrangThai: sẽ
     * bỏ qua.
     */
    public void holdImeis(java.util.List<String> imeis) {
        if (imeis == null || imeis.isEmpty()) {
            return;
        }
        String sql = "UPDATE IMEI SET TrangThai=2 WHERE SoIMEI=?";
        try (java.sql.Connection c = lags.util.XJdbc.openConnection()) {
            c.setAutoCommit(false);
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql)) {
                for (String im : imeis) {
                    ps.setString(1, im);
                    ps.addBatch();
                }
                ps.executeBatch();
                c.commit();
            } catch (Exception e) {
                c.rollback(); // có thể do không có cột TrangThai -> bỏ qua
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * Trả IMEI về kho (TrangThai=0, MaHD=NULL nếu có). Bỏ qua nếu không có các
     * cột.
     */
    public void releaseImeis(java.util.List<String> imeis) {
        if (imeis == null || imeis.isEmpty()) {
            return;
        }
        String sql = "UPDATE IMEI SET TrangThai=0, MaHD=NULL WHERE SoIMEI=?";
        try (java.sql.Connection c = lags.util.XJdbc.openConnection()) {
            c.setAutoCommit(false);
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql)) {
                for (String im : imeis) {
                    ps.setString(1, im);
                    ps.addBatch();
                }
                ps.executeBatch();
                c.commit();
            } catch (Exception e) {
                c.rollback();
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * Chốt bán IMEI (TrangThai=1). Nếu có cột MaHD thì lưu mã HĐ, nếu không có
     * sẽ chỉ cập nhật TrangThai nếu cột tồn tại; nếu không có cột -> im lặng bỏ
     * qua.
     */
    public void markSoldImeis(java.util.List<String> imeis, String maHD) {
        if (imeis == null || imeis.isEmpty()) {
            return;
        }
        String sqlA = "UPDATE IMEI SET TrangThai=1, MaHD=? WHERE SoIMEI=?"; // có MaHD
        String sqlB = "UPDATE IMEI SET TrangThai=1 WHERE SoIMEI=?";         // không có MaHD

        try (java.sql.Connection c = lags.util.XJdbc.openConnection()) {
            c.setAutoCommit(false);
            try {
                // thử cập nhật kèm MaHD
                try (java.sql.PreparedStatement ps = c.prepareStatement(sqlA)) {
                    for (String im : imeis) {
                        ps.setString(1, maHD);
                        ps.setString(2, im);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    c.commit();
                }
            } catch (Exception noMaHD) {
                // fallback: chỉ set TrangThai=1
                try (java.sql.PreparedStatement ps = c.prepareStatement(sqlB)) {
                    for (String im : imeis) {
                        ps.setString(1, im);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    c.commit();
                } catch (Exception e2) {
                    c.rollback();
                }
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception ignore) {
        }

    }

    public void updateTrangThaiDaBan(String soIMEI) {
        String sql = "UPDATE IMEI SET TrangThai = 1 WHERE SoIMEI = ?";
        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, soIMEI);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    
    
}
