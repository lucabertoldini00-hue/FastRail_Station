package FastRailStation.model;

import java.time.*;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.*;

public class Treno {
    // Proprietà finali (identificative)
    private final StringProperty modello;
    private final StringProperty compagnia;
    private final StringProperty codiceRegistrazione;

    // Proprietà dinamiche
    private final StringProperty provenienza;
    private final StringProperty destinazione;
    private final StringProperty stato;
    private final IntegerProperty binario;
    private final StringProperty deposito;
    private final IntegerProperty numeroPostiOccupati;
    private final IntegerProperty ritardo;

    // Proprietà temporali
    private final ObjectProperty<LocalDate> giornoDiArrivo;
    private final ObjectProperty<LocalTime> oraArrivo;
    private final ObjectProperty<LocalDate> giornoDiPartenza;
    private final ObjectProperty<LocalTime> oraPartenza;
    private final ObjectProperty<LocalDate> inizioManutenzione;
    private final ObjectProperty<LocalDate> fineManutenzione;

    // Variabili primitive e utility
    private int numeroMassimoPasseggeri;
    private int intervalloDiGiorni;
    private final DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter formatterOre = DateTimeFormatter.ofPattern("HH:mm:ss");

    private boolean inArrivo = false;
    private boolean inPartenza = false;
    private boolean inManutenzione = false;
    private boolean inCorsa = false;
    private boolean inAttesa = false;

    // ---------------- COSTRUTTORE BASE ----------------
    public Treno(String modello, String provenienza, String destinazione, String compagnia, String codice,
                 int numMax, LocalDate giornoDiArrivo, LocalTime oraArrivo,
                 LocalDate giornoPartenza, LocalTime oraPartenza, int intervallo,
                 String stato, int ritardo, int numPosti) {

        // Logica di aggiornamento date se scadute
        while (giornoDiArrivo.isBefore(LocalDate.now())) {
            giornoDiArrivo = giornoDiArrivo.plusDays(intervallo);
            giornoPartenza = giornoPartenza.plusDays(intervallo);
            ritardo = 0;
            numPosti = 0;
        }

        this.modello = new SimpleStringProperty(modello);
        this.provenienza = new SimpleStringProperty(provenienza);
        this.destinazione = new SimpleStringProperty(destinazione);
        this.compagnia = new SimpleStringProperty(compagnia);
        this.codiceRegistrazione = new SimpleStringProperty(codice);
        this.numeroMassimoPasseggeri = numMax;
        this.numeroPostiOccupati = new SimpleIntegerProperty(numPosti);
        this.giornoDiArrivo = new SimpleObjectProperty<>(giornoDiArrivo);
        this.oraArrivo = new SimpleObjectProperty<>(oraArrivo);
        this.giornoDiPartenza = new SimpleObjectProperty<>(giornoPartenza);
        this.oraPartenza = new SimpleObjectProperty<>(oraPartenza);
        this.intervalloDiGiorni = intervallo;
        this.binario = new SimpleIntegerProperty(-1);
        this.deposito = new SimpleStringProperty(null);
        this.inizioManutenzione = new SimpleObjectProperty<>(null);
        this.fineManutenzione = new SimpleObjectProperty<>(null);
        this.stato = new SimpleStringProperty(stato);
        this.ritardo = new SimpleIntegerProperty(ritardo);
    }

    // ---------------- COSTRUTTORE CON MANUTENZIONE ----------------
    public Treno(String modello, String provenienza, String destinazione, String compagnia, String codice,
                 int numMax, LocalDate giornoDiArrivo, LocalTime oraArrivo,
                 LocalDate giornoPartenza, LocalTime oraPartenza, int intervallo,
                 String stato, LocalDate inizioManutenzione, LocalDate fineManutenzione,
                 String stazione, int ritardo, int numPosti) {

        // Chiama il costruttore base per evitare duplicazione di codice
        this(modello, provenienza, destinazione, compagnia, codice, numMax, giornoDiArrivo,
                oraArrivo, giornoPartenza, oraPartenza, intervallo, stato, ritardo, numPosti);

        this.deposito.set(stazione);
        this.inizioManutenzione.set(inizioManutenzione);
        this.fineManutenzione.set(fineManutenzione);
    }

