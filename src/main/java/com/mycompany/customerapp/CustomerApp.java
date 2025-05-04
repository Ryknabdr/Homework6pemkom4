/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.customerapp;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author abdurraihan
 */
public class CustomerApp extends JFrame {

    private MongoCollection<Document> collection;
    private DefaultTableModel tableModel;

    public CustomerApp() {
        // Koneksi MongoDB
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("db_customer");
        collection = database.getCollection("customer");

        setTitle("Aplikasi Data Customer");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Form input
        /// Form input (dengan GridBagLayout supaya rapi)
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField namaField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField telpField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Nama:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(namaField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Telepon:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(telpField, gbc);

// Tombol-tombol (di bawah form, rapi)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton tambahBtn = new JButton("Tambah ");
        JButton updateBtn = new JButton("Update ");
        JButton clearBtn = new JButton("Clear ");
        buttonPanel.add(tambahBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(clearBtn);
// Set warna tombol
        tambahBtn.setBackground(new Color(46, 204, 113));   // Hijau
        tambahBtn.setForeground(Color.WHITE);

        updateBtn.setBackground(new Color(52, 152, 219));   // Biru
        updateBtn.setForeground(Color.WHITE);

        clearBtn.setBackground(new Color(243, 156, 18));   // Oranye
        clearBtn.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // Tabel data customer
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Email", "Telepon"}, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Tombol hapus
        JButton hapusBtn = new JButton("Hapus Customer Terpilih");
        hapusBtn.setBackground(new Color(231, 76, 60)); // Merah
        hapusBtn.setForeground(Color.WHITE);
        add(hapusBtn, BorderLayout.SOUTH);

        // Event: Tambah
        tambahBtn.addActionListener(e -> {
            Document doc = new Document("nama", namaField.getText())
                    .append("email", emailField.getText())
                    .append("telepon", telpField.getText());
            collection.insertOne(doc);
            loadData();
            namaField.setText("");
            emailField.setText("");
            telpField.setText("");
        });

        // Event: Hapus
        hapusBtn.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                String id = tableModel.getValueAt(selected, 0).toString();
                collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
                loadData();
            }
        });

        // Event: Update
        updateBtn.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                String id = tableModel.getValueAt(selected, 0).toString();
                Document updateDoc = new Document("$set", new Document("nama", namaField.getText())
                        .append("email", emailField.getText())
                        .append("telepon", telpField.getText()));
                collection.updateOne(Filters.eq("_id", new ObjectId(id)), updateDoc);
                loadData();
                namaField.setText("");
                emailField.setText("");
                telpField.setText("");
                table.clearSelection();
            } else {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin diupdate terlebih dahulu.");
            }
        });

        // Event: Clear
        clearBtn.addActionListener(e -> {
            namaField.setText("");
            emailField.setText("");
            telpField.setText("");
            table.clearSelection();
        });

        // Event: Klik baris tabel → isi ke form
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    namaField.setText(tableModel.getValueAt(row, 1).toString());
                    emailField.setText(tableModel.getValueAt(row, 2).toString());
                    telpField.setText(tableModel.getValueAt(row, 3).toString());
                }
            }
        });

        // Load data saat mulai
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Document doc : collection.find()) {
            tableModel.addRow(new Object[]{
                doc.getObjectId("_id").toString(),
                doc.getString("nama"),
                doc.getString("email"),
                doc.getString("telepon")
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerApp().setVisible(true));
    }
}
