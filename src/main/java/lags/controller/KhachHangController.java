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

    /** Lấy danh sách tất cả khách hàng */
    public List<KhachHang> findAll() {
        return dao.findAll();
    }

    /** Lấy tất cả hoặc tìm kiếm theo keyword */
    public List<KhachHang> list(String keyword) {
        return (keyword == null || keyword.isBlank()) ? dao.findAll() : dao.search(keyword);
    }

    /** Lấy chi tiết 1 khách hàng theo mã */
    public KhachHang detail(String maKH) {
        KhachHang kh = dao.findById(maKH);
        if (kh == null) throw new IllegalArgumentException("Không tìm thấy khách hàng: " + maKH);
        return kh;
    }

    /** Thêm mới hoặc cập nhật (auto detect) */
    public Result save(KhachHang kh) {
        try {
            validate(kh);

            if (dao.existsByPhone(kh.getSoDienThoai())) {
                return new Result(false, "Số điện thoại đã tồn tại.");
            }
            if (dao.existsByEmail(kh.getEmail())) {
                return new Result(false, "Email đã tồn tại.");
            }

            KhachHang exist = dao.findById(kh.getMaKH());
            if (exist == null) {
                dao.create(kh);
                return new Result(true, "Thêm khách hàng thành công!");
            } else {
                dao.update(kh);
                return new Result(true, "Cập nhật khách hàng thành công!");
            }
        } catch (IllegalArgumentException e) {
            return new Result(false, "Lỗi: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "Đã xảy ra lỗi trong quá trình thêm hoặc sửa khách hàng.");
        }
    }

    /** Cập nhật khách hàng */
    public Result update(KhachHang kh) {
        try {
            validate(kh);

            if (dao.existsByPhoneExcept(kh.getSoDienThoai(), kh.getMaKH())) {
                return new Result(false, "Số điện thoại đã tồn tại.");
            }
            if (dao.existsByEmailExcept(kh.getEmail(), kh.getMaKH())) {
                return new Result(false, "Email đã tồn tại.");
            }

            dao.update(kh);
            return new Result(true, "Cập nhật thành công!");
        } catch (IllegalArgumentException e) {
            return new Result(false, "Lỗi: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "Đã xảy ra lỗi trong quá trình cập nhật khách hàng.");
        }
    }

    /** Thêm mới khách hàng */
    public Result saveNew(KhachHang kh) {
        try {
            validate(kh);

            if (dao.existsByPhone(kh.getSoDienThoai())) {
                return new Result(false, "Số điện thoại đã tồn tại.");
            }
            if (dao.existsByEmail(kh.getEmail())) {
                return new Result(false, "Email đã tồn tại.");
            }
            if (dao.findById(kh.getMaKH()) != null) {
                return new Result(false, "Mã khách hàng đã tồn tại.");
            }

            dao.create(kh);
            return new Result(true, "Thêm khách hàng thành công!");
        } catch (IllegalArgumentException e) {
            return new Result(false, "Lỗi: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "Đã xảy ra lỗi trong quá trình thêm khách hàng.");
        }
    }

    /** Xóa khách hàng */
    public boolean delete(String maKH) {
        try {
            if (maKH == null || maKH.isBlank()) return false;
            if (!dao.canDelete(maKH)) return false;
            dao.deleteById(maKH);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Kiểm tra dữ liệu đầu vào */
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
