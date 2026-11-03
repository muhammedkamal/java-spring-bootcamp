package com.flairs.iocdemo;

import org.springframework.stereotype.Service;

// @Service is a stereotype: a @Component with a meaning ("business logic lives here").
// One constructor, so @Autowired is not needed. Spring passes the beans it finds.
@Service
public class OrderService {

    private final NotificationSender sender;
    private final PriceCalculator calculator;
    private final ShopProperties props;

    public OrderService(NotificationSender sender, PriceCalculator calculator, ShopProperties props) {
        this.sender = sender;
        this.calculator = calculator;
        this.props = props;
    }

    public double placeOrder(String customer, double price) {
        double total = calculator.withTax(price);
        sender.send(customer, props.name() + ": your total is " + total + " " + props.currency());
        return total;
    }
}
