/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;

import java.util.List;
import lags.entity.GPU;
import lags.util.XJdbc;
import lags.util.XQuery;

/**
 *
 * @author icebear
 */
public class GPUDao {
    
    public List<GPU> findAll(){
        String sql ="select * from GPU";
                
        return XQuery.getBeanList(GPU.class, sql);
        
    }
    
    public void create(GPU entity){
        String sql = "INSERT INTO GPU (MaGPU, TenGPU) VALUES (?, ?)";
        
        Object[] values = {
            entity.getMaGPU(),
            entity.getTenGPU()
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void update(GPU entity){
        String sql = "UPDATE GPU SET TenGPU = ? WHERE MaGPU=?";
        
        Object[] values = {
            entity.getTenGPU(),
            entity.getMaGPU()
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void deleteByID(String MaGPU){
        String sql = "DELETE FROM GPU WHERE MaGPU=?";
        
        XJdbc.executeUpdate(sql, MaGPU);
    }
    
}
