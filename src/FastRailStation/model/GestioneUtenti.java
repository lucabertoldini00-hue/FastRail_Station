package FastRailStation.model;

import java.util.ArrayList;

import FastRailStation.SalvataggioDati.LeggiDati;
import FastRailStation.SalvataggioDati.ScriviDati;

public class GestioneUtenti {
    private static GestioneUtenti instance;
    public ArrayList<Utente> listaUtenti = new ArrayList<Utente>();
    ScriviDati scrivi;
    LeggiDati leggo;

    private String schermataPrecedente = null;

    private boolean loggato = false;
    private int indice;


    public GestioneUtenti (){
        scrivi = new ScriviDati();
        leggo = new LeggiDati();
    }

    public static GestioneUtenti getInstance(){
        if(instance == null){
            instance = new GestioneUtenti();
        }
        return instance;
    }

    public void addUtenti(String nome,String cognome, String mail, String nascita,  String password, String numCell,
                          String nazione, String citta, String via, String codice, String scadenza){

        Utente u = new Utente(nome, cognome, mail, nascita, password, numCell, nazione, citta, via, codice, scadenza);

        listaUtenti.add(u);

        scrivi.scriviUtenti(listaUtenti);
    }

    public void aggiornaLista() {
        listaUtenti = leggo.leggiUtente();
    }

    public void scriviUtenti() {
        scrivi.scriviUtenti(listaUtenti);
    }

    public ArrayList<Utente> getUtenti() {
        return listaUtenti;
    }

    public void setLogin(int indice) {
        loggato = true;
        this.indice = indice;
    }

    public boolean isLogged() {
        return loggato;
    }
    public int getIndice() {
        return indice;
    }

    // Getter e setter di schermataPrecedente
    public String getSchermataPrecedente() {
        return schermataPrecedente;
    }

    public void setSchermataPrecedente(String schermataPrecedente) {
        this.schermataPrecedente = schermataPrecedente;
    }
}