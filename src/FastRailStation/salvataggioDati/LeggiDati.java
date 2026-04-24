package FastRailStation.salvataggioDati;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import FastRailStation.model.Treno;
import FastRailStation.model.Utente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LeggiDati {

    /** Number of days ahead for which recurring trains are materialized in memory. */
    private static final int PRENOTAZIONE_GIORNI_FUTURI = 14;

    // FIX B5/B6: paths must match ScriviDati constants exactly (same casing)
    private static final String PATH_TRENI  = ScriviDati.PATH_TRENI;
    private static final String PATH_UTENTI = ScriviDati.PATH_UTENTI;

    public LeggiDati() {}

    // ── Utenti ────────────────────────────────────────────────────────────────

    public ArrayList<Utente> leggiUtente() {
        ArrayList<Utente> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PATH_UTENTI))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] d = linea.split("\\+");
                if (d.length < 11) continue; // skip malformed lines
                lista.add(new Utente(
                        d[0],  // nome
                        d[1],  // cognome
                        d[2],  // mail
                        d[3],  // nascita
                        d[4],  // password
                        d[5],  // numCell
                        d[6],  // nazione
                        d[7],  // citta
                        d[8],  // via
                        d[9],  // codice carta
                        d[10]  // scadenza
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
            // FIX: return empty list, never null, to avoid NPE in callers
        }
        return lista;
    }

    // ── Treni ─────────────────────────────────────────────────────────────────

    public ObservableList<Treno> leggiTreni() {
        ObservableList<Treno> treni = FXCollections.observableArrayList();
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss");

        try (BufferedReader br = new BufferedReader(new FileReader(PATH_TRENI))) {
            br.readLine(); // skip header row

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] c = line.split(",", -1); // -1 keeps trailing empty strings
                if (c.length < 14) continue;

                try {
                    String    modello        = c[0];
                    String    provenienza    = c[1];
                    String    destinazione   = c[2];
                    String    compagnia      = c[3];
                    String    codice         = c[4];
                    int       numMax         = Integer.parseInt(c[5].trim());
                    LocalDate giornoArrivo   = LocalDate.parse(c[6].trim(),  dateFmt);
                    LocalTime oraArrivo      = LocalTime.parse(c[7].trim(),  timeFmt);
                    LocalDate giornoPartenza = LocalDate.parse(c[8].trim(),  dateFmt);
                    LocalTime oraPartenza    = LocalTime.parse(c[9].trim(),  timeFmt);
                    int       intervallo     = Integer.parseInt(c[10].trim());
                    String    stato          = c[11].trim();
                    int       postiOccupati  = Integer.parseInt(c[12].trim());
                    int       ritardo        = Integer.parseInt(c[13].trim());

                    boolean hasManutenzione = c.length >= 17
                            && !c[14].trim().isEmpty()
                            && !c[15].trim().isEmpty();

                    if (hasManutenzione) {
                        LocalDate inizio   = LocalDate.parse(c[14].trim(), dateFmt);
                        LocalDate fine     = LocalDate.parse(c[15].trim(), dateFmt);
                        String    deposito = c[16].trim();
                        aggiungiOccorrenzeTreno(
                                treni,
                                modello, provenienza, destinazione, compagnia, codice, numMax,
                                giornoArrivo, oraArrivo, giornoPartenza, oraPartenza,
                                intervallo, stato, postiOccupati, ritardo,
                                inizio, fine, deposito
                        );
                    } else {
                        aggiungiOccorrenzeTreno(
                                treni,
                                modello, provenienza, destinazione, compagnia, codice, numMax,
                                giornoArrivo, oraArrivo, giornoPartenza, oraPartenza,
                                intervallo, stato, postiOccupati, ritardo,
                                null, null, null
                        );
                    }
                } catch (Exception ex) {
                    System.err.println("Skipping malformed train row: " + line);
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return treni;
    }

    private void aggiungiOccorrenzeTreno(
            ObservableList<Treno> treni,
            String modello, String provenienza, String destinazione, String compagnia, String codice,
            int numMax, LocalDate giornoArrivoBase, LocalTime oraArrivo,
            LocalDate giornoPartenzaBase, LocalTime oraPartenza,
            int intervallo, String stato, int postiOccupatiBase, int ritardoBase,
            LocalDate inizioManutenzioneBase, LocalDate fineManutenzioneBase, String depositoBase) {

        LocalDate oggi = LocalDate.now();
        LocalDate fineFinestra = oggi.plusDays(PRENOTAZIONE_GIORNI_FUTURI);

        // Recurrence fallback: if interval is invalid, keep a single occurrence.
        int step = intervallo > 0 ? intervallo : PRENOTAZIONE_GIORNI_FUTURI + 1;

        LocalDate giornoArrivo = giornoArrivoBase;
        LocalDate giornoPartenza = giornoPartenzaBase;
        while (giornoArrivo.isBefore(oggi)) {
            giornoArrivo = giornoArrivo.plusDays(step);
            giornoPartenza = giornoPartenza.plusDays(step);
        }

        while (!giornoArrivo.isAfter(fineFinestra)) {
            long delta = ChronoUnit.DAYS.between(giornoArrivoBase, giornoArrivo);
            int postiOccorrenza = giornoArrivo.equals(oggi) ? postiOccupatiBase : 0;
            int ritardoOccorrenza = giornoArrivo.equals(oggi) ? ritardoBase : 0;

            if (inizioManutenzioneBase != null && fineManutenzioneBase != null && depositoBase != null) {
                treni.add(new Treno(
                        modello, provenienza, destinazione, compagnia, codice, numMax,
                        giornoArrivo, oraArrivo, giornoPartenza, oraPartenza,
                        intervallo, stato,
                        inizioManutenzioneBase.plusDays(delta),
                        fineManutenzioneBase.plusDays(delta),
                        depositoBase,
                        ritardoOccorrenza, postiOccorrenza
                ));
            } else {
                treni.add(new Treno(
                        modello, provenienza, destinazione, compagnia, codice, numMax,
                        giornoArrivo, oraArrivo, giornoPartenza, oraPartenza,
                        intervallo, stato,
                        ritardoOccorrenza, postiOccorrenza
                ));
            }

            giornoArrivo = giornoArrivo.plusDays(step);
            giornoPartenza = giornoPartenza.plusDays(step);
        }
    }
}