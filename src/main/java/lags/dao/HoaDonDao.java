/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lags.entity.HoaDon;
import lags.entity.SanPham;
import lags.util.XJdbc;

/**
 *
 * @author PC
 */
public class HoaDonDao {

    public List<HoaDon> findAllHoaDon() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon";
        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("MaHD"));
                hd.setMaKH(rs.getString("MaKH"));
                hd.setMaNV(rs.getString("MaNV"));
                hd.setTenKHNhan(rs.getString("TenKHNhan"));
                hd.setDiaChiNguoiNhan(rs.getString("DiaChiNguoiNhan"));
                hd.setSoDienThoaiNguoiNhan(rs.getString("SoDienThoaiNguoiNhan"));
                hd.setThanhTien(rs.getInt("ThanhTien"));
                hd.setTrangThai(rs.getInt("TrangThai"));
                hd.setIdKhuyenMai(rs.getString("idKhuyenMai"));
                hd.setLoaiGiam(rs.getInt("LoaiGiam"));
                hd.setGiaTriGiam(rs.getInt("GiaTriGiam"));
                hd.setNgayTao(rs.getDate("NgayTao"));
                list.add(hd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> getDonHangCho() {
        String sql = """
        SELECT 
                    hd.MaHD, nv.TenNV, hd.NgayTao, 
                    hd.TenKHNhan, hd.SoDienThoaiNguoiNhan, hd.DiaChiNguoiNhan,
                    hd.ThanhTien, hd.TrangThai, hd.idKhuyenMai, hd.LoaiGiam, hd.GiaTriGiam
                FROM HoaDon hd 
                JOIN NhanVien nv ON nv.MaNV = hd.MaNV
                WHERE hd.TrangThai = 0          -- << chỉ lấy hóa đơn chờ
                ORDER BY hd.NgayTao DESC
    """;

        List<Object[]> list = new ArrayList<>();
        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] row = new Object[]{
                    rs.getString("MaHD"),
                    rs.getString("TenNV"),
                    rs.getDate("NgayTao"),
                    rs.getString("TenKHNhan"),
                    rs.getString("SoDienThoaiNguoiNhan"),
                    rs.getString("DiaChiNguoiNhan"),
                    rs.getInt("ThanhTien"), // ✅ thêm
                    rs.getInt("TrangThai"),
                    rs.getString("idKhuyenMai"),
                    rs.getInt("LoaiGiam"),
                    rs.getInt("GiaTriGiam")
                };
                list.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insertChiTiet(String maHD, String maSPCT, int soLuong, int donGia) {
        String sql = "INSERT INTO HoaDonChiTiet (maHD, MaSPCT, SoLuong, DonGiaBan) VALUES (?, ?, ?, ?)";
        XJdbc.executeUpdate(sql, maHD, maSPCT, soLuong, donGia);
    }

    public void updateTrangThai(String maHD) {
        String sql = "UPDATE HoaDon SET TrangThai = 1 WHERE MaHD = ?";
        XJdbc.executeUpdate(sql, maHD);
    }

    public String insertHoaDon(HoaDon hd) {
    // Lưu ý: cột đúng là SoDienThoaiNguoiNhan (không phải SoDTNguoiNhan)
    String sql = """
        INSERT INTO HoaDon 
        (MaHD, MaKH, MaNV, TenKHNhan, DiaChiNguoiNhan, SoDienThoaiNguoiNhan, 
         ThanhTien, TrangThai, idKhuyenMai, LoaiGiam, GiaTriGiam, NgayTao)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

    try (Connection con = XJdbc.openConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, hd.getMaHD());

        // MaKH của bạn là kiểu NVARCHAR (ví dụ "KH006") → setString, không parseInt
        if (hd.getMaKH() == null || hd.getMaKH().isBlank()) {
            ps.setNull(2, java.sql.Types.VARCHAR);   // khách vãng lai
        } else {
            ps.setString(2, hd.getMaKH());
        }

        ps.setString(3, hd.getMaNV());
        ps.setString(4, hd.getTenKHNhan());
        ps.setString(5, hd.getDiaChiNguoiNhan());
        ps.setString(6, hd.getSoDienThoaiNguoiNhan());
        ps.setInt(7, hd.getThanhTien());
        ps.setInt(8, hd.getTrangThai());
        ps.setString(9, hd.getIdKhuyenMai());
        ps.setInt(10, hd.getLoaiGiam());
        ps.setInt(11, hd.getGiaTriGiam());
        ps.setTimestamp(12, new Timestamp(hd.getNgayTao().getTime()));

        ps.executeUpdate();
        return hd.getMaHD();

    } catch (Exception e) {
        throw new RuntimeException("Lỗi khi insert hóa đơn: " + e.getMessage(), e);
    }
}


    public String generateMaHD() {
        String sql = "SELECT MAX(CAST(SUBSTRING(MaHD, 3, LEN(MaHD)) AS INT)) FROM HoaDon";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            int max = 0;
            if (rs.next()) {
                max = rs.getInt(1);
                if (rs.wasNull()) {
                    max = 0; // bảng rỗng
                }
            }

            return "HD" + (max + 1); // ❌ bỏ %02d, nối trực tiếp

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Generate MaHD failed", e);
        }
    }

    public List<Object[]> timKiemHoaDon(String keyword) {
        List<Object[]> list = new ArrayList<>();
        String sql = """
            SELECT 
                    hd.MaHD, nv.TenNV, hd.NgayTao,
                    hd.TenKHNhan, hd.SoDienThoaiNguoiNhan, hd.DiaChiNguoiNhan,
                    hd.TrangThai
                FROM HoaDon hd
                JOIN NhanVien nv ON nv.MaNV = hd.MaNV
                WHERE hd.TrangThai = 0 AND (     -- << chỉ tìm trong đơn chờ
                      hd.MaHD LIKE ? OR
                      nv.TenNV LIKE ? OR
                      CONVERT(VARCHAR, hd.NgayTao, 23) LIKE ? OR
                      hd.TenKHNhan LIKE ? OR
                      hd.SoDienThoaiNguoiNhan LIKE ? OR
                      hd.DiaChiNguoiNhan LIKE ? OR
                      CASE 
                          WHEN hd.TrangThai = 0 THEN N'Chưa thanh toán'
                          WHEN hd.TrangThai = 1 THEN N'Đã thanh toán'
                      END LIKE ?
                )
                ORDER BY hd.NgayTao DESC
    """;

        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            String key = "%" + keyword + "%";
            for (int i = 1; i <= 7; i++) {
                ps.setString(i, key);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getString("MaHD"),
                    rs.getString("TenNV"),
                    rs.getDate("NgayTao"),
                    rs.getString("TenKHNhan"),
                    rs.getString("SoDienThoaiNguoiNhan"),
                    rs.getString("DiaChiNguoiNhan"),
                    rs.getInt("TrangThai")
                };
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<HoaDon> locTheoNgay(Date tuNgay, Date denNgay) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE NgayTao BETWEEN ? AND ?";
        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(tuNgay.getTime()));
            ps.setDate(2, new java.sql.Date(denNgay.getTime()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("MaHD"));
                hd.setMaKH(rs.getString("MaKH"));
                hd.setMaNV(rs.getString("MaNV"));
                hd.setTenKHNhan(rs.getString("TenKHNhan"));
                hd.setDiaChiNguoiNhan(rs.getString("DiaChiNguoiNhan"));
                hd.setSoDienThoaiNguoiNhan(rs.getString("SoDienThoaiNguoiNhan"));
                hd.setThanhTien(rs.getInt("ThanhTien"));
                hd.setTrangThai(rs.getInt("TrangThai"));
                hd.setIdKhuyenMai(rs.getString("idKhuyenMai"));
                hd.setLoaiGiam(rs.getInt("LoaiGiam"));
                hd.setGiaTriGiam(rs.getInt("GiaTriGiam"));
                hd.setNgayTao(rs.getDate("NgayTao"));
                list.add(hd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    // Tìm từ 1 ngày trở đi

    public List<HoaDon> findHoaDonTuNgay(Date tuNgay) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE NgayTao >= ?";
        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(tuNgay.getTime()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("MaHD"));
                hd.setMaKH(rs.getString("MaKH"));
                hd.setMaNV(rs.getString("MaNV"));
                hd.setTenKHNhan(rs.getString("TenKHNhan"));
                hd.setDiaChiNguoiNhan(rs.getString("DiaChiNguoiNhan"));
                hd.setSoDienThoaiNguoiNhan(rs.getString("SoDienThoaiNguoiNhan"));
                hd.setThanhTien(rs.getInt("ThanhTien"));
                hd.setTrangThai(rs.getInt("TrangThai"));
                hd.setIdKhuyenMai(rs.getString("idKhuyenMai"));
                hd.setLoaiGiam(rs.getInt("LoaiGiam"));
                hd.setGiaTriGiam(rs.getInt("GiaTriGiam"));
                hd.setNgayTao(rs.getDate("NgayTao"));
                list.add(hd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

// Tìm đến 1 ngày (<=)
    public List<HoaDon> findHoaDonDenNgay(Date denNgay) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE NgayTao <= ?";
        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(denNgay.getTime()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("MaHD"));
                hd.setMaKH(rs.getString("MaKH"));
                hd.setMaNV(rs.getString("MaNV"));
                hd.setTenKHNhan(rs.getString("TenKHNhan"));
                hd.setDiaChiNguoiNhan(rs.getString("DiaChiNguoiNhan"));
                hd.setSoDienThoaiNguoiNhan(rs.getString("SoDienThoaiNguoiNhan"));
                hd.setThanhTien(rs.getInt("ThanhTien"));
                hd.setTrangThai(rs.getInt("TrangThai"));
                hd.setIdKhuyenMai(rs.getString("idKhuyenMai"));
                hd.setLoaiGiam(rs.getInt("LoaiGiam"));
                hd.setGiaTriGiam(rs.getInt("GiaTriGiam"));
                hd.setNgayTao(rs.getDate("NgayTao"));
                list.add(hd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

// Wrapper để tương thích tên gọi bạn dùng trong panel
    public List<HoaDon> findHoaDonTheoKhoangNgay(Date tuNgay, Date denNgay) {
        // nếu muốn dùng locTheoNgay sẵn có thì trả về nó
        return locTheoNgay(tuNgay, denNgay);
    }

    public void updateHoaDonSauThanhToan(String maHD, int thanhTien, String idKhuyenMai, int loaiGiam, int giaTriGiam) {
        String sql = """
        UPDATE HoaDon
        SET 
            ThanhTien = ?, 
            idKhuyenMai = ?, 
            LoaiGiam = ?, 
            GiaTriGiam = ?, 
            TrangThai = 1
        WHERE MaHD = ?
    """;

        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, thanhTien);
            ps.setString(2, idKhuyenMai);
            ps.setInt(3, loaiGiam);
            ps.setInt(4, giaTriGiam);
            ps.setString(5, maHD);

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int countPending() {
        String sql = "SELECT COUNT(*) FROM HoaDon WHERE TrangThai = 0"; // 0 = PENDING
        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countPendingByStaff(String maNV) {
        String sql = "SELECT COUNT(*) FROM HoaDon WHERE TrangThai = 0 AND MaNV = ?";
        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    

}
