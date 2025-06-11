package AplikasiPerpus;

import java.util.List;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.JFileChooser;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Asus
 */
public class Jframe_Pinjam extends javax.swing.JFrame {

    DefaultTableModel tabelmodel;
    Connection con = null;
    Statement stat;
    ResultSet res;
    PreparedStatement pst = null;
    String rool_id = "";

    /**
     * Creates new form ProdukBuku
     */
    public Jframe_Pinjam() {
        initComponents();
        koneksi();
        datatojtable();

        cari_anggota.addActionListener(e -> {
            System.out.println("Tombol Cari Anggota diklik!");
            cariAnggota(); // Panggil metode pencarian anggota
        });

        // Tambahkan action listener setelah inisialisasi komponen
        btn_cariBuku1.addActionListener(e -> cariEksemplarBuku(txt_cariBuku1));
        btn_cariBuku2.addActionListener(e -> cariEksemplarBuku(txt_cariBuku2));
        btn_cariBuku3.addActionListener(e -> cariEksemplarBuku(txt_cariBuku3));
    }

    private void koneksi() {//begin
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/db_perpustakaan", "root", "");
            stat = con.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Koneksi Gagal: " + e.getMessage());
        }
    }

    private void Aturkolom() {
        TableColumn column;
        jTable_user.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        column = jTable_user.getColumnModel().getColumn(0); // Kolom checkbox
        column.setPreferredWidth(20);
        column = jTable_user.getColumnModel().getColumn(1);
        column.setPreferredWidth(100);
        column = jTable_user.getColumnModel().getColumn(2);
        column.setPreferredWidth(100);
        column = jTable_user.getColumnModel().getColumn(3);
        column.setPreferredWidth(140);
        column = jTable_user.getColumnModel().getColumn(4);
        column.setPreferredWidth(116);
        column = jTable_user.getColumnModel().getColumn(5);
        column.setPreferredWidth(100);
        column = jTable_user.getColumnModel().getColumn(6);
        column.setPreferredWidth(100);
        column = jTable_user.getColumnModel().getColumn(7);
        column.setPreferredWidth(150);
        column = jTable_user.getColumnModel().getColumn(8);
        column.setPreferredWidth(150);
        column = jTable_user.getColumnModel().getColumn(9); // Kolom status
        column.setPreferredWidth(80);

    }

  private void datatojtable() {
    DefaultTableModel tb = new DefaultTableModel();
    tb.addColumn("Pilih"); // Kolom checkbox
    tb.addColumn("ID Peminjam");
    tb.addColumn("Nama Anggota");
    tb.addColumn("Nama Buku");
    tb.addColumn("Kode Eksemplar");
    tb.addColumn("Jumlah Pinjam");
    tb.addColumn("Nominal Denda");
    tb.addColumn("Tanggal Pinjam");
    tb.addColumn("Tanggal Kembali");
    tb.addColumn("Status");

    jTable_user.setModel(tb);

    try {
        String query = "SELECT a.id_pinjam, d.Nama_anggota, b.NamaBuku, c.KodeEksemplar, "
                + "a.jumlah_pinjam, a.nominal_denda, a.tgl_pinjam, a.tgl_kembali, "
                + "COALESCE(e.status, '$') AS status "
                + "FROM tpeminjam a "
                + "JOIN tmasterbuku b ON a.id_buku = b.id_buku "
                + "JOIN teksemplar c ON a.idEkseplar = c.IdEkseplar "
                + "JOIN tanggota d ON a.id_anggota = d.Id_anggota "
                + "JOIN teksemplar e ON a.idEkseplar = e.IdEkseplar order by a.id_pinjam";

        res = stat.executeQuery(query);
        while (res.next()) {
            tb.addRow(new Object[]{
                false, // Inisialisasi checkbox sebagai false
                res.getString("id_pinjam"),
                res.getString("Nama_anggota"),
                res.getString("NamaBuku"),
                res.getString("KodeEksemplar"),
                res.getString("jumlah_pinjam"),
                res.getDouble("nominal_denda"),
                res.getString("tgl_pinjam"),
                res.getString("tgl_kembali"),
                res.getString("status")
            });
        }
        Aturkolom();

        // Konfigurasi kolom checkbox
        jTable_user.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        jTable_user.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setSelected((Boolean) value); // Set status checkbox
                return checkBox;
            }
        });

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
    }
}
    private void bersihkantextfiled() {
        txt_pinjam.setText("");
        txt_anggota.setText("");
        txt_jumPinjam.setText("");
        txt_cariBuku1.setText("");;
        txt_cariBuku2.setText("");
        txt_cariBuku3.setText("");
        txt_pinjam.requestFocus();
    }

    private void cekdatauser() {
    try {
        if (txt_pinjam.getText().length() != 8) {
            JOptionPane.showMessageDialog(null, "Kode Peminjaman harus 8 digit", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txt_pinjam.requestFocus();
            return;
        }

        // Cek apakah ID Peminjam sudah digunakan oleh anggota lain
        String sqlCekAnggota = "SELECT id_anggota FROM tpeminjam WHERE id_pinjam = ?";
        PreparedStatement pstCekAnggota = con.prepareStatement(sqlCekAnggota);
        pstCekAnggota.setString(1, txt_pinjam.getText());
        ResultSet rsCekAnggota = pstCekAnggota.executeQuery();

        if (rsCekAnggota.next()) {
            String idAnggotaTerdaftar = rsCekAnggota.getString("id_anggota");
            String currentIdAnggota = txt_anggota.getText().trim().split("-")[0].trim();
            
            if (!idAnggotaTerdaftar.equals(currentIdAnggota)) {
                JOptionPane.showMessageDialog(null,
                        "ID Peminjam ini sudah digunakan oleh anggota lain!",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                txt_pinjam.setText("");
                txt_pinjam.requestFocus();
                return;
            }
            
            // Jika anggota sama, cek jumlah buku yang sudah dipinjam
            int jumlahBukuDipinjam = getJumlahBukuDipinjam(currentIdAnggota);
            if (jumlahBukuDipinjam >= 3) {
                JOptionPane.showMessageDialog(null,
                        "Anggota ini sudah meminjam 3 buku (maksimal)!",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String sqlcek = "SELECT a.*, d.Nama_anggota FROM tpeminjam a, tanggota d "
                + "WHERE a.id_anggota = d.Id_anggota AND a.id_pinjam=?";
        PreparedStatement pst = con.prepareStatement(sqlcek);
        pst.setString(1, txt_pinjam.getText());
        ResultSet rscek = pst.executeQuery();

        if (rscek.next()) {
            txt_anggota.setText(rscek.getString("id_anggota") + " - " + rscek.getString("Nama_anggota"));
            txt_jumPinjam.setText(rscek.getString("jumlah_pinjam"));
            txt_anggota.requestFocus();
        } else {
            JOptionPane.showMessageDialog(null, "Data peminjaman tidak ditemukan", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            txt_anggota.setText("");
            txt_jumPinjam.setText("");
            txt_anggota.requestFocus();
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    private void cariAnggota() {
        if (con == null) {
            koneksi(); // Pastikan koneksi database aktif
        }

        JFrame frameCari = new JFrame("Pilih Anggota");
        frameCari.setSize(800, 500);
        frameCari.setLocationRelativeTo(this);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Anggota");
        model.addColumn("Nama Anggota");
        model.addColumn("Alamat");
        model.addColumn("Fakultas");
        model.addColumn("Prodi");
        model.addColumn("Jenis Kelamin");

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            String query = "SELECT Id_anggota, Nama_anggota, alamat, fakultas, prodi, kelamin FROM tanggota";
            ResultSet rs = stat.executeQuery(query);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("Id_anggota"),
                    rs.getString("Nama_anggota"),
                    rs.getString("alamat"),
                    rs.getString("fakultas"),
                    rs.getString("prodi"),
                    rs.getString("kelamin")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frameCari, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JButton btnPilih = new JButton("Pilih");
        btnPilih.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String idAnggota = (String) table.getValueAt(selectedRow, 0);
                txt_anggota.setText(idAnggota); // Isi field ID Anggota
                frameCari.dispose(); // Tutup dialog pencarian
            } else {
                JOptionPane.showMessageDialog(frameCari, "Pilih anggota terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPilih, BorderLayout.SOUTH);

        frameCari.add(panel);
        frameCari.setVisible(true);
    }

    private boolean isAnggotaValid(String idAnggota) {
        try {
            String sql = "SELECT * FROM tanggota WHERE Id_anggota = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, idAnggota);
            ResultSet rs = pst.executeQuery();

            return rs.next(); // Jika ada data, maka anggota valid
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void InserUpdate() {
    try {
        // Validasi input
        if (!validasiDataSebelumInsertUpdate()) {
            return;
        }
        String idPinjam = txt_pinjam.getText().trim();
        String idAnggota = txt_anggota.getText().split("-")[0].trim();
        int jumlahPinjam = Integer.parseInt(txt_jumPinjam.getText());

        // Cek apakah anggota valid
        if (!isAnggotaValid(idAnggota)) {
            JOptionPane.showMessageDialog(this, "ID Anggota tidak valid!");
            return;
        }

        // Cek jumlah buku yang sedang dipinjam
        int totalBukuDipinjam = getJumlahBukuDipinjam(idAnggota);
        if (totalBukuDipinjam + jumlahPinjam > 3) {
            JOptionPane.showMessageDialog(this,
                    "Anggota ini sudah meminjam " + totalBukuDipinjam + " buku.\nMaksimal peminjaman adalah 3 buku per anggota.");
            return;
        }

        // Ambil config denda
        String configQuery = "SELECT nominal_denda, lama_peminjaman, id_config FROM tconfig LIMIT 1";
        ResultSet rsConfig = stat.executeQuery(configQuery);
        int dendaPerBuku = 10000; // default
        int lamaPinjamHari = 7; // default
        String idConfig = "1"; // default
        if (rsConfig.next()) {
            dendaPerBuku = rsConfig.getInt("nominal_denda");
            lamaPinjamHari = rsConfig.getInt("lama_peminjaman");
            idConfig = rsConfig.getString("id_config");
        }

        // Cek apakah data dengan id_pinjam sudah ada
        String sqlCheck = "SELECT id_anggota FROM tpeminjam WHERE id_pinjam = ? LIMIT 1";
        PreparedStatement pstCheck = con.prepareStatement(sqlCheck);
        pstCheck.setString(1, idPinjam);
        ResultSet rsu = pstCheck.executeQuery();
        boolean isExistingLoan = rsu.next();

        if (isExistingLoan) {
            String idAnggotaTerdaftar = rsu.getString("id_anggota");
            if (!idAnggotaTerdaftar.equals(idAnggota)) {
                JOptionPane.showMessageDialog(this,
                        "ID Peminjam ini sudah digunakan oleh anggota lain!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sqlCount = "SELECT COUNT(*) as total FROM tpeminjam WHERE id_pinjam = ?";
            PreparedStatement pstCount = con.prepareStatement(sqlCount);
            pstCount.setString(1, idPinjam);
            ResultSet rsCount = pstCount.executeQuery();
            int bukuSudahDipinjam = 0;
            if (rsCount.next()) {
                bukuSudahDipinjam = rsCount.getInt("total");
            }
            if (bukuSudahDipinjam + jumlahPinjam > 3) {
                JOptionPane.showMessageDialog(this,
                        "ID Peminjam ini sudah memiliki " + bukuSudahDipinjam + " buku.\nGabungan dengan buku baru melebihi batas 3 buku per peminjaman.");
                return;
            }
        }

        List<String[]> bukuDipilih = new ArrayList<>();

        for (JTextField field : new JTextField[]{txt_cariBuku1, txt_cariBuku2, txt_cariBuku3}) {
            String value = field.getText().trim();
            if (!value.isEmpty()) {
                String[] parts = value.split("\\(");
                if (parts.length == 2) {
                    String namaBuku = parts[0].trim();
                    String kodeEksemplar = parts[1].replace(")", "").trim();
                    bukuDipilih.add(new String[]{namaBuku, kodeEksemplar});
                }
            }
        }

        if (bukuDipilih.size() != jumlahPinjam) {
            JOptionPane.showMessageDialog(this,
                    "Jumlah buku yang dipilih (" + bukuDipilih.size()
                            + ") harus sama dengan jumlah pinjam (" + jumlahPinjam + ")!");
            return;
        }

        con.setAutoCommit(false);

        for (String[] buku : bukuDipilih) {
            String namaBuku = buku[0];
            String kodeEksemplar = buku[1];

            String sqlBuku = "SELECT b.id_buku, e.IdEkseplar "
                    + "FROM tmasterbuku b JOIN teksemplar e ON b.id_buku = e.id_buku "
                    + "WHERE b.NamaBuku = ? AND e.KodeEksemplar = ? AND (e.status IS NULL OR e.status = 'V') FOR UPDATE";

            PreparedStatement pstBuku = con.prepareStatement(sqlBuku);
            pstBuku.setString(1, namaBuku);
            pstBuku.setString(2, kodeEksemplar);
            ResultSet rsBuku = pstBuku.executeQuery();

            if (!rsBuku.next()) {
                JOptionPane.showMessageDialog(this, "Eksemplar tidak tersedia: " + namaBuku + " (" + kodeEksemplar + ")");
                con.rollback();
                return;
            }

            String idBuku = rsBuku.getString("id_buku");
            int idEksemplar = rsBuku.getInt("IdEkseplar");

            String sqlInsert = "INSERT INTO tpeminjam (id_pinjam, id_anggota, id_buku, idEkseplar, jumlah_pinjam, nominal_denda, tgl_pinjam, tgl_kembali, id_config) "
                    + "VALUES (?, ?, ?, ?, 1, ?, NOW(), DATE_ADD(NOW(), INTERVAL ? DAY), ?)";
            PreparedStatement pstInsert = con.prepareStatement(sqlInsert);
            pstInsert.setString(1, idPinjam);
            pstInsert.setString(2, idAnggota);
            pstInsert.setString(3, idBuku);
            pstInsert.setInt(4, idEksemplar);
            pstInsert.setInt(5, dendaPerBuku);
            pstInsert.setInt(6, lamaPinjamHari);
            pstInsert.setString(7, idConfig);
            pstInsert.executeUpdate();

            // Update status eksemplar menjadi `$`
            String sqlUpdateEksemplar = "UPDATE teksemplar SET status = '$' WHERE IdEkseplar = ?";
            PreparedStatement pstUpdateEksemplar = con.prepareStatement(sqlUpdateEksemplar);
            pstUpdateEksemplar.setInt(1, idEksemplar);
            pstUpdateEksemplar.executeUpdate();

            String sqlUpdateStok = "UPDATE tmasterbuku SET jumlah = jumlah - 1 WHERE id_buku = ?";
            PreparedStatement pstUpdateStok = con.prepareStatement(sqlUpdateStok);
            pstUpdateStok.setString(1, idBuku);
            pstUpdateStok.executeUpdate();
        }

        con.commit();
        JOptionPane.showMessageDialog(null,
                "Data berhasil disimpan!\n"
                        + jumlahPinjam + " buku telah dipinjam."
                        + (isExistingLoan ? "\n(Buku ditambahkan ke peminjaman yang sudah ada)" : ""));

    } catch (SQLException | NumberFormatException err) {
        try {
            con.rollback();
        } catch (SQLException ignored) {}
        JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
    } finally {
        try {
            con.setAutoCommit(true);
        } catch (SQLException ignored) {}
    }

    datatojtable();
    bersihkantextfiled();
}

    public void hapus_data() {
        if (JOptionPane.showConfirmDialog(null, "Apakah Yakin Akan di Hapus ?", "Informasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                String sqlDelete = "DELETE FROM tpeminjam WHERE id_pinjam = ?";
                PreparedStatement pstDelete = con.prepareStatement(sqlDelete);
                pstDelete.setString(1, txt_pinjam.getText().trim());
                pstDelete.executeUpdate();
                pstDelete.close();

                JOptionPane.showMessageDialog(this, "Data berhasil Di Hapus", "Success", JOptionPane.INFORMATION_MESSAGE);
                bersihkantextfiled();
                datatojtable();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Delete data gagal\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Kode User Batal Di Hapus");
            txt_pinjam.requestFocus();
        }
    }

    // Perbaiki method cariEksemplarBuku
    private void cariEksemplarBuku(JTextField targetField) {
        if (con == null) {
            koneksi();
        }

        JFrame frameCari = new JFrame("Pilih Eksemplar Buku");
        frameCari.setSize(800, 500);
        frameCari.setLocationRelativeTo(this);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Buku");
        model.addColumn("Nama Buku");
        model.addColumn("Kode Eksemplar");
        model.addColumn("Status");

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JTextField txtCari = new JTextField(20);
        JButton btnCari = new JButton("Cari");

        JPanel panelAtas = new JPanel();
        panelAtas.add(new JLabel("Cari: "));
        panelAtas.add(txtCari);
        panelAtas.add(btnCari);

        JButton btnPilih = new JButton("Pilih");

        frameCari.setLayout(new BorderLayout());
        frameCari.add(panelAtas, BorderLayout.NORTH);
        frameCari.add(scrollPane, BorderLayout.CENTER);
        frameCari.add(btnPilih, BorderLayout.SOUTH);

        // Hanya tampilkan eksemplar yang tersedia (status 'V' atau NULL)
        loadDataEksemplar(model, "", true);

        btnCari.addActionListener(e -> {
            loadDataEksemplar(model, txtCari.getText().trim(), true);
        });

        btnPilih.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String namaBuku = table.getValueAt(selectedRow, 1).toString();
                String kodeEksemplar = table.getValueAt(selectedRow, 2).toString();

                // Format: "Nama Buku (Kode Eksemplar)"
                targetField.setText(namaBuku + " (" + kodeEksemplar + ")");

                frameCari.dispose();

                if (targetField == txt_cariBuku1) {
                    txt_cariBuku2.requestFocus();
                } else if (targetField == txt_cariBuku2) {
                    txt_cariBuku3.requestFocus();
                } else {
                    btn_simpan.requestFocus();
                }
            } else {
                JOptionPane.showMessageDialog(frameCari, "Pilih eksemplar terlebih dahulu!");
            }
        });

        frameCari.setVisible(true);
    }

// Modifikasi method loadDataEksemplar
    private void loadDataEksemplar(DefaultTableModel model, String keyword, boolean hanyaTersedia) {
        model.setRowCount(0); // Clear existing data
        try {
            String query = "SELECT a.id_buku, a.NamaBuku, b.KodeEksemplar, COALESCE(b.status, 'V') AS Status "
                    + "FROM tmasterbuku a JOIN teksemplar b ON a.id_buku = b.id_buku "
                    + "WHERE (a.NamaBuku LIKE ? OR b.KodeEksemplar LIKE ?) "
                    + "AND (b.status IS NULL OR b.status = 'V') " // Hanya yang tersedia
                    + "ORDER BY a.id_buku, b.KodeEksemplar";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_buku"),
                    rs.getString("NamaBuku"),
                    rs.getString("KodeEksemplar"),
                    rs.getString("Status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private boolean validasiDataSebelumInsertUpdate() {
        if (txt_pinjam.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Peminjam harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txt_pinjam.requestFocus();
            return false;
        }

        if (txt_anggota.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Anggota harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txt_anggota.requestFocus();
            return false;
        }

        try {
            int jumlah = Integer.parseInt(txt_jumPinjam.getText());
            if (jumlah < 1 || jumlah > 3) {
                JOptionPane.showMessageDialog(this,
                        "Jumlah pinjam harus antara 1-3 buku!",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Jumlah pinjam harus berupa angka!",
                    "Error Input", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public void cekrool_id() {
        String namaBuku = txt_cariBuku1.getText().trim();

        if (namaBuku.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nama buku harus diisi!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            txt_cariBuku1.requestFocus();
            return;
        }

        try {
            // Ambil ID buku dari nama buku
            String sql = "SELECT id_buku FROM tmasterbuku WHERE NamaBuku = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, namaBuku);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                rool_id = rs.getString("id_buku");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Buku '" + namaBuku + "' tidak ditemukan dalam database!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                rool_id = "0";
                txt_cariBuku1.requestFocus();
            }
        } catch (SQLException err) {
            JOptionPane.showMessageDialog(this,
                    "Error database: " + err.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            err.printStackTrace();
        }
    }

    private boolean isIdPinjamExists(String idPinjam) throws SQLException {
        String query = "SELECT id_pinjam FROM tpeminjam WHERE id_pinjam = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, idPinjam);
        ResultSet rs = pst.executeQuery();
        return rs.next();
    }


    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan File Excel");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));
        fileChooser.setSelectedFile(new java.io.File("Data_Peminjam.xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            // Pastikan ekstensi .xlsx
            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                filePath += ".xlsx";
            }

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Data Peminjam");

                // Membuat header (tanpa kolom checkbox)
                Row headerRow = sheet.createRow(0);
                String[] headers = {"ID Peminjam", "Nama Anggota", "Nama Buku", "Kode Eksemplar",
                    "Jumlah Pinjam", "Nominal Denda", "Tanggal Pinjam", "Tanggal Kembali", "Status"};

                // Style untuk header
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                // Membuat sel header
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                // Mengisi data (mulai dari kolom 1, skip kolom 0/checkbox)
                DefaultTableModel model = (DefaultTableModel) jTable_user.getModel();
                for (int i = 0; i < model.getRowCount(); i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 1; j < model.getColumnCount(); j++) { // Mulai dari 1 untuk skip checkbox
                        Object value = model.getValueAt(i, j);
                        // Convert status symbol to meaningful text in Excel
                        if (j == 9) { // Status column (indeks asli 9, setelah skip checkbox jadi 8)
                            String status = value != null ? value.toString() : "";
                            row.createCell(j - 1).setCellValue("$".equals(status) ? "Dipinjam" : "V".equals(status) ? "Dikembalikan" : "");
                        } else {
                            row.createCell(j - 1).setCellValue(value != null ? value.toString() : "");
                        }
                    }
                }

                // Auto size kolom
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Menulis ke file
                try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                    workbook.write(outputStream);
                    JOptionPane.showMessageDialog(this, "Data berhasil diexport ke Excel!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Gagal mengeksport data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void exportRiwayatToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan File Excel Riwayat");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));
        fileChooser.setSelectedFile(new java.io.File("Riwayat_Pengembalian.xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                filePath += ".xlsx";
            }

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Riwayat Pengembalian");

                // Membuat header
                Row headerRow = sheet.createRow(0);
                String[] headers = {"ID Peminjam", "Nama Anggota", "Nama Buku", "Kode Eksemplar",
                    "Jumlah Pinjam", "Nominal Denda", "Tanggal Pinjam", "Tanggal Kembali", "Tanggal Dikembalikan"};

                // Style untuk header
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                // Membuat sel header
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                // Mengisi data
                try {
                    ResultSet rs = stat.executeQuery("SELECT h.id_pinjam, a.Nama_anggota, b.NamaBuku, e.KodeEksemplar, "
                            + "h.jumlah_pinjam, h.nominal_denda, h.tgl_pinjam, h.tgl_kembali, h.tgl_dikembalikan "
                            + "FROM thistory_pengembalian h "
                            + "JOIN tanggota a ON h.id_anggota = a.Id_anggota "
                            + "JOIN tmasterbuku b ON h.id_buku = b.id_buku "
                            + "JOIN teksemplar e ON h.idEkseplar = e.IdEkseplar "
                            + "ORDER BY h.tgl_dikembalikan DESC");

                    int rowNum = 1;
                    while (rs.next()) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(rs.getString("id_pinjam"));
                        row.createCell(1).setCellValue(rs.getString("Nama_anggota"));
                        row.createCell(2).setCellValue(rs.getString("NamaBuku"));
                        row.createCell(3).setCellValue(rs.getString("KodeEksemplar"));
                        row.createCell(4).setCellValue(rs.getInt("jumlah_pinjam"));
                        row.createCell(5).setCellValue(rs.getDouble("nominal_denda"));
                        row.createCell(6).setCellValue(rs.getString("tgl_pinjam"));
                        row.createCell(7).setCellValue(rs.getString("tgl_kembali"));
                        row.createCell(8).setCellValue(rs.getString("tgl_dikembalikan"));
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error mengambil data riwayat: " + e.getMessage());
                    return;
                }

                // Auto size kolom
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Menulis ke file
                try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                    workbook.write(outputStream);
                    JOptionPane.showMessageDialog(this, "Riwayat berhasil diexport ke Excel!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Gagal mengeksport riwayat: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private int getJumlahBukuDipinjam(String idAnggota) throws SQLException {
    String query = "SELECT COUNT(*) as total FROM tpeminjam WHERE id_anggota = ? AND (tgl_kembali IS NULL OR tgl_kembali > NOW())";
    PreparedStatement pst = con.prepareStatement(query);
    pst.setString(1, idAnggota);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
        return rs.getInt("total");
    }
    return 0;
}

   private void pindahkanKeHistory(String idPinjam, List<Integer> idEksemplarDipilih) throws SQLException {
    String sqlSelect = "SELECT * FROM tpeminjam WHERE id_pinjam = ?";
    if (!idEksemplarDipilih.isEmpty()) {
        sqlSelect += " AND idEkseplar IN (" + idEksemplarDipilih.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + ")";
    }

    PreparedStatement pstSelect = con.prepareStatement(sqlSelect);
    pstSelect.setString(1, idPinjam);
    ResultSet rs = pstSelect.executeQuery();

    String sqlInsert = "INSERT INTO thistory_pengembalian (id_pinjam, id_anggota, id_buku, idEkseplar, "
            + "jumlah_pinjam, nominal_denda, tgl_pinjam, tgl_kembali, tgl_dikembalikan) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

    PreparedStatement pstInsert = con.prepareStatement(sqlInsert);

    while (rs.next()) {
        pstInsert.setString(1, rs.getString("id_pinjam"));
        pstInsert.setString(2, rs.getString("id_anggota"));
        pstInsert.setString(3, rs.getString("id_buku"));
        pstInsert.setInt(4, rs.getInt("idEkseplar"));
        pstInsert.setInt(5, rs.getInt("jumlah_pinjam"));
        
        // Gunakan nominal_denda yang sudah diupdate
        pstInsert.setInt(6, rs.getInt("nominal_denda"));
        
        pstInsert.setTimestamp(7, rs.getTimestamp("tgl_pinjam"));
        pstInsert.setTimestamp(8, rs.getTimestamp("tgl_kembali"));
        pstInsert.executeUpdate();
    }

    String sqlDelete = "DELETE FROM tpeminjam WHERE id_pinjam = ?";
    if (!idEksemplarDipilih.isEmpty()) {
        sqlDelete += " AND idEkseplar IN (" + idEksemplarDipilih.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + ")";
    }

    PreparedStatement pstDelete = con.prepareStatement(sqlDelete);
    pstDelete.setString(1, idPinjam);
    pstDelete.executeUpdate();
}

    private void hapusDataTerpilih(boolean hapusSemua) {
        DefaultTableModel model = (DefaultTableModel) jTable_user.getModel();
        String idPinjam = txt_pinjam.getText().trim();

        if (idPinjam.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data peminjaman yang akan dihapus!");
            return;
        }

        try {
            if (hapusSemua) {
                // Hapus semua data dengan ID Peminjam ini
                // Pertama pindahkan ke history
                pindahkanKeHistory(idPinjam, new ArrayList<>());

                JOptionPane.showMessageDialog(this, "Semua data peminjaman dengan ID " + idPinjam + " berhasil dihapus!");
            } else {
                // Hapus data terpilih saja
                ArrayList<Integer> idEksemplarTerpilih = new ArrayList<>();

                for (int i = 0; i < model.getRowCount(); i++) {
                    Boolean isSelected = (Boolean) model.getValueAt(i, 0);
                    if (isSelected) {
                        String kodeEksemplar = model.getValueAt(i, 4).toString();

                        // Ambil ID Eksemplar berdasarkan kode
                        String sql = "SELECT IdEkseplar FROM teksemplar WHERE KodeEksemplar = ?";
                        PreparedStatement pst = con.prepareStatement(sql);
                        pst.setString(1, kodeEksemplar);
                        ResultSet rs = pst.executeQuery();

                        if (rs.next()) {
                            idEksemplarTerpilih.add(rs.getInt("IdEkseplar"));
                        }
                    }
                }

                if (idEksemplarTerpilih.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tidak ada buku yang dipilih untuk dihapus!");
                    return;
                }

                // Pindahkan ke history
                pindahkanKeHistory(idPinjam, idEksemplarTerpilih);

                JOptionPane.showMessageDialog(this, idEksemplarTerpilih.size() + " buku berhasil dihapus dari peminjaman!");
            }

            // Refresh data
            datatojtable();
            bersihkantextfiled();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_pinjam = new javax.swing.JTextField();
        txt_anggota = new javax.swing.JTextField();
        txt_jumPinjam = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_user = new javax.swing.JTable();
        btn_simpan = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        btn_Keluar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        btn_report = new javax.swing.JButton();
        btn_kembalikan = new javax.swing.JButton();
        btn_riwayat = new javax.swing.JButton();
        btn_cariBuku1 = new javax.swing.JButton();
        btn_cariBuku2 = new javax.swing.JButton();
        btn_cariBuku3 = new javax.swing.JButton();
        txt_cariBuku1 = new javax.swing.JTextField();
        txt_cariBuku2 = new javax.swing.JTextField();
        txt_cariBuku3 = new javax.swing.JTextField();
        cari_anggota = new javax.swing.JButton();
        txt_peminjam = new javax.swing.JTextField();
        btn_peminjam = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Swis721 Blk BT", 0, 36)); // NOI18N
        jLabel1.setText("Peminjaman Buku");

        jLabel2.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel2.setText("Id Pinjam");

        jLabel3.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel3.setText("Id Anggota");

        jLabel6.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel6.setText("Jumlah Pinjam");

        txt_pinjam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_pinjamKeyPressed(evt);
            }
        });

        txt_anggota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_anggotaKeyPressed(evt);
            }
        });

        txt_jumPinjam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_jumPinjamKeyPressed(evt);
            }
        });

        jTable_user.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable_user.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_userMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_user);

        btn_simpan.setBackground(new java.awt.Color(204, 204, 255));
        btn_simpan.setForeground(new java.awt.Color(51, 0, 255));
        btn_simpan.setText("Simpan");
        btn_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpanActionPerformed(evt);
            }
        });

        btn_hapus.setBackground(new java.awt.Color(204, 204, 255));
        btn_hapus.setForeground(new java.awt.Color(0, 0, 255));
        btn_hapus.setText("Hapus");
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        btn_Keluar.setBackground(new java.awt.Color(204, 204, 255));
        btn_Keluar.setForeground(new java.awt.Color(51, 51, 255));
        btn_Keluar.setText("Keluar");
        btn_Keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_KeluarActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel7.setText("Nama Buku");

        btn_report.setBackground(new java.awt.Color(204, 204, 255));
        btn_report.setForeground(new java.awt.Color(0, 0, 255));
        btn_report.setText("Cetak");
        btn_report.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_reportActionPerformed(evt);
            }
        });

        btn_kembalikan.setBackground(new java.awt.Color(204, 204, 255));
        btn_kembalikan.setForeground(new java.awt.Color(0, 0, 255));
        btn_kembalikan.setText("Kembalikan Buku");
        btn_kembalikan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_kembalikanActionPerformed(evt);
            }
        });

        btn_riwayat.setBackground(new java.awt.Color(204, 204, 255));
        btn_riwayat.setForeground(new java.awt.Color(0, 0, 255));
        btn_riwayat.setText("Riwayat Pengembalian");
        btn_riwayat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_riwayatActionPerformed(evt);
            }
        });

        btn_cariBuku1.setText("Cari");

        btn_cariBuku2.setText("Cari");

        btn_cariBuku3.setText("Cari");

        txt_cariBuku1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_cariBuku1KeyPressed(evt);
            }
        });

        txt_cariBuku2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_cariBuku2KeyPressed(evt);
            }
        });

        txt_cariBuku3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_cariBuku3KeyPressed(evt);
            }
        });

        cari_anggota.setText("Cari");

        btn_peminjam.setText("Cari Peminjam");
        btn_peminjam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_peminjamActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel2))
                                    .addGap(66, 66, 66)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txt_anggota)
                                        .addComponent(txt_pinjam)))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel6)
                                    .addGap(24, 24, 24)
                                    .addComponent(txt_jumPinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(txt_cariBuku1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(txt_cariBuku2, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_cariBuku3, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btn_cariBuku1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btn_cariBuku2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btn_cariBuku3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cari_anggota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(100, 100, 100)
                                .addComponent(txt_peminjam, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_peminjam))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(209, 209, 209)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 712, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(51, 51, 51)
                                .addComponent(btn_kembalikan)
                                .addGap(43, 43, 43)
                                .addComponent(btn_report, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(btn_Keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btn_riwayat))))
                .addContainerGap(141, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_pinjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_peminjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_peminjam))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(27, 27, 27))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_anggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cari_anggota))
                        .addGap(18, 18, 18)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_jumPinjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(btn_cariBuku1)
                    .addComponent(txt_cariBuku1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_cariBuku2)
                    .addComponent(txt_cariBuku2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_cariBuku3)
                    .addComponent(txt_cariBuku3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_kembalikan, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_report, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                .addComponent(btn_riwayat, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txt_pinjamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pinjamKeyPressed

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (txt_pinjam.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID Peminjam tidak boleh kosong!");
                return;
            }
            cekdatauser();
        }


    }//GEN-LAST:event_txt_pinjamKeyPressed

    private void txt_anggotaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_anggotaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            txt_jumPinjam.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_txt_anggotaKeyPressed

    private void txt_jumPinjamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jumPinjamKeyPressed
        // Validasi input hanya angka
        char c = evt.getKeyChar();
        if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
            evt.consume(); // Abaikan input yang bukan angka
            JOptionPane.showMessageDialog(this,
                    "Jumlah pinjam harus berupa angka!",
                    "Error Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String input = txt_jumPinjam.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Jumlah pinjam tidak boleh kosong!");
                return;
            }

            try {
                int jumlahPinjam = Integer.parseInt(input);
                if (jumlahPinjam < 1 || jumlahPinjam > 3) {
                    JOptionPane.showMessageDialog(this,
                            "Jumlah pinjam harus antara 1-3 buku!",
                            "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                txt_cariBuku1.requestFocus();
            } catch (NumberFormatException e) {
                // Seharusnya tidak terjadi karena sudah divalidasi di atas
                JOptionPane.showMessageDialog(this,
                        "Jumlah pinjam harus berupa angka!",
                        "Error Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_txt_jumPinjamKeyPressed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        // Validasi input
        if (txt_pinjam.getText().isEmpty() || txt_anggota.getText().isEmpty()
                || txt_jumPinjam.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
            return;
        }

        // Validasi jumlah pinjam
        int jumlahPinjam;
        try {
            jumlahPinjam = Integer.parseInt(txt_jumPinjam.getText());
            if (jumlahPinjam < 1 || jumlahPinjam > 3) {
                JOptionPane.showMessageDialog(this,
                        "Jumlah pinjam harus antara 1-3 buku!",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Jumlah pinjam harus berupa angka!",
                    "Error Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hitung jumlah buku yang dipilih
        int bukuTerpilih = 0;
        if (!txt_cariBuku1.getText().isEmpty()) {
            bukuTerpilih++;
        }
        if (!txt_cariBuku2.getText().isEmpty()) {
            bukuTerpilih++;
        }
        if (!txt_cariBuku3.getText().isEmpty()) {
            bukuTerpilih++;
        }

        if (bukuTerpilih != jumlahPinjam) {
            JOptionPane.showMessageDialog(this,
                    "Jumlah buku yang dipilih (" + bukuTerpilih
                    + ") harus sama dengan jumlah pinjam (" + jumlahPinjam + ")!");
            return;
        }

        // Proses peminjaman
        try {
            InserUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        String idPinjam = txt_pinjam.getText().trim();

        if (idPinjam.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data peminjaman yang akan dihapus!");
            return;
        }

        // Tampilkan pilihan
        Object[] options = {"Hapus Buku Terpilih", "Hapus Semua Buku", "Batal"};
        int choice = JOptionPane.showOptionDialog(this,
                "Pilih jenis penghapusan:",
                "Pilihan Penghapusan",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) { // Hapus buku terpilih
            hapusDataTerpilih(false);
        } else if (choice == 1) { // Hapus semua buku
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Apakah Anda yakin ingin menghapus SEMUA buku dalam peminjaman ini?",
                    "Konfirmasi Penghapusan",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                hapusDataTerpilih(true);
            }
        }
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void btn_KeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_KeluarActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin keluar?", "Konfirmasi keluar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();

        }
    }//GEN-LAST:event_btn_KeluarActionPerformed

    private void jTable_userMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_userMouseClicked

        int selectedRow = jTable_user.getSelectedRow();
        if (selectedRow >= 0) {
            txt_pinjam.setText(jTable_user.getValueAt(selectedRow, 1).toString()); // Kolom 1: ID Peminjam
            String namaAnggota = jTable_user.getValueAt(selectedRow, 2).toString(); // Kolom 2: Nama Anggota

            // Ambil ID anggota dari database berdasarkan nama
            try {
                String sql = "SELECT Id_anggota FROM tanggota WHERE Nama_anggota = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, namaAnggota);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    txt_anggota.setText(rs.getString("Id_anggota") + " - " + namaAnggota);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }

            txt_jumPinjam.setText(jTable_user.getValueAt(selectedRow, 5).toString()); // Kolom 5: Jumlah Pinjam
        }
    }//GEN-LAST:event_jTable_userMouseClicked

    private void btn_reportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_reportActionPerformed
        // TODO add your handling code here:
        exportToExcel();
    }//GEN-LAST:event_btn_reportActionPerformed

  private void kembalikanBuku(String idPinjam, boolean kembalikanSemua, List<Integer> idEksemplarDipilih) throws SQLException {
    con.setAutoCommit(false);
    try {
        int totalDenda = 0;
        
        // Hitung total denda terlebih dahulu
        for (Integer idEksemplar : idEksemplarDipilih) {
            totalDenda += hitungDendaForBook(idPinjam, idEksemplar);
        }
        
        // Proses pengembalian buku
        for (Integer idEksemplar : idEksemplarDipilih) {
            // Update status eksemplar menjadi 'V'
            String updateEksemplar = "UPDATE teksemplar SET status = 'V' WHERE IdEkseplar = ?";
            PreparedStatement pstUpdateEksemplar = con.prepareStatement(updateEksemplar);
            pstUpdateEksemplar.setInt(1, idEksemplar);
            pstUpdateEksemplar.executeUpdate();

            // Update stok buku
            String updateStok = "UPDATE tmasterbuku b JOIN teksemplar e ON b.id_buku = e.id_buku "
                    + "SET b.jumlah = b.jumlah + 1 WHERE e.IdEkseplar = ?";
            PreparedStatement pstUpdateStok = con.prepareStatement(updateStok);
            pstUpdateStok.setInt(1, idEksemplar);
            pstUpdateStok.executeUpdate();
            
            // Update nominal denda di tabel peminjaman
            String updateDenda = "UPDATE tpeminjam SET nominal_denda = ?, tgl_kembali = NOW() "
                    + "WHERE id_pinjam = ? AND idEkseplar = ?";
            PreparedStatement pstUpdateDenda = con.prepareStatement(updateDenda);
            pstUpdateDenda.setInt(1, hitungDendaForBook(idPinjam, idEksemplar));
            pstUpdateDenda.setString(2, idPinjam);
            pstUpdateDenda.setInt(3, idEksemplar);
            pstUpdateDenda.executeUpdate();
        }

        pindahkanKeHistory(idPinjam, idEksemplarDipilih);

        con.commit();

        String message = "Pengembalian berhasil!\n";
        if (totalDenda > 0) {
            message += "Total denda yang harus dibayar: Rp" + totalDenda;
        } else {
            message += "Tidak ada denda yang harus dibayar.";
        }
        
        JOptionPane.showMessageDialog(this, message);

    } catch (SQLException e) {
        con.rollback();
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    } finally {
        con.setAutoCommit(true);
    }

    datatojtable();
    bersihkantextfiled();
}
    

    private void tampilkanRiwayatPengembalian() {
        // Buat frame baru
        JFrame frameRiwayat = new JFrame("Riwayat Pengembalian");
        frameRiwayat.setSize(1000, 600);
        frameRiwayat.setLocationRelativeTo(this);

        // Buat model tabel
        DefaultTableModel tb = new DefaultTableModel();
        tb.addColumn("ID Peminjam");
        tb.addColumn("Nama Anggota");
        tb.addColumn("Nama Buku");
        tb.addColumn("Kode Eksemplar");
        tb.addColumn("Jumlah");
        tb.addColumn("Denda");
        tb.addColumn("Tgl Pinjam");
        tb.addColumn("Tgl Kembali");
        tb.addColumn("Tgl Dikembalikan");

        try {
            res = stat.executeQuery("SELECT h.id_pinjam, a.Nama_anggota, b.NamaBuku, e.KodeEksemplar, "
                    + "h.jumlah_pinjam, h.nominal_denda, h.tgl_pinjam, h.tgl_kembali, h.tgl_dikembalikan "
                    + "FROM thistory_pengembalian h "
                    + "JOIN tanggota a ON h.id_anggota = a.Id_anggota "
                    + "JOIN tmasterbuku b ON h.id_buku = b.id_buku "
                    + "JOIN teksemplar e ON h.idEkseplar = e.IdEkseplar "
                    + "ORDER BY h.tgl_dikembalikan DESC");

            while (res.next()) {
                tb.addRow(new Object[]{
                    res.getString("id_pinjam"),
                    res.getString("Nama_anggota"),
                    res.getString("NamaBuku"),
                    res.getString("KodeEksemplar"),
                    res.getString("jumlah_pinjam"),
                    res.getDouble("nominal_denda"),
                    res.getString("tgl_pinjam"),
                    res.getString("tgl_kembali"),
                    res.getString("tgl_dikembalikan")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading history: " + e.getMessage());
            return;
        }

        // Buat tabel
        JTable tableRiwayat = new JTable(tb);
        aturKolomRiwayat(tableRiwayat);

        // Buat panel untuk tombol
        JPanel panelTombol = new JPanel();
        JButton btnExport = new JButton("Export ke Excel");
        btnExport.setBackground(new java.awt.Color(204, 204, 255));
        btnExport.setForeground(new java.awt.Color(0, 0, 255));
        btnExport.addActionListener(e -> exportRiwayatToExcel());

        panelTombol.add(btnExport);

        // Tambahkan komponen ke frame
        frameRiwayat.setLayout(new BorderLayout());
        frameRiwayat.add(new JScrollPane(tableRiwayat), BorderLayout.CENTER);
        frameRiwayat.add(panelTombol, BorderLayout.SOUTH);

        frameRiwayat.setVisible(true);
    }

// Method untuk mengatur lebar kolom tabel riwayat
    private void aturKolomRiwayat(JTable table) {
        TableColumn column;
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        column = table.getColumnModel().getColumn(0); // ID Peminjam
        column.setPreferredWidth(100);

        column = table.getColumnModel().getColumn(1); // Nama Anggota
        column.setPreferredWidth(150);

        column = table.getColumnModel().getColumn(2); // Nama Buku
        column.setPreferredWidth(200);

        column = table.getColumnModel().getColumn(3); // Kode Eksemplar
        column.setPreferredWidth(120);

        column = table.getColumnModel().getColumn(4); // Jumlah
        column.setPreferredWidth(60);

        column = table.getColumnModel().getColumn(5); // Denda
        column.setPreferredWidth(100);

        column = table.getColumnModel().getColumn(6); // Tgl Pinjam
        column.setPreferredWidth(150);

        column = table.getColumnModel().getColumn(7); // Tgl Kembali
        column.setPreferredWidth(150);

        column = table.getColumnModel().getColumn(8); // Tgl Dikembalikan
        column.setPreferredWidth(150);
    }

// Method untuk menghitung denda
    private int hitungDenda(String idPinjam) throws SQLException {
        String configQuery = "SELECT lama_peminjaman, nominal_denda FROM tconfig LIMIT 1";
        ResultSet rsConfig = stat.executeQuery(configQuery);
        int lamaPinjamHari = 7;
        int dendaPerKelipatan = 10000;

        if (rsConfig.next()) {
            lamaPinjamHari = rsConfig.getInt("lama_peminjaman");
            dendaPerKelipatan = rsConfig.getInt("nominal_denda");
        }

        String query = "SELECT DATEDIFF(NOW(), tgl_kembali) AS hari_terlambat "
                + "FROM tpeminjam WHERE id_pinjam = ? AND tgl_kembali < NOW() LIMIT 1";

        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, idPinjam);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            int hariTerlambat = rs.getInt("hari_terlambat");

            if (hariTerlambat <= 0) {
                return 0;
            }

            // Hitung jumlah kelipatan batas waktu
            int jumlahKelipatan = (int) Math.ceil((double) hariTerlambat / lamaPinjamHari);

            return jumlahKelipatan * dendaPerKelipatan;
        }
        return 0;
    }

    public void prosesPengembalianBuku(String idPinjam) {
        try {
            String sql = "SELECT tgl_pinjam FROM tpeminjam WHERE id_pinjam = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, idPinjam);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Timestamp tglPinjam = rs.getTimestamp("tgl_pinjam");
                LocalDate tanggalPinjam = tglPinjam.toLocalDateTime().toLocalDate();
                LocalDate tanggalSekarang = LocalDate.now();

                long selisihHari = ChronoUnit.DAYS.between(tanggalPinjam, tanggalSekarang);
                int batasHari = 3;

                int denda = 0;
                if (selisihHari > batasHari) {
                    int hariTerlambat = (int) (selisihHari - batasHari);
                    denda = hariTerlambat * 10000;
                }

                // Update pengembalian
                String update = "UPDATE tpeminjam SET tgl_kembali = NOW(), nominal_denda = ? WHERE id_pinjam = ?";
                PreparedStatement pstUpdate = con.prepareStatement(update);
                pstUpdate.setInt(1, denda);
                pstUpdate.setString(2, idPinjam);
                pstUpdate.executeUpdate();

                JOptionPane.showMessageDialog(null, "Pengembalian berhasil. Denda: Rp " + denda);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menghitung denda: " + e.getMessage());
        }
    }

    private void tampilkanDialogPengembalian() {
    String idPinjam = txt_pinjam.getText().trim();

    if (idPinjam.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Pilih data peminjaman yang akan dikembalikan!");
        return;
    }

    try {
        // Tampilkan pilihan
        Object[] options = {"Kembalikan Buku Terpilih", "Kembalikan Semua Buku", "Batal"};
        int choice = JOptionPane.showOptionDialog(this,
                "Pilih jenis pengembalian:",
                "Pilihan Pengembalian",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 2) return; // Batal

        boolean kembalikanSemua = (choice == 1);
        List<Integer> idEksemplarDipilih = new ArrayList<>();
        int totalDenda = 0;

        DefaultTableModel model = (DefaultTableModel) jTable_user.getModel();

        if (!kembalikanSemua) {
            for (int i = 0; i < model.getRowCount(); i++) {
                Boolean isSelected = (Boolean) model.getValueAt(i, 0); // Kolom checkbox
                if (isSelected) {
                    String kodeEksemplar = model.getValueAt(i, 4).toString(); // Kolom kode eksemplar

                    String sql = "SELECT IdEkseplar FROM teksemplar WHERE KodeEksemplar = ?";
                    PreparedStatement pst = con.prepareStatement(sql);
                    pst.setString(1, kodeEksemplar);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        int idEksemplar = rs.getInt("IdEkseplar");
                        idEksemplarDipilih.add(idEksemplar);
                        totalDenda += hitungDendaForBook(idPinjam, idEksemplar);
                    }
                }
            }

            if (idEksemplarDipilih.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tidak ada buku yang dipilih untuk dikembalikan!");
                return;
            }
        } else {
            String sql = "SELECT IdEkseplar FROM tpeminjam WHERE id_pinjam = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, idPinjam);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int idEksemplar = rs.getInt("IdEkseplar");
                idEksemplarDipilih.add(idEksemplar);
                totalDenda += hitungDendaForBook(idPinjam, idEksemplar);
            }
        }

        // Buat pesan konfirmasi
        String message = "";
        if (kembalikanSemua) {
            message = "Anda akan mengembalikan SEMUA buku dalam peminjaman ini.\n";
        } else {
            message = "Anda akan mengembalikan " + idEksemplarDipilih.size() + " buku terpilih.\n";
        }

        // Tambahkan detail denda jika ada
        if (totalDenda > 0) {
            message += "\n=== DETAIL DENDA ===\n";
            
            // Dapatkan config denda
            String configQuery = "SELECT lama_peminjaman, nominal_denda FROM tconfig LIMIT 1";
            ResultSet rsConfig = stat.executeQuery(configQuery);
            int lamaPinjamHari = 7;
            int dendaPerKelipatan = 10000;
            
            if (rsConfig.next()) {
                lamaPinjamHari = rsConfig.getInt("lama_peminjaman");
                dendaPerKelipatan = rsConfig.getInt("nominal_denda");
            }
            
            message += "Batas peminjaman: " + lamaPinjamHari + " hari\n";
            message += "Denda per kelipatan: Rp" + dendaPerKelipatan + "\n";
            
            // Hitung detail keterlambatan untuk setiap buku
            for (Integer idEksemplar : idEksemplarDipilih) {
                String bukuQuery = "SELECT b.NamaBuku, e.KodeEksemplar, p.tgl_pinjam, " +
                                 "DATE_ADD(p.tgl_pinjam, INTERVAL ? DAY) as tgl_kembali_seharusnya " +
                                 "FROM tpeminjam p " +
                                 "JOIN tmasterbuku b ON p.id_buku = b.id_buku " +
                                 "JOIN teksemplar e ON p.idEkseplar = e.IdEkseplar " +
                                 "WHERE p.id_pinjam = ? AND p.idEkseplar = ?";
                
                PreparedStatement pstBuku = con.prepareStatement(bukuQuery);
                pstBuku.setInt(1, lamaPinjamHari);
                pstBuku.setString(2, idPinjam);
                pstBuku.setInt(3, idEksemplar);
                ResultSet rsBuku = pstBuku.executeQuery();
                
                if (rsBuku.next()) {
                    String namaBuku = rsBuku.getString("NamaBuku");
                    String kodeEksemplar = rsBuku.getString("KodeEksemplar");
                    Timestamp tglPinjam = rsBuku.getTimestamp("tgl_pinjam");
                    Timestamp tglKembaliSeharusnya = rsBuku.getTimestamp("tgl_kembali_seharusnya");
                    Timestamp tglSekarang = new Timestamp(System.currentTimeMillis());
                    
                    if (tglSekarang.after(tglKembaliSeharusnya)) {
                        long diffMillis = tglSekarang.getTime() - tglKembaliSeharusnya.getTime();
                        int hariTerlambat = (int) (diffMillis / (1000 * 60 * 60 * 24));
                        int jumlahKelipatan = (hariTerlambat + lamaPinjamHari - 1) / lamaPinjamHari;
                        int dendaBuku = jumlahKelipatan * dendaPerKelipatan;
                        
                        message += "\nBuku: " + namaBuku + " (" + kodeEksemplar + ")\n";
                        message += "Terlambat: " + hariTerlambat + " hari\n";
                        message += "Denda: Rp" + dendaBuku + "\n";
                    }
                }
            }
            
            message += "\nTOTAL DENDA: Rp" + totalDenda + "\n";
        } else {
            message += "Tidak ada denda yang harus dibayar.\n";
        }

        message += "\nLanjutkan pengembalian?";

        int confirm = JOptionPane.showConfirmDialog(
                this,
                message,
                "Konfirmasi Pengembalian",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            kembalikanBuku(idPinjam, kembalikanSemua, idEksemplarDipilih);
            bersihkantextfiled();
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}

private int hitungDendaForBook(String idPinjam, int idEksemplar) throws SQLException {
    String sql = "SELECT tgl_pinjam FROM tpeminjam WHERE id_pinjam = ? AND idEkseplar = ?";
    PreparedStatement pst = con.prepareStatement(sql);
    pst.setString(1, idPinjam);
    pst.setInt(2, idEksemplar);
    ResultSet rs = pst.executeQuery();
    
    if (rs.next()) {
        Timestamp tglPinjam = rs.getTimestamp("tgl_pinjam");
        LocalDate tanggalPinjam = tglPinjam.toLocalDateTime().toLocalDate();
        
        int lamaPinjamHari = getLoanDuration();
        int dendaPerKelipatan = getFinePerPeriod();
        
        LocalDate tanggalKembaliSeharusnya = tanggalPinjam.plusDays(lamaPinjamHari);
        LocalDate hariIni = LocalDate.now();
        
        long hariTerlambat = ChronoUnit.DAYS.between(tanggalKembaliSeharusnya, hariIni);
        
        if (hariTerlambat > 0) {
            int jumlahKelipatan = (int) ((hariTerlambat + lamaPinjamHari - 1) / lamaPinjamHari);
            return jumlahKelipatan * dendaPerKelipatan;
        }
    }
    return 0;
}

private int getLoanDuration() throws SQLException {
    String sql = "SELECT lama_peminjaman FROM tconfig LIMIT 1";
    ResultSet rs = stat.executeQuery(sql);
    if (rs.next()) {
        return rs.getInt("lama_peminjaman");
    }
    return 7; // default
}

private int getFinePerPeriod() throws SQLException {
    String sql = "SELECT nominal_denda FROM tconfig LIMIT 1";
    ResultSet rs = stat.executeQuery(sql);
    if (rs.next()) {
        return rs.getInt("nominal_denda");
    }
    return 10000; // default
}

private int[] getConfigValues() throws SQLException {
    String sql = "SELECT lama_peminjaman, nominal_denda FROM tconfig LIMIT 1";
    ResultSet rsConfig = stat.executeQuery(sql);
    int[] config = new int[]{7, 10000}; // Default values: 7 days, Rp10,000 per period
    if (rsConfig.next()) {
        config[0] = rsConfig.getInt("lama_peminjaman");
        config[1] = rsConfig.getInt("nominal_denda");
    }
    return config;
}
    private void btn_kembalikanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_kembalikanActionPerformed
                                      
    // Cek apakah ada baris yang dipilih
    int selectedRow = -1;
    for (int i = 0; i < jTable_user.getRowCount(); i++) {
        Boolean isSelected = (Boolean) jTable_user.getValueAt(i, 0);
        if (isSelected != null && isSelected) {
            selectedRow = i;
            break;
        }
    }

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih minimal satu buku untuk dikembalikan!");
        return;
    }

    String idPinjam = jTable_user.getValueAt(selectedRow, 1).toString();

    try {
        // Tampilkan pilihan pengembalian
        Object[] options = {"Kembalikan Buku Terpilih", "Kembalikan Semua Buku", "Batal"};
        int choice = JOptionPane.showOptionDialog(this,
                "Pilih jenis pengembalian:",
                "Pengembalian Buku",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == JOptionPane.CLOSED_OPTION || choice == 2) return;

        boolean kembalikanSemua = (choice == 1);
        List<Integer> idEksemplarDipilih = new ArrayList<>();
        int totalDenda = 0;

        // Ambil config denda dari database
        String configQuery = "SELECT lama_peminjaman, nominal_denda FROM tconfig LIMIT 1";
        ResultSet rsConfig = stat.executeQuery(configQuery);
        int lamaPinjamHari = 7; // default 7 hari
        int dendaPerKelipatan = 10000; // default Rp10.000 per kelipatan
        if (rsConfig.next()) {
            lamaPinjamHari = rsConfig.getInt("lama_peminjaman");
            dendaPerKelipatan = rsConfig.getInt("nominal_denda");
        }

        DefaultTableModel model = (DefaultTableModel) jTable_user.getModel();

        // Kumpulkan buku yang akan dikembalikan
        if (!kembalikanSemua) {
            // Hanya buku yang dipilih checkbox
            for (int i = 0; i < model.getRowCount(); i++) {
                Boolean isSelected = (Boolean) model.getValueAt(i, 0);
                if (isSelected != null && isSelected) {
                    String kodeEksemplar = model.getValueAt(i, 4).toString();
                    
                    // Cari ID eksemplar berdasarkan kode
                    String sqlEksemplar = "SELECT IdEkseplar FROM teksemplar WHERE KodeEksemplar = ?";
                    PreparedStatement pstEksemplar = con.prepareStatement(sqlEksemplar);
                    pstEksemplar.setString(1, kodeEksemplar);
                    ResultSet rsEksemplar = pstEksemplar.executeQuery();
                    
                    if (rsEksemplar.next()) {
                        int idEksemplar = rsEksemplar.getInt("IdEkseplar");
                        idEksemplarDipilih.add(idEksemplar);
                        
                        // Hitung denda untuk buku ini
                        String sqlPinjam = "SELECT tgl_pinjam, tgl_kembali FROM tpeminjam " +
                                         "WHERE id_pinjam = ? AND idEkseplar = ?";
                        PreparedStatement pstPinjam = con.prepareStatement(sqlPinjam);
                        pstPinjam.setString(1, idPinjam);
                        pstPinjam.setInt(2, idEksemplar);
                        ResultSet rsPinjam = pstPinjam.executeQuery();
                        
                        if (rsPinjam.next()) {
                            Timestamp tglPinjam = rsPinjam.getTimestamp("tgl_pinjam");
                            Timestamp tglKembali = rsPinjam.getTimestamp("tgl_kembali");
                            
                            // Jika tgl_kembali NULL, gunakan tanggal hari ini
                            LocalDate tanggalKembaliSeharusnya = (tglKembali != null) ? 
                                tglKembali.toLocalDateTime().toLocalDate() : 
                                LocalDate.now();
                                
                            LocalDate hariIni = LocalDate.now();
                            
                            // Hitung hari terlambat
                            long hariTerlambat = ChronoUnit.DAYS.between(tanggalKembaliSeharusnya, hariIni);
                            
                            if (hariTerlambat > 0) {
                                int jumlahKelipatan = (int) ((hariTerlambat + lamaPinjamHari - 1) / lamaPinjamHari);
                                int dendaBuku = jumlahKelipatan * dendaPerKelipatan;
                                totalDenda += dendaBuku;
                            }
                        }
                    }
                }
            }
            
            if (idEksemplarDipilih.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tidak ada buku yang dipilih untuk dikembalikan!");
                return;
            }
        } else {
            // Semua buku dalam peminjaman
            String sqlAll = "SELECT idEkseplar, tgl_pinjam, tgl_kembali FROM tpeminjam WHERE id_pinjam = ?";
            PreparedStatement pstAll = con.prepareStatement(sqlAll);
            pstAll.setString(1, idPinjam);
            ResultSet rsAll = pstAll.executeQuery();
            
            while (rsAll.next()) {
                int idEksemplar = rsAll.getInt("idEkseplar");
                idEksemplarDipilih.add(idEksemplar);
                
                Timestamp tglPinjam = rsAll.getTimestamp("tgl_pinjam");
                Timestamp tglKembali = rsAll.getTimestamp("tgl_kembali");
                
                // Hitung denda untuk buku ini
                LocalDate tanggalKembaliSeharusnya = (tglKembali != null) ? 
                    tglKembali.toLocalDateTime().toLocalDate() : 
                    LocalDate.now();
                    
                LocalDate hariIni = LocalDate.now();
                
                long hariTerlambat = ChronoUnit.DAYS.between(tanggalKembaliSeharusnya, hariIni);
                
                if (hariTerlambat > 0) {
                    int jumlahKelipatan = (int) ((hariTerlambat + lamaPinjamHari - 1) / lamaPinjamHari);
                    int dendaBuku = jumlahKelipatan * dendaPerKelipatan;
                    totalDenda += dendaBuku;
                }
            }
        }
        
        // Buat pesan konfirmasi dengan detail denda
        StringBuilder message = new StringBuilder();
        message.append("Anda akan mengembalikan ");
        if (kembalikanSemua) {
            message.append("SEMUA buku (").append(idEksemplarDipilih.size()).append(") dalam peminjaman ini.\n");
        } else {
            message.append(idEksemplarDipilih.size()).append(" buku terpilih.\n");
        }
        
        if (totalDenda >= 0) {
            message.append("\n=== DETAIL DENDA ===\n");
            message.append("Batas peminjaman: ").append(lamaPinjamHari).append(" hari\n");
            message.append("Denda per kelipatan: Rp").append(dendaPerKelipatan).append("\n");
            message.append("Total denda: Rp").append(totalDenda).append("\n");
        } else {
            message.append("\nTidak ada denda yang harus dibayar.\n");
        }
        
        message.append("\nLanjutkan pengembalian?");
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            message.toString(),
            "Konfirmasi Pengembalian",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Proses pengembalian
        con.setAutoCommit(false);
        try {
            // Update status eksemplar dan stok buku
            for (Integer idEksemplar : idEksemplarDipilih) {
                // Update status eksemplar menjadi 'V' (tersedia)
                String updateEksemplar = "UPDATE teksemplar SET status = 'V' WHERE IdEkseplar = ?";
                PreparedStatement pstUpdateEksemplar = con.prepareStatement(updateEksemplar);
                pstUpdateEksemplar.setInt(1, idEksemplar);
                pstUpdateEksemplar.executeUpdate();
                
                // Update stok buku
                String updateStok = "UPDATE tmasterbuku b JOIN teksemplar e ON b.id_buku = e.id_buku " +
                                  "SET b.jumlah = b.jumlah + 1 WHERE e.IdEkseplar = ?";
                PreparedStatement pstUpdateStok = con.prepareStatement(updateStok);
                pstUpdateStok.setInt(1, idEksemplar);
                pstUpdateStok.executeUpdate();
                
                // Update nominal denda di tabel peminjaman
                String updateDenda = "UPDATE tpeminjam SET nominal_denda = ?, tgl_kembali = NOW() " +
                                    "WHERE id_pinjam = ? AND idEkseplar = ?";
                PreparedStatement pstUpdateDenda = con.prepareStatement(updateDenda);
                
                // Hitung denda untuk buku ini lagi untuk memastikan konsistensi
                String sqlPinjam = "SELECT tgl_pinjam, tgl_kembali FROM tpeminjam " +
                                 "WHERE id_pinjam = ? AND idEkseplar = ?";
                PreparedStatement pstPinjam = con.prepareStatement(sqlPinjam);
                pstPinjam.setString(1, idPinjam);
                pstPinjam.setInt(2, idEksemplar);
                ResultSet rsPinjam = pstPinjam.executeQuery();
                
                int dendaBuku = 0;
                if (rsPinjam.next()) {
                    Timestamp tglPinjam = rsPinjam.getTimestamp("tgl_pinjam");
                    Timestamp tglKembali = rsPinjam.getTimestamp("tgl_kembali");
                    
                    LocalDate tanggalKembaliSeharusnya = (tglKembali != null) ? 
                        tglKembali.toLocalDateTime().toLocalDate() : 
                        LocalDate.now();
                        
                    LocalDate hariIni = LocalDate.now();
                    
                    long hariTerlambat = ChronoUnit.DAYS.between(tanggalKembaliSeharusnya, hariIni);
                    
                    if (hariTerlambat > 0) {
                        int jumlahKelipatan = (int) ((hariTerlambat + lamaPinjamHari - 1) / lamaPinjamHari);
                        dendaBuku = jumlahKelipatan * dendaPerKelipatan;
                    }
                }
                
                pstUpdateDenda.setInt(1, dendaBuku);
                pstUpdateDenda.setString(2, idPinjam);
                pstUpdateDenda.setInt(3, idEksemplar);
                pstUpdateDenda.executeUpdate();
            }
            
            // Pindahkan ke tabel history
            String sqlInsertHistory = "INSERT INTO thistory_pengembalian " +
                "(id_pinjam, id_anggota, id_buku, idEkseplar, jumlah_pinjam, nominal_denda, " +
                "tgl_pinjam, tgl_kembali, tgl_dikembalikan) " +
                "SELECT id_pinjam, id_anggota, id_buku, idEkseplar, jumlah_pinjam, nominal_denda, " +
                "tgl_pinjam, tgl_kembali, NOW() " +
                "FROM tpeminjam " +
                "WHERE id_pinjam = ? AND idEkseplar IN (" + 
                idEksemplarDipilih.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
            PreparedStatement pstInsertHistory = con.prepareStatement(sqlInsertHistory);
            pstInsertHistory.setString(1, idPinjam);
            pstInsertHistory.executeUpdate();
            
            // Hapus dari tabel peminjaman
            String sqlDelete = "DELETE FROM tpeminjam WHERE id_pinjam = ? AND idEkseplar IN (" +
                idEksemplarDipilih.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
            PreparedStatement pstDelete = con.prepareStatement(sqlDelete);
            pstDelete.setString(1, idPinjam);
            pstDelete.executeUpdate();
            
            con.commit();
            
            // Tampilkan pesan sukses
            String successMsg = "Pengembalian berhasil!\n";
            if (totalDenda > 0) {
                successMsg += "Total denda: Rp" + totalDenda;
            } else {
                successMsg += "Tidak ada denda yang harus dibayar.";
            }
            JOptionPane.showMessageDialog(this, successMsg);
            
            // Refresh data
            datatojtable();
            bersihkantextfiled();
            
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error rollback: " + ex.getMessage());
            }
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error set auto commit: " + e.getMessage());
            }
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
    }//GEN-LAST:event_btn_kembalikanActionPerformed

    private void btn_riwayatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_riwayatActionPerformed
        tampilkanRiwayatPengembalian();

    }//GEN-LAST:event_btn_riwayatActionPerformed

    private void txt_cariBuku1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_cariBuku1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            txt_cariBuku2.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_txt_cariBuku1KeyPressed

    private void txt_cariBuku2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_cariBuku2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            txt_cariBuku3.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_txt_cariBuku2KeyPressed

    private void txt_cariBuku3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_cariBuku3KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            btn_simpan.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_txt_cariBuku3KeyPressed
private void cariPeminjam(String idPinjam) {
    DefaultTableModel tb = new DefaultTableModel();
    tb.addColumn("Pilih");
    tb.addColumn("ID Peminjam");
    tb.addColumn("Nama Anggota");
    tb.addColumn("Nama Buku");
    tb.addColumn("Kode Eksemplar");
    tb.addColumn("Jumlah Pinjam");
    tb.addColumn("Nominal Denda");
    tb.addColumn("Tanggal Pinjam");
    tb.addColumn("Tanggal Kembali");
    tb.addColumn("Status");

    jTable_user.setModel(tb);

    try {
        String query = "SELECT a.id_pinjam, d.Nama_anggota, b.NamaBuku, c.KodeEksemplar, "
                + "a.jumlah_pinjam, a.nominal_denda, a.tgl_pinjam, a.tgl_kembali, "
                + "COALESCE(e.status, '$') AS status "
                + "FROM tpeminjam a "
                + "JOIN tmasterbuku b ON a.id_buku = b.id_buku "
                + "JOIN teksemplar c ON a.idEkseplar = c.IdEkseplar "
                + "JOIN tanggota d ON a.id_anggota = d.Id_anggota "
                + "JOIN teksemplar e ON a.idEkseplar = e.IdEkseplar "
                + "WHERE a.id_pinjam = ?";

        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, idPinjam);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            tb.addRow(new Object[]{
                false,
                rs.getString("id_pinjam"),
                rs.getString("Nama_anggota"),
                rs.getString("NamaBuku"),
                rs.getString("KodeEksemplar"),
                rs.getInt("jumlah_pinjam"),
                rs.getDouble("nominal_denda"),
                rs.getTimestamp("tgl_pinjam"),
                rs.getTimestamp("tgl_kembali"),
                rs.getString("status")
            });
        }

        Aturkolom();

        jTable_user.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        jTable_user.getColumnModel().getColumn(0).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected((Boolean) value);
            return checkBox;
        });

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error mencari peminjam: " + e.getMessage());
    }
}
    private void btn_peminjamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_peminjamActionPerformed
         String idPinjam = txt_peminjam.getText().trim();

    if (idPinjam.isEmpty()) {
        datatojtable(); // Tampilkan semua data jika field kosong
    } else {
        cariPeminjam(idPinjam); // Cari berdasarkan ID peminjam
    }

    // Kosongkan field setelah pencarian selesai
//    txt_peminjam.setText("");
    }//GEN-LAST:event_btn_peminjamActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Jframe_Pinjam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Jframe_Pinjam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Jframe_Pinjam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Jframe_Pinjam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Jframe_Pinjam().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Keluar;
    private javax.swing.JButton btn_cariBuku1;
    private javax.swing.JButton btn_cariBuku2;
    private javax.swing.JButton btn_cariBuku3;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_kembalikan;
    private javax.swing.JButton btn_peminjam;
    private javax.swing.JButton btn_report;
    private javax.swing.JButton btn_riwayat;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JButton cari_anggota;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_user;
    private javax.swing.JTextField txt_anggota;
    private javax.swing.JTextField txt_cariBuku1;
    private javax.swing.JTextField txt_cariBuku2;
    private javax.swing.JTextField txt_cariBuku3;
    private javax.swing.JTextField txt_jumPinjam;
    private javax.swing.JTextField txt_peminjam;
    private javax.swing.JTextField txt_pinjam;
    // End of variables declaration//GEN-END:variables
}
