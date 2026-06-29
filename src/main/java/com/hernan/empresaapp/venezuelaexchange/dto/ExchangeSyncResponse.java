package com.hernan.empresaapp.venezuelaexchange.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ExchangeSyncResponse(
        int synchronizedRates,
        LocalDateTime synchronizedAt,
        List<ExchangeRateResponse> rates) {
}
