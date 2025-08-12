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
    // Lưu IMEI đã chọn theo Mã SP (nếu bạn gộp giỏ theo MaSP; nếu gộp theo MaSPCT thì đổi key thành maSPCT)
    private final java.util.Map<String, java.util.Set<String>> cartImeis = new java.util.HashMap<>();

// DAO IMEI: bạn đã thêm các hàm vào IMEIDao rồi
    private final lags.dao.IMEIDao imeiDao = new lags.dao.IMEIDao();

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
        DefaultTableModel modelGioHang = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Mã SP", "Tên SP", "Số lượng", "Thành tiền", "Chọn"}
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) {
                    return Boolean.class; // Cột 4 là checkbox
                }
                return String.class;
            }
        };
        tblGioHang.setModel(modelGioHang);
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
        updateInvoiceUIState();
        attachTextListeners();
        ((DefaultTableModel) tblGioHang.getModel()).addTableModelListener(e -> refreshCheckoutState());
        refreshCheckoutState();
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
        jLabel9 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        txtTimKiemHoaDon = new javax.swing.JTextField();
        txtTimKiemSanPham = new javax.swing.JTextField();

        setForeground(new java.awt.Color(0, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel1.setText("Hóa đơn chờ");

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
        tblHoaDonCho.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHoaDonChoMouseClicked(evt);
            }
        });
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
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Mã SP", "Tên SP", "Số lương", "Thành tiền", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
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
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã SPCT", "Mã SP", "Tên SP", "Mã CPU", "Mã RAM", "Mã dung lượng", "Mã GPU", "Giá", "Số Lượng"
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

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboLoaiThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
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
        if (maHDDangTao == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tạo hóa đơn trước khi thêm sản phẩm!");
            return;
        }
        int row = tblSanPham.getSelectedRow();
        if (row == -1) {
            return;
        }

        var modelSP = (javax.swing.table.DefaultTableModel) tblSanPham.getModel();
        var modelGH = (javax.swing.table.DefaultTableModel) tblGioHang.getModel();

        String maSPCT = String.valueOf(modelSP.getValueAt(row, 0));
        String maSP = String.valueOf(modelSP.getValueAt(row, 1));
        String tenSP = String.valueOf(modelSP.getValueAt(row, 2));
        int donGia = safeToInt(modelSP.getValueAt(row, 7));
        int tonKho = safeToInt(modelSP.getValueAt(row, 8));
        if (donGia <= 0 || tonKho <= 0) {
            JOptionPane.showMessageDialog(this, "Sản phẩm hết hàng/giá lỗi");
            return;
        }

        java.util.List<String> imeiCon = imeiDao.findAvailableByMaSPCT_String(maSPCT);
        if (imeiCon.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Không tìm thấy IMEI cho MaSPCT=" + maSPCT + " (0 bản ghi)."
                    + "\nKiểm tra bảng IMEI và TrangThai=0?"
            );
            return;
        }

        java.util.Set<String> imeiDaChon = cartImeis.getOrDefault(maSP, new java.util.HashSet<>());
        ImeiPickerDialog dlg = new ImeiPickerDialog(
                (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
                imeiCon, imeiDaChon
        );
        dlg.setVisible(true);
        java.util.List<String> imeiChonMoi = dlg.getSelectedImeis();
        if (imeiChonMoi == null || imeiChonMoi.isEmpty()) {
            return;
        }

        int soLuongThem = imeiChonMoi.size();
        if (soLuongThem > tonKho) {
            JOptionPane.showMessageDialog(this, "Vượt tồn kho! Còn: " + tonKho);
            return;
        }

        int idx = -1;
        for (int i = 0; i < modelGH.getRowCount(); i++) {
            if (maSP.equalsIgnoreCase(String.valueOf(modelGH.getValueAt(i, 0)))) {
                idx = i;
                break;
            }
        }
        if (idx >= 0) {
            int slCu = safeToInt(modelGH.getValueAt(idx, 2));
            int slMoi = slCu + soLuongThem;
            modelGH.setValueAt(slMoi, idx, 2);
            modelGH.setValueAt(formatVND(donGia * slMoi), idx, 3);
        } else {
            modelGH.addRow(new Object[]{maSP, tenSP, soLuongThem, formatVND(donGia * soLuongThem), Boolean.FALSE});
        }

        // 5) Lưu IMEI đã chọn và giữ chỗ trong DB
        // Lưu IMEI đã chọn theo maSPCT
        java.util.Set<String> gop = new java.util.HashSet<>(imeiDaChon);
        gop.addAll(imeiChonMoi);
        cartImeis.put(maSPCT, gop); // ✅ dùng maSPCT

// Giữ chỗ trong DB
        imeiDao.holdImeis(imeiChonMoi);

        // 6) Cập nhật tồn hiển thị & DB
        int tonKhoMoi = tonKho - soLuongThem;
        modelSP.setValueAt(tonKhoMoi, row, 8);
        spctdao.capNhatSoLuong(maSPCT, tonKhoMoi);

        updateTongTien();

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

        // Tìm lại số lượng tồn và giá từ bảng sản phẩm (cột 8 = SL, 7 = Giá)
        for (int i = 0; i < tblSanPham.getRowCount(); i++) {
            if (tblSanPham.getValueAt(i, 1).toString().equals(maSP)) {
                soLuongTon = safeToInt(tblSanPham.getValueAt(i, 8)); // SL tồn
                donGia = safeToInt(tblSanPham.getValueAt(i, 7)); // Giá
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

            int soLuongCu = safeToInt(model.getValueAt(row, 2));
            int chenhLech = soLuongMoi - soLuongCu;

            if (chenhLech > soLuongTon) {
                JOptionPane.showMessageDialog(this, "Số lượng vượt quá tồn kho.");
                return;
            }

            // Tính lại thành tiền (không lấy % từ cột 3, cột 3 là VNĐ)
            int thanhTien = donGia * soLuongMoi;

            // Cập nhật giỏ
            model.setValueAt(soLuongMoi, row, 2);
            model.setValueAt(formatVND(thanhTien), row, 3);

            // Cập nhật tồn kho hiển thị (ĐỔI 9 -> 8)
            int soLuongMoiTrongSP = soLuongTon - chenhLech;
            tblSanPham.setValueAt(soLuongMoiTrongSP, indexSP, 8);

            // Cập nhật DB
            String maSPCT = tblSanPham.getValueAt(indexSP, 0).toString();
            spctdao.capNhatSoLuong(maSPCT, soLuongMoiTrongSP);

            updateTongTien();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số.");
        }
        updateTongTien();
        refreshCheckoutState();


    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // TODO add your handling code here:
        int row = tblGioHang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        DefaultTableModel modelGH = (DefaultTableModel) tblGioHang.getModel();
        DefaultTableModel modelSP = (DefaultTableModel) tblSanPham.getModel();

        // Lấy thông tin từ giỏ hàng
        String maSP = modelGH.getValueAt(row, 0).toString();
        int soLuongTrongGio = safeToInt(modelGH.getValueAt(row, 2));

        // 1) Trả IMEI đã giữ cho mã SP này (nếu có giữ trước đó)
        java.util.Set<String> imeis = cartImeis.remove(maSP);
        if (imeis != null && !imeis.isEmpty()) {
            // Nếu bạn đã dùng holdImeis khi chọn:
            // imeiDao.releaseImeis(new java.util.ArrayList<>(imeis));
        }

        // 2) Cộng lại tồn kho vào bảng Sản phẩm + cập nhật DB
        for (int i = 0; i < modelSP.getRowCount(); i++) {
            if (modelSP.getValueAt(i, 1).toString().equals(maSP)) {
                int soLuongHienTai = safeToInt(modelSP.getValueAt(i, 8)); // cột 8 = Số lượng
                int soLuongMoi = soLuongHienTai + soLuongTrongGio;

                modelSP.setValueAt(soLuongMoi, i, 8); // cập nhật UI

                String maSPCT = modelSP.getValueAt(i, 0).toString();
                spctdao.capNhatSoLuong(maSPCT, soLuongMoi); // cập nhật DB
                break;
            }
        }

        // 3) Xóa khỏi giỏ hàng
        modelGH.removeRow(row);

        // 4) Cập nhật lại tổng tiền / trạng thái
        HienThiThongtin();
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnTaoHoaDonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTaoHoaDonActionPerformed
        // TODO add your handling code here:
        if (dao.countPending() >= 5) {
            JOptionPane.showMessageDialog(this, "Đã đạt giới hạn 5 hóa đơn chờ, xử lý bớt trước khi tạo mới!");
            return;
        }

        String[] options = {"Khách vãng lai", "Khách đã mua", "Khách mới", "Hủy"};
        int choice = JOptionPane.showOptionDialog(
                this, "Chọn loại khách hàng để tạo hóa đơn", "Chọn khách",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        // Hủy
        if (choice == 3 || choice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        KhachHangDao khDao = new KhachHangDaoImpl();

        // 1) Khách vãng lai
        if (choice == 0) {
            String maKH = ensureGuestCustomer(); // đã có sẵn trong file
            if (maKH == null) {
                JOptionPane.showMessageDialog(this, "Không tạo được khách vãng lai!");
                return;
            }
            TaoDonHangForCustomer(maKH, "Khách vãng lai", null, null, null);
            return;
        }

        // 2) Khách đã mua (tìm theo SĐT)
        if (choice == 1) {
            String sdt = JOptionPane.showInputDialog(this, "Nhập SĐT khách hàng:");
            if (sdt == null || sdt.trim().isEmpty()) {
                return;
            }

            // YÊU CẦU: interface KhachHangDao phải có KhachHang findByPhone(String)
            KhachHang kh = khDao.findByPhone(sdt.trim());
            if (kh == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách có SĐT: " + sdt);
                return;
            }
            TaoDonHangForCustomer(kh.getMaKH(), kh.getTenKH(),
                    kh.getSoDienThoai(), kh.getDiaChi(), kh.getEmail());
            return;
        }

        // 3) Khách mới (lưu thông tin rồi tạo HĐ)
        if (choice == 2) {
            String ten = JOptionPane.showInputDialog(this, "Tên khách hàng mới:");
            String sdt = JOptionPane.showInputDialog(this, "SĐT khách hàng:");
            String diaChi = JOptionPane.showInputDialog(this, "Địa chỉ khách hàng:");
            String email = JOptionPane.showInputDialog(this, "Email (có thể bỏ trống):");

            if (ten == null || sdt == null || diaChi == null) {
                return; // user bấm Hủy
            }
            if (ten.trim().isEmpty() || sdt.trim().isEmpty() || diaChi.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ Tên, SĐT, Địa chỉ!");
                return;
            }

            // ✅ Check trùng SĐT
            KhachHangDao khDaoCheck = new KhachHangDaoImpl();
            KhachHang khTonTai = khDaoCheck.findByPhone(sdt.trim());
            if (khTonTai != null) {
                JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại cho khách: " + khTonTai.getTenKH());
                return;
            }

            KhachHang khNew = new KhachHang();
            khNew.setTenKH(ten.trim());
            khNew.setSoDienThoai(sdt.trim());
            khNew.setDiaChi(diaChi.trim());
            khNew.setEmail((email != null && !email.trim().isEmpty()) ? email.trim() : null);

            String maKH = khDaoCheck.insertAndGetMaKH(khNew);
            if (maKH == null) {
                JOptionPane.showMessageDialog(this, "Không thể tạo khách hàng mới!");
                return;
            }
            TaoDonHangForCustomer(maKH, ten.trim(), sdt.trim(), diaChi.trim(), email);
        }
    }//GEN-LAST:event_btnTaoHoaDonActionPerformed

    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToanActionPerformed
        this.capNhatTongTienVaGiamGia();

        // ✅ Chốt bán tất cả IMEI trong giỏ (TrangThai=1)
        // Chốt bán tất cả IMEI trong giỏ
        java.util.List<String> allImeis = new java.util.ArrayList<>();
        for (java.util.Set<String> imeis : cartImeis.values()) {
            allImeis.addAll(imeis);
        }
        imeiDao.markSoldImeis(allImeis, maHDDangTao);

        this.thanhtoan();
        this.maHDDangTao = null;
        clearCartAndForm();
        updateInvoiceUIState();
    }//GEN-LAST:event_btnThanhToanActionPerformed

    private void btnHuyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHuyActionPerformed
        // TODO add your handling code here:
        // ✅ Trả lại IMEI giữ chỗ khi hủy
        // Trả lại IMEI giữ chỗ
java.util.List<String> allImeis = new java.util.ArrayList<>();
for (java.util.Set<String> imeis : cartImeis.values()) {
    allImeis.addAll(imeis);
}
imeiDao.releaseImeis(allImeis);


        this.huyDonHang();
        this.maHDDangTao = null;
        clearCartAndForm();
        updateInvoiceUIState();

    }//GEN-LAST:event_btnHuyActionPerformed

    private void cboLoaiThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLoaiThanhToanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboLoaiThanhToanActionPerformed

    private void tblGioHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblGioHangMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblGioHangMouseClicked

    private void tblHoaDonChoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHoaDonChoMouseClicked
        // TODO add your handling code here:
          int row = tblHoaDonCho.getSelectedRow();
    if (row == -1) {
        return;
    }

    // Lấy mã HĐ từ bảng
    String maHD = tblHoaDonCho.getValueAt(row, 0).toString();
    maHDDangTao = maHD;

    // Lấy thông tin hóa đơn + khách hàng
    HoaDon hd = dao.findById(maHD);
    if (hd != null) {
        txtKH.setText(hd.getTenKHNhan() != null ? hd.getTenKHNhan() : "");
        txtSDT.setText(hd.getSoDienThoaiNguoiNhan() != null ? hd.getSoDienThoaiNguoiNhan() : "");
        txtDiaChi.setText(hd.getDiaChiNguoiNhan() != null ? hd.getDiaChiNguoiNhan() : "");
//        txtEmail.setText(hd.getEmailNguoiNhan() != null ? hd.getEmailNguoiNhan() : "");
    }

    // Load giỏ hàng
    DefaultTableModel modelGH = (DefaultTableModel) tblGioHang.getModel();
    modelGH.setRowCount(0);
    HoaDonChiTietDao hdctDao = new HoaDonChiTietDao();
    List<Object[]> chiTietList = hdctDao.getChiTietByMaHD(maHD);
    for (Object[] ct : chiTietList) {
        String maSP = (String) ct[0];
        String tenSP = (String) ct[1];
        int soLuong = (int) ct[2];
        int donGia = (int) ct[3];
        modelGH.addRow(new Object[]{
            maSP, tenSP, soLuong, formatVND(donGia * soLuong), Boolean.FALSE
        });
    }

    updateTongTien();
    updateInvoiceUIState();
    }//GEN-LAST:event_tblHoaDonChoMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHuy;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnTaoHoaDon;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cboLoaiThanhToan;
    private javax.swing.JComboBox<String> jComboBox1;
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
    private javax.swing.JTextField txtTimKiemHoaDon;
    private javax.swing.JTextField txtTimKiemSanPham;
    private javax.swing.JTextField txtTongTien;
    // End of variables declaration//GEN-END:variables

    private void fillToTable() {
        DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
        model.setRowCount(0);

        String sql = """
        SELECT 
            spct.MaSPCT,
            spct.MaSP,
            sp.TenSP,
            cpu.TenCPU,
            ram.DungLuong as DLR,
            dl.DungLuong,
            gpu.TenGPU,
            spct.Gia,
            COUNT(im.SoIMEI) AS SoLuong
        FROM SanPhamChiTiet spct
        JOIN SanPham sp ON sp.MaSP = spct.MaSP
        JOIN CPU cpu ON cpu.MaCPU = spct.MaCPU
        JOIN Ram ram ON ram.MaRAM = spct.MaRAM
        JOIN DungLuong dl ON dl.MaDungLuong = spct.MaDungLuong
        JOIN GPU gpu ON gpu.MaGPU = spct.MaGPU
        LEFT JOIN IMEI im 
            ON im.MaSPCT = spct.MaSPCT
            AND (im.TrangThai = 0 OR im.TrangThai IS NULL) -- chỉ đếm IMEI chưa bán
        GROUP BY 
            spct.MaSPCT, 
            spct.MaSP, 
            sp.TenSP, 
            cpu.TenCPU, 
            ram.DungLuong, 
            dl.DungLuong,
            gpu.TenGPU, 
            spct.Gia
        ORDER BY spct.MaSPCT
    """;

        try (Connection con = XJdbc.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("MaSPCT"),
                    rs.getString("MaSP"),
                    rs.getString("TenSP"),
                    rs.getString("TenCPU"),
                    rs.getString("DLR"),
                    rs.getString("DungLuong"),
                    rs.getString("TenGPU"),
                    rs.getInt("Gia"),
                    rs.getInt("SoLuong")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            Object obj = model.getValueAt(i, 3); // Thành tiền
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
//        txtTienGiam.setText(String.format("%,.0f VNĐ", tienGiam));
        txtTongTien.setText(String.format("%,.0f VNĐ", tongTien - tienGiam));
    }

    private void HienThiThongtin() {
        DefaultTableModel model = (DefaultTableModel) tblGioHang.getModel();
        int tong = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean checked = (Boolean) model.getValueAt(i, 4); // ✅ cột cuối là checkbox
            if (Boolean.TRUE.equals(checked)) {
                Object value = model.getValueAt(i, 3); // ✅ cột 3 là Thành tiền
                if (value != null) {
                    tong += Integer.parseInt(
                            value.toString().replace("VNĐ", "").replace(",", "").trim()
                    );
                }
            }

        }

        // Gán vào ô "Tổng tiền"
        txtTongTien.setText(String.valueOf(tong));
        updateTongTien();
    }
    private static final int MAX_PENDING_TOTAL = 5; // GIỚI HẠN TOÀN HỆ THỐNG = 5

    // ==== TẠO HÓA ĐƠN (3 lựa chọn khách) ====
    private void TaoDonHang() {
        // Giới hạn 5 hóa đơn chờ
        if (dao.countPending() >= 5) {
            JOptionPane.showMessageDialog(this,
                    "Đã đạt giới hạn 5 hóa đơn chờ. Vui lòng xử lý bớt trước khi tạo mới!");
            return;
        }

        String tenKH = txtKH.getText().trim();
        String sdt = txtSDT.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String email = txtEmail.getText().trim();

        // Trường hợp khách vãng lai
        if (tenKH.isEmpty() && sdt.isEmpty()) {
            tenKH = "Khách vãng lai";
            diaChi = "";
            email = "";
        }

        // MaKH null khi là khách vãng lai
        String maKH = null;
        KhachHangDao khDao = new KhachHangDaoImpl();

        // Nếu có SĐT -> tìm khách hàng cũ
        if (!sdt.isEmpty()) {
            KhachHang khExisting = khDao.findByPhone(sdt);
            if (khExisting != null) {
                maKH = khExisting.getMaKH(); // khách đã mua
            } else {
                // khách mới -> insert và lấy mã
                KhachHang kh = new KhachHang();
                kh.setTenKH(tenKH);
                kh.setSoDienThoai(sdt);
                kh.setDiaChi(diaChi);
                kh.setEmail(email.isEmpty() ? null : email);
                maKH = khDao.insertAndGetMaKH(kh);
            }
        }

        // Tạo hóa đơn
        HoaDon hd = new HoaDon();
        String maHD = dao.generateMaHD();
        hd.setMaHD(maHD);
        hd.setMaNV("NV1"); // Hoặc lấy từ nhân viên đăng nhập
        hd.setMaKH(maKH);  // null nếu vãng lai
        hd.setTenKHNhan(tenKH);
        hd.setSoDienThoaiNguoiNhan(sdt);
        hd.setDiaChiNguoiNhan(diaChi);
        hd.setThanhTien(0);
        hd.setTrangThai(0);
        hd.setNgayTao(new java.util.Date());

        String maHDInsert = dao.insertHoaDon(hd);
        if (maHDInsert == null) {
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn thất bại");
            return;
        }

        // Lưu lại để thanh toán
        this.maHDDangTao = maHDInsert;
        fillHoaDonCho();

        JOptionPane.showMessageDialog(this, "Đã tạo hóa đơn, mã: " + maHDInsert);

        // Xóa giỏ hàng và form để bắt đầu thêm sản phẩm
        clearCartAndForm();
    }

    private void thanhtoan() {
        if (maHDDangTao == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tạo/chọn hóa đơn chờ trước!");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblGioHang.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống!");
            return;
        }

        int tong = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String ttText = model.getValueAt(i, 3).toString()
                    .replace("VNĐ", "")
                    .replace(",", "")
                    .trim();
            tong += Integer.parseInt(ttText);
        }

        String idKM = (kmDangApDung != null) ? kmDangApDung.getIdKhuyenMai() : null;
        int loaiGiam = (kmDangApDung != null) ? kmDangApDung.getLoaiGiam() : 0;
        int giaTriGiam = 0;
        int thanhToan = Math.max(tong - giaTriGiam, 0);

        HoaDonChiTietDao hdctDao = new HoaDonChiTietDao();
        for (int i = 0; i < model.getRowCount(); i++) {
            String maSP = model.getValueAt(i, 0).toString();
            int soLuong = Integer.parseInt(model.getValueAt(i, 2).toString());
            int donGiaBan = Integer.parseInt(model.getValueAt(i, 3).toString()
                    .replace("VNĐ", "")
                    .replace(",", "")
                    .trim());

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

            HoaDonChiTiet hdct = new HoaDonChiTiet();
            hdct.setIdHDCT(hdctDao.generateMaHDCT());
            hdct.setMaHD(maHDDangTao);
            hdct.setMaSPCT(maSPCT);
            hdct.setSoLuong(soLuong);
            hdct.setDonGiaBan(donGiaBan);
            if (!hdctDao.insertHDCT(hdct)) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm HDCT cho sản phẩm: " + maSP);
            }
        }

        // ✅ Cập nhật trạng thái IMEI đã bán
        for (int i = 0; i < model.getRowCount(); i++) {
            String soIMEI = model.getValueAt(i, 1).toString(); // cột 1 = IMEI
            imeiDao.updateTrangThaiDaBan(soIMEI);
        }

        dao.updateHoaDonSauThanhToan(maHDDangTao, thanhToan, idKM, loaiGiam, giaTriGiam);

        JOptionPane.showMessageDialog(this, "Thanh toán thành công cho hóa đơn: " + maHDDangTao);

        maHDDangTao = null;
        fillHoaDonCho();
        fillToTable(); // ✅ Load lại bảng sản phẩm để số lượng giảm
        clearCartAndForm();
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
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn hủy đơn hàng này không?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        DefaultTableModel modelGioHang = (DefaultTableModel) tblGioHang.getModel();

        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            String maSP = modelGioHang.getValueAt(i, 0).toString();
            int soLuongTrongGio = Integer.parseInt(modelGioHang.getValueAt(i, 2).toString());

            // Trả IMEI đã giữ (nếu có)
            java.util.Set<String> imeis = cartImeis.remove(maSP);
            if (imeis != null && !imeis.isEmpty()) {
                // Nếu trước đó có hold:
                // imeiDao.releaseImeis(new java.util.ArrayList<>(imeis));
            }

            // Cộng lại tồn kho vào bảng sản phẩm (đúng cột 8)
            for (int j = 0; j < tblSanPham.getRowCount(); j++) {
                if (tblSanPham.getValueAt(j, 1).toString().equals(maSP)) {
                    int soLuongHienTai = Integer.parseInt(tblSanPham.getValueAt(j, 8).toString());
                    int soLuongMoi = soLuongHienTai + soLuongTrongGio;
                    tblSanPham.setValueAt(soLuongMoi, j, 8);
                    String maSPCT = tblSanPham.getValueAt(j, 0).toString();
                    spctdao.capNhatSoLuong(maSPCT, soLuongMoi);
                    break;
                }
            }
        }

        // Dọn giỏ + form
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
                //                item.getSoIMEI(),
                item.getSpct().getGia(),
                item.getSpct().getSoLuong()
            });
        }
    }

    private void capNhatTongTienVaGiamGia() {
        int tong = 0;
        int tongSoLuong = 0;

        DefaultTableModel model = (DefaultTableModel) tblGioHang.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            int soLuong = Integer.parseInt(model.getValueAt(i, 2).toString());
            int thanhTienHang = Integer.parseInt(model.getValueAt(i, 3).toString()
                    .replace("VNĐ", "").replace(",", "").trim());
            tong += thanhTienHang;      // ✅ chỉ cộng thành tiền
            tongSoLuong += soLuong;
        }

        double giam = apDungKhuyenMaiTuDong(tong, tongSoLuong);
        txtTongTien.setText(String.format("%,d VNĐ", (int) (tong - giam)));
    }

    private String ensureGuestCustomer() {
        KhachHangDao khDao = new KhachHangDaoImpl();

        // Tìm khách vãng lai đã có
        KhachHang guest = khDao.findGuest();
        if (guest != null && guest.getMaKH() != null) {
            return guest.getMaKH(); // ví dụ "KH006"
        }

        // Chưa có -> tạo bản ghi "Khách vãng lai" (SĐT null)
        KhachHang kh = new KhachHang();
        kh.setTenKH("Khách vãng lai");
        kh.setSoDienThoai(null);
        kh.setDiaChi(null);
        kh.setEmail(null);
        kh.setTrangThai(1);

        // insertAndGetMaKH sẽ sinh KHxxx và trả về
        return new KhachHangDaoImpl().insertAndGetMaKH(kh);
    }

    private void TaoDonHangForCustomer(String maKH, String tenKH, String sdt, String diaChi, String email) {
        HoaDon hd = new HoaDon();
        String maHD = dao.generateMaHD();
        hd.setMaHD(maHD);
        hd.setMaNV(nhanVienDangNhap != null ? nhanVienDangNhap.getMaNV() : "NV1");
        hd.setMaKH(maKH);
        hd.setTenKHNhan(tenKH);
        hd.setSoDienThoaiNguoiNhan(sdt);
        hd.setDiaChiNguoiNhan(diaChi);
        hd.setThanhTien(0);
        hd.setTrangThai(0);
        hd.setNgayTao(new java.util.Date());

        String maHDInsert = dao.insertHoaDon(hd);
        if (maHDInsert == null) {
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn thất bại");
            return;
        }

        this.maHDDangTao = maHDInsert;   // đánh dấu đang tạo
        // fill các ô text để user nhìn/ sửa nếu cần
        txtKH.setText(tenKH);
        txtSDT.setText(sdt == null ? "" : sdt);
        txtDiaChi.setText(diaChi == null ? "" : diaChi);
        txtEmail.setText(email == null ? "" : email);

        fillHoaDonCho();
        updateInvoiceUIState();
        refreshCheckoutState();
// bật nhập liệu + cho thêm sản phẩm
        JOptionPane.showMessageDialog(this, "Đã tạo hóa đơn: " + maHDInsert);
    }

    // trạng thái: có hóa đơn đang tạo (PENDING) không
