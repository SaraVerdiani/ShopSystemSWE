package store.business_logic.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.ORM.DAO.WorkerDAO;
import store.domain_model.workers.Worker;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private WorkerDAO workerDAOMock;

    @InjectMocks
    private AuthenticationService authService;

    @Test
    void testLoginWrongCredentials() {
        when(workerDAOMock.login("wrong@store.com", "wrongPassword")).thenReturn(null);
        Worker loggedWorker = authService.login("wrong@store.com", "wrongPassword");
        assertNull(loggedWorker);
        verify(workerDAOMock, times(1)).login("wrong@store.com", "wrongPassword");
    }

    @Test
    void testLoginSuccess() {
        Worker expectedWorker = new Worker(
                "W001",
                "Mario",
                "Rossi",
                "mario.rossi@store.com",
                "correctPassword123",
                "Manager"
        );

        when(workerDAOMock.login("mario.rossi@store.com", "correctPassword123")).thenReturn(expectedWorker);
        Worker loggedWorker = authService.login("mario.rossi@store.com", "correctPassword123");
        assertNotNull(loggedWorker);
        assertEquals("W001", loggedWorker.getId());
        assertEquals("mario.rossi@store.com", loggedWorker.getEmail());
        assertEquals("Manager", loggedWorker.getRole());
        verify(workerDAOMock, times(1)).login("mario.rossi@store.com", "correctPassword123");
    }

}