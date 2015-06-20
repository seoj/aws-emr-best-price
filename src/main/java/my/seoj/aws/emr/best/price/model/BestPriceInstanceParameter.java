package my.seoj.aws.emr.best.price.model;

import java.math.BigDecimal;

public class BestPriceInstanceParameter
{
   private String instanceType;
   private BigDecimal durabilityThreshold;
   private BigDecimal bidAmount;

   public String getInstanceType()
   {
      return instanceType;
   }

   public void setInstanceType(String instanceType)
   {
      this.instanceType = instanceType;
   }

   public BigDecimal getDurabilityThreshold()
   {
      return durabilityThreshold;
   }

   public void setDurabilityThreshold(BigDecimal durabilityThreshold)
   {
      this.durabilityThreshold = durabilityThreshold;
   }

   public BigDecimal getBidAmount()
   {
      return bidAmount;
   }

   public void setBidAmount(BigDecimal bidAmount)
   {
      this.bidAmount = bidAmount;
   }

   public boolean isOnDemandOnly()
   {
      return bidAmount == null;
   }
}
