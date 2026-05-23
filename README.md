# REST aplikacija za sportske centre i rezervacije

Aplikacija omogućuje upravljanje sportskim centrima, mjestima, sportovima, terenima i rezervacijama termina. Projekt se sastoji od Spring Boot REST API backenda i statičkog HTML/CSS/JavaScript frontenda.

Projekt je izrađen za treću domaću zadaću iz kolegija Informacijski sustavi. Glavni funkcionalni opseg je master-detail forma `SportskiCentar -> Teren`, uz šifrarnike `Sport` i `Mjesto`, validaciju rezervacija i testove za rezervacijski tok po controller, service, repository i integracijskoj razini.

## Sadržaj

- [Tehnologije](#tehnologije)
- [Preuzimanje projekta](#preuzimanje-projekta)
- [Pokrivenost DZ3 zahtjeva](#pokrivenost-dz3-zahtjeva)
- [Arhitektura aplikacije](#arhitektura-aplikacije)
- [Struktura projekta](#struktura-projekta)
- [Model baze podataka](#model-baze-podataka)
- [Preduvjeti](#preduvjeti)
- [Konfiguracija baze](#konfiguracija-baze)
- [Ogledni podaci](#ogledni-podaci)
- [Pokretanje backenda](#pokretanje-backenda)
- [Pokretanje frontenda](#pokretanje-frontenda)
- [Korištenje aplikacije](#korištenje-aplikacije)
- [API rute](#api-rute)
- [Primjeri API poziva](#primjeri-api-poziva)
- [Testiranje](#testiranje)
- [Build aplikacije](#build-aplikacije)
- [Dodatna dokumentacija](#dodatna-dokumentacija)
- [Česti problemi](#česti-problemi)

## Tehnologije

Backend:

- Java 21
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven Wrapper
- Lombok
- springdoc-openapi 3.0.3 / Swagger UI
- JUnit / Spring Boot Test

Frontend:

- HTML
- CSS
- JavaScript
- `serve` za lokalno posluživanje statičkih datoteka

## Preuzimanje projekta

Projekt se može preuzeti s GitHub repozitorija:

```bash
git clone https://github.com/josiprubcic/REST.git
cd REST
```

## Pokrivenost DZ3 zahtjeva

| Zahtjev | Implementacija u projektu |
| --- | --- |
| Master-detail forma | `SportskiCentar` kao master i `Teren` kao detail |
| Šifrarnici | `Sport` i `Mjesto` |
| REST API | Controlleri za mjesta, sportove, sportske centre, terene i rezervacije |
| CRUD operacije | CRUD za mjesta, sportove, sportske centre i terene |
| Dropdown za strani ključ | `Mjesto` dropdown kod sportskog centra i `Sport` dropdown kod terena |
| Pretraživanje i filtriranje | Pretraga mjesta, sportova i centara; filter centara po mjestu; filter terena po sportu |
| Složenija validacija | Provjera intervala rezervacije i preklapanja termina za isti teren |
| Testovi | `RezervacijaControllerTest`, `RezervacijaServiceTest`, `RezervacijaRepositoryTest`, `RezervacijaIntegrationTest` |
| API dokumentacija | Swagger UI na `/swagger-ui.html` |

## Arhitektura aplikacije

Aplikacija prati slojevitu organizaciju:

```text
Frontend HTML/CSS/JS
  -> REST controller
  -> Service
  -> Repository
  -> JPA/Hibernate
  -> PostgreSQL
```

Glavni backend paketi:

| Paket | Uloga |
| --- | --- |
| `config` | Konfiguracija aplikacije, uključujući CORS |
| `controller` | REST endpointi |
| `service` | Poslovna logika i validacije |
| `repository` | Pristup podacima preko Spring Data JPA |
| `model` | JPA entiteti |
| `dto` | Ulazni i izlazni DTO objekti |
| `mapper` | Pretvorba entiteta u DTO objekte |
| `exception` | Iznimke i globalna obrada grešaka |

Frontend se poslužuje lokalno na portu `5500`, a backend na portu `8080`. Frontend šalje HTTP zahtjeve na `http://localhost:8080/api`.

## Struktura projekta

```text
.
├── frontend/
│   ├── css/
│   │   ├── reservations.css
│   │   └── styles.css
│   ├── js/
│   │   ├── api.js
│   │   ├── rezervacije.js
│   │   ├── sportski-centar-detalji.js
│   │   └── sportski-centri.js
│   ├── index.html
│   ├── rezervacije.html
│   ├── sportski-centar-detalji.html
│   ├── sportski-centri.html
│   └── package.json
├── rest-api/
│   ├── src/main/java/hr/fer/rest_api/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── mapper/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   └── RestApiApplication.java
│   ├── src/main/resources/application.properties
│   ├── src/test/java/hr/fer/rest_api/
│   ├── DZ3_STATUS.md
│   ├── HELP.md
│   ├── kreiranje_baze.sql
│   ├── mvnw
│   └── pom.xml
└── README.md
```

## Model baze podataka

Datoteka `rest-api/kreiranje_baze.sql` je PostgreSQL dump sheme baze. Definira tablice, sekvence, primarne ključeve, strane ključeve, unique constraintove, check constraint i indeks za rezervacije.

Glavne tablice:

| Tablica | Uloga |
| --- | --- |
| `korisnik` | Osnovni korisnički podaci |
| `sportas_rekreativac` | Korisnik koji može imati rezervacije |
| `administrator` | Administratorski korisnik |
| `vlasnik_centra` | Vlasnik sportskog centra |
| `sportska_udruga` | Sportska udruga |
| `mjesto` | Šifrarnik mjesta |
| `sport` | Šifrarnik sportova |
| `sportski_centar` | Sportski centar s adresom, mjestom i radnim vremenom |
| `teren` | Teren unutar sportskog centra |
| `rezervacija` | Rezervacija terena u vremenskom intervalu |
| `preferencija` | Veza korisnika i sporta |
| `obavijest` | Obavijest vlasnika centra ili administratora |
| `dogadaj` | Događaj povezan s udrugom, administratorom i terenom |

Važna pravila iz SQL sheme:

- `teren` ima kompozitni primarni ključ `(id_centar, id_teren)`.
- `rezervacija` ima primarni ključ `id_rezervacija`.
- `rezervacija` ima check constraint `vrijeme_pocetka < vrijeme_zavrsetka`.
- `rezervacija` ima unique pravilo nad `(id_centar, id_teren, vrijeme_pocetka)`.
- `rezervacija` referencira `teren` preko `(id_centar, id_teren)`.
- `rezervacija.id_korisnik` referencira `sportas_rekreativac.id_korisnik`.
- `sportski_centar.id_mjesto` referencira `mjesto.id_mjesto`.
- `sportski_centar.id_vlasnik` referencira `vlasnik_centra.id_korisnik`.
- `teren.id_sport` referencira `sport.id_sport`.
- `sportski_centar` ima unique pravilo za adresu u mjestu, uključujući normalizirani indeks po `id_mjesto` i `lower(trim(adresa))`.
- `rezervacija` ima indeks `idx_rezervacija_teren_interval` nad `(id_centar, id_teren, vrijeme_pocetka, vrijeme_zavrsetka)`.

Napomena: `kreiranje_baze.sql` sadrži strukturu baze podataka: tablice, primarne ključeve, strane ključeve, constraintove i indekse. Punjenje baze obrađeno je u prethodnoj domaćoj zadaći.

## Preduvjeti

Za pokretanje projekta potrebni su:

- Java 21
- Node.js i npm
- Git
- PostgreSQL, ako se koristi lokalna baza umjesto aktivne Neon baze

Maven nije potrebno zasebno instalirati jer backend koristi Maven Wrapper.

Provjera verzija:

```bash
java -version
node -v
npm -v
```

## Konfiguracija baze

Backend konfiguracija nalazi se u:

```text
rest-api/src/main/resources/application.properties
```

Aktivna konfiguracija projekta koristi deployanu Neon PostgreSQL bazu. To je vidljivo iz datasource URL-a u `application.properties`, koji pokazuje na host `neon.tech`:

```properties
spring.datasource.url=jdbc:postgresql://...neon.tech/neondb?sslmode=require&channel_binding=require
spring.datasource.username=neondb_owner
```

Frontend i backend se pokreću lokalno, ali backend se spaja na Neon bazu. `localhost` u frontend JavaScript datotekama odnosi se na lokalni backend API (`http://localhost:8080/api`), a ne na bazu.

Aplikacija koristi Hibernate s postavkom:

```properties
spring.jpa.hibernate.ddl-auto=update
```

### Opcionalna lokalna baza

Ako je potrebno pokretanje s lokalnom PostgreSQL bazom, napravite lokalnu bazu:

```bash
createdb rest_api
```

Zatim zamijenite datasource vrijednosti u `rest-api/src/main/resources/application.properties` lokalnom konfiguracijom:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/rest_api
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
springdoc.swagger-ui.path=/swagger-ui.html
```

Za lokalnu shemu prema dumpu učitajte SQL datoteku:

```bash
psql -d rest_api -f rest-api/kreiranje_baze.sql
```

Dump je izrađen za PostgreSQL 17 i sadrži `OWNER TO neondb_owner` naredbe. Ako lokalna baza nema taj role, prilagodite vlasnika objekata ili kreirajte odgovarajući role prije importanja.

## Ogledni podaci

Za rezervacije je potreban korisnik koji postoji u tablicama `korisnik` i `sportas_rekreativac`. Frontend šalje korisnika s ID-em `1`:

```javascript
const TRENUTNI_KORISNIK_ID = 1;
```

Primjer testnog korisnika za lokalnu bazu:

```sql
INSERT INTO korisnik (id_korisnik, ime, prezime, email, tip_korisnika)
VALUES (1, 'Test', 'Korisnik', 'test@example.com', 'SPORTAS_REKREATIVAC')
ON CONFLICT (id_korisnik) DO NOTHING;

INSERT INTO sportas_rekreativac (id_korisnik)
VALUES (1)
ON CONFLICT (id_korisnik) DO NOTHING;

SELECT setval(
  pg_get_serial_sequence('public.korisnik', 'id_korisnik'),
  GREATEST((SELECT MAX(id_korisnik) FROM korisnik), 1)
);
```

Za rad s rezervacijama potrebni su i zapisi u tablicama `mjesto`, `sport`, `sportski_centar` i `teren`. Mjesto i sportski centar unose se na stranici `sportski-centri.html`, sport se može unijeti preko Swagger UI-a ili API poziva, a teren se unosi na stranici detalja sportskog centra.

## Pokretanje backenda

Iz root direktorija projekta:

```bash
cd rest-api
./mvnw spring-boot:run
```

Backend se pokreće na:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Provjera API-ja:

```bash
curl http://localhost:8080/api/sportski-centri
```

## Pokretanje frontenda

U drugom terminalu, iz root direktorija projekta:

```bash
cd frontend
npm install
npm start
```

Frontend se poslužuje na:

```text
http://localhost:5500
```

Stranice:

- `http://localhost:5500/rezervacije.html`
- `http://localhost:5500/sportski-centri.html`
- `http://localhost:5500/sportski-centar-detalji.html?id=1`

Frontend koristi backend API na:

```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

Ako backend pokrenete na drugom portu ili domeni, ažurirajte `API_BASE_URL` u JavaScript datotekama u `frontend/js/`.

## Korištenje aplikacije

Preporučeni redoslijed za lokalni rad:

1. Provjerite datasource postavke u `rest-api/src/main/resources/application.properties`.
2. Pokrenite backend.
3. Pokrenite frontend.
4. Otvorite `http://localhost:5500/sportski-centri.html`.
5. Unesite mjesto.
6. Unesite sportski centar.
7. Unesite sport putem REST API-ja ili Swagger UI-a.
8. Otvorite detalje sportskog centra i unesite teren.
9. Otvorite `http://localhost:5500/rezervacije.html`, odaberite centar i datum te kreirajte rezervaciju.

Ako se koristi lokalna baza, prije pokretanja backenda učitajte shemu iz `rest-api/kreiranje_baze.sql`.

## API rute

Interaktivna API dokumentacija dostupna je na:

```text
http://localhost:8080/swagger-ui.html
```

### Mjesta

| Metoda | Ruta | Opis |
| --- | --- | --- |
| GET | `/api/mjesta` | Dohvat mjesta |
| GET | `/api/mjesta?search=zagreb` | Pretraga mjesta |
| GET | `/api/mjesta/{id}` | Dohvat mjesta po ID-u |
| POST | `/api/mjesta` | Kreiranje mjesta |
| PUT | `/api/mjesta/{id}` | Ažuriranje mjesta |
| DELETE | `/api/mjesta/{id}` | Brisanje mjesta |

JSON za kreiranje i ažuriranje:

```json
{
  "nazivMjesta": "Zagreb",
  "postanskiBroj": "10000"
}
```

### Sportovi

| Metoda | Ruta | Opis |
| --- | --- | --- |
| GET | `/api/sportovi` | Dohvat sportova |
| GET | `/api/sportovi?search=tenis` | Pretraga sportova |
| GET | `/api/sportovi/{id}` | Dohvat sporta po ID-u |
| POST | `/api/sportovi` | Kreiranje sporta |
| PUT | `/api/sportovi/{id}` | Ažuriranje sporta |
| DELETE | `/api/sportovi/{id}` | Brisanje sporta |

JSON za kreiranje i ažuriranje:

```json
{
  "nazivSporta": "Tenis"
}
```

### Sportski Centri

| Metoda | Ruta | Opis |
| --- | --- | --- |
| GET | `/api/sportski-centri` | Dohvat sportskih centara |
| GET | `/api/sportski-centri?search=maksimir` | Pretraga po nazivu |
| GET | `/api/sportski-centri?mjestoId=1` | Filter po mjestu |
| GET | `/api/sportski-centri/{id}` | Dohvat centra po ID-u |
| POST | `/api/sportski-centri` | Kreiranje centra |
| PUT | `/api/sportski-centri/{id}` | Ažuriranje centra |
| DELETE | `/api/sportski-centri/{id}` | Brisanje centra |

JSON za kreiranje i ažuriranje:

```json
{
  "nazivCentra": "Sportski centar Maksimir",
  "adresa": "Maksimirska 128",
  "idMjesto": 1,
  "radnoVrijemeTjedanOd": "08:00",
  "radnoVrijemeTjedanDo": "22:00",
  "radnoVrijemeVikendOd": "09:00",
  "radnoVrijemeVikendDo": "20:00"
}
```

Radno vrijeme se unosi u formatu `HH:mm`. Aplikacija prihvaća pune sate, npr. `08:00`.

### Tereni

| Metoda | Ruta | Opis |
| --- | --- | --- |
| GET | `/api/sportski-centri/{centarId}/tereni` | Dohvat terena centra |
| GET | `/api/sportski-centri/{centarId}/tereni?sportId=1` | Filter terena po sportu |
| GET | `/api/sportski-centri/{centarId}/tereni/{terenId}` | Dohvat terena po oznaci |
| POST | `/api/sportski-centri/{centarId}/tereni` | Kreiranje terena |
| PUT | `/api/sportski-centri/{centarId}/tereni/{terenId}` | Ažuriranje terena |
| DELETE | `/api/sportski-centri/{centarId}/tereni/{terenId}` | Brisanje terena |

JSON za kreiranje:

```json
{
  "idTeren": "T1",
  "idSport": 1,
  "cijena": 12.5
}
```

JSON za ažuriranje:

```json
{
  "idSport": 1,
  "cijena": 14.0
}
```

### Rezervacije

| Metoda | Ruta | Opis |
| --- | --- | --- |
| GET | `/api/rezervacije/available?idCentar=1&datum=2026-05-23` | Dohvat slobodnih termina |
| GET | `/api/rezervacije?idKorisnik=1` | Dohvat rezervacija korisnika |
| POST | `/api/rezervacije` | Kreiranje rezervacije |
| DELETE | `/api/rezervacije/{id}` | Otkazivanje rezervacije |

JSON za kreiranje:

```json
{
  "idKorisnik": 1,
  "idCentar": 1,
  "idTeren": "T1",
  "vrijemePocetka": "2026-05-23T10:00:00",
  "vrijemeZavrsetka": "2026-05-23T11:00:00"
}
```

Slobodni termini generiraju se po satima prema radnom vremenu sportskog centra. Za subotu i nedjelju koriste se vikend radna vremena, a za ostale dane radno vrijeme kroz tjedan.

## Primjeri API poziva

Primjeri pretpostavljaju da API radi na `http://localhost:8080` i da su korišteni ID-evi dostupni u bazi. Ako odgovor API-ja vrati drukčiji ID, zamijenite vrijednost u sljedećim pozivima.

Dodavanje mjesta:

```bash
curl -X POST http://localhost:8080/api/mjesta \
  -H "Content-Type: application/json" \
  -d '{"nazivMjesta":"Zagreb","postanskiBroj":"10000"}'
```

Dodavanje sporta:

```bash
curl -X POST http://localhost:8080/api/sportovi \
  -H "Content-Type: application/json" \
  -d '{"nazivSporta":"Tenis"}'
```

Dodavanje sportskog centra:

```bash
curl -X POST http://localhost:8080/api/sportski-centri \
  -H "Content-Type: application/json" \
  -d '{
    "nazivCentra": "Sportski centar Maksimir",
    "adresa": "Maksimirska 128",
    "idMjesto": 1,
    "radnoVrijemeTjedanOd": "08:00",
    "radnoVrijemeTjedanDo": "22:00",
    "radnoVrijemeVikendOd": "09:00",
    "radnoVrijemeVikendDo": "20:00"
  }'
```

Dodavanje terena:

```bash
curl -X POST http://localhost:8080/api/sportski-centri/1/tereni \
  -H "Content-Type: application/json" \
  -d '{"idTeren":"T1","idSport":1,"cijena":12.50}'
```

Dohvaćanje slobodnih termina:

```bash
curl "http://localhost:8080/api/rezervacije/available?idCentar=1&datum=2026-05-23"
```

Kreiranje rezervacije:

```bash
curl -X POST http://localhost:8080/api/rezervacije \
  -H "Content-Type: application/json" \
  -d '{
    "idKorisnik": 1,
    "idCentar": 1,
    "idTeren": "T1",
    "vrijemePocetka": "2026-05-23T10:00:00",
    "vrijemeZavrsetka": "2026-05-23T11:00:00"
  }'
```

## Testiranje

Backend testovi pokreću se iz direktorija `rest-api`:

```bash
./mvnw test
```

Napomena: testovi trenutno koriste datasource iz `application.properties`, odnosno Neon PostgreSQL bazu. Projekt nema zaseban `application-test.properties`, pa testove treba pokretati pažljivo i samo nad bazom u kojoj su testni podaci prihvatljivi.

Testovi u projektu:

| Test | Razina |
| --- | --- |
| `RezervacijaControllerTest` | Controller |
| `RezervacijaServiceTest` | Service |
| `RezervacijaRepositoryTest` | Repository / JPA |
| `RezervacijaIntegrationTest` | Integracijski test |

Prije pokretanja testova provjerite datasource postavke u `application.properties`, jer Spring Boot koristi konfiguraciju aplikacije za podizanje konteksta.

## Build aplikacije

Iz direktorija `rest-api`:

```bash
./mvnw clean package
```

Pokretanje JAR datoteke:

```bash
java -jar target/rest-api-0.0.1-SNAPSHOT.jar
```

Build bez pokretanja testova:

```bash
./mvnw clean package -DskipTests
```

## Dodatna dokumentacija

U direktoriju `rest-api/` nalaze se dodatni projektni dokumenti:

- `HELP.md`
- `DZ3_STATUS.md`

Swagger dokumentacija dostupna je nakon pokretanja backenda:

```text
http://localhost:8080/swagger-ui.html
```

## Česti problemi

### Frontend se ne može spojiti na API

Provjerite da backend radi na:

```text
http://localhost:8080
```

Provjerite i vrijednost `API_BASE_URL` u datotekama:

- `frontend/js/sportski-centri.js`
- `frontend/js/sportski-centar-detalji.js`
- `frontend/js/rezervacije.js`

### Spajanje na bazu ne uspijeva

Provjerite:

- je li PostgreSQL baza dostupna
- odgovara li `spring.datasource.url` stvarnoj bazi
- jesu li korisničko ime i lozinka ispravni
- ima li korisnik baze prava nad shemom `public`
- postoji li role naveden u SQL dumpu ako importate `kreiranje_baze.sql`

### Kreiranje rezervacije vraća grešku za korisnika

Provjerite postoji li isti ID korisnika u tablicama:

- `korisnik`
- `sportas_rekreativac`

Frontend za rezervacije koristi `TRENUTNI_KORISNIK_ID = 1` u datoteci `frontend/js/rezervacije.js`.

### Brisanje vraća HTTP 409 Conflict

HTTP 409 znači da zapis ima povezane podatke koji sprječavaju brisanje. Primjeri:

- mjesto koristi sportski centar
- sport koristi teren
- sportski centar ima terene
- teren ima rezervacije

### Swagger UI se ne otvara

Provjerite da backend radi i otvorite:

```text
http://localhost:8080/swagger-ui.html
```

Ako backend radi na drugom portu, zamijenite `8080` stvarnim portom.
