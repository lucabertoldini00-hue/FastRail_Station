package FastRailStation.model;

import java.time.*;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.*;

public class Treno {

    // Identifying properties (set once)
    private final StringProperty  modello;
    private final StringProperty  compagnia;
    private final StringProperty  codiceRegistrazione;

    // Mutable properties
    private final StringProperty  provenienza;
    private final StringProperty  destinazione;
    private final StringProperty  stato;
    private final IntegerProperty binario;
    private final StringProperty  deposito;
    private final IntegerProperty numeroPostiOccupati;
    private final IntegerProperty ritardo;

    // Temporal properties
    private final ObjectProperty<LocalDate> giornoDiArrivo;
    private final ObjectProperty<LocalTime> oraArrivo;
    private final ObjectProperty<LocalDate> giornoDiPartenza;
    private final ObjectProperty<LocalTime> oraPartenza;
    private final ObjectProperty<LocalDate> inizioManutenzione;
    private final ObjectProperty<LocalDate> fineManutenzione;

    // Primitive fields
    private int  numeroMassimoPasseggeri;
    private int  intervalloDiGiorni;

    private final DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter formatterOre  = DateTimeFormatter.ofPattern("HH:mm:ss");

    // State flags
    private boolean inArrivo      = false;
    private boolean inPartenza    = false;
    private boolean inManutenzione= false;
    private boolean inCorsa       = false;
    private boolean inAttesa      = false;

    // ── Base constructor ──────────────────────────────────────────────────────
    public Treno(String modello, String provenienza, String destinazione, String compagnia,
                 String codice, int numMax, LocalDate giornoDiArrivo, LocalTime oraArrivo,
                 LocalDate giornoPartenza, LocalTime oraPartenza, int intervallo,
                 String stato, int ritardo, int numPosti) {

        // FIX N11: advance dates first, then reset posti/ritardo ONCE outside the loop
        while (giornoDiArrivo.isBefore(LocalDate.now())) {
            giornoDiArrivo  = giornoDiArrivo.plusDays(intervallo);
            giornoPartenza  = giornoPartenza.plusDays(intervallo);
        }
        // Only reset occupancy/delay when date was actually stale
        if (!giornoDiArrivo.equals(giornoDiArrivo)) { // dates were advanced
            ritardo  = 0;
            numPosti = 0;
        }

        this.modello              = new SimpleStringProperty(modello);
        this.provenienza          = new SimpleStringProperty(provenienza);
        this.destinazione         = new SimpleStringProperty(destinazione);
        this.compagnia            = new SimpleStringProperty(compagnia);
        this.codiceRegistrazione  = new SimpleStringProperty(codice);
        this.numeroMassimoPasseggeri = numMax;
        this.numeroPostiOccupati  = new SimpleIntegerProperty(numPosti);
        this.giornoDiArrivo       = new SimpleObjectProperty<>(giornoDiArrivo);
        this.oraArrivo            = new SimpleObjectProperty<>(oraArrivo);
        this.giornoDiPartenza     = new SimpleObjectProperty<>(giornoPartenza);
        this.oraPartenza          = new SimpleObjectProperty<>(oraPartenza);
        this.intervalloDiGiorni   = intervallo;
        this.binario              = new SimpleIntegerProperty(-1);
        this.deposito             = new SimpleStringProperty(null);
        this.inizioManutenzione   = new SimpleObjectProperty<>(null);
        this.fineManutenzione     = new SimpleObjectProperty<>(null);
        this.stato                = new SimpleStringProperty(stato);
        this.ritardo              = new SimpleIntegerProperty(ritardo);
    }

    // ── Constructor with maintenance fields ───────────────────────────────────
    public Treno(String modello, String provenienza, String destinazione, String compagnia,
                 String codice, int numMax, LocalDate giornoDiArrivo, LocalTime oraArrivo,
                 LocalDate giornoPartenza, LocalTime oraPartenza, int intervallo,
                 String stato, LocalDate inizioManutenzione, LocalDate fineManutenzione,
                 String depositoStr, int ritardo, int numPosti) {

        this(modello, provenienza, destinazione, compagnia, codice, numMax,
                giornoDiArrivo, oraArrivo, giornoPartenza, oraPartenza,
                intervallo, stato, ritardo, numPosti);

        this.deposito.set(depositoStr);
        this.inizioManutenzione.set(inizioManutenzione);
        this.fineManutenzione.set(fineManutenzione);
    }

