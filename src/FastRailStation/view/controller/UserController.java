package FastRailStation.view.controller;

import FastRailStation.model.GestioneUtenti;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class UserController {

    @FXML private Label      navHome;
    @FXML private Label      navArrivi;
    @FXML private Label      navPartenze;
    @FXML private Label      navPrenota;
    @FXML private Label      navProfilo;

    // Search form fields — now wired so CERCA actually does something
    @FXML private TextField  searchDestinazione;
    @FXML private DatePicker searchData;

    private final GestioneUtenti gestioneUtenti = GestioneUtenti.getInstance();

    @FXML
    private void initialize() {
        // Default search date to today
        if (searchData != null) searchData.setValue(LocalDate.now());

        // Navigation bar
        wire(navHome,     () -> navigateTo("../GUI/user.fxml", "FastRail Station"));
        wire(navArrivi,   () -> { gestioneUtenti.setSchermataPrecedente("UserMainPageA"); navigateTo("../GUI/userMain.fxml", "Arrivi"); });
        wire(navPartenze, () -> { gestioneUtenti.setSchermataPrecedente("UserMainPageP"); navigateTo("../GUI/userMain.fxml", "Partenze"); });
        wire(navPrenota,  () -> { gestioneUtenti.setSchermataPrecedente("PrenotaPage");   openPrenotazione(null, null); });
        wire(navProfilo,  () -> navigateTo("../GUI/login.fxml", "Login"));

        // Update profilo label to show user name if logged in
        if (navProfilo != null && gestioneUtenti.isLogged()) {
            int idx = gestioneUtenti.getIndice();
            if (idx >= 0 && idx < gestioneUtenti.getUtenti().size())
                navProfilo.setText(gestioneUtenti.getUtenti().get(idx).getNome());
        }
    }

    /**
     * CERCA button handler — navigates to the booking screen and pre-applies
     * the destination filter and selected date so the table is already filtered.
     */
    @FXML
    private void handleCerca() {
        String dest = searchDestinazione != null ? searchDestinazione.getText().trim() : "";
        LocalDate data = searchData != null && searchData.getValue() != null
                ? searchData.getValue() : LocalDate.now();
        gestioneUtenti.setSchermataPrecedente("PrenotaPage");
        openPrenotazione(dest.isEmpty() ? null : dest, data);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void openPrenotazione(String destinazione, LocalDate data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../GUI/prenotazione.fxml"));
            Parent root = loader.load();
            PrenotazioneController ctrl = loader.getController();
            ctrl.setLogged(gestioneUtenti.isLogged());
            // Pre-fill search parameters if supplied by the home search form
            if (destinazione != null) ctrl.prefillSearch(destinazione, data);
            Stage stage = new Stage();
            stage.setTitle("Prenotazione");
            stage.setScene(new Scene(root));
            stage.show();
            closeCurrentStage();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            closeCurrentStage();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void wire(Label lbl, Runnable action) {
        if (lbl != null) lbl.setOnMouseClicked(e -> action.run());
    }

    private void closeCurrentStage() {
        Label src = navHome != null ? navHome : navArrivi;
        if (src != null && src.getScene() != null)
            ((Stage) src.getScene().getWindow()).close();
    }
}
