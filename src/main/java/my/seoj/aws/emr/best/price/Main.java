package my.seoj.aws.emr.best.price;

import java.math.BigDecimal;

import my.seoj.aws.emr.best.price.model.BestPrice;
import my.seoj.aws.emr.best.price.model.BestPriceInstanceParameter;
import my.seoj.aws.emr.best.price.model.BestPriceRequest;
import my.seoj.aws.emr.best.price.service.BestPriceCalculator;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main
{
   public static void main(String[] args) throws Exception
   {
      try (AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BpcConfig.class))
      {
         BestPriceCalculator bestPriceCalculator = applicationContext.getBean(BestPriceCalculator.class);

         BestPriceRequest request = new BestPriceRequest();
         BestPriceInstanceParameter masterInstance = new BestPriceInstanceParameter();
         masterInstance.setInstanceType("m3.large");
         masterInstance.setDurabilityThreshold(new BigDecimal(".2"));
         request.setMasterInstance(masterInstance);
         BestPriceInstanceParameter coreInstance = new BestPriceInstanceParameter();
         coreInstance.setInstanceType("m3.medium");
         coreInstance.setDurabilityThreshold(new BigDecimal(".2"));
         request.setCoreInstance(coreInstance);
         BestPrice bestPrice = bestPriceCalculator.calculate(request);
         System.out.println("Best Price:");
         System.out.println("Availability zone:\t" + bestPrice.getAvailabilityZone());
         System.out.println("Master instance:\t$" + bestPrice.getMasterPrice().getPrice() + "\t"
            + (bestPrice.getMasterPrice().getIsOnDemand() ? "on-demand" : "spot"));
         System.out.println("Core instance:\t$" + bestPrice.getCorePrice().getPrice() + "\t"
            + (bestPrice.getCorePrice().getIsOnDemand() ? "on-demand" : "spot"));
      }
   }
}
