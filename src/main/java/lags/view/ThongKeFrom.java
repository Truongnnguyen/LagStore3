/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package lags.view;


/**
 *
 * @author admin
 */

import lags.dao.ThongKeDao;
import lags.dao.impl.ThongKeDaoImpl;
import lags.entity.*;
import lags.controller.MyChartPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.SpinnerNumberModel;
import java.awt.event.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
// + các import của lags.entity và lags.dao bạn đã dùng

public class ThongKeFrom extends javax.swing.JPanel {
  private final ThongKeDao dao = new ThongKeDaoImpl();
    private final List<DoanhThuThang> monthData = new ArrayList<>();
    // Component do NetBeans Design tạo, đã đổi tên cho khớp:
    
    public ThongKeFrom() {
        initComponents();
           initCustom();
    }
    // gọi này ngay sau initComponents();
  private void initCustom() {
        initDailyPanel();
        initTopPanel();
        initChartPanel();
    }

    private void initDailyPanel() {
        LocalDate now = LocalDate.now();
        DCTungay.setDate(Date.valueOf(now.withDayOfMonth(1)));
        DCDenngay.setDate(Date.valueOf(now));
        Nam.setModel(new SpinnerNumberModel(now.getYear(), 2000, now.getYear(), 1));

        fillTableDoanhThuNgay();
        fillStatistics();

        DCTungay.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                fillTableDoanhThuNgay();
                fillStatistics();
            }
        });

        DCDenngay.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                fillTableDoanhThuNgay();
                fillStatistics();
            }
        });

        Nam.addChangeListener(e -> {
            fillTableDoanhThuNgay();
            fillStatistics();
        });
    }

    private void initTopPanel() {
        loadTopSanPham();
        loadTopKhachHang();
        loadTopNhanVien();
    }

    private void initChartPanel() {
        Nam.addChangeListener(e -> loadYearChart());
        Thang.addActionListener(e -> loadMonthChart());

        fillComboThang();
        loadYearChart();
    }

    private void fillTableDoanhThuNgay() {
        DefaultTableModel m = (DefaultTableModel) tblNgay.getModel();
        m.setRowCount(0);
        try {
            Date from = new Date(DCTungay.getDate().getTime());
            Date to = new Date(DCDenngay.getDate().getTime());
            dao.doanhThuTheoNgay(from, to).forEach(d ->
                m.addRow(new Object[]{
                    d.getNgay(), d.getDoanhThuNet(), d.getTongGiam(),
                    d.getDoanhThuGross(), d.getSoDon()
                })
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thống kê doanh thu theo ngày", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillStatistics() {
        try {
            TyTrongGiam ty = dao.tyTrongGiam();
            lblTong.setText("Tổng: " + ty.getDoanhThuGross());
            lblNet.setText("Doanh thu: " + ty.getDoanhThuNet());
            lblGiamgia.setText("Giảm giá: " + ty.getTongGiam());
            tbltylegiam.setText("Tỷ lệ giảm: " + String.format("%.2f%%", ty.getTyLeGiam() * 100));

            int totalDon = 0; long totalNet = 0;
            for (int i = 0; i < tblNgay.getRowCount(); i++) {
                totalDon += Integer.parseInt(tblNgay.getValueAt(i, 4).toString());
                totalNet += Long.parseLong(tblNgay.getValueAt(i, 1).toString());
            }
            tblSodon.setText("Số đơn: " + totalDon);
            tbltrungbinh.setText("Trung bình: " + (totalDon == 0 ? 0 : totalNet / totalDon));
        } catch (Exception ignored) {}
    }

    private void loadTopSanPham() {
        DefaultTableModel m = (DefaultTableModel) tblTopSP.getModel();
        m.setRowCount(0);
        dao.topSanPham(10).forEach(sp ->
            m.addRow(new Object[]{sp.getMaSP(), sp.getTenSP(), sp.getDoanhThu(), sp.getSoLuongBan()})
        );
    }

    private void loadTopKhachHang() {
        DefaultTableModel m = (DefaultTableModel) tblTopKH.getModel();
        m.setRowCount(0);
        dao.topKhachHang(10).forEach(kh ->
            m.addRow(new Object[]{kh.getId(), kh.getTen(), kh.getDoanhThu(), kh.getSoDon()})
        );
    }

    private void loadTopNhanVien() {
        DefaultTableModel m = (DefaultTableModel) tblTopNV.getModel();
        m.setRowCount(0);
        dao.topNhanVien(10).forEach(nv ->
            m.addRow(new Object[]{nv.getId(), nv.getTen(), nv.getDoanhThu(), nv.getSoDon()})
        );
    }

   private void fillComboThang() {
    DefaultComboBoxModel<String> mdl = new DefaultComboBoxModel<>();
    monthData.clear();
    int year = (Integer) Nam.getValue();

    List<DoanhThuThang> allData = dao.doanhThuTheoThang(12);

    for (int m = 1; m <= 12; m++) {
        String ym = year + "-" + String.format("%02d", m);
        DoanhThuThang found = allData.stream()
            .filter(dt -> dt.getThang().equals(ym))
            .findFirst().orElse(new DoanhThuThang(ym, 0)); // giả định có constructor phù hợp

        monthData.add(found);
        mdl.addElement(ym);
    }

    Thang.setModel(mdl);
}

    private void loadYearChart() {
        int year = (Integer) Nam.getValue();
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (int m = 1; m <= 12; m++) {
            String key = year + "-" + String.format("%02d", m);
            long v = monthData.stream()
                .filter(dt -> dt.getThang().equals(key))
                .map(DoanhThuThang::getDoanhThuNet)
                .findFirst().orElse(0L);
            ds.addValue(v, "Doanh thu", "Th" + m);
        }
        JFreeChart c = ChartFactory.createBarChart(
            "Doanh thu năm " + year, "Tháng", "VNĐ", ds);
        Bieudo.setChart(c);
        Bieudo.revalidate();
        Bieudo.repaint();
    }

    private void loadMonthChart() {
        int idx = Thang.getSelectedIndex();
        if (idx < 0) return;
        DoanhThuThang dt = monthData.get(idx);
        String[] p = dt.getThang().split("-");
        LocalDate f = LocalDate.of(Integer.parseInt(p[0]), Integer.parseInt(p[1]), 1);
        LocalDate t = f.withDayOfMonth(f.lengthOfMonth());
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        dao.doanhThuTheoNgay(Date.valueOf(f), Date.valueOf(t))
           .forEach(d -> ds.addValue(d.getDoanhThuNet(), "Doanh thu", d.getNgay().toString()));
        JFreeChart c = ChartFactory.createBarChart(
            "Doanh thu " + dt.getThang(), "Ngày", "VNĐ", ds);
        Bieudo.setChart(c);
        Bieudo.revalidate();
        Bieudo.repaint();
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane1 = new java.awt.ScrollPane();
        choice2 = new java.awt.Choice();
        scrollbar1 = new java.awt.Scrollbar();
        myChartPanel1 = new lags.controller.MyChartPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jPanel2 = new javax.swing.JPanel();
        lblNet = new javax.swing.JLabel();
        lblTong = new javax.swing.JLabel();
        lblGiamgia = new javax.swing.JLabel();
        tbltylegiam = new javax.swing.JLabel();
        tblSodon = new javax.swing.JLabel();
        tbltrungbinh = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        DCTungay = new com.toedter.calendar.JDateChooser();
        DCDenngay = new com.toedter.calendar.JDateChooser();
        Bieudo = new lags.controller.MyChartPanel();
        jPanel5 = new javax.swing.JPanel();
        Thang = new javax.swing.JComboBox<>();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNgay = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTopSP = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTopKH = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblTopNV = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        Nam = new javax.swing.JSpinner();

        javax.swing.GroupLayout myChartPanel1Layout = new javax.swing.GroupLayout(myChartPanel1);
        myChartPanel1.setLayout(myChartPanel1Layout);
        myChartPanel1Layout.setHorizontalGroup(
            myChartPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 646, Short.MAX_VALUE)
        );
        myChartPanel1Layout.setVerticalGroup(
            myChartPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 570, Short.MAX_VALUE)
        );

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "KPI", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        lblNet.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblNet.setText("Doanh thu: 0");

        lblTong.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTong.setText("Tổng: 0");

        lblGiamgia.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGiamgia.setText("Giảm giá: 0");

        tbltylegiam.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tbltylegiam.setText("Tỷ lệ giảm: 0");

        tblSodon.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tblSodon.setText("Số đơn: 0");

        tbltrungbinh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tbltrungbinh.setText("Trung bình: 0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblNet, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblTong, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblGiamgia, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tbltylegiam, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(tblSodon, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tbltrungbinh, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNet)
                    .addComponent(lblTong)
                    .addComponent(lblGiamgia))
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbltylegiam)
                    .addComponent(tblSodon)
                    .addComponent(tbltrungbinh))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Khoảng ngày (yyyy-MM-dd)", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Từ: ");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Đến:");

        javax.swing.GroupLayout BieudoLayout = new javax.swing.GroupLayout(Bieudo);
        Bieudo.setLayout(BieudoLayout);
        BieudoLayout.setHorizontalGroup(
            BieudoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 497, Short.MAX_VALUE)
        );
        BieudoLayout.setVerticalGroup(
            BieudoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 406, Short.MAX_VALUE)
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Lọc tháng", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        Thang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Thang, 0, 162, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(Thang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        tblNgay.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Ngày", "Doanh thu", "Giảm", "Tổng", "Số đơn"
            }
        ));
        jScrollPane1.setViewportView(tblNgay);

        jTabbedPane2.addTab("Theo ngày", jScrollPane1);

        tblTopSP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã", "Tên", "Doanh thu", "SL"
            }
        ));
        jScrollPane2.setViewportView(tblTopSP);

        jTabbedPane2.addTab("Top sản phẩm bán chạy", jScrollPane2);

        tblTopKH.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã KH", "Tên KH", "Doanh thu", "Số đơn"
            }
        ));
        jScrollPane3.setViewportView(tblTopKH);

        jTabbedPane2.addTab("Top khách", jScrollPane3);

        tblTopNV.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã NV", "Tên NV", "Doanh thu", "Số đơn"
            }
        ));
        jScrollPane4.setViewportView(tblTopNV);

        jTabbedPane2.addTab("Top nhân viên", jScrollPane4);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lọc năm", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(Nam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(Nam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 4, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(Bieudo, javax.swing.GroupLayout.PREFERRED_SIZE, 497, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(DCTungay, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(DCDenngay, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 9, Short.MAX_VALUE))
                    .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(DCTungay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(DCDenngay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(26, 26, 26)
                        .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Bieudo, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 26, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private lags.controller.MyChartPanel Bieudo;
    private com.toedter.calendar.JDateChooser DCDenngay;
    private com.toedter.calendar.JDateChooser DCTungay;
    private javax.swing.JSpinner Nam;
    private javax.swing.JComboBox<String> Thang;
    private java.awt.Choice choice2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel lblGiamgia;
    private javax.swing.JLabel lblNet;
    private javax.swing.JLabel lblTong;
    private lags.controller.MyChartPanel myChartPanel1;
    private java.awt.ScrollPane scrollPane1;
    private java.awt.Scrollbar scrollbar1;
    private javax.swing.JTable tblNgay;
    private javax.swing.JLabel tblSodon;
    private javax.swing.JTable tblTopKH;
    private javax.swing.JTable tblTopNV;
    private javax.swing.JTable tblTopSP;
    private javax.swing.JLabel tbltrungbinh;
    private javax.swing.JLabel tbltylegiam;
    // End of variables declaration//GEN-END:variables
 
}
