package net.payload.gui.colors;

import java.util.ArrayList;
import java.util.List;

public class GradientColor {
    private static final int GRADIENT_STEPS = 10;

    public static void RGB() {
        Color start = Colors.Red;
        Color end = Colors.Blue;

        List<Color> gradient = generateGradient(start, end, GRADIENT_STEPS);

        // Example output
        for (Color c : gradient) {
            System.out.println(toHex(c));
        }
    }

    public static void HEX() {
        Color start = new Color(255, 0, 0); // from #FF0000
        Color end = new Color(0, 0, 255);   // from #0000FF

        List<Color> gradient = generateGradient(start, end, GRADIENT_STEPS);

        List<String> hexGradient = new ArrayList<>();
        for (Color c : gradient) {
            hexGradient.add(toHex(c));
        }

        for (String hex : hexGradient) {
            System.out.println(hex);
        }
    }

    private static List<Color> generateGradient(Color start, Color end, int steps) {
        List<Color> gradient = new ArrayList<>();
        for (int i = 0; i <= steps; i++) {
            double ratio = (double) i / steps;
            int r = (int) (start.r + (end.r - start.r) * ratio);
            int g = (int) (start.g + (end.g - start.g) * ratio);
            int b = (int) (start.b + (end.b - start.b) * ratio);
            gradient.add(new Color(r, g, b));
        }
        return gradient;
    }

    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X", color.r, color.g, color.b);
    }
}
