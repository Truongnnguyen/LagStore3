/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.entity;

/**
 *
 * @author icebear
 */
public class RAM {
    public String maRAM;
    public String tenRAM;
    public String dungLuong;

    public RAM() {
    }

    public RAM(String maRAM, String tenRAM, String dungLuong) {
        this.maRAM = maRAM;
        this.tenRAM = tenRAM;
        this.dungLuong = dungLuong;
    }

    public String getMaRAM() {
        return maRAM;
    }

    public void setMaRAM(String maRAM) {
        this.maRAM = maRAM;
    }

    public String getTenRAM() {
        return tenRAM;
    }

    public void setTenRAM(String tenRAM) {
        this.tenRAM = tenRAM;
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
        if (!(obj instanceof RAM)) return false;
        RAM other = (RAM) obj;
        return maRAM != null && maRAM.equals(other.maRAM);
    }

    @Override
    public int hashCode() {
        return maRAM != null ? maRAM.hashCode() : 0;
    }
    
}
