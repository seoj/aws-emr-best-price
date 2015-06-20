package my.seoj.aws.emr.best.price.model;

import java.math.BigDecimal;

public class BestPrice implements Comparable<BestPrice>
{
   private String availabilityZone;
   private Ec2Price masterPrice;
   private Ec2Price corePrice;

   public String getAvailabilityZone()
   {
      return availabilityZone;
   }

   public void setAvailabilityZone(String availabilityZone)
   {
      this.availabilityZone = availabilityZone;
   }

   public Ec2Price getMasterPrice()
   {
      return masterPrice;
   }

   public void setMasterPrice(Ec2Price masterPrice)
   {
      this.masterPrice = masterPrice;
   }

   public Ec2Price getCorePrice()
   {
      return corePrice;
   }

   public void setCorePrice(Ec2Price corePrice)
   {
      this.corePrice = corePrice;
   }

   public BigDecimal getTotalPrice()
   {
      return masterPrice.add(corePrice);
   }

   @Override
   public int compareTo(BestPrice o)
   {
      return getTotalPrice().compareTo(o.getTotalPrice());
   }
}
