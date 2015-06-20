package my.seoj.aws.emr.best.price.service;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import my.seoj.aws.emr.best.price.model.HttpRequest;
import my.seoj.aws.emr.best.price.model.HttpResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Service
public class Jsonp
{
   private static final Logger logger = Logger.getLogger(Jsonp.class);

   @Autowired
   private Http http;

   @Autowired
   private Gson gson;

   @Autowired
   private ScriptEngineManager scriptEngineManager;

   public JsonElement request(String url, String callback) throws MalformedURLException, IOException
   {
      logger.debug("request(): url = " + url + ", callback = " + callback);

      String jsonpString;
      HttpRequest httpRequest = new HttpRequest();
      httpRequest.setUrl(url);
      HttpResponse httpResponse = http.request(httpRequest);
      jsonpString = httpResponse.getContent();

      ScriptEngine scriptEngine = scriptEngineManager.getEngineByExtension("js");
      String jsonString;
      try
      {
         scriptEngine.eval("function " + callback + "(obj){return JSON.stringify(obj);}");
         jsonString = (String) scriptEngine.eval(jsonpString);
      }
      catch (ScriptException e)
      {
         throw new RuntimeException("Error processing JSONP response", e);
      }

      JsonElement responseJson = gson.fromJson(jsonString, JsonElement.class);
      logger.debug("request(): responseJson = " + responseJson);

      return responseJson;
   }
}
