package lags.entity;

public class DoanhThuThang {
    private String thang; 
    private long doanhThuNet;
    private long tongGiam;
    private int soDon;
    private double aov;

    // ✅ Constructor mặc định cần thiết cho DAO cũ
    public DoanhThuThang() {
    }

    // ✅ Constructor dùng cho biểu đồ/thống kê tháng
    public DoanhThuThang(String thang, long doanhThuNet) {
        this.thang = thang;
        this.doanhThuNet = doanhThuNet;
    }

    public String getThang() { return thang; }
    public void setThang(String thang) { this.thang = thang; }
    public long getDoanhThuNet() { return doanhThuNet; }
    public void setDoanhThuNet(long doanhThuNet) { this.doanhThuNet = doanhThuNet; }
    public long getTongGiam() { return tongGiam; }
    public void setTongGiam(long tongGiam) { this.tongGiam = tongGiam; }
    public int getSoDon() { return soDon; }
    public void setSoDon(int soDon) { this.soDon = soDon; }
    public double getAov() { return aov; }
    public void setAov(double aov) { this.aov = aov; }

    @Override
    public String toString() {
        return thang + " | net=" + doanhThuNet + " | giam=" + tongGiam +
               " | soDon=" + soDon + " | AOV=" + aov;
    }
}
