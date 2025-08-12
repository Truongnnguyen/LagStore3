/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import lags.entity.HoaDonChiTiet;
import lags.util.XJdbc;

/**
 *
 * @author PC
 */
public class HoaDonChiTietDao {

    public List<Object[]> findAllHoaDonChiTiet() {
        List<Object[]> list = new ArrayList<>();
        String sql = """
         SELECT ct.idHDCT, ct.MaHD, ct.MaSPCT, ct.SoLuong, ct.DonGiaBan, 
                   hd.idKhuyenMai, hd.LoaiGiam, hd.GiaTriGiam,
                   (ct.SoLuong * ct.DonGiaBan) AS ThanhTien
            FROM HoaDonChiTiet ct
            JOIN HoaDon hd ON ct.MaHD = hd.MaHD
    """;
        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                Object[] row = new Object[]{
                    rs.getString("idHDCT"),
                    rs.getString("MaHD"),
                    rs.getString("MaSPCT"),
                    rs.getInt("SoLuong"),
                    rs.getInt("DonGiaBan"),
                    rs.getString("idKhuyenMai"),
                    rs.getInt("LoaiGiam"),
                    rs.getInt("GiaTriGiam"),
                    rs.getInt("ThanhTien")
                };

                list.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> findByMaHD(String maHD) {
        List<Object[]> list = new ArrayList<>();
        String sql = """
            SELECT ct.idHDCT, ct.MaHD, ct.MaSPCT, ct.SoLuong, ct.DonGiaBan,
                   hd.idKhuyenMai, hd.LoaiGiam, hd.GiaTriGiam,
                   (ct.SoLuong * ct.DonGiaBan) AS ThanhTien
            FROM HoaDonChiTiet ct
            JOIN HoaDon hd ON ct.MaHD = hd.MaHD
            WHERE ct.MaHD = ?
        """;

        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[]{
                    rs.getString("idHDCT"),
                    rs.getString("MaHD"),
                    rs.getString("MaSPCT"),
                    rs.getInt("SoLuong"),
                    rs.getInt("DonGiaBan"),
                    rs.getString("idKhuyenMai"),
                    rs.getInt("LoaiGiam"),
                    rs.getInt("GiaTriGiam"),
                    rs.getInt("ThanhTien")
                };
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertHDCT(HoaDonChiTiet hdct) {
        String sql = "INSERT INTO HoaDonChiTiet (idHDCT, MaHD, MaSPCT, SoLuong, DonGiaBan) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, hdct.getIdHDCT());
            ps.setString(2, hdct.getMaHD());
            ps.setString(3, hdct.getMaSPCT());
            ps.setInt(4, hdct.getSoLuong());
            ps.setInt(5, hdct.getDonGiaBan());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateMaHDCT() {
        // Tách phần số từ idHDCT và lấy số lớn nhất
        String sql = "SELECT MAX(CAST(SUBSTRING(idHDCT, 5, LEN(idHDCT) - 4) AS INT)) FROM HoaDonChiTiet";
        try (Connection c = XJdbc.openConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int maxSo = rs.getInt(1); // Lấy số lớn nhất
                if (maxSo > 0) {
                    return "HDCT" + (maxSo + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "HDCT1"; // Nếu bảng trống hoặc lỗi thì trả về HDCT1
    }
    public List<Object[]> getChiTietByMaHD(String maHD) {
    List<Object[]> list = new ArrayList<>();
    String sql = """
        SELECT sp.MaSP, sp.TenSP, hdct.SoLuong, hdct.DonGiaBan
        FROM HoaDonChiTiet hdct
        JOIN SanPhamChiTiet spct ON hdct.MaSPCT = spct.MaSPCT
        JOIN SanPham sp ON spct.MaSP = sp.MaSP
        WHERE hdct.MaHD = ?
    """;
    try (Connection con = XJdbc.openConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, maHD);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("MaSP"),
                    rs.getString("TenSP"),
                    rs.getInt("SoLuong"),
                    rs.getInt("DonGiaBan")
                });
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}


}
