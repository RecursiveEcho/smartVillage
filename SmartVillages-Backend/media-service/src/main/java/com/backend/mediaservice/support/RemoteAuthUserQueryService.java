package com.backend.mediaservice.support;

import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.result.Result;
import com.backend.common.support.AuthUserQueryService;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Primary
@Component
public class RemoteAuthUserQueryService implements AuthUserQueryService {

  private final RestTemplate restTemplate;

  @Value("${services.auth.url}")
  private String authServiceUrl;

  public RemoteAuthUserQueryService(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  @Override
  public Map<Integer, String> getUsernameMap(Set<Integer> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyMap();
    }

    String url =
        UriComponentsBuilder.fromHttpUrl(authServiceUrl)
            .path("/internal/auth/usernames")
            .queryParam("ids", ids.toArray())
            .toUriString();

    try {
      ResponseEntity<Result<Map<Integer, String>>> response =
          restTemplate.exchange(
              url,
              HttpMethod.GET,
              null,
              new ParameterizedTypeReference<Result<Map<Integer, String>>>() {});

      Result<Map<Integer, String>> body = response.getBody();
      if (body == null || body.getData() == null) {
        return Collections.emptyMap();
      }

      return body.getData();
    } catch (RestClientException ex) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用 auth-service 查询用户名失败");
    }
  }
}
