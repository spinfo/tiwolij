# ToDo List

## [done] Automatischer Wikidata-Import
- [done] beim anlegen neuer objekte in der db, wikiartikel/language-links crawl-farmen
- [done] beim anlegen neuer autoren bilder mirrorn

## [done] Datenbanken-Anpassungen
- [done] *volumes* soll *works* heißen
- [done] *citings* soll *quotes* heißen
- [done] volltextlink teils mit metadaten: seitenangabe
- [done] sprachentabelle für übersetzungen/lokalisierung (translations):
	- mehrere wiki-urls
	- namen von autoren
	- namen von werken
- [feature suggestion] direkt-switch zwischen zitaten, wenn es direkte übersetzungen gibt (beispiel: balzac, 1. januar, "eugénie grandet")

## [done] Interface Updates
- [done] eine *home*-seite, mit links zu:
	- zufälligem zitat des tages
	- zufälligem zitat
	- zitat zu spezifiziertem datum
- [done] weiterhin navi-interface auf *karteikarten*-darstellung (frontend):
	- zwischen zitaten des tages
	- zwischen tagen, ggf. vor/nach allen zitaten des tages letzter/nächster tag

## [done] Bilder skalieren beim Import
- die importierten Bilder sollten auf eine maximale Größe (500px höhe) skaliert werden, um die Datenbank nicht unnötig aufzublähen

## [done] Import bestehender Daten
- derzeit wartend auf bestehende CSV-2-Java Import Klasse
- gdocs spreadsheet soll in die db, umweg über csv sinnvoll
- csv-importer für jedes datenblatt/sheet
- minimale daten extrahieren, sonstiges durch automatisierung ergänzen (farmen)

## Export-Funktionalität
- alle db-daten müssen in autochirp-gerechtem format exportierbar sein
- weiterhin export-formate zur aktualisierung der android/ios-app-internen db
- Exportieren von quoteLocales nur, wenn "finalisiert" == true
- Export filterbar nach Sprache/etc.
- Export als "GoogleDocs Style Import" für autoChirp

## Weitere DB-Anpassungen
- Felder für Jahr und Uhrzeit, mindestens für autoChirp Export
- Feld für "final" auf quoteLocale

## my_little_buglist
- abgreifen der query-vars im frontend-controller in sinnvolle logik bringen
	- uc: home->quote for date xx-xx->db(null)->view(null(schedule=xx-xx)) ABER veiw(null(schedule=now)) FALSCH

## [low-prio] Community Integration
- einen *send in* oder *suggest new quote* button im frontend
- rattenschwanz moderationsinterface
- dantenbankfeld *freigeschaltet* (curated oder locked)
