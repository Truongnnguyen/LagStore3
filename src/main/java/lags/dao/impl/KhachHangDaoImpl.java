package lags.dao.impl;

import lags.dao.KhachHangDao;
import lags.entity.KhachHang;
import lags.util.XJdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDaoImpl implements KhachHangDao {

    private KhachHang map(ResultSet rs) throws SQLException {
        KhachHang k = new KhachHang();
        k.setMaKH(rs.getString("MaKH"));
        k.setTenKH(rs.getString("TenKH"));
        k.setDiaChi(rs.getString("DiaChi"));
        k.setSoDienThoai(rs.getString("SoDienThoai"));
        k.setEmail(rs.getString("Email"));
        return k;
    }

    @Override
    public KhachHang create(KhachHang entity) {
        String sql = "INSERT INTO KhachHang (MaKH, TenKH, DiaChi, SoDienThoai, Email) VALUES (?,?,?,?,?)";
        try (Connection c = XJdbc.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, entity.getMaKH());
            ps.setString(2, entity.getTenKH());
            ps.setString(3, entity.getDiaChi());
            ps.setString(4, entity.getSoDienThoai());
            ps.setString(5, entity.getEmail());
            ps.executeUpdate();
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Create KhachHang failed", e);
        }
    }

    @Override
    public void update(KhachHang entity) {
        String sql = "UPDATE KhachHang SET TenKH=?, DiaChi=?, SoDienThoai=?, Email=? WHERE MaKH=?";
        try (Connection c = XJdbc.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, entity.getTenKH());
            ps.setString(2, entity.getDiaChi());
            ps.setString(3, entity.getSoDienThoai());
            ps.setString(4, entity.getEmail());
            ps.setString(5, entity.getMaKH());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Update KhachHang failed", e);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM KhachHang WHERE MaKH=?";
        try (Connection c = XJdbc.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Delete KhachHang failed", e);
        }
    }

    @Override
    public List<KhachHang> findAll() {
        String sql = "SELECT MaKH, TenKH, DiaChi, SoDienThoai, Email FROM KhachHang ORDER BY TenKH";
        List<KhachHang> list = new ArrayList<>();
        try (Connection c = XJdbc.openConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) {
            throw new RuntimeException("findAll KhachHang failed", e);
        }
        return list;
    }

    @Override
    public KhachHang findById(String id) {
        String sql = "SELECT MaKH, TenKH, DiaChi, SoDienThoai, Email FROM KhachHang WHERE MaKH=?";
        try (Connection c = XJdbc.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException("findById KhachHang failed", e);
        }
        return null;
    }

    @Override
    public List<KhachHang> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return findAll();
        String sql = """
            SELECT MaKH, TenKH, DiaChi, SoDienThoai, Email
            FROM KhachHang
            WHERE TenKH LIKE ? OR SoDienThoai LIKE ? OR Email LIKE ?
            ORDER BY TenKH
            """;
        List<KhachHang> list = new ArrayList<>();
        String p = "%" + keyword.trim() + "%";
        try (Connection c = XJdbc.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("search KhachHang failed", e);
        }
        return list;
    }

    @Override
    public boolean canDelete(String maKH) {
        String sql = "SELECT COUNT(*) FROM HoaDon WHERE MaKH = ?";
        try (Connection c = XJdbc.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("canDelete check failed", e);
        }
    }
}
