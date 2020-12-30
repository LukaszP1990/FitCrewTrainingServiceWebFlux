package com.fitcrew.trainingservice.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fitcrew.FitCrewAppModel.domain.model.TrainingModel;

@Configuration
public class RedisConfig {

	@Bean
	public ReactiveRedisTemplate<String, TrainingModel> reactiveRedisTemplateForTrainerModel(ReactiveRedisConnectionFactory factory) {
		var valueSerializer = new Jackson2JsonRedisSerializer<>(TrainingModel.class);
		var keySerializer = new StringRedisSerializer();
		RedisSerializationContext.RedisSerializationContextBuilder<String, TrainingModel> builder =
				RedisSerializationContext.newSerializationContext(keySerializer);
		RedisSerializationContext<String, TrainingModel> context =
				builder.value(valueSerializer).build();

		return new ReactiveRedisTemplate<>(factory, context);
	}
}
