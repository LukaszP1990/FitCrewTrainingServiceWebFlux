package com.fitcrew.trainingservice.dao;

import com.fitcrew.trainingservice.domains.TrainingDocument;
import com.fitcrew.trainingservice.util.TrainingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static com.fitcrew.trainingservice.util.TrainingUtil.*;

@DataMongoTest
@ExtendWith(value = MockitoExtension.class)
@ActiveProfiles("test")
class TrainingRepositoryTest {

    private static final String email = String.valueOf(1).concat(TRAINER_EMAIL);
    private static final String trainingName = String.valueOf(1).concat(TRAINING_NAME);

    @Autowired
    private TrainingRepository trainingRepository;

    @BeforeEach
    void setUp() {
        trainingRepository.deleteAll().thenMany(Flux.fromIterable(TrainingUtil.getTrainingDocuments()))
                .flatMap(trainingDocument -> trainingRepository.save(trainingDocument))
                .doOnNext(ratingTrainerDocument -> System.out.println("Inserted training: " + ratingTrainerDocument))
                .blockLast();
    }

    @Test
    void shouldFindByTrainerEmail() {
        StepVerifier.create(trainingRepository.findByTrainerEmail(email))
                .expectSubscription()
                .expectNextMatches(this::checkTrainingDocumentAssertions)
                .verifyComplete();
    }

    @Test
    void shouldNotFindByTrainerEmail() {
        StepVerifier.create(trainingRepository.findByTrainerEmail(TRAINER_EMAIL))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void shouldFindByTrainingName() {
        StepVerifier.create(trainingRepository.findByTrainingName(trainingName))
                .expectSubscription()
                .expectNextMatches(this::checkTrainingDocumentAssertions)
                .verifyComplete();
    }

    @Test
    void shouldNotFindByTrainingName() {
        StepVerifier.create(trainingRepository.findByTrainingName(TRAINING_NAME))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void shouldFindByTrainingNameAndTrainerEmail() {
        StepVerifier.create(trainingRepository.findByTrainingNameAndTrainerEmail(trainingName, email))
                .expectSubscription()
                .expectNextMatches(this::checkTrainingDocumentAssertions)
                .verifyComplete();
    }

    @Test
    void shouldNotFindByTrainingNameAndTrainerEmail() {
        StepVerifier.create(trainingRepository.findByTrainingNameAndTrainerEmail(TRAINING_NAME, email))
                .expectSubscription()
                .verifyComplete();
    }

    private boolean checkTrainingDocumentAssertions(TrainingDocument trainingDocument) {
        return 1 == trainingDocument.getClients().size() &&
                String.valueOf(1).concat(TRAINER_EMAIL).equals(trainingDocument.getTrainerEmail()) &&
                DESCRIPTION.equals(trainingDocument.getDescription()) &&
                TRAINING.equals(trainingDocument.getTraining()) &&
                String.valueOf(1).concat(TRAINING_NAME).equals(trainingDocument.getTrainingName());
    }
}