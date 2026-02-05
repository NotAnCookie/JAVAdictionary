# Dictionary App

Aplikacja słownikowa umożliwiająca pobieranie definicji słów
z zewnętrznych źródeł słownikowych.

## Proponowane Funkcjonalności

1. **Wyszukiwanie definicji słowa**  
   Użytkownik może wyszukać znaczenie wybranego słowa, wpisując je w aplikacji.

2. **Wyświetlanie definicji słowa**  
   Aplikacja prezentuje użytkownikowi definicję wyszukanego słowa w czytelnej formie.

3. **Wyświetlanie listy synonimów**  
   Jeżeli są dostępne, aplikacja wyświetla synonimy powiązane z wyszukiwanym słowem.

4. **Wybór źródła słownika**  
   Użytkownik może wybrać słownik, z którego mają zostać pobrane definicje.

5. **Porównywanie definicji z różnych słowników**  
   Użytkownik może sprawdzić definicje tego samego słowa pochodzące z różnych źródeł.

6. **Historia wyszukiwań**
   Aplikacja zapisuje historię wyszukiwanych słów, umożliwiając szybki powrót do poprzednich zapytań.

7. **Ulubione słowa**
   Użytkownik może zapisywać wybrane słowa jako ulubione, aby mieć do nich szybki dostęp.

8. **Obsługa wielu języków**  
   Użytkownik może wybrać język słownika, jeśli dane źródło na to pozwala.

9. **Podpowiedzi podczas wpisywania słowa**  
   Podczas wpisywania słowa aplikacja podpowiada możliwe dopasowania lub poprawną pisownię.

10. **Użytkownik może klikać słowa w definicji, aby przejść do ich definicji**

## Technologie

### Backend

- Java 25
- Spring Boot 3.1.5
- REST API
- Maven (zarządzanie zależnościami)

### Frontend

- Android 8+
- Java 17
- Retrofit 2.9.0 - komunikacja z REST API

## Uruchomienie

1. Backend: uruchom klasę `DictionaryApplication` w IntelliJ lub przez Maven:
   ```bash
   mvn spring-boot:run
2. Domyślny port backendu: 8080
3. Test endpointów:
- `/dictionary/{word}` – wyszukiwanie definicji
- `/dictionary/{word}?provider=<nazwa_providera>` – wybór źródła słownika
- `/dictionary/dev/raw/{word}` – RAW JSON (tylko profil `dev`)

### Konfiguracja Klienta:

Aplikacja Android łączy się z backendem pod adresem:
- Emulator: `http://10.0.2.2:8080/`
- Fizyczne urządzenie: Adres IP hosta.


