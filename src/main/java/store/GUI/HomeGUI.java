package store.GUI;

import store.business_logic.controllers.NavigationManager;
import store.business_logic.controllers.UserController;

import javax.swing.*;
import java.awt.*;

public class HomeGUI extends JPanel {

    private final UserController userController;
    private final NavigationManager navigationManager;

    public HomeGUI(UserController userController, NavigationManager navigationManager) {
        setLayout(null);
        setBackground(CommonCostants.PRIMARY_COLOR);
        this.userController = userController;
        this.navigationManager = navigationManager;
        addGuiComponents();
    }

    private void addGuiComponents() {
        JLabel homeLabel = new JLabel("Home");
        homeLabel.setBounds(0, 25, 520, 100);
        homeLabel.setForeground(CommonCostants.TEXT_COLOR);
        homeLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        homeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(homeLabel);

        JButton workerInfoButton = new JButton("Personal Area");
        workerInfoButton.setFont(new Font("Dialog", Font.BOLD, 18));
        workerInfoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        workerInfoButton.setBackground(CommonCostants.TEXT_COLOR);
        workerInfoButton.setBounds(25, 170, 450, 50);
        workerInfoButton.setHorizontalAlignment(SwingConstants.CENTER);
        workerInfoButton.addActionListener(_ -> {
            navigationManager.showPersonalArea();
        });
        add(workerInfoButton);


        JButton productsButton = new JButton("Inventory");
        productsButton.setFont(new Font("Dialog", Font.BOLD, 18));
        productsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        productsButton.setBackground(CommonCostants.TEXT_COLOR);
        productsButton.setBounds(25, 250, 450, 50);
        productsButton.setHorizontalAlignment(SwingConstants.CENTER);
        productsButton.addActionListener(_ -> {
            navigationManager.showInventory();
        });
        add(productsButton);


        JButton manageAllocationsButton = new JButton("Manage unallocated items");
        manageAllocationsButton.setFont(new Font("Dialog", Font.BOLD, 18));
        manageAllocationsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        manageAllocationsButton.setBackground(CommonCostants.TEXT_COLOR);
        manageAllocationsButton.setBounds(25, 350, 450, 50);
        manageAllocationsButton.setHorizontalAlignment(SwingConstants.CENTER);
        manageAllocationsButton.addActionListener(_ -> {
            navigationManager.showManageUnallocatedItems();
        }
        );
        add(manageAllocationsButton);

    }


}

