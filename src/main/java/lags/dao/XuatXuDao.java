/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.dao;

import java.util.List;
import lags.entity.XuatXu;
import lags.util.XJdbc;
import lags.util.XQuery;

/**
 *
 * @author icebear
 */
public class XuatXuDao {
    
    public List<XuatXu> findAll(){
        String sql = "select * from XuatXu";
        
        return XQuery.getBeanList(XuatXu.class, sql);
    }
    
    public void create(XuatXu entity){
        String sql = "INSERT INTO XuatXu (MaXuatXu, XuatXu) VALUES (?, ?)";
        
        Object[] values = {
            entity.getMaXuatXu(),
            entity.getXuatXu()
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void update(XuatXu entity){
        String sql = "UPDATE XuatXu SET XuatXu = ? WHERE MaXuatXu=?";
        
        Object[] values = {
            entity.getXuatXu(),
            entity.getMaXuatXu()
        };
        XJdbc.executeUpdate(sql, values);
    }
    
    public void deleteByID(String MaXuatXu){
        String sql = "DELETE FROM XuatXu WHERE MaXuatXu=?";
        
        XJdbc.executeUpdate(sql, MaXuatXu);
    }
    
}
