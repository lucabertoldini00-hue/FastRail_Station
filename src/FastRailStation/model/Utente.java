package FastRailStation.model;

public class Utente {

    public String mail;
    public String password;
    public String nome;
    public String cognome;
    public String numeroCellulare;
    public String nazioneResideza;
    public String cittaResidenza;
    public String viaResidenza;
    public String codiceCarta;
    public String scadenza;
    public String nascita;
    public Ruolo  ruolo;          // NEW: ADMIN | USER

    // ── Costruttore completo (usato da SignInController) ──────────────────────
    public Utente(String nome, String cognome, String mail, String nascita, String password,
                  String numCell, String nazione, String citta, String via,
                  String codice, String scadenza) {
        this(nome, cognome, mail, nascita, password,
                numCell, nazione, citta, via, codice, scadenza, Ruolo.USER);
    }

    // ── Costruttore con ruolo (usato da LeggiDati) ────────────────────────────
    public Utente(String nome, String cognome, String mail, String nascita, String password,
                  String numCell, String nazione, String citta, String via,
                  String codice, String scadenza, Ruolo ruolo) {
        this.mail            = mail;
        this.password        = password;
        this.nome            = nome;
        this.cognome         = cognome;
        this.numeroCellulare = numCell;
        this.nazioneResideza = nazione;
        this.cittaResidenza  = citta;
        this.viaResidenza    = via;
        this.codiceCarta     = codice;
        this.scadenza        = scadenza;
        this.nascita         = nascita;
        this.ruolo           = ruolo;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getMail()             { return mail; }
    public String getPassword()         { return password; }
    public String getNome()             { return nome; }
    public String getCognome()          { return cognome; }
    public String getNascita()          { return nascita; }
    public String getNumeroCellulare()  { return numeroCellulare; }
    public String getNazioneResideza()  { return nazioneResideza; }
    public String getCittaResidenza()   { return cittaResidenza; }
    public String getViaResidenza()     { return viaResidenza; }
    public String getCodiceCarta()      { return codiceCarta; }
    public String getScadenza()         { return scadenza; }
    public Ruolo  getRuolo()            { return ruolo != null ? ruolo : Ruolo.USER; }

    public boolean isAdmin()            { return getRuolo() == Ruolo.ADMIN; }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setMail(String mail)         { this.mail     = mail; }
    public void setPassword(String password) { this.password = password; }
    public void setRuolo(Ruolo ruolo)        { this.ruolo    = ruolo; }
}