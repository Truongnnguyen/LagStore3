/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.entity;

public class TopItem {
    private String id;
    private String ten;
    private long doanhThu;
    private int soDon;
    private long soLuong; 

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public long getDoanhThu() { return doanhThu; }
    public void setDoanhThu(long doanhThu) { this.doanhThu = doanhThu; }
    public int getSoDon() { return soDon; }
    public void setSoDon(int soDon) { this.soDon = soDon; }
    public long getSoLuong() { return soLuong; }
    public void setSoLuong(long soLuong) { this.soLuong = soLuong; }

    @Override
    public String toString() {
        return id + " - " + ten + " | doanhThu=" + doanhThu +
               " | soDon=" + soDon + (soLuong > 0 ? " | soLuong=" + soLuong : "");
    }
}