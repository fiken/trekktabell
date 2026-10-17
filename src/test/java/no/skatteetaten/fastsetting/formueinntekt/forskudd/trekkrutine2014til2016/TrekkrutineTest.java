package no.skatteetaten.fastsetting.formueinntekt.forskudd.trekkrutine2014til2016;

import static no.skatteetaten.fastsetting.formueinntekt.forskudd.trekkrutine2014til2016.Periode.PERIODE_1_DAG;
import static no.skatteetaten.fastsetting.formueinntekt.forskudd.trekkrutine2014til2016.Periode.PERIODE_1_MAANED;
import static no.skatteetaten.fastsetting.formueinntekt.forskudd.trekkrutine2014til2016.Periode.PERIODE_1_UKE;
import static no.skatteetaten.fastsetting.formueinntekt.forskudd.trekkrutine2014til2016.Tabellnummer.TABELL_0101;
import static no.skatteetaten.fastsetting.formueinntekt.forskudd.trekkrutine2014til2016.Tabellnummer.TABELL_0201;
import static no.skatteetaten.fastsetting.formueinntekt.forskudd.trekkrutine2014til2016.Tabellnummer.TABELL_6600;
import static no.skatteetaten.fastsetting.formueinntekt.forskudd.trekkrutine2014til2016.Tabellnummer.TABELL_7100;
import static no.skatteetaten.fastsetting.formueinntekt.forskudd.trekkrutine2014til2016.Tabellnummer.TABELL_7116;
import static no.skatteetaten.fastsetting.formueinntekt.forskudd.trekkrutine2014til2016.Tabellnummer.TABELL_7209P;
import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;

import org.junit.Test;

public class TrekkrutineTest {

    /**
     * Hashene er av de sorterte linjene i Skatteetatens publiserte trekktabeller (AlleTabelleneIEnFil) for året.
     * En linje er tabellnummer (4), periode (1), pensjonist (1), trekkgrunnlag (5) og trekk (5).
     */
    @Test
    public void tabellene_er_identiske_med_de_publiserte_trekktabellene() throws Exception {
        assertEquals("f755ed8e99dc3822483187cb76ffe180d9b48fd281a22c508118f9e2b6b032d4", hashAvTabellene(2014));
        assertEquals("3f06f6b2cbe063365d8e62a3b8d3cbac2b0d91fb07d9a177518c33528ff0b6db", hashAvTabellene(2015));
        assertEquals("5c3178e6c3d641421226ff608636267e9bc0df83a391b8cffe8572cc136cc5fd", hashAvTabellene(2016));
    }

    /**
     * Verifisert mot Skatteetatens tjeneste for tabelltrekk.
     */
    @Test
    public void overskytende_trekk() {
        assertEquals(44_283, Trekkrutine.beregnTabelltrekk(2014, TABELL_7100, PERIODE_1_MAANED, 100_000));
        assertEquals(62_265, Trekkrutine.beregnTabelltrekk(2014, TABELL_7100, PERIODE_1_MAANED, 133_337));
        assertEquals(51_044, Trekkrutine.beregnTabelltrekk(2014, TABELL_0101, PERIODE_1_MAANED, 133_337));
        assertEquals(37_021, Trekkrutine.beregnTabelltrekk(2014, TABELL_6600, PERIODE_1_MAANED, 133_337));
        assertEquals(31_526, Trekkrutine.beregnTabelltrekk(2014, TABELL_7209P, PERIODE_1_MAANED, 88_000));
        assertEquals(2_200, Trekkrutine.beregnTabelltrekk(2014, TABELL_7100, PERIODE_1_DAG, 5_000));
        assertEquals(3_031, Trekkrutine.beregnTabelltrekk(2014, TABELL_7100, PERIODE_1_DAG, 6_543));
        assertEquals(2_917, Trekkrutine.beregnTabelltrekk(2014, TABELL_7116, PERIODE_1_DAG, 6_543));
        assertEquals(17_504, Trekkrutine.beregnTabelltrekk(2014, TABELL_0201, PERIODE_1_UKE, 43_210));

        assertEquals(43_852, Trekkrutine.beregnTabelltrekk(2015, TABELL_7100, PERIODE_1_MAANED, 100_000));
        assertEquals(61_834, Trekkrutine.beregnTabelltrekk(2015, TABELL_7100, PERIODE_1_MAANED, 133_337));
        assertEquals(50_653, Trekkrutine.beregnTabelltrekk(2015, TABELL_0101, PERIODE_1_MAANED, 133_337));
        assertEquals(36_630, Trekkrutine.beregnTabelltrekk(2015, TABELL_6600, PERIODE_1_MAANED, 133_337));
        assertEquals(31_166, Trekkrutine.beregnTabelltrekk(2015, TABELL_7209P, PERIODE_1_MAANED, 88_000));
        assertEquals(2_179, Trekkrutine.beregnTabelltrekk(2015, TABELL_7100, PERIODE_1_DAG, 5_000));
        assertEquals(3_010, Trekkrutine.beregnTabelltrekk(2015, TABELL_7100, PERIODE_1_DAG, 6_543));
        assertEquals(2_895, Trekkrutine.beregnTabelltrekk(2015, TABELL_7116, PERIODE_1_DAG, 6_543));
        assertEquals(17_410, Trekkrutine.beregnTabelltrekk(2015, TABELL_0201, PERIODE_1_UKE, 43_210));

        assertEquals(43_206, Trekkrutine.beregnTabelltrekk(2016, TABELL_7100, PERIODE_1_MAANED, 100_000));
        assertEquals(61_188, Trekkrutine.beregnTabelltrekk(2016, TABELL_7100, PERIODE_1_MAANED, 133_337));
        assertEquals(50_302, Trekkrutine.beregnTabelltrekk(2016, TABELL_0101, PERIODE_1_MAANED, 133_337));
        assertEquals(35_647, Trekkrutine.beregnTabelltrekk(2016, TABELL_6600, PERIODE_1_MAANED, 133_337));
        assertEquals(30_639, Trekkrutine.beregnTabelltrekk(2016, TABELL_7209P, PERIODE_1_MAANED, 88_000));
        assertEquals(2_147, Trekkrutine.beregnTabelltrekk(2016, TABELL_7100, PERIODE_1_DAG, 5_000));
        assertEquals(2_978, Trekkrutine.beregnTabelltrekk(2016, TABELL_7100, PERIODE_1_DAG, 6_543));
        assertEquals(2_872, Trekkrutine.beregnTabelltrekk(2016, TABELL_7116, PERIODE_1_DAG, 6_543));
        assertEquals(17_335, Trekkrutine.beregnTabelltrekk(2016, TABELL_0201, PERIODE_1_UKE, 43_210));
    }

    private static String hashAvTabellene(int aar) throws Exception {
        List<String> linjer = new ArrayList<>();
        for (Tabellnummer tabellnummer : Tabellnummer.values()) {
            for (Periode periode : Periode.values()) {
                if (!tabellnummer.finnesFor(periode)) {
                    continue;
                }
                for (long grunnlag = 0; grunnlag <= periode.maksTrekkgrunnlag(); grunnlag += periode.inntektssprang()) {
                    long trekk = Trekkrutine.beregnTabelltrekk(aar, tabellnummer, periode, grunnlag);
                    linjer.add("%s%d%d%05d%05d".formatted(
                            tabellnummer.nummer, periode.ordinal() + 1, tabellnummer.pensjonist ? 1 : 0, grunnlag, trekk));
                }
            }
        }
        Collections.sort(linjer);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        for (String linje : linjer) {
            digest.update((linje + "\n").getBytes(StandardCharsets.US_ASCII));
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
