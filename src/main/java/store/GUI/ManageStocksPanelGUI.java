package store.GUI;

import store.business_logic.controllers.NavigationManager;
import store.business_logic.controllers.UserController;
import store.domain_model.inventory.InventoryItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ManageStocksPanelGUI extends JPanel {
    private List<InventoryItem> lowStockItems;
    private final UserController userController;
    private final NavigationManager navigationManager;

    public ManageStocksPanelGUI(List<InventoryItem> lowStockItems, UserController userController, NavigationManager navigationManager) {
        setLayout(null);
        setBackground(CommonCostants.PRIMARY_COLOR);
        this.lowStockItems = lowStockItems;
        this.userController = userController;
        this.navigationManager = navigationManager;
        addGUIComponents();
    }

    public void addGUIComponents() {
        JLabel titleLabel = new JLabel("Manage stocks");
        titleLabel.setBounds(300, 10, 520, 100);
        titleLabel.setForeground(CommonCostants.TEXT_COLOR);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        add(titleLabel);

        JLabel itemRestockLabel = new JLabel("Items that need a restock: ");
        itemRestockLabel.setBounds(20,100, 400, 25);
        itemRestockLabel.setForeground(CommonCostants.TEXT_COLOR);
        itemRestockLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        add(itemRestockLabel);

        String[] columnNames = {"ID", "Name", "Total Quantity", "Critical Threshold", "ItemObject"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable lowStockTable = new JTable(tableModel);
        lowStockTable.setFont(new Font("Dialog", Font.PLAIN, 14));
        lowStockTable.setRowHeight(30);
        lowStockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lowStockTable.getColumnModel().removeColumn(lowStockTable.getColumnModel().getColumn(4));

        for (InventoryItem item : lowStockItems) {
            Object[] rowData = {
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getTotalQuantity(),
                    item.getCriticalThreshold(),
                    item
            };
            tableModel.addRow(rowData);
        }

        JScrollPane scrollPane = new JScrollPane(lowStockTable);
        scrollPane.setBounds(20, 140, 800, 300);
        add(scrollPane);

        JButton restockButton = new JButton("Order new stock");
        restockButton.setFont(new Font("Dialog", Font.BOLD, 18));
        restockButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        restockButton.setBounds(620, 500, 200, 50);

        restockButton.addActionListener(_ -> {
            int selectedRow = lowStockTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(ManageStocksPanelGUI.this,
                        "Please select an item from the table first.",
                        "No item selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            InventoryItem selectedItem = (InventoryItem) tableModel.getValueAt(selectedRow, 4);
            String input = JOptionPane.showInputDialog(
                    ManageStocksPanelGUI.this,
                    "How many items would you like to order?",
                    "Restock item",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (input != null && !input.trim().isEmpty()) {
                try {
                    int qty = Integer.parseInt(input.trim());

                    if (userController.restockItem(selectedItem, qty)) {
                        userController.autoAllocateToBackstock(selectedItem.getProduct().getId(), qty);
                        JOptionPane.showMessageDialog(ManageStocksPanelGUI.this, qty + " " + selectedItem.getProduct().getName() + " ordered. Items will automatically be placed in an available backstock structure.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ManageStocksPanelGUI.this, "Please insert a valid number.", "Format error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(ManageStocksPanelGUI.this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        add(restockButton);

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Dialog", Font.BOLD, 18));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setBackground(CommonCostants.TEXT_COLOR);
        backButton.setBounds(25, 25, 120, 40);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigationManager.showInventory();
            }
        });
        add(backButton);
    }
}
