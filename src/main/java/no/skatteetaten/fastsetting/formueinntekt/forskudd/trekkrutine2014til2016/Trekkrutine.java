package no.skatteetaten.fastsetting.formueinntekt.forskudd.trekkrutine2014til2016;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

/**
 * Tabelltrekk for inntektsårene 2014–2016, portert fra Skatteetatens Cobol-trekkrutine (FT4P200T/FT6P200T,
 * dokumentert i TRKRUT14 og trkrut16).
 * <p>
 * Cobol-programmet regner med fastpunkt-felter: delskattene summeres uavrundet, og bare trekket avrundes. Det er
 * etterlignet her, slik at trekket blir identisk med de publiserte trekktabellene.
 */
public final class Trekkrutine {

    private static final Set<String> TREKK_I_12_MAANEDER = Set.of(
            "7350", "7450", "7500", "7600", "7700", "7800", "6350", "6450", "6500", "6600", "6700", "6800");
    private static final Set<String> UTEN_TRYGDEAVGIFT = Set.of(
            "7500", "7550", "7600", "7650", "0100", "0200", "6500", "6550", "6600", "6650", "7160", "7170", "7260", "7270");
    private static final Set<String> LAV_TRYGDEAVGIFT = Set.of("7700", "7800", "6700", "6800");

    private static final BigDecimal HUNDRE = BigDecimal.valueOf(100);
    private static final BigDecimal ANV_MINSTEFRADRAG_PROSENT = new BigDecimal("37.84");
    private static final BigDecimal MINSTEFRADRAG_PROSENT = new BigDecimal("43");
    private static final BigDecimal TRYGDEAVGIFT_OPPTRAPPING_PROSENT = new BigDecimal("25");
    private static final BigDecimal LAV_TRYGDEAVGIFT_PROSENT = new BigDecimal("5.1");
    private static final BigDecimal HOY_TRYGDEAVGIFT_PROSENT = new BigDecimal("8.2");
    private static final BigDecimal STANDARDFRADRAG_PROSENT = new BigDecimal("10");
    private static final BigDecimal SJOFRADRAG_PROSENT = new BigDecimal("30");
    private static final long MIN_ANV_MINSTEFRADRAG = 3520;
    private static final long MIN_MINSTEFRADRAG = 4000;
    private static final long LONNSFRADRAG = 31800;
    private static final long ANV_LONNSFRADRAG = 27984;
    private static final long MAKS_STANDARDFRADRAG = 40000;
    private static final long MAKS_SJOFRADRAG = 80000;

    private enum Tabelltype {
        VANLIG,
        PENSJONIST,
        STANDARDFRADRAG,
        SJO
    }

    private Trekkrutine() {
    }

    public static long beregnTabelltrekk(int aar, Tabellnummer tabellnummer, Periode periode, long trekkgrunnlag) {
        Satser satser = Satser.forAar(aar);
        if (!tabellnummer.finnesFor(periode)) {
            throw new IllegalArgumentException("Finnes ingen " + tabellnummer + " for " + periode);
        }
        if (trekkgrunnlag < 0) {
            throw new IllegalArgumentException("Negativt trekkgrunnlag: " + trekkgrunnlag);
        }

        BigDecimal overskytendeTrekk = BigDecimal.ZERO;
        if (trekkgrunnlag > periode.maksTrekkgrunnlag) {
            long avrundet = trekkgrunnlag / periode.inntektssprang * periode.inntektssprang;
            overskytendeTrekk = BigDecimal.valueOf(avrundet - periode.maksTrekkgrunnlag)
                    .multiply(BigDecimal.valueOf(satser.overskytendeProsent(tabellnummer)))
                    .divide(HUNDRE);
            trekkgrunnlag = periode.maksTrekkgrunnlag;
        }

        long trekk = beregnTrekkInnenforTabellen(satser, tabellnummer, periode, trekkgrunnlag);
        return BigDecimal.valueOf(trekk).add(overskytendeTrekk).setScale(0, RoundingMode.DOWN).longValueExact();
    }

