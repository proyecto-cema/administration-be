package com.cema.administration.services.client.activity;

import com.cema.administration.domain.activity.Feeding;
import com.cema.administration.domain.activity.Ultrasound;
import com.cema.administration.domain.activity.Weighing;

import java.util.List;

public interface ActivityClientService {
    List<Ultrasound> getAllUltrasounds();

    List<Weighing> getAllWeightings();

    List<Feeding> getAllFeedings();

    List<Weighing> getLastWeightingsForBovine(String bovineTag);
}
