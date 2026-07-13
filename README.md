# MATAWAN - TEST

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)

API REST de gestion de l’effectif de l’OGC Nice.

---

## 📋 Fonctionnalités

- **GET /api/teams** – Liste paginée des équipes avec leurs joueurs. Tri possible sur `name`, `acronym`, `budget`.
- **POST /api/teams** – Création d’une équipe (avec ou sans joueurs).
- Base de données H2 embarquée.
---

## 🛠️ Prérequis

- **Java 21** (ou ultérieur)
- **Maven 3.9+**
- **Docker** (optionnel)

---

## 🚀 Démarrage rapide

### Avec Docker

1. Construire l’image :
   ```bash
   docker build -t matawan-test-api .
2. Lancer le conteneur :
   ```bash
   docker run -p 8080:8080 matawan-test-api
3. Accéder à l’API :
    - API : http://localhost:8080/api/teams
    - Swagger UI : http://localhost:8080/swagger-ui.html

### En local (avec Maven)
1. Compiler et lancer les tests :
   ```bash
   mvn clean install
2. Démarrer l’application :
   ```bash
   mvn spring-boot:run

---

## 📚 Documentation Swagger

L’API est intégralement documentée avec OpenAPI 3.0.

Une fois l’application lancée, rendez-vous sur :
   ```bash
   http://localhost:8080/swagger-ui.html
   ```

La spécification YAML source se trouve dans ```src/main/resources/openapi/matawan-test-api.yaml```.
Les DTOs et les interfaces REST sont générées automatiquement à partir de ce fichier lors du build (via ```openapi-generator-maven-plugin```).

## 🏗️ Architecture & Choix techniques

### Architecture hexagonale (Ports & Adapters)

Le projet suit une architecture hexagonale stricte afin de garantir l’indépendance du domaine métier vis‑à‑vis des frameworks et des technologies d’infrastructure.


**Pourquoi cette architecture ?**
- Le domaine (`domain`) est pur Java, sans aucune dépendance vers Spring, Hibernate ou l’API REST.
- Les cas d’usage (`application`) orchestrent les flux métier en s’appuyant uniquement sur des interfaces (ports).
- Les adaptateurs (`infrastructure`) implémentent ces ports avec les technologies choisies.  
  → Changement de base de données, de framework REST ou de librairie de mapping sans impacter le cœur métier.

### Technologies utilisées

| Composant          | Choix technique                     | Justification |
|--------------------|-------------------------------------|---------------|
| Langage            | Java 21                             | Dernière version LTS, support des records, pattern matching, etc. |
| Framework principal| Spring Boot 3.4, Spring Data JPA    | Standard industriel, grande productivité, configuration automatique |
| Base de données    | H2 (embarquée)                      | Zéro installation, idéale pour les tests et les démonstrations. Remplaçable facilement par PostgreSQL ou Oracle |
| Documentation API  | OpenAPI 3.0 (YAML)                  | Source unique de vérité, contrat d’API clair, génération automatique de code |
| Génération de code | OpenAPI Generator (plugin Maven)    | Génère les DTOs et les interfaces REST à partir du fichier YAML. Évite le code boilerplate et les désynchronisations |
| Mapping objet      | MapStruct 1.6                       | Mapping déclaratif, exécution au compile-time (performant), pas de réflexion |
| Validation         | Bean Validation (Jakarta)           | Annotations sur les DTOs, gestion globale des erreurs |
| Tests              | JUnit 5, Mockito, MockMvc, AssertJ  | Tests unitaires rapides, tests d’intégration avec base en mémoire |
| Conteneurisation   | Docker (multi-stage build)         | Image légère, déploiement reproductible en environnement cible |
| Logging            | SLF4J / Logback                     | Standard Spring Boot, logs structurés |
| Documentation live | Swagger UI (Springdoc)              | Interface interactive pour tester l’API directement depuis le navigateur |

### Détail des composants clés

#### Domain Model
- **Entités** : `Team`, `Player` – classes mutables avec comportement métier (`addPlayer`).
- **Value Objects** : `Budget` (record), `Position` (enum). Immuables, sans identité.
- **Ports** : `GetTeamsUseCase`, `AddTeamUseCase` (inbound), `TeamRepository` (outbound).  
  → Les ports isolent le domaine et facilitent les tests en permettant l’injection de mocks.

#### Application
- **`TeamService`** implémente les deux cas d’usage et orchestre les appels au port de persistance.
- Les règles métier (ex : unicité de l’acronyme) sont centralisées ici.

#### Infrastructure – Persistance
- Entités JPA (`TeamEntity`, `PlayerEntity`) décorrélées du domaine.
- Adaptateur `TeamRepositoryAdapter` qui convertit les objets via MapStruct et gère la relation bidirectionnelle.
- Requêtes optimisées (`JOIN FETCH`) pour éviter le problème N+1.

#### Infrastructure – REST
- Le fichier `matawan-test-api.yaml` décrit l’API.
- Les DTOs et l’interface `TeamsApi` sont générés automatiquement.
- Le contrôleur `TeamController` implémente `TeamsApi` et délègue au service applicatif.
- Les exceptions métier sont interceptées par `GlobalExceptionHandler` qui renvoie des codes HTTP appropriés (409 Conflict pour un acronyme en double, 400 pour les validations, etc.).

#### Infrastructure – Mapping
- **MapStruct** est utilisé pour :
    - `TeamMapper` / `PlayerMapper` : DTOs générés ↔ Modèle domaine
    - `TeamEntityMapper` / `PlayerEntityMapper` : Entités JPA ↔ Modèle domaine

### Tests

Le projet comprend :
- **Tests unitaires** : `TeamServiceTest` (mock du repository)
- **Tests d’intégration** : `TeamControllerIntegrationTest` (MockMvc + base H2), `TeamRepositoryAdapterTest` (persistance réelle)
- **Tests de mapping** : `TeamMapperTest` (vérification des conversions MapStruct)

La base H2 en mémoire est utilisée pour tous les tests d’intégration, garantissant des exécutions rapides et isolées.