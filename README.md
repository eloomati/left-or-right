# 🧠 LeftOrRight ~ LOR

Aplikacja społecznościowa do binarnej debaty — użytkownicy wybierają stronę (lewą/prawą) w różnych tematach, dzielą się opiniami, przeglądają argumenty, a najpopularniejsze tematy wizualizowane są w formie „baniek mydlanych”.

---

## 📌 Spis Treści

- [Opis projektu](#-opis-projektu)
- [Funkcjonalności](#-funkcjonalności)
- [Technologie](#-technologie)
- [Instalacja](#-instalacja)
- [Schemat bazy danych](#-schemat-bazy-danych)
- [API](#-api)
- [Diagram ERD](#-diagram-erd)
- [Rozwój i TODO](#-rozwój-i-todo)
- [Autor](#-autor)

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
- SLF4J (logowanie)
- Swagger (planowane)
- Docker *(planowane)*
- CI/CD *(planowane)*
- Flyway (migracje bazy danych)

---

## 🚀 Instalacja

```bash
git clone https://github.com/eloomati/left-or-right.git
cd left-or-right
```
🛠️ Inicjalizacja aplikacji
1. Zainstaluj Podmana.
2. Uruchom wszystkie usługi i zainicjalizuj bazę jednym poleceniem:
```bash
    ./scripts/setup.sh
```
Podczas uruchamiania skrypt poprosi o:
- hasło administratora PostgreSQL (postgres)
- nazwę bazy danych (ENTER = lor_test)
- hasło dla użytkownika migracji (lor_flyway)
- hasło dla użytkownika aplikacji (lor_app)

📌 Uwagi:
- Użytkownik migracji (Flyway) służy wyłącznie do wykonywania migracji (zmian w strukturze bazy danych).
- Użytkownik aplikacji jest używany przez aplikację w codziennej pracy z danymi.

🔐 Jak uzyskać hasło aplikacji Gmail

1. Zaloguj się na swoje konto Google.
2. Wejdź na: https://myaccount.google.com/security
3. Włącz weryfikację dwuetapową (2FA), jeśli jeszcze nie jest włączona.
4. Po jej aktywacji przejdź do sekcji Hasła aplikacji.
5. Wybierz:
- Aplikacja: Poczta
- Urządzenie: Inne → wpisz np. SpringBoot
- Kliknij "Generuj" – skopiuj 16-znakowe hasło.

🛠️ Uzyskanie klucza JWT

1. Wygeneruj klucz JWT poniższym poleceniem:
```
openssl rand -base64 32
```
- Klucz JWT (sekretny klucz) służy do podpisywania i weryfikacji tokenów JWT przy logowaniu użytkowników.

🛠️ Konfiguracja aplikacji

1. Utwórz plik .env na podstawie wzoru:
- Skopiuj plik env.example do .env w głównym katalogu projektu:
```
cp env.example .env
```
- Następnie uzupełnij plik .env swoimi danymi dostępowymi (hasła do bazy, klucz JWT, dane SMTP).

2. Uruchamianie aplikacji w IntelliJ IDEA:
- Otwórz konfigurację uruchomienia (Run/Debug Configurations).
- Zaznacz opcję Enable env file.
- Wskaż plik .env (jeśli nie widzisz plików ukrytych, użyj skrótu ⌘ + Shift + . lub wpisz .env ręcznie w polu ścieżki).
-  Zapisz konfigurację i uruchom aplikację.

3. Uruchamianie aplikacji w terminalu:
- Korzystaj z Mavena, uruchom aplikację poleceniem:
```
mvn spring-boot:run
```
- Upewnij się, że plik .env znajduje się w katalogu głównym projektu
- Jeśli zmienne nie są ładowane automatycznie, możesz załadować je ręcznie:
```
export $(grep -v '^#' .env | xargs) && mvn spring-boot:run
```

## 🧠 Schemat bazy danych

### Encje (tabele):
- users
- category
- user_categories (relacja many-to-many: użytkownik ↔ kategorie)
- topic
- vote
- comment
- followed_topic
- proposed_topic
- banned_user
- notification
- tag
- topic_tags (relacja many-to-many: temat ↔ tag)
- report

### Typy relacji:
- OneToMany: users ↔ comment, topic ↔ vote, users ↔ topic (created_by), users ↔ proposed_topic (proposed_by), users ↔ banned_user, users ↔ notification, users ↔ report (reporter_id)
- ManyToOne: topic ↔ category, proposed_topic ↔ category, comment ↔ topic, vote ↔ topic, followed_topic ↔ topic, report ↔ topic, report ↔ comment
- ManyToMany: users ↔ category (user_categories), topic ↔ tag (topic_tags)

## 📊 Diagram ERD

![Diagram ERD](docs/erd.png)

## 🧪 API
(W trakcie tworzenia – dodaj opis endpointów, np. login, rejestracja, dodaj komentarz, głosuj, pobierz tematy itd.)

1. Curl do rejestracji użytkownika:
```
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "confirmEmail": "test@example.com",
    "password": "StrongP@ssw0rd!",
    "confirmPassword": "StrongP@ssw0rd!",
    "termsAccepted": true
  }'
```
2. Curl do zalogowania użytkownika
```
   curl -X POST http://localhost:8080/api/users/login \
   -H "Content-Type: application/json" \
   -d '{"username": "username", "password": "userpassword"}'
```

3. Curl do testowania zalogowania
```
   curl -H "Authorization: Bearer secret_token" http://localhost:8080/api/test
```
4. Curl do testowania dodawania tematów

```
   curl -X POST http://localhost:8080/api/v1/topic-requests \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Przykładowy tytuł artykułu",
    "desctription": "To jest przykładowy opis artykułu, który ma więcej niż 50 znaków, aby spełnić walidację.",
    "countryId": 1,
    "continentId": 2,
    "categoryId": 1,
    "tagIds": [1, 2]
  }'
```
5. Utworzenie kategorii
```
    curl -X POST http://localhost:8080/api/categories/create \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer <TWÓJ_TOKEN_JWT>" \
      -d '{"name": "Nowa kategoria"}'
```

6. Pobranie wszystkich kategorii
```
    curl http://localhost:8080/api/categories \
      -H "Authorization: Bearer <TWÓJ_TOKEN_JWT>"
```

7. Pobranie kategorii po ID
```
    curl http://localhost:8080/api/categories/1 \
      -H "Authorization: Bearer <TWÓJ_TOKEN_JWT>"
```

8. Aktualizacja kategorii
```
    curl -X PUT http://localhost:8080/api/categories/1 \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer <TWÓJ_TOKEN_JWT>" \
      -d '{"name": "Zmieniona nazwa"}'
```

9. Usunięcie kategorii
```
    curl -X DELETE http://localhost:8080/api/categories/1 \
      -H "Authorization: Bearer <TWÓJ_TOKEN_JWT>"
```

10. Utworzenie taga
```
curl -X POST http://localhost:8080/api/tags/create \
-H "Content-Type: application/json" \
-H "Authorization: Bearer TWÓJ_TOKEN_JWT" \
-d '{"name": "exampleTag"}'
```
11. Pobranie wszystkich tagów
```
curl http://localhost:8080/api/tags \
-H "Authorization: Bearer TWÓJ_TOKEN_JWT"
```
12. Pobranie taga po ID (np. 1)
```
curl http://localhost:8080/api/tags/1 \
-H "Authorization: Bearer TWÓJ_TOKEN_JWT"
```
13. Aktualizacja taga (np. 1)
```
curl -X PUT http://localhost:8080/api/tags/1 \
-H "Content-Type: application/json" \
-H "Authorization: Bearer TWÓJ_TOKEN_JWT" \
-d '{"name": "updatedTag"}'
```
14. Usunięcie taga (np. 1)
```
curl -X DELETE http://localhost:8080/api/tags/1 \
-H "Authorization: Bearer TWÓJ_TOKEN_JWT"
```
15.  Utworzenie komentarza do tematu (np. 5)
```
curl -X POST http://localhost:8080/api/comments \
-H "Content-Type: application/json" \
-H "Authorization: Bearer TWÓJ_TOKEN_JWT" \
-d '{"topicId":5,"side":"LEFT","content":"To jest komentarz"}'
```
16. Pobranie taga po ID (np. 5)
```
curl http://localhost:8080/api/comments/5\
-H "Authorization: Bearer TWÓJ_TOKEN_JWT"
```
17. Pobieranie wszystkich komentarzy dla tematu (np. 5)
```
curl http://localhost:8080/api/comments/topic/5
-H "Authorization: Bearer TWÓJ_TOKEN_JWT"
```
17. Aktualizacja komentarza (np. 5)
```
curl -X PUT http://localhost:8080/api/comments/5 \
-H "Authorization: Bearer TWÓJ_TOKEN_JWT"
-H "Content-Type: application/json" \
-d '{"topicId":1,"side":"con","content":"Zaktualizowany komentarz"}'
```
18. Usunięcie komentarza (np. 5)
```
curl -X DELETE curl http://localhost:8080/api/comments/5 \
-H "Authorization: Bearer TWÓJ_TOKEN_JWT"
```
19. Oddanie głosu na temat
```
curl -X POST "http://localhost:8080/api/votes/vote?userId=12&topicId=6&side=RIGHT"
```
20. Wycofanie głosu
```
curl -X POST "http://localhost:8080/api/votes/unvote?userId=12&topicId=6"
```
21. Pobranie liczby głosów na temat
```
curl -X GET "http://localhost:8080/api/votes/count?topicId=6"
```
22. Zliczanie głosów po stronie
```
curl -X GET "http://localhost:8080/api/votes/side-count?topicId=6&side=LEFT"
```
23. Pobranie głosów użytkownika
```
curl -X GET "http://localhost:8080/api/votes/user-votes?userId=12"
```
24. Usunięcie wszystkich głosów użytkownika
```
curl -X DELETE "http://localhost:8080/api/votes/user/1/all"
```
25. Najpopularniejsze tematy (top 5)
```
curl -X GET "http://localhost:8080/api/votes/popular?limit=5"
```
26. Aktualizacja głosu użytkownika
```
curl -X PUT "http://localhost:8080/api/votes/update?userId=12&topicId=6&newSide=RIGHT"
```


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

- Kontakt: hetko.mateusz@gmail.com 
- GitHub: github.com/eloomati

