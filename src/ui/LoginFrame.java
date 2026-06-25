// ui/LoginFrame.java
package ui;

import utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberMeCheckBox;
    private JButton loginButton;
    private JLabel forgotPasswordLabel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    
    // Animation
    private Timer fadeInTimer;
    private float opacity = 0f;
    
    public LoginFrame() {
        initializeFrame();
        initializeComponents();
        layoutComponents();
        addEventListeners();
        startFadeInAnimation();
    }
    
    private void initializeFrame() {
        setTitle("Login");
        setSize(450, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        
        // Make window rounded
        setShape(new RoundRectangle2D.Double(0, 0, 450, 650, 30, 30));
    }
    
    private void initializeComponents() {
        // Title
        titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Subtitle
        subtitleLabel = new JLabel("Sign in to continue your journey");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(UIUtils.TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Input fields
        usernameField = UIUtils.createModernTextField("Username");
        passwordField = UIUtils.createModernPasswordField("Password");
        
        // Checkbox
        rememberMeCheckBox = UIUtils.createModernCheckBox("Remember me");
        
        // Forgot password link
        forgotPasswordLabel = new JLabel("Forgot Password?");
        forgotPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        forgotPasswordLabel.setForeground(UIUtils.PRIMARY_LIGHT);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Login button
        loginButton = UIUtils.createModernButton("Sign In", true);
    }
    
    private void layoutComponents() {
        GradientPanel mainPanel = new GradientPanel(UIUtils.BACKGROUND_START, UIUtils.BACKGROUND_END) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw decorative circles
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                g2.setColor(UIUtils.PRIMARY);
                g2.fillOval(-100, -100, 300, 300);
                g2.fillOval(getWidth() - 150, getHeight() - 200, 250, 250);
                
                g2.setColor(UIUtils.SECONDARY);
                g2.fillOval(getWidth() - 100, -50, 200, 200);
                
                g2.dispose();
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        
        // Close button
        JLabel closeButton = new JLabel("✕") {
            private boolean isHovered = false;
            
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        setForeground(UIUtils.ERROR);
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        setForeground(UIUtils.TEXT_SECONDARY);
                    }
                    
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.exit(0);
                    }
                });
            }
        };
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        closeButton.setForeground(UIUtils.TEXT_SECONDARY);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Create content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Add avatar/logo
        JPanel avatarPanel = createAvatarPanel();
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Center align labels
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Center align fields
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setMaximumSize(new Dimension(300, 48));
        
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(300, 48));
        
        // Options row
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setOpaque(false);
        optionsPanel.setMaximumSize(new Dimension(300, 30));
        optionsPanel.add(rememberMeCheckBox, BorderLayout.WEST);
        optionsPanel.add(forgotPasswordLabel, BorderLayout.EAST);
        
        // Center align button
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(300, 48));
        
        // Add components with spacing
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(avatarPanel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(40));
        contentPanel.add(usernameField);
        contentPanel.add(Box.createVerticalStrut(16));
        contentPanel.add(passwordField);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(optionsPanel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(loginButton);
        
        // Add close button
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(15, 0, 0, 20);
        gbc.weightx = 1;
        mainPanel.add(closeButton, gbc);
        
        // Add content panel
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 1;
        mainPanel.add(contentPanel, gbc);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createAvatarPanel() {
        return new JPanel() {
            {
                setOpaque(false);
                setPreferredSize(new Dimension(100, 100));
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 80;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Outer glow
                for (int i = 20; i > 0; i--) {
                    float alpha = (20 - i) / 20f * 0.15f;
                    g2.setColor(new Color(99, 102, 241, (int)(alpha * 255)));
                    g2.fillOval(x - i, y - i, size + i * 2, size + i * 2);
                }
                
                // Gradient circle
                GradientPaint gradient = new GradientPaint(
                    x, y, UIUtils.PRIMARY,
                    x + size, y + size, UIUtils.SECONDARY
                );
                g2.setPaint(gradient);
                g2.fillOval(x, y, size, size);
                
                // User icon
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                // Head
                int headSize = 24;
                g2.fillOval(x + (size - headSize) / 2, y + 18, headSize, headSize);
                
                // Body
                g2.fillArc(x + 15, y + 45, size - 30, 40, 0, 180);
                
                g2.dispose();
            }
        };
    }
    
    private void addEventListeners() {
        // Login button action
        loginButton.addActionListener(e -> performLogin());
        
        // Enter key to login
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        
        // Forgot password
        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showForgotPasswordDialog();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPasswordLabel.setText("<html><u>Forgot Password?</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                forgotPasswordLabel.setText("Forgot Password?");
            }
        });
        
        // Drag window
        final Point[] dragPoint = {null};
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragPoint[0] = e.getPoint();
            }
        });
        contentPane.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragPoint[0] != null) {
                    Point location = getLocation();
                    setLocation(location.x + e.getX() - dragPoint[0].x,
                               location.y + e.getY() - dragPoint[0].y);
                }
            }
        });
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty()) {
            showError("Please enter your username");
            return;
        }
        
        if (password.isEmpty()) {
            showError("Please enter your password");
            return;
        }
        
        // Simulate login animation
        loginButton.setEnabled(false);
        loginButton.setText("Signing in...");
        
        Timer loginTimer = new Timer(1500, e -> {
            dispose();
            WelcomeFrame welcomeFrame = new WelcomeFrame(username);
            welcomeFrame.setVisible(true);
        });
        loginTimer.setRepeats(false);
        loginTimer.start();
    }
    
    private void showError(String message) {
        // Create custom error toast
        JDialog toast = new JDialog(this, false);
        toast.setUndecorated(true);
        toast.setSize(300, 50);
        toast.setLocationRelativeTo(this);
        
        JPanel toastPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIUtils.ERROR);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        toastPanel.setLayout(new BorderLayout());
        
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        toastPanel.add(messageLabel, BorderLayout.CENTER);
        
        toast.setContentPane(toastPanel);
        toast.setVisible(true);
        
        // Auto-dismiss
        Timer dismissTimer = new Timer(2000, e -> toast.dispose());
        dismissTimer.setRepeats(false);
        dismissTimer.start();
    }
    
    private void showForgotPasswordDialog() {
        JDialog dialog = new JDialog(this, "Reset Password", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 400, 280, 20, 20));
        
        GradientPanel panel = new GradientPanel(UIUtils.BACKGROUND_START, UIUtils.BACKGROUND_END);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        JLabel titleLabel = new JLabel("Reset Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel("<html><center>Enter your email address and we'll send you a link to reset your password.</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(UIUtils.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextField emailField = UIUtils.createModernTextField("Email address");
        emailField.setMaximumSize(new Dimension(320, 48));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton sendButton = UIUtils.createModernButton("Send Reset Link", true);
        sendButton.setMaximumSize(new Dimension(320, 48));
        sendButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sendButton.addActionListener(e -> {
            // Show success message
            JOptionPane.showMessageDialog(dialog,
                "If an account exists with this email, a reset link will be sent.",
                "Email Sent",
                JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        
        JButton cancelButton = UIUtils.createModernButton("Cancel", false);
        cancelButton.setMaximumSize(new Dimension(320, 48));
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(25));
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(15));
        panel.add(sendButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(cancelButton);
        
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
    
    private void startFadeInAnimation() {
        setOpacity(0f);
        fadeInTimer = new Timer(16, e -> {
            opacity += 0.05f;
            if (opacity >= 1f) {
                opacity = 1f;
                fadeInTimer.stop();
            }
            setOpacity(opacity);
        });
        fadeInTimer.start();
    }
}
