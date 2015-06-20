package my.seoj.aws.emr.best.price;

import java.io.FileWriter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.IOUtils;

import au.com.bytecode.opencsv.CSVWriter;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryResult;
import com.amazonaws.services.ec2.model.SpotPrice;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class Main
{
   public static void main(String[] args) throws Exception
   {
      try (CSVWriter csvWriter = new CSVWriter(new FileWriter("linux-od.csv")))
      {
         String onDemandPricesJsonpString;
         HttpURLConnection connection =
            (HttpURLConnection) new URL("http://a0.awsstatic.com/pricing/1/ec2/linux-od.min.js?_=" + System.currentTimeMillis()).openConnection();
         connection.setRequestMethod("GET");
         try (InputStream responseInputStream = connection.getInputStream())
         {
            onDemandPricesJsonpString = IOUtils.toString(responseInputStream);
         }
         connection.disconnect();

         ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByExtension("js");
         scriptEngine.eval("function callback(obj){return JSON.stringify(obj);}");
         String onDemandPricesJsonString = (String) scriptEngine.eval(onDemandPricesJsonpString);
         Gson gson = new Gson();
         JsonElement onDemandPricesJson = gson.fromJson(onDemandPricesJsonString, JsonElement.class);
         JsonArray regionsJson = onDemandPricesJson.getAsJsonObject().get("config").getAsJsonObject().get("regions").getAsJsonArray();
         for (JsonElement regionJson : regionsJson)
         {
            String region = regionJson.getAsJsonObject().get("region").getAsString();
            JsonArray instanceTypesJson = regionJson.getAsJsonObject().get("instanceTypes").getAsJsonArray();
            for (JsonElement instanceTypeJson : instanceTypesJson)
            {
               JsonArray sizesJson = instanceTypeJson.getAsJsonObject().get("sizes").getAsJsonArray();
               for (JsonElement sizeJson : sizesJson)
               {
                  String size = sizeJson.getAsJsonObject().get("size").getAsString();
                  JsonArray valueColumnsJson = sizeJson.getAsJsonObject().get("valueColumns").getAsJsonArray();
                  for (JsonElement valueColumnJson : valueColumnsJson)
                  {
                     String price = valueColumnJson.getAsJsonObject().get("prices").getAsJsonObject().get("USD").getAsString();
                     csvWriter.writeNext(new String[] { region, size, price });
                  }
               }
            }
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
