package org.ikigaidigital.adapter.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.ikigaidigital.config.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.yaml.snakeyaml.Yaml;

// The brief allows exactly two endpoints: the running app must expose the contract's operations
// and nothing else. Swagger UI, /v3/api-docs and Boot's /error are documentation and plumbing
// (A-9).
@IntegrationTest
@DisplayName("AC-8/AC-9 the running app exposes exactly the contract's operations")
class ApiSurfaceIT {

  private static final Set<String> DOCUMENTATION_AND_PLUMBING =
      Set.of("org.springdoc", "org.springframework.boot");

  @Autowired
  @Qualifier("requestMappingHandlerMapping")
  RequestMappingHandlerMapping mappings;

  @Test
  void should_expose_exactly_the_contract_operations_when_running() throws IOException {
    Set<String> exposed = new TreeSet<>();
    mappings.getHandlerMethods().entrySet().stream()
        .filter(entry -> !isDocumentationOrPlumbing(entry.getValue()))
        .forEach(entry -> exposed.addAll(operations(entry.getKey())));

    assertThat(exposed).isEqualTo(contractOperations());
  }

  private static boolean isDocumentationOrPlumbing(HandlerMethod handler) {
    String handlerPackage = handler.getBeanType().getPackageName();
    return DOCUMENTATION_AND_PLUMBING.stream().anyMatch(handlerPackage::startsWith);
  }

  private static Set<String> operations(RequestMappingInfo info) {
    Set<String> operations = new TreeSet<>();
    info.getMethodsCondition()
        .getMethods()
        .forEach(
            method ->
                info.getPatternValues()
                    .forEach(path -> operations.add(method.name() + " " + path)));
    return operations;
  }

  @SuppressWarnings("unchecked")
  private static Set<String> contractOperations() throws IOException {
    try (InputStream contract = ApiSurfaceIT.class.getResourceAsStream("/static/openapi.yaml")) {
      Map<String, Map<String, Object>> paths =
          (Map<String, Map<String, Object>>)
              new Yaml().<Map<String, Object>>load(contract).get("paths");
      Set<String> operations = new TreeSet<>();
      paths.forEach(
          (path, methods) ->
              methods
                  .keySet()
                  .forEach(method -> operations.add(method.toUpperCase() + " " + path)));
      return operations;
    }
  }
}
