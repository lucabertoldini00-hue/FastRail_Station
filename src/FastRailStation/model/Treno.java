package FastRailStation.model;

import java.time.*;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Treno
{
    private final StringProperty modello;
    private final StringProperty compagnia;
    private final StringProperty codiceRegistrazione;

    private StringProperty provenienza;
    private StringProperty destinazione;
    private StringProperty stato;

    private IntegerProperty binario;
    private StringProperty deposito;

    private int numeroMassimoPasseggeri;
    private IntegerProperty numeroPostiOccupati;

    private IntegerProperty ritardo;

    private ObjectProperty<LocalDate> giornoDiArrivo;
    private ObjectProperty<LocalTime> oraArrivo;
    private ObjectProperty<LocalDate> giornoDiPartenza;
    private ObjectProperty<LocalTime> oraPartenza;
    private int intervalloDiGiorni;

    private ObjectProperty<LocalDate> inizioManutenzione;
    private ObjectProperty<LocalDate> fineManutenzione;

    private final DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter formatterOre = DateTimeFormatter.ofPattern("HH:mm:ss");

    private boolean discesaPasseggeri = false;
    private boolean salitaPasseggeri = false;

    private boolean aggiornato = false;

    // ---------------- COSTRUTTORE BASE ----------------
    public Treno(String modello, String provenienza, String destinazione, String compagnia, String codice,
                 int numMax, LocalDate giornoDiArrivo, LocalTime oraArrivo,
                 LocalDate giornoPartenza, LocalTime oraPartenza, int intervallo,
                 String stato, int ritardo, int numPosti)
    {
        while (giornoDiArrivo.isBefore(LocalDate.now()))
        {
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
                 String stazione, int ritardo, int numPosti)
    {
        while (giornoDiArrivo.isBefore(LocalDate.now()))
        {
            giornoDiArrivo = giornoDiArrivo.plusDays(intervallo);
            giornoPartenza = giornoPartenza.plusDays(intervallo);
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
        this.deposito = new SimpleStringProperty(stazione);

        this.inizioManutenzione = new SimpleObjectProperty<>(inizioManutenzione);
        this.fineManutenzione = new SimpleObjectProperty<>(fineManutenzione);

        this.stato = new SimpleStringProperty(stato);

        this.ritardo = new SimpleIntegerProperty(ritardo);
    }

    // ---------------- GETTER ----------------

    public String getModello()
    {
        return modello.get();
    }

    public StringProperty getModelloProperty()
    {
        return modello;
    }

    public String getProvenienza()
    {
        return provenienza.get();
    }

    public StringProperty getProvenienzaProperty()
    {
        return provenienza;
    }

    public String getDestinazione()
    {
        return destinazione.get();
    }

    public StringProperty getDestinazioneProperty()
    {
        return destinazione;
    }

    public String getCompagnia()
    {
        return compagnia.get();
    }

    public StringProperty getCompagniaProperty()
    {
        return compagnia;
    }

    public String getCodice()
    {
        return codiceRegistrazione.get();
    }

    public StringProperty getCodiceProperty()
    {
        return codiceRegistrazione;
    }

    public int getPostiMassimi()
    {
        return numeroMassimoPasseggeri;
    }

    public int getNumeroPostiOccupati()
    {
        return numeroPostiOccupati.get();
    }

    public IntegerProperty getNumeroPostiOccupatiProperty()
    {
        return numeroPostiOccupati;
    }

    public int getBinario()
    {
        return binario.get();
    }

    public IntegerProperty getBinarioProperty()
    {
        return binario;
    }

    public String getDeposito()
    {
        return deposito.get();
    }

    public StringProperty getStazioneProperty()
    {
        return deposito;
    }

    public int getRitardo()
    {
        return ritardo.get();
    }

    public IntegerProperty getRitardoProperty()
    {
        return ritardo;
    }

    public LocalDate getGiornoArrivo()
    {
        return giornoDiArrivo.get();
    }

    public String getGiornoArrivoString()
    {
        return giornoDiArrivo.get().format(formatterData);
    }

    public LocalTime getOraArrivo()
    {
        return oraArrivo.get();
    }

    public String getOraArrivoString()
    {
        return oraArrivo.get().format(formatterOre);
    }

    public LocalDate getGiornoPartenza()
    {
        return giornoDiPartenza.get();
    }

    public String getGiornoPartenzaString()
    {
        return giornoDiPartenza.get().format(formatterData);
    }

    public LocalTime getOraPartenza()
    {
        return oraPartenza.get();
    }

    public String getOraPartenzaString()
    {
        return oraPartenza.get().format(formatterOre);
    }

    public int getIntervallo()
    {
        return intervalloDiGiorni;
    }

    public LocalDate getInizioManutenzione()
    {
        return inizioManutenzione.get();
    }

    public LocalDate getFineManutenzione()
    {
        return fineManutenzione.get();
    }

    public String getStato()
    {
        return stato.get();
    }

    public StringProperty getStatoProperty()
    {
        return stato;
    }

    public boolean isDiscesaPasseggeri()
    {
        return discesaPasseggeri;
    }

    public boolean isSalitaPasseggeri()
    {
        return salitaPasseggeri;
    }

    public boolean isAggiornato()
    {
        return aggiornato;
    }

    // ---------------- SETTER ----------------

    public void setProvenienza(String provenienza)
    {
        this.provenienza.set(provenienza);
    }

    public void setDestinazione(String destinazione)
    {
        this.destinazione.set(destinazione);
    }

    public void setNumeroPostiOccupati(int numeroPostiOccupati)
    {
        this.numeroPostiOccupati.set(numeroPostiOccupati);
    }

    public void setPostiMassimi(int postiMassimi)
    {
        this.numeroMassimoPasseggeri = postiMassimi;
    }

    public void setBinario(int binario)
    {
        this.binario.set(binario);
    }

    public void setDeposito(String deposito)
    {
        this.deposito.set(deposito);
    }

    public void setRitardo(int ritardo)
    {
        this.ritardo.set(ritardo);
    }

    public void setGiornoArrivo(LocalDate giornoArrivo)
    {
        this.giornoDiArrivo.set(giornoArrivo);
    }

    public void setOraArrivo(LocalTime oraArrivo)
    {
        this.oraArrivo.set(oraArrivo);
    }

    public void setGiornoPartenza(LocalDate giornoPartenza)
    {
        this.giornoDiPartenza.set(giornoPartenza);
    }

    public void setOraPartenza(LocalTime oraPartenza)
    {
        this.oraPartenza.set(oraPartenza);
    }

    public void setDiscesaPasseggeri(boolean discesaPasseggeri)
    {
        this.discesaPasseggeri = discesaPasseggeri;
    }

    public void setSalitaPasseggeri(boolean salitaPasseggeri)
    {
        this.salitaPasseggeri = salitaPasseggeri;
    }

    public void setIntervalloGiorni(int intervallo)
    {
        this.intervalloDiGiorni = intervallo;
    }

    public void setGiornoInizioManutenzione(LocalDate inizioM)
    {
        this.inizioManutenzione.set(inizioM);
    }

    public void setGiornoFineManutenzione(LocalDate fineM)
    {
        this.fineManutenzione.set(fineM);
    }

    public void setStato(String stato)
    {
        this.stato.set(stato);
    }

    public void setAggiornato(boolean aggiornato)
    {
        this.aggiornato = aggiornato;
    }

    // ---------------- LOGICA STATO TRENO ----------------

    public boolean isInArrivo()
    {
        LocalTime oraAttuale = LocalTime.now();
        LocalTime oraArrivo = getOraArrivo();

        if (oraAttuale.isAfter(oraArrivo.minusMinutes(5)) && oraAttuale.isBefore(oraArrivo.plusMinutes(5)))
        {
            aggiornato = !stato.get().equals("In arrivo");
            return true;
        }
        return false;
    }

    public boolean isInPartenza()
    {
        LocalTime oraAttuale = LocalTime.now();
        LocalTime oraPartenza = getOraPartenza();

        if (oraAttuale.isAfter(oraPartenza.minusMinutes(5)) && oraAttuale.isBefore(oraPartenza.plusMinutes(5)))
        {
            aggiornato = !stato.get().equals("In partenza");
            return true;
        }
        return false;
    }

    public boolean isInAttesa()
    {
        aggiornato = !stato.get().equals("In attesa");
        return !isInArrivo() && !isInPartenza();
    }

    public boolean isInManutenzione()
    {
        LocalDate oggi = LocalDate.now();
        LocalDate inizio = getInizioManutenzione();
        LocalDate fine = getFineManutenzione();

        if (inizio != null && fine != null && oggi.isAfter(inizio) && oggi.isBefore(fine))
        {
            aggiornato = !stato.get().equals("In manutenzione");
            return true;
        }
        return false;
    }
}
