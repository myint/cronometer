/*
 * Created on 29-May-2005
 */
package ca.spaz.util;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.swing.*;
import javax.swing.border.*;

import ca.spaz.gui.ErrorReporter;

/**
 * A class with misc. static utility methods.
 * 
 * @author Aaron Davidson
 */
public class ToolBox {

   /**
    * Get the system specific user data directory.
    * Returns the appropriate location to store application
    * data for the user, on the current platform.
    * 
    * @return a File for the base user data directory
    */
   public static File getUserDirectory() {      
      String userHome = System.getProperty("user.home");
      if (isMacOSX()) {
         return new File(userHome, "Library/Preferences/");
      }
      if (isWindows()) {
         return new File(userHome, "Application Data");
      }
      // default
      return new File(userHome);
   }
   
   public static File getUserAppDirectory(String appname) {
      if (isWindows() || isMacOSX()) {
         return new File(getUserDirectory(), appname);
      }
      return new File(getUserDirectory(), "." + appname );
   }
   
   /**
    * Returns true if we are currently running on Mac OS X
    * @return true if we are currently running on Mac OS X
    */
   public static boolean isMacOSX() {     
      String osname = System.getProperty("os.name");
      return (osname.equals("Mac OS X"));
   }
   
   /**
    * Returns true if we are currently running on Windows
    * @return true if we are currently running on Windows
    */
   public static boolean isWindows() {
      String osname = System.getProperty("os.name").toLowerCase();
      return osname.startsWith("windows");
   }

   /**
    * Returns true if we are currently running on an older Windows OS
    * such as Windows 95, 98, ME
    * @return true if we are currently running on an older Windows OS
    */
   public static boolean isOlderWindows() {
      String osname = System.getProperty("os.name");
      if (osname.equalsIgnoreCase("Windows 95")) return true;
      if (osname.equalsIgnoreCase("Windows 98")) return true;
      if (osname.equalsIgnoreCase("Windows Me")) return true;
      return false;
   }

   /**
    * Decompress a byte array
    * @param data the compressed bytes
    * @return the uncompressed bytes
    */
   public static byte[] uncompress(byte[] data) {
      Inflater decompresser = new Inflater();
      decompresser.setInput(data);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buffer=new byte[1024];
      while (!decompresser.finished()) {
         try {
            int cnt = decompresser.inflate(buffer);
            baos.write(buffer, 0, cnt);
         } catch (DataFormatException e) {
            e.printStackTrace();
         }         
      }
      return baos.toByteArray();
   }
   
   /**
    * Download a URL to a file
    * 
    * @param fromURL
    * @param toFile
    * @throws IOException
    */
   public static void downloadBinary(URL fromURL, File toFile) throws IOException {
      DataInputStream din = new DataInputStream(
         new BufferedInputStream(fromURL.openStream())); 
      DataOutputStream out = new DataOutputStream(
         new BufferedOutputStream(new FileOutputStream(toFile)));
      int b = din.read();
      while (b >= 0) {
         out.write((byte)b); 
         b = din.read();
      }
      din.close();
      out.close();
   }
   
   /**
    * Recursively delete a directory
    * 
    * @param dir The directory to delete.
    * @return <code>true</code> if the delete succeeded, <code>false</code> if it failed at some point.
    */
   public static boolean deleteDir(File dir) {
      if (dir == null) {
         return false;
      }
      // to see if this directory is actually a symbolic link to a directory,
      // we want to get its canonical path - that is, we follow the link to
      // the file it's actually linked to
      File candir;
      try {
         candir = dir.getCanonicalFile();
      } catch (IOException e) {
         return false;
      }
      
      // a symbolic link has a different canonical path than its actual path,
      // unless it's a link to itself
      if (!candir.equals(dir.getAbsoluteFile()) && !isWindows()) {
         // this file is a symbolic link, and there's no reason for us to
         // follow it, because then we might be deleting something outside of
         // the directory we were told to delete
         return false;
      }
      
      // now we go through all of the files and subdirectories in the
      // directory and delete them one by one
      File[] files = candir.listFiles();
      if (files != null) {
         for (int i = 0; i < files.length; i++) {
            File file = files[i];
            
            // in case this directory is actually a symbolic link, or it's
            // empty, we want to try to delete the link before we try
            // anything
            boolean deleted = file.delete();
            if (!deleted) {
               // deleting the file failed, so maybe it's a non-empty
               // directory
               if (file.isDirectory()) deleteDir(file);
               
               // otherwise, there's nothing else we can do
            }
         }
      }
      
      // now that we tried to clear the directory out, we can try to delete it
      // again
      return dir.delete();  
   }
   
