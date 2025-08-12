package lags.entity;


import java.util.Date;

public class HistoryHoaDon {
    private String maKH;
    private String tenKH;
    private String maHD;
    private String diaChi;
    private Date ngayTao;
    private double tongTien;
    private int trangThai;

    public HistoryHoaDon() {}

    public HistoryHoaDon(String maKH, String tenKH, String maHD, 
                         String diaChi, Date ngayTao, double tongTien, int trangThai) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.maHD = maHD;
        this.diaChi = diaChi;
        this.ngayTao = ngayTao;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }
}
