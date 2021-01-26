package com.fitcrew.trainingservice.services;

import com.fitcrew.FitCrewAppModel.domain.dto.TrainingDto;
import com.fitcrew.FitCrewAppModel.domain.model.TrainingModel;
import com.fitcrew.trainingservice.core.converter.TrainingConverter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Component
public class TrainingServiceFacadeImpl implements TrainingServiceFacade {

    private final TrainingService trainingService;
    private final TrainingConverter trainingConverter;

    public TrainingServiceFacadeImpl(TrainingService trainingService,
                                     TrainingConverter trainingConverter) {
        this.trainingService = trainingService;
        this.trainingConverter = trainingConverter;
    }


    @Override
    public Mono<List<TrainingModel>> getTrainerTrainings(String trainerEmail) {
        return trainingService.getTrainerTrainings(trainerEmail);
    }

    @Override
    public Mono<TrainingModel> createTraining(TrainingDto trainingDto) {
        return trainingService.createTraining(trainingDto)
                .filter(trainingDocument -> Objects.nonNull(trainingDocument.getTrainingName()))
                .map(trainingConverter::trainingDocumentToTrainingModel);
    }

    @Override
    public Mono<TrainingModel> deleteTraining(String trainingName,
                                              String trainerEmail) {
        return trainingService.deleteTraining(trainingName, trainerEmail);
    }

    @Override
    public Mono<TrainingModel> updateTraining(TrainingDto trainingDto,
                                              String trainingName) {
        return trainingService.updateTraining(trainingDto, trainingName);
    }

    @Override
    public Mono<TrainingModel> selectTraining(String trainerEmail,
                                              String trainingName) {
        return trainingService.selectTraining(trainerEmail, trainingName);
    }

    @Override
    public Mono<List<String>> clientsWhoBoughtTraining(String trainingName) {
        return trainingService.clientsWhoBoughtTraining(trainingName);
    }

    @Override
    public Mono<List<String>> getAllTrainingsBoughtByClient(String clientName) {
        return trainingService.getAllTrainingsBoughtByClient(clientName);
    }
}
