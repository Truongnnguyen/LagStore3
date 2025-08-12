package lags.dao;


import java.util.List;
import lags.entity.HistoryHoaDon;

public interface HistoryDao {
    List<HistoryHoaDon> findByMaKH(String maKH);
}