//    private String maHDDangTao = null;
    private void updateInvoiceUIState() {
        boolean hasPending = (maHDDangTao != null);

        txtKH.setEnabled(hasPending);
        txtSDT.setEnabled(hasPending);
        txtDiaChi.setEnabled(hasPending);
        txtEmail.setEnabled(hasPending);

        tblSanPham.setEnabled(hasPending);
        tblGioHang.setEnabled(hasPending);
        btnXoa.setEnabled(hasPending);
        btnHuy.setEnabled(hasPending);

        // Nút “Tạo hóa đơn” chỉ bật khi KHÔNG có hóa đơn chờ
        btnTaoHoaDon.setEnabled(!hasPending);

        // Để refreshCheckoutState quyết định bật nút Thanh toán
        refreshCheckoutState();
    }

    private void clearCartAndForm() {
        // 1. Xóa toàn bộ giỏ hàng
        DefaultTableModel modelGioHang = (DefaultTableModel) tblGioHang.getModel();
        modelGioHang.setRowCount(0);

        // 2. Xóa thông tin khách hàng trên form
        txtKH.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        txtEmail.setText("");

        // 3. Reset tổng tiền (nếu có label hoặc field hiển thị tổng)
        try {
            txtTongTien.setText("0 VNĐ");
        } catch (Exception e) {
            // Nếu không có lblTongTien thì bỏ qua
        }

        // 4. Reset label mã hóa đơn (nếu có)
//    try {
//        txt.setText("—");
//    } catch (Exception e) {
//        // Nếu không có lblMaHD thì bỏ qua
//    }
        // 5. Reset biến hóa đơn đang tạo
        this.maHDDangTao = null;

        // 6. Cập nhật lại trạng thái nút và ô nhập
        updateInvoiceUIState();
    }

    private void refreshCheckoutState() {
        boolean hasPending = (maHDDangTao != null);
        String ten = txtKH.getText().trim();
        boolean isGuest = ten.equalsIgnoreCase("Khách vãng lai") || ten.equalsIgnoreCase("guest");

        boolean infoOk = hasPending && (isGuest
                || (!ten.isEmpty()
                && !txtSDT.getText().trim().isEmpty()
                && !txtDiaChi.getText().trim().isEmpty()));

        // Giỏ hàng: có ít nhất 1 dòng (hoặc ít nhất một dòng được tick, nếu bạn dùng checkbox chọn)
        DefaultTableModel m = (DefaultTableModel) tblGioHang.getModel();
        boolean cartOk = m.getRowCount() > 0;

        // Nếu bạn muốn yêu cầu “ít nhất 1 dòng được tick”, dùng đoạn dưới thay cho cartOk:
        // boolean cartOk = false;
        // for (int i = 0; i < m.getRowCount(); i++) {
        //     Object v = m.getValueAt(i, 4); // cột 4 là checkbox
        //     if (Boolean.TRUE.equals(v)) { cartOk = true; break; }
        // }
        btnThanhToan.setEnabled(infoOk && cartOk);
    }

    private void attachTextListeners() {
        javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                refreshCheckoutState();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                refreshCheckoutState();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                refreshCheckoutState();
            }
        };
        txtKH.getDocument().addDocumentListener(dl);
        txtSDT.getDocument().addDocumentListener(dl);
        txtDiaChi.getDocument().addDocumentListener(dl);
    }

    private int safeToInt(Object o) {
        try {
            if (o == null) {
                return 0;
            }
            if (o instanceof Number) {
                return ((Number) o).intValue();
            }
            String s = o.toString().replace("VNĐ", "").replace(",", "").trim();
            if (s.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private String formatVND(int v) {
        return String.format("%,d VNĐ", v);
    }
    // ==== Dialog chọn IMEI còn hàng ====

    private static class ImeiPickerDialog extends javax.swing.JDialog {

        private final javax.swing.table.DefaultTableModel model;
        private final java.util.List<String> result = new java.util.ArrayList<>();

        ImeiPickerDialog(java.awt.Frame owner, java.util.List<String> imeis, java.util.Set<String> exclude) {
            super(owner, "Chọn IMEI", true);
            setSize(420, 500);
            setLocationRelativeTo(owner);

            model = new javax.swing.table.DefaultTableModel(new Object[]{"Chọn", "IMEI"}, 0) {
                @Override
                public Class<?> getColumnClass(int c) {
                    return c == 0 ? Boolean.class : String.class;
                }

                @Override
                public boolean isCellEditable(int r, int c) {
                    return c == 0;
                }
            };
            javax.swing.JTable table = new javax.swing.JTable(model);
            table.setRowHeight(24);

            for (String im : imeis) {
                if (exclude != null && exclude.contains(im)) {
                    continue; // bỏ IMEI đã chọn trước đó
                }
                model.addRow(new Object[]{false, im});
            }

            javax.swing.JButton ok = new javax.swing.JButton("Chọn");
            javax.swing.JButton cancel = new javax.swing.JButton("Hủy");
            ok.addActionListener(e -> {
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (Boolean.TRUE.equals(model.getValueAt(i, 0))) {
                        result.add(model.getValueAt(i, 1).toString());
                    }
                }
                dispose();
            });
            cancel.addActionListener(e -> {
                result.clear();
                dispose();
            });

            javax.swing.JPanel south = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
            south.add(cancel);
            south.add(ok);

            getContentPane().setLayout(new java.awt.BorderLayout());
            getContentPane().add(new javax.swing.JScrollPane(table), java.awt.BorderLayout.CENTER);
            getContentPane().add(south, java.awt.BorderLayout.SOUTH);
        }

        java.util.List<String> getSelectedImeis() {
            return result;
        }
    }

    private void finalizeImeiOnPayment(String maHD) {
        for (var e : cartImeis.entrySet()) {
            java.util.Set<String> imeis = e.getValue();
            if (imeis == null || imeis.isEmpty()) {
                continue;
            }
            // Nếu bạn đã hold trước đó, giờ chuyển sang sold:
            // imeiDao.markSoldImeis(new java.util.ArrayList<>(imeis), maHD);
        }
        cartImeis.clear();
    }

    private void validatePhoneNumber() {
        String phoneNumber = txtSDT.getText().trim();

        // Khởi tạo đối tượng KhachHangDao và gọi phương thức findByPhone
        KhachHangDao khachHangDao = new KhachHangDaoImpl();
        KhachHang kh = khachHangDao.findByPhone(phoneNumber);  // Kiểm tra số điện thoại

        if (kh != null) {
            JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại. Vui lòng nhập lại.");
            return;  // Ngừng xử lý nếu có trùng số điện thoại
        }

        // Tiếp tục xử lý phần còn lại nếu không có trùng
        // Ví dụ: Tạo khách hàng mới hoặc cập nhật thông tin
        String tenKH = txtKH.getText().trim();
        // Tiến hành lưu thông tin khách hàng mới vào cơ sở dữ liệu...
    }

}