   /**
    * Handy dandy method to pop up a quick and easy Dialog.
    * 
    * @param parent the parent frame
    * @param title the dialog title
    * @param content the dialog content panel
    * 
    * @return a dialog ready to display
    */
   public static JDialog getDialog(JFrame parent, String title, JComponent content) {
      JDialog dialog = new JDialog(parent);
      dialog.setTitle(title);
      dialog.getContentPane().add(content);
      dialog.pack();
      dialog.setModal(true);
      dialog.setLocationRelativeTo(parent);
      return dialog;
   }

   /**
    * Sleep for some amount of time.
    * Conveniently wraps sleep in exception.
    * 
    * @param tix milliseconds to sleep.
    */
   public static void sleep(int tix) {
      try {
         Thread.sleep(tix);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
   
   /**
    * Centers a given frame on the screen.
    * @return the location of the top-left of window
    */
   public static Point centerFrame(Window frame) {
      Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
      Dimension scrSize = defaultToolkit.getScreenSize();
      int width = frame.getWidth();
      int height = frame.getHeight();
      Point p = new Point(scrSize.width / 2 - width / 2, scrSize.height / 2 - height / 2);
      frame.setLocation(p);
      return p;
   }
   
   /**
    * Centers a given frame on the screen.
    */
   public static void centerOver(Window top, Window bottom) { 
      int width = top.getWidth();
      int height = top.getHeight();
      top.setLocation(
            bottom.getX() + bottom.getWidth() / 2 - width / 2, 
            bottom.getY() + bottom.getHeight() / 2 - height / 2);
   }
   
   /**
    * See if the two dates fall on the same calendar day.
    * @return true if the two dates fall on the same calendar day.
    */
   public static boolean isSameDay(Date a, Date b) {
      Calendar aC = new GregorianCalendar(TimeZone.getDefault());
      aC.setTime(a);
      Calendar bC = new GregorianCalendar(TimeZone.getDefault());
      bC.setTime(b);
      return 
         (aC.get(Calendar.DATE) == bC.get(Calendar.DATE)) &&
         (aC.get(Calendar.MONTH) == bC.get(Calendar.MONTH)) &&
         (aC.get(Calendar.YEAR) == bC.get(Calendar.YEAR));      
   }
   
   /**
    * Load the contents of a text file into a String 
    * @param fname the file name
    * @return the contents of the file
    */
   public static String loadFile(String fname) {      
     return loadFile(new File(fname));        
   }
   
   /**
    * Load the contents of a text file into a String 
    * @param fname the file name
    * @return the contents of the file
    */
   public static String loadFile(File file) {
      StringBuffer sb = new StringBuffer();
      try {
         InputStream is = new BufferedInputStream(new FileInputStream(file));
         int b = -1;
         while ((b = is.read()) != -1) {
            sb.append((char)b);
         }
         is.close();
      } catch (IOException e) { return null; }
      return sb.toString();
   }

   /**
    * Copy the source file to the destination file.
    * @param source File of the source file
    * @param destination File of the destination file
    * @throws IOException
    * @throws FileNotFoundException
    */
   public static void copyFile(File source, File destination)
      throws IOException, FileNotFoundException
   {
      FileInputStream inStream = new FileInputStream(source);
      FileOutputStream outStream = new FileOutputStream(destination);
      byte[] buffer = new byte[1024];
      int bytesRead = inStream.read(buffer);
      while (bytesRead != -1) {
         outStream.write(buffer, 0, bytesRead);
         bytesRead = inStream.read(buffer);
      }
      inStream.close();
      outStream.close();
   }
   
   public static final int makeIntFromByte4(byte[] b) {
      return b[0]<<24 | (b[1]&0xff)<<16 | (b[2]&0xff)<<8 | (b[3]&0xff);
   }
   
   public static final byte[] makeByte4FromInt(int i) {
      return new byte[] { (byte)(i>>24), (byte)(i>>16), (byte)(i>>8), (byte)i };
   }
   
   /**
    * Get the hex string for a color in HTML
    */
   public static String toHTML(Color col) {
      return Integer.toHexString(col.getRGB() & 0x00FFFFFF);
   }

   /**
    * Divide with a divide-by-zero check.
    * Prevents strange divide by zero problems.
    * @return a/b, or 0 if b == 0.
    */
   public static double safeDivide(double a, double b) {
      return safeDivide(a,b,0);
   }

   /**
    * Divide with a divide-by-zero check.
    * Prevents strange divide by zero problems.
    * @return a/b, or divByZeroResult if b == 0.
    */
   public static double safeDivide(double a, double b, double divByZeroResult) {
      return (b!=0) ? a/b : divByZeroResult;
   }
   
   /**
    * Return an object of the given type, or null if not able.
    * Usefull if we don't want the detailed error handling.
    * @param classname a classname
    * @return  an object of the given type, or null if not able.
    */   
   public static Object instantiate(String classname) {
      Object o = null;
      try {
         Class c = Class.forName(classname);   
         if (c != null) {
            o = c.newInstance();
         }
      } catch (Exception e) { /* deliberately eat errors and return null */ }
      return o;
   }
   
   /**
    * See if a class exists or not   
    * @param classname a classname
    * @return true if it is available
    */   
   public static boolean classExists(String classname) {
       try {  
         return Class.forName(classname) != null;
      } catch (ClassNotFoundException e) { /* deliberately eat errors and return false */ }
      return false;
   }
   

   
   public static void launchURL(Component parent, String url) {
      try {
         BrowserLauncher.openURL(url);
         return;
      } catch (IOException e) {
         e.printStackTrace();
      } 
      try {
         if (ToolBox.isOlderWindows()) {
            Runtime.getRuntime().exec("command.com /e:4096 /c start \""+url+"\"");
         } else {
            Runtime.getRuntime().exec("start \""+url+"\"");
         }
         return;
      } catch (IOException e) {
         Logger.error(e);      
      }
      ErrorReporter.showError("Could not load URL:\n"+url, parent);
   }
   
   public static void changeFontSizes(Border b, float delta) {
      if (b != null) {
         if (b instanceof CompoundBorder) {
            CompoundBorder cb = (CompoundBorder)b;
            changeFontSizes(cb.getInsideBorder(), delta);
            changeFontSizes(cb.getOutsideBorder(), delta);
         } else if (b instanceof TitledBorder) {
            TitledBorder tb = (TitledBorder)b;
            tb.setTitleFont(tb.getTitleFont().deriveFont(tb.getTitleFont().getSize2D()+delta));
         }
      }
   }
   
   public static void changeFontSizes(JComponent c, float delta) {
      c.setFont(c.getFont().deriveFont(c.getFont().getSize2D()+delta)); 
      for (int i=0; i<c.getComponentCount(); i++) {
         Component jc = c.getComponent(i);
         if (jc instanceof JComponent) {
            changeFontSizes((JComponent) jc, delta);
            Border b = ((JComponent) jc).getBorder();
            changeFontSizes(b, delta);           
         }
      }
   }
 
}
