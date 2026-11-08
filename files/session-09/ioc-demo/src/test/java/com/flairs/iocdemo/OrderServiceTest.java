package com.flairs.iocdemo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

// No Spring here. Constructor injection means we can build the service by hand with fakes.
class OrderServiceTest {

    @Test
    void placeOrderAddsTaxAndNotifiesTheCustomer() {
        List<String> sent = new ArrayList<>();
        NotificationSender fake = (to, message) -> sent.add(to + "|" + message);
        OrderService service = new OrderService(fake, new PriceCalculator(0.10), new ShopProperties("T", 0.10, "EGP"));

        double total = service.placeOrder("x@y.com", 100.0);

        assertEquals(110.0, total);
        assertEquals(1, sent.size());
        assertEquals("x@y.com|T: your total is 110.0 EGP", sent.get(0));
    }
}
