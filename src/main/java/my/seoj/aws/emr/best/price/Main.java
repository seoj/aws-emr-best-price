package my.seoj.aws.emr.best.price;

import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.Date;

import au.com.bytecode.opencsv.CSVWriter;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryResult;
import com.amazonaws.services.ec2.model.SpotPrice;

public class Main
{
   public static void main(String[] args) throws Exception
   {
      OnDemandPrices onDemandPrices = new OnDemandPrices();

      try (CSVWriter csvWriter = new CSVWriter(new FileWriter("linux-od.csv")))
      {
         csvWriter.writeNext(new String[] { "region", "instanceType", "price" });
         for (OnDemandPrice onDemandPrice : onDemandPrices.getOnDemandPrices())
         {
            String region = onDemandPrice.getRegion();
            String instanceType = onDemandPrice.getInstanceType();
            BigDecimal price = onDemandPrice.getPrice();
            csvWriter.writeNext(new String[] { region, instanceType, String.valueOf(price) });
         }
      }

      AmazonEC2 ec2 = new AmazonEC2Client();

      DescribeSpotPriceHistoryRequest describeSpotPriceHistoryRequest = new DescribeSpotPriceHistoryRequest();

      DescribeSpotPriceHistoryResult describeSpotPriceHistoryResult = ec2.describeSpotPriceHistory(describeSpotPriceHistoryRequest);
      for (SpotPrice spotPriceHistory : describeSpotPriceHistoryResult.getSpotPriceHistory())
      {
         String availabilityZone = spotPriceHistory.getAvailabilityZone();
         String instanceType = spotPriceHistory.getInstanceType();
         String productDescription = spotPriceHistory.getProductDescription();
         String spotPrice = spotPriceHistory.getSpotPrice();
         Date timestamp = spotPriceHistory.getTimestamp();
      }
   }
}
