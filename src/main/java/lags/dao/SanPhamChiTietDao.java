/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import lags.entity.CPU;
import lags.entity.DungLuong;
import lags.entity.GPU;
import lags.entity.RAM;
import lags.entity.SanPham;
import lags.entity.SanPhamChiTiet;
import lags.entity.ThongTinSP;
import lags.entity.XuatXu;
import lags.util.XJdbc;
import lags.util.XQuery;

/**
 *
 * @author PC
 */
public class SanPhamChiTietDao { //List<Object[]> chủ động tạo ra mảng đúng với các giá trị mà bạn muốn đặt ra

    public List<Object[]> selectAllspchitiet() {
        List<Object[]> list = new ArrayList<>();
        String sql = """
                          SELECT 
                                 hdct.idHDCT,
                                 spct.MaSPCT,
                                 spct.MaSP,
                                 sp.TenSP,
                                 cpu.TenCPU,
                                 ram.DungLuong as DungLuongR,
                                 dl.DungLuong,
                                 gpu.TenGPU,
                                 spct.Gia,
                                 spct.SoLuong
                             FROM SanPhamChiTiet spct
                             JOIN SanPham sp ON sp.MaSP = spct.MaSP
                             JOIN CPU cpu ON cpu.MaCPU = spct.MaCPU
                             JOIN Ram ram ON ram.MaRAM = spct.MaRAM
                             JOIN DungLuong dl ON dl.MaDungLuong = spct.MaDungLuong
                             JOIN GPU gpu ON gpu.MaGPU = spct.MaGPU
                             LEFT JOIN HoaDonChiTiet hdct ON hdct.MaSPCT = spct.MaSPCT
                     """;
        try (Connection conn = XJdbc.openConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] row = new Object[]{
                    rs.getString("idHDCT"),
                    rs.getString("MaSPCT"),
                    rs.getString("MaSP"),
                    rs.getString("TenSP"),
                    rs.getString("TenCPU"),
                    rs.getString("DungLuongR"),
                    rs.getString("DungLuong"),
                    rs.getString("TenGPU"),
                    rs.getInt("Gia"),
                    rs.getInt("SoLuong"),};
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> selectByMaHD(String maHD) {
        List<Object[]> list = new ArrayList<>();
        String sql = """
        SELECT 
            sp.TenSP,
            hdct.SoLuong,
            spct.Gia,
            (hdct.SoLuong * spct.Gia) AS ThanhTien
        FROM HoaDonChiTiet hdct
        JOIN SanPhamChiTiet spct ON spct.MaSPCT = hdct.MaSPCT
        JOIN SanPham sp ON sp.MaSP = spct.MaSP
        WHERE hdct.MaHD = ?
    """;
        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("TenSP"),
                    rs.getInt("SoLuong"),
                    rs.getInt("Gia"),
                    rs.getInt("ThanhTien")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

//    public List<ThongTinSP> findByMaSP(String MaSP) {
//        String sql = """
//                     select
//                     	spct.MaSPCT,
//                     	spct.MaSP,
//                     	sp.TenSP,
//                     	cpu.TenCPU,
//                     	ram.DungLuong as DungLuongR,
//                     	dl.DungLuong,
//                     	gpu.TenGPU,
//                        im.soIMEI,
//                     	spct.Gia,
//                     	spct.SoLuong
//                     from SanPhamChiTiet spct
//                     join SanPham sp on sp.MaSP = spct.MaSP
//                     join CPU cpu on cpu.MaCPU = spct.MaCPU
//                     join Ram ram on ram.MaRAM = spct.MaRAM
//                     join DungLuong dl on dl.MaDungLuong = spct.MaDungLuong
//                     join GPU gpu on gpu.MaGPU = spct.MaGPU
//                     join IMEI im on im.MaSPCT = spct.MaSPCT
//                     where spct.MaSP=?
//                     """;
//
//        return XQuery.getBeanList(ThongTinSP.class, sql, MaSP);
//    }
    public List<ThongTinSP> findALLtt() {
        String sql = """
        SELECT
            spct.MaSPCT, spct.MaSP, spct.MaCPU, spct.MaRAM, spct.MaDungLuong, spct.MaGPU,
            spct.Gia, spct.SoLuong,

            sp.TenSP, sp.MaXuatXu,
            xx.XuatXu,

            cpu.TenCPU,
            ram.TenRAM, ram.DungLuong AS DLRAM,
            dl.DungLuong AS DLDL,
            gpu.TenGPU,
            im.SoIMEI
        FROM SanPhamChiTiet spct
        JOIN SanPham sp ON sp.MaSP = spct.MaSP
        JOIN XuatXu xx ON xx.MaXuatXu = sp.MaXuatXu
        JOIN CPU cpu ON cpu.MaCPU = spct.MaCPU
        JOIN Ram ram ON ram.MaRAM = spct.MaRAM
        JOIN DungLuong dl ON dl.MaDungLuong = spct.MaDungLuong
        JOIN GPU gpu ON gpu.MaGPU = spct.MaGPU
        JOIN IMEI im ON im.MaSPCT = spct.MaSPCT
    """;

        List<ThongTinSP> list = new ArrayList<>();

        try (
                Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql);) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ThongTinSP info = new ThongTinSP();

                // 1. CPU
                CPU cpu = new CPU();
                cpu.setMaCPU(rs.getString("MaCPU"));
                cpu.setTenCPU(rs.getString("TenCPU"));

                // 2. RAM
                RAM ram = new RAM();
                ram.setMaRAM(rs.getString("MaRAM"));
                ram.setTenRAM(rs.getString("TenRAM"));
                ram.setDungLuong(rs.getString("DLRAM"));

                // 3. Dung Lượng
                DungLuong dl = new DungLuong();
                dl.setMaDungLuong(rs.getString("MaDungLuong"));
                dl.setDungLuong(rs.getString("DLDL"));

                // 4. GPU
                GPU gpu = new GPU();
                gpu.setMaGPU(rs.getString("MaGPU"));
                gpu.setTenGPU(rs.getString("TenGPU"));

                // 5. SanPham
                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("MaSP"));
                sp.setTenSP(rs.getString("TenSP"));
                sp.setMaXuatXu(rs.getString("MaXuatXu"));

                // 6. SanPhamChiTiet
                SanPhamChiTiet spct = new SanPhamChiTiet();
                spct.setMaSPCT(rs.getString("MaSPCT"));
                spct.setMaSP(sp.getMaSP());
                spct.setMaCPU(cpu.getMaCPU());
                spct.setMaRAM(ram.getMaRAM());
                spct.setMaDungLuong(dl.getMaDungLuong());
                spct.setMaGPU(gpu.getMaGPU());
                spct.setGia(rs.getInt("Gia"));
                spct.setSoLuong(rs.getInt("SoLuong"));

                // XuatXu
                XuatXu xx = new XuatXu();
                xx.setMaXuatXu(rs.getString("MaXuatXu"));
                xx.setXuatXu(rs.getString("XuatXu"));

                // 7. Build full info
                info.setSpct(spct);
                info.setSanPham(sp);
                info.setCpu(cpu);
                info.setRam(ram);
                info.setDungLuong(dl);
                info.setGpu(gpu);
                info.setSoIMEI(rs.getString("SoIMEI"));
                info.setXuatXu(xx);

                list.add(info);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ThongTinSP> findByMaSP(String maSP) {
        String sql = """
        SELECT
            spct.MaSPCT, spct.MaSP, spct.MaCPU, spct.MaRAM, spct.MaDungLuong, spct.MaGPU,
            spct.Gia, spct.SoLuong,

            sp.TenSP, sp.MaXuatXu,
            xx.XuatXu,

            cpu.TenCPU,
            ram.TenRAM, ram.DungLuong AS DLRAM,
            dl.DungLuong AS DLDL,
            gpu.TenGPU,
            im.SoIMEI
        FROM SanPhamChiTiet spct
        JOIN SanPham sp ON sp.MaSP = spct.MaSP
        JOIN XuatXu xx ON xx.MaXuatXu = sp.MaXuatXu
        JOIN CPU cpu ON cpu.MaCPU = spct.MaCPU
        JOIN Ram ram ON ram.MaRAM = spct.MaRAM
        JOIN DungLuong dl ON dl.MaDungLuong = spct.MaDungLuong
        JOIN GPU gpu ON gpu.MaGPU = spct.MaGPU
        JOIN IMEI im ON im.MaSPCT = spct.MaSPCT
        WHERE spct.MaSP = ?
    """;

        List<ThongTinSP> list = new ArrayList<>();

        try (
                Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ThongTinSP info = new ThongTinSP();

                // 1. CPU
                CPU cpu = new CPU();
                cpu.setMaCPU(rs.getString("MaCPU"));
                cpu.setTenCPU(rs.getString("TenCPU"));

                // 2. RAM
                RAM ram = new RAM();
                ram.setMaRAM(rs.getString("MaRAM"));
                ram.setTenRAM(rs.getString("TenRAM"));
                ram.setDungLuong(rs.getString("DLRAM"));

                // 3. Dung Lượng
                DungLuong dl = new DungLuong();
                dl.setMaDungLuong(rs.getString("MaDungLuong"));
                dl.setDungLuong(rs.getString("DLDL"));

                // 4. GPU
                GPU gpu = new GPU();
                gpu.setMaGPU(rs.getString("MaGPU"));
                gpu.setTenGPU(rs.getString("TenGPU"));

                // 5. SanPham
                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("MaSP"));
                sp.setTenSP(rs.getString("TenSP"));
                sp.setMaXuatXu(rs.getString("MaXuatXu"));

                // 6. SanPhamChiTiet
                SanPhamChiTiet spct = new SanPhamChiTiet();
                spct.setMaSPCT(rs.getString("MaSPCT"));
                spct.setMaSP(sp.getMaSP());
                spct.setMaCPU(cpu.getMaCPU());
                spct.setMaRAM(ram.getMaRAM());
                spct.setMaDungLuong(dl.getMaDungLuong());
                spct.setMaGPU(gpu.getMaGPU());
                spct.setGia(rs.getInt("Gia"));
                spct.setSoLuong(rs.getInt("SoLuong"));

                // XuatXu
                XuatXu xx = new XuatXu();
                xx.setMaXuatXu(rs.getString("MaXuatXu"));
                xx.setXuatXu(rs.getString("XuatXu"));

                // 7. Build full info
                info.setSpct(spct);
                info.setSanPham(sp);
                info.setCpu(cpu);
                info.setRam(ram);
                info.setDungLuong(dl);
                info.setGpu(gpu);
                info.setSoIMEI(rs.getString("SoIMEI"));
                info.setXuatXu(xx);

                list.add(info);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void create(SanPhamChiTiet entity) {
        String sql = "INSERT INTO SanPhamChiTiet (MaSPCT, MaSP, MaCPU, MaRAM, MaDungLuong, MaGPU, Gia, SoLuong) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Object[] values = {
            entity.getMaSPCT(),
            entity.getMaSP(),
            entity.getMaCPU(),
            entity.getMaRAM(),
            entity.getMaDungLuong(),
            entity.getMaGPU(),
            entity.getGia(),
            entity.getSoLuong()
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void update(SanPhamChiTiet entity){
        String sql = "UPDATE SanPhamChiTiet SET MaSP = ?, MaCPU = ?, MaRAM = ?, MaDungLuong = ?, MaGPU = ?, Gia = ?, SoLuong = ? WHERE MaSPCT =?";
        
        Object[] values = {
            entity.getMaSP(),
            entity.getMaCPU(),
            entity.getMaRAM(),
            entity.getMaDungLuong(),
            entity.getMaGPU(),
            entity.getGia(),
            entity.getSoLuong(),
            entity.getMaSPCT()
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void deleteByID(String MaSPCT){
        String sql = "DELETE FROM IMEI WHERE MaSPCT = ?";
        String sql1 = "DELETE FROM SanPhamChiTiet WHERE MaSPCT = ?";
        
        XJdbc.executeUpdate(sql, MaSPCT);
        XJdbc.executeUpdate(sql1, MaSPCT);
    }

}
