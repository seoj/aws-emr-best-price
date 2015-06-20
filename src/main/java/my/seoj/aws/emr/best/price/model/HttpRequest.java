package my.seoj.aws.emr.best.price.model;

public class HttpRequest
{
   private String url;

   public String getUrl()
   {
      return url;
   }

   public void setUrl(String url)
   {
      this.url = url;
   }

   @Override
   public String toString()
   {
      return "HttpRequest [url=" + url + "]";
   }
}
