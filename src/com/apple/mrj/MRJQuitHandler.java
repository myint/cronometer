package com.apple.mrj;
/**
 * A stub for the windows build. This class is loaded under windows
 * and apple's real class is loaded instead on Mac OS X.
 * 
 * @author Aaron Davidson
 */
public interface MRJQuitHandler {
   public void handleQuit();
}
