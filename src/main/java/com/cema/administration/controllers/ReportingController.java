package com.cema.administration.controllers;

import com.cema.administration.constants.OperationType;
import com.cema.administration.domain.activity.Feeding;
import com.cema.administration.domain.activity.Ultrasound;
import com.cema.administration.domain.activity.Weighing;
import com.cema.administration.domain.bovine.Batch;
import com.cema.administration.domain.bovine.Bovine;
import com.cema.administration.domain.economic.BovineOperation;
import com.cema.administration.domain.economic.Supply;
import com.cema.administration.domain.economic.SupplyOperation;
import com.cema.administration.domain.health.Illness;
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
import com.cema.administration.services.client.economic.EconomicClientService;
import com.cema.administration.services.client.health.HealthClientService;
import com.google.common.collect.ImmutableSet;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Generates data reports for CEMA. V1")
@Validated
public class ReportingController {

    private static final String BASE_URL = "/reporting/";
    private static final Set<String> BOVINE_UNUSABLE_STATES = ImmutableSet.of("muerto", "vendido");
    private final Logger LOG = LoggerFactory.getLogger(ReportingController.class);
    private final ActivityClientServiceImpl activityClientService;
    private final BovineClientService bovineClientService;
    private final HealthClientService healthClientService;
    private final EconomicClientService economicClientService;

    public ReportingController(ActivityClientServiceImpl activityClientService, BovineClientService bovineClientService,
                               HealthClientService healthClientService, EconomicClientService economicClientService) {
        this.activityClientService = activityClientService;
        this.bovineClientService = bovineClientService;
        this.healthClientService = healthClientService;
        this.economicClientService = economicClientService;
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Get a report of the pregnancy level over the years and for the current year", response = Pregnancy.class, responseContainer = "List")
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
            double percentage = (positivesCount / totalCount) * 100;
            pregnancy.getReportedList().add(new Pregnancy(year, round(percentage, 2)));
        }

        pregnancy.filterByYear(yearFrom, yearTo);

        pregnancy.getReportedList().sort(null);

