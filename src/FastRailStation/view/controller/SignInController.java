package FastRailStation.view.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import FastRailStation.model.GestioneUtenti;

public class SignInController {

    @FXML private TextField     inserimentoNome;
    @FXML private TextField     inserimentoCognome;
    @FXML private TextField     inserimentoMail;
    @FXML private PasswordField inserimentoPassword;
    @FXML private PasswordField ripetizionePassword;
    @FXML private TextField     inserimentoTelefono;
    @FXML private TextField     inserimentoNazione;
    @FXML private TextField     inserimentoCitta;
    @FXML private TextField     inserimentoIndirizzo;
    @FXML private TextField     inserimentoCarta;
    @FXML private TextField     inserimentoScadenza;
    @FXML private CheckBox      acconsenteNormative;
    @FXML private Hyperlink     vaiAccedi;
    @FXML private DatePicker    selezionaData;
    @FXML private Button        registrati;
    @FXML private Label         erroreLabel;

    private final GestioneUtenti gestioneUtenti = GestioneUtenti.getInstance();

    @FXML
    public void initialize() {
        inserimentoNome.textProperty().addListener((o, a, b)     -> clearError());
        inserimentoCognome.textProperty().addListener((o, a, b)  -> clearError());
        inserimentoMail.textProperty().addListener((o, a, b)     -> clearError());
        inserimentoPassword.textProperty().addListener((o, a, b) -> clearError());
        ripetizionePassword.textProperty().addListener((o, a, b) -> clearError());
        inserimentoCarta.textProperty().addListener((o, a, b)    -> clearError());
        inserimentoScadenza.textProperty().addListener((o, a, b) -> clearError());
    }

    // ── Registrazione ─────────────────────────────────────────────────────────

    @FXML
    public void creaUtente() {
        clearAllBorders();
        clearError();

        // 1. Privacy consent
        if (!acconsenteNormative.isSelected()) {
            showError("Devi accettare le normative Privacy e Sicurezza per procedere.");
            acconsenteNormative.setStyle("-fx-border-color: #ff6b6b; -fx-border-radius: 3;");
            return;
        }

        // 2. Validazione completa — si ferma al primo errore e evidenzia tutti i campi sbagliati
        if (!validaCampi()) return;

        // 3. Email non già registrata
        String mail = inserimentoMail.getText().trim();
        boolean mailEsistente = gestioneUtenti.getUtenti().stream()
                .anyMatch(u -> u.getMail().equalsIgnoreCase(mail));
        if (mailEsistente) {
            showError("Questo indirizzo email è già registrato.");
            highlight(inserimentoMail);
            return;
        }

        // 4. Tutto ok — crea utente
        gestioneUtenti.addUtenti(
                inserimentoNome.getText().trim(),
                inserimentoCognome.getText().trim(),
                mail,
                convertiData(),
                inserimentoPassword.getText(),
                inserimentoTelefono.getText().trim(),
                inserimentoNazione.getText().trim(),
                inserimentoCitta.getText().trim(),
                inserimentoIndirizzo.getText().trim(),
                normalizzaCarta(inserimentoCarta.getText()),
                inserimentoScadenza.getText().trim());

        String dest = gestioneUtenti.getSchermataPrecedente();
        if (dest == null) dest = "Home";
        switch (dest) {
            case "PrenotaPage":   prenotaPage();   break;
            case "UserMainPageA": userMainPage(0); break;
            case "UserMainPageP": userMainPage(1); break;
            default:              homePage();      break;
        }
    }

    // ── Validazione campi ─────────────────────────────────────────────────────

