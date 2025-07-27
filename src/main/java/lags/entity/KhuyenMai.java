/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.entity;

import java.util.Date;

/**
 *
 * @author icebear
 */
public class KhuyenMai {
    public String idKhuyenMai;
    public int loaiGiam;
    public int giaTriGiam;
    public int giamToiDa;
    public String dkApDung;
    public int trangThai;
    public Date ngayTao;
    public Date ngayHetHan;

    public KhuyenMai() {
    }

    public KhuyenMai(String idKhuyenMai, int loaiGiam, int giaTriGiam, int giamToiDa, String dkApDung, int trangThai, Date ngayTao, Date ngayHetHan) {
        this.idKhuyenMai = idKhuyenMai;
        this.loaiGiam = loaiGiam;
        this.giaTriGiam = giaTriGiam;
        this.giamToiDa = giamToiDa;
        this.dkApDung = dkApDung;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.ngayHetHan = ngayHetHan;
    }

    public String getIdKhuyenMai() {
        return idKhuyenMai;
    }

    public void setIdKhuyenMai(String idKhuyenMai) {
        this.idKhuyenMai = idKhuyenMai;
    }

    public int getLoaiGiam() {
        return loaiGiam;
    }

    public void setLoaiGiam(int loaiGiam) {
        this.loaiGiam = loaiGiam;
    }

    public int getGiaTriGiam() {
        return giaTriGiam;
    }

    public void setGiaTriGiam(int giaTriGiam) {
        this.giaTriGiam = giaTriGiam;
    }

    public int getGiamToiDa() {
        return giamToiDa;
    }

    public void setGiamToiDa(int giamToiDa) {
        this.giamToiDa = giamToiDa;
    }

    public String getDkApDung() {
        return dkApDung;
    }

    public void setDkApDung(String dkApDung) {
        this.dkApDung = dkApDung;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Date getNgayHetHan() {
        return ngayHetHan;
    }

    public void setNgayHetHan(Date ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }

    
    
    
}
