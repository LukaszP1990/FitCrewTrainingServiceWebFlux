package com.fitcrew.trainingservice.core.util;

import com.fitcrew.FitCrewAppModel.domain.dto.TrainingDto;
import com.fitcrew.trainingservice.domains.TrainingDocument;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TrainingDocumentUtil {

    public static TrainingDocument getUpdatedTrainingDocument(TrainingDto trainingDto,
                                                              TrainingDocument trainingDocument) {
        trainingDocument.setTrainingName(trainingDto.getTrainingName());
        trainingDocument.setDescription(trainingDto.getDescription());
        trainingDocument.setTraining(trainingDto.getTraining());
        trainingDocument.setTrainerEmail(trainingDto.getTrainerEmail());
        return trainingDocument;
    }
}
