package FastRailStation.salvataggioDati;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import FastRailStation.model.Treno;
import FastRailStation.model.Utente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LeggiDati {

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

                    Treno treno;
                    if (hasManutenzione) {
                        LocalDate inizio   = LocalDate.parse(c[14].trim(), dateFmt);
                        LocalDate fine     = LocalDate.parse(c[15].trim(), dateFmt);
                        String    deposito = c[16].trim();
                        treno = new Treno(modello, provenienza, destinazione, compagnia, codice,
                                numMax, giornoArrivo, oraArrivo, giornoPartenza, oraPartenza,
                                intervallo, stato, inizio, fine, deposito, ritardo, postiOccupati);
                    } else {
                        treno = new Treno(modello, provenienza, destinazione, compagnia, codice,
                                numMax, giornoArrivo, oraArrivo, giornoPartenza, oraPartenza,
                                intervallo, stato, ritardo, postiOccupati);
                    }
                    treni.add(treno);
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
}