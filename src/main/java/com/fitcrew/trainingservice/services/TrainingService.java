package com.fitcrew.trainingservice.services;

import com.fitcrew.FitCrewAppModel.domain.model.TrainingModel;
import com.fitcrew.trainingservice.core.converter.TrainingConverter;
import com.fitcrew.trainingservice.core.util.TrainingDocumentUtil;
import com.fitcrew.trainingservice.dao.TrainingRepository;
import com.fitcrew.trainingservice.domains.TrainingDocument;
import com.fitcrew.trainingservice.dto.TrainingDto;
import com.fitcrew.trainingservice.services.cache.TrainingModelCache;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
class TrainingService {

	private final TrainingRepository trainingRepository;
	private final TrainingConverter trainingConverter;
	private final TrainingModelCache trainingModelCache;

	TrainingService(final TrainingRepository trainingRepository,
					final TrainingConverter trainingConverter,
					final TrainingModelCache trainingModelCache) {
		this.trainingRepository = trainingRepository;
		this.trainingConverter = trainingConverter;
		this.trainingModelCache = trainingModelCache;
	}

	Mono<List<TrainingModel>> getTrainerTrainings(String trainerEmail) {
		log.info("Trainer training by trainer email address: {}", trainerEmail);
		return Try.of(() -> trainerEmail)
				.filter(Objects::nonNull)
				.map(trainingRepository::findByTrainerEmail)
				.map(trainingDocumentFlux -> trainingDocumentFlux
						.collectList()
						.filter(trainingDocuments -> !trainingDocuments.isEmpty())
						.map(this::getTrainingModels))
				.getOrNull();
	}

	Mono<TrainingDocument> createTraining(TrainingDto trainingDto) {
		log.info("Create training: {}", trainingDto);
		return Try.of(() -> trainingDto)
				.map(this::getTrainingDocument)
				.getOrNull();
	}

	Mono<TrainingModel> deleteTraining(String trainingName,
									   String trainerEmail) {
		log.info("Delete training by training name: {} \n trainer email address: {}", trainerEmail, trainingName);
		var trainingNameTrainerEmailTuple = Tuple.of(trainingName, trainerEmail);
		return Mono.justOrEmpty(trainingNameTrainerEmailTuple)
				.filter(tuple -> Objects.nonNull(tuple._1) && Objects.nonNull(tuple._2))
				.flatMap(tuple -> deleteTraining(trainingName, tuple));
	}

	Mono<TrainingModel> updateTraining(TrainingDto trainingDto,
									   String trainingName) {
		log.info("Update training: {} \n by training name: {}", trainingDto, trainingName);
		return Mono.justOrEmpty(trainingName)
				.filter(email -> Objects.nonNull(trainingName))
				.flatMap(email -> updateTrainingDocument(trainingDto, trainingName));
	}

	Mono<TrainingModel> selectTraining(String trainerEmail,
									   String trainingName) {
		log.info("Select training by training name: {} \n trainer email address: {}", trainerEmail, trainingName);
		var trainerEmailTrainingNameTuple = Tuple.of(trainerEmail, trainingName);
		return Mono.justOrEmpty(trainerEmailTrainingNameTuple)
				.filter(tuple -> Objects.nonNull(tuple._1) && Objects.nonNull(tuple._2))
				.flatMap(this::getTrainingModel);
	}

	Mono<List<String>> clientsWhoBoughtTraining(String trainingName) {
		log.info("Clients who bought training by training name: {}", trainingName);
		return Try.of(() -> trainingName)
				.filter(Objects::nonNull)
				.map(trainingRepository::findByTrainingName)
				.map(trainingDocument -> trainingDocument
						.filter(Objects::nonNull)
						.map(TrainingDocument::getClients))
				.getOrNull();
	}

