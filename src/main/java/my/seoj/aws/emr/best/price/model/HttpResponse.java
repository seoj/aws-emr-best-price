package my.seoj.aws.emr.best.price.model;

public class HttpResponse
{
   private String content;

   public String getContent()
   {
      return content;
   }

   public void setContent(String content)
   {
      this.content = content;
   }

   @Override
   public String toString()
   {
      return "HttpResponse [content=" + content + "]";
   }
}
