package store.business_logic.controllers;

import store.GUI.*;
import store.domain_model.inventory.InventoryItem;
import store.domain_model.workers.Worker;

import javax.swing.*;
import java.util.List;

public class NavigationManager {
    private final JFrame mainWindow;
    private final UserController userController;
    private final AuthenticationController authenticationController;

    public NavigationManager(UserController userController, AuthenticationController authenticationController) {
        this.userController = userController;
        this.authenticationController = authenticationController;
        this.mainWindow = new JFrame();
        this.mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainWindow.setLayout(null);
        this.mainWindow.setResizable(false);
        this.mainWindow.setLocationRelativeTo(null);
        this.mainWindow.getContentPane().setBackground(CommonCostants.PRIMARY_COLOR);
    }

    private void navigateTo(JPanel newPanel) {
        mainWindow.setContentPane(newPanel);
        mainWindow.revalidate();
        mainWindow.repaint();

        if (!mainWindow.isVisible()) {
            mainWindow.setVisible(true);
        }
    }

    public void showLoginScreen() {
        LoginFormGUI loginGUI = new LoginFormGUI(authenticationController, this);
        mainWindow.setSize(520, 680);
        navigateTo(loginGUI);
    }

    public void showHomeScreen() {
        HomeGUI homeGUI = new HomeGUI(userController, this);
        mainWindow.setSize(520,680);
        navigateTo(homeGUI);
    }

    public void showPersonalArea() {
        Worker loggedWorker = UserSessionController.getInstance().getLoggedWorker();
        mainWindow.setSize(1000, 680);
        mainWindow.setLocationRelativeTo(null);
        PersonalAreaPanelGUI areaPanel = new PersonalAreaPanelGUI(userController, loggedWorker, this);
        navigateTo(areaPanel);
    }

    public void showInventory() {
        mainWindow.setSize(1000, 680);
        mainWindow.setLocationRelativeTo(null);
        InventoryPanelGUI inventoryPanel = new InventoryPanelGUI(userController, this);
        navigateTo(inventoryPanel);
    }

    public void showEditItems(InventoryItem item) {
        mainWindow.setSize(1000, 680);
        mainWindow.setLocationRelativeTo(null);
        EditItemPanelGUI editItemPanelGUI = new EditItemPanelGUI(item, userController, this);
        navigateTo(editItemPanelGUI);
    }

    public void showManageStocks(List<InventoryItem> lowStockItems) {
        mainWindow.setSize(1000, 680);
        mainWindow.setLocationRelativeTo(null);
        ManageStocksPanelGUI manageStocksPanelGUI = new ManageStocksPanelGUI(lowStockItems, userController, this);
        navigateTo(manageStocksPanelGUI);
    }

    public void showManageUnallocatedItems() {
        mainWindow.setSize(1000, 680);
        mainWindow.setLocationRelativeTo(null);
        ManageUnallocatedItemsPanelGUI manageUnallocatedItemsPanelGUI = new ManageUnallocatedItemsPanelGUI(userController, this);
        navigateTo(manageUnallocatedItemsPanelGUI);
    }
}
