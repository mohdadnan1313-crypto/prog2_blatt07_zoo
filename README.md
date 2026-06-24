# Zoo Management System - Blatt 07

Dieses Projekt implementiert eine Typhierarchie für Tiere unter Verwendung von Java *sealed types* und *records*, generischen Gehegen mit Typgrenzen, systematisches Logging mit `java.util.logging` und Datenabfragen mittels der Java **Stream-API**.

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
