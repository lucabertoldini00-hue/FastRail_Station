package FastRailStation.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import FastRailStation.salvataggioDati.LeggiDati;
import FastRailStation.salvataggioDati.ScriviDati;

public class GestioneTreni {

    private static GestioneTreni instance;

    private ObservableList<Treno> elencoTreniPartenza     = FXCollections.observableArrayList();
    private ObservableList<Treno> elencoTreniArrivo       = FXCollections.observableArrayList();
    private ObservableList<Treno> elencoTreniTutti        = FXCollections.observableArrayList();
    private ObservableList<Treno> elencoTreniDeposito     = FXCollections.observableArrayList();
    private ObservableList<Treno> elencoTreniTerra        = FXCollections.observableArrayList();
    private ObservableList<Treno> elencoTreniManutenzione = FXCollections.observableArrayList();

    private ArrayList<Boolean> binari = new ArrayList<>();

    private String    compArrivi,       destArrivi;
    private LocalDate dateArrivi;

    private String    compPartenze,     destPartenze;
    private LocalDate datePartenze;

    private String    compTerra,        destTerra;
    private LocalDate dateTerra;

    private String    compManutenzione, destManutenzione;
    private LocalDate dateManutenzione;

    private ScriviDati scrivi;
    private LeggiDati  leggi;

    private GestioneTreni() {
        scrivi = new ScriviDati();
        leggi  = new LeggiDati();
        riempiBinari();
        caricaDati();
    }

    public static GestioneTreni getInstance() {
        if (instance == null)
            instance = new GestioneTreni();
        return instance;
    }

    // ── Track initialisation ─────────────────────────────────────────────────

    public void riempiBinari() {
        binari.clear();
        for (int i = 0; i < 70; i++) binari.add(false);
    }

    public ArrayList<Boolean> getBinari() { return binari; }

    /**
     * Returns a random free track index (1-based to be human-readable),
     * or -1 when all tracks are occupied.
     */
    public int assegnaBinario() {
        for (int attempts = 0; attempts < 200; attempts++) {
            int index = (int) (Math.random() * 70);
            if (!binari.get(index)) {
                binari.set(index, true);
                return index + 1;   // binari 1-70, not 0-69
            }
        }
        return -1;
    }

    // ── Add / remove ─────────────────────────────────────────────────────────

    public void addTreno(String modello, String provenienza, String destinazione,
                         String compagnia, String codice, int numMax,
                         LocalDate giornoArrivo, LocalTime oraArrivo,
                         LocalDate giornoPartenza, LocalTime oraPartenza,
                         int intervallo, String stato, int ritardo, int numPosti) {

        Treno treno = new Treno(modello, provenienza, destinazione, compagnia, codice, numMax,
                giornoArrivo, oraArrivo, giornoPartenza, oraPartenza,
                intervallo, stato, ritardo, numPosti);
        treno.setBinario(assegnaBinario());
        synchronized (elencoTreniTutti) { elencoTreniTutti.add(treno); }
        scriviDati();
    }

    public void addTreno(String modello, String provenienza, String destinazione,
                         String compagnia, String codice, int numMax,
                         LocalDate giornoArrivo, LocalTime oraArrivo,
                         LocalDate giornoPartenza, LocalTime oraPartenza,
                         int intervallo, String stato,
                         LocalDate inizioManutenzione, LocalDate fineManutenzione,
                         String deposito, int ritardo, int numPosti) {

        Treno treno = new Treno(modello, provenienza, destinazione, compagnia, codice, numMax,
                giornoArrivo, oraArrivo, giornoPartenza, oraPartenza, intervallo, stato,
                inizioManutenzione, fineManutenzione, deposito, ritardo, numPosti);
        treno.setBinario(assegnaBinario());
        synchronized (elencoTreniTutti) { elencoTreniTutti.add(treno); }
        scriviDati();
    }

    /**
     * FIX BINARIO: assegna sempre un binario valido quando si carica da file.
     * Il costruttore di Treno inizializza binario a -1; senza questo fix
     * tutti i treni caricati dal CSV avrebbero binario -1.
     */
    public void addTreno(Treno treno) {
        if (treno.getBinario() <= 0) {
            int b = assegnaBinario();
            if (b != -1) treno.setBinario(b);
        }
        synchronized (elencoTreniTutti) { elencoTreniTutti.add(treno); }
    }

    public void rimuoviTreno(Treno treno) {
        // Libera il binario prima di rimuovere
        int b = treno.getBinario();
        if (b >= 1 && b <= 70) binari.set(b - 1, false);
        synchronized (elencoTreniTutti) { elencoTreniTutti.remove(treno); }
    }

