package com.fitcrew.trainingservice.services;

import java.util.List;

import com.fitcrew.FitCrewAppModel.domain.model.TrainingModel;
import com.fitcrew.trainingservice.dto.TrainingDto;

import reactor.core.publisher.Mono;

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
