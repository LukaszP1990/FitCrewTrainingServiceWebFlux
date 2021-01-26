package com.fitcrew.trainingservice.services;

import com.fitcrew.FitCrewAppModel.domain.dto.TrainingDto;
import com.fitcrew.FitCrewAppModel.domain.model.TrainingModel;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TrainingServiceFacade {
	Mono<List<TrainingModel>> getTrainerTrainings(String trainerEmail);

	Mono<TrainingModel> createTraining(TrainingDto trainingDto);

	Mono<TrainingModel> deleteTraining(String trainingName,
									   String trainerEmail);

	Mono<TrainingModel> updateTraining(TrainingDto trainingDto,
									   String trainingName);

	Mono<TrainingModel> selectTraining(String trainerEmail,
									   String trainingName);

	Mono<List<String>> clientsWhoBoughtTraining(String trainingName);

	Mono<List<String>> getAllTrainingsBoughtByClient(String clientName);
}
