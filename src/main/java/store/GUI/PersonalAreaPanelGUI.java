package store.GUI;

import store.business_logic.controllers.NavigationManager;
import store.business_logic.controllers.UserController;
import store.domain_model.workers.Shift;
import store.domain_model.workers.Worker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PersonalAreaPanelGUI extends JPanel {
    private final Worker worker;
    private final UserController userController;
    private final NavigationManager navigationManager;

    public PersonalAreaPanelGUI(UserController userController, Worker worker, NavigationManager navigationManager) {
        this.userController = userController;
        this.worker = worker;
        this.navigationManager = navigationManager;
        setLayout(null);
        setBackground(CommonCostants.PRIMARY_COLOR);
        addGuiComponents();
    }

    private void addGuiComponents() {
        JLabel titleLabel = new JLabel("Personal Area");
        titleLabel.setBounds(350, 10, 520, 100);
        titleLabel.setForeground(CommonCostants.TEXT_COLOR);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        add(titleLabel);

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Dialog", Font.BOLD, 18));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setBackground(CommonCostants.TEXT_COLOR);
        backButton.setBounds(25, 25, 120, 40);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigationManager.showHomeScreen();
            }
        });
        add(backButton);

        JLabel idLabel = new JLabel("Id: " + worker.getId());
        idLabel.setBounds(20, 80,400, 25);
        idLabel.setForeground(CommonCostants.TEXT_COLOR);
        idLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(idLabel);

        JLabel nameLabel = new JLabel("Name: " + worker.getName());
        nameLabel.setBounds(20, 100,400, 25);
        nameLabel.setForeground(CommonCostants.TEXT_COLOR);
        nameLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(nameLabel);

        JLabel surnameLabel = new JLabel("Surname: " + worker.getSurname());
        surnameLabel.setBounds(20, 120,400, 25);
        surnameLabel.setForeground(CommonCostants.TEXT_COLOR);
        surnameLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(surnameLabel);

        JLabel emailLabel = new JLabel("Email: " + worker.getEmail());
        emailLabel.setBounds(20, 140,400, 25);
        emailLabel.setForeground(CommonCostants.TEXT_COLOR);
        emailLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(emailLabel);

        JLabel roleLabel = new JLabel("Role: " + worker.getRole());
        roleLabel.setBounds(20, 160,400, 25);
        roleLabel.setForeground(CommonCostants.TEXT_COLOR);
        roleLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(roleLabel);

        JLabel shiftLabel = new JLabel("Shifts: ");
        shiftLabel.setBounds(20, 180,400, 25);
        shiftLabel.setForeground(CommonCostants.TEXT_COLOR);
        shiftLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(shiftLabel);

        String[] columnNames = {"Date", "Type", "Start", "End"};
        List<Shift> schedule = worker.getSchedule();
        Object[][] data = new Object[schedule.size()][4];

        int row = 0;
        for (Shift shift: schedule) {
            data[row][0] = shift.getWorkingDay().toString();
            data[row][1] = shift.getShiftType().name();
            data[row][2] = shift.getShiftType().getStart().toString();
            data[row][3] = shift.getShiftType().getEnd().toString();
            row++;
        }

        JTable shiftsTable = new JTable(data, columnNames);
        shiftsTable.setFont(new Font("Dialog", Font.PLAIN, 14));
        shiftsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(shiftsTable);
        scrollPane.setBounds(20, 215, 450, 250);
        add(scrollPane);

    }
}
