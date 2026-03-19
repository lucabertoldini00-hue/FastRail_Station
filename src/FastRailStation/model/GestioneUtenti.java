package FastRailStation.model;

import java.util.ArrayList;
import java.util.List;

import FastRailStation.salvataggioDati.LeggiDati;
import FastRailStation.salvataggioDati.ScriviDati;

public class GestioneUtenti {

    private static GestioneUtenti instance;

    // FIX N12: keep one stable list reference; never swap it out
    public ArrayList<Utente> listaUtenti = new ArrayList<>();
    private ScriviDati scrivi;
    private LeggiDati  leggi;

    private String  schermataPrecedente = "Home"; // FIX N1: initialised to "Home", never null
    private boolean loggato = false;
    private int     indice  = -1;                 // FIX N4: -1 means "not logged in"

    private GestioneUtenti() {
        scrivi = new ScriviDati();
        leggi  = new LeggiDati();
    }

    public static GestioneUtenti getInstance() {
        if (instance == null)
            instance = new GestioneUtenti();
        return instance;
    }

    public void addUtenti(String nome, String cognome, String mail, String nascita,
                          String password, String numCell, String nazione, String citta,
                          String via, String codice, String scadenza) {
        Utente u = new Utente(nome, cognome, mail, nascita, password,
                numCell, nazione, citta, via, codice, scadenza);
        listaUtenti.add(u);
        scrivi.scriviUtenti(listaUtenti);
    }

    /**
     * FIX N12: re-populates the existing list in-place so callers that hold a
     * reference to getUtenti() see fresh data without needing to re-query.
     */
    public void aggiornaLista() {
        List<Utente> fresh = leggi.leggiUtente();
        listaUtenti.clear();
        if (fresh != null) listaUtenti.addAll(fresh);
    }

    public void scriviUtenti() {
        scrivi.scriviUtenti(listaUtenti);
    }

    public ArrayList<Utente> getUtenti() { return listaUtenti; }

    public void setLogin(int indice) {
        this.loggato = true;
        this.indice  = indice;
    }

    public void logout() {
        this.loggato = false;
        this.indice  = -1;
    }

    public boolean isLogged()  { return loggato; }
    public int     getIndice() { return indice; }

    public String getSchermataPrecedente()                    { return schermataPrecedente; }
    public void   setSchermataPrecedente(String schermata)   { this.schermataPrecedente = schermata; }
}