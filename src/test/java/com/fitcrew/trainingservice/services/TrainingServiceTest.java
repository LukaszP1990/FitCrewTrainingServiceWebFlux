package com.fitcrew.trainingservice.services;

import com.fitcrew.FitCrewAppModel.domain.model.TrainingModel;
import com.fitcrew.trainingservice.core.converter.TrainingConverter;
import com.fitcrew.trainingservice.core.converter.TrainingConverterImpl;
import com.fitcrew.trainingservice.dao.TrainingRepository;
import com.fitcrew.trainingservice.domains.TrainingDocument;
import com.fitcrew.trainingservice.dto.TrainingDto;
import com.fitcrew.trainingservice.services.cache.TrainingModelCache;
import com.fitcrew.trainingservice.util.TrainingUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

import static com.fitcrew.trainingservice.util.TrainingUtil.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TrainingServiceTest {

    private static final String CLIENT_NAME = "client";
    private static final TrainingDto trainingDto = TrainingUtil.getTrainingDto();
    private static final List<TrainingDocument> trainingDocuments = TrainingUtil.getTrainingDocuments();
    private static final TrainingDocument trainingDocument = TrainingUtil.getTrainingDocument(1);
    private static final TrainingDocument trainingDocument2 = TrainingUtil.getTrainingDocument(2);
    private static final TrainingModel trainingModel = TrainingUtil.getTrainingModel();

    private final TrainingRepository trainingRepository = Mockito.mock(TrainingRepository.class);
    private final TrainingConverter trainingConverter = new TrainingConverterImpl();
    private final TrainingModelCache trainingModelCache = Mockito.mock(TrainingModelCache.class);
    private final TrainingService trainingService = new TrainingService(trainingRepository, trainingConverter, trainingModelCache);

    @Test
    void shouldGetTrainerTrainings() {
        when(trainingRepository.findByTrainerEmail(anyString()))
                .thenReturn(Flux.fromIterable(trainingDocuments));
        doNothing()
                .when(trainingModelCache).put(anyString(), any(), anyLong());

        StepVerifier.create(trainingService.getTrainerTrainings(TRAINER_EMAIL))
                .expectSubscription()
                .expectNextMatches(trainings -> 3 == trainings.size())
                .verifyComplete();
    }

    @Test
    void shouldNotGetTrainerTrainings() {
        when(trainingRepository.findByTrainerEmail(anyString()))
                .thenReturn(Flux.empty());
        doNothing()
                .when(trainingModelCache).put(anyString(), any(), anyLong());

        StepVerifier.create(trainingService.getTrainerTrainings(TRAINER_EMAIL))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void shouldCreateTraining() {
        when(trainingRepository.findByTrainingNameAndTrainerEmail(anyString(), anyString()))
                .thenReturn(Mono.empty());
        when(trainingRepository.save(any()))
                .thenReturn(Mono.just(trainingDocument));

        StepVerifier.create(trainingService.createTraining(trainingDto))
                .expectSubscription()
                .expectNextMatches(this::checkTrainingDocumentAssertions)
                .verifyComplete();
    }

    @Test
    void shouldNotCreateTrainingWhen() {
        when(trainingRepository.findByTrainingNameAndTrainerEmail(anyString(), anyString()))
                .thenReturn(Mono.just(trainingDocument));

        StepVerifier.create(trainingService.createTraining(trainingDto))
                .expectSubscription()
                .expectNextMatches(training -> Objects.isNull(training.getClients()))
                .verifyComplete();
    }

    @Test
    void shouldDeleteTraining() {
        when(trainingRepository.findByTrainingNameAndTrainerEmail(anyString(), anyString()))
                .thenReturn(Mono.just(trainingDocument));
        doNothing()
                .when(trainingModelCache).delete(anyString());

        StepVerifier.create(trainingService.deleteTraining(TRAINING_NAME, TRAINER_EMAIL))
                .expectSubscription()
                .expectNextMatches(this::checkTrainingModelAssertions)
                .verifyComplete();
    }

    @Test
    void shouldNotDeleteTraining() {
        when(trainingRepository.findByTrainingNameAndTrainerEmail(anyString(), anyString()))
                .thenReturn(Mono.empty());
        doNothing()
                .when(trainingModelCache).delete(anyString());

        StepVerifier.create(trainingService.deleteTraining(TRAINING_NAME, TRAINER_EMAIL))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void shouldUpdateTraining() {
        when(trainingRepository.findByTrainingName(anyString()))
                .thenReturn(Mono.just(trainingDocument));
        when(trainingRepository.save(any()))
                .thenReturn(Mono.just(trainingDocument));
        doNothing()
                .when(trainingModelCache).put(anyString(), any(), anyLong());

        StepVerifier.create(trainingService.updateTraining(trainingDto, TRAINING_NAME))
                .expectSubscription()
                .expectNextMatches(trainingModel -> TRAINER_EMAIL.equals(trainingModel.getTrainerEmail()))
                .verifyComplete();
    }

    @Test
    void shouldNotUpdateTraining() {
        when(trainingRepository.findByTrainingName(anyString()))
                .thenReturn(Mono.empty());
        doNothing()
                .when(trainingModelCache).put(anyString(), any(), anyLong());

        StepVerifier.create(trainingService.updateTraining(trainingDto, TRAINING_NAME))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void shouldSelectTrainingFromCache() {
        when(trainingModelCache.get(anyString()))
                .thenReturn(Mono.just(trainingModel));

        StepVerifier.create(trainingService.selectTraining(TRAINER_EMAIL, TRAINING_NAME))
                .expectSubscription()
                .expectNextMatches(trainingModel -> TRAINER_EMAIL.equals(trainingModel.getTrainerEmail()))
                .verifyComplete();
    }

    @Test
    void shouldSelectTrainingFromDatabase() {
        when(trainingModelCache.get(anyString()))
                .thenReturn(Mono.empty());
        when(trainingRepository.findByTrainingNameAndTrainerEmail(anyString(), anyString()))
                .thenReturn(Mono.just(trainingDocument));
        doNothing()
                .when(trainingModelCache).put(anyString(), any(), anyLong());

        StepVerifier.create(trainingService.selectTraining(TRAINER_EMAIL, TRAINING_NAME))
                .expectSubscription()
                .expectNextMatches(this::checkTrainingModelAssertions)
                .verifyComplete();
    }

    @Test
    void shouldNotSelectTrainingWhenItsIsNotInCacheAndDatabase() {
        when(trainingModelCache.get(anyString()))
                .thenReturn(Mono.empty());
        when(trainingRepository.findByTrainingNameAndTrainerEmail(anyString(), anyString()))
                .thenReturn(Mono.empty());
        doNothing()
                .when(trainingModelCache).put(anyString(), any(), anyLong());

        StepVerifier.create(trainingService.selectTraining(TRAINER_EMAIL, TRAINING_NAME))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void shouldGetClientsWhoBoughtTraining() {
        when(trainingRepository.findByTrainingName(anyString()))
                .thenReturn(Mono.just(trainingDocument));

        StepVerifier.create(trainingService.clientsWhoBoughtTraining(TRAINING_NAME))
                .expectSubscription()
                .expectNextMatches(clients -> 1 == clients.size())
                .verifyComplete();
    }

    @Test
    void shouldNotGetClientsWhoBoughtTraining() {
        when(trainingRepository.findByTrainingName(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(trainingService.clientsWhoBoughtTraining(TRAINING_NAME))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void shouldGetAllTrainingsBoughtByClient() {
        when(trainingRepository.findAll())
                .thenReturn(Flux.fromIterable(trainingDocuments));

        StepVerifier.create(trainingService.getAllTrainingsBoughtByClient(CLIENT_NAME))
                .expectSubscription()
                .expectNextMatches(clients -> 3 == clients.size())
                .verifyComplete();
    }

    @Test
    void shouldNotGetAllTrainingsBoughtByClient() {
        when(trainingRepository.findAll())
                .thenReturn(Flux.empty());

        StepVerifier.create(trainingService.getAllTrainingsBoughtByClient(CLIENT_NAME))
                .expectSubscription()
                .verifyComplete();
    }

    private boolean checkTrainingModelAssertions(TrainingModel trainingModel) {
        return 1 == trainingModel.getClients().size() &&
                String.valueOf(1).concat(TRAINER_EMAIL).equals(trainingModel.getTrainerEmail()) &&
                DESCRIPTION.equals(trainingModel.getDescription()) &&
                TRAINING.equals(trainingModel.getTraining()) &&
                String.valueOf(1).concat(TRAINING_NAME).equals(trainingModel.getTrainingName());
    }

    private boolean checkTrainingDocumentAssertions(TrainingDocument trainingDocument) {
        return String.valueOf(1).equals(trainingDocument.getId()) &&
                TRAINER_EMAIL.equals(trainingDocument.getTrainerEmail()) &&
                DESCRIPTION.equals(trainingDocument.getDescription()) &&
                TRAINING.equals(trainingDocument.getTraining()) &&
                TRAINING_NAME.equals(trainingDocument.getTrainingName()) &&
                1 == trainingDocument.getClients().size();
    }
}