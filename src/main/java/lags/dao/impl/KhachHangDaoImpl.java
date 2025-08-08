package lags.dao.impl;

import lags.dao.KhachHangDao;
import lags.entity.KhachHang;
import lags.util.XJdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class KhachHangDaoImpl implements KhachHangDao {

    private KhachHang map(ResultSet rs) throws SQLException {
        KhachHang k = new KhachHang();
        k.setMaKH(rs.getString("MaKH"));
        k.setTenKH(rs.getString("TenKH"));
        k.setDiaChi(rs.getString("DiaChi"));
        k.setSoDienThoai(rs.getString("SoDienThoai"));
        k.setEmail(rs.getString("Email"));
        k.setTrangThai(rs.getInt("TrangThai"));
        return k;
    }

    @Override
    public KhachHang create(KhachHang entity) {
        String sql = "INSERT INTO KhachHang (MaKH, TenKH, DiaChi, SoDienThoai, Email, TrangThai) VALUES (?,?,?,?,?,?)";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, entity.getMaKH());
            ps.setString(2, entity.getTenKH());
            ps.setString(3, entity.getDiaChi());
            ps.setString(4, entity.getSoDienThoai());
            ps.setString(5, entity.getEmail());
            ps.setInt(6, entity.getTrangThai());
            ps.executeUpdate();
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Create KhachHang failed", e);
        }
    }

    @Override
    public boolean update(KhachHang entity) {
        String sql = "UPDATE KhachHang SET TenKH=?, DiaChi=?, SoDienThoai=?, Email=?, TrangThai=? WHERE MaKH=?";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, entity.getTenKH());
            ps.setString(2, entity.getDiaChi());
            ps.setString(3, entity.getSoDienThoai());
            ps.setString(4, entity.getEmail());
            ps.setInt(5, entity.getTrangThai());
            ps.setString(6, entity.getMaKH());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Update KhachHang failed", e);
        }
    }

    @Override
    public boolean deleteById(String id) {
        String sql = "DELETE FROM KhachHang WHERE MaKH=?";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Delete KhachHang failed", e);
        }
    }

    @Override
    public List<KhachHang> findAll() {
        String sql = "SELECT MaKH, TenKH, DiaChi, SoDienThoai, Email, TrangThai FROM KhachHang ORDER BY TenKH";
        List<KhachHang> list = new ArrayList<>();
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("findAll KhachHang failed", e);
        }
        return list;
    }

    @Override
    public KhachHang findById(String id) {
        String sql = "SELECT MaKH, TenKH, DiaChi, SoDienThoai, Email, TrangThai FROM KhachHang WHERE MaKH=?";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("findById KhachHang failed", e);
        }
        return null;
    }

    @Override
    public List<KhachHang> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        String sql = """
            SELECT MaKH, TenKH, DiaChi, SoDienThoai, Email, TrangThai
            FROM KhachHang
            WHERE TenKH LIKE ? OR SoDienThoai LIKE ? OR Email LIKE ?
            ORDER BY TenKH
            """;
        List<KhachHang> list = new ArrayList<>();
        String p = "%" + keyword.trim() + "%";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("search KhachHang failed", e);
        }
        return list;
    }

    @Override
    public boolean canDelete(String maKH) {
        String sql = "SELECT COUNT(*) FROM HoaDon WHERE MaKH = ?";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("canDelete check failed", e);
        }
    }

    @Override
    public boolean existsByPhoneExcept(String phone, String maKH) {
        String sql = "SELECT COUNT(*) FROM KhachHang WHERE SoDienThoai = ? AND MaKH <> ?";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("existsByPhoneExcept failed", e);
        }
    }

    @Override
    public boolean existsByEmailExcept(String email, String maKH) {
        String sql = "SELECT COUNT(*) FROM KhachHang WHERE Email = ? AND MaKH <> ?";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("existsByEmailExcept failed", e);
        }
    }

    @Override
    public boolean existsByPhone(String phone) {
        String sql = "SELECT COUNT(*) FROM KhachHang WHERE SoDienThoai = ?";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("existsByPhone failed", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM KhachHang WHERE Email = ?";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("existsByEmail failed", e);
        }
    }

    @Override
    public String insertAndGetMaKH(KhachHang kh) {
        // 1. Kiểm tra khách hàng đã tồn tại
        String sqlCheck = "SELECT MaKH FROM KhachHang WHERE SoDienThoai = ?";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sqlCheck)) {
            ps.setString(1, kh.getSoDienThoai());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Nếu đã tồn tại, trả về mã KH cũ
                    return rs.getString("MaKH");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Check existing customer failed", e);
        }

        // 2. Sinh mã KH mới duy nhất
        String newMaKH = generateNewMaKH();
        if (newMaKH == null) {
            return null;
        }

        // 3. Thêm mới
        String sqlInsert = "INSERT INTO KhachHang (MaKH, TenKH, DiaChi, SoDienThoai, Email, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sqlInsert)) {

            ps.setString(1, newMaKH);
            ps.setString(2, kh.getTenKH());
            ps.setString(3, kh.getDiaChi());
            ps.setString(4, kh.getSoDienThoai());
            if (kh.getEmail() == null || kh.getEmail().trim().isEmpty()) {
                ps.setNull(5, java.sql.Types.VARCHAR);
            } else {
                ps.setString(5, kh.getEmail().trim());
            }
            ps.setInt(6, kh.getTrangThai());

            int rows = ps.executeUpdate();
            return rows > 0 ? newMaKH : null;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi tạo khách hàng: " + e.getMessage());
            throw new RuntimeException("Insert and get MaKH failed", e);
        }
    }

    private String generateNewMaKH() {
        String sql = "SELECT MAX(CAST(SUBSTRING(MaKH, 3, LEN(MaKH)) AS INT)) FROM KhachHang";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int max = rs.getInt(1); // nếu bảng rỗng thì getInt sẽ trả về 0
                return String.format("KH%02d", max + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Generate MaKH failed", e);
        }
        return "KH01"; // fallback nếu bảng trống
    }

}
