# 🧠 LeftOrRight ~ LOR

Aplikacja społecznościowa do binarnej debaty — użytkownicy wybierają stronę (lewą/prawą) w różnych tematach, dzielą się opiniami, przeglądają argumenty, a najpopularniejsze tematy wizualizowane są w formie „baniek mydlanych”.

---

## 📌 Spis Treści

- [Opis projektu](#opis-projektu)
- [Funkcjonalności](#funkcjonalności)
- [Technologie](#technologie)
- [Instalacja](#instalacja)
- [Schemat bazy danych](#schemat-bazy-danych)
- [API](#api)
- [Diagram ERD](#diagram-erd)
- [Rozwój i TODO](#rozwój-i-todo)
- [Autor](#autor)

---

## 🧩 Opis projektu

LeftOrRight to interaktywna aplikacja internetowa do społecznych dyskusji i przeglądania argumentów "za" i "przeciw" w aktualnych, kontrowersyjnych lub popularnych tematach. Treści organizowane są w formie tematów reprezentowanych przez "bańki", których rozmiar zależy od ich popularności.

---

## ✨ Funkcjonalności

- Rejestracja i logowanie użytkowników (JWT, cookies)
- Wybór strony (lewa/prawa) + dodanie komentarza
- Przeglądanie tematów i argumentów bez logowania
- Obserwacja tematów
- Filtrowanie feedu wg kraju, kontynentu, kategorii
- Proponowanie nowych tematów (lub import z API)
- Głosowanie + aktualizacja popularności w czasie rzeczywistym
- Panel administratora (moderacja tematów, komentarzy, użytkowników)

---

## ⚙️ Technologie

### Backend:
- Java 21+
- Spring Boot
- Spring Data JPA
- Spring Security (JWT)
- PostgreSQL
- Maven

### Frontend:
- React *(planowane)*
- lub Thymeleaf *(tymczasowo lub fallback)*

### Inne:
- MapStruct
- Lombok
- WebClient (do integracji z zewnętrznym API)
- Swagger (planowane)
- Docker *(planowane)*
- CI/CD *(planowane)*

---

## 🚀 Instalacja

```bash
git clone https://github.com/eloomati/left-or-right.git
cd left-or-right
```
🛠️ Inicjalizacja bazy danych
1. Zainstaluj Podmana.
2. Uruchom kontener z PostgreSQL:
```bash
  ./scripts/start_postgres.sh
```
3. Zainicjalizuj bazę danych oraz użytkowników Flyway i aplikacji:
- Bez nazwy bazy (używana wartość domyślna):
```bash
  ./scripts/init_db.sh "" haslo_flyway haslo_app
```
- Z podaniem nazwy bazy (np. lor_test):
```bash
  ./scripts/init_db.sh lor_test haslo_flyway haslo_app
```
📌 Uwagi:
- Użytkownik migracji (Flyway) służy wyłącznie do wykonywania migracji (zmian w strukturze bazy danych).
- Użytkownik aplikacji jest używany przez aplikację w codziennej pracy z danymi.
- Hasła należy podać do skryptu czterokrotnie – domyślna wartość to admin

## 🧠 Schemat bazy danych
### Encje (tabele):
- User
- Topic
- Vote
- Comment
- Category
- FollowedTopic
- ProposedTopic

###  Typy relacji:
- OneToMany: User ↔ Comment, Topic ↔ Vote
- ManyToOne: Topic ↔ Category
- ManyToMany: User ↔ Category (preferencje)

## 📊 Diagram ERD
Diagram wygenerowany w IntelliJ IDEA:
👉 docs/erd.png (w trakcie generowania)

## 🧪 API
(W trakcie tworzenia – dodaj opis endpointów, np. login, rejestracja, dodaj komentarz, głosuj, pobierz tematy itd.)

## 📈 Rozwój i TODO
### 🔧 Sprint 1: Model danych
- Schemat bazy danych (JPA + PostgreSQL)
- Startery Maven
- ERD diagram

### 📦 Sprint 2: Backend funkcjonalny
- CRUD: User, Topic, Comment
- Rejestracja / Logowanie z JWT
- DTO i walidacja

### 💬 Sprint 3: Głosowanie i komentarze
- Obsługa głosów i komentarzy
- Liczenie popularności

### 🔒 Sprint 4: Panel administratora
- Usuwanie komentarzy, banowanie użytkowników
- Przenoszenie tematów

### 🌐 Sprint 5: Integracja z API
- Pobieranie tematów z DeepSeek

### 🎨 Sprint 6: Frontend + UI/UX
- Bańki tematyczne
- Responsywny layout
- Filtrowanie, wyszukiwarka

## 👨‍💻 Autor

Kontakt: hetko.mateusz@gmail.com

GitHub: github.com/eloomati

