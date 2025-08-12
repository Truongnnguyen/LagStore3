/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.LagStore;


import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 *
 * @author icebear
 */
public class GradientButton extends JButton{
    private boolean hover = false;
    private boolean pressed = false;

    public GradientButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(getFont().deriveFont(Font.BOLD));

        // Lắng nghe sự kiện chuột để xử lý hover và click
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Tùy theo trạng thái: normal, hover, pressed
        Color color1;
        Color color2;

        if (pressed) {
            color1 = Color.decode("#037d82");   // Đậm hơn khi nhấn
            color2 = Color.decode("#a3d9d4");
        } else if (hover) {
            color1 = Color.decode("#05b3b9");   // Nhạt hơn khi hover
            color2 = Color.decode("#e5f4f2");
        } else {
            color1 = Color.decode("#05999E");   // Mặc định
            color2 = Color.decode("#CBE7E3");
        }

        GradientPaint gp = new GradientPaint(
                0, 0, color1,
                0, getHeight(), color2
        );

        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Không vẽ border, hoặc thêm bo viền nếu muốn
    }
}
