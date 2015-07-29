package cop.swing.demo;

import com.ucware.icontools.LocalPanel;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

public class DemoReader {
    boolean packFrame = false;

    public DemoReader(String icoFile) throws IOException {
        LocalPanel localPanel = new LocalPanel(icoFile);
        if (this.packFrame) {
            localPanel.pack();
        } else {
            localPanel.validate();
        }
        localPanel.setSize(new Dimension(300, 200));
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dimension2 = localPanel.getSize();
        if (dimension2.height > dimension.height) {
            dimension2.height = dimension.height;
        }
        if (dimension2.width > dimension.width) {
            dimension2.width = dimension.width;
        }
        localPanel.setLocation((dimension.width - dimension2.width) / 2, (dimension.height - dimension2.height) / 2);
        localPanel.setVisible(true);
    }

    public static void main(final String... args) {
        if (args.length > 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        new DemoReader(args[0]);
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            System.err.println("usage: java -jar icontools.jar <ico-file>");
        }
    }

//    static class DemoReader implements Runnable {
//        private final /* synthetic */ String[] val$args;
//
//        DemoReader(String[] arrstring) {
//            this.val$args = arrstring;
//        }
//
//        public void run() {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            } catch(Exception var1_1) {
//                var1_1.printStackTrace();
//            }
//            try {
//                new com.ucware.icontools.DemoReader(this.val$args[0]);
//            } catch(IOException var1_2) {
//                var1_2.printStackTrace();
//            }
//        }
//    }

}
