package FastRailStation.view.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import FastRailStation.model.Biglietto;
import FastRailStation.model.GestioneUtenti;
import FastRailStation.model.Utente;
import FastRailStation.salvataggioDati.LeggiDati;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ProfiloController {

    @FXML private Label navHome, navArrivi, navPartenze, navPrenota, navProfilo;

    // Dati personali
    @FXML private TextField tfNome, tfCognome, tfMail, tfNascita;
    @FXML private TextField tfCellulare, tfNazione, tfCitta, tfIndirizzo;
    @FXML private TextField tfCarta, tfScadenza;
    @FXML private Label     lblRuolo;

    // Password
    @FXML private PasswordField pfNuovaPassword, pfConfermaPassword;
    @FXML private Label         lblFeedback;

    // Bottoni
    @FXML private Button btnModifica, btnSalva;

    // Storico
    @FXML private TableView<Biglietto>            tblStorico;
    @FXML private TableColumn<Biglietto,String>   colCodice, colTratta, colData, colClasse, colPrenotato;
    @FXML private TableColumn<Biglietto,Integer>  colPasseggeri, colPrezzo;
    @FXML private Label lblTotaleViaggi, lblTotaleSpesa;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DT_FMT   = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final GestioneUtenti gu    = GestioneUtenti.getInstance();
    private final LeggiDati      leggi = new LeggiDati();

    @FXML private void initialize() {
        caricaDatiUtente(); setupStorico(); caricaStorico(); setupNav();
    }

    private void caricaDatiUtente() {
        int idx = gu.getIndice();
        if (idx < 0 || idx >= gu.getUtenti().size()) return;
        Utente u = gu.getUtenti().get(idx);
        tfNome.setText(u.getNome()); tfCognome.setText(u.getCognome());
        tfMail.setText(u.getMail()); tfNascita.setText(u.getNascita());
        tfCellulare.setText(u.getNumeroCellulare()); tfNazione.setText(u.getNazioneResideza());
        tfCitta.setText(u.getCittaResidenza()); tfIndirizzo.setText(u.getViaResidenza());
        tfCarta.setText(maskCard(u.getCodiceCarta())); tfScadenza.setText(u.getScadenza());
        if (lblRuolo != null) {
            lblRuolo.setText(u.isAdmin() ? "⚙  Amministratore" : "👤  Utente");
            lblRuolo.setStyle(u.isAdmin()
                    ? "-fx-text-fill: #ffdd57; -fx-font-weight: bold;"
                    : "-fx-text-fill: #9a9aa3;");
        }
        if (navProfilo != null) navProfilo.setText("👤 " + u.getNome());
    }

    private String maskCard(String carta) {
        if (carta == null || carta.length() < 4) return carta;
        return "**** **** **** " + carta.substring(carta.length() - 4);
    }

    @FXML private void handleModifica() {
        setEditable(true);
        int idx = gu.getIndice();
        if (idx >= 0 && idx < gu.getUtenti().size())
            tfCarta.setText(gu.getUtenti().get(idx).getCodiceCarta());
    }

    @FXML private void handleSalva() {
        int idx = gu.getIndice();
        if (idx < 0 || idx >= gu.getUtenti().size()) return;
        Utente u = gu.getUtenti().get(idx);
        if (tfNome.getText().trim().isEmpty() || tfCognome.getText().trim().isEmpty()
                || tfMail.getText().trim().isEmpty()) {
            feedback("Nome, cognome e mail sono obbligatori.", false); return;
        }
        String nuovaMail = tfMail.getText().trim();
        for (int i = 0; i < gu.getUtenti().size(); i++) {
            if (i != idx && gu.getUtenti().get(i).getMail().equalsIgnoreCase(nuovaMail)) {
                feedback("Email gia in uso da un altro account.", false); return;
            }
        }
        u.setMail(nuovaMail);
        u.nome = tfNome.getText().trim(); u.cognome = tfCognome.getText().trim();
        u.nascita = tfNascita.getText().trim(); u.numeroCellulare = tfCellulare.getText().trim();
        u.nazioneResideza = tfNazione.getText().trim(); u.cittaResidenza = tfCitta.getText().trim();
        u.viaResidenza = tfIndirizzo.getText().trim(); u.codiceCarta = tfCarta.getText().trim();
        u.scadenza = tfScadenza.getText().trim();
        gu.scriviUtenti(); setEditable(false);
        tfCarta.setText(maskCard(u.getCodiceCarta()));
        feedback("Profilo aggiornato con successo!", true);
    }

    @FXML private void handleCambioPassword() {
        String nuova = pfNuovaPassword.getText(), conferma = pfConfermaPassword.getText();
        if (nuova.isEmpty())         { feedback("Inserisci la nuova password.", false); return; }
        if (!nuova.equals(conferma)) { feedback("Le password non corrispondono.", false); return; }
        if (nuova.length() < 6)      { feedback("Minimo 6 caratteri.", false); return; }
        int idx = gu.getIndice();
        if (idx < 0 || idx >= gu.getUtenti().size()) return;
        gu.getUtenti().get(idx).setPassword(nuova);
        gu.scriviUtenti();
        pfNuovaPassword.clear(); pfConfermaPassword.clear();
        feedback("Password aggiornata!", true);
    }

    @FXML private void handleLogout() { gu.logout(); navigateTo("../GUI/user.fxml", "FastRail Station"); }

    // ── Storico ──────────────────────────────────────────────────────────────

    private void setupStorico() {
        if (tblStorico == null) return;
        colCodice.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCodiceBiglietto()));
        colTratta.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getProvenienza() + " -> " + cd.getValue().getDestinazione()));
        colData.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getDataPartenza() != null
                        ? cd.getValue().getDataPartenza().format(DATE_FMT) : "-"));
        colClasse.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getClasse()));
        colPasseggeri.setCellValueFactory(cd -> new SimpleIntegerProperty(
                cd.getValue().getNAdulti() + cd.getValue().getNBambini()).asObject());
        colPrezzo.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getPrezzoTotale()).asObject());
        colPrenotato.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getDataPrenotazione() != null
                        ? cd.getValue().getDataPrenotazione().format(DT_FMT) : "-"));

        colPrezzo.setCellFactory(col -> new TableCell<Biglietto, Integer>() {
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "EUR " + item);
            }
        });
        colClasse.setCellFactory(col -> new TableCell<Biglietto, String>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Prima classe".equals(item)
                        ? "-fx-text-fill: #ffdd57; -fx-font-weight: bold;"
                        : "-fx-text-fill: #f4e7e7;");
            }
        });
    }

    private void caricaStorico() {
        if (tblStorico == null) return;
        int idx = gu.getIndice();
        if (idx < 0 || idx >= gu.getUtenti().size()) return;
        String mail = gu.getUtenti().get(idx).getMail();
        ArrayList<Biglietto> biglietti = leggi.leggiBigliettiUtente(mail);
        tblStorico.getItems().setAll(biglietti);
        int totSpesa = biglietti.stream().mapToInt(Biglietto::getPrezzoTotale).sum();
        if (lblTotaleViaggi != null)
            lblTotaleViaggi.setText(biglietti.size() + (biglietti.size() == 1 ? " viaggio" : " viaggi"));
        if (lblTotaleSpesa != null)
            lblTotaleSpesa.setText("EUR " + totSpesa + " spesi in totale");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void setEditable(boolean on) {
        for (TextField tf : new TextField[]{tfNome,tfCognome,tfMail,tfNascita,
                tfCellulare,tfNazione,tfCitta,tfIndirizzo,tfCarta,tfScadenza})
            tf.setEditable(on);
        btnSalva.setDisable(!on); btnModifica.setDisable(on);
    }

    private void feedback(String msg, boolean ok) {
        if (lblFeedback == null) return;
        lblFeedback.setText(msg);
        lblFeedback.setStyle(ok ? "-fx-text-fill: #4cff72;" : "-fx-text-fill: #ff6b6b;");
    }

    private void setupNav() {
        wire(navHome,     () -> navigateTo("../GUI/user.fxml",         "FastRail Station"));
        wire(navArrivi,   () -> navigateTo("../GUI/userMain.fxml",     "Arrivi"));
        wire(navPartenze, () -> navigateTo("../GUI/userMain.fxml",     "Partenze"));
        wire(navPrenota,  () -> navigateTo("../GUI/prenotazione.fxml", "Prenotazione"));
        wire(navProfilo,  () -> {});
    }

    private void wire(Label l, Runnable r) { if (l != null) l.setOnMouseClicked(e -> r.run()); }

    private void navigateTo(String path, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            Stage stage = new Stage(); stage.setTitle(title);
            stage.setScene(new Scene(root)); stage.show();
            Stage cur = navHome != null && navHome.getScene() != null
                    ? (Stage) navHome.getScene().getWindow() : null;
            if (cur == null && navProfilo != null && navProfilo.getScene() != null)
                cur = (Stage) navProfilo.getScene().getWindow();
            if (cur != null) cur.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
}