# Icon Manager

This is java utilite reads different icon formats to use it in the applicaitons (e.g. *Swing*), but it doesn't depend on any *Swing's* sources).

E.g. in Swing application, usually we use png files for images. This manager provide ability to use any icon formats instead.

Currently supports:
* *.ico - Windows icons
* *.icns - Macintosh icons
 
##### How do we use images in *Swing* application
```java
JLabel label_16x16_HighColor = new JLabel(new ImageIcon("smile_16x16_HighColor.png"));
JLabel label_24x24_HighColor = new JLabel(new ImageIcon("smile_24x24_HighColor.png"));
JLabel label_32x32_HighColor = new JLabel(new ImageIcon("smile_32x32_HighColor.png"));
```
It means, that if we use lot's of different images, we have lot's of files in resources.

##### How to use *Icon Manager*
```java
IconManager iconManager = IconManager.getInstance();
File file = new File("smile.ico");
String id = file.getName();
IconFile iconFile = iconManager.addIcon(id, ImageIO.createImageInputStream(file));

JLabel label_16x16_HighColor = new JLabel(new ImageIcon(iconFile.getImage(ImageKey.highColor(16))));
JLabel label_24x24_HighColor = new JLabel(new ImageIcon(iconFile.getImage(ImageKey.highColor(24))));
JLabel label_32x32_HighColor = new JLabel(new ImageIcon(iconFile.getImage(ImageKey.highColor(32))));
```

 
