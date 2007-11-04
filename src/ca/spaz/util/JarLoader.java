package ca.spaz.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

/**
 * Allows class resources to be accessed by URL stream type.
 */
public class JarLoader extends URLStreamHandler implements URLStreamHandlerFactory {

   public URLStreamHandler createURLStreamHandler(String protocol) {
      return (protocol.equalsIgnoreCase("class")) ? this : null;
   }
   
   protected URLConnection openConnection(final URL url) throws IOException {
      return new URLConnection(url) {
         private Class base;

         public synchronized void connect() throws IOException { 
            if (base == null) {
               try {
                  base = Class.forName(url.getHost()); 
               } catch (ClassNotFoundException e) {
                  throw new IOException(e.getLocalizedMessage());
               }
            }
         }
         
         public synchronized InputStream getInputStream() throws IOException {
            connect(); // make sure we're connected            
            String path = url.getPath(); 
            if (path.startsWith("/")) {
               path = path.substring(1);
            }
            return base.getClassLoader().getResourceAsStream(path);
         }

         public String getContentType() {
            return guessContentTypeFromName(url.getPath());
         }

      };
   }
  
}
