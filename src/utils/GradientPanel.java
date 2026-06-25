// utils/GradientPanel.java
package utils;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {
    private Color startColor;
    private Color endColor;
    private boolean horizontal;
    
    public GradientPanel(Color startColor, Color endColor) {
        this(startColor, endColor, false);
    }
    
    public GradientPanel(Color startColor, Color endColor, boolean horizontal) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.horizontal = horizontal;
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        GradientPaint gradient;
        if (horizontal) {
            gradient = new GradientPaint(0, 0, startColor, getWidth(), 0, endColor);
        } else {
            gradient = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
        }
        
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Add subtle noise/texture effect
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.03f));
        for (int i = 0; i < getWidth(); i += 4) {
            for (int j = 0; j < getHeight(); j += 4) {
                if (Math.random() > 0.5) {
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(Color.BLACK);
                }
                g2.fillRect(i, j, 2, 2);
            }
        }
        
        g2.dispose();
        super.paintComponent(g);
    }
}