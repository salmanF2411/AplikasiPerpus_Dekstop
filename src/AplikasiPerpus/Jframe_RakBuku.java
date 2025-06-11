package AplikasiPerpus;

import java.awt.event.KeyEvent;
import static java.lang.System.err;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.sql.PreparedStatement;
import javax.swing.JFrame;


public class Jframe_RakBuku extends javax.swing.JFrame {

    DefaultTableModel tabelmodel;
    Connection con = null;
    Statement stat;
    ResultSet res;
    PreparedStatement pst = null;
    String kodee_rak = "";

    /**
     * Creates new form Rak
     */
    public Jframe_RakBuku() {
        initComponents();
        koneksi();
        datatojtable();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    

    private void koneksi() {//begin
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/db_perpustakaan", "root", "");
            stat = con.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//end begin

    private void Aturkolom() {
        TableColumn column;
        jTable_user.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        column = jTable_user.getColumnModel().getColumn(0);
        column.setPreferredWidth(75);
        column = jTable_user.getColumnModel().getColumn(1);
        column.setPreferredWidth(268);
    }

    private void datatojtable() {
        DefaultTableModel tb = new DefaultTableModel();
        // Memberi nama pada setiap kolom tabel
        tb.addColumn("Kode Rak");
        tb.addColumn("Nama Rak");
        jTable_user.setModel(tb);
        try {
            // Mengambil data dari database
            res = stat.executeQuery("select *from trak");
            while (res.next()) {
                // Mengambil data dari database berdasarkan nama kolom pada tabel
                // Lalu di tampilkan ke dalam JTable
                tb.addRow(new Object[]{
                    res.getString("Id_Rak"),
                    res.getString("Nama_Rak")
                });
            }
            //Aturkolom(); //pemanggilan class untuk mengatur kolom
        } catch (SQLException e) {
        }
        Aturkolom();
    }//end method

    private void bersihkantextfiled() {
        kode_rak.setText("");
        nama_rak.setText("");
        kode_rak.requestFocus();
    }

    private void cekdatauser() {
        try {

            if (kode_rak.getText().length() != 8) {
                JOptionPane.showMessageDialog(null, "Panjang Karakter Kurang dari 8 Digit");
                kode_rak.requestFocus();
            } else {
                String sqlcek = "select *from trak where Id_Rak='" + kode_rak.getText() + "'";
                ResultSet rscek = stat.executeQuery(sqlcek);
                if (rscek.next()) {
                    nama_rak.setText(rscek.getString("Nama_Rak"));
                    nama_rak.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(null, "Kode Rak Tidak Di temukan");
                    nama_rak.setText("");
                    nama_rak.requestFocus();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "GAGAL KETEMU");
        }
    }//akhir Methor

    public void HapusData() {
        kodee_rak = (tabelmodel.getValueAt(jTable_user.getSelectedRow(), 0) + "");

        try {
            String sql = "delete from trak where Id_Rak='" + kodee_rak + "'";
            stat.executeUpdate(sql);

            kode_rak.requestFocus();
            kode_rak.setText("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        kode_rak = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        nama_rak = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_user = new javax.swing.JTable();
        btn_simpan = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        btn_keluar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Swis721 Blk BT", 1, 36)); // NOI18N
        jLabel1.setText("RAK");

        jLabel2.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel2.setText("Kode Rak");

        kode_rak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kode_rakActionPerformed(evt);
            }
        });
        kode_rak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kode_rakKeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel3.setText("Nama Rak");

        nama_rak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nama_rakActionPerformed(evt);
            }
        });
        nama_rak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nama_rakKeyPressed(evt);
            }
        });

        jTable_user.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null}
            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        jTable_user.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_userMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_user);

        btn_simpan.setBackground(new java.awt.Color(204, 204, 255));
        btn_simpan.setForeground(new java.awt.Color(51, 51, 255));
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

        btn_keluar.setBackground(new java.awt.Color(204, 204, 255));
        btn_keluar.setForeground(new java.awt.Color(0, 0, 255));
        btn_keluar.setText("Keluar");
        btn_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_keluarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_simpan)
                        .addGap(25, 25, 25)
                        .addComponent(btn_hapus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_keluar))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 73, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(kode_rak, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nama_rak, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(228, 228, 228)
                        .addComponent(jLabel1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel1)
                .addGap(62, 62, 62)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(kode_rak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(nama_rak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btn_keluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(25, 25, 25))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void nama_rakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nama_rakActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nama_rakActionPerformed

    private void kode_rakKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kode_rakKeyPressed
        // TODO add your handling code here:
         // TODO add your handling code here:
          if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            cekdatauser();
            //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_kode_rakKeyPressed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed

        String kodeRak = kode_rak.getText().trim();
String namaRak = nama_rak.getText().trim();

// Validasi: Pastikan semua field terisi

if (kodeRak.isEmpty() || namaRak.isEmpty()) {
    JOptionPane.showMessageDialog(null, "Harap lengkapi semua data!",
            "Peringatan", JOptionPane.WARNING_MESSAGE);
    kode_rak.requestFocus();
    return;
}

if (kodeRak.length() != 8) {
    JOptionPane.showMessageDialog(null, "Kode Rak harus terdiri dari 8 karakter!",
            "Peringatan", JOptionPane.WARNING_MESSAGE);
    kode_rak.requestFocus();
    return;
}



try {
    // Cek apakah data sudah ada di database (baik kode rak maupun nama rak sama)
    PreparedStatement pstSelect = con.prepareStatement("SELECT * FROM trak WHERE Id_Rak = ? AND Nama_Rak = ?");
    pstSelect.setString(1, kodeRak);
    pstSelect.setString(2, namaRak);
    ResultSet rs = pstSelect.executeQuery();

    if (rs.next()) {
        // Jika data sudah ada (kode rak dan nama rak sama), tampilkan notifikasi
        JOptionPane.showMessageDialog(null, "Data sudah ada di rak!", "Peringatan", JOptionPane.WARNING_MESSAGE);
    } else {
        // Cek apakah kode rak sudah ada (untuk menentukan apakah ini update atau simpan)
        PreparedStatement pstCheckKode = con.prepareStatement("SELECT * FROM trak WHERE Id_Rak = ?");
        pstCheckKode.setString(1, kodeRak);
        ResultSet rsKode = pstCheckKode.executeQuery();

        if (rsKode.next()) {
            // Jika kode rak sudah ada, tanyakan apakah ingin mengupdate
            int confirmUpdate = JOptionPane.showConfirmDialog(null,
                    "Apakah Anda yakin ingin memperbarui data?",
                    "Konfirmasi Update", JOptionPane.YES_NO_OPTION);

            if (confirmUpdate == JOptionPane.YES_OPTION) {
                // Proses UPDATE
                PreparedStatement pstUpdate = con.prepareStatement("UPDATE trak SET Nama_Rak = ? WHERE Id_Rak = ?");
                pstUpdate.setString(1, namaRak);
                pstUpdate.setString(2, kodeRak);
                pstUpdate.executeUpdate();
                pstUpdate.close();

                JOptionPane.showMessageDialog(null, "Data berhasil diperbarui!", "Update Berhasil", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            // Jika data belum ada, tanyakan apakah ingin menyimpan
            int confirmSave = JOptionPane.showConfirmDialog(null,
                    "Apakah Anda ingin menyimpan data?",
                    "Konfirmasi Simpan", JOptionPane.YES_NO_OPTION);

            if (confirmSave == JOptionPane.YES_OPTION) {
                // Proses INSERT
                PreparedStatement pstInsert = con.prepareStatement("INSERT INTO trak (Id_Rak, Nama_Rak) VALUES (?, ?)");
                pstInsert.setString(1, kodeRak);
                pstInsert.setString(2, namaRak);
                pstInsert.executeUpdate();
                pstInsert.close();

                JOptionPane.showMessageDialog(null, "Data berhasil disimpan!", "Simpan Berhasil", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        // Tutup koneksi untuk pengecekan kode rak
        rsKode.close();
        pstCheckKode.close();
    }

    // Tutup koneksi untuk pengecekan data
    rs.close();
    pstSelect.close();

    // Refresh tabel dan bersihkan input
    datatojtable();
    kode_rak.setText("");
    nama_rak.setText("");
    kode_rak.requestFocus();

} catch (SQLException e) {
    JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan atau memperbarui data!\n" + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
}

    }//GEN-LAST:event_btn_simpanActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        // TODO add your handling code here:
        String kodeRak = kode_rak.getText().trim();

        // Validasi: Pastikan field tidak kosong
        if (kodeRak.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Pilih data yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Konfirmasi sebelum menghapus
        int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Koneksi langsung di JFrame
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_perpustakaan", "root", "");

                // Query DELETE menggunakan PreparedStatement untuk keamanan
                String sql = "DELETE FROM trak WHERE Id_Rak = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, kodeRak);

                // Eksekusi query
                int rowsDeleted = pst.executeUpdate();
                pst.close();
                con.close();

                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(null, "Data berhasil dihapus!", "Hapus", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Data tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                }

                // Kosongkan JTextField setelah menghapus
                kode_rak.setText("");
                kode_rak.requestFocus();

                // Refresh tabel setelah perubahan
                datatojtable();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_btn_hapusActionPerformed

    private void kode_rakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kode_rakActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_kode_rakActionPerformed

    private void jTable_userMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_userMouseClicked
        // TODO add your handling code here:
        int selectedRow = jTable_user.getSelectedRow();
        kode_rak.setText(jTable_user.getValueAt(selectedRow, 0).toString());
//        txt_nama.setText(jTable_Rak.getValueAt(selectedRow, 1).toString());
        kodee_rak = jTable_user.getValueAt(selectedRow, 0).toString();
    }//GEN-LAST:event_jTable_userMouseClicked

    private void btn_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_keluarActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin keluar?", "Konfirmasi keluar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();

        }

    }//GEN-LAST:event_btn_keluarActionPerformed

    private void nama_rakKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nama_rakKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin 
            btn_simpan.requestFocus();   //panggil komponen yang akan di tuju 
        }  //end if
    }//GEN-LAST:event_nama_rakKeyPressed

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
            java.util.logging.Logger.getLogger(Jframe_RakBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Jframe_RakBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Jframe_RakBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Jframe_RakBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Jframe_RakBuku().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_keluar;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_user;
    private javax.swing.JTextField kode_rak;
    private javax.swing.JTextField nama_rak;
    // End of variables declaration//GEN-END:variables
}
