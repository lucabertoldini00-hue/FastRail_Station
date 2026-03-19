package FastRailStation.view.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
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
    @FXML private CheckBox      acconsenteNormative;   // was RadioButton — now CheckBox
    @FXML private Hyperlink     vaiAccedi;
    @FXML private DatePicker    selezionaData;
    @FXML private Button        registrati;
    @FXML private Label         erroreLabel;           // NEW: error feedback label

    private final GestioneUtenti gestioneUtenti = GestioneUtenti.getInstance();

    @FXML
    public void initialize() {
        // Clear any previous error on every keystroke so UI feels responsive
        inserimentoNome.textProperty().addListener((o, a, b) -> clearError());
        inserimentoMail.textProperty().addListener((o, a, b) -> clearError());
        inserimentoPassword.textProperty().addListener((o, a, b) -> clearError());
        ripetizionePassword.textProperty().addListener((o, a, b) -> clearError());
    }

    @FXML
    public void creaUtente() {
        clearAllBorders();

        // 1. Privacy consent
        if (!acconsenteNormative.isSelected()) {
            showError("Devi accettare le normative Privacy e Sicurezza per procedere.");
            acconsenteNormative.setStyle("-fx-border-color: #ff6b6b; -fx-border-radius: 3;");
            return;
        }

        // 2. Check all fields filled — highlights each empty one
        if (!validaCampi()) return;

        // 3. Password match
        if (!inserimentoPassword.getText().equals(ripetizionePassword.getText())) {
            showError("Le password non corrispondono.");
            highlight(inserimentoPassword);
            highlight(ripetizionePassword);
            return;
        }

        // 4. Email not already registered
        String mail = inserimentoMail.getText().trim();
        boolean mailEsistente = gestioneUtenti.getUtenti().stream()
                .anyMatch(u -> u.getMail().equalsIgnoreCase(mail));
        if (mailEsistente) {
            showError("Questo indirizzo email è già registrato. Prova ad accedere.");
            highlight(inserimentoMail);
            return;
        }

        // 5. All good — create user
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
                inserimentoCarta.getText().trim(),
                inserimentoScadenza.getText().trim());

        // Navigate back
        String dest = gestioneUtenti.getSchermataPrecedente();
        if (dest == null) dest = "Home";
        switch (dest) {
            case "PrenotaPage":   prenotaPage();   break;
            case "UserMainPageA": userMainPage(0); break;
            case "UserMainPageP": userMainPage(1); break;
            default:              homePage();      break;
        }
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private boolean validaCampi() {
        boolean ok = true;

        // Each empty field gets a red border and a specific message
        if (inserimentoNome.getText().trim().isEmpty()) {
            highlight(inserimentoNome);
            if (ok) showError("Il campo Nome è obbligatorio.");
            ok = false;
        }
        if (inserimentoCognome.getText().trim().isEmpty()) {
            highlight(inserimentoCognome);
            if (ok) showError("Il campo Cognome è obbligatorio.");
            ok = false;
        }
        if (inserimentoMail.getText().trim().isEmpty()) {
            highlight(inserimentoMail);
            if (ok) showError("Il campo E-mail è obbligatorio.");
            ok = false;
        }
        if (selezionaData.getValue() == null) {
            selezionaData.setStyle("-fx-border-color: #ff6b6b; -fx-border-radius: 6;");
            if (ok) showError("Inserisci la tua data di nascita.");
            ok = false;
        }
        if (inserimentoPassword.getText().isEmpty()) {
            highlight(inserimentoPassword);
            if (ok) showError("Il campo Password è obbligatorio.");
            ok = false;
        }
        if (ripetizionePassword.getText().isEmpty()) {
            highlight(ripetizionePassword);
            if (ok) showError("Ripeti la password.");
            ok = false;
        }
        if (inserimentoTelefono.getText().trim().isEmpty()) {
            highlight(inserimentoTelefono);
            if (ok) showError("Il campo Cellulare è obbligatorio.");
            ok = false;
        }
        if (inserimentoNazione.getText().trim().isEmpty()) {
            highlight(inserimentoNazione);
            if (ok) showError("Il campo Nazione è obbligatorio.");
            ok = false;
        }
        if (inserimentoCitta.getText().trim().isEmpty()) {
            highlight(inserimentoCitta);
            if (ok) showError("Il campo Città è obbligatorio.");
            ok = false;
        }
        if (inserimentoIndirizzo.getText().trim().isEmpty()) {
            highlight(inserimentoIndirizzo);
            if (ok) showError("Il campo Indirizzo è obbligatorio.");
            ok = false;
        }
        if (inserimentoCarta.getText().trim().isEmpty()) {
            highlight(inserimentoCarta);
            if (ok) showError("Il campo Carta di credito è obbligatorio.");
            ok = false;
        }
        if (inserimentoScadenza.getText().trim().isEmpty()) {
            highlight(inserimentoScadenza);
            if (ok) showError("Inserisci la data di scadenza della carta.");
            ok = false;
        }

        if (!ok) {
            // Append a generic suffix when multiple fields are missing
            if (erroreLabel != null && !erroreLabel.getText().isEmpty()
                    && !erroreLabel.getText().contains("obbligatorio"))
                showError("Compila tutti i campi evidenziati in rosso.");
        }

        return ok;
    }

    private void highlight(Control ctrl) {
        ctrl.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2; -fx-border-radius: 6;");
    }

    private void clearAllBorders() {
        for (Control c : new Control[]{inserimentoNome, inserimentoCognome, inserimentoMail,
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

    // ── Navigation ────────────────────────────────────────────────────────────

    private void homePage() { navigate("../GUI/user.fxml", "FastRail Station"); }
    private void prenotaPage() { navigate("../GUI/prenotazione.fxml", "Prenotazione"); }

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

    @FXML
    private void handleAccedi() { navigate("../GUI/login.fxml", "Login"); }

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
