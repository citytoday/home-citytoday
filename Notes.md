Notes
=====


### Data Record Extractor

1. Genera una collezione di url da crawlare
2. per ogni url estrae la lista dei data record
3. filtra i data record duplicati
4. salva i data record su kafka

Questa componente potrebbe utilizzare akka persistence per mantenere la lista
dei data record gia' visitati.

### Data Record Enriched

Estrae i dettagli dalla pagina del record e aggiunge tutti i campi descritti nella 
case class sotto definita: 

```scala
  case class HomeRecord(
    ref: RawHomeRecord,
    rooms: Int,
    square: Int,
    floor: Short,
    price: Double,
    address: String,
    latLon: (Double, Double),
    advertiser: String,
    energeticClass: Char,
    undergrounds: Seq[String],
    district: String,
    expenses: Int)
```


Il 1 scrive i record su kafka con una coda compattata in modo che elimini i duplicati.
Il 2 legge dalla coda con un group id e processa i record che salva in una altra coda.
Utilizzando una coda compattata non dovremmo avere il problema della gestione dei duplicati,
in quanto questi vengono eliminati da kafka stesso.

## Note

1. l'estrattore deve essere eseguito 1 volta al giorno a mezzanotte
2. deve estrarre una url ogni random secondi in basa a una funzione
3. Per estrarre le coordinate google maps e open street map, azure maps
4. https://github.com/scredis/scredis



