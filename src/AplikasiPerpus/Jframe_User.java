/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author Asus
 */
public class Jframe_User extends javax.swing.JFrame {

    DefaultTableModel tabelmodel;
    Connection con = null;
    Statement stat;
    ResultSet res;
    PreparedStatement pst = null;
    String rool_id = "";

    /**
     * Creates new form Jframe_User
     */
    public Jframe_User() {
        initComponents();
        koneksi();
        datatojtable();
        dataToComboBox();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void koneksi() {//begin
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/dbperpus", "root", "");
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
        column.setPreferredWidth(200);
        column = jTable_user.getColumnModel().getColumn(2);
        column.setPreferredWidth(75);
        column = jTable_user.getColumnModel().getColumn(3);
        column.setPreferredWidth(75);
        column = jTable_user.getColumnModel().getColumn(4);
        column.setPreferredWidth(100);
        column = jTable_user.getColumnModel().getColumn(5);
        column.setPreferredWidth(100);
        column = jTable_user.getColumnModel().getColumn(6);
        column.setPreferredWidth(75);
    }

    private void datatojtable() {
        DefaultTableModel tb = new DefaultTableModel();
        // Memberi nama pada setiap kolom tabel
        tb.addColumn("Id User");
        tb.addColumn("Nama User");
        tb.addColumn("Password");
        tb.addColumn("Role Id");
        tb.addColumn("Nama Role");
        tb.addColumn("Date Create");
        tb.addColumn("Date Modify");
        jTable_user.setModel(tb);
        try {
            // Mengambil data dari database
            res = stat.executeQuery("select a.iduser,a.namauser,a.password,a.roleid,b.namarole,a.datecreate,a.datemodify from tuser a,trole b where a.roleid=b.roleid");
            while (res.next()) {
                // Mengambil data dari database berdasarkan nama kolom pada tabel
                // Lalu di tampilkan ke dalam JTable
                tb.addRow(new Object[]{
                    res.getString("iduser"),
                    res.getString("NamaUser"),
                    res.getString("Password"),
                    res.getString("roleid"),
                    res.getString("namarole"),
                    res.getDate("datecreate"),
                    res.getDate("datemodify")
                });
            }
            //Aturkolom(); //pemanggilan class untuk mengatur kolom
        } catch (SQLException e) {
        }
        Aturkolom();
    }//end method

    private void dataToComboBox() {
        try {
            String sql = "SELECT * FROM trole";
            pst = con.prepareStatement(sql);
            res = pst.executeQuery();

            while (res.next()) {

                jComborole.addItem(res.getString("namarole"));
            }

            res.last();
            int jumlahdata = res.getRow();
            res.first();

        } catch (SQLException e) {
        }
    }//end method

    private void bersihkantextfiled() {
        txt_iduser.setText("");
        txt_nama.setText("");
        txt_pass.setText("");
        txt_konfirmasi.setText("");
        txt_iduser.requestFocus();
    }

    private void cekdatauser() {
        try {

            if (txt_iduser.getText().length() != 8) {
                JOptionPane.showMessageDialog(null, "Panjang Karakter Kurang dari 8 Digit");
                txt_iduser.requestFocus();
            } else {
                String sqlcek = "select *from tuser where iduser='" + txt_iduser.getText() + "'";
                ResultSet rscek = stat.executeQuery(sqlcek);
                if (rscek.next()) {
                    txt_nama.setText(rscek.getString("NamaUser"));
                    txt_pass.setText(rscek.getString("Password"));
                    txt_konfirmasi.setText(rscek.getString("Password"));
                    txt_nama.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(null, "Id user Tidak Di temukan");
                    txt_nama.setText("");
                    txt_pass.setText("");
                    txt_konfirmasi.setText("");
                    txt_nama.requestFocus();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "GAGAL KETEMU");
        }
    }//akhir Methor

    public void cekrool_id() {
        String roleid = jComborole.getSelectedItem().toString();

        try {
            String sql = "select *from trole where namarole='" + roleid + "'";
            ResultSet rsid = stat.executeQuery(sql);
            if (rsid.next()) {
                rool_id = rsid.getString("roleid");
            } else {
                rool_id = "0";
            }
            //Coding Insert Coding
            //memanggil method Insert Update
            InserUpdate();

        } catch (SQLException err) {
            JOptionPane.showMessageDialog(this, "Koneksi Gagal\n" + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//end method

    public void InserUpdate() {
        try {
            Statement strU = con.createStatement();
            Statement strI = con.createStatement();
            Statement str = con.createStatement();

            String sqlu = "select *from tuser where idUser='" + txt_iduser.getText() + "'";
            ResultSet rsu = str.executeQuery(sqlu);

            java.util.Date tanggal = new java.util.Date();
            java.text.SimpleDateFormat setTanggal = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String TglNow = setTanggal.format(tanggal);

            if (rsu.next()) {
                String SqlU = "update tuser set namauser='" + txt_nama.getText() + "',password ='" + txt_pass.getText() + "',roleid='" + rool_id + "',datemodify='" + TglNow + "' where idUser='" + txt_iduser.getText() + "'";
                strU.executeUpdate(SqlU);
                JOptionPane.showMessageDialog(null, "Data Sudah Di Ubah", "Insert", JOptionPane.INFORMATION_MESSAGE);
                datatojtable();
                bersihkantextfiled();

            } else {
                String SqlI = "insert into tuser values('" + txt_iduser.getText() + "','" + txt_nama.getText() + "','" + txt_pass.getText() + "','" + rool_id + "','" + TglNow + "','" + TglNow + "')";
                strI.executeUpdate(SqlI);
                JOptionPane.showMessageDialog(null, "Data Sudah Di Simpan", "Insert", JOptionPane.INFORMATION_MESSAGE);
                datatojtable();
                bersihkantextfiled();

            }

        } catch (SQLException err) {
            JOptionPane.showMessageDialog(this, "Koneksi Gagal\n" + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//akhir Method

    public void hapus_data() {
        if (JOptionPane.showConfirmDialog(null, "Apakah Yakin Akan di Hapus ?", "Informasi", JOptionPane.INFORMATION_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                Statement SqlDel;
                SqlDel = con.createStatement();
                SqlDel.executeUpdate("delete from tuser where idUser='" + txt_iduser.getText() + "'");

                JOptionPane.showMessageDialog(this, "Data berhasil Di Hapus", "Success", JOptionPane.INFORMATION_MESSAGE);
                bersihkantextfiled();
                datatojtable();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Delete data gagal\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(null, "Kode User Batal Di Hapus");
            txt_iduser.requestFocus();
        }

    }//akhir Method

    public void cekPass() {
        String Pass = txt_pass.getText();
        String Pass1 = txt_konfirmasi.getText();
        if (Pass.equals(Pass1)) //pengeceken karakter sama atau tidak pak Jtexfiled
        {
            jComborole.requestFocus();
        } else {
            JOptionPane.showMessageDialog(null, "Password Belum Sama", "Pesan", JOptionPane.ERROR_MESSAGE);
            txt_konfirmasi.requestFocus();
        }
    }//akhir method

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txt_iduser = new javax.swing.JTextField();
        txt_nama = new javax.swing.JTextField();
        txt_pass = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txt_konfirmasi = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_user = new javax.swing.JTable();
        btn_simpan = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        cms_keluar = new javax.swing.JButton();
        jComborole = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("ID User");

        jLabel2.setText("Nama User");

        jLabel3.setText("Password");

        txt_iduser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_iduserActionPerformed(evt);
            }
        });
        txt_iduser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_iduserKeyPressed(evt);
            }
        });

        txt_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_namaKeyPressed(evt);
            }
        });