        return new ResponseEntity<>(pregnancy, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Get a report of the disease level over the years and for the current year", response = Disease.class, responseContainer = "List")
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

        List<Illness> illnesses = healthClientService.getAllBovineIllness();
        Map<String, Disease> reports = new HashMap<>();

        for (Illness illness : illnesses) {
            String diseaseName = illness.getDiseaseName();
            int year = illness.getStartingDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().getYear();
            String key = diseaseName + year;

            Disease disease = reports.containsKey(key) ? reports.get(key) : new Disease(year, 0, diseaseName);
            disease.addOne();
            reports.put(key, disease);
        }

        YearlyReport diseaseReport = YearlyReport.builder()
                .type("disease")
                .description("Cantidad de infecciones anuales por tipo")
                .build();

        diseaseReport.setReportedList(new ArrayList<>(reports.values()));

        diseaseReport.filterByYear(yearFrom, yearTo);

        diseaseReport.getReportedList().sort(null);

        return new ResponseEntity<>(diseaseReport, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Get a report of the weight level over the years and for the current year", response = Weight.class, responseContainer = "List")
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
            String category = weighing.getCategory().toLowerCase(Locale.ROOT);
            Integer year = weighing.getExecutionYear();
            Long weight = weighing.getWeightSafely();
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
    @ApiOperation(value = "Get a report of the weight level over the years and for the current year by batch", response = Weight.class, responseContainer = "List")
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

        List<Batch> batches = bovineClientService.getAllBatches();

        Map<String, Weight> totalWeights = new HashMap<>();
        Map<String, Integer> totals = new HashMap<>();

        for (Batch batch : batches) {
            List<String> bovineTags = batch.getBovineTags();
            String batchName = batch.getBatchName();

            List<Bovine> bovines = bovineClientService.getAllBovinesFromList(bovineTags);
            for (Bovine bovine : bovines) {
                List<Weighing> weightings = activityClientService.getLastWeightingsForBovine(bovine.getTag());

                for (Weighing weighing : weightings) {
                    int year = weighing.getExecutionYear();
                    long weight = weighing.getWeightSafely();

                    String key = batchName + year;

                    int totalCount = totals.getOrDefault(key, 0);
                    Weight totalWeight = totalWeights.getOrDefault(key, new Weight(year, 0L, batchName));
                    totalCount++;
                    totals.put(key, totalCount);
                    totalWeight.setWeight(totalWeight.getWeight() + weight);
                    totalWeights.put(key, totalWeight);
                }
            }
        }

        YearlyReport batch = new YearlyReport();
        batch.setType("batch");
        batch.setDescription("Peso promedio anual por batch");
        batch.setReportedList(new ArrayList<>());

        for (String key : totalWeights.keySet()) {
            Weight weight = totalWeights.get(key);
            Integer total = totals.get(key);
            weight.setWeight(weight.getWeight() / total);
            batch.getReportedList().add(weight);
        }

        batch.filterByYear(yearFrom, yearTo);

        batch.getReportedList().sort(null);

        return new ResponseEntity<>(batch, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Get a report of the food consumed by bovines each year separated by category", response = FoodConsumption.class, responseContainer = "List")
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

        LOG.info("Request to create feed report");

        List<Feeding> feedings = activityClientService.getAllFeedings();

        Map<String, FoodConsumption> reports = new HashMap<>();

        for (Feeding feeding : feedings) {
            int year = feeding.getExecutionYear();
            long foodEaten = feeding.getAmountSafely();
            String tag = feeding.getBovineTag();

            Bovine bovine = bovineClientService.getBovine(tag);
            if (bovine != null) {
                String category = bovine.getCategory();
                String key = category + year;

                FoodConsumption foodConsumption = reports.containsKey(key) ? reports.get(key)
                        : new FoodConsumption(year, 0L, category);
                foodConsumption.setFoodEaten(foodConsumption.getFoodEaten() + foodEaten);
                reports.put(key, foodConsumption);
            }
        }

        YearlyReport foodConsumption = YearlyReport.builder()
                .type("foodConsumption")
                .description("Alimento consumido anualmente por categoria")
                .build();

        foodConsumption.setReportedList(new ArrayList<>(reports.values()));

        foodConsumption.filterByYear(yearFrom, yearTo);

        foodConsumption.getReportedList().sort(null);

        return new ResponseEntity<>(foodConsumption, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Get a report of the cost by live kilogram", response = LiveCost.class, responseContainer = "List")
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

        List<Weighing> weighingList = activityClientService.getAllWeightings();
        List<Feeding> feedingList = activityClientService.getAllFeedings();

        Map<String, Long> weightByYear = new HashMap<>();
        Map<String, Long> spendingByYear = new HashMap<>();

        for (Weighing weighing : weighingList) {
            String yearKey = String.valueOf(weighing.getExecutionYear());

            long weight = weightByYear.getOrDefault(yearKey, 0L);
            weight += weighing.getWeightSafely();
            weightByYear.put(yearKey, weight);
        }

        for (Feeding feeding : feedingList) {
            String yearKey = String.valueOf(feeding.getExecutionYear());
            String foodName = feeding.getFood();
            long foodAmount = feeding.getAmountSafely();
            Supply supply = economicClientService.getSupply(foodName);
            long price = supply.getPrice();

            long spending = spendingByYear.getOrDefault(yearKey, 0L);
            spending += foodAmount * price;
            spendingByYear.put(yearKey, spending);
        }

        YearlyReport liveCost = new YearlyReport();
        liveCost.setType("liveCost");
        liveCost.setDescription("Rendimiento anual de la comida por kilogramo vivo");
        liveCost.setReportedList(new ArrayList<>());

        for (String key : weightByYear.keySet()) {
            long weight = weightByYear.getOrDefault(key, 0L);
            long spending = spendingByYear.getOrDefault(key, 0L);
            double costXKg = weight != 0 ? (double) spending / weight : -1;
            LiveCost report = new LiveCost(Integer.valueOf(key), weight, spending, round(costXKg, 3));
            liveCost.getReportedList().add(report);
        }

        liveCost.filterByYear(yearFrom, yearTo);

        liveCost.getReportedList().sort(null);

        return new ResponseEntity<>(liveCost, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Get a report of the live animals for each year", response = Live.class, responseContainer = "List")
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

        List<Bovine> bovines = bovineClientService.getAllBovines().stream().filter(this::isInCorrectState).collect(Collectors.toList());

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
    @ApiOperation(value = "Get a report of the income versus spending by year", response = Income.class, responseContainer = "List")
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

        List<SupplyOperation> supplyOperations = economicClientService.getAllSupplyOperations();
        List<BovineOperation> bovineOperations = economicClientService.getAllBovineOperations();

        Map<String, Long> spendingByYear = new HashMap<>();
        Map<String, Long> incomeByYear = new HashMap<>();

        for (SupplyOperation supplyOperation : supplyOperations) {
            String yearKey = String.valueOf(supplyOperation.getTransactionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().getYear());
            String supplyName = supplyOperation.getSupplyName();
            Supply supply = economicClientService.getSupply(supplyName);

            String type = supplyOperation.getOperationType();
            long amount = supplyOperation.getAmount();
            long price = supply.getPrice();
            long cost = amount * price;
            if (OperationType.BUY.equalsIgnoreCase(type)) {
                long totalByYear = spendingByYear.getOrDefault(yearKey, 0L);
                totalByYear += cost;
                spendingByYear.put(yearKey, totalByYear);
            }
        }

        for (BovineOperation bovineOperation : bovineOperations) {
            long amount = bovineOperation.getAmount();
            String type = bovineOperation.getOperationType();
            String yearKey = String.valueOf(bovineOperation.getTransactionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().getYear());

            if (OperationType.BUY.equalsIgnoreCase(type)) {
                long totalByYear = spendingByYear.getOrDefault(yearKey, 0L);
                totalByYear += amount;
                spendingByYear.put(yearKey, totalByYear);
            } else if (OperationType.SELL.equalsIgnoreCase(type)) {
                long totalByYear = incomeByYear.getOrDefault(yearKey, 0L);
                totalByYear += amount;
                incomeByYear.put(yearKey, totalByYear);
            }
        }

        YearlyReport income = new YearlyReport();
        income.setType("income");
        income.setDescription("Gastos versus ingresos por año");
        income.setReportedList(new ArrayList<>());

        for (String key : incomeByYear.keySet()) {
            long incomeAmount = incomeByYear.get(key);
            long spendingAmount = spendingByYear.get(key);
            Income incomeReport = new Income(Integer.valueOf(key), incomeAmount, spendingAmount);
            income.getReportedList().add(incomeReport);
        }

        income.filterByYear(yearFrom, yearTo);

        income.getReportedList().sort(null);

        return new ResponseEntity<>(income, HttpStatus.OK);
    }

    private boolean isInCorrectState(Bovine bovine) {
        return !BOVINE_UNUSABLE_STATES.contains(bovine.getStatus().toLowerCase(Locale.ROOT));
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}