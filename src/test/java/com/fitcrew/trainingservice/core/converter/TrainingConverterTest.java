package com.fitcrew.trainingservice.core.converter;

import com.fitcrew.FitCrewAppModel.domain.model.TrainingModel;
import com.fitcrew.trainingservice.domains.TrainingDocument;
import com.fitcrew.trainingservice.dto.TrainingDto;
import com.fitcrew.trainingservice.util.TrainingUtil;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.fitcrew.trainingservice.util.TrainingUtil.*;
import static org.junit.jupiter.api.Assertions.*;

class TrainingConverterTest {

    private final TrainingConverter trainingConverter = Mappers.getMapper(TrainingConverter.class);
    private static final TrainingDto trainingDto = TrainingUtil.getTrainingDto();
    private static final TrainingDocument trainingDocument = TrainingUtil.getTrainingDocument(1);

    @Test
    void shouldConvertTrainingDtoToTrainingDocument() {
        var trainingDocument = trainingConverter.trainingDtoToTrainingDocument(trainingDto);
        assertNotNull(trainingDocument);
        checkTrainingDocumentAssertions(trainingDocument);
    }

    @Test
    void shouldConvertTrainingDocumentToTrainingModel() {
        var trainingModel = trainingConverter.trainingDocumentToTrainingModel(trainingDocument);
        assertNotNull(trainingModel);
        checkTrainingModelAssertions(trainingModel);
    }

    private void checkTrainingDocumentAssertions(TrainingDocument trainingDocument) {
        assertAll(() -> {
            assertEquals(1, trainingDocument.getClients().size());
            assertEquals(TRAINER_EMAIL, trainingDocument.getTrainerEmail());
            assertEquals(DESCRIPTION, trainingDocument.getDescription());
            assertEquals(TRAINING, trainingDocument.getTraining());
            assertEquals(TRAINING_NAME, trainingDocument.getTrainingName());
        });
    }

    private void checkTrainingModelAssertions(TrainingModel trainingModel) {
        assertAll(() -> {
            assertEquals(1, trainingModel.getClients().size());
            assertEquals(String.valueOf(1).concat(TRAINER_EMAIL), trainingModel.getTrainerEmail());
            assertEquals(DESCRIPTION, trainingModel.getDescription());
            assertEquals(TRAINING, trainingModel.getTraining());
            assertEquals(String.valueOf(1).concat(TRAINING_NAME), trainingModel.getTrainingName());
        });
    }
}