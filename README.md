# Laboratorium 5: Nieograniczony Problem Plecakowy (Java)

Projekt realizowany w ramach przedmiotu **Platformy Programistyczne .NET i Java**. Celem zadania było zapoznanie się z podstawami projektowania aplikacji w języku Java (system budowania Maven, Oracle OpenJDK) oraz implementacja algorytmu zachłannego rozwiązującego nieograniczony problem plecakowy.

Aplikacja została zaimplementowana w architekturze okienkowej z użyciem biblioteki **JavaFX** oraz niestandardowych powiadomień systemu (Toast).


1. **Zadanie 1 (Generator instancji problemu):**
   * Implementacja klasy `Problem` oraz klasy reprezentującej przedmioty.
   * Wykorzystanie generatora liczb pseudolosowych (`Random`) z obsługą ziarna (*seed*) do losowania wag i wartości przedmiotów z przedziału $<1, 10>$.
   * Przeciążenie metody `toString()` przy użyciu adnotacji `@Override` w celu czytelnej prezentacji wygenerowanej instancji.

2. **Zadanie 2 (Algorytm rozwiązujący - Solver):**
   * Implementacja metody `Solve(int capacity)` wykorzystującej algorytm aproksymacyjny (Dantzig, 1957).
   * Sortowanie przedmiotów według współczynnika opłacalności (stosunek wartości do wagi).
   * Zachłanne pakowanie najbardziej opłacalnych przedmiotów aż do wyczerpania maksymalnej pojemności plecaka.
   * Zwracanie wyniku w postaci dedykowanej klasy zawierającej listę przedmiotów, ich ilość, sumaryczną wagę oraz sumaryczną wartość.

## Technologia i Środowisko
* **Język:** Java
* **Środowisko:** Oracle OpenJDK
* **System budowania:** Maven
* **Interfejs graficzny:** JavaFX

## Uruchomienie projektu
Projekt wykorzystuje framework Maven. Aby go uruchomić, upewnij się, że masz skonfigurowane zmienne środowiskowe dla JDK, a następnie użyj polecenia:
```bash
mvn clean javafx:run
