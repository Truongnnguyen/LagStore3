/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.entity;

import java.sql.Date;

public class DoanhThuNgay {
    private Date ngay;
    private long doanhThuNet;
    private long tongGiam;
    private long doanhThuGross;
    private int soDon;

    public Date getNgay() { return ngay; }
    public void setNgay(Date ngay) { this.ngay = ngay; }
    public long getDoanhThuNet() { return doanhThuNet; }
    public void setDoanhThuNet(long doanhThuNet) { this.doanhThuNet = doanhThuNet; }
    public long getTongGiam() { return tongGiam; }
    public void setTongGiam(long tongGiam) { this.tongGiam = tongGiam; }
    public long getDoanhThuGross() { return doanhThuGross; }
    public void setDoanhThuGross(long doanhThuGross) { this.doanhThuGross = doanhThuGross; }
    public int getSoDon() { return soDon; }
    public void setSoDon(int soDon) { this.soDon = soDon; }

    @Override
    public String toString() {
        return ngay + " | net=" + doanhThuNet + " | giam=" + tongGiam +
               " | gross=" + doanhThuGross + " | soDon=" + soDon;
    }
}