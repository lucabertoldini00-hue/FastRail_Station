package FastRailStation.view.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import FastRailStation.model.Biglietto;
import FastRailStation.model.GestioneTreni;
import FastRailStation.model.GestioneUtenti;
import FastRailStation.model.Treno;
import FastRailStation.salvataggioDati.ScriviDati;

public class PrenotazioneController {

    @FXML private Label            lblOrologio;
    @FXML private Button           btnHome;
    @FXML private Button           btnAccedi;
    @FXML private DatePicker       dpDataPartenza;
    @FXML private TextField        txtDestinazione;
    @FXML private Label            lblAdulti;
    @FXML private Label            lblBambini;
    @FXML private Label            lblBagagli;
    @FXML private ComboBox<String> cbClasse;
    @FXML private Label            lblPrezzo;
    @FXML private Label            segnala;

    @FXML private TableView<Treno>              tblVoli;
    @FXML private TableColumn<Treno, LocalTime> colOrario;
    @FXML private TableColumn<Treno, Integer>   colRitardo;
    @FXML private TableColumn<Treno, String>    colDestinazione;
    @FXML private TableColumn<Treno, String>    colNVolo;
    @FXML private TableColumn<Treno, Integer>   colGate;
    @FXML private TableColumn<Treno, String>    colCompagnia;
    @FXML private TableColumn<Treno, String>    colStato;

    private static final int RISTRETTO_SECONDA = 50;
    private static final int INTERO_SECONDA    = 70;
    private static final int RISTRETTO_PRIMA   = 80;
    private static final int INTERO_PRIMA      = 160;
    private static final int PREZZO_BAGAGLIO   = 25;

    private boolean isLogged         = false;
    private boolean isSelected       = false;
    private Treno   trenoSelezionato = null;
    private int     nAdulti = 0, nBambini = 0, nBagagli = 0, prezzoTot = 0;
    private Stage   infoStage;

    private final GestioneUtenti gestioneUtenti = GestioneUtenti.getInstance();
    private final GestioneTreni  gestioneTreni  = GestioneTreni.getInstance();
    private final ScriviDati     scriviDati     = new ScriviDati();

