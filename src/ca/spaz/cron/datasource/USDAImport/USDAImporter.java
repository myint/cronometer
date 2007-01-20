/*
 * Created on 19-Mar-2005
 */
package ca.spaz.cron.datasource.USDAImport;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import ca.spaz.cron.foods.*;
import ca.spaz.task.Task;
import ca.spaz.util.*;

public class USDAImporter implements Task {

   private static final int BATCH_SIZE = 100;
   private static final boolean TEST_MODE = false;
   private static final int CONVERSION_PROGRESS_PORTION = 60;
   private static final int DOWNLOAD_PROGRESS_PORTION = 35;
   private static final int FD_GROUP_INDEX = 3;
   private static final int WEIGHT_INDEX = 2;
   private static final int NUT_DATA_INDEX = 1;
   private static final int FOOD_DES_INDEX = 0; 

   private HashMap groups = new HashMap();
   private HashMap foods = new HashMap();

   private static final int HASH_MAX = 50;

   private URL sourceURL;

   private InputStream sourceStream;

   private PrintStream out;

   private URL getFoodSourceURL() {
      URL url = null;
      try {
         url = new URL("http://www.nal.usda.gov/fnic/foodcomp/Data/SR19/dnload/sr19.zip");
      } catch (MalformedURLException e) {
         Logger.error("getFoodSourceURL()", e);
      }
      return url;
   }
   
   public USDAImporter(OutputStream out) {
      if (null == out) {
         this.out = System.out;
      } else {
         this.out = new PrintStream(out);
      }
   }

   public InputStream getSourceStream() {
      return sourceStream;
   }

   public void setSourceStream(InputStream sourceStream) {
      this.sourceStream = sourceStream;
   }

   public URL getSourceURL() {
      return sourceURL;
   }

   public void setSourceURL(URL sourceURL) {
      this.sourceURL = sourceURL;
   }

