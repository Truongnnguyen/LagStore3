package lags.dao;

import lags.entity.KhachHang;
import java.util.List;

public interface KhachHangDao extends CrudDao<KhachHang, String> {
    List<KhachHang> search(String keyword);
    boolean canDelete(String maKH);
}
