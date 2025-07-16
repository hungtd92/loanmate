package com.hungtd.loanmate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "loanmate.prepayment")
@Component
@Data
public class PrepaymentConfig {
    private BigDecimal penaltyRateDefault;
}
