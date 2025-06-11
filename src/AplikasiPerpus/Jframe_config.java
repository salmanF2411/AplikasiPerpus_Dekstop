package AplikasiPerpus;

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

public class Jframe_config extends javax.swing.JFrame {

    DefaultTableModel tabelmodel;
    Connection con = null;
    Statement stat;
    ResultSet res;
    PreparedStatement pst = null;
    String rool_id = "";

    public Jframe_config() {
        initComponents();
        koneksi();
        datatojtable();
    }

    private void koneksi() {
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
        column = jTable_user.getColumnModel().getColumn(0);
        column.setPreferredWidth(150);
        column = jTable_user.getColumnModel().getColumn(1);
        column.setPreferredWidth(200);
        column = jTable_user.getColumnModel().getColumn(2);
        column.setPreferredWidth(150);
        column = jTable_user.getColumnModel().getColumn(3);
        column.setPreferredWidth(100);
    }

    private void datatojtable() {
        DefaultTableModel tb = new DefaultTableModel();
        tb.addColumn("ID Config");
        tb.addColumn("Nama Perpus");
        tb.addColumn("Alamat");
        tb.addColumn("Lama Peminjaman (hari)");
        tb.addColumn("Nominal Denda/hari");

        jTable_user.setModel(tb);
        try {
            res = stat.executeQuery("SELECT * FROM tconfig");
            while (res.next()) {
                tb.addRow(new Object[]{
                    res.getString("id_config"),
                    res.getString("nama_perpus"),
                    res.getString("alamat"),
                    res.getString("lama_peminjaman"),
                    res.getInt("nominal_denda")
                });
            }
            Aturkolom();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading config data: " + e.getMessage());
        }
    }

    private void bersihkantextfiled() {
        id_perpus.setText("");
        nama_perpus.setText("");
        txt_alamat.setText("");
        lama_peminjaman.setText("");
        nominal_denda.setText("");
        id_perpus.requestFocus();
    }

    private void cekdatauser() {
        try {

            if (id_perpus.getText().length() != 8) {
                JOptionPane.showMessageDialog(null, "Kode Buku Kurang dari 8 Digit");
                id_perpus.requestFocus();
            } else {
                String sqlcek = "select *from tconfig where id_config='" + id_perpus.getText() + "'";
                ResultSet rscek = stat.executeQuery(sqlcek);
                if (rscek.next()) {
                    nama_perpus.setText(rscek.getString("Nama_perpus"));
                    txt_alamat.setText(rscek.getString("alamat"));
                    lama_peminjaman.setText(rscek.getString("lama_peminjaman"));
                    nominal_denda.setText(rscek.getString("nominal_denda"));
                    nama_perpus.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(null, "Kode Produk Tidak Di temukan");
                    nama_perpus.setText("");
                    txt_alamat.setText("");
                    lama_peminjaman.setText("");
                    nominal_denda.setText("");
                    nama_perpus.requestFocus();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "GAGAL KETEMU");
        }
    }//akhir Methor

    public void InserUpdate() {
        try {
            String idConfig = id_perpus.getText().trim();
            String namaPerpus = nama_perpus.getText().trim();
            String alamat = txt_alamat.getText().trim();
            String lamaPinjam = lama_peminjaman.getText().trim();
            String denda = nominal_denda.getText().trim();

            // Validasi input
            if (idConfig.isEmpty() || namaPerpus.isEmpty() || alamat.isEmpty()
                    || lamaPinjam.isEmpty() || denda.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cek apakah data sudah ada
            String sqlCheck = "SELECT * FROM tconfig WHERE id_config = ?";
            PreparedStatement pstCheck = con.prepareStatement(sqlCheck);
            pstCheck.setString(1, idConfig);
            ResultSet rsu = pstCheck.executeQuery();

            if (rsu.next()) {
                // Update data yang ada
                String sqlUpdate = "UPDATE tconfig SET nama_perpus=?, alamat=?, lama_peminjaman=?, nominal_denda=? WHERE id_config=?";
                PreparedStatement pstUpdate = con.prepareStatement(sqlUpdate);
                pstUpdate.setString(1, namaPerpus);
                pstUpdate.setString(2, alamat);
                pstUpdate.setString(3, lamaPinjam);
                pstUpdate.setInt(4, Integer.parseInt(denda));
                pstUpdate.setString(5, idConfig);
                pstUpdate.executeUpdate();

                JOptionPane.showMessageDialog(null, "Config berhasil diperbarui!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Insert data baru
                String sqlInsert = "INSERT INTO tconfig (id_config, nama_perpus, alamat, lama_peminjaman, nominal_denda) "
                        + "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstInsert = con.prepareStatement(sqlInsert);
                pstInsert.setString(1, idConfig);
                pstInsert.setString(2, namaPerpus);
                pstInsert.setString(3, alamat);
                pstInsert.setString(4, lamaPinjam);
                pstInsert.setInt(5, Integer.parseInt(denda));
                pstInsert.executeUpdate();

                JOptionPane.showMessageDialog(null, "Config berhasil disimpan!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            datatojtable();
            bersihkantextfiled();

        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void hapus_data() {
        String idConfig = id_perpus.getText().trim();

        if (idConfig.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Pilih data yang ingin dihapus!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus config ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sqlDelete = "DELETE FROM tconfig WHERE id_config = ?";
                PreparedStatement pstDelete = con.prepareStatement(sqlDelete);
                pstDelete.setString(1, idConfig);
                pstDelete.executeUpdate();

                JOptionPane.showMessageDialog(null, "Config berhasil dihapus!", "Success", JOptionPane.INFORMATION_MESSAGE);
                bersihkantextfiled();
                datatojtable();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Gagal menghapus config: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        id_perpus = new javax.swing.JTextField();
        nama_perpus = new javax.swing.JTextField();
        txt_alamat = new javax.swing.JTextField();
        lama_peminjaman = new javax.swing.JTextField();
        nominal_denda = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_user = new javax.swing.JTable();
        btn_simpan = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        btn_Keluar = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Swis721 Blk BT", 0, 36)); // NOI18N
        jLabel1.setText("CONFIG");

        jLabel2.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel2.setText("Nama Perpustakaan");

        jLabel3.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel3.setText("Alamat");

        jLabel4.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel4.setText("Lama Peminjaman");

        jLabel5.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel5.setText("Nominal Denda");

        id_perpus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                id_perpusKeyPressed(evt);
            }
        });

        nama_perpus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nama_perpusKeyPressed(evt);
            }
        });

        txt_alamat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_alamatKeyPressed(evt);
            }
        });