    /**
     * Esegue tutte le validazioni e raccoglie i messaggi di errore.
     * Evidenzia in rosso tutti i campi errati in un unico passaggio,
     * mostrando il messaggio relativo al primo errore trovato.
     * Restituisce true solo se ogni campo supera i controlli.
     */
    private boolean validaCampi() {
        boolean ok = true;
        String primoErrore = null;

        // ── Nome ──────────────────────────────────────────────────────────────
        String nome = inserimentoNome.getText().trim();
        if (nome.isEmpty()) {
            highlight(inserimentoNome);
            if (ok) primoErrore = "Il campo Nome è obbligatorio.";
            ok = false;
        } else if (!isNomeValido(nome)) {
            highlight(inserimentoNome);
            if (ok) primoErrore = "Il Nome non può contenere numeri o simboli speciali (min. 2 caratteri).";
            ok = false;
        }

        // ── Cognome ───────────────────────────────────────────────────────────
        String cognome = inserimentoCognome.getText().trim();
        if (cognome.isEmpty()) {
            highlight(inserimentoCognome);
            if (ok) primoErrore = "Il campo Cognome è obbligatorio.";
            ok = false;
        } else if (!isNomeValido(cognome)) {
            highlight(inserimentoCognome);
            if (ok) primoErrore = "Il Cognome non può contenere numeri o simboli speciali (min. 2 caratteri).";
            ok = false;
        }

        // ── Email ─────────────────────────────────────────────────────────────
        String mail = inserimentoMail.getText().trim();
        if (mail.isEmpty()) {
            highlight(inserimentoMail);
            if (ok) primoErrore = "Il campo E-mail è obbligatorio.";
            ok = false;
        } else if (!isEmailValida(mail)) {
            highlight(inserimentoMail);
            if (ok) primoErrore = "Inserisci un indirizzo email valido (es. nome@dominio.it).";
            ok = false;
        }

        // ── Data di nascita ───────────────────────────────────────────────────
        LocalDate nascita = selezionaData.getValue();
        if (nascita == null) {
            selezionaData.setStyle("-fx-border-color: #ff6b6b; -fx-border-radius: 6;");
            if (ok) primoErrore = "Inserisci la tua data di nascita.";
            ok = false;
        } else if (!isEtaValida(nascita)) {
            selezionaData.setStyle("-fx-border-color: #ff6b6b; -fx-border-radius: 6;");
            if (ok) primoErrore = "Devi avere almeno 16 anni per registrarti.";
            ok = false;
        } else if (nascita.isAfter(LocalDate.now())) {
            selezionaData.setStyle("-fx-border-color: #ff6b6b; -fx-border-radius: 6;");
            if (ok) primoErrore = "La data di nascita non può essere nel futuro.";
            ok = false;
        }

        // ── Password ──────────────────────────────────────────────────────────
        String pwd = inserimentoPassword.getText();
        if (pwd.isEmpty()) {
            highlight(inserimentoPassword);
            if (ok) primoErrore = "Il campo Password è obbligatorio.";
            ok = false;
        } else if (!isPasswordValida(pwd)) {
            highlight(inserimentoPassword);
            if (ok) primoErrore = "La password deve avere almeno 8 caratteri, una lettera e un numero.";
            ok = false;
        }

        // ── Ripeti password ───────────────────────────────────────────────────
        String pwd2 = ripetizionePassword.getText();
        if (pwd2.isEmpty()) {
            highlight(ripetizionePassword);
            if (ok) primoErrore = "Ripeti la password.";
            ok = false;
        } else if (!pwd.equals(pwd2)) {
            highlight(inserimentoPassword);
            highlight(ripetizionePassword);
            if (ok) primoErrore = "Le password non corrispondono.";
            ok = false;
        }

        // ── Cellulare ─────────────────────────────────────────────────────────
        String tel = inserimentoTelefono.getText().trim();
        if (tel.isEmpty()) {
            highlight(inserimentoTelefono);
            if (ok) primoErrore = "Il campo Cellulare è obbligatorio.";
            ok = false;
        } else if (!isCellulareValido(tel)) {
            highlight(inserimentoTelefono);
            if (ok) primoErrore = "Inserisci un numero di cellulare valido (7-15 cifre, può iniziare con +).";
            ok = false;
        }

        // ── Nazione ───────────────────────────────────────────────────────────
        String nazione = inserimentoNazione.getText().trim();
        if (nazione.isEmpty()) {
            highlight(inserimentoNazione);
            if (ok) primoErrore = "Il campo Nazione è obbligatorio.";
            ok = false;
        } else if (!isNazioneValida(nazione)) {
            highlight(inserimentoNazione);
            if (ok) primoErrore = "La Nazione non può contenere numeri o simboli speciali (min. 2 caratteri).";
            ok = false;
        }

        // ── Città ─────────────────────────────────────────────────────────────
        String citta = inserimentoCitta.getText().trim();
        if (citta.isEmpty()) {
            highlight(inserimentoCitta);
            if (ok) primoErrore = "Il campo Città è obbligatorio.";
            ok = false;
        } else if (!isCittaValida(citta)) {
            highlight(inserimentoCitta);
            if (ok) primoErrore = "La Città non è valida (min. 2 caratteri, no simboli speciali).";
            ok = false;
        }

        // ── Indirizzo ─────────────────────────────────────────────────────────
        String indirizzo = inserimentoIndirizzo.getText().trim();
        if (indirizzo.isEmpty()) {
            highlight(inserimentoIndirizzo);
            if (ok) primoErrore = "Il campo Indirizzo è obbligatorio.";
            ok = false;
        } else if (indirizzo.length() < 5) {
            highlight(inserimentoIndirizzo);
            if (ok) primoErrore = "L'indirizzo deve contenere almeno 5 caratteri.";
            ok = false;
        }

        // ── Carta di credito ──────────────────────────────────────────────────
        String carta = inserimentoCarta.getText().trim();
        if (carta.isEmpty()) {
            highlight(inserimentoCarta);
            if (ok) primoErrore = "Il campo Carta di credito è obbligatorio.";
            ok = false;
        } else if (!isCartaValida(carta)) {
            highlight(inserimentoCarta);
            if (ok) primoErrore = "Carta non valida: inserisci 16 cifre (es. 1234 5678 9012 3456).";
            ok = false;
        }

        // ── Scadenza carta ────────────────────────────────────────────────────
        String scadenza = inserimentoScadenza.getText().trim();
        if (scadenza.isEmpty()) {
            highlight(inserimentoScadenza);
            if (ok) primoErrore = "Inserisci la data di scadenza della carta.";
            ok = false;
        } else if (!isScadenzaFormato(scadenza)) {
            highlight(inserimentoScadenza);
            if (ok) primoErrore = "Formato scadenza non valido: usa MM/AA (es. 12/26).";
            ok = false;
        } else if (isCartaScaduta(scadenza)) {
            highlight(inserimentoScadenza);
            if (ok) primoErrore = "La carta di credito è scaduta.";
            ok = false;
        }

        // Mostra il primo messaggio di errore raccolto
        if (!ok) showError(primoErrore);
        return ok;
    }

