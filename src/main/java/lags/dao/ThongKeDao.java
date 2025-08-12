package lags.dao;

import lags.entity.DoanhThuNgay;
import lags.entity.DoanhThuThang;
import lags.entity.TopItem;
import lags.entity.TopSanPham;
import lags.entity.TyTrongGiam;
import java.sql.Date;
import java.util.List;

public interface ThongKeDao {
    List<DoanhThuNgay> doanhThuTheoNgay(Date from, Date to);
    List<DoanhThuThang> doanhThuTheoThang(int soThangGanNhat);
    List<TopItem> topKhachHang(int limit);
    List<TopItem> topNhanVien(int limit);
    List<TopSanPham> topSanPham(int limit);
    TyTrongGiam tyTrongGiam();
}
