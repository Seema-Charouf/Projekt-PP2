package dse.thesis404.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@DisplayName("ArchUnit – Onion/Schichten")
class ArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void init() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("dse.thesis404");
    }

    @Nested
    @DisplayName("Domain unabhängig von Infrastructure/Spring")
    class DomainIndependence {

        @Test
        void domain_haengt_nicht_von_Spring_ab() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.springframework..", "jakarta.servlet..", "jakarta.persistence.."
                    );
            rule.check(classes);
        }

        @Test
        void domain_nutzt_nur_java_und_sich_selbst() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..domain..")
                    .should().onlyDependOnClassesThat().resideInAnyPackage("..domain..", "java..")
                    .because("die Fachlogik soll ohne Framework, Datenbank oder Web verständlich und testbar sein");
            rule.check(classes);
        }

        @Test
        void domain_haengt_nicht_von_infrastructure_ab() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure..");
            rule.check(classes);
        }
    }

    @Nested
    @DisplayName("Application unabhängig von Infrastructure")
    class ApplicationIndependence {

        @Test
        void application_haengt_nicht_von_infrastructure_ab() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                    .because("Use Cases dürfen weder Web-Formulare/DTOs noch Persistenz-Klassen kennen");
            rule.check(classes);
        }

        @Test
        void application_kennt_kein_web_und_kein_servlet() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.springframework.web..", "jakarta.servlet..", "org.springframework.ui..")
                    .because("HTTP ist ein Detail der äußeren Schicht");
            rule.check(classes);
        }
    }

    @Nested
    @DisplayName("Controller nutzen keine JDBC-Repositories direkt")
    class ControllerRules {

        @Test
        void controller_nutzen_keine_ports_direkt() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..infrastructure.web..")
                    .should().dependOnClassesThat().resideInAPackage("..application.port..")
                    .because("Controller sprechen mit Application Services, damit Prüfungen wie Besitzrechte nicht umgangen werden");
            rule.check(classes);
        }

        @Test
        void controller_nutzen_keine_jdbc_repos_direkt() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..persistence.jdbc..")
                    .because("Controller nutzen Application Services, keine JDBC-Repositories");
            rule.check(classes);
        }

        @Test
        void controller_sind_in_infrastructure_web() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..infrastructure.web.controller..");
            rule.check(classes);
        }
    }

    @Nested
    @DisplayName("Schichtenarchitektur")
    class LayeredArchitecture {

        @Test
        void onion_schichten() {
            layeredArchitecture()
                    .consideringAllDependencies()
                    .layer("Domain").definedBy("..domain..")
                    .layer("Application").definedBy("..application..")
                    .layer("Infrastructure").definedBy("..infrastructure..")
                    .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
                    .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
                    .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
                    .check(classes);
        }
    }
}
