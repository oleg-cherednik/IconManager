package com.ucware.icontools;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;

public class LocalPanel extends JFrame {
    JPanel A;
    JPanel B = new JPanel();

    public LocalPanel(String icoFile) throws IOException {
        setDefaultCloseOperation(3);
        A = (JPanel)getContentPane();
        B.setLayout(new FlowLayout(0));
        setTitle("DemoReader");
        Icon[] arricon = IconTools.readIcons(new File(icoFile));
        setTitle("DemoReader");
        for (int i = 0; i < arricon.length; ++i) {
            Icon icon = arricon[i];
            IconImage iconImage = (IconImage)icon;
            String string2 = "" + iconImage.getIconWidth() + "x" + iconImage.getIconHeight() + "@" + iconImage.C();
            B.add(new JButton(string2, icon));
        }
        A.add(B);
    }
}
