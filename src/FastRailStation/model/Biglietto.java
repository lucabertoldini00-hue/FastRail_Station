package FastRailStation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Biglietto {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final String        codiceBiglietto;
    private final String        mailUtente;
    private final String        codiceTreno;
    private final String        provenienza;
    private final String        destinazione;
    private final LocalDate     dataPartenza;
    private final String        oraPartenza;
    private final int           nAdulti;
    private final int           nBambini;
    private final int           nBagagli;
    private final String        classe;
    private final int           prezzoTotale;
    private final LocalDateTime dataPrenotazione;

    // ── Costruttore principale (nuova prenotazione) ───────────────────────────

    public Biglietto(String mailUtente, String codiceTreno, String provenienza,
                     String destinazione, LocalDate dataPartenza, String oraPartenza,
                     int nAdulti, int nBambini, int nBagagli, String classe, int prezzoTotale) {
        this.mailUtente       = mailUtente;
        this.codiceTreno      = codiceTreno;
        this.provenienza      = provenienza;
        this.destinazione     = destinazione;
        this.dataPartenza     = dataPartenza;
        this.oraPartenza      = oraPartenza;
        this.nAdulti          = nAdulti;
        this.nBambini         = nBambini;
        this.nBagagli         = nBagagli;
        this.classe           = classe;
        this.prezzoTotale     = prezzoTotale;
        this.dataPrenotazione = LocalDateTime.now();
        this.codiceBiglietto  = codiceTreno + "-" + System.currentTimeMillis();
    }

    // ── Costruttore privato (lettura da CSV) ──────────────────────────────────

    private Biglietto(String codiceBiglietto, String mailUtente, String codiceTreno,
                      String provenienza, String destinazione, LocalDate dataPartenza,
                      String oraPartenza, int nAdulti, int nBambini, int nBagagli,
                      String classe, int prezzoTotale, LocalDateTime dataPrenotazione) {
        this.codiceBiglietto  = codiceBiglietto;
        this.mailUtente       = mailUtente;
        this.codiceTreno      = codiceTreno;
        this.provenienza      = provenienza;
        this.destinazione     = destinazione;
        this.dataPartenza     = dataPartenza;
        this.oraPartenza      = oraPartenza;
        this.nAdulti          = nAdulti;
        this.nBambini         = nBambini;
        this.nBagagli         = nBagagli;
        this.classe           = classe;
        this.prezzoTotale     = prezzoTotale;
        this.dataPrenotazione = dataPrenotazione;
    }

    // ── Factory: deserializzazione da riga CSV ────────────────────────────────

    /**
     * Crea un Biglietto da un array di colonne CSV già splittato.
     * Restituisce null se i dati sono insufficienti o malformati.
     */
    public static Biglietto fromCsv(String[] c,
                                    DateTimeFormatter dateFmt,
                                    DateTimeFormatter dtFmt) {
        if (c == null || c.length < 13) return null;
        try {
            return new Biglietto(
                    c[0].trim(),                                              // codiceBiglietto
                    c[1].trim(),                                              // mailUtente
                    c[2].trim(),                                              // codiceTreno
                    c[3].trim(),                                              // provenienza
                    c[4].trim(),                                              // destinazione
                    LocalDate.parse(c[5].trim(), dateFmt),                   // dataPartenza
                    c[6].trim(),                                              // oraPartenza
                    Integer.parseInt(c[7].trim()),                           // nAdulti
                    Integer.parseInt(c[8].trim()),                           // nBambini
                    Integer.parseInt(c[9].trim()),                           // nBagagli
                    c[10].trim(),                                             // classe
                    Integer.parseInt(c[11].trim()),                          // prezzoTotale
                    LocalDateTime.parse(c[12].trim(), dtFmt)                 // dataPrenotazione
            );
        } catch (Exception e) {
            return null;
        }
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String        getCodiceBiglietto()  { return codiceBiglietto; }
    public String        getMailUtente()        { return mailUtente; }
    public String        getCodiceTreno()       { return codiceTreno; }
    public String        getProvenienza()       { return provenienza; }
    public String        getDestinazione()      { return destinazione; }
    public LocalDate     getDataPartenza()      { return dataPartenza; }
    public String        getOraPartenza()       { return oraPartenza; }
    public int           getNAdulti()           { return nAdulti; }
    public int           getNBambini()          { return nBambini; }
    public int           getNBagagli()          { return nBagagli; }
    public String        getClasse()            { return classe; }
    public int           getPrezzoTotale()      { return prezzoTotale; }
    public LocalDateTime getDataPrenotazione()  { return dataPrenotazione; }

    // ── Serializzazione CSV ───────────────────────────────────────────────────

    public String toCsvLine() {
        return String.join(",",
                codiceBiglietto,
                mailUtente,
                codiceTreno,
                provenienza,
                destinazione,
                dataPartenza.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                oraPartenza,
                String.valueOf(nAdulti),
                String.valueOf(nBambini),
                String.valueOf(nBagagli),
                classe,
                String.valueOf(prezzoTotale),
                dataPrenotazione.format(FMT));
    }

    @Override
    public String toString() {
        return "[" + codiceBiglietto + "] " + provenienza + " → " + destinazione
                + "  " + dataPartenza + "  " + classe
                + "  adulti:" + nAdulti + " bambini:" + nBambini
                + "  €" + prezzoTotale;
    }
}