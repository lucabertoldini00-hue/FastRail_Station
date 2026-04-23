package FastRailStation.view.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import FastRailStation.model.GestioneTreni;
import FastRailStation.model.GestioneUtenti;
import FastRailStation.model.Treno;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class UserMainController {

    // ── Nav ──────────────────────────────────────────────────────────────────
    @FXML private Label navHome;
    @FXML private Label navArrivi;
    @FXML private Label navPartenze;
    @FXML private Label navPrenota;
    @FXML private Label navProfilo;

    // ── Sidebar ───────────────────────────────────────────────────────────────
    @FXML private Button   btnArrivi;
    @FXML private Button   btnPartenze;
    @FXML private DatePicker dpData;
    @FXML private TextField  txtCerca;
    @FXML private TextField  txtCompagnia;
    @FXML private Label      lblOrologio;
    @FXML private Label      lblContatore;
    @FXML private Label      lblTitoloTabellone;

    // ── Table ─────────────────────────────────────────────────────────────────
    @FXML private TableView<Treno>             tblTabellone;
    @FXML private TableColumn<Treno, String>   colOrario;
    @FXML private TableColumn<Treno, String>   colProvenienza;
    @FXML private TableColumn<Treno, String>   colDestinazione;
    @FXML private TableColumn<Treno, String>   colCodice;
    @FXML private TableColumn<Treno, Integer>  colBinario;
    @FXML private TableColumn<Treno, String>   colCompagnia;
    @FXML private TableColumn<Treno, String>   colStato;
    @FXML private TableColumn<Treno, Integer>  colRitardo;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    /** true = stiamo mostrando le partenze; false = arrivi */
    private boolean mostraPartenze = false;

    private Stage dettagliStage = null;

    private final GestioneTreni  gt = GestioneTreni.getInstance();
    private final GestioneUtenti gu = GestioneUtenti.getInstance();

    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    private void initialize() {
        dpData.setValue(LocalDate.now());

        setupColumns();
        setupNavigation();
        setupListeners();
        startClock();

        // Mostra arrivi di default
        handleArrivi();
    }

    /** Chiamato da LoginController / SignInController prima che la scena sia mostrata. */
    public void setPartenzeSelected(boolean partenze) {
        // Rinviato a dopo initialize() — verrà eseguito nell'FX thread
        javafx.application.Platform.runLater(() -> {
            if (partenze) handlePartenze();
            else          handleArrivi();
        });
    }

    // ── Colonne ───────────────────────────────────────────────────────────────

    private void setupColumns() {
        // Orario: format LocalTime as HH:mm; in arrivi = oraArrivo, in partenze = oraPartenza
        // Il binding viene riapplicato in handleArrivi/handlePartenze
        colCodice.setCellValueFactory(cd -> cd.getValue().codiceProperty());
        colCompagnia.setCellValueFactory(cd -> cd.getValue().compagniaProperty());
        colProvenienza.setCellValueFactory(cd -> cd.getValue().provenienzaProperty());
        colDestinazione.setCellValueFactory(cd -> cd.getValue().destinazioneProperty());
        colBinario.setCellValueFactory(cd -> cd.getValue().binarioProperty().asObject());
        colRitardo.setCellValueFactory(cd -> cd.getValue().ritardoProperty().asObject());

        // Stato con colore
        colStato.setCellValueFactory(cd -> cd.getValue().statoProperty());
        colStato.setCellFactory(col -> new TableCell<Treno, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "In partenza":    setStyle("-fx-text-fill: #4cff72;  -fx-font-weight: bold;"); break;
                    case "In arrivo":      setStyle("-fx-text-fill: #ffdd57;  -fx-font-weight: bold;"); break;
                    case "In attesa":      setStyle("-fx-text-fill: #73c2fb;  -fx-font-weight: bold;"); break;
                    case "In manutenzione":setStyle("-fx-text-fill: #ff6b6b;  -fx-font-weight: bold;"); break;
                    case "In corsa":       setStyle("-fx-text-fill: #b8aeff;  -fx-font-weight: bold;"); break;
                    default:               setStyle("-fx-text-fill: #f4e7e7;");
                }
            }
        });

        // Ritardo con colore
        colRitardo.setCellFactory(col -> new TableCell<Treno, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item == 0 ? "In orario" : "+" + item + " min");
                setStyle(item == 0
                        ? "-fx-text-fill: #4cff72;"
                        : "-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
            }
        });

        // Binario formattato
        colBinario.setCellFactory(col -> new TableCell<Treno, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item <= 0 ? "-" : String.valueOf(item));
            }
        });

        // Click su riga → apri dettagli
        tblTabellone.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, neu) -> { if (neu != null) apriDettagli(neu); });
    }

    // ── Modalità Arrivi/Partenze ──────────────────────────────────────────────

    @FXML
    public void handleArrivi() {
        mostraPartenze = false;
        lblTitoloTabellone.setText("◉  ARRIVI");

        // Orario = ora di arrivo
        colOrario.setCellValueFactory(cd -> {
            LocalTime t = cd.getValue().getOraArrivo();
            return new SimpleStringProperty(t != null ? t.format(TIME_FMT) : "--:--");
        });

        aggiornaBtnStyle(false);
        ricarica();
    }

    @FXML
    public void handlePartenze() {
        mostraPartenze = true;
        lblTitoloTabellone.setText("◉  PARTENZE");

        // Orario = ora di partenza
        colOrario.setCellValueFactory(cd -> {
            LocalTime t = cd.getValue().getOraPartenza();
            return new SimpleStringProperty(t != null ? t.format(TIME_FMT) : "--:--");
        });

        aggiornaBtnStyle(true);
        ricarica();
    }

    private void aggiornaBtnStyle(boolean partenzeAttive) {
        String attivo   = "-fx-background-color: #800303; -fx-text-fill: white; -fx-font-weight: bold;";
        String inattivo = "-fx-background-color: #2a3a4a; -fx-text-fill: #9a9aa3; -fx-font-weight: bold;";

        if (partenzeAttive) {
            btnPartenze.setStyle(attivo   + "-fx-background-radius: 0 6 6 0; -fx-font-size: 11px;");
            btnArrivi.setStyle(  inattivo + "-fx-background-radius: 6 0 0 6; -fx-font-size: 12px;");
        } else {
            btnArrivi.setStyle(  attivo   + "-fx-background-radius: 6 0 0 6; -fx-font-size: 12px;");
            btnPartenze.setStyle(inattivo + "-fx-background-radius: 0 6 6 0; -fx-font-size: 11px;");
        }
    }

    // ── Filtri ────────────────────────────────────────────────────────────────

    @FXML
    private void handleFiltra() {
        String cerca     = txtCerca.getText()     == null ? "" : txtCerca.getText().trim();
        String compagnia = txtCompagnia.getText() == null ? "" : txtCompagnia.getText().trim();

        if (mostraPartenze) {
            gt.setDataPartenza(dpData.getValue());
            if (!compagnia.isEmpty())
                gt.aggiornaPartenzaAdmin(cerca, compagnia);
            else
                gt.aggiornaPartenza(cerca);
            tblTabellone.setItems(gt.getElencoListaPartenze());
        } else {
            gt.setDataArrivo(dpData.getValue());
            if (!compagnia.isEmpty())
                gt.aggiornaArrivoAdmin(cerca, compagnia);
            else
                gt.aggiornaArrivo(cerca);
            tblTabellone.setItems(gt.getElencoListaArrivi());
        }
        aggiornaContatore();
    }

    @FXML
    private void handleReset() {
        txtCerca.clear();
        txtCompagnia.clear();
        dpData.setValue(LocalDate.now());
        ricarica();
    }

    // ── Ricarica dati ─────────────────────────────────────────────────────────

    private void ricarica() {
        LocalDate data = dpData.getValue() != null ? dpData.getValue() : LocalDate.now();
        if (mostraPartenze) {
            gt.setDataPartenza(data);
            tblTabellone.setItems(gt.getElencoListaPartenze());
        } else {
            gt.setDataArrivo(data);
            tblTabellone.setItems(gt.getElencoListaArrivi());
        }
        aggiornaContatore();
    }

    private void aggiornaContatore() {
        int n = tblTabellone.getItems() != null ? tblTabellone.getItems().size() : 0;
        lblContatore.setText(n + (n == 1 ? " treno" : " treni"));
    }

    // ── Dettagli treno ────────────────────────────────────────────────────────

    private void apriDettagli(Treno treno) {
        if (dettagliStage != null && dettagliStage.isShowing()) dettagliStage.close();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../GUI/dettagliTreno.fxml"));
            Parent root = loader.load();
            DettagliTrenoController ctrl = loader.getController();
            ctrl.setTreno(treno);
            dettagliStage = new Stage();
            dettagliStage.setTitle("Dettagli treno — " + treno.getCodice());
            dettagliStage.setScene(new Scene(root));
            dettagliStage.setResizable(false);
            dettagliStage.setOnCloseRequest(e -> {
                tblTabellone.getSelectionModel().clearSelection();
                dettagliStage = null;
            });
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            dettagliStage.setX(bounds.getMinX() + 30);
            dettagliStage.setY(bounds.getMinY() + 30);
            dettagliStage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Navigazione ───────────────────────────────────────────────────────────

    private void setupNavigation() {
        wire(navHome,     () -> navigateTo("../GUI/user.fxml", "FastRail Station"));
        wire(navArrivi,   () -> handleArrivi());
        wire(navPartenze, () -> handlePartenze());
        wire(navPrenota,  () -> {
            gu.setSchermataPrecedente("PrenotaPage");
            navigateTo("../GUI/prenotazione.fxml", "Prenotazione");
        });
        wire(navProfilo, () -> {
            if (gu.isLogged()) {
                navigateTo("../GUI/profilo.fxml", "Il mio profilo");
            } else {
                gu.setSchermataPrecedente(mostraPartenze ? "UserMainPageP" : "UserMainPageA");
                navigateTo("../GUI/login.fxml", "Login");
            }
        });

        // Aggiorna label profilo
        if (navProfilo != null && gu.isLogged()) {
            int idx = gu.getIndice();
            if (idx >= 0 && idx < gu.getUtenti().size())
                navProfilo.setText("👤 " + gu.getUtenti().get(idx).getNome());
        }
    }

    private void wire(Label lbl, Runnable action) {
        if (lbl != null) lbl.setOnMouseClicked(e -> action.run());
    }

    private void navigateTo(String path, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            Stage stage = navHome != null && navHome.getScene() != null
                    ? (Stage) navHome.getScene().getWindow()
                    : new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showNavigationError(path, e);
        }
    }

    private void showNavigationError(String path, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore navigazione");
        alert.setHeaderText("Impossibile aprire la schermata");
        alert.setContentText("Path: " + path + "\n" + e.getClass().getSimpleName() + ": " + e.getMessage());
        alert.showAndWait();
    }

    private void chiudiStageCorrente() {
        Stage s = navHome != null && navHome.getScene() != null
                ? (Stage) navHome.getScene().getWindow() : null;
        if (s == null && navArrivi != null && navArrivi.getScene() != null)
            s = (Stage) navArrivi.getScene().getWindow();
        if (s != null) s.close();
    }

    // ── Listeners dinamici ────────────────────────────────────────────────────

    private void setupListeners() {
        dpData.valueProperty().addListener((obs, o, n) -> { if (n != null) ricarica(); });
        txtCerca.textProperty().addListener((obs, o, n) -> {
            if (n == null || n.isEmpty()) ricarica(); else handleFiltra();
        });
    }

    // ── Orologio ──────────────────────────────────────────────────────────────

    private void startClock() {
        AnimationTimer timer = new AnimationTimer() {
            @Override public void handle(long now) {
                LocalTime t = LocalTime.now();
                lblOrologio.setText(String.format("%02d:%02d:%02d",
                        t.getHour(), t.getMinute(), t.getSecond()));
            }
        };
        timer.start();
    }
}
