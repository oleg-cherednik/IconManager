# cop.swing.icon-manager

This is java utilite reads different icon formats to use it in the applicaitons (e.g. *Swing*), but it doesn't depend on any *Swing's* sources).

E.g. in Swing application, usually we use png files for images. This manager provide ability to use any icon formats instead.

Currently supports:
* *.ico - Windows icons
* *.icns - Macintosh icons
 
###### How do we use images in *Swing* application

```java
JLabel label = new JLabel(new ImageIcon("smile.png"));
```

It means, that if we use lot's of different images, we have lot's of files in resources.
