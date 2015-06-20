package my.seoj.aws.emr.best.price;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class OnDemandPrices
{
   public List<OnDemandPrice> getOnDemandPrices() throws IOException
   {
      List<OnDemandPrice> onDemandPrices = new ArrayList<OnDemandPrice>();

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
      String onDemandPricesJsonString;
      try
      {
         scriptEngine.eval("function callback(obj){return JSON.stringify(obj);}");
         onDemandPricesJsonString = (String) scriptEngine.eval(onDemandPricesJsonpString);
      }
      catch (ScriptException e)
      {
         throw new RuntimeException("Error processing JSONP response", e);
      }
      
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

                  OnDemandPrice onDemandPrice = new OnDemandPrice();
                  onDemandPrice.setRegion(region);
                  onDemandPrice.setInstanceType(size);
                  onDemandPrice.setPrice(new BigDecimal(price));
                  onDemandPrices.add(onDemandPrice);
               }
            }
         }
      }

      return onDemandPrices;
   }
}
