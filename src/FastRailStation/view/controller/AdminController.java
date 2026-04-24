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
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class AdminController {

    // ── Header ────────────────────────────────────────────────────────────────
    @FXML private Label lblOrologio;

    // ── Filtri sidebar ────────────────────────────────────────────────────────
    @FXML private Button   btnFiltroTutti;
    @FXML private Button   btnFiltroPartenze;
    @FXML private Button   btnFiltroArrivi;
    @FXML private Button   btnFiltroManut;
    @FXML private DatePicker dpFiltroData;
    @FXML private TextField  txtFiltroCerca;
    @FXML private TextField  txtFiltroCompagnia;

    // ── Aggiungi treno sidebar ────────────────────────────────────────────────
    @FXML private ComboBox<String> cmbModello;
    @FXML private TextField        tfProvenienza;
    @FXML private TextField        tfDestinazione;
    @FXML private TextField        tfCompagnia;
    @FXML private TextField        tfCodice;
    @FXML private TextField        tfOraArrivo;
    @FXML private TextField        tfOraPartenza;
    @FXML private TextField        tfPostiMax;
    @FXML private TextField        tfIntervallo;
    @FXML private ComboBox<String> cmbStato;
    @FXML private Label            lblAggiungiMsg;

    // ── Tabella principale ────────────────────────────────────────────────────
    @FXML private TableView<Treno>           tblTreni;
    @FXML private TableColumn<Treno,String>  colModello;
    @FXML private TableColumn<Treno,String>  colProvenienza;
    @FXML private TableColumn<Treno,String>  colDestinazione;
    @FXML private TableColumn<Treno,String>  colCompagnia;
    @FXML private TableColumn<Treno,String>  colCodice;
    @FXML private TableColumn<Treno,Integer> colBinario;
    @FXML private TableColumn<Treno,String>  colOraArrivo;
    @FXML private TableColumn<Treno,String>  colOraPartenza;
    @FXML private TableColumn<Treno,String>  colStato;
    @FXML private TableColumn<Treno,Integer> colRitardo;
    @FXML private TableColumn<Treno,String>  colAzioni;
    @FXML private Label                       lblTitolo;
    @FXML private Label                       lblContatore;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter TIME_PARSE = DateTimeFormatter.ofPattern("HH:mm");

    /** Modalità filtro corrente: "tutti" | "partenze" | "arrivi" | "manutenzione" */
    private String modalitaFiltro = "tutti";

    private Stage dettagliStage = null;

    private final GestioneTreni  gt = GestioneTreni.getInstance();
    private final GestioneUtenti gu = GestioneUtenti.getInstance();

    @FXML
    private void initialize() {
        dpFiltroData.setValue(LocalDate.now());
        popolaCombobox();
        setupColonne();
        startClock();
        handleFiltroTutti();
    }

    // ── Combobox ──────────────────────────────────────────────────────────────

    private void popolaCombobox() {
        cmbModello.getItems().addAll(
                "Frecciarossa 1000", "Frecciarossa 700", "Frecciarossa 500",
                "Frecciargento", "Frecciabianca", "Intercity", "Intercity Notte",
                "Regionale", "Regionale Veloce", "Italo Next", "Italo EVO", "Italo AGV",
                "EuroCity ETR610", "EuroNight");
        cmbStato.getItems().addAll(
                "In partenza", "In arrivo", "In attesa", "In manutenzione", "In corsa");
        cmbStato.setValue("In partenza");
    }

    // ── Colonne tabella ───────────────────────────────────────────────────────

    private void setupColonne() {
        colModello.setCellValueFactory(      cd -> cd.getValue().modelloProperty());
        colProvenienza.setCellValueFactory(  cd -> cd.getValue().provenienzaProperty());
        colDestinazione.setCellValueFactory( cd -> cd.getValue().destinazioneProperty());
        colCompagnia.setCellValueFactory(    cd -> cd.getValue().compagniaProperty());
        colCodice.setCellValueFactory(       cd -> cd.getValue().codiceProperty());
        colBinario.setCellValueFactory(      cd -> cd.getValue().binarioProperty().asObject());
        colStato.setCellValueFactory(        cd -> cd.getValue().statoProperty());
        colRitardo.setCellValueFactory(      cd -> cd.getValue().ritardoProperty().asObject());

        colOraArrivo.setCellValueFactory(cd -> {
            LocalTime t = cd.getValue().getOraArrivo();
            return new SimpleStringProperty(t != null ? t.format(TIME_FMT) : "--:--");
        });
        colOraPartenza.setCellValueFactory(cd -> {
            LocalTime t = cd.getValue().getOraPartenza();
            return new SimpleStringProperty(t != null ? t.format(TIME_FMT) : "--:--");
        });

        // Stato colorato
        colStato.setCellFactory(col -> new TableCell<Treno, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "In partenza":     setStyle("-fx-text-fill: #4cff72;  -fx-font-weight: bold;"); break;
                    case "In arrivo":       setStyle("-fx-text-fill: #ffdd57;  -fx-font-weight: bold;"); break;
                    case "In attesa":       setStyle("-fx-text-fill: #73c2fb;  -fx-font-weight: bold;"); break;
                    case "In manutenzione": setStyle("-fx-text-fill: #ff6b6b;  -fx-font-weight: bold;"); break;
                    case "In corsa":        setStyle("-fx-text-fill: #b8aeff;  -fx-font-weight: bold;"); break;
                    default:                setStyle("-fx-text-fill: #f4e7e7;");
                }
            }
        });

        // Ritardo colorato
        colRitardo.setCellFactory(col -> new TableCell<Treno, Integer>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item == 0 ? "—" : "+" + item + " min");
                setStyle(item == 0 ? "-fx-text-fill: #4cff72;" : "-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
            }
        });

        // Binario: mai mostrare 0 o -1
        colBinario.setCellFactory(col -> new TableCell<Treno, Integer>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null || item <= 0 ? "-" : String.valueOf(item));
            }
        });

        // Colonna azioni: pulsanti Modifica + Rimuovi
        colAzioni.setCellFactory(col -> new TableCell<Treno, String>() {
            private final Button btnMod = new Button("✏");
            private final Button btnDel = new Button("🗑");
            private final HBox   box    = new HBox(4, btnMod, btnDel);

            {
                btnMod.setStyle("-fx-background-color: #9a9aa3; -fx-text-fill: #800303; -fx-font-size: 12px; -fx-cursor: hand; -fx-background-radius: 4; -fx-padding: 2 6 2 6;");
                btnDel.setStyle("-fx-background-color: #3d1515; -fx-text-fill: #ff6b6b; -fx-font-size: 12px; -fx-cursor: hand; -fx-background-radius: 4; -fx-padding: 2 6 2 6;");

                btnMod.setOnAction(e -> {
                    Treno t = getTableView().getItems().get(getIndex());
                    apriDettagliAdmin(t);
                });
                btnDel.setOnAction(e -> {
                    Treno t = getTableView().getItems().get(getIndex());
                    if (confermaRimozione(t)) {
                        gt.rimuoviTreno(t);
                        gt.aggiornaLista();
                        ricarica();
                        aggiornaContatore();
                    }
                });
            }

            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // Click su riga → modifica inline
        tblTreni.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, neu) -> { if (neu != null) apriDettagliAdmin(neu); });
    }

    // ── Filtri ────────────────────────────────────────────────────────────────

    @FXML public void handleFiltroTutti() {
        modalitaFiltro = "tutti";
        lblTitolo.setText("TUTTI I TRENI");
        tblTreni.setItems(gt.getElencoLista());
        aggiornaStileBtnFiltro(btnFiltroTutti);
        aggiornaContatore();
    }

    @FXML public void handleFiltroPartenze() {
        modalitaFiltro = "partenze";
        lblTitolo.setText("PARTENZE");
        gt.setDataPartenzaAdmin(dpFiltroData.getValue());
        tblTreni.setItems(gt.getElencoListaPartenze());
        aggiornaStileBtnFiltro(btnFiltroPartenze);
        aggiornaContatore();
    }

    @FXML public void handleFiltroArrivi() {
        modalitaFiltro = "arrivi";
        lblTitolo.setText("ARRIVI");
        gt.setDataArrivoAdmin(dpFiltroData.getValue());
        tblTreni.setItems(gt.getElencoListaArrivi());
        aggiornaStileBtnFiltro(btnFiltroArrivi);
        aggiornaContatore();
    }

    @FXML public void handleFiltroManutenzione() {
        modalitaFiltro = "manutenzione";
        lblTitolo.setText("IN MANUTENZIONE");
        gt.setDataManutenzione(dpFiltroData.getValue());
        tblTreni.setItems(gt.getElencoListaManutenzione());
        aggiornaStileBtnFiltro(btnFiltroManut);
        aggiornaContatore();
    }

    @FXML
    private void handleApplicaFiltro() {
        String cerca     = txt(txtFiltroCerca);
        String compagnia = txt(txtFiltroCompagnia);
        LocalDate data   = dpFiltroData.getValue() != null ? dpFiltroData.getValue() : LocalDate.now();

        switch (modalitaFiltro) {
            case "partenze":
                gt.setDataPartenzaAdmin(data);
                if (!compagnia.isEmpty()) gt.aggiornaPartenzaAdmin(cerca, compagnia);
                else gt.aggiornaPartenza(cerca);
                tblTreni.setItems(gt.getElencoListaPartenze());
                break;
            case "arrivi":
                gt.setDataArrivoAdmin(data);
                if (!compagnia.isEmpty()) gt.aggiornaArrivoAdmin(cerca, compagnia);
                else gt.aggiornaArrivo(cerca);
                tblTreni.setItems(gt.getElencoListaArrivi());
                break;
            case "manutenzione":
                gt.setDataManutenzione(data);
                if (!compagnia.isEmpty()) gt.aggiornaManutenzioneAdmin(cerca, compagnia);
                tblTreni.setItems(gt.getElencoListaManutenzione());
                break;
            default:
                // Filtra su lista completa per testo libero
                javafx.collections.ObservableList<Treno> tutti = gt.getElencoLista();
                javafx.collections.ObservableList<Treno> filtrati =
                        javafx.collections.FXCollections.observableArrayList();
                for (Treno t : tutti) {
                    boolean matchTesto = cerca.isEmpty()
                            || t.getDestinazione().toLowerCase().contains(cerca)
                            || t.getProvenienza().toLowerCase().contains(cerca)
                            || t.getCodice().toLowerCase().contains(cerca);
                    boolean matchComp  = compagnia.isEmpty()
                            || t.getCompagnia().toLowerCase().contains(compagnia.toLowerCase());
                    if (matchTesto && matchComp) filtrati.add(t);
                }
                tblTreni.setItems(filtrati);
        }
        aggiornaContatore();
    }

    @FXML
    private void handleResetFiltro() {
        txtFiltroCerca.clear();
        txtFiltroCompagnia.clear();
        dpFiltroData.setValue(LocalDate.now());
        handleFiltroTutti();
    }

    private void aggiornaStileBtnFiltro(Button attivo) {
        String on  = "-fx-background-color: #800303; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 4;";
        String off = "-fx-background-color: #2a3a4a; -fx-text-fill: #9a9aa3; -fx-font-size: 11px; -fx-background-radius: 4;";
        for (Button b : new Button[]{btnFiltroTutti, btnFiltroPartenze, btnFiltroArrivi, btnFiltroManut})
            b.setStyle(b == attivo ? on : off);
    }

    // ── Aggiungi treno ────────────────────────────────────────────────────────

    @FXML
    private void handleAggiungiTreno() {
        // Validazione
        if (cmbModello.getValue() == null || tfProvenienza.getText().trim().isEmpty()
                || tfDestinazione.getText().trim().isEmpty() || tfCompagnia.getText().trim().isEmpty()
                || tfCodice.getText().trim().isEmpty() || tfOraArrivo.getText().trim().isEmpty()
                || tfOraPartenza.getText().trim().isEmpty()) {
            msgAggiungi("Compila tutti i campi obbligatori.", false);
            return;
        }

        // Controllo codice duplicato
        String codice = tfCodice.getText().trim();
        for (Treno t : gt.getElencoLista()) {
            if (t.getCodice().equalsIgnoreCase(codice)) {
                msgAggiungi("Codice treno già esistente.", false);
                return;
            }
        }

        LocalTime oraArr, oraPart;
        try {
            oraArr  = LocalTime.parse(tfOraArrivo.getText().trim(),   TIME_PARSE);
            oraPart = LocalTime.parse(tfOraPartenza.getText().trim(), TIME_PARSE);
        } catch (Exception ex) {
            msgAggiungi("Formato orario non valido (usa HH:mm).", false);
            return;
        }

        // Orario realistico: arrivo almeno 15 min prima della partenza
        if (!oraArr.isBefore(oraPart.minusMinutes(15))) {
            msgAggiungi("L'ora di arrivo deve precedere quella di partenza di almeno 15 min.", false);
            return;
        }

        int postiMax  = parseIntOr(tfPostiMax.getText(),  500);
        int intervallo = parseIntOr(tfIntervallo.getText(), 1);
        LocalDate oggi = LocalDate.now();

        gt.addTreno(
                cmbModello.getValue(),
                tfProvenienza.getText().trim(),
                tfDestinazione.getText().trim(),
                tfCompagnia.getText().trim(),
                codice,
                postiMax,
                oggi, oraArr,
                oggi, oraPart,
                intervallo,
                cmbStato.getValue() != null ? cmbStato.getValue() : "In partenza",
                0, 0);

        pulisciFormAggiungi();
        ricarica();
        msgAggiungi("Treno aggiunto con binario " +
                gt.getElencoLista().get(gt.getElencoLista().size()-1).getBinario() + "!", true);
    }

    private void pulisciFormAggiungi() {
        cmbModello.setValue(null);
        for (TextField tf : new TextField[]{tfProvenienza, tfDestinazione, tfCompagnia,
                tfCodice, tfOraArrivo, tfOraPartenza, tfPostiMax, tfIntervallo})
            tf.clear();
        cmbStato.setValue("In partenza");
    }

    private void msgAggiungi(String msg, boolean ok) {
        lblAggiungiMsg.setText(msg);
        lblAggiungiMsg.setStyle(ok
                ? "-fx-text-fill: #4cff72; -fx-font-size: 12px;"
                : "-fx-text-fill: #ff6b6b; -fx-font-size: 12px;");
    }

    // ── Modifica dettagli (finestra separata) ─────────────────────────────────

    private void apriDettagliAdmin(Treno treno) {
        if (dettagliStage != null && dettagliStage.isShowing()) dettagliStage.close();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("../GUI/dettagliTrenoAdmin.fxml"));
            Parent root = loader.load();
            DettagliTrenoAdminController ctrl = loader.getController();
            ctrl.setTreno(treno);
            ctrl.setOnSaveCallback(this::ricarica);   // aggiorna tabella dopo salvataggio
            dettagliStage = new Stage();
            dettagliStage.setTitle("Modifica — " + treno.getCodice());
            dettagliStage.setScene(new Scene(root));
            dettagliStage.setResizable(false);
            dettagliStage.setOnCloseRequest(e -> {
                tblTreni.getSelectionModel().clearSelection();
                dettagliStage = null;
                ricarica();
            });
            Rectangle2D b = Screen.getPrimary().getVisualBounds();
            dettagliStage.setX(b.getMinX() + b.getWidth() / 2 - 310);
            dettagliStage.setY(b.getMinY() + 40);
            dettagliStage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Conferma rimozione ────────────────────────────────────────────────────

    private boolean confermaRimozione(Treno t) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma rimozione");
        alert.setHeaderText("Rimuovere il treno " + t.getCodice() + "?");
        alert.setContentText(t.getProvenienza() + " → " + t.getDestinazione());
        return alert.showAndWait()
                .filter(r -> r == ButtonType.OK)
                .isPresent();
    }

    // ── Navigazione ───────────────────────────────────────────────────────────

    @FXML
    private void handleHomeUtente() { navigateTo("../GUI/user.fxml", "FastRail Station"); }

    @FXML
    private void handleLogout() {
        gu.logout();
        navigateTo("../GUI/user.fxml", "FastRail Station");
    }

    private void navigateTo(String path, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            Stage cur = lblOrologio != null && lblOrologio.getScene() != null
                    ? (Stage) lblOrologio.getScene().getWindow() : null;
            if (cur != null) cur.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void ricarica() {
        switch (modalitaFiltro) {
            case "partenze":    handleFiltroPartenze();    break;
            case "arrivi":      handleFiltroArrivi();      break;
            case "manutenzione":handleFiltroManutenzione();break;
            default:            handleFiltroTutti();
        }
    }

    private void aggiornaContatore() {
        int n = tblTreni.getItems() != null ? tblTreni.getItems().size() : 0;
        lblContatore.setText(n + (n == 1 ? " treno" : " treni"));
    }

    private String txt(TextField tf) {
        return tf.getText() == null ? "" : tf.getText().trim().toLowerCase();
    }

    private int parseIntOr(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }

    private void startClock() {
        new AnimationTimer() {
            @Override public void handle(long now) {
                LocalTime t = LocalTime.now();
                lblOrologio.setText(String.format("%02d:%02d:%02d",
                        t.getHour(), t.getMinute(), t.getSecond()));
            }
        }.start();
    }
}
