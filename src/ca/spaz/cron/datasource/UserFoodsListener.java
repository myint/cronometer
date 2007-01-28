package ca.spaz.cron.datasource;

public interface UserFoodsListener {

   public void userFoodAdded(FoodProxy fp);
   public void userFoodModified(FoodProxy fp);
   public void userFoodDeleted(FoodProxy fp);

}