   /* (non-Javadoc)
    * @see com.sun.swingutils.SwingWorker#construct()
    */
   public void run() {
      abort = false;
      curTask = "Importing USDA sr19";
      if (null == sourceURL && null == sourceStream) {         
         return;
      }
      if (null == sourceStream) {
//       From a source URL -- this will probably also be a zip.
         try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            tempDir = new File(tempDir, "usda_dl");
            if (tempDir.exists()) {
               out.print("Clearing old temp dir... ");
               out.flush();
               ToolBox.deleteDir(tempDir);
               out.println("Done!");
               out.flush();
            }
            out.println("Creating temp directory.");
            out.flush();
            tempDir.mkdir();
            if (!tempDir.exists()) {
               // Indicate that it aborted, somehow.
               return;
            }
            File tempFile = new File(tempDir, "usda_sr19_temp.zip");
            if (!downloadFile(sourceURL, tempFile, DOWNLOAD_PROGRESS_PORTION)) {
               return; // Indicate failure.
            }
            if (abort) return;
            ZipFile zif = new ZipFile(tempFile);
            ZipEntry[] relevantEntries = getZipEntries(zif);
            // Prepare the progress counts, starting with 50%, for the number of bytes
            // in each zip entry.
            long nbytes = getExtractedBytes(relevantEntries);

            progress = DOWNLOAD_PROGRESS_PORTION;
            InputStream ins;
            int idx = FD_GROUP_INDEX;
            int baseProgress = progress;
            ins = zif.getInputStream(relevantEntries[idx]);
            parseInputStream(ins, "food groups", relevantEntries[idx].getSize(), nbytes, baseProgress, idx);           
            if (!abort) {
               idx = FOOD_DES_INDEX;
               baseProgress = progress;
               ins = zif.getInputStream(relevantEntries[idx]);
               parseInputStream(ins, "food descriptions", relevantEntries[idx].getSize(), nbytes, baseProgress, idx);
            }
            if (!abort) {
               idx = WEIGHT_INDEX;
               baseProgress = progress;
               ins = zif.getInputStream(relevantEntries[idx]);
               parseInputStream(ins, "measures", relevantEntries[idx].getSize(), nbytes, baseProgress, idx);
            }
            if (!abort) {
               idx = NUT_DATA_INDEX;
               baseProgress = progress;
               ins = zif.getInputStream(relevantEntries[idx]);
               parseInputStream(ins, "nutrients", relevantEntries[idx].getSize(), nbytes, baseProgress, idx);
            }
            zif.close();
            if (ToolBox.deleteDir(tempDir)) {
               out.println("Succeeded in clearing temp");
               out.flush();
            } else {
               out.println("Failed to clear temp - delete " + tempFile + " manually.");
               out.flush();
            }
         } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
         } catch (IOException e) {
            e.printStackTrace();
            return;
         }
      } else {
//       From an input stream.  Assume that it is the zip.
         out.println("Reading data from local input stream");
      }
      if (!TEST_MODE) {
         out.println("Writing XML:");
         writeFoods();
      }
      progress = 100;
      return;
   }
   
   private void parseInputStream(InputStream ins, String streamName, long streamSize, long totalSize, int baseProgress, int parseType) throws IOException {
      int progressSize = (int) (CONVERSION_PROGRESS_PORTION * (double) streamSize / totalSize);
      out.print("Reading in " + streamName + "...");
      out.flush();
      CountableInputStream cbis = new CountableInputStream(ins);
      BufferedReader brin = new BufferedReader(new InputStreamReader(cbis));
      String line = brin.readLine();
      int linenumber = 0;
      while (line != null) {
         if (abort) return;
         switch (parseType) {
         case FD_GROUP_INDEX:
            parseFoodGroupLine(line);
            break;
         case FOOD_DES_INDEX:
            USDAFood food = new USDAFood(line, groups);
            Food f = new Food();
            f.setDescription(food.description);
            f.setSourceUID(food.ndb_id);
            f.setProteinConversionFactor(food.pCF);
            f.setLipidConversionFactor(food.fCF);
            f.setCarbConversionFactor(food.cCF);
            //f.setFoodGroup(new FoodGroup(food.foodgroup));
            foods.put(food.ndb_id, f);
            break;
         case WEIGHT_INDEX:
            USDAWeight w = new USDAWeight(line);            
            w.addToDB(foods);
            break;
         case NUT_DATA_INDEX:
            String[] parts = line.split("\\^");
            for (int i = 0; i < parts.length; i++) {
               parts[i] = parts[i].replaceAll("^~", "");
               parts[i] = parts[i].replaceAll("~$", "");
            }
            double amount = Double.parseDouble(parts[2]);
            String usdaID = parts[1];           
            Food fd = (Food)foods.get(parts[0]); 
            NutrientInfo ni = NutrientInfo.getByUSDA(usdaID);     
            if (ni != null) {
               double val = fd.getNutrientAmount(ni);
               fd.setNutrientAmount(ni, val + amount);
            }
            break;
         default:
            throw new IllegalArgumentException("Invalid parse type: " + parseType);
         }
         progress = (int) (baseProgress + (progressSize * ((double) cbis.getBytesRead() / streamSize)));
         line = brin.readLine();
         linenumber++;
      }

      out.println("Done.");
   }
   
   private void writeFoods() {
      Iterator iter = foods.values().iterator();
      while (iter.hasNext()) {
         try {
            Food f = (Food)iter.next();
            File file = new File("usda_sr19/"+f.getSourceUID()+".xml");
            PrintStream ps = new PrintStream(
                  new BufferedOutputStream(new FileOutputStream(file)));
            f.writeXML(ps, false);
            System.out.println(f.getDescription());
            ps.close();
         } catch (IOException ie) {
            Logger.error(ie);
         }
      }
      writeFoodsIndex();
   }
   
   private void writeFoodsIndex() {
      try {
         File file = new File("usda_sr19/foods.index");
         PrintStream ps = new PrintStream(
               new BufferedOutputStream(new FileOutputStream(file)));
         Iterator iter = foods.values().iterator();
         while (iter.hasNext()) {
            Food f = (Food)iter.next();
            ps.println(f.getSourceUID()+"|"+f.getDescription());
         }     
         ps.close();
      } catch (IOException ie) {
         Logger.error(ie);
      }
   }
   
   
   /**
    * @param line
    */
   private void parseFoodGroupLine(String line) {
      String[] parts = line.split("\\^");
      for (int i = 0; i < parts.length; i++) {
         parts[i] = parts[i].replaceAll("^~", "");
         parts[i] = parts[i].replaceAll("~$", "");
      }
      groups.put(parts[0], parts[1]);
   }

   /**
    * @param relevantEntries
    * @return
    */
   private long getExtractedBytes(ZipEntry[] relevantEntries) {
      long nbytes = 0;
      for (int i = 0; i < relevantEntries.length; i++) {
         nbytes += relevantEntries[i].getSize();
      }
      if (nbytes < 0) {
         nbytes = Long.MAX_VALUE;
      }
      return nbytes;
   }

   /**
    * @param zif
    * @return
    */
   private ZipEntry[] getZipEntries(ZipFile zif) {
      ZipEntry[] relevantEntries = new ZipEntry[4];
      for(Enumeration e = zif.entries(); e.hasMoreElements();) {
         ZipEntry went = (ZipEntry) e.nextElement();
         if (went.getName().equalsIgnoreCase("food_des.txt")) {
            relevantEntries[FOOD_DES_INDEX] = went;
         } else if (went.getName().equalsIgnoreCase("nut_data.txt")) {
            relevantEntries[NUT_DATA_INDEX] = went;
         } else if (went.getName().equalsIgnoreCase("weight.txt")) {
            relevantEntries[WEIGHT_INDEX] = went;
         } else if (went.getName().equalsIgnoreCase("fd_group.txt")) {
            relevantEntries[FD_GROUP_INDEX] = went;
         } else {
            // Irrelevant file.
         }
      }
      return relevantEntries;
   }

   /**
    * Download a URL into a file.  Takes the provided portion of the overall progress.
    * @param url an URL to download.
    * @param tempFile a file to save its contents in.
    * @param progressPortion The portion of the overall progress that this method takes.
    * @throws FileNotFoundException if the destination file cannot be found.
    * @throws IOException for all other I/O errors.
    */
   private boolean downloadFile(URL url, File tempFile, int progressPortion) throws FileNotFoundException, IOException {
      FileOutputStream fos = new FileOutputStream(tempFile);
      out.println("Created temp file: " + tempFile);
      out.flush();
      HttpURLConnection uconn = (HttpURLConnection) url.openConnection();
      out.println("Getting data from " + url);
      out.flush();
      InputStream raw = uconn.getInputStream();
      InputStream in = new BufferedInputStream(raw);
      int contentLength = uconn.getContentLength();
      byte[] data = new byte[contentLength];
      int read = 0;
      int offs = 0;
      while (offs < contentLength && !abort) {
         read = in.read(data, offs, data.length - offs);
         if (read < 0) {
            break;
         }
         offs += read;
         progress = (int)(progressPortion * (offs/(double)contentLength));
      }
      in.close();
      if (offs != contentLength) {
         out.println("Error downloading " + url);
         out.flush();
         return false;
      }
      out.print("Completed downloading " + url + ", writing...");
      out.flush();
      fos.write(data);
      fos.close();
      out.println("Done!");
      out.flush();
      return true;
   }

   private volatile int progress = 0;
   private volatile boolean abort = false;
   private volatile String curTask;
   
   public int getTaskProgress() {
      return progress;
   }

   public void abortTask() {
      abort = true;
   }

   public boolean canAbortTask() {
      return true;
   }

   public String getTaskDescription() {
      return curTask;
   }

/*
   private static HashMap nutrientMap;
   private static void addNutrient(String table, String tag, String nid) {
      USDANutrientInfo ni = new USDANutrientInfo();
      ni.table = table;
      ni.tag = tag;
      getNutrientMap().put(nid, ni);
   }
   
   public static HashMap getNutrientMap() {
      if (nutrientMap == null) {
         nutrientMap = new HashMap();
         makeNutrientMap();
      }
      return nutrientMap;
   }*/
   
   
   public static void main(String args[]) {
      USDAImporter ui = new USDAImporter(System.out);
      ui.setSourceURL(ui.getFoodSourceURL());
      ui.run();
     /* USDAFoods usdaDS = new USDAFoods();
      usdaDS.initialize();
      logger.debug("Searching...");
      String[] keys = {"APPLE", "RAW", "SKIN"};
      List list = usdaDS.findFoods(keys);
      logger.debug("Found: " + list.size());
      for (int i=0; i<list.size(); i++) {
         FoodProxy f = (FoodProxy)list.get(i);
         logger.debug(f.getDescription() + ", " + f.getSourceID());
         logger.debug("\t " + f.getFood().getCalories());
      }*/
   }
   
   
}
