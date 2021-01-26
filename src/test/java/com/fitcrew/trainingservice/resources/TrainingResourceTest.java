package com.fitcrew.trainingservice.resources;

import com.fitcrew.FitCrewAppConstant.message.type.RoleType;
import com.fitcrew.FitCrewAppModel.domain.dto.TrainingDto;
import com.fitcrew.FitCrewAppModel.domain.model.TrainingModel;
import com.fitcrew.trainingservice.AbstractIntegrationTest;
import com.fitcrew.trainingservice.services.cache.TrainingModelCache;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import static com.fitcrew.trainingservice.util.TrainingUtil.TRAINER_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class TrainingResourceTest extends AbstractIntegrationTest {

    @MockBean
    private TrainingModelCache trainingModelCache;

    @Test
    void shouldGetTrainings() {
        var email = getEmail(1, TRAINER_EMAIL);
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/training/trainings")
                        .queryParam("trainer-email", email)
                        .build())
                .header(AUTHORIZATION_HEADER, getToken(RoleType.ROLE_TRAINER, TRAINER_EMAIL))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TrainingModel.class)
                .hasSize(1);
    }

    @Test
    void shouldCreateTraining() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/training")
                        .build())
                .header(AUTHORIZATION_HEADER, getToken(RoleType.ROLE_TRAINER, TRAINER_EMAIL))
                .body(Mono.just(trainingDto), TrainingDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TrainingModel.class)
                .value(trainingModel -> checkTrainingModelAssertions(trainingModel, false));
    }

    @Test
    void shouldDeleteTraining() {
        var email = getEmail(1, TRAINER_EMAIL);
        doNothing().when(trainingModelCache)
                .delete(anyString());

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/training")
                        .queryParam("trainer-email", email)
                        .queryParam("training-name", trainingName)
                        .build())
                .header(AUTHORIZATION_HEADER, getToken(RoleType.ROLE_TRAINER, TRAINER_EMAIL))
                .exchange()
                .expectStatus().isOk()
                .expectBody(TrainingModel.class)
                .value(trainingModel -> checkTrainingModelAssertions(trainingModel, true));
    }

    @Test
    void shouldUpdateTraining() {
        var email = getEmail(100, TRAINER_EMAIL);
        trainingDto.setTrainerEmail(email);
        doNothing().when(trainingModelCache)
                .put(anyString(), any(), anyLong());

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/training")
                        .queryParam("training-name", trainingName)
                        .build())
                .header(AUTHORIZATION_HEADER, getToken(RoleType.ROLE_TRAINER, TRAINER_EMAIL))
                .body(Mono.just(trainingDto), TrainingDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TrainingModel.class)
                .value(trainingModel -> assertEquals(email, trainingModel.getTrainerEmail()));
    }

    @Test
    void shouldSelectTraining() {
        var email = getEmail(1, TRAINER_EMAIL);
        when(trainingModelCache.get(anyString()))
                .thenReturn(Mono.empty());
        doNothing().when(trainingModelCache)
                .put(anyString(), any(), anyLong());

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/training/select")
                        .queryParam("trainer-email", email)
                        .queryParam("training-name", trainingName)
                        .build())
                .header(AUTHORIZATION_HEADER, getToken(RoleType.ROLE_TRAINER, TRAINER_EMAIL))
                .exchange()
                .expectStatus().isOk()
                .expectBody(TrainingModel.class)
                .value(trainingModel -> checkTrainingModelAssertions(trainingModel, true));
    }

    @Test
    void shouldGetClientsWhoBoughtTraining() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/training/client-bought-training")
                        .queryParam("training-name", trainingName)
                        .build())
                .header(AUTHORIZATION_HEADER, getToken(RoleType.ROLE_TRAINER, TRAINER_EMAIL))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(String.class)
                .hasSize(1);
    }

    @Test
    void shouldGetTrainingsBoughtByClient() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/training/bought-by-client")
                        .queryParam("client-name", CLIENT_NAME)
                        .build())
                .header(AUTHORIZATION_HEADER, getToken(RoleType.ROLE_CLIENT, CLIENT_EMAIL))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(String.class)
                .hasSize(1);
    }
}