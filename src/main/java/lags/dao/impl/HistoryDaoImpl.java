package lags.dao.impl;


import lags.dao.HistoryDao;
import lags.entity.HistoryHoaDon;
import lags.util.XJdbc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryDaoImpl implements HistoryDao {

    @Override
    public List<HistoryHoaDon> findByMaKH(String maKH) {
        String sql = """
            SELECT hd.MaKH, kh.TenKH, hd.MaHD, hd.DiaChiNguoiNhan, hd.NgayTao,
                   hd.ThanhTien, hd.TrangThai
            FROM HoaDon hd
            JOIN KhachHang kh ON hd.MaKH = kh.MaKH
            WHERE hd.MaKH = ?
            ORDER BY hd.NgayTao DESC
            """;
        List<HistoryHoaDon> list = new ArrayList<>();
        try (Connection c = XJdbc.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HistoryHoaDon hd = new HistoryHoaDon();
                    hd.setMaKH(rs.getString("MaKH"));
                    hd.setTenKH(rs.getString("TenKH"));
                    hd.setMaHD(rs.getString("MaHD"));
                    hd.setDiaChi(rs.getString("DiaChiNguoiNhan"));
                    hd.setNgayTao(rs.getDate("NgayTao"));
                    hd.setTongTien(rs.getDouble("ThanhTien"));
                    hd.setTrangThai(rs.getInt("TrangThai"));
                    list.add(hd);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi truy vấn lịch sử hóa đơn: " + e.getMessage(), e);
        }
        return list;
    }
}
