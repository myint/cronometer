/*
 * Created on 29-May-2005
 */
package ca.spaz.util;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.*;

import javax.swing.*;

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
    */
   public static void centerFrame(Window frame) {
      Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
      Dimension scrSize = defaultToolkit.getScreenSize();
      int width = frame.getWidth();
      int height = frame.getHeight();
      frame.setLocation(scrSize.width / 2 - width / 2, scrSize.height / 2 - height / 2);
   }
   
   /**
    * See if the two dates fall on the same calendar day.
    * @return true if the two dates fall on the same calendar day.
    */
   public static boolean isSameDay(Date a, Date b) {
      Calendar aC = new GregorianCalendar();
      aC.setTime(a);
      Calendar bC = new GregorianCalendar();
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

}
