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
    String sql = "SELECT MAX(idHDCT) FROM HoaDonChiTiet";
    try (Connection c = XJdbc.openConnection();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        if (rs.next() && rs.getString(1) != null) {
            String lastId = rs.getString(1); // VD: "HDCT5"
            int so = Integer.parseInt(lastId.replace("HDCT", ""));
            return "HDCT" + (so + 1);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return "HDCT1"; // Trường hợp bảng đang trống
}


}
