# Eventorias

Application Android de gestion d'événements — Kotlin, Jetpack Compose, Firebase.

## Prérequis

- Android Studio Hedgehog ou plus récent
- JDK 11+
- Un projet Firebase (Auth + Firestore + Storage + Cloud Messaging activés)

## Installation

```bash
git clone https://github.com/DamienA24/p15-openclassroom
cd p15
```

## Configuration des clés

### 1. Firebase — `app/google-services.json`

Télécharger le fichier depuis la console Firebase :
> Console Firebase → Paramètres du projet → Vos applications → google-services.json

Placer le fichier dans `app/google-services.json`.

### 2. Google Maps Static API — `local.properties`

Ajouter à la racine du projet dans `local.properties` :

```properties
MAPS_API_KEY=votre_clé_maps_ici
```

Obtenir une clé : [Google Cloud Console](https://console.cloud.google.com/) → APIs & Services → Maps Static API.

> `local.properties` est dans `.gitignore` — ne jamais commiter ce fichier.

## Lancer l'application

```bash
./gradlew installDebug
```

## Tests

```bash
# Tests unitaires (JVM, sans émulateur)
./gradlew testDebugUnitTest

# Tests UI (émulateur ou appareil requis)
./gradlew connectedDebugAndroidTest
```
