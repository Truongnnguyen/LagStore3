package lags.dao;




import lags.entity.KhachHang;
import java.util.List;

public interface KhachHangDao {

    KhachHang create(KhachHang entity);

    boolean update(KhachHang entity);

    boolean deleteById(String id);

    KhachHang findById(String id);

    List<KhachHang> findAll();

    List<KhachHang> search(String keyword);

    boolean canDelete(String maKH);

    boolean existsByPhoneExcept(String phone, String maKH);

    boolean existsByEmailExcept(String email, String maKH);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

// ✅ THÊM MỚI VÀO:
    String insertAndGetMaKH(KhachHang kh);
     KhachHang findByPhone(String sdt);   // <-- Thêm dòng này
     
     KhachHang findGuest(); // TenKH='Khách vãng lai' & SoDienThoai IS NULL (hoặc theo quy ước của bạn)
     
     
     
     
}
