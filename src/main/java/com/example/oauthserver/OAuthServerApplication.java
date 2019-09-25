package com.example.oauthserver;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OAuthServerApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(OAuthServerApplication.class, args);
    }

    @Bean
    ApplicationRunner app(ApplicationContext ctx)
    {
        return args -> {
            String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
            for (int i = 0; i < beanDefinitionNames.length; i++)
            {
                System.out.println(beanDefinitionNames[i]);
            }
        };
    }

}
