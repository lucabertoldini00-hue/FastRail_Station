package FastRailStation.view.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import FastRailStation.model.GestioneTreni;
import FastRailStation.model.Treno;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DettagliTrenoAdminController
{
    @FXML private ComboBox<String> modelloTrenoCmbx;
    @FXML private TextField provenienzaTxF;
    @FXML private TextField destinazioneTxF;
    @FXML private TextField binarioTxF;
    @FXML private TextField compagniaTxF;
    @FXML private TextField postiTotaliTxF;
    @FXML private TextField postiOccupatiTxF;
    @FXML private ComboBox<String> statoCmbx;
    @FXML private DatePicker giornoArrivoDp;
    @FXML private DatePicker giornoPartenzaDp;
    @FXML private TextField orarioArrivoTxF;
    @FXML private TextField orarioPartenzaTxF;
    @FXML private TextField ritardoTxF;
    @FXML private TextField codiceTxF;
    @FXML private TextField intervalloTxF;

    @FXML private DatePicker inizioLavoriDp;
    @FXML private DatePicker fineLavoriDp;
    @FXML private TextField hangarTxf;

    @FXML private TextField nomeTreno;
    @FXML private Button salvaBtn;
    @FXML private Button modificaBtn;

    @FXML private Label orologio;

}