/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lags.entity.SanPham;
import lags.entity.SanPhamChiTiet;
import lags.util.XJdbc;
import lags.util.XQuery;

/**
 *
 * @author PC
 */
public class SanPhamDao {

    public List<SanPham> selectAll() {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM SanPham";
        try (Connection conn = XJdbc.openConnection();
                PreparedStatement ps = conn.prepareStatement(sql); 
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SanPham sp = new SanPham(
                        rs.getString("MaSP"),
                        rs.getString("MaXuatXu"),
                        rs.getString("TenSP")
                );

                list.add(sp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<SanPham> findAll(){
        String sql = "SELECT * FROM SanPham";
        return XQuery.getBeanList(SanPham.class, sql);
    }
    public List<Object[]> timKiemSanPham(String keyword) {
    List<Object[]> list = new ArrayList<>();
    String sql = """
        SELECT sp.MaSP, sp.TenSP, ct.MaSPCT, ct.GiaBan, ct.SoLuongTon
        FROM SanPham sp
        JOIN SanPhamChiTiet ct ON sp.MaSP = ct.MaSP
        WHERE sp.MaSP LIKE ? OR sp.TenSP LIKE ?
    """;
    try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
        String key = "%" + keyword + "%";
        ps.setString(1, key);
        ps.setString(2, key);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] row = {
                rs.getString("MaSP"),
                rs.getString("TenSP"),
                rs.getString("MaSPCT"),
                rs.getInt("GiaBan"),
                rs.getInt("SoLuongTon")
            };
            list.add(row);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}

    
    public void create(SanPham entity){
        String sql = "INSERT INTO SanPham (MaSP, MaXuatXu, TenSP) VALUES (?, ?, ?)";
        
        Object[] values = {
            entity.getMaSP(),
            entity.getMaXuatXu(),
            entity.getTenSP()
            
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void update(SanPham entity){
        String sql = "UPDATE SanPham SET MaXuatXu = ?, TenSP = ? WHERE MaSP=?";
        
        Object[] values = {
            entity.getMaXuatXu(),
            entity.getTenSP(),
            entity.getMaSP()
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void deleteByID(String MaSP){
        String sql = "DELETE FROM SanPham WHERE MaSP=?";
        
        XJdbc.executeUpdate(sql, MaSP);
    }
    
}
