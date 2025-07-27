package lags.controller;

import lags.dao.KhachHangDao;
import lags.dao.impl.KhachHangDaoImpl;
import lags.entity.KhachHang;

import java.util.List;
import java.util.regex.Pattern;

public class KhachHangController {

    private final KhachHangDao dao = new KhachHangDaoImpl();

    private static final Pattern PHONE = Pattern.compile("^0\\d{9}$");
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+\\-]+@[\\w\\-]+\\.[A-Za-z]{2,}$");

    /**
     * Lấy tất cả hoặc tìm kiếm
     */
    public List<KhachHang> list(String keyword) {
        return (keyword == null || keyword.isBlank()) ? dao.findAll() : dao.search(keyword);
    }

    public KhachHang detail(String maKH) {
        KhachHang kh = dao.findById(maKH);
        if (kh == null) {
            throw new IllegalArgumentException("Không tìm thấy khách hàng: " + maKH);
        }
        return kh;
    }

    public void save(KhachHang kh) {
        validate(kh);
        KhachHang exist = dao.findById(kh.getMaKH());
        if (exist == null) {
            dao.create(kh);
        } else {
            dao.update(kh);
        }
    }

    public void delete(String maKH) {
        if (maKH == null || maKH.isBlank()) {
            throw new IllegalArgumentException("Mã khách hàng rỗng.");
        }
        if (!dao.canDelete(maKH)) {
            throw new IllegalStateException("Không thể xóa vì đã tồn tại hóa đơn.");
        }
        dao.deleteById(maKH);
    }

    private void validate(KhachHang kh) {
        if (kh.getMaKH() == null || kh.getMaKH().isBlank())
            throw new IllegalArgumentException("Mã KH bắt buộc.");
        if (kh.getTenKH() == null || kh.getTenKH().isBlank())
            throw new IllegalArgumentException("Tên KH bắt buộc.");
        if (kh.getSoDienThoai() != null && !kh.getSoDienThoai().isBlank()
                && !PHONE.matcher(kh.getSoDienThoai()).matches())
            throw new IllegalArgumentException("Số điện thoại không hợp lệ.");
        if (kh.getEmail() != null && !kh.getEmail().isBlank()
                && !EMAIL.matcher(kh.getEmail()).matches())
            throw new IllegalArgumentException("Email không hợp lệ.");
    }
}
