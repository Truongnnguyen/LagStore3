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
        SELECT ct.idHDCT, ct.MaHD, ct.MaSPCT, ct.SoLuong, ct.DonGiaBan, hd.idKhuyenMai,
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
           SELECT ct.idHDCT, ct.MaHD, ct.MaSPCT, ct.SoLuong, ct.DonGiaBan, (ct.SoLuong * ct.DonGiaBan) AS ThanhTien
            FROM HoaDonChiTiet ct
            WHERE ct.MaHD = ?
    """;
        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getString("idHDCT"),
                    rs.getString("MaHD"),
                    rs.getString("MaSPCT"),
                    rs.getInt("SoLuong"),
                    rs.getInt("DonGiaBan"),
                    //                rs.getString("idKhuyenMai"),
                    rs.getInt("ThanhTien")
                };
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
