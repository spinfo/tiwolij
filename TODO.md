# ToDo List

## [done] Datanbanken-Anpassungen
- [done] *volumes* soll *works* heißen
- [done] *citings* soll *quotes* heißen
- [done] volltextlink teils mit metadaten: seitenangabe
- [done] sprachentabelle für übersetzungen/lokalisierung (translations):
	- mehrere wiki-urls
	- namen von autoren
	- namen von werken

## Interface Updates
- eine *home*-seite, mit links zu:
	- zufälligem zitat des tages
	- zufälligem zitat
	- zitat zu spezifiziertem datum
- weiterhin navi-interface auf *karteikarten*-darstellung (frontend):
	- zwischen zitaten des tages
	- zwischen tagen, ggf. vor/nach allen zitaten des tages letzter/nächster tag

## Community Integration
- einen *send in* oder *suggest new quote* button im frontend
- rattenschwanz moderationsinterface
- dantenbankfeld *freigeschaltet* (curated oder locked)

## Import-Funktionalität

### Automatischer Wikidata-Import
- beim anlegen neuer objekte in der db, wikiartikel/language-links crawl-farmen
- beim anlegen neuer authoren bilder mirrorn

### Import bestehender Daten
- gdocs spreadsheet soll in die db, umweg über csv sinnvoll
- csv-importer für jedes datenblatt/sheet
- minimale daten extrahieren, sonstiges durch automatisierung ergänzen (farmen)

## Export-Funktionalität
- alle db-daten müssen in autochirp-gerechtem format exportierbar sein
- weiterhin export-formate zur aktualisierung der android/ios-app-internen db
