package com.hernan.empresaapp.venezuelaexchange.client;

import com.hernan.empresaapp.venezuelaexchange.dto.BcvTodayRateDto;

public interface VenezuelaExchangeProvider {
    BcvTodayRateDto fetchLatest();
    String source();
}
