package my.seoj.aws.emr.best.price;

import javax.script.ScriptEngineManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.google.gson.Gson;

@Configuration
@ComponentScan(basePackages = "my.seoj.aws.emr.best.price")
public class BpcConfig
{
   @Bean
   public Gson gson()
   {
      return new Gson();
   }

   @Bean
   public ScriptEngineManager scriptEngineManager()
   {
      return new ScriptEngineManager();
   }

   @Bean
   public AmazonEC2 ec2()
   {
      return new AmazonEC2Client();
   }
}
