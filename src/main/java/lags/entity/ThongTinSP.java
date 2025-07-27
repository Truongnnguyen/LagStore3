/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.entity;

/**
 *
 * @author icebear
 */
public class ThongTinSP {

    private SanPhamChiTiet spct;
    private SanPham sanPham;
    private CPU cpu;
    private RAM ram;
    private DungLuong dungLuong;
    private GPU gpu;
    private String soIMEI; // hoặc bạn tạo class IMEI nếu cần chi tiết hơn
    private XuatXu Xx;

    // === Getter & Setter ===
    public SanPhamChiTiet getSpct() {
        return spct;
    }

    public void setSpct(SanPhamChiTiet spct) {
        this.spct = spct;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public CPU getCpu() {
        return cpu;
    }

    public void setCpu(CPU cpu) {
        this.cpu = cpu;
    }

    public RAM getRam() {
        return ram;
    }

    public void setRam(RAM ram) {
        this.ram = ram;
    }

    public DungLuong getDungLuong() {
        return dungLuong;
    }

    public void setDungLuong(DungLuong dungLuong) {
        this.dungLuong = dungLuong;
    }

    public GPU getGpu() {
        return gpu;
    }

    public void setGpu(GPU gpu) {
        this.gpu = gpu;
    }

    public String getSoIMEI() {
        return soIMEI;
    }

    public void setSoIMEI(String soIMEI) {
        this.soIMEI = soIMEI;
    }

    public XuatXu getXuatXu() {
        return Xx;
    }

    public void setXuatXu(XuatXu xx) {
        this.Xx = xx;
    }

}
