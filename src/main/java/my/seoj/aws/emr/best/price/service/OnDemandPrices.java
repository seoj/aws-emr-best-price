package my.seoj.aws.emr.best.price.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import my.seoj.aws.emr.best.price.model.OnDemandPrice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class OnDemandPrices
{
   private static final String JSONP_URL = "http://a0.awsstatic.com/pricing/1/ec2/linux-od.min.js?_=";
   private static final String JSONP_CALLBACK = "callback";

   @Autowired
   private Jsonp jsonp;

   private List<OnDemandPrice> onDemandPrices;

   public List<OnDemandPrice> getOnDemandPrices() throws IOException
   {
      if (onDemandPrices == null)
      {
         onDemandPrices = new ArrayList<OnDemandPrice>();

         String url = JSONP_URL + System.currentTimeMillis();
         JsonElement onDemandPricesJson = jsonp.request(url, JSONP_CALLBACK);

         addOnDemandPrices(onDemandPricesJson);
      }

      return onDemandPrices;
   }

   private void addOnDemandPrices(JsonElement onDemandPricesJson)
   {
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
   }
}
