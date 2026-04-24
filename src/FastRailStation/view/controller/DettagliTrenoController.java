package FastRailStation.view.controller;

import FastRailStation.model.Treno;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class DettagliTrenoController {

    @FXML private Label      nomeTreno;
    @FXML private Label      lblModello;
    @FXML private Label      lblProvenienza;
    @FXML private Label      lblDestinazione;
    @FXML private Label      lblCompagnia;
    @FXML private Label      lblGiornoArrivo;
    @FXML private Label      lblOrarioArrivo;
    @FXML private Label      lblGiornoPartenza;
    @FXML private Label      lblOrarioPartenza;
    @FXML private Label      lblPostiTotali;
    @FXML private Label      lblRitardo;
    @FXML private Label      lblBinario;
    @FXML private Label      lblTerminal;
    @FXML private Label      lblPostiOccupati;
    @FXML private Label      orologio;
    @FXML private AnchorPane anchImage;

    public void setTreno(Treno treno) {
        nomeTreno.setText(treno.getModello());
        lblModello.setText(treno.getModello());

        // FIX B10: getProvenienzaString() → getProvenienza()
        lblProvenienza.setText(treno.getProvenienza());

        lblPostiTotali.setText(String.valueOf(treno.getPostiMassimi()));
        lblGiornoPartenza.setText(treno.getGiornoPartenzaString());

        // FIX B10: getRitardoInt() → getRitardo()
        lblRitardo.setText(treno.getRitardo() + " min");

        lblCompagnia.setText(treno.getCompagnia());

        // FIX B10: getDestinazioneString() → getDestinazione()
        lblDestinazione.setText(treno.getDestinazione());

        lblBinario.setText(String.valueOf(treno.getBinario()));
        lblOrarioArrivo.setText(treno.getOraArrivoString());
        lblGiornoArrivo.setText(treno.getGiornoArrivoString());
        lblOrarioPartenza.setText(treno.getOraPartenzaString());

        // FIX B10: getNumeroPostiOccupatiInt() → getNumeroPostiOccupati()
        lblPostiOccupati.setText(String.valueOf(treno.getNumeroPostiOccupati()));

        // FIX B10: getTerminalInt() doesn't exist — trains don't have terminals;
        //          mapped to binario which is the train equivalent
        if (lblTerminal != null)
            lblTerminal.setText(String.valueOf(treno.getBinario()));

        setImage(treno.getModello());
    }

    @FXML
    private void initialize() { startClock(); }

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

    private void setImage(String modello) {
        if (anchImage == null) return;
        String base = "-fx-background-size: contain; " +
                "-fx-background-repeat: no-repeat; " +
                "-fx-background-position: center; ";
        switch (modello) {
            case "Frecciarossa 1000":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/Frecciarossa1000.jpeg'); " + base); break;
            case "Frecciarossa 700":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/Frecciarossa700.jpg'); "   + base); break;
            case "Frecciarossa 500":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/Frecciarossa500.jpg'); "   + base); break;
            case "Frecciargento":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/Frecciargento.jpg'); "     + base); break;
            case "Frecciabianca":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/Frecciabianca.jpg'); "     + base); break;
            case "Intercity":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/Intercity.jpg'); "         + base); break;
            case "Intercity Notte":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/IntercityNotte.jpg'); "    + base); break;
            case "Regionale":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/Regionale.jpg'); "         + base); break;
            case "Regionale Veloce":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/RegionaleVeloce.jpg'); "   + base); break;
            case "Italo Next": case "Italo EVO":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/ItaloEVO.jpg'); "          + base); break;
            case "Italo AGV":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/ItaloAGV.jpg'); "          + base); break;
            default: break;
        }
    }
}