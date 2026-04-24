package FastRailStation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a confirmed train booking.
 * FIX N13: was an empty stub — now holds all booking data and can be serialised.
 */
public class Biglietto {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final String        codiceBiglietto;   // unique ticket ID
    private final String        mailUtente;
    private final String        codiceTreno;
    private final String        provenienza;
    private final String        destinazione;
    private final LocalDate     dataPartenza;
    private final String        oraPartenza;
    private final int           nAdulti;
    private final int           nBambini;
    private final int           nBagagli;
    private final String        classe;            // "Prima classe" | "Seconda classe"
    private final int           prezzoTotale;
    private final LocalDateTime dataPrenotazione;

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
        // Unique code: treno + epoch millis
        this.codiceBiglietto  = codiceTreno + "-" + System.currentTimeMillis();
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

    /**
     * Returns a CSV line for persistence:
     * codiceBiglietto,mailUtente,codiceTreno,provenienza,destinazione,
     * dataPartenza,oraPartenza,nAdulti,nBambini,nBagagli,classe,prezzo,dataPrenotazione
     */
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