        lama_peminjaman.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lama_peminjamanKeyPressed(evt);
            }
        });

        nominal_denda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nominal_dendaKeyPressed(evt);
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

        jLabel6.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel6.setText("ID PERPUS");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel6))
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(id_perpus, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lama_peminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_alamat, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nominal_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nama_perpus, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(286, 286, 286)
                        .addComponent(jLabel1)))
                .addContainerGap(283, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(366, 366, 366)
                        .addComponent(btn_Keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 601, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(118, 118, 118))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(id_perpus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addGap(27, 27, 27)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nama_perpus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txt_alamat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lama_peminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(nominal_denda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(127, 127, 127))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void id_perpusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_id_perpusKeyPressed
        // TODO add your handling code here:
        String Kodebuku = id_perpus.getText();
        if (Kodebuku.isEmpty()) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
                nama_perpus.requestFocus();   //panggil komponen yang akan di tuju
            }
        } else {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
                cekdatauser();
                //panggil komponen yang akan di tuju
            }  //end if
        }


    }//GEN-LAST:event_id_perpusKeyPressed

    private void nama_perpusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nama_perpusKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            txt_alamat.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_nama_perpusKeyPressed

    private void txt_alamatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_alamatKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            lama_peminjaman.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_txt_alamatKeyPressed

    private void lama_peminjamanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lama_peminjamanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            nominal_denda.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_lama_peminjamanKeyPressed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        // TODO add your handling code here:
        InserUpdate();
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        // TODO add your handling code here:
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
        id_perpus.setText(jTable_user.getValueAt(selectedRow, 0).toString());
//        txt_nama.setText(jTable_Rak.getValueAt(selectedRow, 1).toString());
        rool_id = jTable_user.getValueAt(selectedRow, 0).toString();

    }//GEN-LAST:event_jTable_userMouseClicked

    private void nominal_dendaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nominal_dendaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            btn_simpan.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_nominal_dendaKeyPressed

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
            java.util.logging.Logger.getLogger(Jframe_config.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Jframe_config.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Jframe_config.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Jframe_config.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Jframe_config().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Keluar;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JTextField id_perpus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_user;
    private javax.swing.JTextField lama_peminjaman;
    private javax.swing.JTextField nama_perpus;
    private javax.swing.JTextField nominal_denda;
    private javax.swing.JTextField txt_alamat;
    // End of variables declaration//GEN-END:variables

    private void Caridata(String namaBuku) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
