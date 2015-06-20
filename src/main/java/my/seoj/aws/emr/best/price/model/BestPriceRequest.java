package my.seoj.aws.emr.best.price.model;

import java.util.HashSet;
import java.util.Set;

public class BestPriceRequest
{
   private Set<String> availabilityZones = new HashSet<String>();
   private BestPriceInstanceParameter masterInstance;
   private BestPriceInstanceParameter coreInstance;

   public Set<String> getAvailabilityZones()
   {
      return availabilityZones;
   }

   public void setAvailabilityZones(Set<String> availabilityZones)
   {
      this.availabilityZones = availabilityZones;
   }

   public BestPriceInstanceParameter getMasterInstance()
   {
      return masterInstance;
   }

   public void setMasterInstance(BestPriceInstanceParameter masterInstance)
   {
      this.masterInstance = masterInstance;
   }

   public BestPriceInstanceParameter getCoreInstance()
   {
      return coreInstance;
   }

   public void setCoreInstance(BestPriceInstanceParameter coreInstance)
   {
      this.coreInstance = coreInstance;
   }
}
