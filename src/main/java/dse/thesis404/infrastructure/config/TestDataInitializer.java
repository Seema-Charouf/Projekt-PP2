package dse.thesis404.infrastructure.config;

import dse.thesis404.application.port.BetreuerProfilRepository;
import dse.thesis404.application.port.StudentRepository;
import dse.thesis404.application.port.StudentThemeRequestRepository;
import dse.thesis404.application.port.ThemeRepository;
import dse.thesis404.domain.model.betreuer.BetreuerProfil;
import dse.thesis404.domain.model.betreuer.ContactInformations;
import dse.thesis404.domain.model.betreuer.Email;
import dse.thesis404.domain.model.betreuer.PhoneNumber;
import dse.thesis404.domain.model.betreuer.Raum;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.student.RequestStatus;
import dse.thesis404.domain.model.student.Student;
import dse.thesis404.domain.model.student.StudentThemeRequest;
import dse.thesis404.domain.model.theme.Beschreibung;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.ThemeId;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class TestDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(TestDataInitializer.class);

    private final BetreuerProfilRepository betreuerProfilRepository;
    private final ThemeRepository themeRepository;
    private final StudentRepository studentRepository;
    private final StudentThemeRequestRepository studentThemeRequestRepository;

    public TestDataInitializer(
            BetreuerProfilRepository betreuerProfilRepository,
            ThemeRepository themeRepository,
            StudentRepository studentRepository,
            StudentThemeRequestRepository studentThemeRequestRepository
    ) {
        this.betreuerProfilRepository = betreuerProfilRepository;
        this.themeRepository = themeRepository;
        this.studentRepository = studentRepository;
        this.studentThemeRequestRepository = studentThemeRequestRepository;
    }

    @PostConstruct
    public void initTestData() {
        log.info("=== Initialisiere Testdaten ===");

        if (!betreuerProfilRepository.findAll().isEmpty()) {
            log.info("Daten bereits vorhanden, überspringe Testdaten-Initialisierung.");
            return;
        }

        ProfileId profileId1 = ProfileId.ofGithubUserId(1001);
        BetreuerProfil betreuer1 = createBetreuer(
                profileId1,
                "Prof. Dr. Anna Schmidt",
                "anna.schmidt@hhu.de",
                "+4921181012345",
                "25.12.02.10"
        );
        betreuer1.addFachgebiet("Software Engineering");
        betreuer1.addFachgebiet("Agile Methoden");
        betreuer1.addFachgebiet("Clean Code");
        betreuer1.addLink("https://www.se.hhu.de/schmidt", "Persönliche Homepage");
        betreuerProfilRepository.save(betreuer1);

        ProfileId profileId2 = ProfileId.ofGithubUserId(1002);
        BetreuerProfil betreuer2 = createBetreuer(
                profileId2,
                "Dr. Michael Weber",
                "michael.weber@hhu.de",
                "+4921181012346",
                "25.13.01.05"
        );
        betreuer2.addFachgebiet("Künstliche Intelligenz");
        betreuer2.addFachgebiet("Machine Learning");
        betreuer2.addFachgebiet("Deep Learning");
        betreuer2.addLink("https://www.ki.hhu.de/weber", "KI-Labor");
        betreuerProfilRepository.save(betreuer2);

        ProfileId profileId3 = ProfileId.ofGithubUserId(1003);
        BetreuerProfil betreuer3 = createBetreuer(
                profileId3,
                "Prof. Dr. Sarah Müller",
                "sarah.mueller@hhu.de",
                "+4921181012347",
                "25.12.03.20"
        );
        betreuer3.addFachgebiet("Datenbanken");
        betreuer3.addFachgebiet("Big Data");
        betreuer3.addFachgebiet("NoSQL");
        betreuerProfilRepository.save(betreuer3);

        ProfileId profileId4 = ProfileId.ofGithubUserId(1004);
        BetreuerProfil betreuer4 = createBetreuer(
                profileId4,
                "Dr. Thomas Fischer",
                "thomas.fischer@hhu.de",
                "+4921181012348",
                "25.14.02.15"
        );
        betreuer4.addFachgebiet("IT-Sicherheit");
        betreuer4.addFachgebiet("Kryptographie");
        betreuer4.addFachgebiet("Netzwerksicherheit");
        betreuer4.addLink("https://www.sec.hhu.de/fischer", "Security Lab");
        betreuerProfilRepository.save(betreuer4);

        ProfileId profileId5 = ProfileId.ofGithubUserId(1005);
        BetreuerProfil betreuer5 = createBetreuer(
                profileId5,
                "Prof. Dr. Julia Becker",
                "julia.becker@hhu.de",
                "+4921181012349",
                "25.12.01.30"
        );
        betreuer5.addFachgebiet("Verteilte Systeme");
        betreuer5.addFachgebiet("Cloud Computing");
        betreuer5.addFachgebiet("Microservices");
        betreuerProfilRepository.save(betreuer5);

        log.info("5 Betreuer-Profile erstellt.");

        Theme theme1 = createTheme(
                profileId1,
                "Entwicklung einer RESTful API mit Spring Boot",
                "In dieser Arbeit soll eine **RESTful API** für ein Projektmanagement-Tool entwickelt werden.\n\n" +
                "## Anforderungen\n" +
                "- CRUD-Operationen für Projekte und Aufgaben\n" +
                "- JWT-basierte Authentifizierung\n" +
                "- OpenAPI-Dokumentation\n\n" +
                "## Technologien\n" +
                "Spring Boot, Spring Security, PostgreSQL"
        );
        theme1.addFachgebiet("Software Engineering");
        theme1.addFachgebiet("Backend-Entwicklung");
        theme1.addVeranstaltung("Programmierung");
        theme1.addVeranstaltung("Softwaretechnik");
        themeRepository.save(theme1);

        Theme theme2 = createTheme(
                profileId1,
                "Test-Driven Development in der Praxis",
                "Analyse und Implementierung von **Test-Driven Development (TDD)** in einem bestehenden Projekt.\n\n" +
                "## Ziele\n" +
                "- Einführung von TDD-Praktiken\n" +
                "- Verbesserung der Codequalität\n" +
                "- Messung der Testabdeckung"
        );
        theme2.addFachgebiet("Software Engineering");
        theme2.addFachgebiet("Qualitätssicherung");
        theme2.addVeranstaltung("Softwaretechnik");
        themeRepository.save(theme2);

        Theme theme3 = createTheme(
                profileId2,
                "Bildklassifikation mit Convolutional Neural Networks",
                "Entwicklung eines **CNN-Modells** zur automatischen Klassifikation von medizinischen Bildern.\n\n" +
                "## Aufgaben\n" +
                "- Datenvorbereitung und Augmentation\n" +
                "- Modellarchitektur-Design\n" +
                "- Training und Evaluation\n\n" +
                "## Technologien\n" +
                "Python, TensorFlow/PyTorch, Jupyter Notebooks"
        );
        theme3.addFachgebiet("Künstliche Intelligenz");
        theme3.addFachgebiet("Deep Learning");
        theme3.addVeranstaltung("Machine Learning");
        theme3.addVeranstaltung("Data Science");
        themeRepository.save(theme3);

        Theme theme4 = createTheme(
                profileId2,
                "Chatbot-Entwicklung mit Large Language Models",
                "Entwicklung eines **intelligenten Chatbots** unter Verwendung von LLMs.\n\n" +
                "## Inhalt\n" +
                "- Integration von GPT-APIs\n" +
                "- Prompt Engineering\n" +
                "- Retrieval Augmented Generation (RAG)"
        );
        theme4.addFachgebiet("Künstliche Intelligenz");
        theme4.addFachgebiet("Natural Language Processing");
        theme4.addVeranstaltung("Machine Learning");
        themeRepository.save(theme4);

        Theme theme5 = createTheme(
                profileId3,
                "Performance-Optimierung von SQL-Abfragen",
                "Analyse und Optimierung von **SQL-Abfragen** in einer produktiven Umgebung.\n\n" +
                "## Schwerpunkte\n" +
                "- Query-Analyse mit EXPLAIN\n" +
                "- Index-Optimierung\n" +
                "- Partitionierung großer Tabellen"
        );
        theme5.addFachgebiet("Datenbanken");
        theme5.addVeranstaltung("Datenbanken");
        themeRepository.save(theme5);

        Theme theme6 = createTheme(
                profileId3,
                "Vergleich von SQL und NoSQL Datenbanken",
                "**Empirischer Vergleich** verschiedener Datenbanktypen für unterschiedliche Anwendungsfälle.\n\n" +
                "## Datenbanken\n" +
                "- PostgreSQL (relational)\n" +
                "- MongoDB (dokumentenorientiert)\n" +
                "- Redis (key-value)\n" +
                "- Neo4j (graph)"
        );
        theme6.addFachgebiet("Datenbanken");
        theme6.addFachgebiet("NoSQL");
        theme6.addVeranstaltung("Datenbanken");
        theme6.addVeranstaltung("Big Data");
        themeRepository.save(theme6);

        Theme theme7 = createTheme(
                profileId4,
                "Penetration Testing einer Webanwendung",
                "Durchführung eines umfassenden **Penetrationstests** einer bestehenden Webanwendung.\n\n" +
                "## Prüfbereiche\n" +
                "- OWASP Top 10 Vulnerabilities\n" +
                "- SQL Injection\n" +
                "- Cross-Site Scripting (XSS)\n" +
                "- Authentifizierungsschwächen"
        );
        theme7.addFachgebiet("IT-Sicherheit");
        theme7.addFachgebiet("Web Security");
        theme7.addVeranstaltung("IT-Sicherheit");
        themeRepository.save(theme7);

        Theme theme8 = createTheme(
                profileId4,
                "Implementierung von Zero-Trust-Architektur",
                "Konzeption und Implementierung einer **Zero-Trust-Sicherheitsarchitektur**.\n\n" +
                "## Komponenten\n" +
                "- Identity and Access Management\n" +
                "- Micro-Segmentierung\n" +
                "- Continuous Verification"
        );
        theme8.addFachgebiet("IT-Sicherheit");
        theme8.addFachgebiet("Netzwerksicherheit");
        theme8.addVeranstaltung("IT-Sicherheit");
        theme8.addVeranstaltung("Rechnernetze");
        themeRepository.save(theme8);

        Theme theme9 = createTheme(
                profileId5,
                "Microservices-Migration einer Monolith-Anwendung",
                "Schrittweise **Migration** einer monolithischen Anwendung zu einer Microservices-Architektur.\n\n" +
                "## Phasen\n" +
                "- Analyse der bestehenden Architektur\n" +
                "- Domain-Driven Design\n" +
                "- Strangler Fig Pattern\n" +
                "- Container-Orchestrierung mit Kubernetes"
        );
        theme9.addFachgebiet("Verteilte Systeme");
        theme9.addFachgebiet("Microservices");
        theme9.addVeranstaltung("Softwaretechnik");
        theme9.addVeranstaltung("Verteilte Systeme");
        themeRepository.save(theme9);

        Theme theme10 = createTheme(
                profileId5,
                "Event-Driven Architecture mit Apache Kafka",
                "Implementierung einer **ereignisgesteuerten Architektur** mit Apache Kafka.\n\n" +
                "## Inhalte\n" +
                "- Event Sourcing\n" +
                "- CQRS Pattern\n" +
                "- Stream Processing"
        );
        theme10.addFachgebiet("Verteilte Systeme");
        theme10.addFachgebiet("Cloud Computing");
        theme10.addVeranstaltung("Verteilte Systeme");
        themeRepository.save(theme10);

        log.info("10 Themen erstellt.");

        Student s1 = new Student(ProfileId.ofGithubUserId(2001));
        s1.setDisplayName("Max Mustermann");
        s1.addInteresse("Software Engineering");
        s1.addInteresse("Clean Code");
        s1.addVeranstaltung("Programmierung");
        s1.addVeranstaltung("Softwaretechnik");
        studentRepository.save(s1);

        Student s2 = new Student(ProfileId.ofGithubUserId(2002));
        s2.setDisplayName("Lisa Meyer");
        s2.addInteresse("Künstliche Intelligenz");
        s2.addInteresse("Data Science");
        s2.addVeranstaltung("Machine Learning");
        s2.addVeranstaltung("Datenbanken");
        studentRepository.save(s2);

        Student s3 = new Student(ProfileId.ofGithubUserId(2003));
        s3.setDisplayName("Tom Schneider");
        s3.addInteresse("IT-Sicherheit");
        s3.addVeranstaltung("Rechnernetze");
        s3.addVeranstaltung("IT-Sicherheit");
        studentRepository.save(s3);

        Student s4 = new Student(ProfileId.ofGithubUserId(2004));
        s4.setDisplayName("Sarah Wagner");
        s4.addInteresse("Cloud Computing");
        s4.addVeranstaltung("Verteilte Systeme");
        s4.addVeranstaltung("Softwaretechnik");
        studentRepository.save(s4);

        Student s5 = new Student(ProfileId.ofGithubUserId(2005));
        s5.setDisplayName("Felix Klein");
        s5.addInteresse("Datenbanken");
        s5.addInteresse("Backend-Entwicklung");
        s5.addVeranstaltung("Datenbanken");
        s5.addVeranstaltung("Programmierung");
        studentRepository.save(s5);

        log.info("5 Studenten erstellt.");

        studentThemeRequestRepository.save(new StudentThemeRequest(
                ProfileId.ofGithubUserId(2001), theme1.getId(), RequestStatus.PENDING));
        studentThemeRequestRepository.save(new StudentThemeRequest(
                ProfileId.ofGithubUserId(2002), theme3.getId(), RequestStatus.PENDING));
        log.info("2 Beispiel-Anfragen (Student-Thema) erstellt.");

        log.info("=== Testdaten-Initialisierung abgeschlossen ===");
        log.info("Verfügbare Test-Benutzer (GitHub IDs): 1001–1005 (Betreuer), 2001–2005 (Studenten)");
    }

    private BetreuerProfil createBetreuer(ProfileId id, String name, String email, String phone, String raum) {
        ContactInformations contact = new ContactInformations(
                new Email(email),
                phone != null ? new PhoneNumber(phone) : null,
                Raum.of(raum)
        );
        return new BetreuerProfil(id, name, contact);
    }

    private Theme createTheme(ProfileId ownerId, String titel, String beschreibungMarkdown) {
        return new Theme(
                ThemeId.newId(),
                ownerId,
                titel,
                new Beschreibung(beschreibungMarkdown)
        );
    }
}
