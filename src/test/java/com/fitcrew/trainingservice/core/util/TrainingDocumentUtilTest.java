package com.fitcrew.trainingservice.core.util;

import com.fitcrew.trainingservice.domains.TrainingDocument;
import com.fitcrew.trainingservice.dto.TrainingDto;
import com.fitcrew.trainingservice.util.TrainingUtil;
import org.junit.jupiter.api.Test;

import static com.fitcrew.trainingservice.util.TrainingUtil.*;
import static com.fitcrew.trainingservice.util.TrainingUtil.TRAINING_NAME;
import static org.junit.jupiter.api.Assertions.*;

class TrainingDocumentUtilTest {

    private static final TrainingDto trainingDto = TrainingUtil.getTrainingDto();
    private static final TrainingDocument trainingDocument = TrainingUtil.getTrainingDocument(1);

    @Test
    void shouldGetUpdatedTrainingDocument() {
        var updatedTrainingDocument = TrainingDocumentUtil.getUpdatedTrainingDocument(trainingDto, trainingDocument);
        assertNotNull(updatedTrainingDocument);
        assertAll(() -> {
            assertEquals(1, trainingDocument.getClients().size());
            assertEquals(TRAINER_EMAIL, trainingDocument.getTrainerEmail());
            assertEquals(DESCRIPTION, trainingDocument.getDescription());
            assertEquals(TRAINING, trainingDocument.getTraining());
            assertEquals(TRAINING_NAME, trainingDocument.getTrainingName());
        });
    }
}