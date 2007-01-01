/*
 *******************************************************************************
 * Copyright (c) 2005 Chris Rose and AIMedia
 * All rights reserved. CountableBufferedInputStream and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Chris Rose
 *******************************************************************************/
package ca.spaz.util;

import java.io.*;

/**
 * This class implements an InputStream that counts the bytes that it has
 * read.
 * 
 * @author Chris Rose
 */
public class CountableInputStream extends FilterInputStream {

   private long bytesRetrieved = 0;

   private long markpos = -1;

   /**
    * Create a new <code>CountableInputStream</code> and wrap it around the
    * supplied <code>InputStream</code>.
    * 
    * @param in an <code>InputStream</code> object to count.
    */
   public CountableInputStream(InputStream in) {
      super(in);
   }

   /* (non-Javadoc)
    * @see java.io.InputStream#read()
    */
   public synchronized int read() throws IOException {
      int next = super.read();
      if (next != -1) {
         bytesRetrieved++;
      }
      return next;
   }

   /* (non-Javadoc)
    * @see java.io.InputStream#read(byte[], int, int)
    */
   public synchronized int read(byte[] b, int off, int len) throws IOException {
      int next = super.read(b, off, len);
      if (next != -1) {
         bytesRetrieved += next;
      }
      return next;
   }

   /* (non-Javadoc)
    * @see java.io.InputStream#mark(int)
    */
   public synchronized void mark(int readlimit) {
      super.mark(readlimit);
      markpos = bytesRetrieved;
   }

   /* (non-Javadoc)
    * @see java.io.InputStream#skip(long)
    */
   public long skip(long n) throws IOException {
      long next = super.skip(n);
      bytesRetrieved += next;
      return next;
   }

   /* (non-Javadoc)
    * @see java.io.InputStream#reset()
    */
   public synchronized void reset() throws IOException {
      bytesRetrieved = markpos;
      super.reset();
   }

   /* (non-Javadoc)
    * @see java.io.InputStream#read(byte[])
    */
   public int read(byte[] b) throws IOException {
      int next = super.read(b);
      if (next != -1) {
         bytesRetrieved += next;
      }
      return next;
   }
   
   /**
    * Get a count of the bytes read by this stream.
    * @return the number of bytes read by this stream.
    */
   public long getBytesRead() {
      return bytesRetrieved;
   }

}
