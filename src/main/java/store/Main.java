import store.ORM.DAO.*;
import store.business_logic.controllers.AuthenticationController;
import store.business_logic.controllers.UserController;
import store.business_logic.services.AuthenticationService;
import store.business_logic.services.InventoryService;
import store.business_logic.services.LogisticsService;
import store.business_logic.services.WorkerService;
import store.business_logic.controllers.NavigationManager;

import javax.swing.*;

void main() {
    InventoryDAO invDAO = new InventoryDAO();
    PositionProductDAO positionProductDAO = new PositionProductDAO();
    ClothingDAO clothingDAO = new ClothingDAO();
    FoodDAO foodDAO = new FoodDAO();
    FurnitureDAO furnitureDAO = new FurnitureDAO();
    PositionDAO positionDAO = new PositionDAO();
    ProductDAO productDAO = new ProductDAO();
    WorkerDAO workerDAO = new WorkerDAO();


    AuthenticationService authenticationService = new AuthenticationService(workerDAO);
    InventoryService inventoryService = new InventoryService(invDAO, positionProductDAO);
    LogisticsService logisticsService = new LogisticsService(positionDAO, invDAO, positionProductDAO);
    WorkerService workerService = new WorkerService(
            invDAO,
            productDAO,
            positionDAO,
            clothingDAO,
            furnitureDAO,
            foodDAO,
            positionProductDAO
    );


    UserController userController = new UserController(inventoryService, logisticsService, workerService);
    AuthenticationController authenticationController = new AuthenticationController(authenticationService);
    NavigationManager navManager = new NavigationManager(userController, authenticationController);


    SwingUtilities.invokeLater(navManager::showLoginScreen);
}

