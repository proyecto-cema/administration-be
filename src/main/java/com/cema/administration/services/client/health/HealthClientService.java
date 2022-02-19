package com.cema.administration.services.client.health;

import com.cema.administration.domain.health.Illness;
import lombok.SneakyThrows;

import java.util.List;

public interface HealthClientService {
    @SneakyThrows
    List<Illness> getAllBovineIllness();
}
