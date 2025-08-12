/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.LagStore;


import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author icebear
 */
public class GradientPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // gọi để vẽ các thành phần con

        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();

        // Gradient từ trái sang phải
        GradientPaint gp = new GradientPaint(0, 0, Color.decode("#353A5F"), width, height, Color.decode("#9EBAF3"));

        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
    }
}
