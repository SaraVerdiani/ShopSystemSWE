package store.business_logic.services;

import store.ORM.DAO.WorkerDAO;
import store.domain_model.workers.Worker;

public class AuthenticationService {
    private WorkerDAO workerDAO;

    public AuthenticationService(WorkerDAO workerDAO) {
        this.workerDAO = workerDAO;
    }

    public Worker login(String email, String password) {
        return workerDAO.login(email, password);
    }
}
