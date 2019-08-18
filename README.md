[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.org/oleg-cherednik/IconManager.svg?branch=master)](https://travis-ci.org/oleg-cherednik/IconManager)
               
# Icon Manager

This is java utilite reads different icon formats to use it in the applicaitons (e.g. _Swing_), but it doesn't depend on any _Swing's_ sources).

E.g. in Swing application, usually we use png files for images. This manager provide ability to use any icon formats instead.

Currently supports:
- *.ico - Windows Icon
- *.icl - Windows Icon Library
- *.icns - Macintosh Icon
 
##### How do we use images in _Swing_ application
```java
JLabel label_16x16_HighColor = new JLabel(new ImageIcon("smile_16x16_HighColor.png"));
JLabel label_24x24_HighColor = new JLabel(new ImageIcon("smile_24x24_HighColor.png"));
JLabel label_32x32_HighColor = new JLabel(new ImageIcon("smile_32x32_HighColor.png"));
```
It means, that if we use lot's of different images, we have lot's of files in resources.

##### How to use _Icon Manager_
- _*.ico_ and _*.icns_
```java
IconManager iconManager = IconManager.getInstance();
File file = new File("smile.ico");  // or "smile.icns"
String id = file.getName();
iconManager.addIcon(id, ImageIO.createImageInputStream(file));

// get icon from the icon manager
IconFile iconFile = iconManager.getIconFile(id);

JLabel label_16x16_HighColor = new JLabel(new ImageIcon(iconFile.getImage(ImageKey.parse(16, 16, 16))));
JLabel label_24x24_HighColor = new JLabel(new ImageIcon(iconFile.getImage(ImageKey.parse(24, 24, 16))));
JLabel label_32x32_HighColor = new JLabel(new ImageIcon(iconFile.getImage(ImageKey.parse(32, 32, 16))));

// or use id directly in format: "<width>x<height>_<bitsPerPixel>"
JLabel label_16x16_HighColor = new JLabel(new ImageIcon(iconFile.getImage("16x16_16")));
JLabel label_24x24_HighColor = new JLabel(new ImageIcon(iconFile.getImage("24x24_16")));
JLabel label_32x32_HighColor = new JLabel(new ImageIcon(iconFile.getImage("32x32_16")));
```
- _*.icl_
This is a Windows icon library, therefore each icon has it's own name (lower-case) inside the library
```java
IconManager iconManager = IconManager.getInstance();
File file = new File("smile.icl");
String id = file.getName();
iconManager.addIcon(id, ImageIO.createImageInputStream(file));

// get icon library from the icon manager
IclFile iconFile = iconManager.getIconFile(id);

// get list of  existed icons from the library
Set<String> names = getNames(); // e.g. name = "Doom"
String firstIcon = names.iterator().next();

JLabel label_16x16_HighColor = new JLabel(new ImageIcon(iconFile.getImage(ImageKey.parse("Doom", 16, 16, 16))));
JLabel label_24x24_HighColor = new JLabel(new ImageIcon(iconFile.getImage(ImageKey.parse("Doom", 24, 24, 16))));
JLabel label_32x32_HighColor = new JLabel(new ImageIcon(iconFile.getImage(ImageKey.parse("Doom", 32, 32, 16))));

// or use id directly in format: "<iconMame>_<width>x<height>_<bitsPerPixel>"
JLabel label_16x16_HighColor = new JLabel(new ImageIcon(iconFile.getImage("doom_16x16_16")));
JLabel label_24x24_HighColor = new JLabel(new ImageIcon(iconFile.getImage("doom_24x24_16")));
JLabel label_32x32_HighColor = new JLabel(new ImageIcon(iconFile.getImage("doom_32x32_16")));
```     
##### Links
* Home page: https://github.com/oleg-cherednik/IconManager
