package com.cema.administration.services.client.economic;

import com.cema.administration.domain.economic.BovineOperation;
import com.cema.administration.domain.economic.Supply;
import com.cema.administration.domain.economic.SupplyOperation;
import lombok.SneakyThrows;

import java.util.List;

public interface EconomicClientService {
    @SneakyThrows
    Supply getSupply(String name);

    @SneakyThrows
    List<SupplyOperation> getAllSupplyOperations();

    @SneakyThrows
    List<BovineOperation> getAllBovineOperations();
}
