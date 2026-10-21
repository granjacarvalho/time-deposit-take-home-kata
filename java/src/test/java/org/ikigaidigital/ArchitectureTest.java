package org.ikigaidigital;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;

// Hexagonal rule from CLAUDE.md, enforced in the build instead of by convention.
@AnalyzeClasses(
    packages = "org.ikigaidigital",
    importOptions = ImportOption.DoNotIncludeTests.class)
@DisplayName("Hexagonal boundaries")
class ArchitectureTest {

  private static final String[] CORE = {
    "org.ikigaidigital", "org.ikigaidigital.interest..", "org.ikigaidigital.application.."
  };

  @ArchTest
  static final ArchRule domainAndApplicationDoNotUseSpring =
      noClasses()
          .that()
          .resideInAnyPackage(CORE)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("org.springframework..");

  @ArchTest
  static final ArchRule domainAndApplicationDoNotDependOnAdapters =
      noClasses()
          .that()
          .resideInAnyPackage(CORE)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("org.ikigaidigital.adapter..", "org.ikigaidigital.config..");
}
