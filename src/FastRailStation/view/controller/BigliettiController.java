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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
        setupRowClickHandler();
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

    private void setupRowClickHandler() {
        tblBiglietti.setRowFactory(tv -> {
            TableRow<Biglietto> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getClickCount() == 1) {
                    mostraRiepilogo(row.getItem());
                    tblBiglietti.getSelectionModel().clearSelection();
                }
            });
            return row;
        });
    }

    private void mostraRiepilogo(Biglietto b) {
        Stage dialog = new Stage(StageStyle.UNDECORATED);
        dialog.initOwner(tblBiglietti.getScene().getWindow());

        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36, 48, 36, 48));
        card.setStyle(
                "-fx-background-color: #67696f;" +
                        "-fx-border-color: #800303;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 14;" +
                        "-fx-background-radius: 14;" +
                        "-fx-effect: dropshadow(gaussian, rgba(113,99,99,0.6), 24, 0, 0, 0);"
        );
        card.setMaxWidth(460);

        Label icon = new Label("✓");
        icon.setStyle("-fx-font-size: 48px; -fx-text-fill: #4cff72;");

        Label titolo = new Label("Riepilogo prenotazione");
        titolo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #800303;");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #1e3a5f;");

        Label codiceLabel = new Label("Codice biglietto");
        codiceLabel.setStyle("-fx-text-fill: #9a9aa3; -fx-font-size: 12px;");

        Label codice = new Label(b.getCodiceBiglietto());
        codice.setStyle(
                "-fx-font-size: 15px; -fx-font-weight: bold;" +
                        "-fx-text-fill: #f4e7e7;" +
                        "-fx-font-family: 'Courier New', monospace;" +
                        "-fx-background-color: #0d1b2a;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 8 16 8 16;"
        );

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: #1e3a5f;");

        GridPane dettagli = new GridPane();
        dettagli.setHgap(16);
        dettagli.setVgap(8);
        dettagli.setAlignment(Pos.CENTER_LEFT);

        addRow(dettagli, 0, "Tratta",     b.getProvenienza() + "  →  " + b.getDestinazione());
        addRow(dettagli, 1, "Data",       b.getDataPartenza().toString());
        addRow(dettagli, 2, "Orario",     b.getOraPartenza());
        addRow(dettagli, 3, "Classe",     b.getClasse());
        addRow(dettagli, 4, "Passeggeri", b.getNAdulti() + " adulti, " + b.getNBambini() + " bambini");
        if (b.getNBagagli() > 0)
            addRow(dettagli, 5, "Bagagli", String.valueOf(b.getNBagagli()));
        addRow(dettagli, 6, "Totale",     "€ " + b.getPrezzoTotale());

        Label avviso = new Label("Conserva il codice biglietto per il check-in.");
        avviso.setStyle("-fx-text-fill: #9a9aa3; -fx-font-size: 12px;");
        avviso.setWrapText(true);

        Button chiudi = new Button("Chiudi");
        chiudi.setStyle(
                "-fx-background-color: #800303;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-weight: bold; -fx-font-size: 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 40 10 40; -fx-cursor: hand;"
        );
        chiudi.setOnAction(e -> dialog.close());
        chiudi.setOnMouseEntered(e -> chiudi.setStyle(
                "-fx-background-color: #ac0909;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-weight: bold; -fx-font-size: 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 40 10 40; -fx-cursor: hand;"
        ));
        chiudi.setOnMouseExited(e -> chiudi.setStyle(
                "-fx-background-color: #800303;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-weight: bold; -fx-font-size: 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 40 10 40; -fx-cursor: hand;"
        ));

        card.getChildren().addAll(icon, titolo, sep, codiceLabel, codice,
                sep2, dettagli, avviso, chiudi);

        StackPane overlay = new StackPane(card);
        overlay.setStyle("-fx-background-color: rgba(13,27,42,0.88);");
        overlay.setPrefSize(680, 620);
        overlay.setAlignment(Pos.CENTER);

        Scene scene = new Scene(overlay);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.setOnHidden(e -> tblBiglietti.getSelectionModel().clearSelection());

        Stage parent = (Stage) tblBiglietti.getScene().getWindow();
        dialog.setX(parent.getX() + (parent.getWidth()  - 500) / 2);
        dialog.setY(parent.getY() + (parent.getHeight() - 560) / 2);

        dialog.showAndWait();
    }

    private void addRow(GridPane grid, int row, String label, String value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #9a9aa3; -fx-font-size: 13px; -fx-min-width: 100px;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: #f4e7e7; -fx-font-size: 13px; -fx-font-weight: bold;");
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }
}

