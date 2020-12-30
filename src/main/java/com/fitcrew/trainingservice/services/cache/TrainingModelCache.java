package com.fitcrew.trainingservice.services.cache;

import java.time.Duration;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;

import com.fitcrew.FitCrewAppModel.domain.model.TrainingModel;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class TrainingModelCache {

	private final ReactiveRedisConnectionFactory factory;
	private final ReactiveRedisOperations<String, TrainingModel> trainingOps;

	public TrainingModelCache(ReactiveRedisConnectionFactory factory,
							  ReactiveRedisOperations<String, TrainingModel> trainingOps) {
		this.factory = factory;
		this.trainingOps = trainingOps;
	}

	public void put(String key,
					TrainingModel trainingModel,
					long hour) {
		trainingOps.opsForValue().set(key, trainingModel, Duration.ofHours(hour))
				.subscribe(result -> log.info("Add trainerModel: {} to cache", trainingModel));
	}

	public Mono<TrainingModel> get(String key) {
		log.info("Get trainingModel by key: {} from cache", key);
		return trainingOps.opsForValue().get(key);
	}

	public void delete(String key) {
		trainingOps.opsForValue().delete(key)
				.subscribe(result -> log.info("Delete trainingModel by key: {} from cache", key));
	}
}
