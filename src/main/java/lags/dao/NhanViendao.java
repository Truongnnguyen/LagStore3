/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;



import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import lags.entity.NhanVien;
import lags.util.XJdbc;
import lags.util.XQuery;

/**
 *
 * @author admin
 */
public class NhanViendao {
       public List<NhanVien> getAllNhanVien() {
        String getAllNhanVienSql = "SELECT * FROM NhanVien";
        
        return XQuery.getBeanList(NhanVien.class, getAllNhanVienSql);
    }
       public int insertNhanVien(NhanVien nv) {
        String insertNhanVienSql = "INSERT INTO NhanVien(MaNV, TenNV, ChucVu, Email, SoDienThoai, GioiTinh, TenDangNhap, MatKhau) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        Object[] values = {
            nv.getMaNV(),
            nv.getTenNV(),
            nv.getChucVu(),
            nv.getEmail(),
            nv.getSoDienThoai(),
            nv.getGioiTinh(),
            nv.getTenDangNhap(),
            nv.getMatKhau()
        };
        return XJdbc.executeUpdate(insertNhanVienSql, values);
    }
       public int create(NhanVien nv) {
        String createNhanVienSql = "insert into NhanVien(MaNV,TenNV,ChucVu,Email,SoDienThoai,GioiTinh,TenDangNhap,MatKhau) values(?,?,?,?,?,?,?,?)";

        Object[] values = {
            nv.getMaNV(),
            nv.getTenNV(),
            nv.getChucVu(),
            nv.getEmail(),
            nv.getSoDienThoai(),
            nv.getGioiTinh(),
            nv.getTenDangNhap(),
            nv.getMatKhau()
        };

        return XJdbc.executeUpdate(createNhanVienSql, values);
       }
       public int update(NhanVien nv) {
        String updateNhanVienSql = "update NhanVien set TenNV = ?, ChucVu = ?,Email = ?, SoDienThoai = ?, GioiTinh = ?, TenDangNhap = ?,MatKhau = ? WHERE MaNV=?";

        Object[] values = {
            
            nv.getTenNV(),
            nv.getChucVu(),
            nv.getEmail(),
            nv.getSoDienThoai(),
            nv.getGioiTinh(),
            nv.getTenDangNhap(),
            nv.getMatKhau(),
            nv.getMaNV(),
        };
        return XJdbc.executeUpdate(updateNhanVienSql, values);
    }
     public List<NhanVien> selectAll() {
    List<NhanVien> list = new ArrayList<>();
    String sql = "SELECT * FROM NhanVien";
    try (ResultSet rs = XJdbc.executeQuery(sql)) {
        while (rs.next()) {
            NhanVien nv = new NhanVien();
            nv.setMaNV(rs.getString("MaNV"));
            nv.setTenNV(rs.getString("TenNV"));
            nv.setChucVu(rs.getInt("ChucVu"));
            nv.setEmail(rs.getString("Email"));
            nv.setSoDienThoai(rs.getString("SoDienThoai"));
            nv.setGioiTinh(rs.getInt("GioiTinh"));
            nv.setTenDangNhap(rs.getString("TenDangNhap"));
            nv.setMatKhau(rs.getString("MatKhau"));
            list.add(nv);
        }
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Lỗi truy vấn dữ liệu nhân viên");
    }
    return list;
}
    public int delete(String maNV) {
        String deleteNhanVienSql = "delete from NhanVien where MaNV = ?";

        return XJdbc.executeUpdate(deleteNhanVienSql, maNV);

    }
    public List<NhanVien> findByName(String TenNV) {
        String findByNameSql = "select * from NhanVien where TenNV like ?";

        Object[] values = {
            "%" + TenNV + "%"
        };
        return XQuery.getBeanList(NhanVien.class, findByNameSql, values);
    }
    
    public List<NhanVien> findByTND(String TDN){
        String sql = "select * from NhanVien where TenDangNhap = ?";
        
        return XQuery.getBeanList(NhanVien.class, sql, TDN);
    }
    
    
}