    private static long beregnTrekkInnenforTabellen(Satser satser, Tabellnummer tabellnummer, Periode periode, long trekkgrunnlag) {
        String nummer = tabellnummer.nummer;
        int skatteprosent = nummer.charAt(0) - '0';
        int tabelltypesiffer = nummer.charAt(1) - '0';
        int tabellnr = Integer.parseInt(nummer.substring(2));
        boolean finnmark = skatteprosent == 6;

        Tabelltype tabelltype;
        if (skatteprosent == 0) tabelltype = Tabelltype.SJO;
        else if (tabellnummer.pensjonist) tabelltype = Tabelltype.PENSJONIST;
        else if (tabelltypesiffer < 3) tabelltype = Tabelltype.VANLIG;
        else tabelltype = Tabelltype.STANDARDFRADRAG;

        int klasse;
        BigDecimal inntektsperioder;
        BigDecimal trekkperioder;
        switch (tabelltype) {
            case VANLIG -> {
                klasse = tabelltypesiffer;
                boolean spesialtabell = tabellnr == 50 || tabellnr == 60 || tabellnr == 70;
                inntektsperioder = spesialtabell ? periode.inntektsperioder : periode.inntektsperioderVanlig;
                if (tabellnr == 50 || tabellnr == 60) trekkperioder = periode.inntektsperioder;
                else if (tabellnr == 70) trekkperioder = periode.trekkperioderStandardfradrag;
                else trekkperioder = periode.trekkperioderVanlig;
            }
            case PENSJONIST -> {
                klasse = tabelltypesiffer;
                inntektsperioder = periode.inntektsperioder;
                trekkperioder = periode.trekkperioderPensjonist;
            }
            case STANDARDFRADRAG -> {
                klasse = tabelltypesiffer % 2 == 1 ? 1 : 2;
                inntektsperioder = periode.inntektsperioder;
                trekkperioder = TREKK_I_12_MAANEDER.contains(nummer)
                        ? periode.inntektsperioder
                        : periode.trekkperioderStandardfradrag;
            }
            default -> {
                klasse = tabelltypesiffer;
                inntektsperioder = periode.inntektsperioder;
                trekkperioder = periode.inntektsperioder;
            }
        }

        long avrundetTrekkgrunnlag = trekkgrunnlag / periode.inntektssprang * periode.inntektssprang;
        BigDecimal aarsinntekt = BigDecimal.valueOf(avrundetTrekkgrunnlag + periode.inntektssprang / 2).multiply(inntektsperioder);

        BigDecimal anvMinstefradrag = BigDecimal.ZERO;
        BigDecimal minstefradrag = BigDecimal.ZERO;
        switch (tabelltype) {
            case VANLIG, STANDARDFRADRAG -> anvMinstefradrag = minstefradrag(
                    aarsinntekt, ANV_MINSTEFRADRAG_PROSENT, MIN_ANV_MINSTEFRADRAG, satser.maksAnvMinstefradrag(), ANV_LONNSFRADRAG);
            // Cobol-rutinen begrenser ikke pensjonistenes minstefradrag til inntekten
            case PENSJONIST -> anvMinstefradrag = prosentMedGrenser(
                    aarsinntekt, satser.anvMinstefradragProsentPensjon(), MIN_ANV_MINSTEFRADRAG, satser.maksAnvMinstefradragPensjon());
            case SJO -> minstefradrag = minstefradrag(
                    aarsinntekt, MINSTEFRADRAG_PROSENT, MIN_MINSTEFRADRAG, satser.maksMinstefradrag(), LONNSFRADRAG);
        }

        long fradrag = tabelltype == Tabelltype.VANLIG || tabelltype == Tabelltype.PENSJONIST ? tabellfradrag(tabellnr) : 0;

        BigDecimal standardfradrag = tabelltype == Tabelltype.STANDARDFRADRAG || tabelltype == Tabelltype.SJO
                ? prosent(aarsinntekt, STANDARDFRADRAG_PROSENT).min(BigDecimal.valueOf(MAKS_STANDARDFRADRAG))
                : BigDecimal.ZERO;
        BigDecimal sjofradrag = tabelltype == Tabelltype.SJO
                ? prosent(aarsinntekt, SJOFRADRAG_PROSENT).setScale(0, RoundingMode.DOWN).min(BigDecimal.valueOf(MAKS_SJOFRADRAG))
                : BigDecimal.ZERO;

        long klassefradrag;
        if (finnmark) klassefradrag = klasse == 1 ? satser.klassefradrag1Finnmark() : satser.klassefradrag2Finnmark();
        else klassefradrag = klasse == 1 ? satser.klassefradrag1() : satser.klassefradrag2();

        BigDecimal skattbarInntekt = aarsinntekt
                .subtract(anvMinstefradrag)
                .subtract(minstefradrag)
                .subtract(BigDecimal.valueOf(fradrag))
                .subtract(standardfradrag)
                .subtract(sjofradrag)
                .subtract(BigDecimal.valueOf(klassefradrag));

        BigDecimal kommuneskatt = BigDecimal.ZERO;
        BigDecimal fellesskatt = BigDecimal.ZERO;
        if (skattbarInntekt.signum() >= 0) {
            kommuneskatt = prosent(skattbarInntekt, satser.skattore());
            fellesskatt = prosent(skattbarInntekt, finnmark ? satser.fellesskattProsentFinnmark() : satser.fellesskattProsent());
        }

        BigDecimal trygdeavgift = BigDecimal.ZERO;
        if (!UTEN_TRYGDEAVGIFT.contains(nummer) && aarsinntekt.compareTo(BigDecimal.valueOf(satser.avgiftsfriTrygdeavgift())) >= 0) {
            boolean lav = tabelltype == Tabelltype.PENSJONIST || LAV_TRYGDEAVGIFT.contains(nummer);
            long grense = lav ? satser.lavGrenseTrygdeavgift() : satser.hoyGrenseTrygdeavgift();
            trygdeavgift = aarsinntekt.compareTo(BigDecimal.valueOf(grense)) > 0
                    ? prosent(aarsinntekt, lav ? LAV_TRYGDEAVGIFT_PROSENT : HOY_TRYGDEAVGIFT_PROSENT)
                    : prosent(aarsinntekt.subtract(BigDecimal.valueOf(satser.avgiftsfriTrygdeavgift())), TRYGDEAVGIFT_OPPTRAPPING_PROSENT);
        }

        BigDecimal trinnskatt = BigDecimal.ZERO;
        long[] grenser = satser.trinnskattGrenser();
        for (int i = 0; i < grenser.length && aarsinntekt.compareTo(BigDecimal.valueOf(grenser[i])) > 0; i++) {
            BigDecimal ovre = i + 1 < grenser.length ? aarsinntekt.min(BigDecimal.valueOf(grenser[i + 1])) : aarsinntekt;
            BigDecimal trinnprosent = finnmark && i == satser.finnmarkTrinn()
                    ? satser.finnmarkTrinnProsent()
                    : satser.trinnskattProsenter()[i];
            trinnskatt = trinnskatt.add(prosent(ovre.subtract(BigDecimal.valueOf(grenser[i])), trinnprosent));
        }

        BigDecimal sumSkatt = kommuneskatt.add(fellesskatt).add(trygdeavgift).add(trinnskatt);
        long trekk = sumSkatt
                .divide(trekkperioder, 0, tabelltype == Tabelltype.SJO ? RoundingMode.DOWN : RoundingMode.HALF_UP)
                .longValueExact();

        return Math.min(Math.max(trekk, 0), avrundetTrekkgrunnlag);
    }

