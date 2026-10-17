package no.skatteetaten.fastsetting.formueinntekt.forskudd.trekkrutine2014til2016;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Satsene som endres fra år til år. Anvendt minstefradrag er 88 % av det reelle.
 * Satsene for 2015 er utledet fra de publiserte trekktabellene, siden rutinebeskrivelsen ikke er tilgjengelig.
 */
record Satser(
        long klassefradrag1,
        long klassefradrag2,
        long klassefradrag1Finnmark,
        long klassefradrag2Finnmark,
        long[] trinnskattGrenser,
        BigDecimal[] trinnskattProsenter,
        int finnmarkTrinn,
        BigDecimal finnmarkTrinnProsent,
        BigDecimal skattore,
        BigDecimal fellesskattProsent,
        BigDecimal fellesskattProsentFinnmark,
        long avgiftsfriTrygdeavgift,
        long lavGrenseTrygdeavgift,
        long hoyGrenseTrygdeavgift,
        long maksAnvMinstefradrag,
        BigDecimal anvMinstefradragProsentPensjon,
        long maksAnvMinstefradragPensjon,
        long maksMinstefradrag,
        Map<String, Integer> overskytendeProsent) {

    private static final Map<String, Integer> OVERSKYTENDE_PROSENT_2014_2015 = Map.ofEntries(
            Map.entry("7300", 54), Map.entry("7400", 54),
            Map.entry("7350", 47), Map.entry("7450", 47),
            Map.entry("7500", 39), Map.entry("7600", 39),
            Map.entry("7550", 45), Map.entry("7650", 45),
            Map.entry("7700", 44), Map.entry("7800", 44),
            Map.entry("0100", 39), Map.entry("0200", 39),
            Map.entry("0101", 47), Map.entry("0201", 47),
            Map.entry("6300", 50), Map.entry("6400", 50),
            Map.entry("6350", 44), Map.entry("6450", 44),
            Map.entry("6500", 36), Map.entry("6600", 36),
            Map.entry("6550", 41), Map.entry("6650", 41),
            Map.entry("6700", 41), Map.entry("6800", 41),
            Map.entry("7150", 47), Map.entry("7250", 47),
            Map.entry("7160", 39), Map.entry("7260", 39),
            Map.entry("7170", 45), Map.entry("7270", 45));

    private static final Map<String, Integer> OVERSKYTENDE_PROSENT_2016 = Map.ofEntries(
            Map.entry("7300", 54), Map.entry("7400", 54),
            Map.entry("7350", 47), Map.entry("7450", 47),
            Map.entry("7500", 39), Map.entry("7600", 39),
            Map.entry("7550", 44), Map.entry("7650", 44),
            Map.entry("7700", 44), Map.entry("7800", 44),
            Map.entry("0100", 39), Map.entry("0200", 39),
            Map.entry("0101", 47), Map.entry("0201", 47),
            Map.entry("6300", 50), Map.entry("6400", 50),
            Map.entry("6350", 43), Map.entry("6450", 43),
            Map.entry("6500", 35), Map.entry("6600", 35),
            Map.entry("6550", 40), Map.entry("6650", 40),
            Map.entry("6700", 40), Map.entry("6800", 40),
            Map.entry("7150", 47), Map.entry("7250", 47),
            Map.entry("7160", 39), Map.entry("7260", 39),
            Map.entry("7170", 44), Map.entry("7270", 44));

    private static final Satser SATSER_2014 = new Satser(
            48800, 72000, 63800, 87000,
            new long[]{527400, 857300},
            new BigDecimal[]{new BigDecimal("9"), new BigDecimal("12")},
            0, new BigDecimal("7"),
            new BigDecimal("14.05"), new BigDecimal("12.95"), new BigDecimal("9.45"),
            39600, 49749, 58929,
            74052,
            new BigDecimal("23.76"), 61952,
            84150,
            OVERSKYTENDE_PROSENT_2014_2015);

    private static final Satser SATSER_2015 = new Satser(
            50400, 74250, 65900, 89750,
            new long[]{550550, 885600},
            new BigDecimal[]{new BigDecimal("9"), new BigDecimal("12")},
            0, new BigDecimal("7"),
            new BigDecimal("14.05"), new BigDecimal("12.95"), new BigDecimal("9.45"),
            49650, 62374, 73884,
            78364,
            new BigDecimal("25.52"), 63536,
            89050,
            OVERSKYTENDE_PROSENT_2014_2015);

    private static final Satser SATSER_2016 = new Satser(
            51750, 76250, 67250, 91750,
            new long[]{159800, 224900, 565400, 909500},
            new BigDecimal[]{new BigDecimal("0.44"), new BigDecimal("1.7"), new BigDecimal("10.7"), new BigDecimal("13.7")},
            2, new BigDecimal("8.7"),
            new BigDecimal("14.45"), new BigDecimal("10.55"), new BigDecimal("7.05"),
            49650, 62374, 73884,
            80476,
            new BigDecimal("25.52"), 64768,
            91450,
            OVERSKYTENDE_PROSENT_2016);

    static Satser forAar(int aar) {
        return switch (aar) {
            case 2014 -> SATSER_2014;
            case 2015 -> SATSER_2015;
            case 2016 -> SATSER_2016;
            default -> throw new IllegalArgumentException("Har ikke trekkrutine for " + aar);
        };
    }

    int overskytendeProsent(Tabellnummer tabellnummer) {
        if (tabellnummer.pensjonist) {
            return 48;
        }
        return overskytendeProsent.getOrDefault(tabellnummer.nummer, 54);
    }
}
