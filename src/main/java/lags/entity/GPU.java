/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.entity;

/**
 *
 * @author icebear
 */
public class GPU {
    public String maGPU;
    public String tenGPU;

    public GPU() {
    }

    public GPU(String maGPU, String tenGPU) {
        this.maGPU = maGPU;
        this.tenGPU = tenGPU;
    }

    public String getMaGPU() {
        return maGPU;
    }

    public void setMaGPU(String maGPU) {
        this.maGPU = maGPU;
    }

    public String getTenGPU() {
        return tenGPU;
    }

    public void setTenGPU(String tenGPU) {
        this.tenGPU = tenGPU;
    }
    
    public String toString(){
        return this.tenGPU;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GPU)) return false;
        GPU other = (GPU) obj;
        return maGPU != null && maGPU.equals(other.maGPU);
    }

    @Override
    public int hashCode() {
        return maGPU != null ? maGPU.hashCode() : 0;
    }
    
}
