package com.flairs.iocdemo;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Binds the "shop:" block of application.yml to a typed, immutable record.
// Relaxed binding: tax-rate in YAML maps to taxRate here.
@ConfigurationProperties(prefix = "shop")
public record ShopProperties(String name, double taxRate, String currency) {
}
