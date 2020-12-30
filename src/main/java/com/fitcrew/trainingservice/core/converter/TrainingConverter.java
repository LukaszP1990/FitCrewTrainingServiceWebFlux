package com.fitcrew.trainingservice.core.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fitcrew.FitCrewAppModel.domain.model.TrainingModel;
import com.fitcrew.trainingservice.domains.TrainingDocument;
import com.fitcrew.trainingservice.dto.TrainingDto;

@Mapper
public interface TrainingConverter {

	@Mapping(target = "id", ignore = true)
	TrainingDocument trainingDtoToTrainingDocument(TrainingDto trainingDto);

	TrainingModel trainingDocumentToTrainingModel(TrainingDocument trainingDocument);
}