    // ── Regole di validazione ─────────────────────────────────────────────────

    /**
     * Nome e Cognome: solo lettere (incluse accentate), spazi, apostrofi e trattini.
     * Min 2 caratteri, max 50.
     */
    private boolean isNomeValido(String s) {
        // \p{L} copre tutte le lettere Unicode (à, è, ì, ò, ù, é, ñ, ecc.)
        return s.matches("[\\p{L} '\\-]{2,50}");
    }

    /**
     * Email: formato standard user@dominio.ext
     */
    private boolean isEmailValida(String s) {
        return s.matches("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Password: almeno 8 caratteri, almeno una lettera e almeno un numero.
     */
    private boolean isPasswordValida(String s) {
        return s.length() >= 8
                && s.chars().anyMatch(Character::isLetter)
                && s.chars().anyMatch(Character::isDigit);
    }

    /**
     * Età minima 16 anni.
     */
    private boolean isEtaValida(LocalDate nascita) {
        return nascita.isBefore(LocalDate.now().minusYears(16));
    }

    /**
     * Cellulare: cifre con eventuale + iniziale, da 7 a 15 caratteri totali.
     * Accetta sia formato nazionale (3331234567) sia internazionale (+393331234567).
     */
    private boolean isCellulareValido(String s) {
        return s.matches("\\+?[0-9]{7,15}");
    }

    /**
     * Nazione: solo lettere e spazi, min 2 caratteri.
     */
    private boolean isNazioneValida(String s) {
        return s.matches("[\\p{L} ]{2,60}");
    }

    /**
     * Città: lettere, spazi, trattini e apostrofi (es. "Sant'Angelo", "Reggio-Emilia").
     * Min 2 caratteri.
     */
    private boolean isCittaValida(String s) {
        return s.matches("[\\p{L}0-9 '\\-]{2,100}");
    }

    /**
     * Carta di credito: esattamente 16 cifre (gli spazi vengono rimossi prima del controllo).
     * Verifica il checksum con l'algoritmo di Luhn.
     */
    private boolean isCartaValida(String s) {
        String digits = s.replaceAll("[\\s\\-]", "");
        return digits.matches("[0-9]{16}") && luhn(digits);
    }

    /** Rimuove spazi e trattini dalla carta prima di salvarla. */
    private String normalizzaCarta(String s) {
        return s.replaceAll("[\\s\\-]", "");
    }

    /**
     * Algoritmo di Luhn per la verifica del numero di carta.
     */
    private boolean luhn(String numero) {
        int somma = 0;
        boolean raddoppia = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int cifra = numero.charAt(i) - '0';
            if (raddoppia) {
                cifra *= 2;
                if (cifra > 9) cifra -= 9;
            }
            somma += cifra;
            raddoppia = !raddoppia;
        }
        return somma % 10 == 0;
    }

    /**
     * Formato scadenza: MM/AA dove MM è 01-12 e AA sono 2 cifre.
     */
    private boolean isScadenzaFormato(String s) {
        return s.matches("(0[1-9]|1[0-2])/[0-9]{2}");
    }

    /**
     * Verifica che la carta non sia scaduta rispetto alla data corrente.
     * Una carta scade alla fine del mese indicato.
     */
    private boolean isCartaScaduta(String s) {
        try {
            int mese = Integer.parseInt(s.substring(0, 2));
            int anno = 2000 + Integer.parseInt(s.substring(3, 5));
            LocalDate oggi = LocalDate.now();
            // La carta è ancora valida durante tutto il mese di scadenza
            LocalDate scadenza = LocalDate.of(anno, mese, 1).plusMonths(1).minusDays(1);
            return oggi.isAfter(scadenza);
        } catch (Exception e) {
            return true; // formato non parsabile = considera scaduta
        }
    }

    // ── Helpers UI ────────────────────────────────────────────────────────────

    private void highlight(Control ctrl) {
        ctrl.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2; -fx-border-radius: 6;");
    }

    private void clearAllBorders() {
        for (Control c : new Control[]{
                inserimentoNome, inserimentoCognome, inserimentoMail,
                inserimentoPassword, ripetizionePassword, inserimentoTelefono,
                inserimentoNazione, inserimentoCitta, inserimentoIndirizzo,
                inserimentoCarta, inserimentoScadenza}) {
            c.setStyle("");
        }
        selezionaData.setStyle("");
        acconsenteNormative.setStyle("");
    }

    private void showError(String msg) {
        if (erroreLabel == null) return;
        erroreLabel.setText(msg);
        erroreLabel.setStyle("-fx-text-fill: #ff6b6b;");
    }

    private void clearError() {
        if (erroreLabel != null) erroreLabel.setText("");
    }

    private String convertiData() {
        return selezionaData.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    // ── Navigazione ───────────────────────────────────────────────────────────

    private void homePage()     { navigate("../GUI/user.fxml",         "FastRail Station"); }
    private void prenotaPage()  { navigate("../GUI/prenotazione.fxml", "Prenotazione"); }

    private void userMainPage(int tab) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../GUI/userMain.fxml"));
            Parent root = loader.load();
            UserMainController ctrl = loader.getController();
            ctrl.setPartenzeSelected(tab == 1);
            Stage stage = new Stage();
            stage.setTitle("Tabellone");
            stage.setScene(new Scene(root));
            stage.show();
            closeStage();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void handleAccedi() { navigate("../GUI/login.fxml", "Login"); }

    private void navigate(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            closeStage();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void closeStage() {
        Stage s = null;
        if (registrati != null && registrati.getScene() != null)
            s = (Stage) registrati.getScene().getWindow();
        else if (vaiAccedi != null && vaiAccedi.getScene() != null)
            s = (Stage) vaiAccedi.getScene().getWindow();
        if (s != null) s.close();
    }
}
