package FastRailStation.view.controller;

import FastRailStation.model.GestioneUtenti;
import FastRailStation.model.Utente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ProfiloController {

    // ── Nav ──────────────────────────────────────────────────────────────────
    @FXML private Label navHome;
    @FXML private Label navArrivi;
    @FXML private Label navPartenze;
    @FXML private Label navPrenota;
    @FXML private Label navProfilo;

    // ── Dati personali ────────────────────────────────────────────────────────
    @FXML private TextField tfNome;
    @FXML private TextField tfCognome;
    @FXML private TextField tfMail;
    @FXML private TextField tfNascita;
    @FXML private TextField tfCellulare;
    @FXML private TextField tfNazione;
    @FXML private TextField tfCitta;
    @FXML private TextField tfIndirizzo;
    @FXML private TextField tfCarta;
    @FXML private TextField tfScadenza;

    // ── Password ──────────────────────────────────────────────────────────────
    @FXML private PasswordField pfNuovaPassword;
    @FXML private PasswordField pfConfermaPassword;
    @FXML private Label         lblFeedback;

    // ── Bottoni ───────────────────────────────────────────────────────────────
    @FXML private Button btnModifica;
    @FXML private Button btnSalva;

    private final GestioneUtenti gu = GestioneUtenti.getInstance();

    @FXML
    private void initialize() {
        caricaDatiUtente();
        setupNav();
    }

    // ── Carica ───────────────────────────────────────────────────────────────

    private void caricaDatiUtente() {
        int idx = gu.getIndice();
        if (idx < 0 || idx >= gu.getUtenti().size()) return;
        Utente u = gu.getUtenti().get(idx);

        tfNome.setText(u.getNome());
        tfCognome.setText(u.getCognome());
        tfMail.setText(u.getMail());
        tfNascita.setText(u.getNascita());
        tfCellulare.setText(u.getNumeroCellulare());
        tfNazione.setText(u.getNazioneResideza());
        tfCitta.setText(u.getCittaResidenza());
        tfIndirizzo.setText(u.getViaResidenza());
        tfCarta.setText(maskCard(u.getCodiceCarta()));
        tfScadenza.setText(u.getScadenza());

        navProfilo.setText("👤 " + u.getNome());
    }

    /** Mostra solo le ultime 4 cifre della carta */
    private String maskCard(String carta) {
        if (carta == null || carta.length() < 4) return carta;
        return "**** **** **** " + carta.substring(carta.length() - 4);
    }

    // ── Modifica / Salva ──────────────────────────────────────────────────────

    @FXML
    private void handleModifica() {
        setEditable(true);
        // Mostra numero carta reale per modifica
        int idx = gu.getIndice();
        if (idx >= 0 && idx < gu.getUtenti().size())
            tfCarta.setText(gu.getUtenti().get(idx).getCodiceCarta());
    }

    @FXML
    private void handleSalva() {
        int idx = gu.getIndice();
        if (idx < 0 || idx >= gu.getUtenti().size()) return;
        Utente u = gu.getUtenti().get(idx);

        // Validazione base
        if (tfNome.getText().trim().isEmpty() || tfCognome.getText().trim().isEmpty()
                || tfMail.getText().trim().isEmpty()) {
            feedback("Nome, cognome e mail sono obbligatori.", false);
            return;
        }

        // Controlla mail duplicata (diversa dall'utente corrente)
        String nuovaMail = tfMail.getText().trim();
        for (int i = 0; i < gu.getUtenti().size(); i++) {
            if (i != idx && gu.getUtenti().get(i).getMail().equalsIgnoreCase(nuovaMail)) {
                feedback("Email già in uso da un altro account.", false);
                return;
            }
        }

        u.setMail(nuovaMail);
        u.nome            = tfNome.getText().trim();
        u.cognome         = tfCognome.getText().trim();
        u.nascita         = tfNascita.getText().trim();
        u.numeroCellulare = tfCellulare.getText().trim();
        u.nazioneResideza = tfNazione.getText().trim();
        u.cittaResidenza  = tfCitta.getText().trim();
        u.viaResidenza    = tfIndirizzo.getText().trim();
        u.codiceCarta     = tfCarta.getText().trim();
        u.scadenza        = tfScadenza.getText().trim();

        gu.scriviUtenti();
        setEditable(false);
        tfCarta.setText(maskCard(u.getCodiceCarta()));
        feedback("Profilo aggiornato con successo!", true);
    }

    // ── Cambio password ───────────────────────────────────────────────────────

    @FXML
    private void handleCambioPassword() {
        String nuova   = pfNuovaPassword.getText();
        String conferma = pfConfermaPassword.getText();

        if (nuova.isEmpty()) { feedback("Inserisci la nuova password.", false); return; }
        if (!nuova.equals(conferma)) { feedback("Le password non corrispondono.", false); return; }
        if (nuova.length() < 6) { feedback("La password deve avere almeno 6 caratteri.", false); return; }

        int idx = gu.getIndice();
        if (idx < 0 || idx >= gu.getUtenti().size()) return;
        gu.getUtenti().get(idx).setPassword(nuova);
        gu.scriviUtenti();
        pfNuovaPassword.clear();
        pfConfermaPassword.clear();
        feedback("Password aggiornata!", true);
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    @FXML
    private void handleLogout() {
        gu.logout();
        navigateTo("../GUI/user.fxml", "FastRail Station");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setEditable(boolean on) {
        for (TextField tf : new TextField[]{tfNome, tfCognome, tfMail, tfNascita,
                tfCellulare, tfNazione, tfCitta, tfIndirizzo, tfCarta, tfScadenza})
            tf.setEditable(on);
        btnSalva.setDisable(!on);
        btnModifica.setDisable(on);
    }

    private void feedback(String msg, boolean ok) {
        lblFeedback.setText(msg);
        lblFeedback.setStyle(ok
                ? "-fx-text-fill: #4cff72; -fx-font-size: 13px;"
                : "-fx-text-fill: #ff6b6b; -fx-font-size: 13px;");
    }

    // ── Nav ───────────────────────────────────────────────────────────────────

    private void setupNav() {
        wire(navHome,     () -> navigateTo("../GUI/user.fxml",         "FastRail Station"));
        wire(navArrivi,   () -> navigateTo("../GUI/userMain.fxml",     "Arrivi"));
        wire(navPartenze, () -> navigateTo("../GUI/userMain.fxml",     "Partenze"));
        wire(navPrenota,  () -> navigateTo("../GUI/prenotazione.fxml", "Prenotazione"));
        wire(navProfilo,  () -> {});
    }

    private void wire(Label l, Runnable r) { if (l != null) l.setOnMouseClicked(e -> r.run()); }

    private void navigateTo(String path, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            Stage cur = navHome != null && navHome.getScene() != null
                    ? (Stage) navHome.getScene().getWindow() : null;
            if (cur == null && navProfilo != null && navProfilo.getScene() != null)
                cur = (Stage) navProfilo.getScene().getWindow();
            if (cur != null) cur.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
