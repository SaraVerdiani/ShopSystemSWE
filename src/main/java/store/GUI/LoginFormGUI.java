package store.GUI;

import store.business_logic.controllers.AuthenticationController;
import store.business_logic.controllers.NavigationManager;

import javax.swing.*;
import java.awt.*;

public class LoginFormGUI extends JPanel{
    public AuthenticationController authenticationController;
    public final NavigationManager navigationManager;

    public LoginFormGUI(AuthenticationController authenticationController, NavigationManager navigationManager) {
        setLayout(null);
        setBackground(CommonCostants.PRIMARY_COLOR);
        this.authenticationController = authenticationController;
        this.navigationManager = navigationManager;
        addGuiComponents();
    }

    private void addGuiComponents() {
        JLabel loginLabel = new JLabel("Login");
        loginLabel.setBounds(0,25,520,100);
        loginLabel.setForeground(CommonCostants.TEXT_COLOR);
        loginLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(loginLabel);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(30,150,400,25);
        usernameLabel.setForeground(CommonCostants.TEXT_COLOR);
        usernameLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        JTextField usernameField = new JTextField();
        usernameField.setBounds(30,185,450,55);
        usernameField.setBackground(CommonCostants.SECONDARY_COLOR);
        usernameField.setForeground(CommonCostants.TEXT_COLOR);
        usernameField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(usernameLabel);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30,335,400,25);
        passwordLabel.setForeground(CommonCostants.TEXT_COLOR);
        passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        JTextField passwordField = new JPasswordField();
        passwordField.setBounds(30,365,450,55);
        passwordField.setBackground(CommonCostants.SECONDARY_COLOR);
        passwordField.setForeground(CommonCostants.TEXT_COLOR);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(passwordLabel);
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Dialog", Font.BOLD, 18));
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setBackground(CommonCostants.TEXT_COLOR);
        loginButton.setBounds(125,450,250,50);
        loginButton.addActionListener(_ -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if(authenticationController.login(username, password) != null) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                navigationManager.showHomeScreen();
            }
            else {
                JOptionPane.showMessageDialog(this, "Login Failed, check username and password");
            }
        });
        add(loginButton);
    }
}
