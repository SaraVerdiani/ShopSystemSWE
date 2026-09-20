package store.business_logic.controllers;

import store.domain_model.workers.Worker;

public class UserSessionController {
    private static UserSessionController instance;
    private Worker loggedWorker;

    private UserSessionController() {}

    public static UserSessionController getInstance() {
        if (instance == null) {
            instance = new UserSessionController();
        }
        return instance;
    }

    public void setLoggedWorker(Worker worker) { this.loggedWorker = worker; }
    public Worker getLoggedWorker() { return this.loggedWorker; }
}
