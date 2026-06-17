# Laboratorium 6: Wielowątkowość i GUI w Java – Przetwarzanie Obrazów

Projekt realizowany w ramach przedmiotu **Platformy Programistyczne .NET i Java** (Politechnika Wrocławska). Celem zadania było stworzenie okienkowej aplikacji graficznej w technologii **JavaFX** służącej do zaawansowanej obróbki i edycji plików graficznych w formacie `.jpg`.

Aplikacja została zaprojektowana w oparciu o dostarczone wymagania biznesowe i historyjki użytkownika (User Stories).

### 1. Zarządzanie plikami i interfejs (Zadanie 1)
* **Ekran startowy:** Zgodny z zasadami UX, zawierający logo PWr, dane autora, podgląd obrazu oryginalnego oraz przetworzonego.
* **Wczytywanie obrazu:** Integracja z systemowym oknem wyboru plików z walidacją formatu `.jpg` oraz obsługą pamięci podręcznej.
* **Zapis obrazu:** Okno modalne do podawania nowej nazwy pliku z pełną walidacją długości znaków (3-100) i automatycznym zapisem do systemowego folderu *Obrazy*.
* **Komunikaty:** Dynamiczne powiadomienia typu **Toast** informujące użytkownika o sukcesie, błędach lub ostrzeżeniach (np. brak wybranej operacji).

### 2. Operacje Przetwarzania Obrazu (Zadanie 2)
* **Skalowanie:** Okno modalne umożliwiające zmianę szerokości i wysokości w pikselach (zakres 0-3000) z opcją przywrócenia oryginalnych wymiarów.
* **Obrót obrazu:** Szybkie przyciski z ikonami strzałek pozwalające na obracanie obrazu o 90 stopni w lewo oraz w prawo.
* **Negatyw:** Generowanie negatywu na kopii roboczej obrazu z powiadomieniem Toast o statusie operacji.
* **Progowanie:** Przetwarzanie obrazu z wykorzystaniem okna modalnego do wyboru progu numerycznego w zakresie 0-255.
* **Konturowanie:** Algorytm wyodrębniający kontury kształtów znajdujących się na wgranym zdjęciu.

## Technologia i Środowisko
* **Język:** Java
* **Framework GUI:** JavaFX
* **System budowania:** Maven

## Uruchomienie projektu
Aplikacja wykorzystuje system Maven. Aby uruchomić ją lokalnie, użyj polecenia:
```bash
mvn clean javafx:run
