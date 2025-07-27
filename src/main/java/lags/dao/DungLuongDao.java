/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;

import java.util.List;
import lags.entity.DungLuong;
import lags.util.XJdbc;
import lags.util.XQuery;

/**
 *
 * @author icebear
 */
public class DungLuongDao {
    
    public List<DungLuong> findAll(){
        String sql = "select * from DungLuong";
        
        return XQuery.getBeanList(DungLuong.class, sql);
    }
    
    public void create(DungLuong entity){
        String sql = "INSERT INTO DungLuong (MaDungLuong, DungLuong) VALUES (?, ?)";
        
        Object[] values = {
            entity.getMaDungLuong(),
            entity.getDungLuong()
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void update(DungLuong entity){
        String sql = "UPDATE DungLuong SET DungLuong = ? WHERE MaDungLuong=?";
        
        Object[] values = {
            entity.getDungLuong(),
            entity.getMaDungLuong()
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void deleteByID(String MaCpu){
        String sql = "DELETE FROM DungLuong WHERE MaDungLuong=?";
        
        XJdbc.executeUpdate(sql, MaCpu);
    }
    
}
