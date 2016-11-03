# Todo Liste

## Buglist
- Firefox 45.2.0 auf Debian 8 rendert die Seite falsch (Jannik)

## Offene Punkte, Vorschläge und Diskussion

### Hosting an der Uni zu Köln
- von RRZK die Domain tiwoli(j|app|web).spinfo.uni-koeln.de auf unsere VM mappen
- von LetsEncrypt ein SSL-Zertifikat für die Domain holen
- eine (productive) Instanz von TiwoliJ hosten

### Pagination und verbesserte Navigation/Suche fürs Backend
- derzeit werden alle DB-Objekte auf einer Seite angezeigt, die entsprechend lang ist
- wenn man ein bestimmtes Zitat suchen will, muss man bisher die Browsersuche nutzen, das kann man einfacher übers Backend löschen

### Direktlink auf alternative Locales desselben Zitats
- Direkt-Switch zwischen Zitaten, wenn es direkte Übersetzungen gibt (bspw. Balzac, 1. Januar, "Eugénie Grandet")
> Phil: Kann ich machen, hätte aber gern eine Rückmeldung dazu, wo das ins Interface sollte ums nicht zu überladen.

### Hervorheben von Zitaten mit mehreren Locales
- im Interesse des formalisierten Erschließens von in den Daten versteckten Erkenntnissen,
- sollen Zitate mit mehreren Locales auf einen Blick ersichtlich sein
- sollen differenzen zwischen Datumsangaben zwischen "gleichen" Zitaten errechnet und angezeigt werden
- und mehr!

### Weitere DB-Anpassungen
- optionale Felder für *year* und *time*, mindestens für autoChirp Export
- Felder für die Moderation der DB-Records:
	- *final* oder *locked*
	- *curated*

### Export-Funktionalität
- Export als "GoogleDocs Style Import" für autoChirp
- Export-Formate zur Aktualisierung der Android/iOS-App
- Exporte filtern, bspw.:
	- Exportieren von quoteLocales nur, wenn "finalisiert"
	- Exportieren von einer Sprache
	- Exportieren von Datums-Ranges

### Spanische Lokalisierung des Interface
- im Projekt unter src/main/resources/locales liegen die verschiedenen Lokalisierungen der Applikation, Spanisch fehlt
- sobald alle Strings in Deutsch/Englisch vorhanden sind bräuchten wir einmal einen Spanischsprechenden zum Übersetzen

### Community Integration
- einen *Send in*- oder *Suggest new Quote*-Button im Frontend inkl. Moderationsinterface
> Phil: Würde ich erstmal weit zurückstellen und stattdessen ein eMail-Formular o.ä. anbieten.

## Abgeschlossenes

### Automatischer Wikidata-Import
- beim Anlegen neuer Objekte in der DB Stammdaten aus Wikidata/-pedia importieren
- beim Anlegen neuer Autoren Bilder aus Wikidata mirrorn

### Datenbanken-Anpassungen
- *volumes* soll *works* heißen
- *citings* soll *quotes* heißen
- Volltextlink teils mit Metadaten (seitenangabe) als *meta* Feld
- Sprachentabelle für Übersetzungen/Lokalisierung:

### Interface Updates
- *home*-Seite, mit Links zu:
	- zufälligem Zitat des Tages
	- zufälligem Zitat
	- Zitat zu spezifiziertem Datum
- Navigations-Interface auf Karteikarten-Darstellung (Frontend):
	- zwischen Tagen
	- zwischen Zitaten des Tages

### Bilder skalieren beim Import
- importierte Bilder auf eine maximale Größe (500px höhe) skalieren

### Import bestehender Daten
- GDocs Spreadsheet soll in die DB, Umweg über TSV sinnvoll
- TSV-Importer für jedes Datenblatt/Sheet
- Minimale Daten extrahieren, sonstiges aus Wikidata/-pedia ergänzen