    public void aggiornaLista() { scriviDati(); }

    public void scriviDati() {
        synchronized (elencoTreniTutti) { scrivi.scriviTreniFine(elencoTreniTutti); }
    }

    // ── Observable list accessors ─────────────────────────────────────────────

    public ObservableList<Treno> getElencoLista()             { return elencoTreniTutti; }
    public ObservableList<Treno> getElencoListaArrivi()       { return elencoTreniArrivo; }
    public ObservableList<Treno> getElencoListaPartenze()     { return elencoTreniPartenza; }
    public ObservableList<Treno> getElencoListaTerra()        { return elencoTreniTerra; }
    public ObservableList<Treno> getElencoListaManutenzione() { return elencoTreniManutenzione; }

    // ── Date filters ─────────────────────────────────────────────────────────

    public void setDataPartenza(LocalDate data) {
        snapshot();
        elencoTreniPartenza.clear();
        LocalTime now = LocalDate.now().isEqual(data) ? LocalTime.now() : null;
        for (Treno t : elencoTreniDeposito) {
            String stato = t.getStato();
            boolean statoValido = "In partenza".equals(stato) || "In attesa".equals(stato)
                    || stato == null || stato.isEmpty();
            LocalTime ora = t.getOraPartenza();
            boolean nonPassato = now == null || (ora != null && !ora.isBefore(now));
            if (t.getGiornoPartenza().isEqual(data) && statoValido && nonPassato)
                elencoTreniPartenza.add(t);
        }
        bubbleSortByOraPartenza(elencoTreniPartenza);
    }

    public void setDataPartenzaAdmin(LocalDate data) {
        snapshot();
        elencoTreniPartenza.clear();
        for (Treno t : elencoTreniDeposito)
            if (t.getGiornoPartenza().isEqual(data))
                elencoTreniPartenza.add(t);
        bubbleSortByOraPartenza(elencoTreniPartenza);
    }

    public void setDataArrivo(LocalDate data) {
        snapshot();
        elencoTreniArrivo.clear();
        LocalTime now = LocalDate.now().isEqual(data) ? LocalTime.now() : null;
        for (Treno t : elencoTreniDeposito) {
            String stato = t.getStato();
            boolean statoValido = "In arrivo".equals(stato) || "In attesa".equals(stato)
                    || stato == null || stato.isEmpty();
            LocalTime ora = t.getOraArrivo();
            boolean nonPassato = now == null || (ora != null && !ora.isBefore(now));
            if (t.getGiornoArrivo().isEqual(data) && statoValido && nonPassato)
                elencoTreniArrivo.add(t);
        }
        bubbleSortByOraArrivo(elencoTreniArrivo);
    }

    public void setDataArrivoAdmin(LocalDate data) {
        snapshot();
        elencoTreniArrivo.clear();
        for (Treno t : elencoTreniDeposito)
            if (t.getGiornoArrivo().isEqual(data))
                elencoTreniArrivo.add(t);
        bubbleSortByOraArrivo(elencoTreniArrivo);
    }

    public void setDataTerra(LocalDate data) {
        snapshot();
        elencoTreniTerra.clear();
        for (Treno t : elencoTreniDeposito)
            if (t.getGiornoArrivo().isEqual(data) && t.getStato().equals("In attesa"))
                elencoTreniTerra.add(t);
        bubbleSortByOraPartenza(elencoTreniTerra);
    }

    public void setDataManutenzione(LocalDate data) {
        snapshot();
        elencoTreniManutenzione.clear();
        for (Treno t : elencoTreniDeposito) {
            LocalDate inizio = t.getInizioManutenzione();
            LocalDate fine   = t.getFineManutenzione();
            if (t.getStato().equals("In manutenzione") &&
                    inizio != null && fine != null &&
                    (data.isEqual(inizio) || data.isAfter(inizio)) &&
                    (data.isEqual(fine)   || data.isBefore(fine)))
                elencoTreniManutenzione.add(t);
        }
        bubbleSortByOraPartenza(elencoTreniManutenzione);
    }

    // ── Text filters ─────────────────────────────────────────────────────────

    public void aggiornaArrivo(String parola) {
        String p = parola == null ? "" : parola.toLowerCase();
        ObservableList<Treno> snap = FXCollections.observableArrayList(elencoTreniArrivo);
        elencoTreniArrivo.clear();
        for (Treno t : snap)
            if (t.getCompagnia().toLowerCase().contains(p) ||
                    t.getProvenienza().toLowerCase().contains(p) ||
                    t.getDestinazione().toLowerCase().contains(p))
                elencoTreniArrivo.add(t);
    }

