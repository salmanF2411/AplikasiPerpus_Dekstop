package AplikasiPerpus;

import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.JFileChooser;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Asus
 */
public class Jframe_MasterBuku extends javax.swing.JFrame {

    DefaultTableModel tabelmodel;
    Connection con = null;
    Statement stat;
    ResultSet res;
    PreparedStatement pst = null;
    String rool_id = "";
    String Rak_Id = "";

    /**
     * Creates new form ProdukBuku
     */
    public Jframe_MasterBuku() {
        initComponents();
        koneksi();
        datatojtable();
        dataToComboBox();
        dataToComboBox1();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void koneksi() {//begin
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/db_perpustakaan", "root", "");
            stat = con.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Koneksi Gagal: " + e.getMessage());
        }
    }//end begin

    private void Aturkolom() {
        TableColumn column;
        jTable_user.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        column = jTable_user.getColumnModel().getColumn(0);
        column.setPreferredWidth(75);
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
        column.setPreferredWidth(75);
        column = jTable_user.getColumnModel().getColumn(7);
        column.setPreferredWidth(140);
        column = jTable_user.getColumnModel().getColumn(8);
        column.setPreferredWidth(140);
    }

    private void datatojtable() {
        DefaultTableModel tb = new DefaultTableModel();
        // Memberi nama pada setiap kolom tabel
        tb.addColumn("Kode Buku");
        tb.addColumn("Kode Kategori");
        tb.addColumn("Kode Rak");
        tb.addColumn("Nama Buku");
        tb.addColumn("Pengarang");
        tb.addColumn("Penerbit");
        tb.addColumn("Jumlah");
        tb.addColumn("Nama Kategori");
        tb.addColumn("Nama Rak");

        jTable_user.setModel(tb);

        try {
            // Perbaikan query dengan penyesuaian nama kolom yang tepat
            String query = "SELECT a.id_buku, a.Id_Kategori, a.Id_Rak, a.NamaBuku, "
                    + "a.Pengarang, a.Penerbit, a.jumlah, b.Nama_kategori, c.Nama_Rak "
                    + "FROM tmasterbuku a "
                    + "LEFT JOIN tkategori b ON a.Id_Kategori = b.Id_Kategori "
                    + "LEFT JOIN trak c ON a.Id_Rak = c.Id_Rak";

            res = stat.executeQuery(query);

            while (res.next()) {
                tb.addRow(new Object[]{
                    res.getString("id_buku"),
                    res.getString("Id_Kategori"),
                    res.getString("Id_Rak"),
                    res.getString("NamaBuku"),
                    res.getString("Pengarang"), // Diperbaiki urutannya sesuai dengan query
                    res.getString("Penerbit"),
                    res.getInt("jumlah"),
                    res.getString("Nama_kategori"),
                    res.getString("Nama_Rak")
                });
            }

            Aturkolom();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void dataToComboBox() {
        try {
            String sql = "SELECT * FROM tkategori";
            pst = con.prepareStatement(sql);
            res = pst.executeQuery();

            while (res.next()) {

                jComborole.addItem(res.getString("Nama_kategori"));
            }

            res.last();
            int jumlahdata = res.getRow();
            res.first();

        } catch (SQLException e) {
        }
    }//end method

    private void dataToComboBox1() {
        try {
            String sql = "SELECT * FROM trak";
            pst = con.prepareStatement(sql);
            res = pst.executeQuery();

            while (res.next()) {

                jComborole1.addItem(res.getString("Nama_Rak"));
            }

            res.last();
            int jumlahdata = res.getRow();
            res.first();

        } catch (SQLException e) {
        }
    }//end method

    private void bersihkantextfiled() {
        kode_buku.setText("");
        nama_produk.setText("");
        penerbit.setText("");
        pengarang.setText("");
        kode_buku.requestFocus();
        jText_jml.setText("");
    }

    private void cekdatauser() {
        try {

            if (kode_buku.getText().length() != 8) {
                JOptionPane.showMessageDialog(null, "Kode Buku Kurang dari 8 Digit");
                kode_buku.requestFocus();
            } else {
                String sqlcek = "select *from tmasterbuku where id_buku='" + kode_buku.getText() + "'";
                ResultSet rscek = stat.executeQuery(sqlcek);
                if (rscek.next()) {
                    nama_produk.setText(rscek.getString("NamaBuku"));
                    penerbit.setText(rscek.getString("Penerbit"));
                    pengarang.setText(rscek.getString("Pengarang"));
                    jText_jml.setText(rscek.getString("jumlah"));
                    nama_produk.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(null, "Kode Produk Tidak Di temukan");
                    nama_produk.setText("");
                    penerbit.setText("");
                    pengarang.setText("");
                    jText_jml.setText("");
                    nama_produk.requestFocus();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "GAGAL KETEMU");
        }
    }//akhir Methor

    public void InserUpdate() {
        try {
            String kodeBuku = kode_buku.getText().trim();
            String namaProduk = nama_produk.getText().trim();
            String penerbitText = penerbit.getText().trim();
            String pengarangText = pengarang.getText().trim();
            String jumlahText = jText_jml.getText().trim();

            if (kodeBuku.isEmpty() || namaProduk.isEmpty() || pengarangText.isEmpty()
                    || penerbitText.isEmpty() || jumlahText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (kodeBuku.length() != 8) {
                JOptionPane.showMessageDialog(null, "Kode Buku harus 8 digit!", "Error", JOptionPane.ERROR_MESSAGE);
                kode_buku.requestFocus();
                return;
            }

            int jumlahBaru = Integer.parseInt(jumlahText);

            String sqlCheck = "SELECT * FROM tmasterbuku WHERE id_buku = ?";
            PreparedStatement pstCheck = con.prepareStatement(sqlCheck);
            pstCheck.setString(1, kodeBuku);
            ResultSet rsu = pstCheck.executeQuery();

            if (rsu.next()) {
                boolean isDataChanged = !namaProduk.equals(rsu.getString("NamaBuku"))
                        || !penerbitText.equals(rsu.getString("penerbit"))
                        || !pengarangText.equals(rsu.getString("pengarang"));

                // Selalu update metadata buku
                String sqlUpdate = "UPDATE tmasterbuku SET NamaBuku=?, pengarang=?, penerbit=? WHERE id_buku=?";
                PreparedStatement pstUpdate = con.prepareStatement(sqlUpdate);
                pstUpdate.setString(1, namaProduk);
                pstUpdate.setString(2, pengarangText);
                pstUpdate.setString(3, penerbitText);
                pstUpdate.setString(4, kodeBuku);
                pstUpdate.executeUpdate();
                pstUpdate.close();

                // Jalankan update eksemplar
                updEksemplar();

            } else {
                // Data belum ada, lakukan insert
                int confirmInsert = JOptionPane.showConfirmDialog(null,
                        "Data belum ada. Simpan sebagai data baru?",
                        "Konfirmasi Simpan", JOptionPane.YES_NO_OPTION);

                if (confirmInsert == JOptionPane.YES_OPTION) {
                    if (rool_id.isEmpty() || Rak_Id.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Kategori dan Rak harus dipilih!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String SqlI = "INSERT INTO tmasterbuku (id_buku, NamaBuku, id_kategori, Id_Rak, pengarang, penerbit, jumlah) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstInsert = con.prepareStatement(SqlI);
                    pstInsert.setString(1, kodeBuku);
                    pstInsert.setString(2, namaProduk);
                    pstInsert.setString(3, rool_id);
                    pstInsert.setString(4, Rak_Id);
                    pstInsert.setString(5, pengarangText);
                    pstInsert.setString(6, penerbitText);
                    pstInsert.setInt(7, jumlahBaru);
                    pstInsert.executeUpdate();
                    pstInsert.close();

                    insEksemplar(); // Insert eksemplar baru

                    JOptionPane.showMessageDialog(null, "Data berhasil disimpan!",
                            "Simpan Berhasil", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            rsu.close();
            pstCheck.close();
            datatojtable();
            bersihkantextfiled();

        } catch (SQLException | NumberFormatException err) {
            JOptionPane.showMessageDialog(null, "Error: " + err.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            err.printStackTrace();
        }
    }

    public void hapus_data() {
    if (JOptionPane.showConfirmDialog(null, "Apakah Yakin Data Akan Dihapus ?", "Informasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        try {
            // Hapus data dari tabel eksemplar terlebih dahulu
            String sqlDeleteEksemplar = "DELETE FROM teksemplar WHERE id_buku = ?";
            PreparedStatement pstEksemplar = con.prepareStatement(sqlDeleteEksemplar);
            pstEksemplar.setString(1, kode_buku.getText().trim());
            pstEksemplar.executeUpdate();
            pstEksemplar.close();

            // Hapus data dari tabel master buku
            String sqlDelete = "DELETE FROM tmasterbuku WHERE id_buku = ?";
            PreparedStatement pstDelete = con.prepareStatement(sqlDelete);
            pstDelete.setString(1, kode_buku.getText().trim());
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
        kode_buku.requestFocus();
    }
}


    public void cekrool_id() {
        String roleid = jComborole.getSelectedItem().toString();

        try {
            // Query untuk mengambil Kode_Rak dari tabel trak
            String sql = "SELECT id_kategori FROM tkategori WHERE Nama_kategori = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, roleid);
            ResultSet rsid = pstmt.executeQuery();

            if (rsid.next()) {
                rool_id = rsid.getString("id_kategori"); // Simpan Kode_Rak ke variabel rool_id
            } else {
                rool_id = "0"; // Jika tidak ditemukan, set rool_id ke "0"
            }

            // Panggil method InserUpdate untuk insert atau update data
            InserUpdate();

        } catch (SQLException err) {
            JOptionPane.showMessageDialog(this, "Koneksi Gagal\n" + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cek_idRak() {
        String RakId = jComborole1.getSelectedItem().toString();

        try {
            // Query untuk mengambil Kode_Rak dari tabel trak
            String sql = "SELECT id_Rak FROM trak WHERE Nama_Rak = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, RakId);
            ResultSet rsid = pstmt.executeQuery();

            if (rsid.next()) {
                Rak_Id = rsid.getString("id_Rak"); // Simpan Kode_Rak ke variabel rool_id
            } else {
                Rak_Id = "0"; // Jika tidak ditemukan, set rool_id ke "0"
            }

            // Panggil method InserUpdate untuk insert atau update data
            InserUpdate();

        } catch (SQLException err) {
            JOptionPane.showMessageDialog(this, "Koneksi Gagal\n" + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insEksemplar() {
        java.util.Date tanggal = new java.util.Date();
        java.text.SimpleDateFormat setTanggal = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String TglNow = setTanggal.format(tanggal);

        try {
            String kodeBuku = kode_buku.getText().trim();
            int jumlah = Integer.parseInt(jText_jml.getText());

            int start = 1;
            String query = "SELECT COUNT(*) FROM teksemplar WHERE id_buku='" + kodeBuku + "'";
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    start = rs.getInt(1) + 1;
                }
            }

            for (int i = 0; i < jumlah; i++) {
                String kodeEks = kodeBuku + "." + (start + i);
                String sql = "INSERT INTO teksemplar (id_buku, KodeEksemplar, TglCreate, TglModify) "
                        + "VALUES ('" + kodeBuku + "', '" + kodeEks + "', '" + TglNow + "', '" + TglNow + "')";
                try (Statement stmt = con.createStatement()) {
                    stmt.executeUpdate(sql);
                }
            }

            JOptionPane.showMessageDialog(null, "Data eksemplar berhasil ditambahkan.");
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambahkan eksemplar: " + e.getMessage());
        }
    }

    private void updEksemplar() {
        java.util.Date tanggal = new java.util.Date();
        java.text.SimpleDateFormat setTanggal = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String TglNow = setTanggal.format(tanggal);
        String kodeBuku = kode_buku.getText().trim();

        int konfirmasi = JOptionPane.showConfirmDialog(null,
                "Apakah data stok akan ditambah [Yes] atau ubah stok [No]?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        try {
            int jumlahBaru = Integer.parseInt(jText_jml.getText());

            if (konfirmasi == JOptionPane.NO_OPTION) {
                // Reset semua eksemplar dan ganti dengan jumlah baru
                try (Statement stmt = con.createStatement()) {
                    stmt.executeUpdate("DELETE FROM teksemplar WHERE id_buku='" + kodeBuku + "'");
                }

                for (int i = 1; i <= jumlahBaru; i++) {
                    String kodeEks = kodeBuku + "." + i;
                    String sql = "INSERT INTO teksemplar (id_buku, KodeEksemplar, TglCreate, TglModify) "
                            + "VALUES (?, ?, ?, ?)";
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setString(1, kodeBuku);
                        pst.setString(2, kodeEks);
                        pst.setString(3, TglNow);
                        pst.setString(4, TglNow);
                        pst.executeUpdate();
                    }
                }

                // Update jumlah di tmasterbuku ke jumlahBaru
                String updateJumlah = "UPDATE tmasterbuku SET jumlah = ? WHERE id_buku = ?";
                try (PreparedStatement pst = con.prepareStatement(updateJumlah)) {
                    pst.setInt(1, jumlahBaru);
                    pst.setString(2, kodeBuku);
                    pst.executeUpdate();
                }

            } else {
                // Tambahkan eksemplar baru sesuai jumlah input
                int start = 1;
                String query = "SELECT COUNT(*) FROM teksemplar WHERE id_buku='" + kodeBuku + "'";
                try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                    if (rs.next()) {
                        start = rs.getInt(1) + 1;
                    }
                }

                for (int i = 0; i < jumlahBaru; i++) {
                    String kodeEks = kodeBuku + "." + (start + i);
                    String sql = "INSERT INTO teksemplar (id_buku, KodeEksemplar, TglCreate, TglModify) "
                            + "VALUES (?, ?, ?, ?)";
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setString(1, kodeBuku);
                        pst.setString(2, kodeEks);
                        pst.setString(3, TglNow);
                        pst.setString(4, TglNow);
                        pst.executeUpdate();
                    }
                }

                // Tambah jumlah di tmasterbuku
                String updateJumlah = "UPDATE tmasterbuku SET jumlah = jumlah + ? WHERE id_buku = ?";
                try (PreparedStatement pst = con.prepareStatement(updateJumlah)) {
                    pst.setInt(1, jumlahBaru);
                    pst.setString(2, kodeBuku);
                    pst.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(null, "Update data eksemplar berhasil.");

        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Gagal update eksemplar: " + e.getMessage());
        }
    }
    
    // Tambahkan method untuk export ke Excel
private void exportToExcel() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Simpan File Excel");
    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));
    fileChooser.setSelectedFile(new java.io.File("Data_Buku.xlsx"));
    
    int userSelection = fileChooser.showSaveDialog(this);
    
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        java.io.File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();
        
        // Pastikan ekstensi .xlsx
        if (!filePath.toLowerCase().endsWith(".xlsx")) {
            filePath += ".xlsx";
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data Buku");
            
            // Membuat header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Kode Buku", "Kode Kategori", "Kode Rak", "Nama Buku", 
                               "Pengarang", "Penerbit", "Jumlah", "Nama Kategori", "Nama Rak"};
            
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
            DefaultTableModel model = (DefaultTableModel) jTable_user.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    row.createCell(j).setCellValue(value != null ? value.toString() : "");
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        kode_buku = new javax.swing.JTextField();
        nama_produk = new javax.swing.JTextField();
        penerbit = new javax.swing.JTextField();
        pengarang = new javax.swing.JTextField();
        jText_jml = new javax.swing.JTextField();
        jComborole = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_user = new javax.swing.JTable();
        btn_simpan = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        btn_Keluar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jComborole1 = new javax.swing.JComboBox<>();
        btn_hapus1 = new javax.swing.JButton();
        btn_report = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 255));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Swis721 Blk BT", 0, 36)); // NOI18N
        jLabel1.setText("Produk Buku");

        jLabel2.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel2.setText("Kode Buku");

        jLabel3.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel3.setText("Nama Buku");

        jLabel4.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel4.setText("Penerbit");

        jLabel5.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel5.setText("Pengarang");

        jLabel6.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel6.setText("Jumlah");

        kode_buku.setBackground(new java.awt.Color(153, 153, 153));
        kode_buku.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kode_bukuKeyPressed(evt);
            }
        });

        nama_produk.setBackground(new java.awt.Color(153, 153, 153));
        nama_produk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nama_produkKeyPressed(evt);
            }
        });

        penerbit.setBackground(new java.awt.Color(153, 153, 153));
        penerbit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                penerbitKeyPressed(evt);
            }
        });

