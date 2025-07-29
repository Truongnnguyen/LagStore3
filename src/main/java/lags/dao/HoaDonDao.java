/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            hd.MaHD, 
            nv.TenNV, 
            hd.NgayTao, 
            hd.TenKHNhan,
            hd.SoDienThoaiNguoiNhan,
            hd.DiaChiNguoiNhan,
            hd.TrangThai
        FROM HoaDon hd 
        JOIN NhanVien nv ON nv.MaNV = hd.MaNV
        WHERE hd.TrangThai = 0 OR hd.TrangThai = 1
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
                    rs.getInt("TrangThai")
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
        String sql = "INSERT INTO HoaDon (MaHD, MaKH, MaNV, TenKHNhan, DiaChiNguoiNhan, SoDienThoaiNguoiNhan, ThanhTien, TrangThai, idKhuyenMai, LoaiGiam, GiaTriGiam, NgayTao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hd.getMaHD()); // 👈 thêm MaHD truyền từ Java
            ps.setString(2, hd.getMaKH());
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

            ps.executeUpdate(); // vì không dùng OUTPUT nữa
            return hd.getMaHD(); // Trả lại mã mà bạn đã sinh

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String generateMaHD() {
        String sql = "SELECT MAX(MaHD) FROM HoaDon";
        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String maxMa = rs.getString(1);
                if (maxMa == null) {
                    return "HD001";
                }

                int number = Integer.parseInt(maxMa.replace("HD", ""));
                return String.format("HD%03d", number + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "HD001";
    }

    public List<Object[]> timKiemHoaDon(String keyword) {
        List<Object[]> list = new ArrayList<>();
        String sql = """
        SELECT hd.MaHD, hd.TenKHNhan, hd.SoDienThoaiNguoiNhan, hd.NgayTao, hd.TrangThai
        FROM HoaDon hd
        WHERE hd.MaHD LIKE ? OR hd.TenKHNhan LIKE ? OR hd.SoDienThoaiNguoiNhan LIKE ?
    """;
        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            String key = "%" + keyword + "%";
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getString("MaHD"),
                    rs.getString("TenKHNhan"),
                    rs.getString("SoDienThoaiNguoiNhan"),
                    rs.getDate("NgayTao"),
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

    
}
