package FastRailStation.view.controller;

import FastRailStation.model.GestioneTreni;
import FastRailStation.model.GestioneUtenti;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;

public class UserMainController {

    @FXML private Label navHome;
    @FXML private Label navArrivi;
    @FXML private Label navPartenze;
    @FXML private Label navPrenota;
    @FXML private Label navProfilo;

    private boolean partenzeSelected = false;

    private final GestioneTreni  gestioneTreni  = GestioneTreni.getInstance();
    private final GestioneUtenti gestioneUtenti = GestioneUtenti.getInstance();

    /**
     * Called by LoginController / SignInController before the scene is shown
     * to pre-select arrivals (false) or departures (true) tab.
     */
    public void setPartenzeSelected(boolean partenze) {
        this.partenzeSelected = partenze;
    }

    @FXML
    private void initialize() {
        // Pre-load the correct table based on which tab was requested
        if (partenzeSelected)
            gestioneTreni.setDataPartenza(LocalDate.now());
        else
            gestioneTreni.setDataArrivo(LocalDate.now());

        // Wire navigation labels
        if (navHome != null)
            navHome.setOnMouseClicked(e -> navigateTo("../GUI/user.fxml", "FastRail Station"));
        if (navArrivi != null)
            navArrivi.setOnMouseClicked(e -> {
                gestioneUtenti.setSchermataPrecedente("UserMainPageA");
                navigateTo("../GUI/userMain.fxml", "Arrivi");
            });
        if (navPartenze != null)
            navPartenze.setOnMouseClicked(e -> {
                gestioneUtenti.setSchermataPrecedente("UserMainPageP");
                navigateTo("../GUI/userMain.fxml", "Partenze");
            });
        if (navPrenota != null)
            navPrenota.setOnMouseClicked(e -> {
                gestioneUtenti.setSchermataPrecedente("PrenotaPage");
                navigateTo("../GUI/prenotazione.fxml", "Prenotazione");
            });
        if (navProfilo != null)
            navProfilo.setOnMouseClicked(e -> navigateTo("../GUI/login.fxml", "Login"));
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            Stage current = getCurrentStage();
            if (current != null) current.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Stage getCurrentStage() {
        if (navHome != null && navHome.getScene() != null)
            return (Stage) navHome.getScene().getWindow();
        if (navArrivi != null && navArrivi.getScene() != null)
            return (Stage) navArrivi.getScene().getWindow();
        return null;
    }
}