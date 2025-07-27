/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.entity;

/**
 *
 * @author icebear
 */
public class SanPham {
    public String maSP;
    public String MaXuatXu;
    public String tenSP;

    public SanPham() {
    }

    public SanPham(String maSP, String MaXuatXu, String tenSP) {
        this.maSP = maSP;
        this.MaXuatXu = MaXuatXu;
        this.tenSP = tenSP;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getMaXuatXu() {
        return MaXuatXu;
    }

    public void setMaXuatXu(String MaXuatXu) {
        this.MaXuatXu = MaXuatXu;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    
    
    
}
