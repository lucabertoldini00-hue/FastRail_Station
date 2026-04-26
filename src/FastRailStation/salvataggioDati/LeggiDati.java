package FastRailStation.salvataggioDati;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import FastRailStation.model.Biglietto;
import FastRailStation.model.Ruolo;
import FastRailStation.model.Treno;
import FastRailStation.model.Utente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LeggiDati {

    private static final String PATH_TRENI   = ScriviDati.PATH_TRENI;
    private static final String PATH_UTENTI  = ScriviDati.PATH_UTENTI;
    private static final String PATH_BIGLIETTI = ScriviDati.PATH_BIGLIETTI;

    public LeggiDati() {}

    // ── Utenti ────────────────────────────────────────────────────────────────

    public ArrayList<Utente> leggiUtente() {
        ArrayList<Utente> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PATH_UTENTI))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] d = linea.split("\\+");
                if (d.length < 11) continue;

                // Campo 11 (indice 11) = Ruolo — opzionale per retrocompatibilità
                Ruolo ruolo = d.length > 11 ? Ruolo.from(d[11]) : Ruolo.USER;

                lista.add(new Utente(
                        d[0],   // nome
                        d[1],   // cognome
                        d[2],   // mail
                        d[3],   // nascita
                        d[4],   // password
                        d[5],   // numCell
                        d[6],   // nazione
                        d[7],   // citta
                        d[8],   // via
                        d[9],   // codiceCarta
                        d[10],  // scadenza
                        ruolo
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ── Treni ─────────────────────────────────────────────────────────────────

    public ObservableList<Treno> leggiTreni() {
        ObservableList<Treno> treni = FXCollections.observableArrayList();
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss");

        try (BufferedReader br = new BufferedReader(new FileReader(PATH_TRENI))) {
            br.readLine(); // skip header

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] c = line.split(",", -1);
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
                    System.err.println("Riga treno malformata, saltata: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return treni;
    }

    // ── Biglietti ─────────────────────────────────────────────────────────────

    /**
     * Legge tutti i biglietti dal CSV.
     * Formato colonne (indice):
     * 0  CodiceBiglietto
     * 1  MailUtente
     * 2  CodiceTreno
     * 3  Provenienza
     * 4  Destinazione
     * 5  DataPartenza      (dd/MM/yyyy)
     * 6  OraPartenza       (HH:mm:ss)
     * 7  NAdulti
     * 8  NBambini
     * 9  NBagagli
     * 10 Classe
     * 11 Prezzo
     * 12 DataPrenotazione  (dd/MM/yyyy HH:mm:ss)
     */
    public ArrayList<Biglietto> leggiBiglietti() {
        ArrayList<Biglietto> lista = new ArrayList<>();
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dtFmt   = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        try (BufferedReader br = new BufferedReader(new FileReader(PATH_BIGLIETTI))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] c = line.split(",", -1);
                if (c.length < 13) continue;
                try {
                    Biglietto b = Biglietto.fromCsv(c, dateFmt, dtFmt);
                    if (b != null) lista.add(b);
                } catch (Exception ex) {
                    System.err.println("Biglietto malformato, saltato: " + line);
                }
            }
        } catch (IOException e) {
            // file potrebbe non esistere ancora
        }
        return lista;
    }

    /** Filtra i biglietti per mail utente. */
    public ArrayList<Biglietto> leggiBigliettiUtente(String mail) {
        ArrayList<Biglietto> tutti   = leggiBiglietti();
        ArrayList<Biglietto> filtrati = new ArrayList<>();
        for (Biglietto b : tutti)
            if (b.getMailUtente().equalsIgnoreCase(mail))
                filtrati.add(b);
        return filtrati;
    }
}