    private static BigDecimal minstefradrag(BigDecimal aarsinntekt, BigDecimal prosentsats, long minimum, long maksimum, long lonnsfradrag) {
        BigDecimal fradrag = prosentMedGrenser(aarsinntekt, prosentsats, minimum, maksimum);
        BigDecimal lonnsfradragBegrenset = aarsinntekt.compareTo(BigDecimal.valueOf(lonnsfradrag)) < 0
                ? aarsinntekt.setScale(0, RoundingMode.DOWN)
                : BigDecimal.valueOf(lonnsfradrag);
        return fradrag.max(lonnsfradragBegrenset).min(aarsinntekt);
    }

    private static BigDecimal prosentMedGrenser(BigDecimal grunnlag, BigDecimal prosentsats, long minimum, long maksimum) {
        BigDecimal fradrag = prosent(grunnlag, prosentsats);
        if (fradrag.compareTo(BigDecimal.valueOf(maksimum)) > 0) return BigDecimal.valueOf(maksimum);
        if (fradrag.compareTo(BigDecimal.valueOf(minimum)) < 0) return BigDecimal.valueOf(minimum);
        return fradrag;
    }

    /**
     * Cobol-feltene har 9 desimaler, og COMPUTE uten ROUNDED trunkerer.
     */
    private static BigDecimal prosent(BigDecimal grunnlag, BigDecimal prosentsats) {
        return grunnlag.multiply(prosentsats).divide(HUNDRE).setScale(9, RoundingMode.DOWN);
    }

    private static long tabellfradrag(int tabellnr) {
        return switch (tabellnr) {
            case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 -> tabellnr * 4000L;
            case 10 -> 41000;
            case 11 -> 46000;
            case 12 -> 53000;
            case 13, 14, 15, 16, 17, 18, 19 -> (tabellnr - 7) * 10000L;
            case 20, 21, 22, 23, 24, 25, 26, 27, 28 -> -(tabellnr - 19) * 4000L;
            case 29 -> -41000;
            case 30 -> -46000;
            case 31 -> -53000;
            case 32 -> -60000;
            case 33 -> -67000;
            default -> 0;
        };
    }
}