        txt_pass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_passKeyPressed(evt);
            }
        });

        jLabel4.setText("Confirm");

        txt_konfirmasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_konfirmasiKeyPressed(evt);
            }
        });

        jLabel5.setText("Role ID");

        jTable_user.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable_user.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_userMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_user);

        btn_simpan.setText("Simpan");
        btn_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpanActionPerformed(evt);
            }
        });

        btn_hapus.setText("Hapus");
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        cms_keluar.setText("Keluar");
        cms_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cms_keluarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 464, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txt_iduser, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                                        .addGap(22, 22, 22))
                                    .addComponent(txt_pass))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(txt_konfirmasi, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_simpan)
                        .addGap(45, 45, 45)
                        .addComponent(btn_hapus)
                        .addGap(108, 108, 108)
                        .addComponent(cms_keluar))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(61, 61, 61)
                        .addComponent(jComborole, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(105, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(103, 103, 103)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txt_iduser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txt_pass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txt_konfirmasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(68, 68, 68)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jComborole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan)
                    .addComponent(btn_hapus)
                    .addComponent(cms_keluar))
                .addContainerGap(51, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        // TODO add your handling code here:
        String kodeuser = txt_iduser.getText();
        String namauser = txt_nama.getText();
        String pass = txt_pass.getText();
        String cpass = txt_konfirmasi.getText();
        String roleid = jComborole.getSelectedItem().toString();
        String roolid;

        if (namauser.isEmpty() && kodeuser.isEmpty() && pass.isEmpty() && cpass.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ada kolom yang masih kosong");
            txt_iduser.requestFocus();
        } else {
            if (kodeuser.length() == 8) {
                //Coding sini
                if (pass.equals(cpass)) {
                    cekrool_id();
                } else {
                    JOptionPane.showMessageDialog(null, "Password Belum Sama", "Pesan", JOptionPane.ERROR_MESSAGE);
                    txt_konfirmasi.requestFocus();
                }

            } else {
                JOptionPane.showMessageDialog(null, "Panjang Karakter Kurang dari 8 Digit");
                txt_iduser.requestFocus();
            }
        }//end if


    }//GEN-LAST:event_btn_simpanActionPerformed


    private void txt_iduserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_iduserActionPerformed

    }//GEN-LAST:event_txt_iduserActionPerformed

    private void txt_iduserKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_iduserKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            txt_nama.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            cekdatauser();
            //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_txt_iduserKeyPressed

    private void txt_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            txt_pass.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_txt_namaKeyPressed

    private void txt_passKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_passKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            txt_konfirmasi.requestFocus();   //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_txt_passKeyPressed

    private void txt_konfirmasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_konfirmasiKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            cekPass();   //panggil komponen yang akan di tuju
        }  //end if  
    }//GEN-LAST:event_txt_konfirmasiKeyPressed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        // TODO add your handling code here:
        if (txt_iduser.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Kode User Tidak Boleh Kosong");
            txt_iduser.requestFocus();

        } else {
            //PANGGIL METHOD HAPUS DATA
            hapus_data();
        }

    }//GEN-LAST:event_btn_hapusActionPerformed

    private void cms_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cms_keluarActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin keluar?", "Konfirmasi keluar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();

        }
    }//GEN-LAST:event_cms_keluarActionPerformed

    private void jTable_userMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_userMouseClicked
        // TODO add your handling code here:
          int selectedRow = jTable_user.getSelectedRow();
        txt_iduser.setText(jTable_user.getValueAt(selectedRow, 0).toString());
//        txt_nama.setText(jTable_Rak.getValueAt(selectedRow, 1).toString());
        rool_id = jTable_user.getValueAt(selectedRow, 0).toString();
    }//GEN-LAST:event_jTable_userMouseClicked

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
            java.util.logging.Logger.getLogger(Jframe_User.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Jframe_User.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Jframe_User.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Jframe_User.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Jframe_User().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JButton cms_keluar;
    private javax.swing.JComboBox<String> jComborole;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_user;
    private javax.swing.JTextField txt_iduser;
    private javax.swing.JTextField txt_konfirmasi;
    private javax.swing.JTextField txt_nama;
    private javax.swing.JTextField txt_pass;
    // End of variables declaration//GEN-END:variables
}
