package store.ORM.DAO;

import store.ORM.connection.ConnectionManager;
import store.domain_model.workers.Shift;
import store.domain_model.workers.Worker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ShiftDAO {

    Connection connection;
    private WorkerDAO workerDAO;


    public ShiftDAO() {
        this.connection = ConnectionManager.getInstance().getConnection();
        this.workerDAO = new WorkerDAO();
    }

    public void addShiftToWorker(String workerId, Shift shift) {
        String sql = """
                INSERT INTO public."Shift" (worker_id, day, type) VALUES
                (?, ?, ?)
                """;

        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            Worker worker = workerDAO.getWorkerById(workerId);
            pst.setString(1, worker.getId());
            pst.setObject(2, shift.getWorkingDay());
            pst.setString(3, shift.getShiftType().name());
            worker.addShift(shift);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.printf(e.getMessage());
        }
    }

    public void removeShiftByWorkerId(String id) {
        String sql = """
                """;
    }



}
