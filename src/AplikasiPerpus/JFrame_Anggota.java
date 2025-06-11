/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AplikasiPerpus;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Asus
 */
public class JFrame_Anggota extends javax.swing.JFrame {

    DefaultTableModel tabelmodel;
    Connection con = null;
    Statement stat;
    ResultSet res;
    PreparedStatement pst = null;
    String kodee_rak = "";

    private String fotoPath = "";
    private JButton btnPilihFoto;
    private JButton btnCetakKartu;
    private JLabel lblFoto;

    /**
     * Creates new form JFrame_Anggota
     */
    public JFrame_Anggota() {
        initComponents();
        koneksi();
        datatojtable();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tambahkanKomponenKartu();
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
        column.setPreferredWidth(100);
        column = jTable_user.getColumnModel().getColumn(1);
        column.setPreferredWidth(100);
        column = jTable_user.getColumnModel().getColumn(2);
        column.setPreferredWidth(100);
        column = jTable_user.getColumnModel().getColumn(3);
        column.setPreferredWidth(100);
        column = jTable_user.getColumnModel().getColumn(4);
        column.setPreferredWidth(125);
        column = jTable_user.getColumnModel().getColumn(5);
        column.setPreferredWidth(85);
    }

    private void datatojtable() {
        DefaultTableModel tb = new DefaultTableModel();
        // Memberi nama pada setiap kolom tabel
        tb.addColumn("Id Anggota");
        tb.addColumn("Nama Anggota");
        tb.addColumn("Alamat");
        tb.addColumn("Fakultas");
        tb.addColumn("Prodi");
        tb.addColumn("Jenis Kelamin");
        jTable_user.setModel(tb);
        try {
            // Mengambil data dari database
            res = stat.executeQuery("select *from tanggota");
            while (res.next()) {
                // Mengambil data dari database berdasarkan nama kolom pada tabel
                // Lalu di tampilkan ke dalam JTable
                tb.addRow(new Object[]{
                    res.getString("Id_anggota"),
                    res.getString("Nama_anggota"),
                    res.getString("alamat"),
                    res.getString("fakultas"),
                    res.getString("prodi"),
                    res.getString("kelamin")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
        Aturkolom();
    }//end method

    private void bersihkantextfiled() {
        kode_rak.setText("");
        nama_rak.setText("");
        txt_alamat.setText("");
        txt_fakultas.setText("");
        txt_prodi.setText("");
        jCheckBox1.setSelected(false);
        jCheckBox2.setSelected(false);
        kode_rak.requestFocus();
    }

    private void cekdatauser() {
        try {
            if (kode_rak.getText().length() != 8) {
                JOptionPane.showMessageDialog(null, "Panjang Karakter Kurang dari 8 Digit");
                kode_rak.requestFocus();
            } else {
                String sqlcek = "select *from tanggota where Id_anggota='" + kode_rak.getText() + "'";
                ResultSet rscek = stat.executeQuery(sqlcek);
                if (rscek.next()) {
                    nama_rak.setText(rscek.getString("Nama_anggota"));
                    txt_alamat.setText(rscek.getString("alamat"));
                    txt_fakultas.setText(rscek.getString("fakultas"));
                    txt_prodi.setText(rscek.getString("prodi"));

                    String kelamin = rscek.getString("kelamin");
                    if (kelamin.equalsIgnoreCase("pria")) {
                        jCheckBox1.setSelected(true);
                        jCheckBox2.setSelected(false);
                    } else if (kelamin.equalsIgnoreCase("wanita")) {
                        jCheckBox1.setSelected(false);
                        jCheckBox2.setSelected(true);
                    }

                    btn_simpan.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(null, "Kode Anggota Tidak Ditemukan");
                    nama_rak.setText("");
                    nama_rak.requestFocus();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "GAGAL KETEMU: " + e.getMessage());
        }
    }//akhir Methor

    public void HapusData() {
        kodee_rak = (tabelmodel.getValueAt(jTable_user.getSelectedRow(), 0) + "");

        try {
            String sql = "delete from tanggota where Id_anggota='" + kodee_rak + "'";
            stat.executeUpdate(sql);

            kode_rak.requestFocus();
            kode_rak.setText("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void tambahkanKomponenKartu() {
        // Label untuk menampilkan foto
        lblFoto = new JLabel();
        lblFoto.setBounds(500, 50, 150, 200);
        lblFoto.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        getContentPane().add(lblFoto);

        // Button untuk memilih foto
        btnPilihFoto = new JButton("Pilih Foto");
        btnPilihFoto.setBounds(500, 260, 150, 30);
        btnPilihFoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihFotoActionPerformed(evt);
            }
        });
        getContentPane().add(btnPilihFoto);

        // Button untuk mencetak kartu
        btnCetakKartu = new JButton("Cetak Kartu");
        btnCetakKartu.setBounds(500, 300, 150, 30);
        btnCetakKartu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cetakActionPerformed(evt);
            }
        });
        getContentPane().add(btnCetakKartu);

        // Sesuaikan ukuran frame
        setSize(700, 600);
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
        jLabel3 = new javax.swing.JLabel();
        kode_rak = new javax.swing.JTextField();
        nama_rak = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_user = new javax.swing.JTable();
        btn_simpan = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        btn_keluar = new javax.swing.JButton();
        txt_alamat = new javax.swing.JTextField();
        txt_fakultas = new javax.swing.JTextField();
        txt_prodi = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        pilihFoto = new javax.swing.JButton();
        btn_cetak = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("ANGGOTA PERPUS");

        jLabel2.setText("Id Anggota");

        jLabel3.setText("Nama Anggota");

        kode_rak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kode_rakKeyPressed(evt);
            }
        });

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

        btn_simpan.setText("simpan");
        btn_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpanActionPerformed(evt);
            }
        });

        btn_hapus.setText("hapus");
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        btn_keluar.setText("keluar");
        btn_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_keluarActionPerformed(evt);
            }
        });

        txt_alamat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_alamatActionPerformed(evt);
            }
        });
        txt_alamat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_alamatKeyPressed(evt);
            }
        });

        txt_fakultas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_fakultasActionPerformed(evt);
            }
        });
        txt_fakultas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_fakultasKeyPressed(evt);
            }
        });

        txt_prodi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_prodiActionPerformed(evt);
            }
        });
        txt_prodi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_prodiKeyPressed(evt);
            }
        });

        jLabel4.setText("Alamat");

        jLabel5.setText("Fakultas");

        jLabel6.setText("Prodi");

        jLabel7.setText("Jenis Kelamin");

        jCheckBox1.setText("pria");
        jCheckBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCheckBox1KeyPressed(evt);
            }
        });

        jCheckBox2.setText("wanita");

        pilihFoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihFotoActionPerformed(evt);
            }
        });

        btn_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cetakActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addGap(54, 54, 54)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(kode_rak, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jCheckBox1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCheckBox2))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(nama_rak, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                                .addComponent(txt_alamat, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txt_prodi, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txt_fakultas))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(195, 195, 195)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(204, 204, 204)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(pilihFoto, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btn_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_keluar)
                            .addComponent(btn_simpan, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btn_hapus))))
                .addGap(62, 125, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(81, 81, 81))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(kode_rak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nama_rak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txt_alamat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_fakultas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_prodi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_simpan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_hapus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_keluar))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pilihFoto)
                .addGap(29, 29, 29)
                .addComponent(btn_cetak)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void nama_rakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nama_rakActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nama_rakActionPerformed

    private void kode_rakKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kode_rakKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin
            cekdatauser();
            //panggil komponen yang akan di tuju
        }  //end if
    }//GEN-LAST:event_kode_rakKeyPressed

    private void nama_rakKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nama_rakKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin 
            txt_alamat.requestFocus();   //panggil komponen yang akan di tuju 
        }  //end if
    }//GEN-LAST:event_nama_rakKeyPressed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        // TODO add your handling code here:
        String idAnggota = kode_rak.getText().trim();
        String namaAnggota = nama_rak.getText().trim();
        String alamat = txt_alamat.getText().trim();
        String fakultas = txt_fakultas.getText().trim();
        String prodi = txt_prodi.getText().trim();
        String kelamin = "";

        if (jCheckBox1.isSelected() && !jCheckBox2.isSelected()) {
            kelamin = "pria";
        } else if (!jCheckBox1.isSelected() && jCheckBox2.isSelected()) {
            kelamin = "wanita";
        } else if (jCheckBox1.isSelected() && jCheckBox2.isSelected()) {
            JOptionPane.showMessageDialog(null, "Pilih salah satu jenis kelamin!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        } else {
            JOptionPane.showMessageDialog(null, "Jenis kelamin harus dipilih!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (idAnggota.length() != 8) {
            JOptionPane.showMessageDialog(null, "ID Anggota harus terdiri dari 8 karakter!",
                    "Validasi ID", JOptionPane.WARNING_MESSAGE);
            kode_rak.requestFocus();
            return;
        }

        if (idAnggota.isEmpty() || namaAnggota.isEmpty() || alamat.isEmpty()
                || fakultas.isEmpty() || prodi.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Semua field wajib diisi!",
                    "Validasi Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menyimpan data?",
                "Konfirmasi Simpan", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement pstSelect = con.prepareStatement("SELECT * FROM tanggota WHERE Id_anggota = ?");
                pstSelect.setString(1, idAnggota);
                ResultSet rs = pstSelect.executeQuery();

                if (rs.next()) {
                    int confirmUpdate = JOptionPane.showConfirmDialog(null,
                            "ID Anggota sudah terdaftar.\n"
                            + "Apakah Anda ingin memperbarui data ini?",
                            "Konfirmasi Update", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if (confirmUpdate == JOptionPane.YES_OPTION) {
                        PreparedStatement pstUpdate = con.prepareStatement(
                                "UPDATE tanggota SET Nama_anggota = ?, alamat = ?, fakultas = ?, prodi = ?, kelamin = ? WHERE Id_anggota = ?");
                        pstUpdate.setString(1, namaAnggota);
                        pstUpdate.setString(2, alamat);
                        pstUpdate.setString(3, fakultas);
                        pstUpdate.setString(4, prodi);
                        pstUpdate.setString(5, kelamin);
                        pstUpdate.setString(6, idAnggota);
                        pstUpdate.executeUpdate();
                        pstUpdate.close();

                        JOptionPane.showMessageDialog(null, "Data anggota berhasil diperbarui!",
                                "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    PreparedStatement pstInsert = con.prepareStatement(
                            "INSERT INTO tanggota (Id_anggota, Nama_anggota, alamat, fakultas, prodi, kelamin) VALUES (?, ?, ?, ?, ?, ?)");
                    pstInsert.setString(1, idAnggota);
                    pstInsert.setString(2, namaAnggota);
                    pstInsert.setString(3, alamat);
                    pstInsert.setString(4, fakultas);
                    pstInsert.setString(5, prodi);
                    pstInsert.setString(6, kelamin);
                    pstInsert.executeUpdate();
                    pstInsert.close();

                    JOptionPane.showMessageDialog(null, "Data anggota berhasil disimpan!",
                            "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }

                rs.close();
                pstSelect.close();

                datatojtable();
                bersihkantextfiled();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan data!\n" + e.getMessage(),
                        "Error Database", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        // TODO add your handling code here:
        String kodeRak = kode_rak.getText().trim();

        if (kodeRak.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Pilih data yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_perpustakaan", "root", "");
                String sql = "DELETE FROM tanggota WHERE Id_anggota = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, kodeRak);

                int rowsDeleted = pst.executeUpdate();
                pst.close();
                con.close();

                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(null, "Data berhasil dihapus!", "Hapus", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Data tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                }

                bersihkantextfiled();
                datatojtable();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_btn_hapusActionPerformed

    private void jTable_userMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_userMouseClicked
        // TODO add your handling code here:
        int selectedRow = jTable_user.getSelectedRow();
        if (selectedRow >= 0) {
            kode_rak.setText(jTable_user.getValueAt(selectedRow, 0).toString());
            nama_rak.setText(jTable_user.getValueAt(selectedRow, 1).toString());
            txt_alamat.setText(jTable_user.getValueAt(selectedRow, 2).toString());
            txt_fakultas.setText(jTable_user.getValueAt(selectedRow, 3).toString());
            txt_prodi.setText(jTable_user.getValueAt(selectedRow, 4).toString());

            String kelamin = jTable_user.getValueAt(selectedRow, 5).toString();
            jCheckBox1.setSelected(kelamin.equalsIgnoreCase("pria"));
            jCheckBox2.setSelected(kelamin.equalsIgnoreCase("wanita"));

            kodee_rak = jTable_user.getValueAt(selectedRow, 0).toString();
        }
    }//GEN-LAST:event_jTable_userMouseClicked

    private void btn_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_keluarActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin keluar?", "Konfirmasi keluar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();

        }
    }//GEN-LAST:event_btn_keluarActionPerformed

    private void txt_alamatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_alamatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_alamatActionPerformed

    private void txt_alamatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_alamatKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin 
            txt_fakultas.requestFocus();   //panggil komponen yang akan di tuju 
        }  //end if
    }//GEN-LAST:event_txt_alamatKeyPressed

    private void txt_fakultasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_fakultasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_fakultasActionPerformed

    private void txt_fakultasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_fakultasKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin 
            txt_prodi.requestFocus();   //panggil komponen yang akan di tuju 
        }  //end if
    }//GEN-LAST:event_txt_fakultasKeyPressed

    private void txt_prodiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_prodiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_prodiActionPerformed

    private void txt_prodiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_prodiKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin 
            jCheckBox1.requestFocus();   //panggil komponen yang akan di tuju 
        }  //end if
    }//GEN-LAST:event_txt_prodiKeyPressed

    private void jCheckBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCheckBox1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { // begin 
            jCheckBox2.requestFocus();   //panggil komponen yang akan di tuju 
        }  //end if
    }//GEN-LAST:event_jCheckBox1KeyPressed

    private void btn_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cetakActionPerformed
        // TODO add your handling code here:
        // Validasi data
        if (kode_rak.getText().trim().isEmpty() || nama_rak.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data anggota belum lengkap!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (fotoPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih foto terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Buat frame untuk kartu anggota
            JFrame frameKartu = new JFrame("Kartu Anggota Perpustakaan");
            frameKartu.setSize(400, 600);
            frameKartu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // mengatur frame
            Point lokasiFrameUtama = this.getLocationOnScreen();
            frameKartu.setLocation(lokasiFrameUtama.x - frameKartu.getWidth() - 10, lokasiFrameUtama.y);

            JPanel panelKartu = new JPanel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    RenderingHints rh = new RenderingHints(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHints(rh);

                    // Background gradient
                    GradientPaint gp = new GradientPaint(0, 0, new Color(230, 240, 255), 0, getHeight(), new Color(200, 220, 240));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    // Border
                    g2d.setColor(new Color(0, 102, 204));
                    g2d.setStroke(new BasicStroke(4f));
                    g2d.drawRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 25, 25);

                    // Header
                    g2d.setColor(new Color(0, 51, 102));
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
                    g2d.drawString("KARTU ANGGOTA", 120, 40);
                    g2d.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    g2d.drawString("PERPUSTAKAAN UNIVERSITAS X", 80, 60);

                    // Foto
                    try {
                        BufferedImage originalImage = ImageIO.read(new File(fotoPath));
                        int imageWidth = 120;
                        int imageHeight = 160;
                        int imageX = (getWidth() - imageWidth) / 2;
                        int imageY = 80;

                        Image scaledImage = originalImage.getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
                        g2d.drawImage(scaledImage, imageX, imageY, imageWidth, imageHeight, null);

                        // Frame foto
                        g2d.setColor(Color.GRAY);
                        g2d.drawRect(imageX - 1, imageY - 1, imageWidth + 2, imageHeight + 2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Data anggota
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    int textX = 40;
                    int textY = 270;
                    int lineSpacing = 25;

                    g2d.drawString("ID Anggota: " + kode_rak.getText(), textX, textY);
                    g2d.drawString("Nama     : " + nama_rak.getText(), textX, textY + lineSpacing);
                    g2d.drawString("Alamat   : " + txt_alamat.getText(), textX, textY + 2 * lineSpacing);
                    g2d.drawString("Fakultas : " + txt_fakultas.getText(), textX, textY + 3 * lineSpacing);
                    g2d.drawString("Prodi    : " + txt_prodi.getText(), textX, textY + 4 * lineSpacing);

                    String kelamin = jCheckBox1.isSelected() ? "Pria" : "Wanita";
                    g2d.drawString("Jenis Kelamin: " + kelamin, textX, textY + 5 * lineSpacing);

                    // Footer
                    g2d.setFont(new Font("SansSerif", Font.ITALIC, 12));
                    g2d.setColor(new Color(80, 80, 80));
                    g2d.drawString("Kartu ini berlaku selama menjadi anggota", 80, 500);
                    g2d.drawString("Perpustakaan Universitas XYZ", 110, 520);
                }
            };

            frameKartu.add(panelKartu);
            frameKartu.setVisible(true);

            // Opsi simpan
            int option = JOptionPane.showConfirmDialog(this,
                    "Apakah Anda ingin menyimpan kartu sebagai gambar?",
                    "Simpan Kartu", JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                JFileChooser saveChooser = new JFileChooser();
                saveChooser.setDialogTitle("Simpan Kartu Anggota");
                saveChooser.setSelectedFile(new File("Kartu_" + kode_rak.getText() + ".png"));

                if (saveChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = saveChooser.getSelectedFile();
                    String path = file.getAbsolutePath();

                    if (!path.toLowerCase().endsWith(".png")) {
                        path += ".png";
                    }

                    BufferedImage image = new BufferedImage(
                            panelKartu.getWidth(), panelKartu.getHeight(),
                            BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = image.createGraphics();
                    panelKartu.paint(g2d);
                    g2d.dispose();

                    ImageIO.write(image, "png", new File(path));
                    JOptionPane.showMessageDialog(this, "Kartu berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btn_cetakActionPerformed

    private void pilihFotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihFotoActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih Foto Anggota");

        // Filter hanya file gambar
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            fotoPath = selectedFile.getAbsolutePath();

            // Tampilkan preview foto
            ImageIcon icon = new ImageIcon(fotoPath);
            Image img = icon.getImage().getScaledInstance(lblFoto.getWidth(), lblFoto.getHeight(), Image.SCALE_SMOOTH);
            lblFoto.setIcon(new ImageIcon(img));
        }
    }//GEN-LAST:event_pilihFotoActionPerformed

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
            java.util.logging.Logger.getLogger(JFrame_Anggota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_Anggota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_Anggota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_Anggota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrame_Anggota().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cetak;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_keluar;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_user;
    private javax.swing.JTextField kode_rak;
    private javax.swing.JTextField nama_rak;
    private javax.swing.JButton pilihFoto;
    private javax.swing.JTextField txt_alamat;
    private javax.swing.JTextField txt_fakultas;
    private javax.swing.JTextField txt_prodi;
    // End of variables declaration//GEN-END:variables
}
