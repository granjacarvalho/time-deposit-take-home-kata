package org.ikigaidigital;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

// Hexagonal rule from CLAUDE.md, enforced in the build instead of by convention.
@AnalyzeClasses(
    packages = "org.ikigaidigital",
    importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

  private static final String[] CORE = {
    "org.ikigaidigital", "org.ikigaidigital.interest..", "org.ikigaidigital.application.."
  };

  @ArchTest
  static final ArchRule should_keep_domain_and_application_free_of_spring =
      noClasses()
          .that()
          .resideInAnyPackage(CORE)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("org.springframework..");

  @ArchTest
  static final ArchRule should_keep_domain_and_application_independent_of_adapters =
      noClasses()
          .that()
          .resideInAnyPackage(CORE)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("org.ikigaidigital.adapter..", "org.ikigaidigital.config..");
}