    @FXML
    public void initialize() {
        isLogged = gestioneUtenti.isLogged();

        startClock();
        initializeTable();
        setupRowSelectionListener();
        cbClasse.getItems().addAll("Prima classe", "Seconda classe");

        if (dpDataPartenza.getValue() == null)
            dpDataPartenza.setValue(LocalDate.now());
        changeData();

        dpDataPartenza.valueProperty().addListener((obs, o, n) -> {
            if (n != null) changepartenza(txtDestinazione.getText());
        });
        cbClasse.valueProperty().addListener((obs, o, n) -> { if (n != null) aggiornaPrezzo(); });
        txtDestinazione.textProperty().addListener((obs, o, n) -> changepartenza(n));

        checkLogin();

        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(60), e -> aggiornaStato()));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();
    }

    /**
     * Called by UserController when the user used the home-screen CERCA form.
     * Pre-fills the destination filter and date so the table is already filtered.
     */
    public void prefillSearch(String destinazione, LocalDate data) {
        if (data != null) dpDataPartenza.setValue(data);
        if (destinazione != null && !destinazione.isEmpty()) {
            txtDestinazione.setText(destinazione);
        }
        changepartenza(txtDestinazione.getText());
    }

    private void initializeTable() {
        colOrario.setCellValueFactory(       cd -> cd.getValue().getOraPartenzaProperty());
        colRitardo.setCellValueFactory(      cd -> cd.getValue().ritardoProperty().asObject());
        colDestinazione.setCellValueFactory( cd -> cd.getValue().destinazioneProperty());
        colNVolo.setCellValueFactory(        cd -> cd.getValue().codiceProperty());
        colGate.setCellValueFactory(         cd -> cd.getValue().binarioProperty().asObject());
        colCompagnia.setCellValueFactory(    cd -> cd.getValue().compagniaProperty());
        colStato.setCellValueFactory(        cd -> cd.getValue().statoProperty());
        tblVoli.setItems(gestioneTreni.getElencoListaPartenze());
    }

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

    private void setupRowSelectionListener() {
        tblVoli.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, neu) -> { if (neu != null) handleRowSelected(neu); });
    }

    public void aggiornaStato() {
        synchronized (gestioneTreni.getElencoLista()) {
            Platform.runLater(() -> {
                for (Treno t : gestioneTreni.getElencoLista()) {
                    if      (t.isInCorsa())        t.setStato("In corsa");
                    else if (t.isInArrivo())       t.setStato("In arrivo");
                    else if (t.isInAttesa())       t.setStato("In attesa");
                    else if (t.isInManutenzione()) t.setStato("In manutenzione");
                }
                changepartenza(txtDestinazione.getText());
            });
        }
    }

    @FXML public void changeData() { gestioneTreni.setDataPartenza(dpDataPartenza.getValue()); }

    public void changepartenza(String filtro) {
        gestioneTreni.setDataPartenza(dpDataPartenza.getValue());
        gestioneTreni.aggiornaPartenza(filtro == null ? "" : filtro);
    }

    private void checkLogin() {
        if (gestioneUtenti.isLogged()) {
            int idx = gestioneUtenti.getIndice();
            if (idx >= 0 && idx < gestioneUtenti.getUtenti().size())
                btnAccedi.setText(gestioneUtenti.getUtenti().get(idx).getNome());
            btnAccedi.setDisable(true);
            btnAccedi.setOpacity(1);
            isLogged = true;
        }
    }

    // ── Booking ───────────────────────────────────────────────────────────────

    @FXML
    public void confermaPrenotazione() {
        if (!controllaTutto()) return;

        trenoSelezionato.setNumeroPostiOccupati(
                trenoSelezionato.getNumeroPostiOccupati() + nAdulti + nBambini);
        gestioneTreni.scriviDati();

        String mailUtente = gestioneUtenti.isLogged()
                ? gestioneUtenti.getUtenti().get(gestioneUtenti.getIndice()).getMail()
                : "ospite";

        Biglietto biglietto = new Biglietto(
                mailUtente,
                trenoSelezionato.getCodice(),
                trenoSelezionato.getProvenienza(),
                trenoSelezionato.getDestinazione(),
                trenoSelezionato.getGiornoPartenza(),
                trenoSelezionato.getOraPartenzaString(),
                nAdulti, nBambini, nBagagli,
                cbClasse.getValue(),
                prezzoTot);
        scriviDati.scríviBiglietto(biglietto);

        resetCampi();

        // Show the full confirmation dialog instead of a 2-second toast
        mostraConferma(biglietto);
    }

    /**
     * Shows a proper modal confirmation card with the ticket code and all
     * booking details — replaces the old 2-second disappearing toast.
     */
    private void mostraConferma(Biglietto b) {
        Stage dialog = new Stage(StageStyle.UNDECORATED);
        dialog.initOwner(tblVoli.getScene().getWindow());

        // ── Card layout ───────────────────────────────────────────────────────
        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36, 48, 36, 48));
        card.setStyle(
                "-fx-background-color: #112240;" +
                        "-fx-border-color: #64ffda;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 14;" +
                        "-fx-background-radius: 14;"
        );
        card.setMaxWidth(460);

        Label icon = new Label("✓");
        icon.setStyle("-fx-font-size: 48px; -fx-text-fill: #64ffda;");

        Label titolo = new Label("Prenotazione confermata!");
        titolo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #64ffda;");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #1e3a5f;");

        Label codiceLabel = new Label("Codice biglietto");
        codiceLabel.setStyle("-fx-text-fill: #8892b0; -fx-font-size: 12px;");

        Label codice = new Label(b.getCodiceBiglietto());
        codice.setStyle(
                "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ccd6f6;" +
                        "-fx-font-family: 'Courier New', monospace;" +
                        "-fx-background-color: #0d1b2a; -fx-background-radius: 6;" +
                        "-fx-padding: 8 16 8 16;"
        );

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: #1e3a5f;");

        GridPane dettagli = new GridPane();
        dettagli.setHgap(16);
        dettagli.setVgap(8);
        dettagli.setAlignment(Pos.CENTER_LEFT);

        addRow(dettagli, 0, "Tratta",      b.getProvenienza() + "  →  " + b.getDestinazione());
        addRow(dettagli, 1, "Data",        b.getDataPartenza().toString());
        addRow(dettagli, 2, "Orario",      b.getOraPartenza());
        addRow(dettagli, 3, "Classe",      b.getClasse());
        addRow(dettagli, 4, "Passeggeri",  b.getNAdulti() + " adulti, " + b.getNBambini() + " bambini");
        if (b.getNBagagli() > 0)
            addRow(dettagli, 5, "Bagagli", b.getNBagagli() + "");
        addRow(dettagli, 6, "Totale",      "€ " + b.getPrezzoTotale());

        Label avviso = new Label("Conserva il codice biglietto per il check-in.");
        avviso.setStyle("-fx-text-fill: #8892b0; -fx-font-size: 12px;");
        avviso.setWrapText(true);

        Button chiudi = new Button("Chiudi");
        chiudi.setStyle(
                "-fx-background-color: #64ffda; -fx-text-fill: #0d1b2a;" +
                        "-fx-font-weight: bold; -fx-font-size: 14px;" +
                        "-fx-background-radius: 8; -fx-padding: 10 40 10 40; -fx-cursor: hand;"
        );
        chiudi.setOnAction(e -> dialog.close());

        card.getChildren().addAll(icon, titolo, sep, codiceLabel, codice,
                sep2, dettagli, avviso, chiudi);

        // ── Dimmed overlay ─────────────────────────────────────────────────────
        StackPane overlay = new StackPane(card);
        overlay.setStyle("-fx-background-color: rgba(13,27,42,0.88);");
        overlay.setPrefSize(680, 620);
        overlay.setAlignment(Pos.CENTER);

        Scene scene = new Scene(overlay);
        dialog.setScene(scene);
        dialog.setResizable(false);

        // Centre over parent window
        Stage parent = (Stage) tblVoli.getScene().getWindow();
        dialog.setX(parent.getX() + (parent.getWidth()  - 500) / 2);
        dialog.setY(parent.getY() + (parent.getHeight() - 560) / 2);

        dialog.showAndWait();
    }

    private void addRow(GridPane grid, int row, String label, String value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #8892b0; -fx-font-size: 13px; -fx-min-width: 100px;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: #ccd6f6; -fx-font-size: 13px; -fx-font-weight: bold;");
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }

    public boolean controllaTutto() {
        if (!isLogged) { showError("Effettua il login per prenotare."); return false; }
        if (!isSelected || trenoSelezionato == null) { showError("Seleziona un treno dalla lista."); return false; }
        if (nAdulti == 0 && nBambini == 0) { showError("Seleziona almeno un passeggero."); return false; }
        if (cbClasse.getValue() == null) { showError("Seleziona la classe."); return false; }
        if (trenoSelezionato.getPostiMassimi() <
                trenoSelezionato.getNumeroPostiOccupati() + nAdulti + nBambini) {
            showError("Posti insufficienti su questo treno."); return false;
        }
        return true;
    }

    private void showError(String msg) {
        segnala.setStyle("-fx-text-fill: #ff6b6b;");
        segnala.setText(msg);
        PauseTransition p = new PauseTransition(Duration.seconds(3));
        p.setOnFinished(e -> segnala.setText(""));
        p.play();
    }

    private void resetCampi() {
        nAdulti = 0; nBambini = 0; nBagagli = 0; prezzoTot = 0;
        lblAdulti.setText("0");
        lblBambini.setText("0");
        lblBagagli.setText("0");
        lblPrezzo.setText("0");
        isSelected = false;
        trenoSelezionato = null;
        tblVoli.getSelectionModel().clearSelection();
    }

    // ── Passenger counters ───────────────────────────────────────────────────

    @FXML public void aggiungiAdulto()  { nAdulti++;  lblAdulti.setText(str(nAdulti));   aggiornaPrezzo(); }
    @FXML public void rimuoviAdulto()   { if (nAdulti  > 0) { nAdulti--;  lblAdulti.setText(str(nAdulti));   aggiornaPrezzo(); } }
    @FXML public void aggiungiBambino() { nBambini++; lblBambini.setText(str(nBambini)); aggiornaPrezzo(); }
    @FXML public void rimuoviBambino()  { if (nBambini > 0) { nBambini--; lblBambini.setText(str(nBambini)); aggiornaPrezzo(); } }
    @FXML public void aggiungiBagaglio(){ nBagagli++; lblBagagli.setText(str(nBagagli)); aggiornaPrezzo(); }
    @FXML public void rimuoviBagaglio() { if (nBagagli > 0) { nBagagli--; lblBagagli.setText(str(nBagagli)); aggiornaPrezzo(); } }
    private String str(int n) { return String.valueOf(n); }

    private void aggiornaPrezzo() {
        if (cbClasse.getValue() == null) return;
        prezzoTot = cbClasse.getValue().equals("Prima classe")
                ? (nAdulti * INTERO_PRIMA) + (nBambini * RISTRETTO_PRIMA)
                : (nAdulti * INTERO_SECONDA) + (nBambini * RISTRETTO_SECONDA);
        prezzoTot += nBagagli * PREZZO_BAGAGLIO;
        lblPrezzo.setText(str(prezzoTot));
    }

    // ── Row selection ─────────────────────────────────────────────────────────

    private void handleRowSelected(Treno treno) {
        isSelected = true;
        trenoSelezionato = treno;
        if (infoStage != null && infoStage.isShowing()) infoStage.close();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../GUI/dettagliTreno.fxml"));
            Parent root = loader.load();
            DettagliTrenoController ctrl = loader.getController();
            ctrl.setTreno(treno);
            infoStage = new Stage();
            infoStage.setTitle("Dettagli treno");
            infoStage.setScene(new Scene(root));
            infoStage.setOnCloseRequest(e -> infoStage = null);
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            infoStage.setX(bounds.getMinX() + 20);
            infoStage.setY(bounds.getMinY() + 20);
            infoStage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @FXML private void handleBtnHome()   { navigate("../GUI/user.fxml", "FastRail Station"); }

    @FXML private void handleBtnAccedi() {
        gestioneUtenti.setSchermataPrecedente("PrenotaPage");
        navigate("../GUI/login.fxml", "Login");
    }

    public void setLogged(boolean logged) { this.isLogged = logged; }

    private void navigate(String path, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            if (btnHome.getScene() != null)
                ((Stage) btnHome.getScene().getWindow()).close();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
