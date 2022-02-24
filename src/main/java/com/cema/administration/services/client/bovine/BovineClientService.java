package com.cema.administration.services.client.bovine;

import com.cema.administration.domain.bovine.Batch;
import com.cema.administration.domain.bovine.Bovine;
import lombok.SneakyThrows;

import java.util.List;

public interface BovineClientService {
    @SneakyThrows
    Bovine getBovine(String tag);

    List<Bovine> getAllBovines();

    @SneakyThrows
    List<Batch> getAllBatches();

    @SneakyThrows
    List<Bovine> getAllBovinesFromList(List<String> tags);
}
