package com.fitcrew.trainingservice.dto.validation;

import com.fitcrew.trainingservice.dto.TrainingDto;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
@Constraint(validatedBy = NotAllRequiredValueSetInTrainingDto.NotAllRequiredValueSetInTrainingDtoValidator.class)
public @interface NotAllRequiredValueSetInTrainingDto {
	String message() default "Not all required values has been set in training dto. This doesn't apply to the field trainerEmail and clients;";

	Class<?>[] groups() default {};

	Class[] payload() default {};

	class NotAllRequiredValueSetInTrainingDtoValidator implements ConstraintValidator<NotAllRequiredValueSetInTrainingDto, TrainingDto> {

		@Override
		public boolean isValid(TrainingDto trainingDto,
							   ConstraintValidatorContext constraintValidatorContext) {
			return checkTraining(trainingDto);
		}

		private boolean checkTraining(TrainingDto trainingDto) {
			List<? extends Comparable<? extends Comparable<?>>> listOfFields = Stream.of(
					trainingDto.getTraining(),
					trainingDto.getDescription(),
					trainingDto.getTrainingName())
					.collect(Collectors.toList());
			return validateFields(listOfFields);
		}

		private boolean validateFields(List<? extends Comparable<? extends Comparable<?>>> listOfFields) {
			return Optional.ofNullable(listOfFields)
					.map(field -> isField(listOfFields))
					.orElse(false);
		}

		private boolean isField(List<? extends Comparable<? extends Comparable<?>>> listOfFields) {
			return IntStream.rangeClosed(0, listOfFields.size() - 1)
					.allMatch(value -> Objects.nonNull(listOfFields.get(value)));
		}
	}
}
