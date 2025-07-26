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
