package com.cema.administration.controllers;

import com.cema.administration.domain.activity.Ultrasound;
import com.cema.administration.domain.activity.Weighing;
import com.cema.administration.domain.bovine.Bovine;
import com.cema.administration.domain.report.Batch;
import com.cema.administration.domain.report.Disease;
import com.cema.administration.domain.report.FoodConsumption;
import com.cema.administration.domain.report.Income;
import com.cema.administration.domain.report.Live;
import com.cema.administration.domain.report.LiveCost;
import com.cema.administration.domain.report.Pregnancy;
import com.cema.administration.domain.report.Weight;
import com.cema.administration.domain.report.YearlyReport;
import com.cema.administration.services.client.activity.impl.ActivityClientServiceImpl;
import com.cema.administration.services.client.bovine.BovineClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Generates data reports for CEMA. V1")
@Validated
public class ReportingController {

    private static final String BASE_URL = "/reporting/";

    private final Logger LOG = LoggerFactory.getLogger(ReportingController.class);

    private final ActivityClientServiceImpl activityClientService;
    private final BovineClientService bovineClientService;

    public ReportingController(BovineClientService bovineClientService, ActivityClientServiceImpl activityClientService) {
        this.bovineClientService = bovineClientService;
        this.activityClientService = activityClientService;
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Get a report of the pregnancy level over the years and for the current year")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Report returned."),
            @ApiResponse(code = 401, message = "You are not allowed to get this report")
    })
    @GetMapping(value = BASE_URL + "/pregnancy", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<YearlyReport> getPregnancyReport(
            @ApiParam(
                    value = "The year when the report starts.",
                    example = "2017")
            @RequestParam(value = "yearFrom", required = false, defaultValue = "-1") int yearFrom,
            @ApiParam(
                    value = "The year when the report ends",
                    example = "2021")
            @RequestParam(value = "yearTo", required = false, defaultValue = "-1") int yearTo) {

        LOG.info("Request to create pregnancy report");

        Map<Integer, Integer> positives = new HashMap<>();
        Map<Integer, Integer> total = new HashMap<>();

        List<Ultrasound> ultrasounds = activityClientService.getAllUltrasounds();

        ultrasounds = ultrasounds.stream()
                .filter(ultrasound -> StringUtils.hasText(ultrasound.getResult()))
                .collect(Collectors.toList());

        for (Ultrasound ultrasound : ultrasounds) {
            Integer year = ultrasound.getExecutionYear();
            int totalCount = total.getOrDefault(year, 0);
            int positivesCount = positives.getOrDefault(year, 0);
            totalCount++;
            total.put(year, totalCount);
            if ("positivo".equalsIgnoreCase(ultrasound.getResult()) || "positive".equalsIgnoreCase(ultrasound.getResult())) {
                positivesCount++;
            }
            positives.put(year, positivesCount);
        }

        YearlyReport pregnancy = new YearlyReport();
        pregnancy.setType("pregnancy");
        pregnancy.setDescription("Porcentaje de vacas preñadas por año");
        pregnancy.setReportedList(new ArrayList<>());

        for (Integer year : total.keySet()) {
            float totalCount = total.get(year);
            float positivesCount = positives.get(year);
            Float percentage = (positivesCount / totalCount) * 100;
            pregnancy.getReportedList().add(new Pregnancy(year, percentage));
        }

        pregnancy.filterByYear(yearFrom, yearTo);

        pregnancy.getReportedList().sort(null);

        return new ResponseEntity<>(pregnancy, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Get a report of the disease level over the years and for the current year")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Report returned."),
            @ApiResponse(code = 401, message = "You are not allowed to get this report")
    })
    @GetMapping(value = BASE_URL + "/disease", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<YearlyReport> getDiseaseReport(
            @ApiParam(
                    value = "The year when the report starts.",
                    example = "2017")
            @RequestParam(value = "yearFrom", required = false, defaultValue = "-1") int yearFrom,
            @ApiParam(
                    value = "The year when the report ends",
                    example = "2021")
            @RequestParam(value = "yearTo", required = false, defaultValue = "-1") int yearTo) {

        LOG.info("Request to create disease report");

        YearlyReport disease = YearlyReport.builder()
                .type("disease")
                .description("Cantidad de infecciones anuales por tipo")
                .reported(new Disease(2016, 123, "Aftosa"))
                .reported(new Disease(2017, 200, "Aftosa"))
                .reported(new Disease(2018, 250, "Aftosa"))
                .reported(new Disease(2019, 101, "Aftosa"))
                .reported(new Disease(2020, 115, "Aftosa"))
                .reported(new Disease(2021, 150, "Aftosa"))
                .reported(new Disease(2016, 40, "Leptospirosis"))
                .reported(new Disease(2017, 20, "Leptospirosis"))
                .reported(new Disease(2018, 5, "Leptospirosis"))
                .reported(new Disease(2019, 50, "Leptospirosis"))
                .reported(new Disease(2020, 55, "Leptospirosis"))
                .reported(new Disease(2021, 10, "Leptospirosis"))
                .reported(new Disease(2016, 13, "Brucelosis"))
                .reported(new Disease(2017, 3, "Brucelosis"))
                .reported(new Disease(2018, 15, "Brucelosis"))
                .reported(new Disease(2019, 2, "Brucelosis"))
                .reported(new Disease(2020, 0, "Brucelosis"))
                .reported(new Disease(2021, 3, "Brucelosis"))
                .build();

        disease.filterByYear(yearFrom, yearTo);

        return new ResponseEntity<>(disease, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Get a report of the weight level over the years and for the current year")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Report returned."),
            @ApiResponse(code = 401, message = "You are not allowed to get this report")
    })
    @GetMapping(value = BASE_URL + "/weight", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<YearlyReport> getWeightReport(
            @ApiParam(
                    value = "The year when the report starts.",
                    example = "2017")
            @RequestParam(value = "yearFrom", required = false, defaultValue = "-1") int yearFrom,
            @ApiParam(
                    value = "The year when the report ends",
                    example = "2021")
            @RequestParam(value = "yearTo", required = false, defaultValue = "-1") int yearTo) {

        LOG.info("Request to create weight report");

        List<Weighing> weightings = activityClientService.getAllWeightings();
        weightings = weightings.stream()
                .filter(weighing -> StringUtils.hasText(weighing.getCategory()))
                .filter(weighing -> weighing.getWeight() != null)
                .collect(Collectors.toList());

        Map<String, Weight> totalWeights = new HashMap<>();
        Map<String, Integer> totals = new HashMap<>();

        for (Weighing weighing : weightings) {
            String category = weighing.getCategory();
            Integer year = weighing.getExecutionYear();
            Long weight = weighing.getWeight();
            String key = category + year;

            int totalCount = totals.getOrDefault(key, 0);
            Weight totalWeight = totalWeights.getOrDefault(key, new Weight(year, 0L, category));
            totalCount++;
            totals.put(key, totalCount);
            totalWeight.setWeight(totalWeight.getWeight() + weight);
            totalWeights.put(key, totalWeight);
        }

        YearlyReport weightReport = new YearlyReport();
        weightReport.setType("weight");
        weightReport.setDescription("Peso promedio anual por categoria");
        weightReport.setReportedList(new ArrayList<>());

        for (String key : totalWeights.keySet()) {
            Weight weight = totalWeights.get(key);
            Integer total = totals.get(key);
            weight.setWeight(weight.getWeight() / total);
            weightReport.getReportedList().add(weight);
        }

        weightReport.filterByYear(yearFrom, yearTo);

        weightReport.getReportedList().sort(null);

        return new ResponseEntity<>(weightReport, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Get a report of the weight level over the years and for the current year by batch")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Report returned."),
            @ApiResponse(code = 401, message = "You are not allowed to get this report")
    })
    @GetMapping(value = BASE_URL + "/batch", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<YearlyReport> getBatchReport(
            @ApiParam(
                    value = "The year when the report starts.",
                    example = "2017")
            @RequestParam(value = "yearFrom", required = false, defaultValue = "-1") int yearFrom,
            @ApiParam(
                    value = "The year when the report ends",
                    example = "2021")
            @RequestParam(value = "yearTo", required = false, defaultValue = "-1") int yearTo) {

        LOG.info("Request to create batch report");

        YearlyReport batch = YearlyReport.builder()
                .type("batch")
                .description("Peso promedio anual por batch")
                .reported(new Batch(2016, 712, "las_vacas_negras"))
                .reported(new Batch(2017, 715, "las_vacas_negras"))
                .reported(new Batch(2018, 760, "las_vacas_negras"))
                .reported(new Batch(2019, 750, "las_vacas_negras"))
                .reported(new Batch(2020, 700, "las_vacas_negras"))
                .reported(new Batch(2021, 712, "las_vacas_negras"))
                .reported(new Batch(2016, 711, "batch_2"))
                .reported(new Batch(2017, 712, "batch_2"))
                .reported(new Batch(2018, 730, "batch_2"))
                .reported(new Batch(2019, 750, "batch_2"))
                .reported(new Batch(2020, 722, "batch_2"))
                .reported(new Batch(2021, 723, "batch_2"))
                .reported(new Batch(2016, 1100, "toros"))
                .reported(new Batch(2017, 1115, "toros"))
                .reported(new Batch(2018, 1200, "toros"))
                .reported(new Batch(2019, 1221, "toros"))
                .reported(new Batch(2020, 1150, "toros"))
                .reported(new Batch(2021, 1200, "toros"))
                .build();

        batch.filterByYear(yearFrom, yearTo);

        return new ResponseEntity<>(batch, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Get a report of the food consumed by bovines each year separated by category")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Report returned."),
            @ApiResponse(code = 401, message = "You are not allowed to get this report")
    })
    @GetMapping(value = BASE_URL + "/feed", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<YearlyReport> getFeedReport(
            @ApiParam(
                    value = "The year when the report starts.",
                    example = "2017")
            @RequestParam(value = "yearFrom", required = false, defaultValue = "-1") int yearFrom,
            @ApiParam(
                    value = "The year when the report ends",
                    example = "2021")
            @RequestParam(value = "yearTo", required = false, defaultValue = "-1") int yearTo) {

        LOG.info("Request to create batch report");

        YearlyReport foodConsumption = YearlyReport.builder()
                .type("foodConsumption")
                .description("Alimento consumido anualmente por categoria")
                .reported(new FoodConsumption(2016, 2500, "Ternero"))
                .reported(new FoodConsumption(2017, 2250, "Ternero"))
                .reported(new FoodConsumption(2018, 2100, "Ternero"))
                .reported(new FoodConsumption(2019, 2500, "Ternero"))
                .reported(new FoodConsumption(2020, 2200, "Ternero"))
                .reported(new FoodConsumption(2021, 2000, "Ternero"))
                .reported(new FoodConsumption(2016, 7500, "Vaca"))
                .reported(new FoodConsumption(2017, 7250, "Vaca"))
                .reported(new FoodConsumption(2018, 7500, "Vaca"))
                .reported(new FoodConsumption(2019, 7100, "Vaca"))
                .reported(new FoodConsumption(2020, 7000, "Vaca"))
                .reported(new FoodConsumption(2021, 7100, "Vaca"))
                .reported(new FoodConsumption(2016, 8500, "Toro"))
                .reported(new FoodConsumption(2017, 8500, "Toro"))
                .reported(new FoodConsumption(2018, 8000, "Toro"))
                .reported(new FoodConsumption(2019, 8250, "Toro"))
                .reported(new FoodConsumption(2020, 8100, "Toro"))
                .reported(new FoodConsumption(2021, 8100, "Toro"))
                .build();

        foodConsumption.filterByYear(yearFrom, yearTo);

        return new ResponseEntity<>(foodConsumption, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Get a report of the cost by live kilogram")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Report returned."),
            @ApiResponse(code = 401, message = "You are not allowed to get this report")
    })
    @GetMapping(value = BASE_URL + "/performance", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<YearlyReport> getFoodPerformanceReport(
            @ApiParam(
                    value = "The year when the report starts.",
                    example = "2017")
            @RequestParam(value = "yearFrom", required = false, defaultValue = "-1") int yearFrom,
            @ApiParam(
                    value = "The year when the report ends",
                    example = "2021")
            @RequestParam(value = "yearTo", required = false, defaultValue = "-1") int yearTo) {

        LOG.info("Request to create batch report");

        YearlyReport liveCost = YearlyReport.builder()
                .type("liveCost")
                .description("Rendimiento anual de la comida por kilogramo vivo")
                .reported(new LiveCost(2016, 372000, 1000021.5, 2.6))
                .reported(new LiveCost(2017, 315000, 800021.5, 2.5))
                .reported(new LiveCost(2018, 333000, 790021.5, 2.37))
                .reported(new LiveCost(2019, 382020, 1050021.5, 2.74))
                .reported(new LiveCost(2020, 312000, 700021.5, 2.2))
                .reported(new LiveCost(2021, 333000, 1000500.5, 3.0))
                .build();

        liveCost.filterByYear(yearFrom, yearTo);

        return new ResponseEntity<>(liveCost, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Get a report of the live animals for each year")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Report returned."),
            @ApiResponse(code = 401, message = "You are not allowed to get this report")
    })
    @GetMapping(value = BASE_URL + "/live", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<YearlyReport> getLiveAnimalsReport(
            @ApiParam(
                    value = "The year when the report starts.",
                    example = "2017")
            @RequestParam(value = "yearFrom", required = false, defaultValue = "-1") int yearFrom,
            @ApiParam(
                    value = "The year when the report ends",
                    example = "2021")
            @RequestParam(value = "yearTo", required = false, defaultValue = "-1") int yearTo) {

        LOG.info("Request to create live animals report");

        List<Bovine> bovines = bovineClientService.getAllBovines();

        Map<String, Live> reports = new HashMap<>();

        for (Bovine bovine : bovines) {
            String category = bovine.getCategory();
            Integer year = bovine.getTaggingYear();
            String key = category + year;

            Live live = reports.containsKey(key) ? reports.get(key) : new Live(year, 0, category);
            live.addOne();
            reports.put(key, live);
        }

        YearlyReport liveReport = new YearlyReport();
        liveReport.setType("live");
        liveReport.setDescription("Cantidad de animales vivos por categoria por año");

        liveReport.setReportedList(new ArrayList<>(reports.values()));

        liveReport.filterByYear(yearFrom, yearTo);

        liveReport.getReportedList().sort(null);

        return new ResponseEntity<>(liveReport, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Get a report of the income versus spending by year")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Report returned."),
            @ApiResponse(code = 401, message = "You are not allowed to get this report")
    })
    @GetMapping(value = BASE_URL + "/income", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<YearlyReport> getIncomeReport(
            @ApiParam(
                    value = "The year when the report starts.",
                    example = "2017")
            @RequestParam(value = "yearFrom", required = false, defaultValue = "-1") int yearFrom,
            @ApiParam(
                    value = "The year when the report ends",
                    example = "2021")
            @RequestParam(value = "yearTo", required = false, defaultValue = "-1") int yearTo) {

        LOG.info("Request to create income report");

        YearlyReport income = YearlyReport.builder()
                .type("income")
                .description("Porcentaje de vacas preñadas por año")
                .reported(new Income(2016, 1050021.5, 1000021.5))
                .reported(new Income(2017, 790021.5, 800021.5))
                .reported(new Income(2018, 820021.5, 790021.5))
                .reported(new Income(2019, 1550021.5, 1050021.5))
                .reported(new Income(2020, 710021.5, 700021.5))
                .reported(new Income(2021, 1030500.5, 1000500.5))
                .build();

        income.filterByYear(yearFrom, yearTo);

        return new ResponseEntity<>(income, HttpStatus.OK);
    }

}