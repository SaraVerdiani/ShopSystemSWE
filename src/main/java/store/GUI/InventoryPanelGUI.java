package store.GUI;

import store.business_logic.controllers.NavigationManager;
import store.business_logic.filter.ProductFilter;
import store.business_logic.controllers.UserController;
import store.domain_model.inventory.*;
import store.domain_model.logistics.PositionProduct;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public class InventoryPanelGUI extends JPanel {

    private final UserController userController;
    private final NavigationManager navigationManager;

    public InventoryPanelGUI(UserController userController, NavigationManager navigationManager) {
        this.userController = userController;
        this.navigationManager = navigationManager;
        setLayout(null);
        setBackground(CommonCostants.PRIMARY_COLOR);
        addGUIComponents();

    }



    public void addGUIComponents() {
        JTextField searchField = new JTextField();
        searchField.setBounds(20, 120, 450, 55);
        searchField.setBackground(CommonCostants.SECONDARY_COLOR);
        searchField.setForeground(CommonCostants.TEXT_COLOR);
        searchField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchField);


        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Dialog", Font.BOLD, 18));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setBackground(CommonCostants.TEXT_COLOR);
        backButton.setBounds(25, 25, 120, 40);
        backButton.addActionListener(_ -> navigationManager.showHomeScreen());
        add(backButton);



        JLabel titleLabel = new JLabel("Inventory");
        titleLabel.setBounds(400, 10, 520, 100);
        titleLabel.setForeground(CommonCostants.TEXT_COLOR);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        add(titleLabel);

        JLabel searchLabel = new JLabel("Search for products:");
        searchLabel.setBounds(20, 80, 400, 25);
        searchLabel.setForeground(CommonCostants.TEXT_COLOR);
        searchLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(searchLabel);

        JLabel searchedProductNameLabel = new JLabel("");
        searchedProductNameLabel.setBounds(20, 200, 400, 25);
        searchedProductNameLabel.setForeground(CommonCostants.TEXT_COLOR);
        searchedProductNameLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(searchedProductNameLabel);

        JLabel searchedProductBrandLabel = new JLabel("");
        searchedProductBrandLabel.setBounds(20, 230, 400, 25);
        searchedProductBrandLabel.setForeground(CommonCostants.TEXT_COLOR);
        searchedProductBrandLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(searchedProductBrandLabel);

        JLabel searchedProductPriceLabel = new JLabel("");
        searchedProductPriceLabel.setBounds(20, 260, 400, 25);
        searchedProductPriceLabel.setForeground(CommonCostants.TEXT_COLOR);
        searchedProductPriceLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(searchedProductPriceLabel);

        JLabel searchedProductTotalQuantityLabel = new JLabel("");
        searchedProductTotalQuantityLabel.setBounds(20, 290, 400, 25);
        searchedProductTotalQuantityLabel.setForeground(CommonCostants.TEXT_COLOR);
        searchedProductTotalQuantityLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(searchedProductTotalQuantityLabel);

        String[] colorOptions = {" Select Color ", "Black", "White", "Red", "Blue", "Green", "Grey", "Denim Blue"};
        JComboBox<String> colorComboBox = new JComboBox<>(colorOptions);
        colorComboBox.setBounds(610, 120, 150, 30);
        colorComboBox.setFont(new Font("Dialog", Font.PLAIN, 14));
        colorComboBox.setBackground(Color.WHITE);
        add(colorComboBox);

        String[] sizeOptions = {" Select Size ", "XS", "S", "M", "L", "XL", "XXL"};
        JComboBox<String> sizeComboBox = new JComboBox<>(sizeOptions);
        sizeComboBox.setBounds(800, 120, 150, 30);
        sizeComboBox.setFont(new Font("Dialog", Font.PLAIN, 14));
        sizeComboBox.setBackground(Color.WHITE);
        add(sizeComboBox);

        String[] columnNames = {"ID", "Name", "Details", "Positions", "Quantity", "Item"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable resultsTable = new JTable(tableModel);
        resultsTable.setFont(new Font("Dialog", Font.PLAIN, 14));
        resultsTable.setRowHeight(30);
        resultsTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 14));
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.getColumnModel().removeColumn(resultsTable.getColumnModel().getColumn(5));
        resultsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int selectedRow = resultsTable.getSelectedRow();
                    if(selectedRow != -1) {
                        InventoryItem clickedItem = (InventoryItem) tableModel.getValueAt(selectedRow, 5);
                        navigationManager.showEditItems(clickedItem);
                    }
                }
            }
        });


        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBounds(20, 200, 900, 300);
        add(scrollPane);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Dialog", Font.BOLD, 18));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBackground(CommonCostants.TEXT_COLOR);
        searchButton.setBounds(500, 120, 100, 50);

        searchButton.addActionListener(_ -> {
            String searchedProduct = searchField.getText().trim();
            String selectedColor = (String) colorComboBox.getSelectedItem();
            String selectedSize = (String) sizeComboBox.getSelectedItem();

            if (searchedProduct.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Insert an id to research an item");
                return;
            }

            boolean areFiltersUsed = !selectedColor.equals(" Select Color ") || !selectedSize.equals(" Select Size ");

            ProductFilter filter = null;

            if(areFiltersUsed) {
                 filter = new ProductFilter.Builder()
                        .withId(searchedProduct)
                        .withColor(selectedColor)
                        .withSize(selectedSize)
                        .build();
            }

            Map<InventoryItem, List<PositionProduct>> foundItems = userController.searchItem(searchedProduct, filter);

            if (!foundItems.isEmpty()) {
                tableModel.setRowCount(0);

                for (Map.Entry<InventoryItem, List<PositionProduct>> entry : foundItems.entrySet()) {

                    InventoryItem item = entry.getKey();
                    List<PositionProduct> positions = entry.getValue();
                    Product p = entry.getKey().getProduct();

                    String details = "";
                    switch (p) {
                        case Clothes c -> details = "Size: " + c.getSize() + ", Color: " + c.getColor();
                        case Food f -> details = "Exp: " + f.getExpirationDate() + ", Type: " + f.getType();
                        case Furniture furn ->
                                details = "Color: " + furn.getColor() + " (" + furn.getWidth() + "x" + furn.getHeight() + ")";
                        default -> {
                        }
                    }

                    long activePositionsCount = 0;
                    if (positions != null) {
                        activePositionsCount = positions.stream().filter(pos -> pos.getQuantity() > 0).count();
                    }

                    String posString = (activePositionsCount == 0)
                            ? "Not Placed"
                            : activePositionsCount + " position(s)";
                    Object[] rowData = {
                            p.getId(),
                            p.getName(),
                            details,
                            posString,
                            item.getTotalQuantity(),
                            item

                    };
                    tableModel.addRow(rowData);
                }
            } else
                if (areFiltersUsed) {
                JOptionPane.showMessageDialog(null,
                        "Product out of stock for the selected filters.",
                        "Out of Stock",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Product not found. Check the id and try again.",
                        "Not Found",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        add(searchButton);
        JButton addItemButton = new JButton("Add new item");
        addItemButton.setFont(new Font("Dialog", Font.BOLD, 18));
        addItemButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addItemButton.setBackground(CommonCostants.TEXT_COLOR);
        addItemButton.setBounds(760, 545, 200, 50);
        addItemButton.addActionListener(_ -> {
            String[] itemTypes = {"Clothing", "Food", "Furniture"};
            int typeChoice = JOptionPane.showOptionDialog(this,
                    "Select the type of item to add:",
                    "Add New Item",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    itemTypes,
                    itemTypes[0]);

            if (typeChoice < 0) return;

            String selectedType = itemTypes[typeChoice];

            JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));

            JTextField nameField = new JTextField();
            JTextField brandField = new JTextField();
            JTextField priceField = new JTextField();
            JTextField thresholdField = new JTextField();

            inputPanel.add(new JLabel("Name:"));
            inputPanel.add(nameField);
            inputPanel.add(new JLabel("Brand:"));
            inputPanel.add(brandField);
            inputPanel.add(new JLabel("Price:"));
            inputPanel.add(priceField);
            inputPanel.add(new JLabel("Critical Threshold:"));
            inputPanel.add(thresholdField);

            String[] colors = {"Black", "White", "Red", "Blue", "Green", "Gray", "Denim Blue"};

            JComboBox<String> sizeCombo = null;
            JComboBox<String> colorCombo = null;
            JTextField expDateField = null;
            JComboBox<String> foodTypeCombo = null;
            JTextField heightField = null;
            JTextField widthField = null;
            JTextField lengthField = null;
            JComboBox<String> furnColorCombo = null;

            switch (selectedType) {
                case "Clothing" -> {
                    String[] sizes = {"XS", "S", "M", "L", "XL", "XXL"};
                    sizeCombo = new JComboBox<>(sizes);
                    colorCombo = new JComboBox<>(colors);

                    inputPanel.add(new JLabel("Size:"));
                    inputPanel.add(sizeCombo);
                    inputPanel.add(new JLabel("Color:"));
                    inputPanel.add(colorCombo);
                }
                case "Food" -> {
                    expDateField = new JTextField();
                    String[] foodTypes = {"sweet", "savory"};
                    foodTypeCombo = new JComboBox<>(foodTypes);

                    inputPanel.add(new JLabel("Expiration Date (e.g. 2026-12-31):"));
                    inputPanel.add(expDateField);
                    inputPanel.add(new JLabel("Type:"));
                    inputPanel.add(foodTypeCombo);
                }
                case "Furniture" -> {
                    heightField = new JTextField();
                    widthField = new JTextField();
                    lengthField = new JTextField();
                    furnColorCombo = new JComboBox<>(colors);

                    inputPanel.add(new JLabel("Height:"));
                    inputPanel.add(heightField);
                    inputPanel.add(new JLabel("Width:"));
                    inputPanel.add(widthField);
                    inputPanel.add(new JLabel("Length:"));
                    inputPanel.add(lengthField);
                    inputPanel.add(new JLabel("Color:"));
                    inputPanel.add(furnColorCombo);
                }
            }

            int result = JOptionPane.showConfirmDialog(this, inputPanel,
                    "Add " + selectedType + " Details",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String name = nameField.getText().trim();
                    String brand = brandField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    int criticalThreshold = Integer.parseInt(thresholdField.getText().trim());

                    String prefix = switch (selectedType) {
                        case "Clothing" -> "CL";
                        case "Food" -> "FD";
                        case "Furniture" -> "FR";
                        default -> "XX";
                    };

                    java.util.Random random = new java.util.Random();
                    String generatedId = prefix + String.format("%04d", random.nextInt(10000));

                    Product newProduct = null;

                    switch (selectedType) {
                        case "Clothing" -> {
                            String size = (String) sizeCombo.getSelectedItem();
                            String color = (String) colorCombo.getSelectedItem();
                            newProduct = new Clothes(generatedId, name, brand, price, size, color);
                        }
                        case "Food" -> {
                            String expDateStr = expDateField.getText().trim();
                            String fType = (String) foodTypeCombo.getSelectedItem();

                            java.util.Date expDate;
                            try {
                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                                sdf.setLenient(false);
                                expDate = sdf.parse(expDateStr);
                            } catch (java.text.ParseException e) {
                                JOptionPane.showMessageDialog(this, "Please use a valid date format (yyyy-MM-dd).", "Format Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            newProduct = new Food(generatedId, name, brand, price, expDate, fType);
                        }
                        case "Furniture" -> {
                            float height = Float.parseFloat(heightField.getText().trim());
                            float width = Float.parseFloat(widthField.getText().trim());
                            float length = Float.parseFloat(lengthField.getText().trim());
                            String furnColor = (String) furnColorCombo.getSelectedItem();
                            newProduct = new Furniture(generatedId, name, brand, price, height, width, length, furnColor);
                        }
                    }

                    InventoryItem newItem = new InventoryItem(newProduct, 0, criticalThreshold, 0);

                    userController.registerItem(newItem);

                    JOptionPane.showMessageDialog(this,
                            "Item successfully added!\nGenerated ID: " + generatedId,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter valid numeric values for prices and dimensions.", "Format Error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(addItemButton);


        List<InventoryItem> lowStockItems = userController.getLowStockItems();
        if(!lowStockItems.isEmpty()) {
            JLabel warningLabel = new JLabel("WARNING: " + lowStockItems.size() + " items are under the critical threshold");
            warningLabel.setBounds(20, 520, 520, 100);
            warningLabel.setForeground(CommonCostants.TEXT_COLOR);
            warningLabel.setFont(new Font("Dialog", Font.BOLD, 20));
            add(warningLabel);

            JButton manageStocksButton = new JButton("Manage stocks");
            manageStocksButton.setFont(new Font("Dialog", Font.BOLD, 18));
            manageStocksButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            manageStocksButton.setBackground(CommonCostants.TEXT_COLOR);
            manageStocksButton.setBounds(550, 545, 200, 50);
            manageStocksButton.addActionListener(_ -> {
                navigationManager.showManageStocks(lowStockItems);
            });
            add(manageStocksButton);
        }
    }
}