    // ---------------- GETTER / SETTER / PROPERTY ----------------

    public String getModello() { return modello.get(); }
    public StringProperty modelloProperty() { return modello; }

    public String getProvenienza() { return provenienza.get(); }
    public void setProvenienza(String p) { this.provenienza.set(p); }
    public StringProperty getProvenienzaProperty() { return provenienza; }

    public String getDestinazione() { return destinazione.get(); }
    public void setDestinazione(String d) { this.destinazione.set(d); }
    public StringProperty getDestinazioneProperty() { return destinazione; }

    public String getCompagnia() { return compagnia.get(); }
    public StringProperty getCompagniaProperty() { return compagnia; }

    public String getCodice() { return codiceRegistrazione.get(); }
    public StringProperty getCodiceProperty() { return codiceRegistrazione; }

    public int getPostiMassimi() { return numeroMassimoPasseggeri; }
    public void setPostiMassimi(int max) { this.numeroMassimoPasseggeri = max; }

    public int getNumeroPostiOccupati() { return numeroPostiOccupati.get(); }
    public void setNumeroPostiOccupati(int n) { this.numeroPostiOccupati.set(n); }
    public IntegerProperty getNumeroPostiOccupatiProperty() { return numeroPostiOccupati; }

    public int getBinario() { return binario.get(); }
    public void setBinario(int b) { this.binario.set(b); }
    public IntegerProperty getBinarioProperty() { return binario; }

    public String getDeposito() { return deposito.get(); }
    public void setDeposito(String d) { this.deposito.set(d); }
    public StringProperty getDepositoProperty() { return deposito; }

    public int getRitardo() { return ritardo.get(); }
    public void setRitardo(int r) { this.ritardo.set(r); }
    public IntegerProperty getRitardoProperty() { return ritardo; }

    public String getStato() { return stato.get(); }
    public void setStato(String s) { this.stato.set(s); }
    public StringProperty getStatoProperty() { return stato; }

    // Date e Orari
    public LocalDate getGiornoArrivo() { return giornoDiArrivo.get(); }
    public ObjectProperty<LocalDate> getGiornoArrivoProperty() { return giornoDiArrivo; }
    public String getGiornoArrivoString() { return giornoDiArrivo.get().format(formatterData); }

    public LocalTime getOraArrivo() { return oraArrivo.get(); }
    public ObjectProperty<LocalTime> getOraArrivoProperty() { return oraArrivo; }
    public String getOraArrivoString() { return oraArrivo.get().format(formatterOre); }

    public LocalDate getGiornoPartenza() { return giornoDiPartenza.get(); }
    public ObjectProperty<LocalDate> giornoPartenzaProperty() { return giornoDiPartenza; }
    public String getGiornoPartenzaString() { return giornoDiPartenza.get().format(formatterData); }

    public LocalTime getOraPartenza() { return oraPartenza.get(); }
    public ObjectProperty<LocalTime> getOraPartenzaProperty() { return oraPartenza; }
    public String getOraPartenzaString() { return oraPartenza.get().format(formatterOre); }

    // Manutenzione
    public LocalDate getInizioManutenzione() { return inizioManutenzione.get(); }
    public ObjectProperty<LocalDate> inizioManutenzioneProperty() { return inizioManutenzione; }

    public LocalDate getFineManutenzione() { return fineManutenzione.get(); }
    public ObjectProperty<LocalDate> fineManutenzioneProperty() { return fineManutenzione; }

    public int getIntervallo() { return intervalloDiGiorni; }
    public void setIntervallo(int i) { this.intervalloDiGiorni = i; }

    // Boolean flags
    public boolean isInArrivo() { return inArrivo; }
    public void setInArrivo(boolean b) { this.inArrivo = b; }

    public boolean isInPartenza() { return inPartenza; }
    public void setInPartenza(boolean b) { this.inPartenza = b; }

    public boolean isInManutenzione(boolean b) { return inManutenzione; }
    public void setInManutenzione(boolean b) { this.inManutenzione = b; }

    public boolean isInCorsa() { return inCorsa; }
    public void setInCorsa(boolean b) { this.inCorsa = b; }

    public boolean isInAttesa() { return inAttesa; }
    public void setInAttesa(boolean b) { this.inAttesa = b; }
}
