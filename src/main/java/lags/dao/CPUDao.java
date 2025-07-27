/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;

import java.util.List;
import lags.entity.CPU;
import lags.util.XJdbc;
import lags.util.XQuery;

/**
 *
 * @author icebear
 */
public class CPUDao {
    public List<CPU> findAll(){
        String sql = "select * from CPU";
        
        return XQuery.getBeanList(CPU.class, sql);
    }
    
    public void create(CPU entity){
        String sql = "INSERT INTO CPU (MaCPU, TenCPU) VALUES (?, ?)";
        
        Object[] values = {
            entity.getMaCPU(),
            entity.getTenCPU()
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void update(CPU entity){
        String sql = "UPDATE CPU SET TenCPU = ? WHERE MaCPU=?";
        
        Object[] values = {
            entity.getTenCPU(),
            entity.getMaCPU()
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void deleteByID(String MaCpu){
        String sql = "DELETE FROM CPU WHERE MaCPU=?";
        
        XJdbc.executeUpdate(sql, MaCpu);
    }
    
}
