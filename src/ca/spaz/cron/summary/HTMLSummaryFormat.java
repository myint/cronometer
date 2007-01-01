/*
 * Created on 28-Jan-2006
 */
package ca.spaz.cron.summary;

import java.util.*;

import ca.spaz.cron.foods.NutrientInfo;
import ca.spaz.cron.targets.Target;
import ca.spaz.cron.user.User;
import ca.spaz.util.StringUtil;

public class HTMLSummaryFormat extends SummaryFormat { 
   
   public String getFormatName() {
      return "HTML";
   }
    

   public String export(List servings, Date start, Date end, int days, boolean targetsOnly) {
      StringBuffer sb = new StringBuffer();
      sb.append("<html>\n");
      sb.append("<head><title>Nutrition Summary</title></head>\n");
      sb.append("<body>\n");      

      if (days > 1) {
         sb.append("<h2>Nutrition Summary</h2>\n");
         sb.append("<h3>"+dateFormat.format(start) + " to "+ dateFormat.format(end)+"\n");
         sb.append("<br>Daily Averages over " + days + " days</h3>\n");
      } else {
         sb.append("<h2>Nutrition Summary for " + dateFormat.format(end) + "</h2>\n");
      }
       
      for (int i=0; i<NutrientInfo.CATEGORIES.length; i++) {
         sb.append(exportCategory(NutrientInfo.CATEGORIES[i], servings, days, targetsOnly));
      }
      sb.append("</body>\n");
      sb.append("</html>\n");
      return sb.toString();
   }

   
   public String exportCategory(String category, List servings, int days, boolean targetsOnly) {

      StringBuffer sb = new StringBuffer();
      List nutrients = NutrientInfo.getCategory(category);
      
      sb.append("<small><table border=0 width=\"100%\" cellpadding=\"0\" cellspacing=\"1\">");
      sb.append("<tr bgcolor=\"#bbbbbb\"><td colspan=\"4\"><b>");
      sb.append(category);
      
      double tcp = getTargetCompletion(servings, nutrients, days, false);
      if (!Double.isNaN(tcp)) {
         sb.append(" (");
         sb.append(nf.format(tcp));
         sb.append(")");
      } else {
         return "";
      }
      sb.append("</b></td></tr>\n");
      
      int i = 0;
      Iterator iter = nutrients.iterator();
      while (iter.hasNext()) {
         NutrientInfo ni = (NutrientInfo)iter.next();
         Target target = User.getUser().getTarget(ni);
         if (targetsOnly) {
            if (target.isUndefined() || !User.getUser().isTracking(ni)) continue;
         }
         i++;
         if (i%2==0) {
            sb.append("<tr bgcolor=\"#ffffff\">");
         } else {
            sb.append("<tr bgcolor=\"#efefff\">");
         }
         
         sb.append(export(ni, servings, days, targetsOnly)); 
         sb.append("</tr>\n"); 
      }
      sb.append("</table></small>");

      return sb.toString();
   }

   public String export(NutrientInfo ni, List servings, int days, boolean targetsOnly) {

      StringBuffer sb = new StringBuffer();
       
      double amount = getAmount(servings, ni) / (double)days;
       
      Target target = User.getUser().getTarget(ni);
      
      sb.append("<td>"); 
      if (ni.getParent() != null) {
         sb.append("&nbsp;&nbsp;");
      }
      sb.append(ni.getName());
      sb.append("</td>");
      
      sb.append("<td align=\"right\">");
      sb.append(df.format(amount));
      sb.append("</td>");
      
      sb.append("<td>&nbsp;");
      sb.append(ni.getUnits());        
      sb.append("</td>");
      
      sb.append("<td align=\"right\">");
      if (target.getMin() > 0) {
         sb.append(StringUtil.padl(nf.format(amount/target.getMin()), 5));
      }
      sb.append("&nbsp;</td>");

      return sb.toString();
   }
    
}
