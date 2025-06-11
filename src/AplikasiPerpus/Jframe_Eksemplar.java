/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AplikasiPerpus;

import java.awt.Component;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
//penambahan library
import java.awt.event.KeyEvent;
import javax.swing.table.TableColumn;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.JFileChooser;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Asus
 */
public class Jframe_Eksemplar extends javax.swing.JFrame {

    DefaultTableModel tabelModel;
    Connection con = null;
    Statement stat;
    ResultSet res;
    PreparedStatement pst = null;

    /**
     * Creates new form Jframe_Eksemplar
     */
    public Jframe_Eksemplar() {
        initComponents();
        koneksi();
        datatojtable();
    }

    private void koneksi() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            con = DriverManager.getConnection("jdbc:mysql://localhost/db_perpustakaan",
                    "root", "");
            stat = con.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

   private void datatojtable() {
    DefaultTableModel tb = new DefaultTableModel();
    tb.addColumn("ID Buku");
    tb.addColumn("Nama Buku");
    tb.addColumn("Pengarang");
    tb.addColumn("Penerbit");
    tb.addColumn("Jumlah");
    tb.addColumn("Eksemplar");
    tb.addColumn("Id Eksemplar");
    tb.addColumn("Kode Eksemplar");
    tb.addColumn("Status");
    tb.addColumn("Date Create");
    tb.addColumn("Date Modify");

    jTable_Inv.setModel(tb);
    try {
        String query = "SELECT a.id_buku, a.NamaBuku, a.Pengarang, a.Penerbit, a.Jumlah, " +
                  "b.IdEkseplar, b.KodeEksemplar, b.TglCreate, b.TglModify, " +
                  "COALESCE(b.status, 'V') AS Status " + // Use 'V' as default if status is null
                  "FROM tmasterbuku a " +
                  "JOIN teksemplar b ON a.id_buku = b.id_buku " +
                  "ORDER BY b.IdEkseplar ASC";

        res = stat.executeQuery(query);
        while (res.next()) {
            tb.addRow(new Object[]{
                res.getString("id_buku"),
                res.getString("NamaBuku"),
                res.getString("Pengarang"),
                res.getString("Penerbit"),
                res.getString("Jumlah"),
                res.getString("Jumlah"),
                res.getString("IdEkseplar"),
                res.getString("KodeEksemplar"),
                res.getString("Status"),
                res.getString("TglCreate"),
                res.getString("TglModify")
            });
        }
        Aturkolom();

        // Custom renderer untuk warna status
        jTable_Inv.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) table.getModel().getValueAt(row, 8); // Kolom status
                if ("$".equals(status)) {
                    c.setBackground(java.awt.Color.RED);
                    c.setForeground(java.awt.Color.WHITE);
                } else {
                    c.setBackground(java.awt.Color.GREEN);
                    c.setForeground(java.awt.Color.BLACK);
                }
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                }
                return c;
            }
        });

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }
}
    

    private void Aturkolom() {
        TableColumn column;
        jTable_Inv.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        column = jTable_Inv.getColumnModel().getColumn(0);
        column.setPreferredWidth(75);
        column = jTable_Inv.getColumnModel().getColumn(1);
        column.setPreferredWidth(120);
        column = jTable_Inv.getColumnModel().getColumn(2);
        column.setPreferredWidth(100);
        column = jTable_Inv.getColumnModel().getColumn(3);
        column.setPreferredWidth(100);
        column = jTable_Inv.getColumnModel().getColumn(4);
        column.setPreferredWidth(75);
        column = jTable_Inv.getColumnModel().getColumn(5);
        column.setPreferredWidth(80);
        column = jTable_Inv.getColumnModel().getColumn(6);
        column.setPreferredWidth(85);
        column = jTable_Inv.getColumnModel().getColumn(7);
        column.setPreferredWidth(130);
        column = jTable_Inv.getColumnModel().getColumn(8);
        column.setPreferredWidth(70);
        column = jTable_Inv.getColumnModel().getColumn(9);
        column.setPreferredWidth(80);
        column = jTable_Inv.getColumnModel().getColumn(10); // Kolom status
column.setPreferredWidth(80);
    }

    private void Caridata(String key) {
    DefaultTableModel tb = new DefaultTableModel();
    // Memberi nama pada setiap kolom tabel
    tb.addColumn("ID Buku");
    tb.addColumn("Nama Buku");
    tb.addColumn("Pengarang");
    tb.addColumn("Penerbit");
    tb.addColumn("Jumlah");
    tb.addColumn("Eksemplar");
    tb.addColumn("Date Create");
    tb.addColumn("Date Modify");
    tb.addColumn("Id Eksemplar");
    tb.addColumn("Kode Eksemplar");
    tb.addColumn("Status");

    jTable_Inv.setModel(tb);
    try {
        String query = "SELECT a.id_buku, a.NamaBuku, a.Pengarang, a.Penerbit, a.Jumlah, " +
                  "b.IdEkseplar, b.KodeEksemplar, b.TglCreate, b.TglModify, " +
                  "COALESCE(b.status, 'V') AS Status " + // Use 'V' as default if status is null
                  "FROM tmasterbuku a " +
                  "JOIN teksemplar b ON a.id_buku = b.id_buku " +
                  "WHERE a.NamaBuku LIKE '%" + key + "%' OR a.Pengarang LIKE '%" + key + "%' " +
                  "ORDER BY a.id_buku ASC";
        
        res = stat.executeQuery(query);
        
        while (res.next()) {
            tb.addRow(new Object[]{
                res.getString("id_buku"),
                res.getString("NamaBuku"),
                res.getString("Pengarang"),
                res.getString("Penerbit"),
                res.getString("Jumlah"),
                res.getString("Jumlah"),
                res.getString("TglCreate"),
                res.getString("TglModify"),
                res.getString("IdEkseplar"),
                res.getString("KodeEksemplar"),
                res.getString("Status")
            });
        }
        Aturkolom();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }
}
    
    private void exportToExcel() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Simpan File Excel");
    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));
    fileChooser.setSelectedFile(new java.io.File("Data_Eksemplar.xlsx"));
    
    int userSelection = fileChooser.showSaveDialog(this);
    
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        java.io.File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();
        
        if (!filePath.toLowerCase().endsWith(".xlsx")) {
            filePath += ".xlsx";
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data Eksemplar");
            
            // Membuat header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID Buku", "Nama Buku", "Pengarang", "Penerbit", 
                               "Jumlah Buku", "Eksemplar", "Date Create", "Date Modify",
                               "Id Eksemplar", "Kode Eksemplar", "Status"};
            
            // Style untuk header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Mengisi data
            DefaultTableModel model = (DefaultTableModel) jTable_Inv.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    // Convert status symbol to meaningful text in Excel
                    if (j == 10) { // Status column
                        String status = value != null ? value.toString() : "";
                        row.createCell(j).setCellValue("$".equals(status) ? "Dipinjam" : "V".equals(status) ? "Tersedia" : "");
                    } else {
                        row.createCell(j).setCellValue(value != null ? value.toString() : "");
                    }
                }
            }
            
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
                JOptionPane.showMessageDialog(this, "Data berhasil diexport ke Excel!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengeksport data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
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
        jLabel2 = new javax.swing.JLabel();
        jText_cari = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Inv = new javax.swing.JTable();
        btn_keluar = new javax.swing.JButton();
        btn_report = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setText("DATA EKSEMPLAR BUKU");

        jLabel2.setText("Cari Data");

        jText_cari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jText_cariKeyPressed(evt);
            }
        });

        jTable_Inv.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable_Inv.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_InvMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_Inv);

        btn_keluar.setText("Keluar");
        btn_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_keluarActionPerformed(evt);
            }
        });

        btn_report.setBackground(new java.awt.Color(204, 204, 255));
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
                .addGap(134, 134, 134)
                .addComponent(btn_report, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(203, 203, 203))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jText_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(257, 257, 257)
                        .addComponent(jLabel1)))
                .addContainerGap(279, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(95, 95, 95)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jText_cari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_report, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jText_cariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jText_cariKeyPressed
        // TODO add your handling code here:
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String key = jText_cari.getText().trim();  // hapus spasi depan belakang

            if (!key.isEmpty()) {
                Caridata(key);  // jika ada input, lakukan pencarian
            } else {
                datatojtable(); // jika kosong, tampilkan semua data
            }
        }
    }//GEN-LAST:event_jText_cariKeyPressed

    private void btn_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_keluarActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin keluar?", "Konfirmasi keluar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();

        }
    }//GEN-LAST:event_btn_keluarActionPerformed

    private void jTable_InvMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_InvMouseClicked
        // TODO add your handling code here:
        int selectedRow = jTable_Inv.getSelectedRow();
        if (selectedRow != -1) {
            // Ambil nilai dari kolom Nama Buku (kolom ke-1 = index 1)
            String namaBuku = jTable_Inv.getValueAt(selectedRow, 1).toString();
            jText_cari.setText(namaBuku);
            Caridata(namaBuku); // langsung tampilkan hasil pencarian berdasarkan Nama Buku
        }
    }//GEN-LAST:event_jTable_InvMouseClicked

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
            java.util.logging.Logger.getLogger(Jframe_Eksemplar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Jframe_Eksemplar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Jframe_Eksemplar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Jframe_Eksemplar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Jframe_Eksemplar().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_keluar;
    private javax.swing.JButton btn_report;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_Inv;
    private javax.swing.JTextField jText_cari;
    // End of variables declaration//GEN-END:variables
}
