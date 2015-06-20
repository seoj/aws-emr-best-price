package my.seoj.aws.emr.best.price.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import my.seoj.aws.emr.best.price.model.BestPrice;
import my.seoj.aws.emr.best.price.model.BestPriceRequest;
import my.seoj.aws.emr.best.price.model.Ec2Price;
import my.seoj.aws.emr.best.price.model.OnDemandPrice;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryResult;
import com.amazonaws.services.ec2.model.SpotPrice;

@Service
public class BestPriceCalculator
{
   private static final Logger logger = Logger.getLogger(BestPriceCalculator.class);

   @Autowired
   private OnDemandPrices onDemandPrices;

   @Autowired
   private AmazonEC2 ec2;

   public BestPrice calculate(BestPriceRequest request)
   {
      List<BestPrice> bestPrices = new ArrayList<BestPrice>();
      DescribeAvailabilityZonesRequest describeAvailabilityZonesRequest = new DescribeAvailabilityZonesRequest();
      describeAvailabilityZonesRequest.setZoneNames(request.getAvailabilityZones());
      DescribeAvailabilityZonesResult describeAvailabilityZonesResult = ec2.describeAvailabilityZones(describeAvailabilityZonesRequest);
      for (AvailabilityZone availabilityZone : describeAvailabilityZonesResult.getAvailabilityZones())
      {
         logger.debug("Finding best prices for availabilityZone:\t" + availabilityZone);
         String masterInstanceType = request.getMasterInstance().getInstanceType();
         String coreInstanceType = request.getCoreInstance().getInstanceType();

         Map<String, BigDecimal> instanceTypeSpotPrices = getInstanceTypeSpotPrices(availabilityZone, Arrays.asList(masterInstanceType, coreInstanceType));
         Map<String, BigDecimal> instanceTypeOnDemandPrices = getInstanceOnDemandPrices(availabilityZone);

         BigDecimal masterSpotPrice = instanceTypeSpotPrices.get(masterInstanceType);
         logger.debug("masterSpotPrice:\t" + masterSpotPrice);
         BigDecimal masterOnDemandPrice = instanceTypeOnDemandPrices.get(masterInstanceType);
         logger.debug("masterOnDemandPrice:\t" + masterOnDemandPrice);
         Ec2Price masterBestPrice = pickBestPrice(masterSpotPrice, masterOnDemandPrice, request.getMasterInstance().getDurabilityThreshold());
         logger.debug("masterBestPrice:\t" + masterBestPrice);

         BigDecimal coreSpotPrice = instanceTypeSpotPrices.get(coreInstanceType);
         logger.debug("coreSpotPrice:\t" + coreSpotPrice);
         BigDecimal coreOnDemandPrice = instanceTypeOnDemandPrices.get(coreInstanceType);
         logger.debug("coreOnDemandPrice:\t" + coreOnDemandPrice);
         Ec2Price coreBestPrice = pickBestPrice(coreSpotPrice, coreOnDemandPrice, request.getCoreInstance().getDurabilityThreshold());
         logger.debug("coreBestPrice:\t" + coreBestPrice);

         BestPrice bestPrice = new BestPrice();
         bestPrice.setAvailabilityZone(availabilityZone.getZoneName());
         bestPrice.setMasterPrice(masterBestPrice);
         bestPrice.setCorePrice(coreBestPrice);
         bestPrices.add(bestPrice);
         logger.debug("");
      }

      Collections.sort(bestPrices);

      return bestPrices.get(0);
   }

   private Ec2Price pickBestPrice(BigDecimal spotPrice, BigDecimal onDemandPrice, BigDecimal durabilityThreshold)
   {
      Ec2Price ec2Price = new Ec2Price();
      ec2Price.setIsOnDemand(true);
      ec2Price.setPrice(onDemandPrice);

      if (spotPrice.compareTo(onDemandPrice) < 0)
      {
         BigDecimal durabilityRange = spotPrice.add(spotPrice.multiply(durabilityThreshold));
         if (durabilityRange.compareTo(onDemandPrice) < 0)
         {
            ec2Price.setIsOnDemand(false);
            ec2Price.setPrice(spotPrice);
         }
      }

      return ec2Price;
   }

   private Map<String, BigDecimal> getInstanceTypeSpotPrices(AvailabilityZone availabilityZone, Collection<String> instanceTypes)
   {
      logger.debug("Finding spot prices for\t" + availabilityZone + "\t" + instanceTypes);
      Map<String, BigDecimal> instanceTypeSpotPrices = new HashMap<String, BigDecimal>();

      DescribeSpotPriceHistoryRequest describeSpotPriceHistoryRequest = new DescribeSpotPriceHistoryRequest();
      describeSpotPriceHistoryRequest.setAvailabilityZone(availabilityZone.getZoneName());
      describeSpotPriceHistoryRequest.setInstanceTypes(instanceTypes);

      DescribeSpotPriceHistoryResult describeSpotPriceHistoryResult = ec2.describeSpotPriceHistory(describeSpotPriceHistoryRequest);
      Set<String> instanceTypesFound = new HashSet<String>();
      for (SpotPrice spotPrice : describeSpotPriceHistoryResult.getSpotPriceHistory())
      {
         String instanceType = spotPrice.getInstanceType();
         if (instanceTypesFound.add(instanceType))
         {
            instanceTypeSpotPrices.put(instanceType, new BigDecimal(spotPrice.getSpotPrice()));
         }
      }
      return instanceTypeSpotPrices;
   }

   private Map<String, BigDecimal> getInstanceOnDemandPrices(AvailabilityZone availabilityZone)
   {
      Map<String, BigDecimal> instanceTypeOnDemandPrices = new HashMap<String, BigDecimal>();
      try
      {
         for (OnDemandPrice onDemandPrice : onDemandPrices.getOnDemandPrices())
         {
            if (availabilityZone.getRegionName().equals(onDemandPrice.getRegion()))
            {
               instanceTypeOnDemandPrices.put(onDemandPrice.getInstanceType(), onDemandPrice.getPrice());
            }
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      return instanceTypeOnDemandPrices;
   }
}
