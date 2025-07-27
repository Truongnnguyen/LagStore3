/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;

import java.util.List;
import lags.entity.IMEI;
import lags.util.XQuery;

/**
 *
 * @author icebear
 */
public class IMEIDao {
    public List<IMEI> findAll(){
        String sql = "SELECT * FROM IMEI";
        return XQuery.getBeanList(IMEI.class, sql);
    }
}
