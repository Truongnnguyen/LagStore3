/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.entity;

/**
 *
 * @author icebear
 */
public class CPU {
    public String maCPU;
    public String tenCPU;

    public CPU() {
    }

    public CPU(String maCPU, String tenCPU) {
        this.maCPU = maCPU;
        this.tenCPU = tenCPU;
    }

    public String getMaCPU() {
        return maCPU;
    }

    public void setMaCPU(String maCPU) {
        this.maCPU = maCPU;
    }

    public String getTenCPU() {
        return tenCPU;
    }

    public void setTenCPU(String tenCPU) {
        this.tenCPU = tenCPU;
    }
    
    public String toString(){
        return this.tenCPU;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CPU)) return false;
        CPU other = (CPU) obj;
        return maCPU != null && maCPU.equals(other.maCPU);
    }

    @Override
    public int hashCode() {
        return maCPU != null ? maCPU.hashCode() : 0;
    }
    
}
