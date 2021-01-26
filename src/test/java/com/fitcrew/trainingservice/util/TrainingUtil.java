package com.fitcrew.trainingservice.util;

import com.fitcrew.FitCrewAppModel.domain.dto.TrainingDto;
import com.fitcrew.FitCrewAppModel.domain.model.TrainingModel;
import com.fitcrew.trainingservice.domains.TrainingDocument;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class TrainingUtil {

    public static final String TRAINER_EMAIL = "mockedTrainer@gmail.com";
    public static final String CLIENT = "client";
    public static final String DESCRIPTION = "description";
    public static final String TRAINING = "example of traning";
    public static final String TRAINING_NAME = "training name";

    public static TrainingDto getTrainingDto() {
        return TrainingDto.builder()
                .clients(Collections.singletonList(CLIENT))
                .trainerEmail(TRAINER_EMAIL)
                .description(DESCRIPTION)
                .training(TRAINING)
                .trainingName(TRAINING_NAME)
                .build();
    }

    public static List<TrainingDocument> getTrainingDocuments() {
        return IntStream.rangeClosed(1,3)
                .mapToObj(TrainingUtil::getTrainingDocument)
                .collect(Collectors.toList());
    }

    public static TrainingDocument getTrainingDocument(int value) {
        return TrainingDocument.builder()
                .id(String.valueOf(value))
                .clients(Collections.singletonList(CLIENT))
                .trainerEmail(String.valueOf(value).concat(TRAINER_EMAIL))
                .description(DESCRIPTION)
                .training(TRAINING)
                .trainingName(String.valueOf(value).concat(TRAINING_NAME))
                .build();
    }

    public static TrainingModel getTrainingModel() {
        return TrainingModel.builder()
                .clients(Collections.singletonList(CLIENT))
                .trainerEmail(TRAINER_EMAIL)
                .description(DESCRIPTION)
                .training(TRAINING)
                .trainingName(TRAINING_NAME)
                .build();
    }
}