        pengarang.setBackground(new java.awt.Color(153, 153, 153));
        pengarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pengarangKeyPressed(evt);
            }
        });

        jText_jml.setBackground(new java.awt.Color(153, 153, 153));
        jText_jml.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jText_jmlKeyPressed(evt);
            }
        });

        jComborole.setForeground(new java.awt.Color(0, 0, 255));
        jComborole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pilih Kategori" }));
        jComborole.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboroleKeyPressed(evt);
            }
        });

        jTable_user.setBackground(new java.awt.Color(102, 102, 102));
        jTable_user.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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
        jLabel7.setText("Nama Kategori");

        jLabel8.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel8.setText("Nama Rak");

        jComborole1.setForeground(new java.awt.Color(0, 0, 204));
        jComborole1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pilih Rak" }));
        jComborole1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComborole1KeyPressed(evt);
            }
        });

        btn_hapus1.setBackground(new java.awt.Color(204, 204, 255));
        btn_hapus1.setForeground(new java.awt.Color(0, 0, 255));
        btn_hapus1.setText("View");
        btn_hapus1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus1ActionPerformed(evt);
            }
        });

        btn_report.setBackground(new java.awt.Color(255, 51, 51));
        btn_report.setForeground(new java.awt.Color(0, 0, 255));
        btn_report.setText("Cetak");
        btn_report.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_reportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 712, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(69, 69, 69)
                                .addComponent(btn_report, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btn_hapus1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(btn_Keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel4))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(kode_buku, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                                        .addComponent(penerbit)))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jComborole, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 3, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel3))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(nama_produk, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(pengarang, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(85, 85, 85))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComborole1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(88, 88, 88))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jText_jml, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))))
            .addGroup(layout.createSequentialGroup()
                .addGap(287, 287, 287)
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addGap(87, 87, 87)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(kode_buku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(nama_produk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(penerbit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(pengarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jText_jml, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComborole, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jComborole1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_hapus1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_report, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(75, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void kode_bukuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kode_bukuKeyPressed
        // TODO add your handling code here:
        String Kodebuku = kode_buku.getText();
        if (Kodebuku.isEmpty()) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
                nama_produk.requestFocus();   //panggil komponen yang akan di tuju
            }
        } else {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
                cekdatauser();
                //panggil komponen yang akan di tuju
            }  //end if
        }


    }//GEN-LAST:event_kode_bukuKeyPressed

    private void nama_produkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nama_produkKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            penerbit.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_nama_produkKeyPressed

    private void penerbitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_penerbitKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            pengarang.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_penerbitKeyPressed

    private void pengarangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pengarangKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            jText_jml.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_pengarangKeyPressed

    private void jText_jmlKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jText_jmlKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            jComborole.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_jText_jmlKeyPressed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        // Ambil nilai dari input fields
        String kodeBuku = kode_buku.getText().trim();
        String namaBuku = nama_produk.getText().trim();
        String penerbitText = penerbit.getText().trim();
        String pengarangText = pengarang.getText().trim();
        String jumlahText = jText_jml.getText().trim();
        String kategori = jComborole.getSelectedItem() != null ? jComborole.getSelectedItem().toString() : "";
        String rak = jComborole1.getSelectedItem() != null ? jComborole1.getSelectedItem().toString() : "";

        // Validasi input kosong
        if (kodeBuku.isEmpty() || namaBuku.isEmpty() || pengarangText.isEmpty()
                || penerbitText.isEmpty() || jumlahText.isEmpty() || kategori.isEmpty() || rak.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            if (kodeBuku.isEmpty()) {
                kode_buku.requestFocus();
            } else if (namaBuku.isEmpty()) {
                nama_produk.requestFocus();
            } else if (pengarangText.isEmpty()) {
                pengarang.requestFocus();
            } else if (penerbitText.isEmpty()) {
                penerbit.requestFocus();
            } else if (jumlahText.isEmpty()) {
                jText_jml.requestFocus();
            } else if (kategori.isEmpty()) {
                jComborole.requestFocus();
            } else {
                jComborole1.requestFocus();
            }
            return;
        }

        // Validasi kode buku harus 8 digit
        if (kodeBuku.length() != 8) {
            JOptionPane.showMessageDialog(null, "Kode Buku harus 8 digit!", "Error", JOptionPane.ERROR_MESSAGE);
            kode_buku.requestFocus();
            return;
        }

        // Validasi jumlah harus angka
        try {
            Integer.parseInt(jumlahText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Jumlah harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            jText_jml.requestFocus();
            return;
        }

        // Dapatkan ID kategori dan rak
        try {
            // Ambil ID kategori
            String sqlKategori = "SELECT id_kategori FROM tkategori WHERE Nama_kategori = ?";
            PreparedStatement pstKategori = con.prepareStatement(sqlKategori);
            pstKategori.setString(1, kategori);
            ResultSet rsKategori = pstKategori.executeQuery();

            if (rsKategori.next()) {
                rool_id = rsKategori.getString("id_kategori");
            } else {
                JOptionPane.showMessageDialog(null, "Kategori tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ambil ID rak
            String sqlRak = "SELECT id_Rak FROM trak WHERE Nama_Rak = ?";
            PreparedStatement pstRak = con.prepareStatement(sqlRak);
            pstRak.setString(1, rak);
            ResultSet rsRak = pstRak.executeQuery();

            if (rsRak.next()) {
                Rak_Id = rsRak.getString("id_Rak");
            } else {
                JOptionPane.showMessageDialog(null, "Rak tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Panggil method insert/update
            InserUpdate();

            // Tutup resources
            rsKategori.close();
            pstKategori.close();
            rsRak.close();
            pstRak.close();

        } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, "Error database: " + err.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            err.printStackTrace();
        }

    }//GEN-LAST:event_btn_simpanActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        // TODO add your handling code here:
        hapus_data();
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void btn_KeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_KeluarActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin keluar?", "Konfirmasi keluar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();

        }
    }//GEN-LAST:event_btn_KeluarActionPerformed

    private void jTable_userMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_userMouseClicked
        // TODO add your handling code here:
        int selectedRow = jTable_user.getSelectedRow();
        kode_buku.setText(jTable_user.getValueAt(selectedRow, 0).toString());
//        txt_nama.setText(jTable_Rak.getValueAt(selectedRow, 1).toString());
        rool_id = jTable_user.getValueAt(selectedRow, 0).toString();
    }//GEN-LAST:event_jTable_userMouseClicked

    private void jComboroleKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboroleKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            btn_simpan.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if              
    }//GEN-LAST:event_jComboroleKeyPressed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowActivated

    private void jComborole1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComborole1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComborole1KeyPressed

    private void btn_hapus1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapus1ActionPerformed
        // TODO add your handling code here:
        Jframe_Eksemplar view = new Jframe_Eksemplar();
        view.setLocationRelativeTo(null);
        view.setVisible(true);
    }//GEN-LAST:event_btn_hapus1ActionPerformed

    private void btn_reportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_reportActionPerformed
        // TODO add your handling code here:
        exportToExcel();
    }//GEN-LAST:event_btn_reportActionPerformed

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
            java.util.logging.Logger.getLogger(Jframe_MasterBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Jframe_MasterBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Jframe_MasterBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Jframe_MasterBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Jframe_MasterBuku().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Keluar;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_hapus1;
    private javax.swing.JButton btn_report;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JComboBox<String> jComborole;
    private javax.swing.JComboBox<String> jComborole1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_user;
    private javax.swing.JTextField jText_jml;
    private javax.swing.JTextField kode_buku;
    private javax.swing.JTextField nama_produk;
    private javax.swing.JTextField penerbit;
    private javax.swing.JTextField pengarang;
    // End of variables declaration//GEN-END:variables
}