    public void aggiornaPartenza(String parola) {
        String p = parola == null ? "" : parola.toLowerCase();
        ObservableList<Treno> snap = FXCollections.observableArrayList(elencoTreniPartenza);
        elencoTreniPartenza.clear();
        for (Treno t : snap)
            if (t.getCompagnia().toLowerCase().contains(p) ||
                    t.getDestinazione().toLowerCase().contains(p) ||
                    t.getProvenienza().toLowerCase().contains(p))
                elencoTreniPartenza.add(t);
    }

    public void aggiornaArrivoAdmin(String parola, String compagnia) {
        String p = parola == null ? "" : parola.toLowerCase();
        String c = compagnia == null ? "" : compagnia.toLowerCase();
        ObservableList<Treno> snap = FXCollections.observableArrayList(elencoTreniArrivo);
        elencoTreniArrivo.clear();
        for (Treno t : snap)
            if (t.getCompagnia().toLowerCase().contains(c) &&
                    (t.getProvenienza().toLowerCase().contains(p) ||
                            t.getDestinazione().toLowerCase().contains(p)))
                elencoTreniArrivo.add(t);
    }

    public void aggiornaPartenzaAdmin(String parola, String compagnia) {
        String p = parola == null ? "" : parola.toLowerCase();
        String c = compagnia == null ? "" : compagnia.toLowerCase();
        ObservableList<Treno> snap = FXCollections.observableArrayList(elencoTreniPartenza);
        elencoTreniPartenza.clear();
        for (Treno t : snap)
            if (t.getCompagnia().toLowerCase().contains(c) &&
                    (t.getDestinazione().toLowerCase().contains(p) ||
                            t.getProvenienza().toLowerCase().contains(p)))
                elencoTreniPartenza.add(t);
    }

    public void aggiornaTerraAdmin(String parola, String compagnia) {
        String p = parola == null ? "" : parola.toLowerCase();
        String c = compagnia == null ? "" : compagnia.toLowerCase();
        ObservableList<Treno> snap = FXCollections.observableArrayList(elencoTreniTerra);
        elencoTreniTerra.clear();
        for (Treno t : snap)
            if (t.getCompagnia().toLowerCase().contains(c) &&
                    (t.getDestinazione().toLowerCase().contains(p) ||
                            t.getProvenienza().toLowerCase().contains(p)))
                elencoTreniTerra.add(t);
    }

    public void aggiornaManutenzioneAdmin(String parola, String compagnia) {
        String p = parola == null ? "" : parola.toLowerCase();
        String c = compagnia == null ? "" : compagnia.toLowerCase();
        ObservableList<Treno> snap = FXCollections.observableArrayList(elencoTreniManutenzione);
        elencoTreniManutenzione.clear();
        for (Treno t : snap)
            if (t.getCompagnia().toLowerCase().contains(c) &&
                    (t.getDestinazione().toLowerCase().contains(p) ||
                            t.getProvenienza().toLowerCase().contains(p)))
                elencoTreniManutenzione.add(t);
    }

    // ── Reset ─────────────────────────────────────────────────────────────────

    public void resetPartenze() {
        elencoTreniPartenza.clear();
        synchronized (elencoTreniTutti) { elencoTreniPartenza.addAll(elencoTreniTutti); }
        bubbleSortByOraPartenza(elencoTreniPartenza);
    }

    public void resetArrivi() {
        elencoTreniArrivo.clear();
        synchronized (elencoTreniTutti) { elencoTreniArrivo.addAll(elencoTreniTutti); }
        bubbleSortByOraArrivo(elencoTreniArrivo);
    }

    public void resetFilter() { resetArrivi(); resetPartenze(); }

    // ── Sorting ───────────────────────────────────────────────────────────────

