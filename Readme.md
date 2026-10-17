# Trekkrutine for beregning av forskuddstrekk 2014–2016

Disse java-klassene beregner tabelltrekk for inntektsårene 2014, 2015 og 2016.

Det er metoden `beregnTabelltrekk` i klassen `Trekkrutine` som skal kalles når tabelltrekket skal beregnes. Inntektsåret sendes inn som parameter.

### Kilde
Rutinen er portert fra Skatteetatens Cobol-trekkrutine (FT4P200T/FT6P200T), dokumentert i TRKRUT14 og trkrut16 (kun tilgjengelig via Internet Archive). Rutinebeskrivelsen for 2015 er ikke tilgjengelig, så satsene for 2015 er utledet fra de publiserte trekktabellene.

Cobol-programmet regner med fastpunkt-felter: delskattene summeres uavrundet, og bare trekket avrundes. Java-porteringen Skatteetaten laget for 2016 (branch `2016`) avrunder delskattene og avviker derfor med opptil 2 kr fra trekktabellene. Denne versjonen etterligner Cobol-aritmetikken og gir trekk identisk med de publiserte trekktabellene for alle tre år. Det kontrolleres i `TrekkrutineTest`.

### Overskytende trekk
Over maks trekkgrunnlag for perioden beregnes overskytende trekk med en fast prosent per tabell, som i Cobol-rutinen.
