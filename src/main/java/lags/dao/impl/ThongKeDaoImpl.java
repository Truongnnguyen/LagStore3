/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao.impl;
import lags.dao.ThongKeDao;
import lags.entity.*;
import lags.util.XJdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author admin
 */
public class ThongKeDaoImpl implements ThongKeDao {
     @Override
    public List<DoanhThuNgay> doanhThuTheoNgay(Date from, Date to) {
        String sql = """
            SELECT NgayTao,
                   SUM(ThanhTien) AS DoanhThuNet,
                   SUM(GiaTriGiam) AS TongGiam,
                   SUM(ThanhTien + GiaTriGiam) AS DoanhThuGross,
                   COUNT(*) AS SoDon
            FROM HoaDon
            WHERE NgayTao BETWEEN ? AND ?
            GROUP BY NgayTao
            ORDER BY NgayTao
            """;
        List<DoanhThuNgay> list = new ArrayList<>();
        try (Connection c = XJdbc.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, from);
            ps.setDate(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DoanhThuNgay d = new DoanhThuNgay();
                    d.setNgay(rs.getDate("NgayTao"));
                    d.setDoanhThuNet(rs.getLong("DoanhThuNet"));
                    d.setTongGiam(rs.getLong("TongGiam"));
                    d.setDoanhThuGross(rs.getLong("DoanhThuGross"));
                    d.setSoDon(rs.getInt("SoDon"));
                    list.add(d);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi doanhThuTheoNgay", e);
        }
        return list;
    }

    @Override
    public List<DoanhThuThang> doanhThuTheoThang(int soThangGanNhat) {
        // Lấy N tháng gần nhất dựa trên FORMAT yyyy-MM
        String sql = """
            SELECT TOP (?) FORMAT(NgayTao,'yyyy-MM') AS Thang,
                   SUM(ThanhTien) AS DoanhThuNet,
                   SUM(GiaTriGiam) AS TongGiam,
                   COUNT(*) AS SoDon,
                   SUM(ThanhTien)*1.0 / NULLIF(COUNT(*),0) AS AOV
            FROM HoaDon
            GROUP BY FORMAT(NgayTao,'yyyy-MM')
            ORDER BY Thang DESC
            """;
        List<DoanhThuThang> list = new ArrayList<>();
        try (Connection c = XJdbc.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, soThangGanNhat);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DoanhThuThang d = new DoanhThuThang();
                    d.setThang(rs.getString("Thang"));
                    d.setDoanhThuNet(rs.getLong("DoanhThuNet"));
                    d.setTongGiam(rs.getLong("TongGiam"));
                    d.setSoDon(rs.getInt("SoDon"));
                    d.setAov(rs.getDouble("AOV"));
                    list.add(d);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi doanhThuTheoThang", e);
        }
        return list;
    }

    @Override
    public List<TopItem> topKhachHang(int limit) {
        String sql = """
            SELECT TOP (?) HD.MaKH, KH.TenKH,
                   SUM(HD.ThanhTien) AS DoanhThuNet,
                   COUNT(*) AS SoDon
            FROM HoaDon HD
            JOIN KhachHang KH ON KH.MaKH = HD.MaKH
            GROUP BY HD.MaKH, KH.TenKH
            ORDER BY DoanhThuNet DESC
            """;
        List<TopItem> list = new ArrayList<>();
        try (Connection c = XJdbc.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TopItem t = new TopItem();
                    t.setId(rs.getString("MaKH"));
                    t.setTen(rs.getString("TenKH"));
                    t.setDoanhThu(rs.getLong("DoanhThuNet"));
                    t.setSoDon(rs.getInt("SoDon"));
                    list.add(t);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi topKhachHang", e);
        }
        return list;
    }

    @Override
    public List<TopItem> topNhanVien(int limit) {
        String sql = """
            SELECT TOP (?) HD.MaNV, NV.TenNV,
                   SUM(HD.ThanhTien) AS DoanhThuNet,
                   COUNT(*) AS SoDon
            FROM HoaDon HD
            JOIN NhanVien NV ON NV.MaNV = HD.MaNV
            GROUP BY HD.MaNV, NV.TenNV
            ORDER BY DoanhThuNet DESC
            """;
        List<TopItem> list = new ArrayList<>();
        try (Connection c = XJdbc.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TopItem t = new TopItem();
                    t.setId(rs.getString("MaNV"));
                    t.setTen(rs.getString("TenNV"));
                    t.setDoanhThu(rs.getLong("DoanhThuNet"));
                    t.setSoDon(rs.getInt("SoDon"));
                    list.add(t);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi topNhanVien", e);
        }
        return list;
    }

    @Override
    public List<TopSanPham> topSanPham(int limit) {
        String sql = """
            SELECT TOP (?) SP.MaSP, SP.TenSP,
                   SUM(CT.SoLuong * CT.DonGiaBan) AS DoanhThuSP,
                   SUM(CT.SoLuong) AS SoLuongBan
            FROM HoaDonChiTiet CT
            JOIN HoaDon HD ON HD.MaHD = CT.MaHD
            JOIN SanPhamChiTiet SPCT ON SPCT.MaSPCT = CT.MaSPCT
            JOIN SanPham SP ON SP.MaSP = SPCT.MaSP
            GROUP BY SP.MaSP, SP.TenSP
            ORDER BY DoanhThuSP DESC
            """;
        List<TopSanPham> list = new ArrayList<>();
        try (Connection c = XJdbc.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TopSanPham t = new TopSanPham();
                    t.setMaSP(rs.getString("MaSP"));
                    t.setTenSP(rs.getString("TenSP"));
                    t.setDoanhThu(rs.getLong("DoanhThuSP"));
                    t.setSoLuongBan(rs.getInt("SoLuongBan"));
                    list.add(t);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi topSanPham", e);
        }
        return list;
    }

    @Override
    public TyTrongGiam tyTrongGiam() {
        String sql = """
            SELECT
              SUM(GiaTriGiam) AS TongGiam,
              SUM(ThanhTien)  AS DoanhThuNet,
              SUM(ThanhTien + GiaTriGiam) AS DoanhThuGross
            FROM HoaDon
            """;
        try (Connection c = XJdbc.openConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                TyTrongGiam t = new TyTrongGiam();
                long giam = rs.getLong("TongGiam");
                long net = rs.getLong("DoanhThuNet");
                long gross = rs.getLong("DoanhThuGross");
                t.setTongGiam(giam);
                t.setDoanhThuNet(net);
                t.setDoanhThuGross(gross);
                t.setTyLeGiam(gross == 0 ? 0 : (giam * 1.0 / gross));
                return t;
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tyTrongGiam", e);
        }
        return null;
    }
}