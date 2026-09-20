package store.GUI;

import store.business_logic.controllers.NavigationManager;
import store.business_logic.controllers.UserController;
import store.domain_model.inventory.InventoryItem;
import store.domain_model.logistics.Position;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ManageUnallocatedItemsPanelGUI extends JPanel {
    private UserController userController;
    private final NavigationManager navigationManager;

    public ManageUnallocatedItemsPanelGUI(UserController userController, NavigationManager navigationManager) {
        setLayout(null);
        setBackground(CommonCostants.PRIMARY_COLOR);
        this.userController = userController;
        this.navigationManager = navigationManager;
        addGUIComponents();
    }

    public void displaySearchPositionsTable(Consumer<Position> onConfirm) {
        List<Position> availablePositions = userController.getAvailablePositions();

        if (availablePositions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "There are no positions available.",
                    "No positions available", JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog selectionDialog = new JDialog((Frame) parentWindow, "Search positions", true);
        selectionDialog.setSize(500, 300);
        selectionDialog.setLayout(new BorderLayout());

        String[] cols = {"ID", "Storage Structure", "Description", "Remaining capacity"};
        DefaultTableModel tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (Position pos : availablePositions) {
            Object[] rowData = {
                    pos.getId(),
                    pos.getShelfId(),
                    pos.getDescription(),
                    pos.getCapacity() - userController.getTotalPlacedQuantityInPosition(pos.getId()),
            };
            tableModel.addRow(rowData);
        }

        JTable selectionTable = new JTable(tableModel);
        selectionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionTable.setFont(new Font("Dialog", Font.PLAIN, 14));
        selectionTable.setRowHeight(25);
        selectionDialog.add(new JScrollPane(selectionTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton confirmBtn = new JButton("Confirm");
        JButton cancelBtn = new JButton("Cancel");
        buttonPanel.add(confirmBtn);
        buttonPanel.add(cancelBtn);
        selectionDialog.add(buttonPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(_ -> selectionDialog.dispose());

        confirmBtn.addActionListener(_ -> {
            int selectedRow = selectionTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(selectionDialog, "Please select a position from the list.");
                return;
            }

            String selectedId = (String) selectionTable.getValueAt(selectedRow, 0);
            Position selectedPos = availablePositions.stream()
                    .filter(pp -> pp.getId().equals(selectedId))
                    .findFirst().orElse(null);

            selectionDialog.dispose();

            if (selectedPos != null) {
                onConfirm.accept(selectedPos);
            }
        });

        selectionDialog.setLocationRelativeTo(this);
        selectionDialog.setVisible(true);
    }

    public void addGUIComponents() {
        JLabel titleLabel = new JLabel("Unallocated items");
        titleLabel.setBounds(300, 10, 520, 100);
        titleLabel.setForeground(CommonCostants.TEXT_COLOR);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        add(titleLabel);

        JLabel label = new JLabel("These items are all stored in the pending zone.");
        label.setBounds(300, 100, 520, 100);
        label.setForeground(CommonCostants.TEXT_COLOR);
        label.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(label);


        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Dialog", Font.BOLD, 18));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setBackground(CommonCostants.TEXT_COLOR);
        backButton.setBounds(25, 25, 120, 40);
        backButton.addActionListener(_ -> navigationManager.showHomeScreen());
        add(backButton);



        List<String> unallocatedItems = userController.getUnallocatedItems();

        if (unallocatedItems.isEmpty()) {
            JLabel noUnallocatedItemsLabel = new JLabel("There are no unallocated items");
            noUnallocatedItemsLabel.setBounds(100, 200, 120, 100);
            noUnallocatedItemsLabel.setForeground(CommonCostants.TEXT_COLOR);
            noUnallocatedItemsLabel.setFont(new Font("Dialog", Font.BOLD, 18));
            add(noUnallocatedItemsLabel);
        }

        String[] cols = {"Item ID", "Name", "Quantity"};
        DefaultTableModel tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (String itemId : unallocatedItems) {
            InventoryItem item = userController.getItemById(itemId);
            if(item.getUnallocatedQuantity() > 0) {
                Object[] rowData = {item.getProduct().getId(), item.getProduct().getName(), item.getUnallocatedQuantity()};
                tableModel.addRow(rowData);
            }
        }

        JTable selectionTable = new JTable(tableModel);
        selectionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionTable.setFont(new Font("Dialog", Font.PLAIN, 14));
        selectionTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(selectionTable);
        scrollPane.setBounds(20, 200, 900, 300);
        add(scrollPane);

        JButton moveItemsButton = new JButton("Move items");
        moveItemsButton.setFont(new Font("Dialog", Font.BOLD, 18));
        moveItemsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        moveItemsButton.setBackground(CommonCostants.TEXT_COLOR);
        moveItemsButton.setBounds(750, 520, 170, 40);
        moveItemsButton.addActionListener(e -> {

            int selectedRow = selectionTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(ManageUnallocatedItemsPanelGUI.this, "Please select an item from the table.");
                return;
            }

            String productId = (String) tableModel.getValueAt(selectedRow, 0);
            InventoryItem itemToMove = userController.getItemById(productId);

            int maxMovable = (int) tableModel.getValueAt(selectedRow, 2);

            String qtyInput = JOptionPane.showInputDialog(
                    ManageUnallocatedItemsPanelGUI.this,
                    "How many items to move?\n(Max available: " + maxMovable + ")",
                    "Move Items", JOptionPane.QUESTION_MESSAGE
            );

            if (qtyInput != null && !qtyInput.trim().isEmpty()) {
                try {
                    int qty = Integer.parseInt(qtyInput.trim());

                    Position pendingZone = new Position("P00054", "Backstock", "Pending Zone", 999);

                    displaySearchPositionsTable(selectedDestPos -> {
                        try {
                            if (userController.moveItem(pendingZone, selectedDestPos, itemToMove, qty, maxMovable)) {

                                int remaining = maxMovable - qty;
                                if (remaining == 0) {
                                    tableModel.removeRow(selectedRow);
                                } else {
                                    tableModel.setValueAt(remaining, selectedRow, 2);
                                }

                                JOptionPane.showMessageDialog(ManageUnallocatedItemsPanelGUI.this, "Items moved successfully!");
                            } else {
                                JOptionPane.showMessageDialog(ManageUnallocatedItemsPanelGUI.this, "Move failed. Check destination capacity.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (IllegalArgumentException ex) {
                            JOptionPane.showMessageDialog(ManageUnallocatedItemsPanelGUI.this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                        }

                    });

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ManageUnallocatedItemsPanelGUI.this, "Invalid number format.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(moveItemsButton);

    }
}
