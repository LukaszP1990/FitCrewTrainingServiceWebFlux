package com.fitcrew.trainingservice.core.converter;

import com.fitcrew.FitCrewAppModel.domain.dto.TrainingDto;
import com.fitcrew.FitCrewAppModel.domain.model.TrainingModel;
import com.fitcrew.trainingservice.domains.TrainingDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TrainingConverter {

	@Mapping(target = "id", ignore = true)
	TrainingDocument trainingDtoToTrainingDocument(TrainingDto trainingDto);

	TrainingModel trainingDocumentToTrainingModel(TrainingDocument trainingDocument);
}
