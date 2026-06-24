# Zoo Management System - Blatt 07

Dieses Projekt implementiert eine Typhierarchie für Tiere unter Verwendung von Java *sealed types* und *records*, generischen Gehegen mit Typgrenzen, systematisches Logging mit `java.util.logging` und Datenabfragen mittels der Java **Stream-API**.

---

## Begründung der Entwurfsentscheidungen (Aufgabe 1)

### 1. Wahl der inneren Datenstruktur für Gehege (Aufgabe 1.2)
* **Datenstruktur**: `Set<T>` (implementiert als `LinkedHashSet<T>`).
* **Begründung**: 
  * Ein `Set` garantiert von Natur aus, dass jedes Element nur einmal vorkommen kann (Duplikatfreiheit). Dies verhindert, dass dasselbe Tier-Objekt mehrfach in das Gehege gelegt wird.
  * `LinkedHashSet` wurde gewählt, weil es die Einfügereihenfolge beibehält. Das erleichtert das Testen, Debuggen und die Anzeige der Bewohner im Vergleich zu einer ungeordneten `HashSet`, während es gleichzeitig die \(O(1)\)-Zeiteffizienz für Hinzufügen, Entfernen und Suchen beibehält.

### 2. Parameter- und Rückgabetypen der Zoo-Methoden (Aufgabe 1.4)
* **`addEnclosure(Enclosure<? extends Animal> enclosure)`**:
  * *Parameter*: `Enclosure<? extends Animal>` (Verwendung von Upper-Bounded Wildcards) erlaubt die Übergabe beliebiger konkreter Gehegetypen wie `Aquarium` (das `Enclosure<Fish>` erweitert) oder `CatHouse<Tiger>`, was mit einem starren `Enclosure<Animal>` wegen der Invarianz von Generics in Java nicht möglich wäre.
  * *Rückgabe*: `boolean` zeigt dem Aufrufer an, ob das Gehege erfolgreich hinzugefügt wurde (schlägt z. B. bei Namensdopplung fehl).
* **`getEnclosures()`**:
  * *Rückgabe*: `List<Enclosure<? extends Animal>>`, gekapselt mit `Collections.unmodifiableList()`. Dies schützt den internen Zustand der Zoo-Klasse vor unbefugter Manipulation von außen (Read-Only Ansicht).
* **`findEnclosureByName(String name)`**:
  * *Rückgabe*: `Optional<Enclosure<? extends Animal>>`. Macht explizit deutlich, dass ein Gehege eventuell nicht existiert. Dadurch wird der Aufrufer gezwungen, das Fehlen zu behandeln und potenzielle `NullPointerException`s werden vermieden.
* **`getAnimalsByPredicate(Predicate<Animal> predicate)`**:
  * *Parameter*: `Predicate<Animal>` ermöglicht es dem Aufrufer, hochgradig flexible Such- und Filterkriterien als Lambda-Ausdruck (z. B. Namenslänge oder spezifische Artmerkmale) zu übergeben.
* **`countAnimalsByType()`**:
  * *Rückgabe*: `Map<Class<? extends Animal>, Long>` bildet jeden konkreten Tiertyp (`Class<? extends Animal>`) auf die entsprechende Anzahl der Tiere ab, was für Auswertungen ideal ist.

---

## Antworten zu Aufgabe 3: Reflektion

### 1. Generics
* **Vermeidung von Fehlern zur Compile-Zeit**:  
  Generics erlauben es uns, den Typ der zulässigen Bewohner in einem Gehege (`Enclosure<T extends Animal>`) festzulegen. Der Java-Compiler stellt dadurch statisch sicher, dass nur kompatible Tierobjekte in ein Gehege gelegt werden können. Ein Entwickler kann somit nicht versehentlich ein Tier in ein inkompatibles Gehege einfügen.
* **Konkretes Beispiel aus der Implementierung**:  
  Wenn ein `CatHouse<Tiger>` instanziiert wird:
  ```java
  CatHouse<Tiger> tigerHouse = new CatHouse<>("Tigers", Tiger.class);
  ```
  verhindert der Java-Typchecker das Hinzufügen eines Löwen zur Compile-Zeit:
  ```java
  tigerHouse.add(new Lion("Simba")); // COMPILE ERROR: Enclosure.add(Tiger) cannot be applied to Lion
  ```

### 2. Logging
* **Sinnvoller als Ausgaben mit `System.out.println`**:  
  * **Granularität und Filterung**: Logging ermöglicht es, Log-Meldungen nach Wichtigkeit (Log-Levels) zu kategorisieren. Im Produktionsbetrieb können Debugging-Details (`FINE`) ausgeblendet werden, während sie bei der Entwicklung aktiv sind – ganz ohne Codeänderungen.
  * **Ausgabeziele**: Logs können flexibel an verschiedene Handler weitergeleitet werden (z. B. in Logdateien, Datenbanken oder Netzwerk-Streams), anstatt starr auf die Standardausgabe zu schreiben.
  * **Strukturierte Metadaten**: Log-Meldungen enthalten automatisch wertvolle Zusatzinformationen wie präzise Zeitstempel, Thread-IDs sowie Klassen- und Methodennamen.
* **Verwendung der Log-Level**:  
  * `INFO`: Zur Dokumentation regulärer Geschäftsaktionen (z. B. Start einer Methode wie `admitAnimal` mit ihren Parametern).
  * `WARNING`: Wenn ein gesuchtes Objekt nicht existiert (z. B. wenn `findEnclosureByName` ein leeres `Optional` zurückgibt, da das Gehege nicht existiert).
  * `SEVERE`: Bei schwerwiegenden fachlichen Inkonsistenzen, die den Zustand des Systems gefährden (z. B. doppelte Gehegenamen, Versuche ein Tier in zwei Gehegen gleichzeitig unterzubringen, oder Typinkompatibilitäten bei dynamischen Aktionen).

### 3. Streams
* **Vorteile (wo sie geholfen haben)**:  
  Streams machen das Durchsuchen, Filtern und Transformieren verschachtelter Strukturen extrem kompakt. Das flachklopfen aller Gehegebewohner in eine einzige Liste mittels `flatMap`:
  ```java
  enclosures.stream().flatMap(e -> e.getInhabitants().stream()).toList()
  ```
  ist weitaus lesbarer als verschachtelte Schleifen mit temporären Hilfslisten. Auch die Gruppierung und Zählung von Tiertypen über `groupingBy` ist sehr elegant.
* **Nachteile / Unübersichtlichkeit**:  
  * **Typ-Inferenz und Wildcards**: Bei gemischten Listen mit Wildcards (z. B. `List<Enclosure<? extends Animal>>`) stößt Java's Typinferenz an ihre Grenzen. Um Capture-Fehler beim Kompilieren zu vermeiden, mussten explizite `.map(a -> (Animal) a)` Casts eingebaut werden. Das bläht die Stream-Pipeline syntaktisch auf.
  * **Fehlerdiagnose**: Das Debuggen (z. B. Setzen von Breakpoints) innerhalb von komplexen Stream-Pipelines und Lambdas ist im Vergleich zu klassischen for-Schleifen unhandlicher.

---

## Ausführen des Projekts

### Voraussetzungen
* Java JDK 21 oder neuer (wurde erfolgreich mit Java 25 getestet)

### Kompilieren
Führen Sie folgenden Befehl im Projektverzeichnis `zoo-project` aus:
```powershell
javac -d bin src/zoo/Main.java src/zoo/Zoo.java src/zoo/animal/*.java src/zoo/enclosure/*.java
```

### Ausführen der Demo
```powershell
java -cp bin zoo.Main
```