    private void bubbleSortByOraPartenza(ObservableList<Treno> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++)
                if (list.get(j).getOraPartenza().isAfter(list.get(j + 1).getOraPartenza())) {
                    Treno tmp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, tmp);
                }
    }

    private void bubbleSortByOraArrivo(ObservableList<Treno> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++)
                if (list.get(j).getOraArrivo().isAfter(list.get(j + 1).getOraArrivo())) {
                    Treno tmp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, tmp);
                }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void snapshot() {
        elencoTreniDeposito.clear();
        synchronized (elencoTreniTutti) { elencoTreniDeposito.addAll(elencoTreniTutti); }
    }

    private void caricaDati() {
        ObservableList<Treno> base = leggi.leggiTreni();
        for (Treno treno : base) addTreno(treno);
        expandFutureTreni(base, 14);
        // Keep startup-expanded recurrences in memory only; persist only on explicit edits/bookings.
    }

    private void expandFutureTreni(ObservableList<Treno> base, int maxDays) {
        LocalDate today = LocalDate.now();
        for (Treno t : base) {
            int shiftMinutes = randomTimeShiftMinutes();
            LocalTime oraArrivo = shiftTime(t.getOraArrivo(), shiftMinutes);
            LocalTime oraPartenza = shiftTime(t.getOraPartenza(), shiftMinutes);
            for (int d = 1; d <= maxDays; d++) {
                LocalDate giornoArrivo = today.plusDays(d);
                LocalDate giornoPartenza = today.plusDays(d);
                Treno clone = cloneWithDates(t, giornoArrivo, oraArrivo, giornoPartenza, oraPartenza);
                addTrenoFuture(clone);
            }
        }
    }

    private void addTrenoFuture(Treno treno) {
        // Treni futuri: binario/stato/ritardo restano vuoti o default.
        synchronized (elencoTreniTutti) { elencoTreniTutti.add(treno); }
    }

    private Treno cloneWithDates(Treno t, LocalDate giornoArrivo, LocalTime oraArrivo,
                                 LocalDate giornoPartenza, LocalTime oraPartenza) {
        int max = t.getPostiMassimi();
        int posti = randomBetween(0, Math.max(0, max));
        int ritardo = 0;
        String stato = "";

        boolean hasManutenzione = t.getInizioManutenzione() != null
                && t.getFineManutenzione() != null
                && t.getDeposito() != null;

        if (hasManutenzione) {
            LocalDate inizio = t.getInizioManutenzione().plusDays(giornoArrivo.toEpochDay() - t.getGiornoArrivo().toEpochDay());
            LocalDate fine = t.getFineManutenzione().plusDays(giornoArrivo.toEpochDay() - t.getGiornoArrivo().toEpochDay());
            return new Treno(t.getModello(), t.getProvenienza(), t.getDestinazione(), t.getCompagnia(),
                    t.getCodice(), max, giornoArrivo, oraArrivo, giornoPartenza, oraPartenza,
                    t.getIntervallo(), stato, inizio, fine, t.getDeposito(), ritardo, posti);
        }

        return new Treno(t.getModello(), t.getProvenienza(), t.getDestinazione(), t.getCompagnia(),
                t.getCodice(), max, giornoArrivo, oraArrivo, giornoPartenza, oraPartenza,
                t.getIntervallo(), stato, ritardo, posti);
    }

    private int randomTimeShiftMinutes() {
        return ThreadLocalRandom.current().nextInt(-10, 11);
    }

    private int randomBetween(int min, int max) {
        if (max <= min) return min;
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private LocalTime shiftTime(LocalTime base, int minutes) {
        return base != null ? base.plusMinutes(minutes) : null;
    }

    // ── FIX B3: dedicated getters/setters per filter category ─────────────────

    public void setTreniInArrivo(String comp, String dest, LocalDate date)
    { compArrivi = comp; destArrivi = dest; dateArrivi = date; }
    public String    getCompArrivi()  { return compArrivi; }
    public String    getDestArrivi()  { return destArrivi; }
    public LocalDate getDateArrivi()  { return dateArrivi; }

    public void setTreniInPartenze(String comp, String dest, LocalDate date)
    { compPartenze = comp; destPartenze = dest; datePartenze = date; }
    public String    getCompPartenze()  { return compPartenze; }
    public String    getDestPartenze()  { return destPartenze; }
    public LocalDate getDatePartenze()  { return datePartenze; }

    public void setTreniInTerra(String comp, String dest, LocalDate date)
    { compTerra = comp; destTerra = dest; dateTerra = date; }
    public String    getCompTerra()  { return compTerra; }
    public String    getDestTerra()  { return destTerra; }
    public LocalDate getDateTerra()  { return dateTerra; }

    public void setTreniInManutenzione(String comp, String dest, LocalDate date)
    { compManutenzione = comp; destManutenzione = dest; dateManutenzione = date; }
    public String    getCompManutenzione()  { return compManutenzione; }
    public String    getDestManutenzione()  { return destManutenzione; }
    public LocalDate getDateManutenzione()  { return dateManutenzione; }
}
