/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package lags.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import lags.dao.HoaDonChiTietDao;
import lags.dao.HoaDonDao;
import lags.dao.IMEIDao;
import lags.dao.KhachHangDao;
import lags.dao.SanPhamChiTietDao;
import lags.dao.SanPhamDao;
import lags.entity.HoaDon;
import lags.entity.IMEI;
import lags.entity.KhuyenMai;
import lags.entity.NhanVien;
import lags.entity.SanPham;
import lags.entity.ThongTinSP;
import lags.util.XJdbc;
import lags.dao.KhuyenMaiDao;
import lags.dao.impl.KhachHangDaoImpl;
import lags.entity.HoaDonChiTiet;
import lags.entity.KhachHang;
import lags.entity.KhuyenMai;

/**
 *
 * @author PC
 */
public class BanHang extends javax.swing.JPanel {

    /**
     * Creates new form BanHang
     */
    private KhuyenMai kmDangApDung = null;

    List<SanPham> lstSanPham = List.of();
    List<ThongTinSP> lstThongtin = List.of();
    SanPhamDao spDAO = new SanPhamDao();
    private List<Object[]> dataSanPham = new ArrayList<>();
    private String maHDDangTao = null;
    HoaDonDao dao = new HoaDonDao(); // 
    private NhanVien nhanVienDangNhap;
    SanPhamChiTietDao spctdao = new SanPhamChiTietDao();
    private KhuyenMaiDao kmDao = new KhuyenMaiDao();

