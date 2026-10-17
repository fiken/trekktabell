package no.skatteetaten.fastsetting.formueinntekt.forskudd.trekkrutine2014til2016;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum Periode {
    PERIODE_1_MAANED(79800, 100, "12.12", "10.5", "12", "11", "10.5"),
    PERIODE_14_DAGER(36800, 50, "26.26", "23", "26", "24", "23"),
    PERIODE_1_UKE(18400, 20, "52.52", "46", "52", "48", "46"),
    PERIODE_4_DAGER(15900, 20, "60.60", andel(60, "46", 52), "91", andel(91, "11", 12), andel(91, "10.5", 12)),
    PERIODE_3_DAGER(11900, 20, "80.80", andel(80, "46", 52), "122", andel(122, "11", 12), andel(122, "10.5", 12)),
    PERIODE_2_DAGER(7900, 20, "121.20", andel(120, "46", 52), "183", andel(183, "11", 12), andel(183, "10.5", 12)),
    PERIODE_1_DAG(3900, 20, "242.40", andel(240, "46", 52), "365", andel(365, "11", 12), andel(365, "10.5", 12));

    final long maksTrekkgrunnlag;
    final long inntektssprang;
    final BigDecimal inntektsperioderVanlig;
    final BigDecimal trekkperioderVanlig;
    final BigDecimal inntektsperioder;
    final BigDecimal trekkperioderPensjonist;
    final BigDecimal trekkperioderStandardfradrag;

    Periode(long maksTrekkgrunnlag, long inntektssprang, String inntektsperioderVanlig, String trekkperioderVanlig,
            String inntektsperioder, String trekkperioderPensjonist, String trekkperioderStandardfradrag) {
        this.maksTrekkgrunnlag = maksTrekkgrunnlag;
        this.inntektssprang = inntektssprang;
        this.inntektsperioderVanlig = new BigDecimal(inntektsperioderVanlig);
        this.trekkperioderVanlig = new BigDecimal(trekkperioderVanlig);
        this.inntektsperioder = new BigDecimal(inntektsperioder);
        this.trekkperioderPensjonist = new BigDecimal(trekkperioderPensjonist);
        this.trekkperioderStandardfradrag = new BigDecimal(trekkperioderStandardfradrag);
    }

    /**
     * Høyeste trekkgrunnlag i trekktabellen. Over dette beregnes overskytende trekk med en fast prosent.
     */
    public long maksTrekkgrunnlag() {
        return maksTrekkgrunnlag;
    }

    /**
     * Trinnene i trekktabellen. Trekkgrunnlaget avrundes ned til nærmeste trinn.
     */
    public long inntektssprang() {
        return inntektssprang;
    }

    /**
     * Antall trekkperioder i dagtabellene lagres med 7 desimaler i Cobol-rutinen.
     */
    private static String andel(int dager, String perioder, int perAar) {
        return BigDecimal.valueOf(dager)
                .multiply(new BigDecimal(perioder))
                .divide(BigDecimal.valueOf(perAar), 7, RoundingMode.HALF_UP)
                .toPlainString();
    }
}
