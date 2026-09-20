package store.business_logic.controllers;
import store.business_logic.services.AuthenticationService;
import store.domain_model.workers.Worker;

public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public Worker login(String email, String password) {

        Worker loggedWorker = authenticationService.login(email, password);

        if(loggedWorker != null) {
            UserSessionController.getInstance().setLoggedWorker(loggedWorker);
            return loggedWorker;
        }
        return null;
    }
}
