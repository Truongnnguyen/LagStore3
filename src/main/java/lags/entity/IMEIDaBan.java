/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.entity;


/**
 *
 * @author icebear
 */
public class IMEIDaBan {
    public String idMaIMEI;
    public String idHDCT;
    public String soIMEI;

    public IMEIDaBan() {
    }

    public IMEIDaBan(String idMaIMEI, String idHDCT, String soIMEI) {
        this.idMaIMEI = idMaIMEI;
        this.idHDCT = idHDCT;
        this.soIMEI = soIMEI;
    }

    public String getIdMaIMEI() {
        return idMaIMEI;
    }

    public void setIdMaIMEI(String idMaIMEI) {
        this.idMaIMEI = idMaIMEI;
    }

    public String getIdHDCT() {
        return idHDCT;
    }

    public void setIdHDCT(String idHDCT) {
        this.idHDCT = idHDCT;
    }

    public String getSoIMEI() {
        return soIMEI;
    }

    public void setSoIMEI(String soIMEI) {
        this.soIMEI = soIMEI;
    }
    
    
    
}
