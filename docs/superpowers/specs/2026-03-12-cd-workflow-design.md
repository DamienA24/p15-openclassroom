# CD Workflow Design — Firebase App Distribution

**Date:** 2026-03-12
**Project:** Eventorias (p15)
**Scope:** Étape 7 — Livraison continue via GitHub Actions

---

## Objectif

Créer un workflow GitHub Actions `.github/workflows/cd.yml` qui :
1. Se déclenche automatiquement lors du push d'un tag Git (ex: `v1.0.0`)
2. Construit un APK release signé
3. Uploade l'APK sur Firebase App Distribution

---

## Déclencheur

```yaml
on:
  push:
    tags:
      - 'v*'
```

Le workflow ne s'exécute que sur les tags préfixés par `v` (ex: `v1.0.0`, `v2.1`).

---

## Architecture du workflow

### Étapes

| # | Nom | Action/Commande |
|---|---|---|
| 1 | Checkout | `actions/checkout@v4` |
| 2 | Setup JDK 17 | `actions/setup-java@v4` (temurin, cache gradle) |
| 3 | Permissions gradlew | `chmod +x gradlew` |
| 4 | Créer `app/google-services.json` | `echo '${{ secrets.GOOGLE_SERVICES_JSON }}' > app/google-services.json` |
| 5 | Créer `local.properties` à la racine | `echo "sdk.dir=$ANDROID_SDK_ROOT" > local.properties` + `echo "MAPS_API_KEY=..."` |
| 6 | Créer `keystore.properties` à la racine | voir ci-dessous |
| 7 | Créer `service-account.json` à la racine | `echo '${{ secrets.CREDENTIAL_FILE_CONTENT }}' > service-account.json` |
| 8 | Assembler APK release | `./gradlew assembleRelease` |
| 9 | Upload Firebase App Distribution | `wzieba/Firebase-Distribution-Github-Action@v1` |

### Détail étape 6 — `keystore.properties`

Fichier créé **à la racine du projet** (car `build.gradle.kts` le lit via `rootProject.file("keystore.properties")`).
La valeur `storeFile` doit être `eventorias.keystore` (sans le préfixe `app/`) car Gradle résout ce chemin relativement au répertoire du module `app/`.

```bash
echo "storeFile=eventorias.keystore" > keystore.properties
echo "storePassword=${{ secrets.KEYSTORE_STORE_PASSWORD }}" >> keystore.properties
echo "keyAlias=${{ secrets.KEYSTORE_KEY_ALIAS }}" >> keystore.properties
echo "keyPassword=${{ secrets.KEYSTORE_KEY_PASSWORD }}" >> keystore.properties
```

### Détail étape 9 — Upload Firebase App Distribution

```yaml
- uses: wzieba/Firebase-Distribution-Github-Action@v1
  with:
    appId: ${{ secrets.FIREBASE_APP_ID }}
    serviceCredentialsFileContent: ${{ secrets.CREDENTIAL_FILE_CONTENT }}
    groups: ""          # Aucun groupe configuré pour l'instant
    releaseNotes: ""    # Intentionnellement vide — aucun testeur défini
    file: app/build/outputs/apk/release/app-release.apk
```

Note : `app/eventorias.keystore` est commité dans le dépôt et sera restauré par le checkout. Seul `keystore.properties` (gitignored) doit être recréé.

---

## APK produit

`app/build/outputs/apk/release/app-release.apk`

---

## Secrets GitHub requis

### Déjà configurés (réutilisés depuis CI)

| Secret | Usage |
|---|---|
| `GOOGLE_SERVICES_JSON` | Config Firebase |
| `MAPS_API_KEY` | Clé Google Maps |
| `KEYSTORE_STORE_PASSWORD` | Signature APK |
| `KEYSTORE_KEY_ALIAS` | Signature APK |
| `KEYSTORE_KEY_PASSWORD` | Signature APK |

### Nouveaux secrets à ajouter

| Secret | Valeur |
|---|---|
| `FIREBASE_APP_ID` | ID de l'app Firebase, format `1:xxxx:android:xxxx` (Firebase Console → Paramètres du projet → Vos applications) |
| `CREDENTIAL_FILE_CONTENT` | Contenu JSON du compte de service (`Compte de service App Engine p15-openclassroom.json`) |

---

## Fichiers impactés

| Fichier | Action |
|---|---|
| `.github/workflows/cd.yml` | **Créer** |

Aucune modification de `build.gradle.kts` ou d'autres fichiers existants n'est nécessaire.

---

## Contraintes et points de vigilance

- `keystore.properties` est dans `.gitignore` → recréé en CD via les secrets, comme dans `ci.yml`
- `storeFile=eventorias.keystore` (relatif au module `app/`) — ne pas écrire `app/eventorias.keystore`
- `keystore.properties` doit être créé à la **racine du projet**, pas dans `app/`
- Le compte de service JSON ne doit jamais être commité
- `sdk.dir` dans `local.properties` est un héritage de `ci.yml` (optionnel sur runners GitHub, le SDK est déjà disponible via `ANDROID_SDK_ROOT`)
- `releaseNotes` et `groups` laissés vides intentionnellement — aucun testeur configuré
