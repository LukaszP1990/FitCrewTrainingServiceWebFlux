package com.fitcrew.trainingservice.core.config;

import com.fitcrew.jwt.util.JWTUtil;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientBuilderConfig {

	private static final String SYSTEM_NAME = "FIT_CREW_TRAINING_SERVICE";
	private final JWTUtil jwtUtil;

	public WebClientBuilderConfig(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder() {
		return WebClient.builder()
				.filter(this::propagateClientRequestWithAuthorization);
	}

	private Mono<ClientResponse> propagateClientRequestWithAuthorization(ClientRequest request,
																		 ExchangeFunction next) {
		return jwtUtil.getAuthenticationToken()
				.map(token -> propagateClientRequestWithAuthorization(request, token))
				.switchIfEmpty(getDefaultJwt(request))
				.flatMap(next::exchange);
	}

	private Mono<ClientRequest> getDefaultJwt(ClientRequest request) {
		var clientRequest = propagateClientRequestWithAuthorization(
				request, JWTUtil.BEARER + jwtUtil.createDefaultToken(SYSTEM_NAME));
		return Mono.defer(() -> Mono.just(clientRequest));
	}

	private ClientRequest propagateClientRequestWithAuthorization(ClientRequest clientRequest,
																  String token) {
		return ClientRequest.from(clientRequest)
				.header(HttpHeaders.AUTHORIZATION, token)
				.build();
	}
}
