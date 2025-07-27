/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.entity;

/**
 *
 * @author icebear
 */
public class SanPhamChiTiet {
    public String maSPCT;
    public String maSP;
    public String maCPU;
    public String maRAM;
    public String maDungLuong;
    public String maGPU;
    public int gia;
    public int soLuong;

    public SanPhamChiTiet() {
    }

    public SanPhamChiTiet(String maSPCT, String maSP, String maCPU, String maRAM, String maDungLuong, String maGPU, int gia, int soLuong) {
        this.maSPCT = maSPCT;
        this.maSP = maSP;
        this.maCPU = maCPU;
        this.maRAM = maRAM;
        this.maDungLuong = maDungLuong;
        this.maGPU = maGPU;
        this.gia = gia;
        this.soLuong = soLuong;
    }

    public String getMaSPCT() {
        return maSPCT;
    }

    public void setMaSPCT(String maSPCT) {
        this.maSPCT = maSPCT;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getMaCPU() {
        return maCPU;
    }

    public void setMaCPU(String maCPU) {
        this.maCPU = maCPU;
    }

    public String getMaRAM() {
        return maRAM;
    }

    public void setMaRAM(String maRAM) {
        this.maRAM = maRAM;
    }

    public String getMaDungLuong() {
        return maDungLuong;
    }

    public void setMaDungLuong(String maDungLuong) {
        this.maDungLuong = maDungLuong;
    }

    public String getMaGPU() {
        return maGPU;
    }

    public void setMaGPU(String maGPU) {
        this.maGPU = maGPU;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    
    
    
}
