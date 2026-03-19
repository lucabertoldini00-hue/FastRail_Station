package FastRailStation.view.controller;

import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import FastRailStation.model.GestioneUtenti;
import FastRailStation.model.Utente;

public class LoginController {

    @FXML private TextField     inserisciMail;
    @FXML private PasswordField inserisciPassword;
    @FXML private TextField     vediInserisciPassword;
    @FXML private Button        btnHome;
    @FXML private CheckBox      visualizzaPassword;
    @FXML private Hyperlink     vaiRegistrati;
    @FXML private Hyperlink     vaiResetPassword;
    @FXML private Label         segnalaErrore;

    private final GestioneUtenti gestioneUtenti = GestioneUtenti.getInstance();

    @FXML
    public void initialize() {
        vediInserisciPassword.setVisible(false);
        vediInserisciPassword.setEditable(false);
        inserisciPassword.setVisible(true);
        inserisciPassword.setEditable(true);

        gestioneUtenti.aggiornaLista();

        inserisciMail.setOnKeyPressed(this::handleKeyPress);
        inserisciPassword.setOnKeyPressed(this::handleKeyPress);

        // Clear error styling as soon as user starts correcting
        inserisciMail.textProperty().addListener((o, a, b) -> clearError());
        inserisciPassword.textProperty().addListener((o, a, b) -> clearError());
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) login();
    }

    @FXML
    public void visualizza() {
        if (visualizzaPassword.isSelected()) {
            inserisciPassword.setVisible(false);
            vediInserisciPassword.setVisible(true);
            vediInserisciPassword.setText(inserisciPassword.getText());
        } else {
            inserisciPassword.setVisible(true);
            vediInserisciPassword.setVisible(false);
        }
    }

    @FXML
    public void login() {
        String mail     = inserisciMail.getText().trim();
        String password = inserisciPassword.getText();

        // Per-field validation with highlighting
        if (mail.isEmpty() && password.isEmpty()) {
            highlight(inserisciMail); highlight(inserisciPassword);
            showError("Inserisci email e password."); return;
        }
        if (mail.isEmpty()) {
            highlight(inserisciMail);
            showError("Inserisci la tua email."); return;
        }
        if (password.isEmpty()) {
            highlight(inserisciPassword);
            showError("Inserisci la tua password."); return;
        }

        // FIX N2: admin branch returns immediately
        if (mail.equals("admin") && password.equals("admin")) {
            handleAdminPage();
            return;
        }

        ArrayList<Utente> listaUtenti = gestioneUtenti.getUtenti();
        int foundIndex = -1;
        for (int i = 0; i < listaUtenti.size(); i++) {
            if (listaUtenti.get(i).getMail().equals(mail) &&
                    listaUtenti.get(i).getPassword().equals(password)) {
                foundIndex = i;
                break;
            }
        }

        if (foundIndex < 0) {
            highlight(inserisciMail); highlight(inserisciPassword);
            showError("Email o password errati.");
            shakeLabel(segnalaErrore);
            return;
        }

        clearError();
        gestioneUtenti.setLogin(foundIndex);

        // FIX N1: schermataPrecedente is initialised to "Home" — never null
        String dest = gestioneUtenti.getSchermataPrecedente();
        if (dest == null) dest = "Home";

        switch (dest) {
            case "PrenotaPage":    prenotaPage();      break;
            case "UserMainPageA":  userMainPage(0);    break;
            case "UserMainPageP":  userMainPage(1);    break;
            default:               handleBtnHome();    break;
        }
    }

    // ── Navigation helpers ────────────────────────────────────────────────────

    // FIX N3: all paths use GUI/ (actual folder) not guiFolder/ (non-existent)

    @FXML
    private void handleBtnHome() {
        navigate("../GUI/user.fxml", "FastRail Station", btnHome);
    }

    private void prenotaPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../GUI/prenotazione.fxml"));
            Parent root = loader.load();
            PrenotazioneController ctrl = loader.getController();
            ctrl.setLogged(true);
            Stage stage = new Stage();
            stage.setTitle("Prenotazione");
            stage.setScene(new Scene(root));
            stage.show();
            ((Stage) btnHome.getScene().getWindow()).close();
        } catch (Exception e) { e.printStackTrace(); }
    }

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
            ((Stage) btnHome.getScene().getWindow()).close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleAdminPage() {
        navigate("../GUI/main.fxml", "Admin", vaiRegistrati);
    }

    @FXML
    private void handleRegistratiLink() {
        // Keep schermataPrecedente as-is so sign-in can return to the right screen
        navigate("../GUI/signin.fxml", "Registrati", vaiRegistrati);
    }

    @FXML
    private void handlePasswChangeLink() {
        navigate("../GUI/pwChange.fxml", "Cambia Password", vaiResetPassword);
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private void navigate(String fxmlPath, String title, javafx.scene.Node sourceNode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            ((Stage) sourceNode.getScene().getWindow()).close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showError(String msg) {
        if (segnalaErrore == null) return;
        segnalaErrore.setText(msg);
        segnalaErrore.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px;");
    }

    private void clearError() {
        if (segnalaErrore != null) segnalaErrore.setText("");
        inserisciMail.setStyle("");
        inserisciPassword.setStyle("");
    }

    private void highlight(Control ctrl) {
        ctrl.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2; -fx-border-radius: 6;");
    }

    private void shakeLabel(Label lbl) {
        if (lbl == null) return;
        double ox = lbl.getTranslateX();
        Timeline shake = new Timeline(
                new KeyFrame(Duration.millis(0),   new KeyValue(lbl.translateXProperty(), ox)),
                new KeyFrame(Duration.millis(60),  new KeyValue(lbl.translateXProperty(), ox - 8)),
                new KeyFrame(Duration.millis(120), new KeyValue(lbl.translateXProperty(), ox + 8)),
                new KeyFrame(Duration.millis(180), new KeyValue(lbl.translateXProperty(), ox - 6)),
                new KeyFrame(Duration.millis(240), new KeyValue(lbl.translateXProperty(), ox + 6)),
                new KeyFrame(Duration.millis(300), new KeyValue(lbl.translateXProperty(), ox))
        );
        shake.play();
    }
}
