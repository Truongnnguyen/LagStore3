/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;

import java.sql.ResultSet;      // ✅ đúng
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import lags.entity.KhuyenMai;
import lags.util.XJdbc;
import lags.util.XQuery;

/**
 *
 * @author ADMIN
 */
public class KhuyenMaiDao {

    public List<KhuyenMai> findAll() {
        String sql = "SELECT * FROM KhuyenMai";
        return XQuery.getBeanList(KhuyenMai.class, sql);
    }

    public void create(KhuyenMai entity) {
        String sql = "INSERT INTO KhuyenMai "
                + "(idKhuyenMai, LoaiGiam, GiaTriGiam, GiamToiDa, DKApDung, TrangThai, NgayTao, NgayHetHan) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Object[] values = {
            entity.getIdKhuyenMai(),
            entity.getLoaiGiam(),
            entity.getGiaTriGiam(),
            entity.getGiamToiDa(),
            entity.getDkApDung(),
            entity.getTrangThai(),
            new java.sql.Date(entity.getNgayTao().getTime()),
            new java.sql.Date(entity.getNgayHetHan().getTime())
        };

        XJdbc.executeUpdate(sql, values);
    }

    public void update(KhuyenMai entity) {
        String sql = "UPDATE KhuyenMai SET "
                + "LoaiGiam = ?, "
                + "GiaTriGiam = ?, "
                + "GiamToiDa = ?, "
                + "DKApDung = ?, "
                + "TrangThai = ?, "
                + "NgayTao = ?, "
                + "NgayHetHan = ? "
                + "WHERE idKhuyenMai = ?";

        Object[] values = {
            entity.getLoaiGiam(),
            entity.getGiaTriGiam(),
            entity.getGiamToiDa(),
            entity.getDkApDung(),
            entity.getTrangThai(),
            new java.sql.Date(entity.getNgayTao().getTime()),
            new java.sql.Date(entity.getNgayHetHan().getTime()),
            entity.getIdKhuyenMai()
        };

        XJdbc.executeUpdate(sql, values);
    }

    public void deleteByID(String idKhuyenMai) {
        String sql = "DELETE FROM KhuyenMai WHERE idKhuyenMai = ?";
        XJdbc.executeUpdate(sql, idKhuyenMai);
    }

    public List<KhuyenMai> getKhuyenMaiConHieuLuc() {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai WHERE TrangThai = 1 AND GETDATE() BETWEEN NgayTao AND NgayHetHan";

        try (
            Connection con = XJdbc.openConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                KhuyenMai km = new KhuyenMai();
                km.setIdKhuyenMai(rs.getString("idKhuyenMai"));
                km.setLoaiGiam(rs.getInt("LoaiGiam"));
                km.setGiaTriGiam(rs.getInt("GiaTriGiam"));
                km.setGiamToiDa(rs.getInt("GiamToiDa"));
//                km.setDkApDung(rs.getString("DKApDung"));
                km.setTrangThai(rs.getInt("TrangThai"));
                km.setNgayTao(rs.getDate("NgayTao"));
                km.setNgayHetHan(rs.getDate("NgayHetHan"));
                list.add(km);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public Object[] getThongTinKM(String idKM) {
            String sql = "SELECT * FROM KhuyenMai WHERE idKhuyenMai = ?";
            try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, idKM);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return new Object[]{
                        rs.getString("idKhuyenMai"),
                        rs.getInt("LoaiGiam"),
                        rs.getInt("GiaTriGiam"),
                        rs.getInt("GiamToiDa"),
                        rs.getString("DKApDung"),
                        rs.getDate("NgayHetHan")
                    };
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
}
