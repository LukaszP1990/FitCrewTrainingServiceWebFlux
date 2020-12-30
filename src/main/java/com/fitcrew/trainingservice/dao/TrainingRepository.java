package com.fitcrew.trainingservice.dao;

import com.fitcrew.trainingservice.domains.TrainingDocument;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TrainingRepository extends ReactiveMongoRepository<TrainingDocument, String> {
	Flux<TrainingDocument> findByTrainerEmail(String trainerEmail);

	Mono<TrainingDocument> findByTrainingName(String trainingName);

	Mono<TrainingDocument> findByTrainingNameAndTrainerEmail(String trainingName,
															 String trainerEmail);
}
