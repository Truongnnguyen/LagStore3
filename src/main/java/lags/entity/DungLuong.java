/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.entity;

/**
 *
 * @author icebear
 */
public class DungLuong {
    public String maDungLuong;
    public String dungLuong;

    public DungLuong() {
    }

    public DungLuong(String maDungLuong, String dungLuong) {
        this.maDungLuong = maDungLuong;
        this.dungLuong = dungLuong;
    }

    public String getMaDungLuong() {
        return maDungLuong;
    }

    public void setMaDungLuong(String maDungLuong) {
        this.maDungLuong = maDungLuong;
    }

    public String getDungLuong() {
        return dungLuong;
    }

    public void setDungLuong(String dungLuong) {
        this.dungLuong = dungLuong;
    }
    
    public String toString(){
        return this.dungLuong;
    }
    
     @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DungLuong)) return false;
        DungLuong other = (DungLuong) obj;
        return maDungLuong != null && maDungLuong.equals(other.maDungLuong);
    }

    @Override
    public int hashCode() {
        return maDungLuong != null ? maDungLuong.hashCode() : 0;
    }
    
}
