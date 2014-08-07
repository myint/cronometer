package ca.spaz.gui;

import java.awt.Component;
import java.awt.Font;
import java.io.InputStream;

import javax.swing.JButton;


public class IconFont {

    private static Font iconFont;

    public static JButton createIconFontButton(String code,
            Component component) {
        JButton button = new JButton(code);
        button.setFont(getIconFont(component));
        return button;
    }

    private static Font getIconFont(Component component) {
        if (iconFont == null)
        {
            try {
                InputStream in = component.getClass().getResourceAsStream(
                                     "/fontawesome.tff");

                Font base = Font.createFont(Font.TRUETYPE_FONT, in);
                iconFont = base.deriveFont(Font.PLAIN, 16);
            } catch (java.lang.Exception exception) {
                ErrorReporter.showError(exception, component);
                iconFont = new Font("Application", Font.PLAIN, 16);
            }
        }

        return iconFont;
    }
}