	Mono<List<String>> getAllTrainingsBoughtByClient(String clientName) {
		log.info("Trainings bought by client: {}", clientName);
		return Try.of(() -> clientName)
				.filter(Objects::nonNull)
				.map(this::getTrainingsBoughtByClient)
				.map(trainingsMono -> trainingsMono
						.filter(trainings-> !trainings.isEmpty())
						.map(trainings -> trainings))
				.getOrNull();
	}

	private List<TrainingModel> getTrainingModels(List<TrainingDocument> training) {
		return training.stream()
				.map(trainingConverter::trainingDocumentToTrainingModel)
				.collect(Collectors.toList());
	}

	private Mono<TrainingModel> getTrainingModel(Tuple2<String, String> trainerEmailTrainingNameTuple) {
		return trainingModelCache.get(trainerEmailTrainingNameTuple._2)
				.switchIfEmpty(Mono.defer(() -> getTrainingModelFromDb(trainerEmailTrainingNameTuple)));
	}

	private Mono<TrainingModel> getTrainingModelFromDb(Tuple2<String, String> trainerEmailTrainingNameTuple) {
		return trainingRepository.findByTrainingNameAndTrainerEmail(trainerEmailTrainingNameTuple._2, trainerEmailTrainingNameTuple._1)
				.filter(Objects::nonNull)
				.map(trainingConverter::trainingDocumentToTrainingModel)
				.doOnSuccess(trainingModel -> trainingModelCache.put(trainerEmailTrainingNameTuple._2, trainingModel, 5));
	}

	private Mono<TrainingDocument> getTrainingDocument(TrainingDto trainingDto) {
		return trainingRepository.findByTrainingNameAndTrainerEmail(trainingDto.getTrainingName(), trainingDto.getTrainerEmail())
				.map(trainingDocument -> TrainingDocument.builder().build())
				.switchIfEmpty(Mono.defer(() -> save(trainingDto)));
	}

	private Mono<TrainingDocument> save(TrainingDto trainingDto) {
		return trainingRepository.save(trainingConverter.trainingDtoToTrainingDocument(trainingDto));
	}

	private Mono<TrainingModel> deleteTraining(String trainingName,
											   Tuple2<String, String> tuple) {
		return trainingRepository.findByTrainingNameAndTrainerEmail(tuple._1, tuple._2)
				.filter(Objects::nonNull)
				.flatMap(this::prepareSuccessfulTrainingDeleting)
				.map(trainingConverter::trainingDocumentToTrainingModel)
				.doOnSuccess(trainingModel -> trainingModelCache.delete(trainingName));
	}

	private Mono<TrainingDocument> prepareSuccessfulTrainingDeleting(TrainingDocument trainingDocument) {
		trainingRepository.delete(trainingDocument);
		return Mono.just(trainingDocument);
	}

	private Mono<TrainingModel> updateTrainingDocument(TrainingDto trainingDto,
													   String trainingName) {
		return trainingRepository.findByTrainingName(trainingName)
				.filter(Objects::nonNull)
				.map(trainingDocument -> TrainingDocumentUtil.getUpdatedTrainingDocument(trainingDto, trainingDocument))
				.flatMap(trainingRepository::save)
				.map(trainingConverter::trainingDocumentToTrainingModel)
				.doOnSuccess(trainingModel -> trainingModelCache.put(trainingName, trainingModel, 5));
	}

	private Mono<List<String>> getTrainingsBoughtByClient(String clientName) {
		return trainingRepository.findAll()
				.collectList()
				.filter(trainingDocuments -> !trainingDocuments.isEmpty())
				.map(trainingDocuments -> getTrainingDocuments(clientName, trainingDocuments));
	}

	private List<String> getTrainingDocuments(String clientName,
											  List<TrainingDocument> trainingDocuments) {
		return trainingDocuments.stream()
				.map(trainingDocument -> getTrainingByClientName(clientName, trainingDocument))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
	}

	private Optional<String> getTrainingByClientName(String clientName,
													 TrainingDocument trainingDocument) {
		return trainingDocument.getClients().stream()
				.filter(client -> client.equals(clientName))
				.findFirst();
	}
}
