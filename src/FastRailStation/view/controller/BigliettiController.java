package FastRailStation.view.controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import FastRailStation.model.Biglietto;
import FastRailStation.model.GestioneUtenti;
import FastRailStation.model.Utente;
import FastRailStation.salvataggioDati.LeggiDati;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class BigliettiController {

    @FXML private Label   lblOrologio;
    @FXML private Label   lblNome;
    @FXML private Label   lblTotaleViaggi;
    @FXML private Label   lblTotaleSpesa;
    @FXML private Label   lblContatore;
    @FXML private Label   lblNessunBiglietto;
    @FXML private Button  btnHome;

    @FXML private TableView<Biglietto>             tblBiglietti;
    @FXML private TableColumn<Biglietto, String>   colCodice;
    @FXML private TableColumn<Biglietto, String>   colTratta;
    @FXML private TableColumn<Biglietto, String>   colData;
    @FXML private TableColumn<Biglietto, String>   colOrario;
    @FXML private TableColumn<Biglietto, String>   colClasse;
    @FXML private TableColumn<Biglietto, Integer>  colPasseggeri;
    @FXML private TableColumn<Biglietto, Integer>  colBagagli;
    @FXML private TableColumn<Biglietto, Integer>  colPrezzo;
    @FXML private TableColumn<Biglietto, String>   colPrenotato;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DT_FMT   = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final GestioneUtenti gu    = GestioneUtenti.getInstance();
    private final LeggiDati      leggi = new LeggiDati();

    @FXML
    private void initialize() {
        setupColonne();
        caricaBiglietti();
        startClock();
    }

    // ── Configurazione colonne ────────────────────────────────────────────────

    private void setupColonne() {
        colCodice.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getCodiceBiglietto()));

        colTratta.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getProvenienza() + "  →  " + cd.getValue().getDestinazione()));

        colData.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getDataPartenza() != null
                                ? cd.getValue().getDataPartenza().format(DATE_FMT) : "-"));

        colOrario.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getOraPartenza()));

        colClasse.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getClasse()));

        colPasseggeri.setCellValueFactory(cd ->
                new SimpleIntegerProperty(
                        cd.getValue().getNAdulti() + cd.getValue().getNBambini()).asObject());

        colBagagli.setCellValueFactory(cd ->
                new SimpleIntegerProperty(cd.getValue().getNBagagli()).asObject());

        colPrezzo.setCellValueFactory(cd ->
                new SimpleIntegerProperty(cd.getValue().getPrezzoTotale()).asObject());

        colPrenotato.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getDataPrenotazione() != null
                                ? cd.getValue().getDataPrenotazione().format(DT_FMT) : "-"));

        // Colore classe
        colClasse.setCellFactory(col -> new TableCell<Biglietto, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Prima classe".equals(item)
                        ? "-fx-text-fill: #ffdd57; -fx-font-weight: bold;"
                        : "-fx-text-fill: #f4e7e7;");
            }
        });

        // Prezzo con simbolo €
        colPrezzo.setCellFactory(col -> new TableCell<Biglietto, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText("€ " + item);
                setStyle("-fx-text-fill: #4cff72; -fx-font-weight: bold;");
            }
        });

        // Passeggeri con descrizione
        colPasseggeri.setCellFactory(col -> new TableCell<Biglietto, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                Biglietto b = getTableView().getItems().get(getIndex());
                String label = b.getNAdulti() + "A";
                if (b.getNBambini() > 0) label += " + " + b.getNBambini() + "B";
                setText(label);
            }
        });

        // Bagagli: mostra "—" se zero
        colBagagli.setCellFactory(col -> new TableCell<Biglietto, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item == 0 ? "—" : String.valueOf(item));
                setStyle(item == 0 ? "-fx-text-fill: #4a5568;" : "-fx-text-fill: #f4e7e7;");
            }
        });
    }

    // ── Caricamento biglietti ─────────────────────────────────────────────────

    private void caricaBiglietti() {
        int idx = gu.getIndice();
        if (idx < 0 || idx >= gu.getUtenti().size()) return;

        Utente u = gu.getUtenti().get(idx);
        lblNome.setText("👤  " + u.getNome() + " " + u.getCognome());

        ArrayList<Biglietto> biglietti = leggi.leggiBigliettiUtente(u.getMail());
        tblBiglietti.getItems().setAll(biglietti);

        int n        = biglietti.size();
        int totSpesa = biglietti.stream().mapToInt(Biglietto::getPrezzoTotale).sum();

        lblTotaleViaggi.setText("🎟  " + n + (n == 1 ? " biglietto" : " biglietti"));
        lblTotaleSpesa.setText("💶  € " + totSpesa + " spesi in totale");
        lblContatore.setText(n + (n == 1 ? " biglietto" : " biglietti"));

        // Messaggio se lista vuota
        if (lblNessunBiglietto != null) {
            boolean vuota = n == 0;
            lblNessunBiglietto.setVisible(vuota);
            lblNessunBiglietto.setManaged(vuota);
        }
    }

    // ── Navigazione ───────────────────────────────────────────────────────────

    @FXML
    private void handleBtnHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../GUI/user.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("FastRail Station");
            stage.setScene(new Scene(root));
            stage.show();
            chiudiStageCorrente();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chiudiStageCorrente() {
        if (btnHome != null && btnHome.getScene() != null)
            ((Stage) btnHome.getScene().getWindow()).close();
    }

    // ── Orologio ──────────────────────────────────────────────────────────────

    private void startClock() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                LocalTime t = LocalTime.now();
                lblOrologio.setText(String.format("%02d:%02d:%02d",
                        t.getHour(), t.getMinute(), t.getSecond()));
            }
        }.start();
    }
}