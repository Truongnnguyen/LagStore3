/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package lags.view;
import lags.dao.ThongKeDao;
import lags.dao.impl.ThongKeDaoImpl;
import lags.entity.DoanhThuNgay;
import lags.entity.TopSanPham;
import lags.entity.TopItem;
import java.sql.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author admin
 */

public class ThongKeFrom extends javax.swing.JPanel {

    /**
     * Creates new form ThongKeFfrom
     */
    private final ThongKeDao thongKeDao = new ThongKeDaoImpl();
    public ThongKeFrom() {
        initComponents();
         initCustom();  
    }
    
private void initCustom() {

    btnLoc.addActionListener(e -> filterManual());
    btn7 .addActionListener(e -> quickRange(7));
    btn30.addActionListener(e -> quickRange(30));

    quickRange(7);
}

private void quickRange(int days) {
    var to   = java.time.LocalDate.now();
    var from = to.minusDays(days);
    txtTuNgay .setText(from.toString());
    txtDenNgay.setText(to  .toString());
    loadAllWithRange(from, to);
}

private void filterManual() {
    try {
        var from = java.time.LocalDate.parse(txtTuNgay.getText().trim());
        var to   = java.time.LocalDate.parse(txtDenNgay.getText().trim());
        loadAllWithRange(from, to);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
            "Ngày không hợp lệ (yyyy-MM-dd).", "Lỗi",
            JOptionPane.ERROR_MESSAGE);
    }
}

private void loadAllWithRange(java.time.LocalDate from, java.time.LocalDate to) {

    txtTuNgay .setText(from.toString());
    txtDenNgay.setText(to  .toString());


    var mNgay = (javax.swing.table.DefaultTableModel) tblNgay.getModel();
    mNgay.setRowCount(0);
    List<DoanhThuNgay> lst = thongKeDao.doanhThuTheoNgay(Date.valueOf(from), Date.valueOf(to));
    long sumNet=0, sumDisc=0, sumGross=0;
    int  cnt=0;
    for (DoanhThuNgay d : lst) {
        mNgay.addRow(new Object[]{
            d.getNgay(),
            d.getDoanhThuNet(),
            d.getTongGiam(),
            d.getDoanhThuGross(),
            d.getSoDon()
        });
        sumNet   += d.getDoanhThuNet();
        sumDisc  += d.getTongGiam();
        sumGross += d.getDoanhThuGross();
        cnt      += d.getSoDon();
    }
    double aov  = cnt==0?0:((double)sumNet)/cnt;
    double rate = sumGross==0?0:((double)sumDisc)/sumGross;


    lblNet       .setText("Doanh thu (Net): " + fmt(sumNet));
    lblTong      .setText("Tổng: "           + fmt(sumGross));
    lblGiamgia   .setText("Giảm giá: "       + fmt(sumDisc));
    tbltylegiam  .setText(String.format("Tỷ lệ giảm: %.2f%%", rate*100));
    tblSodon     .setText("Số đơn: "         + cnt);
    tbltrungbinh .setText("Trung bình: "     + fmt(Math.round(aov)));


    var mSP = (javax.swing.table.DefaultTableModel) tblTopSP.getModel();
    mSP.setRowCount(0);
    for (var sp : thongKeDao.topSanPham(5)) {
        mSP.addRow(new Object[]{ sp.getMaSP(), sp.getTenSP(), sp.getDoanhThu(), sp.getSoLuongBan() });
    }


    var mKH = (javax.swing.table.DefaultTableModel) tblTopKH.getModel();
    mKH.setRowCount(0);
    for (var t : thongKeDao.topKhachHang(5)) {
        mKH.addRow(new Object[]{ t.getId(), t.getTen(), t.getDoanhThu(), t.getSoDon() });
    }


    var mNV = (javax.swing.table.DefaultTableModel) tblTopNV.getModel();
    mNV.setRowCount(0);
    for (var t : thongKeDao.topNhanVien(5)) {
        mNV.addRow(new Object[]{ t.getId(), t.getTen(), t.getDoanhThu(), t.getSoDon() });
    }
}

private String fmt(long v) {
    return String.format("%,d", v);
}


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtTuNgay = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDenNgay = new javax.swing.JTextField();
        btnLoc = new javax.swing.JButton();
        btn7 = new javax.swing.JButton();
        btn30 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        lblNet = new javax.swing.JLabel();
        lblTong = new javax.swing.JLabel();
        lblGiamgia = new javax.swing.JLabel();
        tbltylegiam = new javax.swing.JLabel();
        tblSodon = new javax.swing.JLabel();
        tbltrungbinh = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNgay = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTopSP = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTopKH = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblTopNV = new javax.swing.JTable();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Khoảng ngày (yyyy-MM-dd)", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Từ: ");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Đến:");

        btnLoc.setText("Lọc");
        btnLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocActionPerformed(evt);
            }
        });

        btn7.setText("7 ngày");
        btn7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn7ActionPerformed(evt);
            }
        });

        btn30.setText("30 ngày");
        btn30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn30ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTuNgay, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDenNgay, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLoc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn30)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtTuNgay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtDenNgay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn7)
                    .addComponent(btn30))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "KPI", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        lblNet.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblNet.setText("Doanh thu (Net): 0");

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbltylegiam)
                    .addComponent(tblSodon)
                    .addComponent(tbltrungbinh))
                .addContainerGap(24, Short.MAX_VALUE))
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

        jTabbedPane2.addTab("Top sản phẩm", jScrollPane2);

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocActionPerformed
        // TODO add your handling code here:
            filterManual();
    }//GEN-LAST:event_btnLocActionPerformed

    private void btn7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn7ActionPerformed
        // TODO add your handling code here:
            quickRange(7);
    }//GEN-LAST:event_btn7ActionPerformed

    private void btn30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn30ActionPerformed
        // TODO add your handling code here:
            quickRange(30);
    }//GEN-LAST:event_btn30ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn30;
    private javax.swing.JButton btn7;
    private javax.swing.JButton btnLoc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel lblGiamgia;
    private javax.swing.JLabel lblNet;
    private javax.swing.JLabel lblTong;
    private javax.swing.JTable tblNgay;
    private javax.swing.JLabel tblSodon;
    private javax.swing.JTable tblTopKH;
    private javax.swing.JTable tblTopNV;
    private javax.swing.JTable tblTopSP;
    private javax.swing.JLabel tbltrungbinh;
    private javax.swing.JLabel tbltylegiam;
    private javax.swing.JTextField txtDenNgay;
    private javax.swing.JTextField txtTuNgay;
    // End of variables declaration//GEN-END:variables
}
