# Todo Liste

## Buglist
- Firefox 45.2.0 auf Debian 8 rendert die Seite falsch (Jannik)
- Wechseln zwischen Sprachen führt nicht immer zu dem richtigen Datum, z.B. von Deutsch 16-11-2016 (1) zu English endet im 15.11. - oder kommt man beim Sprachenwechsel immer am heutigen Tag raus? Finde ich nicht so ganz intuitiv, vielleicht dann lieber ein genereller "Heute / Today" Button der immer da ist? (Jannik)


## Offene Punkte, Vorschläge und Diskussion

### Import-Benutzermeh
- Wenn man beim Import die falsche Lokalisierung angibt, werden gnadenlos alle Autoren mit der falschen Lokalisierung importiert. Hier täte ein Panic-Knopf mit Rollback gut (ja, ich weiß, hat auch Nachteile, aber ich habe mir gerade die ganze DB verhunzt und muss jetzt alles noch einmal neu importieren. (jh)
 
### Ueberschrift der tiwoli Seite
- in der Ueberschrift würde ich das "2016" also das Jahr generell raus lassen, stattdessen das Datum in Schriftform anzeigen, also deutsch: 11. November, english: 11th of November etc.?

### Hosting an der Uni zu Köln
- von RRZK die Domain tiwoli(j|app|web).spinfo.uni-koeln.de auf unsere VM mappen
- von LetsEncrypt ein SSL-Zertifikat für die Domain holen
- eine (productive) Instanz von TiwoliJ hosten
- TivoliJ Compilation als WAR auf Jetty anpassen

### TiwoliChirp
- TEXT deutsch: “Der DD. MONATAUSGESCHRIEBEN in der Weltliteratur: AUTORVORNAME AUTORNACHNAME: WERKTITEL. #tiwoli LINK zur Seite” + angehangenes Bild (verbraucht keinen Platz mehr in den 140 Zeichen, Rest müsste eigentlich immer passen, zur Not Titel mit … abkürzen).
- BILD Screenshots macht sich eigentlich ganz hübsch auf Twitter → Sind die irgendwie automatisch erzeugbar? Alternativ (aber nicht so schön): Einfach das Autorbild anhängen.
- SPRACHE: Andere Sprachen, andere Twitter-Accounts? (JH) würde dafür plädieren. Wir können ja mal mit deutsch anfangen und dann sehen, wer uns so folgt.
- UHRZEIT: In manchen Zitatschnipseln steht die Uhrzeit mit im Text, allerdings nicht analysiert, d.h. da müsste HeidelTime ein zweites Mal drüber. Die Uhrzeit könnte man für das Scheduling gebrauchen.
- VERTEILUNG: Zitate mit Uhrzeit sind ja festgelegt, die anderen sollten über den Tag verteilt werden. Hat dazu Vorschläge?

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

### Export-Funktionalität
- Export als "GoogleDocs Style Import" für autoChirp
- Export-Formate zur Aktualisierung der Android/iOS-App
- Exporte filtern, bspw.:
	- Exportieren von quoteLocales nur, wenn "finalisiert"
	- Exportieren von einer Sprache
	- Exportieren von Datums-Ranges

### Spanische Lokalisierung des Interface
- im Projekt unter src/main/resources/locales liegen die verschiedenen Lokalisierungen der Applikation, Spanisch fehlt
- sobald alle Strings in Deutsch/Englisch vorhanden sind bräuchten wir einmal einen Spanischsprechenden zum Übersetzen --> JH: Kann Francisco fragen (Chilene, der bei uns arbeitet)

### Community Integration
- einen *Send in*- oder *Suggest new Quote*-Button im Frontend inkl. Moderationsinterface
> Phil: Würde ich erstmal weit zurückstellen und stattdessen ein eMail-Formular o.ä. anbieten.


## Abgeschlossenes

### Bugs
- (nicht reproduzierbar) Versehentlicher doppelter Import verdoppelt nicht alle, aber einige Zitate (JH)
- Import-format des Meta-Feldes auf "<Url> <dash-separated-string>"

### Weitere DB-Anpassungen
- optionale Felder für *year* und *time*, mindestens für autoChirp Export
- Felder für die Moderation der DB-Records:
	- *final* oder *locked*
	- *curated*

### Import
- Fehler beim Import mit Feldangabe (Zeile|Spalte) an den Nutzer weiterleiten
> Nicht (i|j), $Zeile; done.

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
