package com.fitcrew.trainingservice.validation;


import com.fitcrew.trainingservice.dto.TrainingDto;
import com.fitcrew.trainingservice.dto.validation.NotAllRequiredValueSetInTrainingDto;
import com.fitcrew.trainingservice.util.TrainingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class NotAllRequiredValueSetInTrainingDtoTest {

    private static final TrainingDto okTrainingDto = TrainingUtil.getTrainingDto();
    private static final TrainingDto badTrainingDto = TrainingDto.builder().build();

    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Mock
    private NotAllRequiredValueSetInTrainingDto.NotAllRequiredValueSetInTrainingDtoValidator clientValidator;

    @BeforeEach
    void setUp() {
        when(clientValidator.isValid(any(), any()))
                .thenCallRealMethod();
    }

    @Test
    void shouldSucceedWhenRequiredValuesHaveBeenSet() {
        assertTrue(clientValidator
                .isValid(okTrainingDto, constraintValidatorContext));
    }

    @Test
    void shouldFailWhenWhenRequiredValuesHaveNotBeenSet() {
        assertFalse(clientValidator
                .isValid(badTrainingDto, constraintValidatorContext));
    }
}
