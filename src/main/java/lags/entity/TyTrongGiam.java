/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.entity;


/**
 *
 * @author admin
 */
public class TyTrongGiam {
     private long tongGiam;
    private long doanhThuNet;
    private long doanhThuGross;
    private double tyLeGiam;

    public long getTongGiam() { return tongGiam; }
    public void setTongGiam(long tongGiam) { this.tongGiam = tongGiam; }
    public long getDoanhThuNet() { return doanhThuNet; }
    public void setDoanhThuNet(long doanhThuNet) { this.doanhThuNet = doanhThuNet; }
    public long getDoanhThuGross() { return doanhThuGross; }
    public void setDoanhThuGross(long doanhThuGross) { this.doanhThuGross = doanhThuGross; }
    public double getTyLeGiam() { return tyLeGiam; }
    public void setTyLeGiam(double tyLeGiam) { this.tyLeGiam = tyLeGiam; }

    @Override
    public String toString() {
        return "tongGiam=" + tongGiam + " | net=" + doanhThuNet +
               " | gross=" + doanhThuGross + " | tyLe=" + String.format("%.2f%%", tyLeGiam * 100);
    }
}
