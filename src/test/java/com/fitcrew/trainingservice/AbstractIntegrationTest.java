package com.fitcrew.trainingservice;

import com.fitcrew.FitCrewAppConstant.message.type.RoleType;
import com.fitcrew.FitCrewAppModel.domain.model.TrainingModel;
import com.fitcrew.trainingservice.dao.TrainingRepository;
import com.fitcrew.trainingservice.domains.TrainingDocument;
import com.fitcrew.trainingservice.dto.TrainingDto;
import com.fitcrew.trainingservice.util.JwtUtil;
import com.fitcrew.trainingservice.util.TrainingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static com.fitcrew.trainingservice.util.TrainingUtil.*;
import static com.fitcrew.trainingservice.util.TrainingUtil.TRAINING_NAME;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TrainingServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = "200000000")
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {
    public static final String trainingName = String.valueOf(1).concat(TRAINING_NAME);
    public static final String CLIENT_EMAIL = "mockedClient@gmail.com";
    public static final String CLIENT_NAME = "client";
    public static final TrainingDto trainingDto = TrainingUtil.getTrainingDto();
    protected static final String AUTHORIZATION_HEADER = "Authorization";
    protected static final String SECRET = "ThisIsSecretForJWTHS256SignatureAlgorithmThatMUSTHave64ByteLength";
    private static final String PASSWORD = "rmkFOU0k/LsmSG0CrVmqk+9BitPoqVAavuH1+8mreh0=";
    @Autowired
    public WebTestClient webTestClient;

    @Autowired
    public TrainingRepository trainingRepository;

    @BeforeEach
    void setUp() {
        trainingRepository.deleteAll().thenMany(Flux.fromIterable(TrainingUtil.getTrainingDocuments()))
                .flatMap(trainingDocument -> trainingRepository.save(trainingDocument))
                .doOnNext(trainingDocument -> System.out.println("Inserted training: " + trainingDocument))
                .blockLast();
    }

    public String getEmail(int i,
                           String trainerEmail) {
        return String.valueOf(i).concat(trainerEmail);
    }

    public String getToken(RoleType roleType,
                           String email) {
        return JwtUtil.createToken(SECRET, roleType, email);
    }

    public void checkTrainingModelAssertions(TrainingModel trainingModel,
                                             boolean concatName) {
        assertAll(() -> {
            assertEquals(1, trainingModel.getClients().size());
            assertEquals(DESCRIPTION, trainingModel.getDescription());
            assertEquals(TRAINING, trainingModel.getTraining());
            if(concatName) {
                assertEquals(String.valueOf(1).concat(TRAINING_NAME), trainingModel.getTrainingName());
                assertEquals(String.valueOf(1).concat(TRAINER_EMAIL), trainingModel.getTrainerEmail());
            }
            else {
                assertEquals(TRAINING_NAME, trainingModel.getTrainingName());
            }
        });
    }
}
