package store.domain_model.workers;

import java.util.*;

public class Worker {
    private String id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String role;
    private List<Shift> schedule;


    public Worker(String id, String name, String surname, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.schedule = new ArrayList<>();
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public List<Shift> getSchedule() {
        return schedule;
    }

    public void addShift(Shift shift) {
        this.schedule.add(shift);
        System.out.print("Turno aggiunto: ");
    }

}
