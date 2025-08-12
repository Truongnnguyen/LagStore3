package lags.entity;

public class KhachHang {
    private String maKH;
    private String tenKH;
    private String diaChi;
    private String soDienThoai;
    private String email;
    private int trangThai; // 0 = Đang hoạt động, 1 = Ngừng hoạt động, 2 = Khóa

    public KhachHang() {
    }

    public KhachHang(String maKH, String tenKH, String diaChi, String soDienThoai, String email, int trangThai) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.trangThai = trangThai;
    }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getTenKH() { return tenKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }

    public String getTrangThaiText() {
        return switch (trangThai) {
            case 0 -> "Đang hoạt động";
            case 1 -> "Ngừng hoạt động";
            default -> "Không xác định";
        };
    }
}
