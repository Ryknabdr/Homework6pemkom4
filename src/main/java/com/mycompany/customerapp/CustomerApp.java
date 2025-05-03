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
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        JTextField namaField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField telpField = new JTextField();

        inputPanel.add(new JLabel("Nama:"));
        inputPanel.add(namaField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Telepon:"));
        inputPanel.add(telpField);

        JButton tambahBtn = new JButton("Tambah Customer");
        inputPanel.add(tambahBtn);

        add(inputPanel, BorderLayout.NORTH);

        // Tabel data customer
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Email", "Telepon"}, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Tombol hapus
        JButton hapusBtn = new JButton("Hapus Customer Terpilih");
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
