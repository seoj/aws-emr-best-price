package my.seoj.aws.emr.best.price.model;

import java.math.BigDecimal;

public class Ec2Price
{
   private BigDecimal price;
   private Boolean isOnDemand;

   public BigDecimal getPrice()
   {
      return price;
   }

   public void setPrice(BigDecimal price)
   {
      this.price = price;
   }

   public Boolean getIsOnDemand()
   {
      return isOnDemand;
   }

   public void setIsOnDemand(Boolean isOnDemand)
   {
      this.isOnDemand = isOnDemand;
   }

   public BigDecimal add(Ec2Price augend)
   {
      return price.add(augend.price);
   }

   @Override
   public String toString()
   {
      return "[" + price + ", isOnDemand=" + isOnDemand + "]";
   }
}
