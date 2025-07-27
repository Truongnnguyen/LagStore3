/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;

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
}
