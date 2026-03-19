package FastRailStation.salvataggioDati;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import javafx.collections.ObservableList;
import FastRailStation.model.Biglietto;
import FastRailStation.model.Treno;
import FastRailStation.model.Utente;

public class ScriviDati {

    // FIX B5/B6/B7: single canonical path constants, matching LeggiDati exactly
    static final String PATH_TRENI    = "./src/FastRailStation/salvataggioDati/treni.csv";
    static final String PATH_UTENTI   = "./src/FastRailStation/salvataggioDati/utenti.txt";
    static final String PATH_BIGLIETTI= "./src/FastRailStation/salvataggioDati/biglietti.csv";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ScriviDati() {}

    // ── Utenti ────────────────────────────────────────────────────────────────

    public void scriviUtenti(ArrayList<Utente> utenti) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(PATH_UTENTI))) {
            for (Utente u : utenti) {
                w.write(u.getNome()            + "+");
                w.write(u.getCognome()         + "+");
                w.write(u.getMail()            + "+");
                w.write(u.getNascita()         + "+");
                w.write(u.getPassword()        + "+");
                w.write(u.getNumeroCellulare() + "+");
                w.write(u.getNazioneResideza() + "+");
                w.write(u.getCittaResidenza()  + "+");
                w.write(u.getViaResidenza()    + "+");
                w.write(u.getCodiceCarta()     + "+");
                w.write(u.getScadenza()        + "+");
                w.newLine();
            }
            w.flush();
        } catch (IOException e) {
            System.err.println("Errore scrittura utenti: " + e.getMessage());
        }
    }

    // ── Treni ─────────────────────────────────────────────────────────────────

    /**
     * FIX B7: path was ./src/mainFolder/... — now uses PATH_TRENI constant.
     * FIX B8: writes the full 17-column format (was 11 in old scriviTreni).
     * Non-maintenance rows write empty placeholders so column count is always 17.
     */
    public void scriviTreniFine(ObservableList<Treno> treni) {
        try (FileWriter w = new FileWriter(PATH_TRENI)) {
            w.write("Modello,Provenienza,Destinazione,Compagnia,Codice,NumMax," +
                    "GiornoArrivo,OraArrivo,GiornoPartenza,OraPartenza,Intervallo," +
                    "Stato,PostiOccupati,Ritardo,InizioManutenzione,FineManutenzione,Deposito\n");

            for (Treno t : treni) {
                boolean hasManutenzione = t.getInizioManutenzione() != null
                        && t.getFineManutenzione() != null
                        && t.getDeposito() != null;

                String inizio  = hasManutenzione ? t.getInizioManutenzione().format(DATE_FMT) : "";
                String fine    = hasManutenzione ? t.getFineManutenzione().format(DATE_FMT)   : "";
                String deposito= hasManutenzione ? t.getDeposito()                            : "";

                w.write(String.format("%s,%s,%s,%s,%s,%d,%s,%s,%s,%s,%d,%s,%d,%d,%s,%s,%s\n",
                        t.getModello(),
                        t.getProvenienza(),
                        t.getDestinazione(),
                        t.getCompagnia(),
                        t.getCodice(),
                        t.getPostiMassimi(),
                        t.getGiornoArrivoString(),
                        t.getOraArrivoString(),
                        t.getGiornoPartenzaString(),
                        t.getOraPartenzaString(),
                        t.getIntervallo(),
                        t.getStato(),
                        t.getNumeroPostiOccupati(),
                        t.getRitardo(),
                        inizio,
                        fine,
                        deposito));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Biglietti ─────────────────────────────────────────────────────────────

    /**
     * FIX N13: appends a confirmed ticket to the biglietti CSV.
     */
    public void scríviBiglietto(Biglietto b) {
        try (FileWriter w = new FileWriter(PATH_BIGLIETTI, true)) { // append mode
            w.write(b.toCsvLine() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}