    private double apDungKhuyenMaiTuDong(double tongTien, int tongSoLuongSP) {
        List<KhuyenMai> dsKM = kmDao.getKhuyenMaiConHieuLuc();
        double tienGiamMax = 0;
        kmDangApDung = null;

        for (KhuyenMai km : dsKM) {
            boolean hopLe = false;

            // điều kiện áp dụng (có thể mở rộng thêm ở đây)
            if (km.getIdKhuyenMai().equals("KM1") && tongTien >= 20000000) {
                hopLe = true;
            } else if (km.getIdKhuyenMai().equals("KM2") && tongSoLuongSP >= 2) {
                hopLe = true;
            }

            if (hopLe) {
                double tienGiam = 0;
                if (km.getLoaiGiam() == 1) { // giảm %
                    tienGiam = tongTien * km.getGiaTriGiam() / 100.0;
                    if (tienGiam > km.getGiamToiDa()) {
                        tienGiam = km.getGiamToiDa();
                    }
                } else if (km.getLoaiGiam() == 2) { // giảm trực tiếp
                    tienGiam = km.getGiaTriGiam();
                }

                if (tienGiam > tienGiamMax) {
                    tienGiamMax = tienGiam;
                    kmDangApDung = km;
                }
            }
        }

        return tienGiamMax;
    }

//    List<KhuyenMai> dsKM = new ArrayList<>();
    public BanHang(NhanVien nv) {
        initComponents();
        txtTongTien.setEditable(false);
        this.fillToTable();
        this.fillComBoBox();
        this.nhanVienDangNhap = nv;

        tblHoaDonCho.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblHoaDonCho.getSelectedRow();
                if (row >= 0) {
                    String maHD = tblHoaDonCho.getValueAt(row, 0).toString();
                    maHDDangTao = maHD;

                    // Nếu bạn có chức năng load lại giỏ hàng theo hóa đơn thì gọi ở đây
                    // loadGioHangTheoMaHD(maHD);
                }
            }
        });
        ((DefaultTableModel) tblGioHang.getModel()).setRowCount(0);
        ((DefaultTableModel) tblGioHang.getModel()).addTableModelListener(e -> HienThiThongtin());
        this.fillHoaDonCho();
        init();
        init2();
        txtTimKiemHoaDon.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                timKiem();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                timKiem();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                timKiem();
            }
        });

        txtTimKiemSanPham.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timKiemSanPham();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timKiemSanPham();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timKiemSanPham();
            }
        });

    }

    public BanHang() {
        this(null); // Gọi constructor chính và truyền null
    }

    private void init() {

        txtTimKiemHoaDon.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                checkReset();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                checkReset();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                checkReset();
            }

            private void checkReset() {
                if (txtTimKiemHoaDon.getText().trim().isEmpty()) {
                    List<Object[]> list = dao.getDonHangCho();
                    fillHoaDonCho2(list); // ✅ Reset bảng khi xóa nội dung tìm kiếm
                }
            }
        });
    }

    private void init2() {

        txtTimKiemSanPham.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                checkReset();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                checkReset();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                checkReset();
            }

            private void checkReset() {
                if (txtTimKiemSanPham.getText().trim().isEmpty()) {
                    List<Object[]> list = dao.getDonHangCho();
                    fillToTable(); // ✅ Reset bảng khi xóa nội dung tìm kiếm
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHoaDonCho = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblGioHang = new javax.swing.JTable();
        btnXoa = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblSanPham = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtKH = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtSDT = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTongTien = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cboLoaiThanhToan = new javax.swing.JComboBox<>();
        btnThanhToan = new javax.swing.JButton();
        btnTaoHoaDon = new javax.swing.JButton();
        btnHuy = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtDiaChi = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtTienGiam = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtTimKiemHoaDon = new javax.swing.JTextField();
        txtTimKiemSanPham = new javax.swing.JTextField();

        setForeground(new java.awt.Color(0, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel1.setText("Đơn hàng chờ");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setForeground(new java.awt.Color(0, 102, 51));

        tblHoaDonCho.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tblHoaDonCho.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã hóa đơn", "Tên nhân viên", "Ngày tạo", "Tên KH", "SDT", "Địa chỉ", "Trạng thái"
            }
        ));
        jScrollPane1.setViewportView(tblHoaDonCho);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(305, 305, 305))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel2.setForeground(new java.awt.Color(0, 102, 0));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel2.setText("Giỏ hàng");

        tblGioHang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tblGioHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã SP", "Tên SP", "Số lương", "Giảm giá ", "Thành tiền", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblGioHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblGioHangMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblGioHang);

        btnXoa.setBackground(new java.awt.Color(102, 102, 102));
        btnXoa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXoa.setForeground(new java.awt.Color(255, 255, 255));
        btnXoa.setText("Xóa");
        btnXoa.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnSua.setBackground(new java.awt.Color(102, 102, 102));
        btnSua.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSua.setForeground(new java.awt.Color(255, 255, 255));
        btnSua.setText("Sửa");
        btnSua.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXoa)
                    .addComponent(btnSua))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        tblSanPham.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tblSanPham.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã SPCT", "Mã SP", "Tên SP", "Mã CPU", "Mã RAM", "Mã dung lượng", "Mã GPU", "IMEI", "Giá", "Số Lượng"
            }
        ));
        tblSanPham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSanPhamMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblSanPham);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel3.setText("Sản phẩm ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Tên KH");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("SDT");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Tổng tiền");

        txtTongTien.setEditable(false);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Hình thức thanh toán");

        cboLoaiThanhToan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboLoaiThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLoaiThanhToanActionPerformed(evt);
            }
        });

        btnThanhToan.setBackground(new java.awt.Color(102, 102, 102));
        btnThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThanhToan.setForeground(new java.awt.Color(255, 255, 255));
        btnThanhToan.setText("Thanh toán");
        btnThanhToan.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThanhToanActionPerformed(evt);
            }
        });

        btnTaoHoaDon.setBackground(new java.awt.Color(102, 102, 102));
        btnTaoHoaDon.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTaoHoaDon.setForeground(new java.awt.Color(255, 255, 255));
        btnTaoHoaDon.setText("Tạo hóa đơn");
        btnTaoHoaDon.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnTaoHoaDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTaoHoaDonActionPerformed(evt);
            }
        });

        btnHuy.setBackground(new java.awt.Color(102, 102, 102));
        btnHuy.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHuy.setForeground(new java.awt.Color(255, 255, 255));
        btnHuy.setText("Hủy");
        btnHuy.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnHuy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHuyActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Địa chỉ");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Khuyến mãi");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Email");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTaoHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnHuy, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtKH))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTongTien))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTienGiam))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel10))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDiaChi)
                            .addComponent(txtSDT)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(42, 42, 42)
                        .addComponent(txtEmail))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboLoaiThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTongTien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTienGiam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cboLoaiThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnHuy)
                    .addComponent(btnTaoHoaDon)
                    .addComponent(btnThanhToan))
                .addGap(74, 74, 74))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(txtTimKiemSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(668, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(18, 18, 18))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(txtTimKiemHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(71, 71, 71)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTimKiemHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(39, 39, 39)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTimKiemSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblSanPhamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSanPhamMouseClicked
        // TODO add your handling code here:
        int row = tblSanPham.getSelectedRow();
        if (row == -1) {
            return;
        }
        JOptionPane.showMessageDialog(this, "Bạn đã thêm thành công!");

        DefaultTableModel modelSP = (DefaultTableModel) tblSanPham.getModel();
        DefaultTableModel modelGioHang = (DefaultTableModel) tblGioHang.getModel();

        Object oMaSPCT = modelSP.getValueAt(row, 0); // Lấy MaSPCT
        Object oMaSP = modelSP.getValueAt(row, 1);
        Object oTenSP = modelSP.getValueAt(row, 2);
        Object oGia = modelSP.getValueAt(row, 8);

        if (oMaSPCT == null || oMaSP == null || oTenSP == null || oGia == null) {
            JOptionPane.showMessageDialog(this, "Thiếu dữ liệu sản phẩm, không thể thêm vào giỏ.");
            return;
        }

        String maSPCT = oMaSPCT.toString();
        String maSP = oMaSP.toString();
        String tenSP = oTenSP.toString();
        int gia = Integer.parseInt(oGia.toString());
        int soLuong = 1;
        int giamGia = 0;
        int thanhTien = gia * soLuong;
        modelGioHang.addRow(new Object[]{
            maSP,
            tenSP,
            soLuong,
            giamGia + " VNĐ",
            thanhTien,
            false
        });
        int oldSL = Integer.parseInt(modelSP.getValueAt(row, 9).toString());
        int newSL = oldSL - soLuong;
        if (newSL < 0) {
            JOptionPane.showMessageDialog(this, "Sản phẩm đã hết hàng!");
            return;
        }
        modelSP.setValueAt(newSL, row, 9);
        spctdao.capNhatSoLuong(maSPCT, newSL);


    }//GEN-LAST:event_tblSanPhamMouseClicked

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        // TODO add your handling code here:
        int row = tblGioHang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Bạn chưa chọn sản phẩm trong giỏ hàng để sửa.");
            return;
        }
        DefaultTableModel model = (DefaultTableModel) tblGioHang.getModel();
        String maSP = model.getValueAt(row, 0).toString(); // Mã SP
        int donGia = -1;
        int soLuongTon = -1;
        int indexSP = -1;

