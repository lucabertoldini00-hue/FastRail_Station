package FastRailStation.view.controller;

import FastRailStation.model.GestioneTreni;
import FastRailStation.model.Treno;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DettagliTrenoAdminController {

    // FIX B12: field names match the corrected fx:ids in dettagliTrenoAdmin.fxml
    @FXML private ComboBox<String> modelloTrenoCmbx;
    @FXML private TextField        provenienzaTxF;
    @FXML private TextField        destinazioneTxF;
    @FXML private TextField        binarioTxF;
    @FXML private TextField        compagniaTxF;
    @FXML private TextField        postiTotaliTxF;
    @FXML private TextField        postiOccupatiTxF;
    @FXML private ComboBox<String> statoCmbx;
    @FXML private DatePicker       giornoArrivoDp;
    @FXML private DatePicker       giornoPartenzaDp;
    @FXML private TextField        orarioArrivoTxF;
    @FXML private TextField        orarioPartenzaTxF;
    @FXML private TextField        ritardoTxF;
    @FXML private TextField        codiceTxF;
    @FXML private TextField        intervalloTxF;
    @FXML private DatePicker       inizioLavoriDp;
    @FXML private DatePicker       fineLavoriDp;
    @FXML private TextField        hangarTxf;
    @FXML private ComboBox<String> depositoCmbx;
    @FXML private TextField        nomeTreno;
    @FXML private Button           salvaBtn;
    @FXML private Button           modificaBtn;
    @FXML private Label            orologio;

    private Treno trenoCorrente;
    private final GestioneTreni gestioneTreni = GestioneTreni.getInstance();

    @FXML
    private void initialize() {
        statoCmbx.getItems().addAll(
                "In arrivo", "In partenza", "In attesa", "In manutenzione", "In corsa");
        modelloTrenoCmbx.getItems().addAll(
                "Frecciarossa 1000", "Frecciarossa 700", "Frecciarossa 500",
                "Frecciargento", "Frecciabianca", "Intercity", "Intercity Notte",
                "Regionale", "Regionale Veloce", "Italo Next", "Italo AGV", "EuroCity ETR610",
                "EuroNight");
        startClock();
        setEditable(false);
    }

    public void setTreno(Treno treno) {
        this.trenoCorrente = treno;
        modelloTrenoCmbx.setValue(treno.getModello());
        nomeTreno.setText(treno.getModello());
        provenienzaTxF.setText(treno.getProvenienza());
        destinazioneTxF.setText(treno.getDestinazione());
        binarioTxF.setText(String.valueOf(treno.getBinario()));
        compagniaTxF.setText(treno.getCompagnia());
        postiTotaliTxF.setText(String.valueOf(treno.getPostiMassimi()));
        postiOccupatiTxF.setText(String.valueOf(treno.getNumeroPostiOccupati()));
        statoCmbx.setValue(treno.getStato());
        giornoArrivoDp.setValue(treno.getGiornoArrivo());
        giornoPartenzaDp.setValue(treno.getGiornoPartenza());
        orarioArrivoTxF.setText(treno.getOraArrivoString());
        orarioPartenzaTxF.setText(treno.getOraPartenzaString());
        ritardoTxF.setText(String.valueOf(treno.getRitardo()));
        codiceTxF.setText(treno.getCodice());
        intervalloTxF.setText(String.valueOf(treno.getIntervallo()));
        if (treno.getInizioManutenzione() != null) inizioLavoriDp.setValue(treno.getInizioManutenzione());
        if (treno.getFineManutenzione()   != null) fineLavoriDp.setValue(treno.getFineManutenzione());
        if (treno.getDeposito()           != null) hangarTxf.setText(treno.getDeposito());
    }

    @FXML
    private void modifica() { setEditable(true); }

    @FXML
    private void salva() {
        if (trenoCorrente == null) return;
        trenoCorrente.setProvenienza(provenienzaTxF.getText());
        trenoCorrente.setDestinazione(destinazioneTxF.getText());
        if (statoCmbx.getValue() != null) trenoCorrente.setStato(statoCmbx.getValue());
        tryParse(ritardoTxF.getText(),      v -> trenoCorrente.setRitardo(v));
        tryParse(postiTotaliTxF.getText(),  v -> trenoCorrente.setPostiMassimi(v));
        tryParse(postiOccupatiTxF.getText(),v -> trenoCorrente.setNumeroPostiOccupati(v));
        tryParse(intervalloTxF.getText(),   v -> trenoCorrente.setIntervallo(v));
        if (giornoArrivoDp.getValue()   != null) trenoCorrente.getGiornoArrivoProperty().set(giornoArrivoDp.getValue());
        if (giornoPartenzaDp.getValue() != null) trenoCorrente.giornoPartenzaProperty().set(giornoPartenzaDp.getValue());
        if (inizioLavoriDp.getValue()   != null) trenoCorrente.inizioManutenzioneProperty().set(inizioLavoriDp.getValue());
        if (fineLavoriDp.getValue()     != null) trenoCorrente.fineManutenzioneProperty().set(fineLavoriDp.getValue());
        trenoCorrente.setDeposito(hangarTxf.getText());
        gestioneTreni.aggiornaLista();
        setEditable(false);
    }

    @FXML
    private void rimuoviTreno() {
        if (trenoCorrente == null) return;
        gestioneTreni.rimuoviTreno(trenoCorrente);
        gestioneTreni.aggiornaLista();
        trenoCorrente = null;
    }

    private void setEditable(boolean on) {
        provenienzaTxF.setEditable(on);
        destinazioneTxF.setEditable(on);
        compagniaTxF.setEditable(on);
        postiTotaliTxF.setEditable(on);
        postiOccupatiTxF.setEditable(on);
        ritardoTxF.setEditable(on);
        codiceTxF.setEditable(on);
        intervalloTxF.setEditable(on);
        hangarTxf.setEditable(on);
        statoCmbx.setDisable(!on);
        modelloTrenoCmbx.setDisable(!on);
        giornoArrivoDp.setDisable(!on);
        giornoPartenzaDp.setDisable(!on);
        inizioLavoriDp.setDisable(!on);
        fineLavoriDp.setDisable(!on);
        salvaBtn.setDisable(!on);
    }

    private void startClock() {
        AnimationTimer timer = new AnimationTimer() {
            @Override public void handle(long now) {
                java.time.LocalTime t = java.time.LocalTime.now();
                orologio.setText(String.format("%02d:%02d:%02d",
                        t.getHour(), t.getMinute(), t.getSecond()));
            }
        };
        timer.start();
    }

    @FunctionalInterface interface IntConsumer { void accept(int v); }
    private void tryParse(String s, IntConsumer setter) {
        try { setter.accept(Integer.parseInt(s.trim())); } catch (NumberFormatException ignored) {}
    }
}
