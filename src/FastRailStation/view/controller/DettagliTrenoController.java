package FastRailStation.view.controller;

import FastRailStation.model.Treno;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DettagliTrenoController
{
    @FXML private Label nomeTreno;
    @FXML private Label lblModello;
    @FXML private Label lblProvenienza;
    @FXML private Label lblDestinazione;
    @FXML private Label lblCompagnia;
    @FXML private Label lblGiornoArrivo;
    @FXML private Label lblOrarioArrivo;
    @FXML private Label lblGiornoPartenza;
    @FXML private Label lblOrarioPartenza;
    @FXML private Label lblPostiTotali;
    @FXML private Label lblRitardo;
    @FXML private Label lblBinario;
    @FXML private Label lblTerminal;
    @FXML private Label lblPostiOccupati;
    @FXML private Label orologio;
    @FXML private AnchorPane anchImage;

    public void setTreno(Treno treno)
    {
        nomeTreno.setText(treno.getModello());
        lblModello.setText(treno.getModello());
        lblProvenienza.setText(treno.getProvenienzaString());
        lblPostiTotali.setText(String.valueOf(treno.getPostiMassimi()));
        lblGiornoPartenza.setText(treno.getGiornoPartenzaString());
        lblRitardo.setText(String.valueOf(treno.getRitardoInt()));
        lblCompagnia.setText(treno.getCompagnia());
        lblDestinazione.setText(treno.getDestinazioneString());
        lblBinario.setText(String.valueOf(treno.getBinario()));
        lblOrarioArrivo.setText(treno.getOraArrivoString());
        lblGiornoArrivo.setText(treno.getGiornoArrivoString());
        lblOrarioPartenza.setText(treno.getOraPartenzaString());
        lblPostiOccupati.setText(String.valueOf(treno.getNumeroPostiOccupatiInt()));
        lblTerminal.setText(String.valueOf(treno.getTerminalInt()));

        // Chiamo metodo che imposta l'immagine del treno corretto
        setImage(treno.getModello(), treno.getCompagnia());
    }

    @FXML
    private void initialize() {
        startClockUpdateAnimation();
    }

    private void startClockUpdateAnimation()
    {
        AnimationTimer timer = new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                orologio.setText(String.format("%02d:%02d:%02d",
                        java.time.LocalTime.now().getHour(),
                        java.time.LocalTime.now().getMinute(),
                        java.time.LocalTime.now().getSecond()));
            }
        };
        timer.start();
    }

    private void setImage(String modello, String compagnia)
    {
        String temp = "-fx-background-size: contain; -fx-background-repeat: no-repeat; -fx-background-position: center";
        switch (modello)
        {
            case "Frecciarossa 1000":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/Frecciarossa1000.jpeg'); " + temp);
                break;
            case "Frecciarossa 700":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/Frecciarossa700.jpg'); " + temp);
                break;
            case "Frecciarossa 500":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/Frecciarossa500.jpg'); " + temp);
                break;
            case "Frecciargento":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/Frecciargento.jpg'); " + temp);
                break;
            case "Frecciabianca":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/Frecciabianca.jpg'); " + temp);
                break;
            case "Intercity":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/Intercity.jpg'); " + temp);
                break;
            case "Intercity Notte":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/IntercityNotte.jpg'); " + temp);
                break;
            case "Regionale":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/Regionale.jpg'); " + temp);
                break;
            case "Italo Next":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/ItaloEVO.jpg'); " + temp);
                break;
            case "Italo AGV":
                anchImage.setStyle("-fx-background-image: url('FastRailStation/immagini/ItaloAGV.jpg'); " + temp);
                break;
            default:
                break;
        }
    }
}