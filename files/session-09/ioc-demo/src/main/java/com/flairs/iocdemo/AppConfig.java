package com.flairs.iocdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // The method name (priceCalculator) becomes the bean name.
    // Spring injects ShopProperties into the parameter, just like into a constructor.
    @Bean
    public PriceCalculator priceCalculator(ShopProperties props) {
        return new PriceCalculator(props.taxRate());
    }
}