// Tìm lại số lượng tồn và giá từ bảng sản phẩm
        for (int i = 0; i < tblSanPham.getRowCount(); i++) {
            if (tblSanPham.getValueAt(i, 1).toString().equals(maSP)) {
                soLuongTon = Integer.parseInt(tblSanPham.getValueAt(i, 9).toString());
                donGia = Integer.parseInt(tblSanPham.getValueAt(i, 8).toString());
                indexSP = i;
                break;
            }
        }

        if (soLuongTon == -1 || donGia == -1) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm trong danh sách!");
            return;
        }

        try {
            String input = JOptionPane.showInputDialog(this, "Nhập số lượng mới:");
            if (input == null || input.trim().isEmpty()) {
                return;
            }

            int soLuongMoi = Integer.parseInt(input.trim());
            if (soLuongMoi <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0.");
                return;
            }

            // Số lượng ban đầu trong giỏ hàng
            int soLuongCu = Integer.parseInt(model.getValueAt(row, 2).toString());
            int chenhLech = soLuongMoi - soLuongCu;

            if (chenhLech > soLuongTon) {
                JOptionPane.showMessageDialog(this, "Số lượng vượt quá tồn kho.");
                return;
            }

            // Lấy phần trăm giảm giá từ cột
            String giamGiaText = model.getValueAt(row, 3).toString().replace("%", "").replace(" VNĐ", "").replace(",", "").trim();
            int giamGia = 0;
            try {
                giamGia = Integer.parseInt(giamGiaText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Giảm giá không hợp lệ: \"" + giamGiaText + "\"");
                return;
            }

            // Tính lại thành tiền
            int thanhTien = (int) (donGia * soLuongMoi * (1 - giamGia / 100.0));

            // Cập nhật giỏ hàng
            model.setValueAt(soLuongMoi, row, 2);
            model.setValueAt(thanhTien + " VNĐ", row, 4); // Ghi rõ đơn vị VNĐ

            // Cập nhật tồn kho
            int soLuongMoiTrongSP = soLuongTon - chenhLech;
            tblSanPham.setValueAt(soLuongMoiTrongSP, indexSP, 9);

            // Cập nhật DB
            String maSPCT = tblSanPham.getValueAt(indexSP, 0).toString();
            spctdao.capNhatSoLuong(maSPCT, soLuongMoiTrongSP);

            updateTongTien();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số.");
        }


    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // TODO add your handling code here:
        int row = tblGioHang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        DefaultTableModel modelGH = (DefaultTableModel) tblGioHang.getModel();
        DefaultTableModel modelSP = (DefaultTableModel) tblSanPham.getModel();

        // Lấy thông tin từ giỏ hàng
        String maSP = modelGH.getValueAt(row, 0).toString();
        int soLuongTrongGio = Integer.parseInt(modelGH.getValueAt(row, 2).toString());

        // Tìm sản phẩm tương ứng trong bảng tblSanPham để cộng lại số lượng
        for (int i = 0; i < modelSP.getRowCount(); i++) {
            if (modelSP.getValueAt(i, 1).toString().equals(maSP)) {
                int soLuongHienTai = Integer.parseInt(modelSP.getValueAt(i, 9).toString());
                int soLuongMoi = soLuongHienTai + soLuongTrongGio;

                modelSP.setValueAt(soLuongMoi, i, 9); // Cập nhật lại trên bảng UI

                // Cập nhật số lượng trong DB
                String maSPCT = modelSP.getValueAt(i, 0).toString();
                spctdao.capNhatSoLuong(maSPCT, soLuongMoi);
                break;
            }
        }

        // Xóa khỏi giỏ hàng
        modelGH.removeRow(row);

        // Cập nhật lại tổng tiền
        HienThiThongtin();
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnTaoHoaDonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTaoHoaDonActionPerformed
        // TODO add your handling code here:
        this.TaoDonHang();

    }//GEN-LAST:event_btnTaoHoaDonActionPerformed

    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToanActionPerformed

        this.thanhtoan();
    }//GEN-LAST:event_btnThanhToanActionPerformed

    private void btnHuyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHuyActionPerformed
        // TODO add your handling code here:
        this.huyDonHang();
    }//GEN-LAST:event_btnHuyActionPerformed

    private void cboLoaiThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLoaiThanhToanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboLoaiThanhToanActionPerformed

    private void tblGioHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblGioHangMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblGioHangMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHuy;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnTaoHoaDon;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cboLoaiThanhToan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblGioHang;
    private javax.swing.JTable tblHoaDonCho;
    private javax.swing.JTable tblSanPham;
    private javax.swing.JTextField txtDiaChi;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtKH;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtTienGiam;
    private javax.swing.JTextField txtTimKiemHoaDon;
    private javax.swing.JTextField txtTimKiemSanPham;
    private javax.swing.JTextField txtTongTien;
    // End of variables declaration//GEN-END:variables

    private void fillToTable() {

        DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
        model.setRowCount(0);

//        List<Object[]> list = daoSPCT.selectAllspchitiet();
//        for (Object[] x : list) {
//            model.addRow(x);
//        }
        lstThongtin = spctdao.findALLtt();
        lstThongtin.forEach(item -> {
            model.addRow(new Object[]{
                item.getSpct().getMaSPCT(),
                item.getSpct().getMaSP(),
                item.getSanPham().getTenSP(),
                item.getCpu(),
                item.getRam(),
                item.getDungLuong(),
                item.getGpu(),
                item.getSoIMEI(),
                item.getSpct().getGia(),
                item.getSpct().getSoLuong()
            });
        });

    }

    private void fillHoaDonCho() {
        DefaultTableModel hdc = (DefaultTableModel) tblHoaDonCho.getModel();
        hdc.setRowCount(0);

        List<Object[]> list = dao.getDonHangCho(); // Lấy danh sách đơn hàng chờ
        for (Object[] x : list) {
            int trangThai = (x[6] instanceof Integer) ? (Integer) x[6] : 0;
            String trangThaiText = (trangThai == 0) ? "Chưa thanh toán" : "Đã thanh toán";

            // ✅ Xử lý mã hóa đơn: "006" hoặc "HD006" → "HD6"
            String maHDGoc = x[0].toString(); // lấy từ DB
            String soHD;

            if (maHDGoc.startsWith("HD")) {
                soHD = maHDGoc.substring(2); // bỏ "HD" → còn "006"
            } else {
                soHD = maHDGoc; // đã là "006" rồi
            }

            String maHDHienThi = "HD" + Integer.parseInt(soHD); // "HD6"

            hdc.addRow(new Object[]{
                maHDHienThi, // ✅ Mã hóa đơn đã chuẩn hóa: HD6, HD7...
                x[1], // Tên nhân viên
                x[2], // Ngày tạo
                x[3], // Tên khách hàng
                x[4], // SĐT
                x[5], // Địa chỉ
                trangThaiText // Trạng thái
            });
        }
    }

    private void updateTongTien() {
        DefaultTableModel model = (DefaultTableModel) tblGioHang.getModel();
        int tongTien = 0;
        int tongSoLuong = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            Object obj = model.getValueAt(i, 4); // Thành tiền
            if (obj == null) {
                continue;
            }

            String thanhTienText = obj.toString().replace("VNĐ", "").replace(",", "").trim();
            try {
                int thanhTien = Integer.parseInt(thanhTienText);
                tongTien += thanhTien;
            } catch (NumberFormatException e) {
                System.out.println("Lỗi tính thành tiền: " + thanhTienText);
            }

            Object slObj = model.getValueAt(i, 2); // Cột số lượng
            if (slObj != null) {
                try {
                    tongSoLuong += Integer.parseInt(slObj.toString());
                } catch (NumberFormatException e) {
                    // bỏ qua
                }
            }
        }

        // ✅ Tính tiền giảm theo KM
        double tienGiam = apDungKhuyenMaiTuDong(tongTien, tongSoLuong);
        txtTienGiam.setText(String.format("%,.0f VNĐ", tienGiam));
        txtTongTien.setText(String.format("%,.0f VNĐ", tongTien - tienGiam));
    }

    private void HienThiThongtin() {
        DefaultTableModel model = (DefaultTableModel) tblGioHang.getModel();
        int tong = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean checked = (Boolean) model.getValueAt(i, 5); // cột checkbox
            if (checked != null && checked) {
                Object value = model.getValueAt(i, 4); // Thành tiền
                if (value != null) {
                    try {
                        tong += Integer.parseInt(value.toString());
                    } catch (NumberFormatException e) {
                        // Bỏ qua nếu lỗi
                    }
                }
            }
        }

        // Gán vào ô "Tổng tiền"
        txtTongTien.setText(String.valueOf(tong));
        updateTongTien();
    }

    private void TaoDonHang() {
        // Lưu hóa đơn với trạng thái 0 (Chưa thanh toán)
        if (txtKH.getText().trim().isEmpty()
                || txtSDT.getText().trim().isEmpty()
                || txtDiaChi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên khách hàng, SĐT và Địa chỉ!");
            return;
        }

        String tenKH = txtKH.getText();
        String sdt = txtSDT.getText();
        String diaChi = txtDiaChi.getText();
        String email = txtEmail.getText().trim();
        // Thêm khách hàng mới
        KhachHang kh = new KhachHang();
        kh.setTenKH(tenKH);
        kh.setSoDienThoai(sdt);
        kh.setDiaChi(diaChi);
        kh.setEmail(email.isEmpty() ? null : email);
        KhachHangDao khDao = new KhachHangDaoImpl();
        String maKH = khDao.insertAndGetMaKH(kh); // Bạn cần tạo DAO này
        if (maKH == null) {
            JOptionPane.showMessageDialog(this, "Không thể tạo ra mã khách hàng mới!");
            return;
        }
        HoaDon hd = new HoaDon(); // ✅ Phải tạo hd trước
        String maHD = dao.generateMaHD(); // ✅ Gọi sinh mã sau đó
        hd.setMaHD(maHD);

        hd.setMaHD(maHD);
        hd.setMaNV("NV1");
        hd.setMaKH(maKH);
        hd.setTenKHNhan(tenKH);
        hd.setSoDienThoaiNguoiNhan(sdt);
        hd.setDiaChiNguoiNhan(diaChi);
        hd.setThanhTien(0);
        hd.setTrangThai(0);
        hd.setNgayTao(new java.util.Date());

        String maHDInsert = dao.insertHoaDon(hd); // Đổi tên biến tránh trùng
        if (maHDInsert == null) {
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn thất bại");
            return;
        }

        this.maHDDangTao = maHDInsert; // ✅ lưu lại để thêm chi tiết khi thanh toán
        System.out.println("Tạo xong hóa đơn, gọi fill...");
        this.fillHoaDonCho();

        JOptionPane.showMessageDialog(this, "Đã tạo hóa đơn, mã: " + maHDInsert);
        System.out.println("Mã hóa đơn vừa tạo: " + maHD);

    }

    private void thanhtoan() {
        if (maHDDangTao == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn trong danh sách đơn chờ!");
            return;
        }

        // Xử lý khuyến mãi & tổng tiền
        String idKM = (kmDangApDung != null) ? kmDangApDung.getIdKhuyenMai() : null;
        int loaiGiam = (kmDangApDung != null) ? kmDangApDung.getLoaiGiam() : 0;
        int giaTriGiam = 0;
        try {
            giaTriGiam = Integer.parseInt(txtTienGiam.getText().replace("VNĐ", "").replace(",", "").trim());
        } catch (Exception e) {
            giaTriGiam = 0;
        }

        int thanhTien = 0;
        try {
            thanhTien = Integer.parseInt(txtTongTien.getText().replace("VNĐ", "").replace(",", "").trim());
        } catch (Exception e) {
            thanhTien = 0;
        }

        //. Cập nhật hóa đơn
        dao.updateHoaDonSauThanhToan(maHDDangTao, thanhTien, idKM, loaiGiam, giaTriGiam);

        // Thêm chi tiết hóa đơn vào bảng HDCT
        HoaDonChiTietDao hdctDao = new HoaDonChiTietDao();  // DAO bạn đã có
        DefaultTableModel model = (DefaultTableModel) tblGioHang.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            String maSP = model.getValueAt(i, 0).toString();  // Mã SP
            int soLuong = Integer.parseInt(model.getValueAt(i, 2).toString());

            String donGiaText = model.getValueAt(i, 4).toString().replace("VNĐ", "").replace(",", "").trim();
            int donGia = Integer.parseInt(donGiaText);

            //  Tìm lại mã SPCT từ bảng sản phẩm
            String maSPCT = null;
            for (int j = 0; j < tblSanPham.getRowCount(); j++) {
                if (tblSanPham.getValueAt(j, 1).toString().equals(maSP)) {
                    maSPCT = tblSanPham.getValueAt(j, 0).toString();
                    break;
                }
            }

            if (maSPCT == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy mã SPCT cho sản phẩm: " + maSP);
                continue;
            }

            // Tạo đối tượng HDCT
            HoaDonChiTiet hdct = new HoaDonChiTiet();
            hdct.setIdHDCT(hdctDao.generateMaHDCT()); // ?tự sinh mã HDCT
            hdct.setMaHD(maHDDangTao);
            hdct.setMaSPCT(maSPCT);
            hdct.setSoLuong(soLuong);
            hdct.setDonGiaBan(donGia);

            boolean success = hdctDao.insertHDCT(hdct);
            if (!success) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm sản phẩm vào bảng HDCT: " + maSP);
            }
        }

        JOptionPane.showMessageDialog(this, "Thanh toán thành công cho hóa đơn: " + maHDDangTao);
        maHDDangTao = null;
        fillHoaDonCho();
        clearForm();

    }

    private void clearForm() {
        DefaultTableModel model = (DefaultTableModel) tblGioHang.getModel();
        model.setRowCount(0);
        txtKH.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        txtTongTien.setText("");
//        txtKhachDuaTien.setText("");
        maHDDangTao = null;
    }

    private void fillComBoBox() {
        String[] hinhThuc = {"Tiền mặt", "Chuyển khoản", "Khác"};
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(hinhThuc);
        cboLoaiThanhToan.setModel(model);
    }

    private void huyDonHang() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn hủy đơn hàng này không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        DefaultTableModel modelGioHang = (DefaultTableModel) tblGioHang.getModel();
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            String maSP = modelGioHang.getValueAt(i, 0).toString(); // Mã SP
            int soLuongTrongGio = Integer.parseInt(modelGioHang.getValueAt(i, 2).toString());

            for (int j = 0; j < tblSanPham.getRowCount(); j++) {
                if (tblSanPham.getValueAt(j, 1).toString().equals(maSP)) {
                    int soLuongHienTai = Integer.parseInt(tblSanPham.getValueAt(j, 9).toString());
                    int soLuongMoi = soLuongHienTai + soLuongTrongGio;
                    tblSanPham.setValueAt(soLuongMoi, j, 9);

                    String maSPCT = tblSanPham.getValueAt(j, 0).toString(); // Mã SPCT
                    spctdao.capNhatSoLuong(maSPCT, soLuongMoi); // cập nhật vào DB
                    break;
                }
            }
        }
        // Xóa giỏ hàng và reset form
        modelGioHang.setRowCount(0);
        txtKH.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        txtTongTien.setText("");
        cboLoaiThanhToan.setSelectedIndex(0);
        maHDDangTao = null;

        JOptionPane.showMessageDialog(this, "Đã hủy đơn hàng và hoàn số lượng!");
    }

    private void fillHoaDonCho2(List<Object[]> list) {
        DefaultTableModel hdc = (DefaultTableModel) tblHoaDonCho.getModel();
        hdc.setRowCount(0);

        for (Object[] x : list) {
            int trangThai = (x[6] instanceof Integer) ? (Integer) x[6] : 0;
            String trangThaiText = (trangThai == 0) ? "Chưa thanh toán" : "Đã thanh toán";

            hdc.addRow(new Object[]{
                x[0], // MaHD
                x[1], // TenNV
                x[2], // NgayTao
                x[3], // TenKH
                x[4], // SDT
                x[5], // DiaChi
                trangThaiText
            });
        }
    }

    void timKiem() {
        String key = txtTimKiemHoaDon.getText();
        List<Object[]> list = dao.timKiemHoaDon(key);
        DefaultTableModel model = (DefaultTableModel) tblHoaDonCho.getModel();
        model.setRowCount(0);
        for (Object[] x : list) {
            int trangThai = (x[6] instanceof Integer) ? (Integer) x[6] : 0;
            String trangThaiText = (trangThai == 0) ? "Chưa thanh toán" : "Đã thanh toán";

            model.addRow(new Object[]{
                x[0], // Mã hóa đơn
                x[1], // Tên nhân viên
                x[2], // Ngày tạo
                x[3], // Tên KH
                x[4], // SDT
                x[5], // Địa chỉ
                trangThaiText // Chuyển trạng thái thành chuỗi
            });
        }
    }

    void timKiemSanPham() {
        String keyword = txtTimKiemSanPham.getText().trim();
        DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
        model.setRowCount(0);
        List<ThongTinSP> list = spctdao.timKiemThongTinSP(keyword);
        for (ThongTinSP item : list) {
            model.addRow(new Object[]{
                item.getSpct().getMaSPCT(),
                item.getSpct().getMaSP(),
                item.getSanPham().getTenSP(),
                item.getCpu().getMaCPU(),
                item.getRam().getMaRAM(),
                item.getDungLuong().getMaDungLuong(),
                item.getGpu().getMaGPU(),
                item.getSoIMEI(),
                item.getSpct().getGia(),
                item.getSpct().getSoLuong()
            });
        }
    }
}
