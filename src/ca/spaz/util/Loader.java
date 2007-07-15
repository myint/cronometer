package ca.spaz.util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ImageIcon;
 
/**
 * A special class for loading classes from an jar file.
 * 
 * @author Aaron Davidson
 */
public final class Loader extends ClassLoader {
   private ZipFile archive; 

   public Loader(File file) {
      try {
         archive = new ZipFile(file);
      } catch (Exception e) {
         e.printStackTrace(); 
      }  
   }
   
   /**
    * Create a new instance of a class in this jar file.
    * Must have a basic constructor with no arguments.
    * 
    * @param name the class name to instantiate
    * 
    * @return an Object created from the given class
    */
   public Object newInstance(String name) {
      try {
         Class c = this.loadClass(name, true);
         return c.newInstance();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }
   
   /**
    * Load a class by name.
    * @see java.lang.ClassLoader#loadClass()
    */
   protected Class loadClass(String name, boolean resolve) 
                           throws ClassNotFoundException {
      Class c = findLoadedClass(name);
      if (c == null) {
         try {
            c = findSystemClass(name);
         } catch (Exception e) {}
      }
      if (c == null) {
         try {
            byte data[] = loadClassData(name);
            if (data != null) {
               c = defineClass(name, data, 0, data.length);
            }
            if (c == null) {
               throw new ClassNotFoundException(name);
            }
         } catch (IOException e) {
            throw new ClassNotFoundException("Error reading class: " + name);
         }
      }
      if (resolve) {
         resolveClass(c);
      }
      return c;
   }

   private byte[] loadClassData(String filename) throws IOException {
      if (archive == null) return null;
      filename = filename.replaceAll("\\.", "/");
      return loadResource(filename + ".class");
   }  

   private byte[] loadResource(String name) {
      try {
         ZipEntry ze = archive.getEntry(name);
         if (ze == null) {          
            return null;
         }
         InputStream in = archive.getInputStream(ze);
         int size = (int)ze.getSize();
         byte buff[] = new byte[size];
         BufferedInputStream bis = new BufferedInputStream(in);
         DataInputStream dis = new DataInputStream(bis);
         
         int n = 0;
         try {
            while (true) {
               buff[n++] = dis.readByte();
            }
         } catch (EOFException eof) {}
         n--;
         dis.close();         
         return buff;
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }
   
   public ImageIcon loadImageIcon(String name) {
      byte buff[] = loadResource(name);
      if (buff != null) {     
         return new ImageIcon(buff);
      } else {
         return null;
      }     
   }
   
}
