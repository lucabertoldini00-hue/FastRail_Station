package FastRailStation.view.controller;

import FastRailStation.model.GestioneUtenti;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.time.LocalDate;

public class UserController {

    @FXML private Label      navHome;
    @FXML private Label      navArrivi;
    @FXML private Label      navPartenze;
    @FXML private Label      navPrenota;
    @FXML private Label      navBiglietti;   // ← NUOVO
    @FXML private Label      navProfilo;

    @FXML private TextField  searchDestinazione;
    @FXML private DatePicker searchData;

    private final GestioneUtenti gu = GestioneUtenti.getInstance();

    @FXML
    private void initialize() {
        if (searchData != null) searchData.setValue(LocalDate.now());

        // ── Barra nav ────────────────────────────────────────────────────────
        wire(navHome, () -> navigateTo("../GUI/user.fxml", "FastRail Station"));

        wire(navArrivi, () -> {
            gu.setSchermataPrecedente("UserMainPageA");
            apriTabellone(false);
        });
        wire(navPartenze, () -> {
            gu.setSchermataPrecedente("UserMainPageP");
            apriTabellone(true);
        });
        wire(navPrenota, () -> {
            gu.setSchermataPrecedente("PrenotaPage");
            openPrenotazione(null, null);
        });

        // NUOVO: voce Biglietti nella nav
        wire(navBiglietti, () -> openBiglietti());

        wire(navProfilo, () -> {
            if (gu.isLogged()) navigateTo("../GUI/profilo.fxml", "Il mio profilo");
            else { gu.setSchermataPrecedente("Home"); navigateTo("../GUI/login.fxml", "Login"); }
        });

        // ── Label profilo dinamica ────────────────────────────────────────────
        if (navProfilo != null) {
            if (gu.isLogged()) {
                int idx = gu.getIndice();
                if (idx >= 0 && idx < gu.getUtenti().size())
                    navProfilo.setText("👤 " + gu.getUtenti().get(idx).getNome());
            } else {
                navProfilo.setText("Accedi");
            }
        }
    }

    // ── CERCA button ─────────────────────────────────────────────────────────

    @FXML
    private void handleCerca() {
        String dest = searchDestinazione != null ? searchDestinazione.getText().trim() : "";
        LocalDate data = searchData != null && searchData.getValue() != null
                ? searchData.getValue() : LocalDate.now();
        gu.setSchermataPrecedente("PrenotaPage");
        openPrenotazione(dest.isEmpty() ? null : dest, data);
    }

    // ── Quick-link cards ──────────────────────────────────────────────────────

    @FXML private void handleQuickArrivi(MouseEvent e) {
        gu.setSchermataPrecedente("UserMainPageA");
        apriTabellone(false);
    }

    @FXML private void handleQuickPartenze(MouseEvent e) {
        gu.setSchermataPrecedente("UserMainPageP");
        apriTabellone(true);
    }

    @FXML private void handleQuickPrenota(MouseEvent e) {
        gu.setSchermataPrecedente("PrenotaPage");
        openPrenotazione(null, null);
    }

    /** NUOVO: click sulla card Biglietti. */
    @FXML private void handleQuickBiglietti(MouseEvent e) {
        openBiglietti();
    }

    // ── Navigazione ───────────────────────────────────────────────────────────

    /**
     * Apre la finestra dei biglietti.
     * Se l'utente non è loggato, reindirizza al login salvando la schermata
     * di ritorno in modo che, dopo il login, possa tornare qui.
     */
    private void openBiglietti() {
        if (!gu.isLogged()) {
            gu.setSchermataPrecedente("Home");
            navigateTo("../GUI/login.fxml", "Login");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../GUI/biglietti.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("I miei biglietti");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            chiudiStageCorrente();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apriTabellone(boolean partenze) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../GUI/userMain.fxml"));
            Parent root = loader.load();
            UserMainController ctrl = loader.getController();
            ctrl.setPartenzeSelected(partenze);
            Stage stage = new Stage();
            stage.setTitle(partenze ? "Partenze" : "Arrivi");
            stage.setScene(new Scene(root));
            stage.show();
            chiudiStageCorrente();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openPrenotazione(String destinazione, LocalDate data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../GUI/prenotazione.fxml"));
            Parent root = loader.load();
            PrenotazioneController ctrl = loader.getController();
            ctrl.setLogged(gu.isLogged());
            if (destinazione != null) ctrl.prefillSearch(destinazione, data);
            Stage stage = new Stage();
            stage.setTitle("Prenotazione");
            stage.setScene(new Scene(root));
            stage.show();
            chiudiStageCorrente();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            chiudiStageCorrente();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void wire(Label lbl, Runnable action) {
        if (lbl != null) lbl.setOnMouseClicked(e -> action.run());
    }

    private void chiudiStageCorrente() {
        Label src = navHome != null ? navHome : navArrivi;
        if (src != null && src.getScene() != null)
            ((Stage) src.getScene().getWindow()).close();
    }
}