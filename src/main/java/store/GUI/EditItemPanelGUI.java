package store.GUI;

import store.business_logic.controllers.NavigationManager;
import store.business_logic.controllers.UserController;
import store.domain_model.inventory.*;
import store.domain_model.logistics.Position;
import store.domain_model.logistics.PositionProduct;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class EditItemPanelGUI extends JPanel {
    private final InventoryItem item;
    private final UserController userController;
    private final NavigationManager navigationManager;

    private JTextField nameTextField;
    private JTextField brandTextField;
    private JTextField priceTextField;
    private JComboBox<String> sizeComboBox;
    private JComboBox<String> colorComboBox;
    private JTextField expirationDateTextField;
    private JTextField typeTextField;
    private JTextField heightTextField;
    private JTextField widthTextField;
    private JTextField lengthTextField;


    public EditItemPanelGUI(
            InventoryItem item,
            UserController userController,
            NavigationManager navigationManager
    ) {
        this.item = item;
        this.userController = userController;
        this.navigationManager = navigationManager;
        setLayout(null);
        setBackground(CommonCostants.PRIMARY_COLOR);
        addGUIComponents();
    }

    public void displayItemPositionsTable(String title, String confirmButtonText, Consumer<PositionProduct> onConfirm) {
        List<PositionProduct> currentAllocations = item.getAllocations();

        if (currentAllocations.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "There are no items allocated in any position.",
                    "Empty", JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog selectionDialog = new JDialog((Frame) parentWindow, title, true);
        selectionDialog.setSize(500, 300);
        selectionDialog.setLayout(new BorderLayout());

        String[] cols = {"Position ID", "Description", "Quantity"};
        DefaultTableModel tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (PositionProduct alloc : currentAllocations) {
            if (alloc.getQuantity() > 0) {
                Object[] rowData = { alloc.getPosition().getId(), alloc.getPosition().getDescription(), alloc.getQuantity() };
                tableModel.addRow(rowData);
            }
        }

        JTable selectionTable = new JTable(tableModel);
        selectionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionTable.setFont(new Font("Dialog", Font.PLAIN, 14));
        selectionTable.setRowHeight(25);
        selectionDialog.add(new JScrollPane(selectionTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton confirmBtn = new JButton(confirmButtonText);
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
            PositionProduct selectedAlloc = currentAllocations.stream()
                    .filter(pp -> pp.getPosition().getId().equals(selectedId))
                    .findFirst().orElse(null);

            selectionDialog.dispose();

            if (selectedAlloc != null) {
                onConfirm.accept(selectedAlloc);
            }
        });

        selectionDialog.setLocationRelativeTo(this);
        selectionDialog.setVisible(true);
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

        Product product = item.getProduct();

        JLabel titleLabel = new JLabel("Edit Item");
        titleLabel.setBounds(400, 10, 520, 100);
        titleLabel.setForeground(CommonCostants.TEXT_COLOR);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        add(titleLabel);

        JLabel idLabel = new JLabel("ID: " + product.getId());
        idLabel.setBounds(20, 100, 400, 25);
        idLabel.setForeground(CommonCostants.TEXT_COLOR);
        idLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(idLabel);

        JLabel nameLabel = new JLabel("Name: ");
        nameLabel.setBounds(20, 150, 400, 25);
        nameLabel.setForeground(CommonCostants.TEXT_COLOR);
        nameLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(nameLabel);

        nameTextField = new JTextField(product.getName());
        nameTextField.setBounds(75, 150, 200, 35);
        nameTextField.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(nameTextField);

        JLabel positionLabel = new JLabel("Positions: ");
        positionLabel.setBounds(20, 200, 400, 25);
        positionLabel.setForeground(CommonCostants.TEXT_COLOR);
        positionLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(positionLabel);

        if(item.isUnderCriticalThreshold()) {

            JLabel criticalThresholdLabel = new JLabel("WARNING: this item is under the critical threshold.");
            criticalThresholdLabel.setBounds(20, 600, 400, 25);
            criticalThresholdLabel.setForeground(CommonCostants.TEXT_COLOR);
            criticalThresholdLabel.setFont(new Font("Dialog", Font.BOLD, 18));
            add(criticalThresholdLabel);

        }

        String[] columnNames = {"ID", "Storage Structure", "Description", "Remaining capacity", "Quantity"};
        List<PositionProduct> allocations = item.getAllocations();
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for(PositionProduct alloc : allocations) {
            if (alloc.getQuantity() > 0) {
                Position pos = alloc.getPosition();

                Object[] rowData = {
                        pos.getId(),
                        pos.getShelfId(),
                        pos.getDescription(),
                        pos.getCapacity() - userController.getTotalPlacedQuantityInPosition(pos.getId()),
                        alloc.getQuantity()
                };
                tableModel.addRow(rowData);
            }
        }
        JTable positionsTable = new JTable(tableModel);
        positionsTable.setFont(new Font("Dialog", Font.PLAIN, 14));
        positionsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(positionsTable);
        scrollPane.setBounds(20, 250, 600, 250);
        add(scrollPane);

        JLabel brandLabel = new JLabel("Brand: ");
        brandLabel.setBounds(500, 100, 400, 25);
        brandLabel.setForeground(CommonCostants.TEXT_COLOR);
        brandLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(brandLabel);

        brandTextField = new JTextField(product.getBrand());
        brandTextField.setBounds(575, 100, 200, 35);
        brandTextField.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(brandTextField);

        JLabel priceLabel = new JLabel("Price: ");
        priceLabel.setBounds(500, 150, 400, 25);
        priceLabel.setForeground(CommonCostants.TEXT_COLOR);
        priceLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(priceLabel);

        priceTextField = new JTextField(String.valueOf(product.getPrice()));
        priceTextField.setBounds(575, 150, 200, 35);
        priceTextField.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(priceTextField);

        JLabel totalQuantityLabel = new JLabel("Total quantity: " + item.getTotalQuantity());
        totalQuantityLabel.setBounds(500, 200, 400, 25);
        totalQuantityLabel.setForeground(CommonCostants.TEXT_COLOR);
        totalQuantityLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(totalQuantityLabel);

        JButton orderStockButton = new JButton("Order new stock");
        orderStockButton.setFont(new Font("Dialog", Font.BOLD, 15));
        orderStockButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        orderStockButton.setBackground(CommonCostants.TEXT_COLOR);
        orderStockButton.setBounds(650, 200, 170, 40);
        orderStockButton.addActionListener(_ -> {
            String input = JOptionPane.showInputDialog(
                    EditItemPanelGUI.this,
                    "How many items would you like to order?",
                    "Restock item",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (input != null && !input.trim().isEmpty()) {
                try {
                    int qty = Integer.parseInt(input.trim());

                    if (userController.restockItem(item, qty)) {
                        userController.autoAllocateToBackstock(item.getProduct().getId(), qty);
                        JOptionPane.showMessageDialog(EditItemPanelGUI.this, qty + " " + item.getProduct().getName() + " ordered. Items will automatically be placed in an available backstock structure.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(EditItemPanelGUI.this, "Please insert a valid number.", "Format error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(EditItemPanelGUI.this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        add(orderStockButton);

        JButton addItemsButton = new JButton("+");
        addItemsButton.setFont(new Font("Dialog", Font.BOLD, 15));
        addItemsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addItemsButton.setBackground(CommonCostants.TEXT_COLOR);
        addItemsButton.setBounds(850, 200, 50, 40);
        addItemsButton.addActionListener(_ -> {
            String quantityInput = JOptionPane.showInputDialog(
                    EditItemPanelGUI.this,
                    "How many items would you like to add?",
                    "Add additional items",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (quantityInput != null && !quantityInput.trim().isEmpty()) {

                displaySearchPositionsTable(selectedPosition -> {
                    try {
                        int qty = Integer.parseInt(quantityInput.trim());

                        if (userController.restockItem(item, qty)) {

                            if (userController.placeItem(selectedPosition, item, qty)) {

                                JOptionPane.showMessageDialog(EditItemPanelGUI.this, "Item added and placed successfully!");

                            } else {
                                JOptionPane.showMessageDialog(EditItemPanelGUI.this, "Position full. Please choose a different position.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(EditItemPanelGUI.this, "Please insert a valid number.", "Format error", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(EditItemPanelGUI.this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                    }
                });
            }
        });
        add(addItemsButton);

        JButton removeItemsButton = new JButton("-");
        removeItemsButton.setFont(new Font("Dialog", Font.BOLD, 15));
        removeItemsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeItemsButton.setBackground(CommonCostants.TEXT_COLOR);
        removeItemsButton.setBounds(910, 200, 50, 40);
        removeItemsButton.addActionListener(e -> {
            displayItemPositionsTable("Select Position to Remove From", "Remove", selectedAlloc -> {

                String posId = selectedAlloc.getPosition().getId();
                int maxRemovable = selectedAlloc.getQuantity();

                String qtyInput = JOptionPane.showInputDialog(
                        EditItemPanelGUI.this,
                        "How many items to remove from " + posId + "?\n(Max: " + maxRemovable + ")",
                        "Remove Items", JOptionPane.QUESTION_MESSAGE
                );

                if (qtyInput != null && !qtyInput.trim().isEmpty()) {
                    try {
                        int qty = Integer.parseInt(qtyInput.trim());

                        if (userController.decreaseStock(item, qty, maxRemovable)) {
                            if (userController.removeItem(posId, item.getProduct().getId(), qty)) {
                                selectedAlloc.setQuantity(maxRemovable - qty);
                                JOptionPane.showMessageDialog(EditItemPanelGUI.this, "Items removed!");
                            } else {
                                JOptionPane.showMessageDialog(EditItemPanelGUI.this, "Database error.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(EditItemPanelGUI.this, "Please insert a valid number.", "Format error", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(EditItemPanelGUI.this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });
        });
        add(removeItemsButton);

        switch (product) {
            case Clothes c -> {
                JLabel sizeLabel = new JLabel("Size: ");
                sizeLabel.setBounds(630, 250, 400, 25);
                sizeLabel.setForeground(CommonCostants.TEXT_COLOR);
                sizeLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
                add(sizeLabel);

                String[] sizeOptions = {"XS", "S", "M", "L", "XL", "XXL"};
                sizeComboBox = new JComboBox<>(sizeOptions);
                sizeComboBox.setSelectedItem(c.getSize());
                sizeComboBox.setBounds(690, 250, 200, 35);
                sizeComboBox.setFont(new Font("Dialog", Font.PLAIN, 20));
                add(sizeComboBox);

                JLabel colorLabel = new JLabel("Color: ");
                colorLabel.setBounds(630, 300, 400, 25);
                colorLabel.setForeground(CommonCostants.TEXT_COLOR);
                colorLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
                add(colorLabel);

                String[] colorOptions = {"Black", "White", "Red", "Blue", "Green", "Grey", "Denim Blue"};
                colorComboBox = new JComboBox<>(colorOptions);
                colorComboBox.setSelectedItem(c.getColor());
                colorComboBox.setBounds(690, 300, 200, 35);
                colorComboBox.setFont(new Font("Dialog", Font.PLAIN, 20));
                add(colorComboBox);

            }

            case Food food -> {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = sdf.format(food.getExpirationDate());

                JLabel expirationDateLabel = new JLabel("Expiration date: ");
                expirationDateLabel.setBounds(630, 250, 200, 25);
                expirationDateLabel.setForeground(CommonCostants.TEXT_COLOR);
                expirationDateLabel.setFont(new Font("Dialog", Font.PLAIN, 15));
                add(expirationDateLabel);

                expirationDateTextField = new JTextField(String.valueOf(food.getExpirationDate()));
                expirationDateTextField.setBounds(750, 250, 200, 35);
                expirationDateTextField.setFont(new Font("Dialog", Font.PLAIN, 20));
                add(expirationDateTextField);

                JLabel typeLabel = new JLabel("Type: ");
                typeLabel.setBounds(630, 300, 400, 25);
                typeLabel.setForeground(CommonCostants.TEXT_COLOR);
                typeLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
                add(typeLabel);

                typeTextField = new JTextField(food.getType());
                typeTextField.setBounds(715, 300, 200, 35);
                typeTextField.setFont(new Font("Dialog", Font.PLAIN, 20));
                add(typeTextField);

            }

            case Furniture furniture -> {
                JLabel heightLabel = new JLabel("Height: ");
                heightLabel.setBounds(630, 250, 400, 25);
                heightLabel.setForeground(CommonCostants.TEXT_COLOR);
                heightLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
                add(heightLabel);

                heightTextField = new JTextField(String.valueOf(furniture.getHeight()));
                heightTextField.setBounds(715, 250, 200, 35);
                heightTextField.setFont(new Font("Dialog", Font.PLAIN, 20));
                add(heightTextField);

                JLabel widthLabel = new JLabel("Width: ");
                widthLabel.setBounds(630, 300, 400, 25);
                widthLabel.setForeground(CommonCostants.TEXT_COLOR);
                widthLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
                add(widthLabel);

                widthTextField = new JTextField(String.valueOf(furniture.getWidth()));
                widthTextField.setBounds(715, 300, 200, 35);
                widthTextField.setFont(new Font("Dialog", Font.PLAIN, 20));
                add(widthTextField);

                JLabel lengthLabel = new JLabel("Length: ");
                lengthLabel.setBounds(630, 350, 400, 25);
                lengthLabel.setForeground(CommonCostants.TEXT_COLOR);
                lengthLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
                add(lengthLabel);

                lengthTextField = new JTextField(String.valueOf(furniture.getLength()));
                lengthTextField.setBounds(715, 350, 200, 35);
                lengthTextField.setFont(new Font("Dialog", Font.PLAIN, 20));
                add(lengthTextField);

                JLabel colorLabel = new JLabel("Color: ");
                colorLabel.setBounds(630, 400, 400, 25);
                colorLabel.setForeground(CommonCostants.TEXT_COLOR);
                colorLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
                add(colorLabel);

                String[] furnitureColorOptions = {"Black", "White", "Red", "Blue", "Green", "Grey", "Denim Blue"};
                colorComboBox = new JComboBox<>(furnitureColorOptions);
                colorComboBox.setSelectedItem(furniture.getColor());
                colorComboBox.setBounds(715, 400, 200, 35);
                colorComboBox.setFont(new Font("Dialog", Font.PLAIN, 20));
                add(colorComboBox);

            }
            default -> throw new IllegalStateException("Unexpected value: " + product);
        }

        JButton saveChangesButton = new JButton("Save changes");
        saveChangesButton.setFont(new Font("Dialog", Font.BOLD, 18));
        saveChangesButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveChangesButton.setBackground(CommonCostants.TEXT_COLOR);
        saveChangesButton.setBounds(700, 550, 200, 40);
        saveChangesButton.addActionListener(_ -> {
            try {
                String newName = nameTextField.getText().trim();
                String newBrand = brandTextField.getText().trim();
                double newPrice = Double.parseDouble(priceTextField.getText().trim());

                Product updatedProduct = null;

                switch (product) {
                    case Clothes c -> {
                        String newColor = colorComboBox.getSelectedItem() != null ? colorComboBox.getSelectedItem().toString() : c.getColor();
                        String newSize = sizeComboBox.getSelectedItem() != null ? sizeComboBox.getSelectedItem().toString() : c.getSize();
                        updatedProduct = new Clothes(c.getId(), newName, newBrand, newPrice, newColor, newSize);
                    }
                    case Food fd -> {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        sdf.setLenient(false);
                        Date newParsedDate = sdf.parse(expirationDateTextField.getText().trim());
                        String newType = typeTextField.getText().trim();
                        updatedProduct = new Food(fd.getId(), newName, newBrand, newPrice, newParsedDate, newType);
                    }
                    case Furniture fr -> {
                        String newColor = colorComboBox.getSelectedItem() != null ? colorComboBox.getSelectedItem().toString() : fr.getColor();
                        float newHeight = Float.parseFloat(heightTextField.getText().trim());
                        float newWidth = Float.parseFloat(widthTextField.getText().trim());
                        float newLength = Float.parseFloat(lengthTextField.getText().trim());
                        updatedProduct = new Furniture(fr.getId(), newName, newBrand, newPrice, newHeight, newWidth, newLength, newColor);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + product);
                }

                if (userController.editItem(item, updatedProduct)) {
                    JOptionPane.showMessageDialog(EditItemPanelGUI.this, "Item edited successfully!");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(EditItemPanelGUI.this, "Price and dimensions must be valid numbers.", "Format Error", JOptionPane.ERROR_MESSAGE);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(EditItemPanelGUI.this, "Please insert a valid date (yyyy-MM-dd).", "Format Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(EditItemPanelGUI.this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        add(saveChangesButton);


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

        JButton moveItemsButton = new JButton("Move items");
        moveItemsButton.setFont(new Font("Dialog", Font.BOLD, 18));
        moveItemsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        moveItemsButton.setBackground(CommonCostants.TEXT_COLOR);
        moveItemsButton.setBounds(50, 550, 150, 40);
        moveItemsButton.addActionListener(e -> {
            displayItemPositionsTable("Select Source Position", "Select Source", selectedAlloc -> {

                Position sourcePos = selectedAlloc.getPosition();
                int maxMovable = selectedAlloc.getQuantity();

                String qtyInput = JOptionPane.showInputDialog(
                        EditItemPanelGUI.this,
                        "How many items to move from " + sourcePos.getId() + "?\n(Max: " + maxMovable + ")",
                        "Move Items", JOptionPane.QUESTION_MESSAGE
                );

                if (qtyInput != null && !qtyInput.trim().isEmpty()) {
                    try {
                        int qty = Integer.parseInt(qtyInput.trim());

                        displaySearchPositionsTable(selectedDestPos -> {
                            try {
                                if (userController.moveItem(sourcePos, selectedDestPos, item, qty, maxMovable)) {
                                    JOptionPane.showMessageDialog(EditItemPanelGUI.this, "Items moved successfully!");
                                } else {
                                    JOptionPane.showMessageDialog(EditItemPanelGUI.this, "Move failed. Check destination capacity.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (IllegalArgumentException ex) {
                                JOptionPane.showMessageDialog(EditItemPanelGUI.this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                            }
                        });

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(EditItemPanelGUI.this, "Please insert a valid number.", "Format error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        });
        add(moveItemsButton);
        add(moveItemsButton);
    }
}
