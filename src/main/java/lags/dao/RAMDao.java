/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;


import java.util.List;
import lags.entity.RAM;
import lags.util.XJdbc;
import lags.util.XQuery;

/**
 *
 * @author icebear
 */
public class RAMDao {
    
    public List<RAM> findAll(){
        String sql= "select * from RAM";
        
        return XQuery.getBeanList(RAM.class, sql);
        
    }
    
    public void create(RAM entity){
        String sql = "INSERT INTO Ram (MaRAM, TenRAM, DungLuong) VALUES (?, ?, ?)";
        
        Object[] values = {
            entity.getMaRAM(),
            entity.getTenRAM(),
            entity.getDungLuong()
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void update(RAM entity){
        String sql = "UPDATE Ram SET TenRAM = ?, DungLuong = ? WHERE MaRAM = ?";
        
        Object[] values = {
            entity.getTenRAM(),
            entity.getDungLuong(),
            entity.getMaRAM()
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void deleteByID(String MaRam){
        String sql = "DELETE FROM Ram WHERE MaRAM = ?";
        
        XJdbc.executeUpdate(sql, MaRam);
    }
    
}