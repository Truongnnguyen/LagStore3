/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.entity;


public class TopSanPham {
    private String maSP;
    private String tenSP;
    private long doanhThu;
    private int soLuongBan;

    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }
    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }
    public long getDoanhThu() { return doanhThu; }
    public void setDoanhThu(long doanhThu) { this.doanhThu = doanhThu; }
    public int getSoLuongBan() { return soLuongBan; }
    public void setSoLuongBan(int soLuongBan) { this.soLuongBan = soLuongBan; }

    @Override
    public String toString() {
        return maSP + " - " + tenSP + " | doanhThu=" + doanhThu + " | qty=" + soLuongBan;
    }
}