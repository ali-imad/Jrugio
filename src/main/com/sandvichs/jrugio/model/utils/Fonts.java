package com.sandvichs.jrugio.model.utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Fonts {
    public static Font tileFont;
//    public static Font textFont;

    static {
        try {
            tileFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/fonts/FSEX302.ttf"));
//            textFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/fonts/CozetteVector.ttf"));
        } catch (FontFormatException | IOException e) {
            tileFont = Font.getFont(Font.MONOSPACED);
//            textFont = Font.getFont(Font.MONOSPACED);
        }
    }
}