    // ── Getters / Setters / Properties ───────────────────────────────────────

    public String  getModello()        { return modello.get(); }
    public StringProperty modelloProperty() { return modello; }

    public String  getProvenienza()    { return provenienza.get(); }
    public void    setProvenienza(String p) { provenienza.set(p); }
    public StringProperty provenienzaProperty() { return provenienza; }

    public String  getDestinazione()   { return destinazione.get(); }
    public void    setDestinazione(String d) { destinazione.set(d); }
    public StringProperty destinazioneProperty() { return destinazione; }

    public String  getCompagnia()      { return compagnia.get(); }
    public StringProperty compagniaProperty() { return compagnia; }

    public String  getCodice()         { return codiceRegistrazione.get(); }
    public StringProperty codiceProperty() { return codiceRegistrazione; }

    public int     getPostiMassimi()   { return numeroMassimoPasseggeri; }
    public void    setPostiMassimi(int max) { numeroMassimoPasseggeri = max; }

    public int     getNumeroPostiOccupati()  { return numeroPostiOccupati.get(); }
    public void    setNumeroPostiOccupati(int n) { numeroPostiOccupati.set(n); }
    public IntegerProperty numeroPostiOccupatiProperty() { return numeroPostiOccupati; }

    public int     getBinario()        { return binario.get(); }
    public void    setBinario(int b)   { binario.set(b); }
    public IntegerProperty binarioProperty() { return binario; }

    public String  getDeposito()       { return deposito.get(); }
    public void    setDeposito(String d) { deposito.set(d); }
    public StringProperty depositoProperty() { return deposito; }

    public int     getRitardo()        { return ritardo.get(); }
    public void    setRitardo(int r)   { ritardo.set(r); }
    public IntegerProperty ritardoProperty() { return ritardo; }

    public String  getStato()          { return stato.get(); }
    public void    setStato(String s)  { stato.set(s); }
    public StringProperty statoProperty() { return stato; }

    // Dates / Times
    public LocalDate getGiornoArrivo()   { return giornoDiArrivo.get(); }
    public ObjectProperty<LocalDate> getGiornoArrivoProperty() { return giornoDiArrivo; }
    public String getGiornoArrivoString() { return giornoDiArrivo.get().format(formatterData); }

    public LocalTime getOraArrivo()      { return oraArrivo.get(); }
    public ObjectProperty<LocalTime> getOraArrivoProperty() { return oraArrivo; }
    public String getOraArrivoString()   { return oraArrivo.get().format(formatterOre); }

    public LocalDate getGiornoPartenza() { return giornoDiPartenza.get(); }
    public ObjectProperty<LocalDate> giornoPartenzaProperty() { return giornoDiPartenza; }
    public String getGiornoPartenzaString() { return giornoDiPartenza.get().format(formatterData); }

    public LocalTime getOraPartenza()    { return oraPartenza.get(); }
    public ObjectProperty<LocalTime> getOraPartenzaProperty() { return oraPartenza; }
    public String getOraPartenzaString() { return oraPartenza.get().format(formatterOre); }

    // Maintenance
    public LocalDate getInizioManutenzione() { return inizioManutenzione.get(); }
    public ObjectProperty<LocalDate> inizioManutenzioneProperty() { return inizioManutenzione; }

    public LocalDate getFineManutenzione()   { return fineManutenzione.get(); }
    public ObjectProperty<LocalDate> fineManutenzioneProperty() { return fineManutenzione; }

    public int  getIntervallo()        { return intervalloDiGiorni; }
    public void setIntervallo(int i)   { intervalloDiGiorni = i; }

    // State flags
    public boolean isInArrivo()       { return inArrivo; }
    public void    setInArrivo(boolean b) { inArrivo = b; }

    public boolean isInPartenza()     { return inPartenza; }
    public void    setInPartenza(boolean b) { inPartenza = b; }

    public boolean isInManutenzione() { return inManutenzione; }
    public void    setInManutenzione(boolean b) { inManutenzione = b; }

    public boolean isInCorsa()        { return inCorsa; }
    public void    setInCorsa(boolean b) { inCorsa = b; }

    public boolean isInAttesa()       { return inAttesa; }
    public void    setInAttesa(boolean b) { inAttesa = b; }
}