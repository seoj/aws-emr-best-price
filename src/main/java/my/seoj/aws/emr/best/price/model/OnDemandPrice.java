package my.seoj.aws.emr.best.price.model;

import java.math.BigDecimal;

public class OnDemandPrice
{
   private String region;
   private String instanceType;
   private BigDecimal price;

   public String getRegion()
   {
      return region;
   }

   public void setRegion(String region)
   {
      this.region = region;
   }

   public String getInstanceType()
   {
      return instanceType;
   }

   public void setInstanceType(String instanceType)
   {
      this.instanceType = instanceType;
   }

   public BigDecimal getPrice()
   {
      return price;
   }

   public void setPrice(BigDecimal price)
   {
      this.price = price;
   }
}
