package cop.swing.demo;

import cop.swing.icoman.IconFile;
import cop.swing.icoman.IconImage;
import cop.swing.icoman.IconManager;
import cop.swing.icoman.ImageKey;
import cop.swing.icoman.exceptions.FormatNotSupportedException;
import cop.swing.icoman.exceptions.IconManagerException;
import cop.swing.icoman.exceptions.ImageNotFoundException;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * @author Oleg Cherednik
 * @since 01.09.2013
 */
public class IconManagerDemo extends JFrame {
    private final IconManagerPanel panel = new IconManagerPanel();
    private final SettingsPanel settingsPanel = new SettingsPanel(panel);

    public IconManagerDemo() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(5, 5));

        add(new JScrollPane(panel), BorderLayout.CENTER);
        add(settingsPanel, BorderLayout.EAST);

        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setBackground(Color.gray);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
    }

    // ========== static ==========

    public static void main(String... args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new IconManagerDemo().setVisible(true);
            }
        });
    }

    // ========== classes ==========

    static class IconManagerPanel extends JPanel {
        private boolean showBorder = true;
        private boolean showSize = true;
        private IconFile iconFile;

        public IconManagerPanel() {
            init();
        }

        private void init() {
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.anchor = GridBagConstraints.WEST;
        }

        public void setShowBorder(boolean showBorder) {
            try {
                this.showBorder = showBorder;
                showIcon(iconFile);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        public void setShowSize(boolean showSize) {
            try {
                this.showSize = showSize;
                showIcon(iconFile);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        public void showIcon(IconFile iconFile) throws ImageNotFoundException, IOException {
            this.iconFile = iconFile;
            removeAll();
            GridBagConstraints gbc = createConstraints();

            for (ImageKey key : iconFile.getKeys()) {
                IconImage iconImage = iconFile.getImage(key);
                JLabel icon = createLabelIcon(iconFile.getImage(key).getIcon(), showBorder);
                JLabel sizeLabel = showSize ? new JLabel(iconImage.getHeader().getImageKey().toString()) : null;
                add(createPanel(icon, sizeLabel), gbc);
            }

            updateUI();
        }

        // ========== static ==========

        private static GridBagConstraints createConstraints() {
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.weightx = 1;

            return gbc;
        }

        private static JLabel createLabelIcon(Icon icon, boolean showBorder) {
            JLabel label = new JLabel(icon);

            label.setBorder(showBorder ? BorderFactory.createEtchedBorder() : BorderFactory.createEmptyBorder(2, 2, 2, 2));
            label.setOpaque(false);

            return label;
        }

        private static JPanel createPanel(Component... components) {
            JPanel panel = new JPanel();

            panel.setOpaque(false);

            for (Component comp : components)
                if (comp != null)
                    panel.add(comp);

            return panel;
        }
    }

    static class SettingsPanel extends JPanel implements ActionListener {
        private static final String DEF_ICON_ID = "testico";
        private static final String DEF_ICON_FILE = "testico.ico";

        private final IconManagerPanel panel;
        private final JButton changeBackgroundButton = new JButton("Change background");
        private final JButton openButton = new JButton("Open icon");
        private final JCheckBox showBorderCheckBox = new JCheckBox("border");
        private final JCheckBox showSizeCheckBox = new JCheckBox("size");
        private final JComboBox<IconKey> iconKeyCombo = new JComboBox<>();

        private final IconManager iconManager = IconManager.getInstance();
        private final Random rand = new Random();

        public SettingsPanel(IconManagerPanel panel) {
            this.panel = panel;

            init();
            addListeners();
            addDefaultIcon();
        }

        private void init() {
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = createConstraints();

            add(openButton, gbc);
            add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
            add(changeBackgroundButton, gbc);
            add(showBorderCheckBox, gbc);
            add(showSizeCheckBox, gbc);
            add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
            add(iconKeyCombo, gbc);
            add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

            gbc.weighty = 1;
            add(Box.createVerticalGlue(), gbc);

            showBorderCheckBox.setSelected(true);
            showSizeCheckBox.setSelected(true);
        }

        private void addDefaultIcon() {
            try {
                IconFile iconFile = iconManager.addIcon(DEF_ICON_ID, DEF_ICON_FILE);
                iconKeyCombo.addItem(new IconKey(DEF_ICON_ID, iconFile.getImagesAmount()));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        private void foo() {
            try (ImageInputStream in = ImageIO.createImageInputStream(new File("d:/foo1.ico"))) {
                IconFile iconFile = iconManager.addIcon("foo", in);
                iconKeyCombo.addItem(new IconKey("foo", iconFile.getImagesAmount()));
            } catch(IOException e) {
                e.printStackTrace();
            } catch(IconManagerException e) {
                e.printStackTrace();
            }
        }

        public void onSelectIcon(String id) {
            try {
                panel.showIcon(iconManager.getIconFile(id));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        private void addListeners() {
            changeBackgroundButton.addActionListener(this);
            openButton.addActionListener(this);
            iconKeyCombo.addActionListener(this);
            showBorderCheckBox.addActionListener(this);
            showSizeCheckBox.addActionListener(this);
        }

        private void onOpenButton() {
            JFileChooser dialog = new JFileChooser();

            dialog.setDialogTitle("Select files to open");
            dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
            dialog.setDialogType(JFileChooser.OPEN_DIALOG);
            dialog.setMultiSelectionEnabled(true);
            dialog.addChoosableFileFilter(FileFilterImpl.ICO);
            dialog.addChoosableFileFilter(FileFilterImpl.ICL);
            dialog.addChoosableFileFilter(FileFilterImpl.ICNS);
            dialog.setFileFilter(FileFilterImpl.ICO);

            if (dialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                for (File file : dialog.getSelectedFiles()) {
                    file = file.getAbsoluteFile();
                    String id = FilenameUtils.getBaseName(file.getName()).toLowerCase();
                    String ext = FilenameUtils.getExtension(file.getName()).toLowerCase();

                    try (ImageInputStream in = ImageIO.createImageInputStream(file)) {
                        IconFile iconFile = iconManager.addIcon(id, in);

                        IconKey iconKey = new IconKey(id, iconFile.getImagesAmount());
                        iconKeyCombo.addItem(iconKey);
                        iconKeyCombo.setSelectedItem(iconKey);
                    } catch(FormatNotSupportedException e) {
                        String message = String.format("File format '%s' is not supported", ext);
                        JOptionPane.showMessageDialog(null, message, "Could not open file", JOptionPane.ERROR_MESSAGE);
                    } catch(Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Internal error", "Could not open file", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }

        // ========== ActionListener ==========

        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == changeBackgroundButton)
                panel.setBackground(new Color(rand.nextInt(0xFFFFFF)));
            else if (event.getSource() == openButton)
                onOpenButton();
            else if (event.getSource() == iconKeyCombo)
                onSelectIcon(((IconKey)iconKeyCombo.getSelectedItem()).id);
            else if (event.getSource() == showBorderCheckBox)
                panel.setShowBorder(showBorderCheckBox.isSelected());
            else if (event.getSource() == showSizeCheckBox)
                panel.setShowSize(showSizeCheckBox.isSelected());
        }

        // ========== static ==========

        private static GridBagConstraints createConstraints() {
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;

            return gbc;
        }

        // ========== class ==========

        private static final class IconKey {
            final String id;
            final int imagesAmount;

            public IconKey(String id, int imagesAmount) {
                this.id = id;
                this.imagesAmount = imagesAmount;
            }

            // ========== Object ==========

            @Override
            public String toString() {
                return String.format("%s [:%d]", id, imagesAmount);
            }
        }

        private static final class FileFilterImpl extends FileFilter {
            private static final FileFilter ICO = new FileFilterImpl("ico", "Windows icon");
            private static final FileFilter ICL = new FileFilterImpl("icl", "Windows Icon Library File");
            private static final FileFilter ICNS = new FileFilterImpl("icns", "Mac OS X Icon Resource File");

            private final String ext;
            private final String description;

            private FileFilterImpl(String ext, String description) {
                this.ext = '.' + ext;
                this.description = description;
            }

            // ========== FileFilter ==========

            @Override
            public boolean accept(File file) {
                return file != null && (file.isDirectory() || file.toString().endsWith(ext));
            }

            @Override
            public String getDescription() {
                return description;
            }
        }
    }
}

