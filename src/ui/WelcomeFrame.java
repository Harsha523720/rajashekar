// ui/WelcomeFrame.java
package ui;

import utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class WelcomeFrame extends JFrame {
    private String username;
    private Timer animationTimer;
    private float animationProgress = 0f;
    private java.util.List<Particle> particles = new java.util.ArrayList<>();
    
    public WelcomeFrame(String username) {
        this.username = username;
        initializeFrame();
        createParticles();
        layoutComponents();
        startAnimation();
    }
    
    private void initializeFrame() {
        setTitle("Welcome");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 800, 600, 30, 30));
    }
    
    private void createParticles() {
        for (int i = 0; i < 50; i++) {
            particles.add(new Particle(
                Math.random() * 800,
                Math.random() * 600,
                (Math.random() - 0.5) * 2,
                (Math.random() - 0.5) * 2,
                Math.random() * 4 + 2,
                new Color(
                    (int)(Math.random() * 100 + 99),
                    (int)(Math.random() * 100 + 102),
                    (int)(Math.random() * 100 + 141),
                    (int)(Math.random() * 100 + 50)
                )
            ));
        }
    }
    
    private void layoutComponents() {
        GradientPanel mainPanel = new GradientPanel(UIUtils.BACKGROUND_START, UIUtils.BACKGROUND_END) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw particles
                for (Particle p : particles) {
                    g2.setColor(p.color);
                    g2.fillOval((int)p.x, (int)p.y, (int)p.size, (int)p.size);
                }
                
                // Decorative elements
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                g2.setColor(UIUtils.PRIMARY);
                g2.fillOval(-150, -150, 400, 400);
                g2.setColor(UIUtils.SECONDARY);
                g2.fillOval(getWidth() - 200, getHeight() - 250, 350, 350);
                
                g2.dispose();
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        
        // Close button
        JLabel closeButton = createCloseButton();
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Welcome animation panel
        JPanel welcomePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                
                String welcomeText = "Welcome, " + username + "!";
                
                // Animated gradient text
                Font font = new Font("Segoe UI", Font.BOLD, 48);
                g2.setFont(font);
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(welcomeText);
                int x = (getWidth() - textWidth) / 2;
                int y = getHeight() / 2 + fm.getAscent() / 4;
                
                // Create animated gradient
                float gradientOffset = animationProgress * 2;
                GradientPaint gradient = new GradientPaint(
                    x + (textWidth * gradientOffset), 0, UIUtils.PRIMARY,
                    x + textWidth + (textWidth * gradientOffset), 0, UIUtils.SECONDARY
                );
                g2.setPaint(gradient);
                g2.drawString(welcomeText, x, y);
                
                // Glow effect
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2.setColor(UIUtils.PRIMARY);
                for (int i = 1; i <= 3; i++) {
                    g2.drawString(welcomeText, x, y + i);
                    g2.drawString(welcomeText, x, y - i);
                }
                
                g2.dispose();
            }
        };
        welcomePanel.setOpaque(false);
        welcomePanel.setPreferredSize(new Dimension(700, 100));
        welcomePanel.setMaximumSize(new Dimension(700, 100));
        welcomePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Your gaming experience awaits");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(UIUtils.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Stats cards
        JPanel statsPanel = createStatsPanel();
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Play Chess button
        JButton playChessButton = createPlayChessButton();
        playChessButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Logout button
        JButton logoutButton = UIUtils.createModernButton("Sign Out", false);
        logoutButton.setMaximumSize(new Dimension(200, 45));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        
        // Layout
        contentPanel.add(Box.createVerticalStrut(40));
        contentPanel.add(welcomePanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(50));
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(50));
        contentPanel.add(playChessButton);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(logoutButton);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(15, 0, 0, 20);
        gbc.weightx = 1;
        mainPanel.add(closeButton, gbc);
        
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 1;
        mainPanel.add(contentPanel, gbc);
        
        setContentPane(mainPanel);
        
        // Drag functionality
        enableDrag(mainPanel);
    }
    
    private JLabel createCloseButton() {
        JLabel closeButton = new JLabel("✕");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        closeButton.setForeground(UIUtils.TEXT_SECONDARY);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(UIUtils.ERROR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(UIUtils.TEXT_SECONDARY);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });
        return closeButton;
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        statsPanel.setOpaque(false);
        
        statsPanel.add(createStatCard("🏆", "0", "Games Won"));
        statsPanel.add(createStatCard("♟️", "0", "Games Played"));
        statsPanel.add(createStatCard("⭐", "1200", "Rating"));
        
        return statsPanel;
    }
    
    private JPanel createStatCard(String icon, String value, String label) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Glass effect background
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                
                // Border
                g2.setColor(new Color(255, 255, 255, 30));
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(UIUtils.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelLabel.setForeground(UIUtils.TEXT_SECONDARY);
        labelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(labelLabel);
        
        return card;
    }
    
    private JButton createPlayChessButton() {
        JButton button = new JButton("Play Chess") {
            private boolean isHovered = false;
            private float pulseProgress = 0f;
            private Timer pulseTimer;
            
            {
                pulseTimer = new Timer(30, e -> {
                    pulseProgress += 0.05f;
                    if (pulseProgress > Math.PI * 2) {
                        pulseProgress = 0f;
                    }
                    repaint();
                });
                pulseTimer.start();
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Pulsing glow
                float glowIntensity = (float)(Math.sin(pulseProgress) + 1) / 2 * 0.3f + 0.1f;
                for (int i = 15; i > 0; i--) {
                    g2.setColor(new Color(99, 102, 241, (int)(glowIntensity * 255 * (15 - i) / 15)));
                    g2.fill(new RoundRectangle2D.Float(-i, -i, getWidth() + i * 2, getHeight() + i * 2, 20 + i, 20 + i));
                }
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, isHovered ? UIUtils.PRIMARY_DARK : UIUtils.PRIMARY,
                    getWidth(), getHeight(), isHovered ? UIUtils.SECONDARY : UIUtils.PRIMARY_DARK
                );
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                
                // Chess icon and text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                String text = "♔  Play Chess";
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, x, y);
                
                g2.dispose();
            }
        };
        
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(250, 55));
        button.setMaximumSize(new Dimension(250, 55));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new ChessFrame(username).setVisible(true);
            }
        });
        
        return button;
    }
    
    private void enableDrag(JPanel panel) {
        final Point[] dragPoint = {null};
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragPoint[0] = e.getPoint();
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
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
    
    private void startAnimation() {
        animationTimer = new Timer(16, e -> {
            animationProgress += 0.005f;
            if (animationProgress > 1f) {
                animationProgress = 0f;
            }
            
            // Update particles
            for (Particle p : particles) {
                p.x += p.vx;
                p.y += p.vy;
                
                if (p.x < 0 || p.x > 800) p.vx *= -1;
                if (p.y < 0 || p.y > 600) p.vy *= -1;
            }
            
            repaint();
        });
        animationTimer.start();
    }
    
    // Inner class for particles
    private static class Particle {
        double x, y, vx, vy, size;
        Color color;
        
        Particle(double x, double y, double vx, double vy, double size, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.size = size;
            this.color = color;
        }
    }
}
