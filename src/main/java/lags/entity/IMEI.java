/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.entity;

/**
 *
 * @author icebear
 */
public class IMEI {
    public String maIMEI;
    public String maSPCT;
    public String soIMEI;
    public int trangThai;

    public IMEI() {
    }

    public IMEI(String maIMEI, String maSPCT, String soIMEI, int trangThai) {
        this.maIMEI = maIMEI;
        this.maSPCT = maSPCT;
        this.soIMEI = soIMEI;
        this.trangThai = trangThai;
    }

    public String getMaIMEI() {
        return maIMEI;
    }

    public void setMaIMEI(String maIMEI) {
        this.maIMEI = maIMEI;
    }

    public String getMaSPCT() {
        return maSPCT;
    }

    public void setMaSPCT(String maSPCT) {
        this.maSPCT = maSPCT;
    }

    public String getSoIMEI() {
        return soIMEI;
    }

    public void setSoIMEI(String soIMEI) {
        this.soIMEI = soIMEI;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }
    
    
}
