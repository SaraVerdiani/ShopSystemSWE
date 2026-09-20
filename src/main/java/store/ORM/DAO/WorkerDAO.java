package store.ORM.DAO;

import store.ORM.connection.ConnectionManager;
import store.domain_model.workers.Shift;
import store.domain_model.workers.Worker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class WorkerDAO {

    Connection connection;

    public WorkerDAO() {
        this.connection = ConnectionManager.getInstance().getConnection();
    }

    public  Worker getWorkerById(String id) {
        String sql = "SELECT * FROM \"Workers\" WHERE id = ?";
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                return new Worker(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.printf("ERROR WHILE TRYING TO GET WORKER: " + e.getMessage());
        }
        return null;
    }

    public Worker login(String email, String password) {
        String sql = "SELECT * FROM \"Workers\" WHERE email = ? AND password = ?";
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, email);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                Worker worker = new Worker(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                Map<LocalDate, Shift> workerShifts = this.getShiftsById(worker.getId());

                for (Shift shift : workerShifts.values()) {
                    worker.addShift(shift);
                }
                return worker;
            }
        } catch(SQLException e) {
            System.err.printf("LOGIN ERROR: " + e.getMessage());
        }
        return null;
    }

    public Map<LocalDate, Shift> getShiftsById(String id) {
        String sql = """
                SELECT day, type
                FROM public."Shift"
                WHERE worker_id = ?
                """;
        Map<LocalDate, Shift> dailyShifts = new TreeMap<>();

        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                LocalDate day = rs.getObject("day", LocalDate.class);
                String typeString = rs.getString("type");
                Shift.ShiftType type = Shift.ShiftType.valueOf(typeString);
                Shift shift = new Shift(day, type);
                dailyShifts.put(day, shift);
            }


        } catch(SQLException e) {
            System.err.printf(e.getMessage());
        }
        return dailyShifts;

    }

}
