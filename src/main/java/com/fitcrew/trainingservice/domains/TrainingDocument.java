package com.fitcrew.trainingservice.domains;

import lombok.*;

import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
@Getter
@Setter
@Builder
@AllArgsConstructor(onConstructor = @__(@Builder))
@NoArgsConstructor
@ToString
public class TrainingDocument implements Serializable {

	private static final long serialVersionUID = -3255126230685615683L;

	@Id
	private String id;

	@Field(value = "TRAINING_NAME")
	@Indexed(unique = true)
	@NotNull
	@Length(max = 100)
	private String trainingName;

	@Field(value = "DESCRIPTION")
	@NotNull
	@Length(max = 400)
	private String description;

	@Field(value = "TRAINING")
	@NotNull
	private String training;

	@Field(value = "TRAINER_EMAIL")
	@NotNull
	@Length(max = 50)
	private String trainerEmail;

	@Field(value = "CLIENTS")
	@NotNull
	@Length(max = 400)
	private List<String> clients;
}
