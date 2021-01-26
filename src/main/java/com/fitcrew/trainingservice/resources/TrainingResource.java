package com.fitcrew.trainingservice.resources;

import com.fitcrew.FitCrewAppModel.domain.dto.TrainingDto;
import com.fitcrew.FitCrewAppModel.domain.model.TrainingModel;
import com.fitcrew.trainingservice.services.TrainingServiceFacade;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/training")
class TrainingResource {

    private final TrainingServiceFacade trainingServiceFacade;

    public TrainingResource(TrainingServiceFacade trainingServiceFacade) {
        this.trainingServiceFacade = trainingServiceFacade;
    }

    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping(value = "/trainings", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<TrainingModel>> getTrainings(@RequestParam(name = "trainer-email") String trainerEmail) {
        return trainingServiceFacade.getTrainerTrainings(trainerEmail);
    }

    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TrainingModel> createTraining(@RequestBody TrainingDto trainingDto) {
        return trainingServiceFacade.createTraining(trainingDto);
    }

    @PreAuthorize("hasRole('TRAINER')")
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TrainingModel> deleteTraining(@RequestParam(name = "trainer-email") String trainerEmail,
                                              @RequestParam(name = "training-name") String trainingName) {
        return trainingServiceFacade.deleteTraining(trainingName, trainerEmail);
    }

    @PreAuthorize("hasRole('TRAINER')")
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TrainingModel> updateTraining(@RequestBody TrainingDto trainingDto,
                                              @RequestParam(name = "training-name") String trainerEmail) {
        return trainingServiceFacade.updateTraining(trainingDto, trainerEmail);
    }

    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping(value = "/select", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TrainingModel> selectTraining(@RequestParam(name = "trainer-email") String trainerEmail,
                                              @RequestParam(name = "training-name") String trainingName) {
        return trainingServiceFacade.selectTraining(trainerEmail, trainingName);

    }

    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping(value = "/client-bought-training", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<String>> clientsWhoBoughtTraining(@RequestParam(name = "training-name") String trainingName) {
        return trainingServiceFacade.clientsWhoBoughtTraining(trainingName);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping(value = "/bought-by-client", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<String>> trainingsBoughtByClient(@RequestParam(name = "client-name") String clientName) {
        return trainingServiceFacade.getAllTrainingsBoughtByClient(clientName);
    }
}
