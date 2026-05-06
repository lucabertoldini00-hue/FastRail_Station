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
import FastRailStation.model.Ruolo;
import FastRailStation.model.Utente;

public class LoginController {

    @FXML private TextField     inserisciMail;
    @FXML private PasswordField inserisciPassword;
    @FXML private TextField     vediInserisciPassword;
    @FXML private Button        btnHome;
    @FXML private Label         togglePassword;
    @FXML private Hyperlink     vaiRegistrati;
    @FXML private Hyperlink     vaiResetPassword;
    @FXML private Label         segnalaErrore;


    private final GestioneUtenti gu = GestioneUtenti.getInstance();

    @FXML
    public void initialize() {

        // bind automatico tra i due campi
        vediInserisciPassword.textProperty().bindBidirectional(inserisciPassword.textProperty());

        // stato iniziale
        vediInserisciPassword.setVisible(false);
        vediInserisciPassword.setManaged(false);

        inserisciPassword.setVisible(true);
        inserisciPassword.setManaged(true);
        vediInserisciPassword.setOnKeyPressed(this::handleKeyPress);

        // toggle con click sull'icona
        togglePassword.setOnMouseClicked(e -> togglePassword());

        gu.aggiornaLista();

        inserisciMail.setOnKeyPressed(this::handleKeyPress);
        inserisciPassword.setOnKeyPressed(this::handleKeyPress);

        inserisciMail.textProperty().addListener((o, a, b) -> clearError());
        inserisciPassword.textProperty().addListener((o, a, b) -> clearError());
    }
    private boolean mostraPassword = false;

    private void togglePassword() {
        mostraPassword = !mostraPassword;

        inserisciPassword.setVisible(!mostraPassword);
        inserisciPassword.setManaged(!mostraPassword);

        vediInserisciPassword.setVisible(mostraPassword);
        vediInserisciPassword.setManaged(mostraPassword);

        togglePassword.setText(mostraPassword ? "🙈" : "👁");
    }

    private Control getPasswordField() {
        return inserisciPassword.isVisible() ? inserisciPassword : vediInserisciPassword;
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) login();
    }

    @FXML
    public void login() {
        String mail     = inserisciMail.getText().trim();
        String password = inserisciPassword.getText();

        if (mail.isEmpty() && password.isEmpty()) {
            highlight(inserisciMail); highlight(getPasswordField());
            showError("Inserisci email e password."); return;
        }
        if (mail.isEmpty())     { highlight(inserisciMail);     showError("Inserisci la tua email.");    return; }
        if (password.isEmpty()) { highlight(getPasswordField()); showError("Inserisci la tua password."); return; }

        ArrayList<Utente> lista = gu.getUtenti();
        int foundIndex = -1;
        for (int i = 0; i < lista.size(); i++) {
            Utente u = lista.get(i);
            if (u.getMail().equals(mail) && u.getPassword().equals(password)) {
                foundIndex = i;
                break;
            }
        }

        if (foundIndex < 0) {
            highlight(inserisciMail); highlight(getPasswordField());
            showError("Email o password errati.");
            shakeLabel(segnalaErrore);
            return;
        }

        clearError();
        gu.setLogin(foundIndex);
        Utente utente = lista.get(foundIndex);

        // Routing basato sul ruolo letto dal file — zero hardcoding
        if (utente.getRuolo() == Ruolo.ADMIN) {
            navigate("../GUI/admin.fxml", "Admin Panel", btnHome);
            return;
        }

        String dest = gu.getSchermataPrecedente();
        if (dest == null) dest = "Home";
        switch (dest) {
            case "PrenotaPage":   prenotaPage();       break;
            case "UserMainPageA": userMainPage(false); break;
            case "UserMainPageP": userMainPage(true);  break;
            case "Profilo":       navigate("../GUI/profilo.fxml", "Il mio profilo", btnHome); break;
            case "Biglietti":    navigate("../GUI/biglietti.fxml", "I miei biglietti", btnHome); break;
            default:              navigate("../GUI/user.fxml", "FastRail Station",   btnHome); break;
        }
    }

    @FXML private void handleBtnHome()         { navigate("../GUI/user.fxml",    "FastRail Station", btnHome); }
    @FXML private void handleRegistratiLink()  { navigate("../GUI/signin.fxml",  "Registrati",       vaiRegistrati); }
    @FXML private void handlePasswChangeLink() { navigate("../GUI/pwChange.fxml","Cambia Password",  vaiResetPassword); }

    private void prenotaPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../GUI/prenotazione.fxml"));
            Parent root = loader.load();
            PrenotazioneController ctrl = loader.getController();
            ctrl.setLogged(true);
            Stage stage = new Stage(); stage.setTitle("Prenotazione");
            stage.setScene(new Scene(root)); stage.show(); closeStage(btnHome);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void userMainPage(boolean partenze) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../GUI/userMain.fxml"));
            Parent root = loader.load();
            UserMainController ctrl = loader.getController();
            ctrl.setPartenzeSelected(partenze);
            Stage stage = new Stage(); stage.setTitle(partenze ? "Partenze" : "Arrivi");
            stage.setScene(new Scene(root)); stage.show(); closeStage(btnHome);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void navigate(String path, String title, javafx.scene.Node src) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            Stage stage = new Stage(); stage.setTitle(title);
            stage.setScene(new Scene(root)); stage.show(); closeStage(src);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void closeStage(javafx.scene.Node node) {
        if (node != null && node.getScene() != null)
            ((Stage) node.getScene().getWindow()).close();
    }

    private void showError(String msg) {
        if (segnalaErrore == null) return;
        segnalaErrore.setText(msg);
        segnalaErrore.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px;");
    }

    private void clearError() {
        if (segnalaErrore != null) segnalaErrore.setText("");
        inserisciMail.setStyle(""); inserisciPassword.setStyle("");
    }

    private void highlight(Control ctrl) {
        ctrl.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2; -fx-border-radius: 6;");
    }

    private void shakeLabel(Label lbl) {
        if (lbl == null) return;
        double ox = lbl.getTranslateX();
        new Timeline(
                new KeyFrame(Duration.millis(0),   new KeyValue(lbl.translateXProperty(), ox)),
                new KeyFrame(Duration.millis(60),  new KeyValue(lbl.translateXProperty(), ox - 8)),
                new KeyFrame(Duration.millis(120), new KeyValue(lbl.translateXProperty(), ox + 8)),
                new KeyFrame(Duration.millis(180), new KeyValue(lbl.translateXProperty(), ox - 6)),
                new KeyFrame(Duration.millis(240), new KeyValue(lbl.translateXProperty(), ox + 6)),
                new KeyFrame(Duration.millis(300), new KeyValue(lbl.translateXProperty(), ox))
        ).play();
    }
}