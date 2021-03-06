# Todo Liste

## Buglist
- Firefox 45.2.0 auf Debian 8 rendert die Seite falsch (Jannik)

## Offene Punkte, Vorschläge und Diskussion

### Issues statt ToDo
- Oder spricht was dagegen?

### Neues Importformat 
- Format: WerkWikiDataID - Schedule/Date - Schedule/Time - Lang - Text - Source 
- Wenn Jahr in Date angegeben --> speichern

### Pagination und verbesserte Navigation/Suche fürs Backend
- derzeit werden alle DB-Objekte auf einer Seite angezeigt, die entsprechend lang ist
- wenn man ein bestimmtes Zitat suchen will, muss man bisher die Browsersuche nutzen, das kann man einfacher übers Backend lösen

### Export-Funktionalität
- Export-Formate zur Aktualisierung der Android/iOS-App

### Spanische Lokalisierung des Interface
- im Projekt unter src/main/resources/locales liegen die verschiedenen Lokalisierungen der Applikation, Spanisch fehlt
- sobald alle Strings in Deutsch/Englisch vorhanden sind bräuchten wir einmal einen Spanischsprechenden zum Übersetzen
	- JH: Kann Francisco fragen (Chilene, der bei uns arbeitet)

### Transaction History / Rollbacks
- Wenn man beim Import die falsche Lokalisierung angibt, werden gnadenlos alle Autoren mit der falschen Lokalisierung importiert. Hier täte ein Panic-Knopf mit Rollback gut (ja, ich weiß, hat auch Nachteile, aber ich habe mir gerade die ganze DB verhunzt und muss jetzt alles noch einmal neu importieren. (jh)
	- PS: Haben kurz drüber geredet, ist nahezu unschaffbar ohne Transaction-History etc. - ich denke, da sollten die Kuratoren anfangs Vorsicht walten lassen und später können wir aushandeln, ob dieses Feature an eine etwaige Nutzerverwaltung gehängt wird oder seperat implementiert werden soll; marke es erstmal als medium-prio.

### Hervorheben von Zitaten mit mehreren Locales
- im Interesse des formalisierten Erschließens von in den Daten versteckten Erkenntnissen,
- sollen Zitate mit mehreren Locales auf einen Blick ersichtlich sein
- sollen differenzen zwischen Datumsangaben zwischen "gleichen" Zitaten errechnet und angezeigt werden
- und mehr!

### Community Integration
- einen *Send in*- oder *Suggest new Quote*-Button im Frontend inkl. Moderationsinterface
	- PS: Würde ich erstmal weit zurückstellen und stattdessen ein eMail-Formular o.ä. anbieten.
	- JH: Jo, das wäre was für ein Forschungsgefördertes Projekt.

### TwLiterature-Specials
- JH: wir könnten ein Weihnachtsspecial mit "A christmas carol" vorbereiten, wo zwar die Daten 25./26.12. nur implizit vorkommen, aber eine Menge Uhrzeiten genannt werden. Lustig v.a., weil Scrooge die Nacht gleich ein paarmal erlebt. Mal schauen, ob ich da eine Timeline draus gebaut bekomme. Die Objekte müssten evtl. in der DB durch ein zusätzliches Flag gekennzeichnet werden, da sie keine expliziten Datumsangaben enthalten.
- JH: Gleiches überlege ich mir für den 16. Juni / Bloomsday - Bei beidem Unterstützung gerne willkommen.

## Abgeschlossenes

### Header-Zeile anpassen
- Die Header-Zeile müsste an das neue Datumsformat angepasst werden - wie in Klasse TiwoliChirp (prefix + daySuffix+ delim  + month)
- PS: Auch Header-Zeile auf Flashcards angepasst

### Export-Funktionalität
- Export als "GoogleDocs Style Import" für autoChirp
- Export-Formate zur Aktualisierung der Android/iOS-App
- Exporte filtern, bspw.:
	- Exportieren von quoteLocales nur, wenn "finalisiert"
	- Exportieren von einer Sprache
	- Exportieren von Datums-Ranges

### Flashcard-Anpassungen
- line-breaks werden nicht mitgemacht und nicht mal ein whitespace zwischen absatzende und absatzanfang gesetzt (FF)
- PS: Absätze zu machen ist ziemlich untrivial; daher werden nun alle <br> durch ein whitespace ersetzt
- PS: Doch trivialer als gedacht; <br> auf Flashcards durch tatsächliche newlines ersetzt. Pls test!

### Bildattribution irreführend (FF)
- unter das Autorenbild den Namen des Autors setzen und dann einen Link auf die Wikimedia Commons (nicht die Copyright-Info)
	- denn das sieht in manchen Fällen so aus, als ob das der Abgebildete ist, etwa hier: https://tiwoli.spinfo.uni-koeln.de/view?id=341&lang=de

### TiwoliChirp / Export-Funktionalität
- TEXT deutsch: “Der DD. MONATAUSGESCHRIEBEN in der Weltliteratur: AUTORVORNAME AUTORNACHNAME: WERKTITEL. #tiwoli LINK zur Seite” + angehangenes Bild (FlashCard, verbraucht keinen Platz mehr in den 140 Zeichen, Rest müsste eigentlich immer passen, zur Not Titel mit … abkürzen).
- BILD FlashCard - automatisch erzeugter Screenshot mit etwas eingeschränkterer Optik.
- SPRACHE: Andere Sprachen, andere Twitter-Accounts? (JH) würde dafür plädieren. Wir können ja mal mit deutsch anfangen und dann sehen, wer uns so folgt.
- UHRZEIT: Zitate sind inzwischen mit HeidelTime geparst, Uhrzeiten und Jahreszahlen werden mit in die DB importiert.
- VERTEILUNG: Zitate mit Uhrzeit sind ja festgelegt, die anderen sollten über den Tag verteilt werden. JH macht mal einen Randomizer mit zufälliger Uhrzeit von 14-20 Uhr.
- Export als "GoogleDocs Style Import" für autoChirp
- Exporte filtern, bspw.:
	- Exportieren von quoteLocales nur, wenn "finalisiert"
	- Exportieren von einer Sprache
	- Exportieren von Datums-Ranges

### Datenbank-Anpassungen (FF)
- Erscheinungsjahr des Werkes als VARCHAR
- einige andere Felder etwas weniger strikt gestaltet
- die Anzeige einer **Version History** übers Web-Backend wär ganz cool, damit man den Überblick nicht verliert bzw. schnell Sachen backtracken kann

### Design-Kram (FF)
- wie bei zitaten üblich ganz oben im kasten die reihenfolge so wählen "name": "titel" ("quelle")
- statt "Referenz" würde ich "Quelle" schreiben, das ist genauer.

### Weitere DB-Anpassungen
- Felder für die Moderation der DB-Records:
	- *final* oder *locked*
	- *curated*
- Time-feld auf 8 Stellen erweitern
	- PS: Implementiert und können bearbeitet werden, haben aber bisher keine Effekte

### Bugs
- (nicht reproduzierbar) Versehentlicher doppelter Import verdoppelt nicht alle, aber einige Zitate (JH)
- Import-format des Meta-Feldes auf "\[Url] [dash-separated-string]"
- Bildreferenz auf flashcard setzen, Farben anpassen
- Wechseln zwischen Sprachen führt nicht immer zu dem richtigen Datum, z.B. von Deutsch 16-11-2016 (1) zu English endet im 15.11. - oder kommt man beim Sprachenwechsel immer am heutigen Tag raus? Finde ich nicht so ganz intuitiv, vielleicht dann lieber ein genereller "Heute / Today" Button der immer da ist? (Jannik)
> PS: Wenn ein Zitat angezeigt wird, werden nur noch die Sprach-Links eingeblendet, in denen das Zitat vorliegt; auf allen anderen Seiten werden alle Sprach-Links angeboten.

### Direktlink auf alternative Locales desselben Zitats
- Direkt-Switch zwischen Zitaten, wenn es direkte Übersetzungen gibt (bspw. Balzac, 1. Januar, "Eugénie Grandet")
> PS: Kann ich machen, hätte aber gern eine Rückmeldung dazu, wo das ins Interface sollte ums nicht zu überladen.
> PS: Ggf. nur auf Homepage alle möglichen Sprachen/Flaggen einblenden und pro Zitat nur die, für dies Übersetzungen gibt?!

### Überschrift der tiwoli Seite
- in der Ueberschrift würde ich das "2016" also das Jahr generell raus lassen, stattdessen das Datum in Schriftform anzeigen, also deutsch: 11. November, english: 11th of November etc.?

### Hosting an der Uni zu Köln
- von RRZK die Domain tiwoli(j|app|web).spinfo.uni-koeln.de auf unsere VM mappen
- von LetsEncrypt ein SSL-Zertifikat für die Domain holen
- eine (productive) Instanz von TiwoliJ hosten
- TivoliJ Compilation als WAR auf Jetty anpassen

### Flashcards
- Anzeige per Domain //image/flashcard?id=<authorId>&lang=<language>
- Baut Image mit Autorenbild, Corpus und Autor/Werk im TiwoliJ-Stil

### Weitere DB-Anpassungen
- optionale Felder für *year* und *time*, mindestens für autoChirp Export

### Import
- Fehler beim Import mit Feldangabe (Zeile|Spalte) an den Nutzer weiterleiten
 	- Nicht (i|j), $Zeile; done.

### Automatischer Wikidata-Import
- beim Anlegen neuer Objekte in der DB Stammdaten aus Wikidata/-pedia importieren
- beim Anlegen neuer Autoren Bilder aus Wikidata mirrorn

### Datenbanken-Anpassungen
- *volumes* soll *works* heißen
- *citings* soll *quotes* heißen
- Volltextlink teils mit Metadaten (seitenangabe) als *meta* Feld
- Sprachentabelle für Übersetzungen/Lokalisierung

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
