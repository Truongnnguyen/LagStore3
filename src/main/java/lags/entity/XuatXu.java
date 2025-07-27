/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.entity;

import java.util.Objects;

/**
 *
 * @author icebear
 */
public class XuatXu {

    private String maXuatXu;
    private String xuatXu;

    public XuatXu() {
    }

    public XuatXu(String maXuatXu, String xuatXu) {
        this.maXuatXu = maXuatXu;
        this.xuatXu = xuatXu;
    }

    public String getMaXuatXu() {
        return maXuatXu;
    }

    public void setMaXuatXu(String maXuatXu) {
        this.maXuatXu = maXuatXu;
    }

    public String getXuatXu() {
        return xuatXu;
    }

    public void setXuatXu(String xuatXu) {
        this.xuatXu = xuatXu;
    }

    @Override
    public String toString() {
        return this.xuatXu;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof XuatXu)) return false;
        XuatXu other = (XuatXu) obj;
        return maXuatXu != null && maXuatXu.equals(other.maXuatXu);
    }

    @Override
    public int hashCode() {
        return maXuatXu != null ? maXuatXu.hashCode() : 0;
    }

}
