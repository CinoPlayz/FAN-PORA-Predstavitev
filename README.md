# Fast Android Networking 📡

## Zakaj 🤔

Fast Android Networking je knjižnica za lažje upravljanje z omrežnjem. Omogoča ustvarjanje vseh HTTP vrst zahtev v omrežju, prenos datotek, nalaganje datotek, prekinjanje zahtev, prioritiziranje zahtev in direkten prenos slike v ImegeView widget. Omogoča HTTP/2 zahteve, za varno izmenljivost podatkov. Uporablja manj računalniških virov zaslugi Okio knjižnici, na kateri je zgrajena. 

## Prednosti ✅
- Preprosta sintaksa
- Možnosti prekinitve zahteve
- Prepomnenje zahtev
- Sporočanje napredkov pri prenosu in nalaganju

## Slabosti ❌
- Primarno naret za Javo
- Veliko odprtih issue-jev
- Ni direktne pretvorbe iz json podatka v objekt

## Licenca 📄

[Apache 2.0 Licenca](https://github.com/amitshekhariitbhu/Fast-Android-Networking?tab=Apache-2.0-1-ov-file#readme)
Standardna odprto kodna licenca, ki omogoča prosto uporabo in modifikacijo kode za kogarkoli.

## Število zvezdic, forkov ⭐

![Static Badge](https://img.shields.io/badge/Stars-5700-blue)
![Static Badge](https://img.shields.io/badge/Forks-962-blue)

## Vzdrževanje projekta 🛠️

Projekt vzdržuje primarno samo ena oseba. Zadnji commit in release pa je bil Avgusta 2024

## Primeri Uporabe

Po dodaji knjižnice v build.gradle je treba dodati dovoljenje za internet v menifest datoteko

![Menifest](/assets/menifest_perrmission.png)

Treba je inicializirati Android Network razred

![Init](/assets/inicialization.png)

### Preprosti GET zahtevek

Podamo URL in parser za rezultat

![get](/assets/simple_get.png)

Rezultat

![get_result](/assets/simple_get_result.png)

### Zahtevnejši GET zahtevek

Ustvarimo GET zahtevek kateremu podamo spremenljivko za pot v URL-ju, dodan header za bearer token, prioriteta zahteve, ki pa nam določi kdaj se naj izvede request, pa še executor, ki pa določi da se naj izvede zahteva na drugi niti. Za pridobivanje rezultata zahteve pa uporabimo JSONArray parser.

![get](/assets/more_coplicated_get.png)

Rezultat

![get_result](/assets/more_coplicated_get_result.png)


