/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;

import java.util.List;
import lags.entity.IMEI;
import lags.util.XJdbc;
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
    
    public void create(IMEI entity){
        String sql = "INSERT INTO IMEI (MaIMEI, MaSPCT, SoIMEI) VALUES (?, ?, ?)";
        
        Object[] values = {
            entity.getMaIMEI(),
            entity.getMaSPCT(),
            entity.getSoIMEI()
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void update(IMEI entity){
        String sql = "UPDATE IMEI SET MaSPCT = ?, SoIMEI = ? WHERE MaIMEI = ?";
        
        Object[] values = {
            entity.getMaSPCT(),
            entity.getSoIMEI(),
            entity.getMaIMEI()
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void deleteByID(String MaIm){
        String sql = "DELETE FROM IMEI WHERE MaIMEI = ?";
        
        XJdbc.executeUpdate(sql, MaIm);
    }
    
}
