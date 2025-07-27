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
public class HoaDon {
    public String maHD;
    public String maKH;
    public String maNV;
    public String tenKHNhan;
    public String diaChiNguoiNhan;
    public String soDienThoaiNguoiNhan;
    public int thanhTien;
    public int trangThai;
    public String idKhuyenMai;
    public int loaiGiam;
    public int giaTriGiam;
    public Date ngayTao;

    public HoaDon() {
    }

    public HoaDon(String maHD, String maKH, String maNV, String tenKHNhan, String diaChiNguoiNhan, String soDienThoaiNguoiNhan, int thanhTien, int trangThai, String idKhuyenMai, int loaiGiam, int giaTriGiam, Date ngayTao) {
        this.maHD = maHD;
        this.maKH = maKH;
        this.maNV = maNV;
        this.tenKHNhan = tenKHNhan;
        this.diaChiNguoiNhan = diaChiNguoiNhan;
        this.soDienThoaiNguoiNhan = soDienThoaiNguoiNhan;
        this.thanhTien = thanhTien;
        this.trangThai = trangThai;
        this.idKhuyenMai = idKhuyenMai;
        this.loaiGiam = loaiGiam;
        this.giaTriGiam = giaTriGiam;
        this.ngayTao = ngayTao;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTenKHNhan() {
        return tenKHNhan;
    }

    public void setTenKHNhan(String tenKHNhan) {
        this.tenKHNhan = tenKHNhan;
    }

    public String getDiaChiNguoiNhan() {
        return diaChiNguoiNhan;
    }

    public void setDiaChiNguoiNhan(String diaChiNguoiNhan) {
        this.diaChiNguoiNhan = diaChiNguoiNhan;
    }

    public String getSoDienThoaiNguoiNhan() {
        return soDienThoaiNguoiNhan;
    }

    public void setSoDienThoaiNguoiNhan(String soDienThoaiNguoiNhan) {
        this.soDienThoaiNguoiNhan = soDienThoaiNguoiNhan;
    }

    public int getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(int thanhTien) {
        this.thanhTien = thanhTien;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
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

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    
}
