package FastRailStation.view.controller;

import java.util.ArrayList;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import FastRailStation.model.GestioneUtenti;
import FastRailStation.model.Utente;

public class PwChangeController {

    @FXML private TextField     inserisciMail;
    @FXML private PasswordField inserisciPassword;
    @FXML private PasswordField confermaNuovaPassword;
    @FXML private Button        btnHome;
    @FXML private Button        cambiPassword;
    @FXML private Label         feedbackLabel;   // NEW — was missing

    private final GestioneUtenti gestioneUtenti = GestioneUtenti.getInstance();

    @FXML
    public void initialize() {
        gestioneUtenti.aggiornaLista();
        // Clear feedback as soon as the user starts typing again
        inserisciMail.textProperty().addListener((o, a, b) -> clearFeedback());
        inserisciPassword.textProperty().addListener((o, a, b) -> clearFeedback());
        confermaNuovaPassword.textProperty().addListener((o, a, b) -> clearFeedback());
    }

    @FXML
    private void changePassword() {
        clearFeedback();
        clearBorders();

        // 1. All fields must be filled
        boolean ok = true;
        if (inserisciMail.getText().trim().isEmpty()) {
            insertiBorder(inserisciMail);
            showError("Inserisci la tua email.");
            ok = false;
        }
        if (inserisciPassword.getText().isEmpty()) {
            insertiBorder(inserisciPassword);
            if (ok) showError("Inserisci la nuova password.");
            ok = false;
        }
        if (confermaNuovaPassword.getText().isEmpty()) {
            insertiBorder(confermaNuovaPassword);
            if (ok) showError("Ripeti la nuova password.");
            ok = false;
        }
        if (!ok) return;

        // 2. Passwords must match
        if (!inserisciPassword.getText().equals(confermaNuovaPassword.getText())) {
            insertiBorder(inserisciPassword);
            insertiBorder(confermaNuovaPassword);
            showError("Le password non corrispondono.");
            return;
        }

        // 3. Find user by mail
        ArrayList<Utente> lista = gestioneUtenti.getUtenti();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getMail().equalsIgnoreCase(inserisciMail.getText().trim())) {
                lista.get(i).setPassword(inserisciPassword.getText());
                gestioneUtenti.scriviUtenti();
                gestioneUtenti.setLogin(i);
                showSuccess("Password aggiornata! Reindirizzo alla home…");
                // Navigate after a short pause so the user can read the message
                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(e -> handleBtnHome());
                pause.play();
                return;
            }
        }

        // 4. No user found
        insertiBorder(inserisciMail);
        showError("Nessun account trovato con questa email.");
    }

    @FXML
    private void handleBtnHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../GUI/user.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("FastRail Station");
            stage.setScene(new Scene(root));
            stage.show();
            if (btnHome != null && btnHome.getScene() != null)
                ((Stage) btnHome.getScene().getWindow()).close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void showError(String msg) {
        if (feedbackLabel == null) return;
        feedbackLabel.setText(msg);
        feedbackLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 13px;");
    }

    private void showSuccess(String msg) {
        if (feedbackLabel == null) return;
        feedbackLabel.setText(msg);
        feedbackLabel.setStyle("-fx-text-fill: #64ffda; -fx-font-size: 13px;");
    }

    private void clearFeedback() {
        if (feedbackLabel != null) feedbackLabel.setText("");
    }

    private void insertiBorder(Control c) {
        c.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2; -fx-border-radius: 6;");
    }

    private void clearBorders() {
        for (Control c : new Control[]{inserisciMail, inserisciPassword, confermaNuovaPassword})
            c.setStyle("");
    }
}
