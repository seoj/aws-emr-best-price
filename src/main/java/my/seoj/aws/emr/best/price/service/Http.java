package my.seoj.aws.emr.best.price.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import my.seoj.aws.emr.best.price.model.HttpRequest;
import my.seoj.aws.emr.best.price.model.HttpResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class Http
{
   private static final Logger logger = Logger.getLogger(Http.class);

   public HttpResponse request(HttpRequest httpRequest) throws MalformedURLException, IOException
   {
      logger.debug("request(): httpRequest = " + httpRequest);

      String responseContent;
      HttpURLConnection connection = (HttpURLConnection) new URL(httpRequest.getUrl()).openConnection();
      connection.setRequestMethod("GET");
      try (InputStream responseInputStream = connection.getInputStream())
      {
         responseContent = IOUtils.toString(responseInputStream);
      }
      connection.disconnect();

      HttpResponse httpResponse = new HttpResponse();
      httpResponse.setContent(responseContent);

      logger.debug("request(): httpResponse = " + httpResponse);
      return httpResponse;
   }
}
