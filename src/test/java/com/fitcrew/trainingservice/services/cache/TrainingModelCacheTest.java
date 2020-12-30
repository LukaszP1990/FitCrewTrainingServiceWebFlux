package com.fitcrew.trainingservice.services.cache;

import com.fitcrew.FitCrewAppModel.domain.model.TrainingModel;
import com.fitcrew.trainingservice.util.TrainingUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Objects;

import static com.fitcrew.trainingservice.util.TrainingUtil.TRAINING_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TrainingModelCacheTest {

    private static final TrainingModel trainingModel = TrainingUtil.getTrainingModel();

    @Mock
    private ReactiveRedisConnectionFactory factory;
    @Mock
    private ReactiveRedisOperations<String, TrainingModel> trainingOps;
    @Mock
    private ReactiveValueOperations<String, TrainingModel> reactiveValueOperations;

    @InjectMocks
    private TrainingModelCache trainingModelCache;

    @Test
    void put() {
        ReflectionTestUtils.setField(trainingModelCache, "factory", factory);
        ReflectionTestUtils.setField(trainingModelCache, "trainingOps", trainingOps);
        when(trainingOps.opsForValue())
                .thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.set(anyString(), any(), any()))
                .thenReturn(Mono.just(true));

        trainingModelCache.put(TRAINING_NAME, trainingModel, 1);

        verify(reactiveValueOperations, times(1)).set(TRAINING_NAME, trainingModel, Duration.ofHours(1));
    }

    @Test
    void get() {
        ReflectionTestUtils.setField(trainingModelCache, "factory", factory);
        ReflectionTestUtils.setField(trainingModelCache, "trainingOps", trainingOps);
        when(trainingOps.opsForValue())
                .thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.get(anyString()))
                .thenReturn(Mono.just(trainingModel));

        StepVerifier.create(trainingModelCache.get(TRAINING_NAME))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void delete() {
        ReflectionTestUtils.setField(trainingModelCache, "factory", factory);
        ReflectionTestUtils.setField(trainingModelCache, "trainingOps", trainingOps);
        when(trainingOps.opsForValue())
                .thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.delete(anyString()))
                .thenReturn(Mono.just(true));

        trainingModelCache.delete(TRAINING_NAME);

        verify(reactiveValueOperations, times(1)).delete(TRAINING_NAME);
    }
}