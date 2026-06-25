
package utils;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class UIUtils {
 
 // Modern color palette
 public static final Color PRIMARY = new Color(99, 102, 241);
 public static final Color PRIMARY_DARK = new Color(79, 70, 229);
 public static final Color PRIMARY_LIGHT = new Color(165, 180, 252);
 public static final Color SECONDARY = new Color(236, 72, 153);
 public static final Color BACKGROUND_START = new Color(15, 23, 42);
 public static final Color BACKGROUND_END = new Color(30, 41, 59);
 public static final Color SURFACE = new Color(51, 65, 85);
 public static final Color SURFACE_LIGHT = new Color(71, 85, 105);
 public static final Color TEXT_PRIMARY = new Color(248, 250, 252);
 public static final Color TEXT_SECONDARY = new Color(148, 163, 184);
 public static final Color SUCCESS = new Color(34, 197, 94);
 public static final Color ERROR = new Color(239, 68, 68);
 public static final Color WARNING = new Color(234, 179, 8);
 
 // Chess colors
 public static final Color CHESS_LIGHT = new Color(240, 217, 181);
 public static final Color CHESS_DARK = new Color(181, 136, 99);
 public static final Color CHESS_HIGHLIGHT = new Color(186, 202, 68, 180);
 public static final Color CHESS_POSSIBLE_MOVE = new Color(99, 102, 241, 120);
 public static final Color CHESS_CHECK = new Color(239, 68, 68, 150);
 
 public static JTextField createModernTextField(String placeholder) {
     JTextField field = new JTextField() {
         @Override
         protected void paintComponent(Graphics g) {
             Graphics2D g2 = (Graphics2D) g.create();
             g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
             
             g2.setColor(SURFACE);
             g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
             
             super.paintComponent(g);
             
             if (getText().isEmpty() && !hasFocus()) {
                 g2.setColor(TEXT_SECONDARY);
                 g2.setFont(getFont());
                 FontMetrics fm = g2.getFontMetrics();
                 int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                 g2.drawString(placeholder, 15, y);
             }
             g2.dispose();
         }
     };
     
     field.setOpaque(false);
     field.setForeground(TEXT_PRIMARY);
     field.setCaretColor(PRIMARY);
     field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
     field.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
     field.setPreferredSize(new Dimension(300, 48));
     
     return field;
 }
 
 public static JPasswordField createModernPasswordField(String placeholder) {
     JPasswordField field = new JPasswordField() {
         @Override
         protected void paintComponent(Graphics g) {
             Graphics2D g2 = (Graphics2D) g.create();
             g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
             
             g2.setColor(SURFACE);
             g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
             
             super.paintComponent(g);
             
             if (getPassword().length == 0 && !hasFocus()) {
                 g2.setColor(TEXT_SECONDARY);
                 g2.setFont(getFont());
                 FontMetrics fm = g2.getFontMetrics();
                 int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                 g2.drawString(placeholder, 15, y);
             }
             g2.dispose();
         }
     };
     
     field.setOpaque(false);
     field.setForeground(TEXT_PRIMARY);
     field.setCaretColor(PRIMARY);
     field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
     field.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
     field.setPreferredSize(new Dimension(300, 48));
     
     return field;
 }
 
 public static JButton createModernButton(String text, boolean isPrimary) {
     JButton button = new JButton(text) {
         private boolean isHovered = false;
         private boolean isPressed = false;
         
         {
             addMouseListener(new java.awt.event.MouseAdapter() {
                 @Override
                 public void mouseEntered(java.awt.event.MouseEvent e) {
                     isHovered = true;
                     repaint();
                 }
                 
                 @Override
                 public void mouseExited(java.awt.event.MouseEvent e) {
                     isHovered = false;
                     repaint();
                 }
                 
                 @Override
                 public void mousePressed(java.awt.event.MouseEvent e) {
                     isPressed = true;
                     repaint();
                 }
                 
                 @Override
                 public void mouseReleased(java.awt.event.MouseEvent e) {
                     isPressed = false;
                     repaint();
                 }
             });
         }
         
         @Override
         protected void paintComponent(Graphics g) {
             Graphics2D g2 = (Graphics2D) g.create();
             g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
             
             Color bgColor;
             if (isPrimary) {
                 if (isPressed) {
                     bgColor = PRIMARY_DARK.darker();
                 } else if (isHovered) {
                     bgColor = PRIMARY_DARK;
                 } else {
                     bgColor = PRIMARY;
                 }
                 
                 // Gradient background
                 GradientPaint gradient = new GradientPaint(
                     0, 0, bgColor,
                     0, getHeight(), bgColor.darker()
                 );
                 g2.setPaint(gradient);
             } else {
                 if (isPressed) {
                     bgColor = SURFACE_LIGHT.darker();
                 } else if (isHovered) {
                     bgColor = SURFACE_LIGHT;
                 } else {
                     bgColor = SURFACE;
                 }
                 g2.setColor(bgColor);
             }
             
             g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
             
             // Subtle glow effect for primary buttons
             if (isPrimary && isHovered) {
                 g2.setColor(new Color(99, 102, 241, 50));
                 g2.setStroke(new BasicStroke(3));
                 g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 12, 12));
             }
             
             // Draw text
             g2.setColor(TEXT_PRIMARY);
             g2.setFont(getFont());
             FontMetrics fm = g2.getFontMetrics();
             int x = (getWidth() - fm.stringWidth(getText())) / 2;
             int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
             g2.drawString(getText(), x, y);
             
             g2.dispose();
         }
     };
     
     button.setOpaque(false);
     button.setContentAreaFilled(false);
     button.setBorderPainted(false);
     button.setFocusPainted(false);
     button.setFont(new Font("Segoe UI", Font.BOLD, 14));
     button.setForeground(TEXT_PRIMARY);
     button.setPreferredSize(new Dimension(300, 48));
     button.setCursor(new Cursor(Cursor.HAND_CURSOR));
     
     return button;
 }
 
 public static JCheckBox createModernCheckBox(String text) {
     JCheckBox checkBox = new JCheckBox(text) {
         @Override
         protected void paintComponent(Graphics g) {
             Graphics2D g2 = (Graphics2D) g.create();
             g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
             
             // Draw checkbox
             int boxSize = 20;
             int boxY = (getHeight() - boxSize) / 2;
             
             if (isSelected()) {
                 g2.setColor(PRIMARY);
                 g2.fill(new RoundRectangle2D.Float(0, boxY, boxSize, boxSize, 6, 6));
                 
                 // Draw checkmark
                 g2.setColor(Color.WHITE);
                 g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                 g2.drawLine(5, boxY + 10, 8, boxY + 14);
                 g2.drawLine(8, boxY + 14, 15, boxY + 6);
             } else {
                 g2.setColor(SURFACE_LIGHT);
                 g2.fill(new RoundRectangle2D.Float(0, boxY, boxSize, boxSize, 6, 6));
                 g2.setColor(TEXT_SECONDARY);
                 g2.setStroke(new BasicStroke(1.5f));
                 g2.draw(new RoundRectangle2D.Float(0, boxY, boxSize, boxSize, 6, 6));
             }
             
             // Draw text
             g2.setColor(TEXT_SECONDARY);
             g2.setFont(getFont());
             FontMetrics fm = g2.getFontMetrics();
             int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
             g2.drawString(getText(), boxSize + 10, textY);
             
             g2.dispose();
         }
     };
     
     checkBox.setOpaque(false);
     checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
     checkBox.setForeground(TEXT_SECONDARY);
     checkBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
     
     return checkBox;
 }
 
 public static BufferedImage createGlowEffect(BufferedImage image, Color glowColor, int radius) {
     int w = image.getWidth() + radius * 2;
     int h = image.getHeight() + radius * 2;
     BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
     Graphics2D g2 = result.createGraphics();
     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
     
     // Draw glow
     for (int i = radius; i > 0; i--) {
         float alpha = (float) (radius - i) / radius * 0.3f;
         g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), (int) (alpha * 255)));
         g2.fillOval(radius - i, radius - i, image.getWidth() + i * 2, image.getHeight() + i * 2);
     }
     
     g2.drawImage(image, radius, radius, null);
     g2.dispose();
     return result;
 }
}