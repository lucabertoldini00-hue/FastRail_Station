# 🚄 FastRail Station

Sistema di gestione stazione ferroviaria sviluppato in **JavaFX 8**.  
Permette agli utenti di consultare arrivi e partenze, prenotare biglietti e gestire il proprio profilo. Gli amministratori dispongono di un pannello dedicato per la gestione completa dei treni.

---

## 📁 Struttura del progetto

```
src/
└── FastRailStation/
    ├── Main.java
    ├── model/
    │   ├── Biglietto.java
    │   ├── GestioneTreni.java
    │   ├── GestioneUtenti.java
    │   ├── Treno.java
    │   └── Utente.java
    ├── salvataggioDati/
    │   ├── LeggiDati.java
    │   ├── ScriviDati.java
    │   ├── treni.csv
    │   ├── utenti.txt
    │   └── biglietti.csv
    └── view/
        ├── controller/
        │   ├── AdminController.java
        │   ├── DettagliTrenoAdminController.java
        │   ├── DettagliTrenoController.java
        │   ├── LoginController.java
        │   ├── PrenotazioneController.java
        │   ├── ProfiloController.java
        │   ├── PwChangeController.java
        │   ├── SignInController.java
        │   ├── UserController.java
        │   └── UserMainController.java
        ├── GUI/
        │   ├── admin.fxml
        │   ├── dettagliTreno.fxml
        │   ├── dettagliTrenoAdmin.fxml
        │   ├── login.fxml
        │   ├── prenotazione.fxml
        │   ├── profilo.fxml
        │   ├── pwChange.fxml
        │   ├── signin.fxml
        │   ├── styles.css
        │   ├── user.fxml
        │   └── userMain.fxml
        └── immagini/
            ├── Logo.png
            ├── Sfondo1.jpg
            └── [immagini treni...]
```

---

## ▶️ Requisiti

| Requisito | Versione |
|-----------|----------|
| Java JDK  | 8 (con JavaFX bundled) |
| IDE consigliato | IntelliJ IDEA / Eclipse |

> ⚠️ JavaFX è incluso nel JDK 8. Con JDK 11+ è necessario aggiungere la libreria JavaFX separatamente.

---

## 📝 Salvataggio Dati

Il file `treni.csv` e `utenti.txt` nella cartella `salvataggioDati/` vengono letti all'avvio e riscritti ad ogni modifica. Non eliminarli.

---

## 👤 Credenziali di accesso

### Utente normale
Registra un nuovo account dalla schermata **Registrati**, oppure usa uno degli account di test presenti in `utenti.txt`:

| Email | Password |
|-------|----------|
| `berts@gmail.com` | `password` |
| `gianna@gmail.com` | `password` |
| `chiara@gmail.com` | `password` |

### Amministratore
| Email | Password |
|-------|----------|
| `admin` | `admin` |

---

## 🗺️ Schermate

### 🏠 Home (`user.fxml`)
- Barra di navigazione con link a tutte le sezioni
- Campo di ricerca rapida per destinazione e data
- Tre card di accesso rapido: Arrivi, Partenze, Prenota
- Il nome dell'utente appare nella nav se loggato

### 📋 Tabellone (`userMain.fxml`)
- Toggle **Arrivi / Partenze** nella sidebar
- Filtro per data, testo libero (destinazione o provenienza) e compagnia
- Colonne colorate per stato (verde = in orario, giallo = in arrivo, rosso = ritardo)
- Click su una riga apre la finestra **Dettagli treno**

### 🎫 Prenotazione (`prenotazione.fxml`)
- Ricerca treni per data e destinazione
- Selezione passeggeri (adulti / bambini), bagagli e classe
- Calcolo prezzo in tempo reale
- Conferma con modale che mostra il codice biglietto
- Il biglietto viene salvato in `biglietti.csv`

### 👤 Profilo (`profilo.fxml`)
- Visualizzazione di tutti i dati personali
- Modalità **Modifica** per aggiornare i dati
- Cambio password inline con validazione
- Numero carta mascherato in lettura (`**** **** **** 1234`)
- Pulsante Logout

### 🔐 Login (`login.fxml`)
- Validazione per campo con evidenziazione rossa
- Toggle mostra/nascondi password
- Link a Registrazione e Reset password
- Credenziali `admin / admin` reindirizzano al pannello Admin

### 📝 Registrazione (`signin.fxml`)
- Tutti i campi obbligatori con feedback visivo
- Controllo password corrispondenti
- Controllo email già esistente
- Accettazione normativa Privacy obbligatoria

### ⚙️ Admin Panel (`admin.fxml`)
- Tabella completa di tutti i treni con filtri per modalità (Tutti / Partenze / Arrivi / Manutenzione)
- Filtro per data, testo e compagnia
- **Aggiunta treno** dalla sidebar con validazione orari (arrivo ≥ 15 min prima della partenza)
- **Modifica** treno nella finestra dettagli dedicata
- **Rimozione** treno con conferma dialog
- Binario assegnato automaticamente e liberato alla rimozione

---

## 🗃️ Formato dati

### `treni.csv`
```
Modello, Provenienza, Destinazione, Compagnia, Codice, NumMax,
GiornoArrivo, OraArrivo, GiornoPartenza, OraPartenza, Intervallo,
Stato, PostiOccupati, Ritardo, InizioManutenzione, FineManutenzione, Deposito
```

### `utenti.txt`
```
Nome+Cognome+Mail+Nascita+Password+Cellulare+Nazione+Città+Via+Carta+Scadenza
```

### `biglietti.csv`
```
CodiceBiglietto, MailUtente, CodiceTreno, Provenienza, Destinazione,
DataPartenza, OraPartenza, NAdulti, NBambini, NBagagli, Classe, Prezzo, DataPrenotazione
```

---

## 💰 Tariffe biglietti

| Tipologia | Seconda classe | Prima classe |
|-----------|---------------|--------------|
| Adulto    | €70           | €160         |
| Bambino   | €50           | €80          |
| Bagaglio  | €25 (extra)   | €25 (extra)  |

---

## 🎨 Tema grafico

Il progetto usa un tema scuro definito in `styles.css`:

| Colore | Uso |
|--------|-----|
| `#0d1b2a` | Sfondo principale |
| `#800303` | Accento (rosso scuro) — bottoni, titoli |
| `#f4e7e7` | Testo primario |
| `#9a9aa3` | Testo secondario |
| `#4cff72` | Stato positivo (in orario, successo) |
| `#ff6b6b` | Stato errore / ritardo |

---

## 🐛 Bug noti e fix applicati

### Fase 1: Backend & Struttura
| ID | Descrizione | Fix |
|----|-------------|-----|
| B1 | Lista binari era `static` | Rimossa keyword `static` |
| B2 | Loop infinito in `assegnaBinario()` | Aggiunto limite 200 tentativi |
| B3 | Filtri condividevano variabili | Triplet dedicati per categoria |
| B5/B6 | Path file non corrispondenti | Costanti centralizzate in `ScriviDati` |
| B7/B8 | `scriviTreni` scriveva solo 11 colonne | Riscritta con 17 colonne |
| B10 | Metodi inesistenti in `DettagliTrenoController` | Corretti i getter |
| B11 | `main.fxml` aveva controller placeholder | Sostituito con `UserController` |
| B12 | Label aeroporto in FXML treni | Aggiornate tutte le label al dominio ferroviario |
| N1 | `schermataPrecedente` poteva essere null | Inizializzato a `"Home"` |
| N4 | `indice` non inizializzato | Inizializzato a `-1` |
| N11 | Loop date avanzava all'infinito | Riscritta logica avanzamento date |
| N12 | `listaUtenti` veniva sostituita | Aggiornata in-place |
| N13 | `Biglietto` era stub vuoto | Implementata classe completa con CSV |
| **BINARIO** | Tutti i treni caricati da CSV avevano binario `-1` | `addTreno(Treno)` assegna binario se ≤ 0 |

### Fase 2: UI/UX & Navigazione (Issue Resolution)
| ID | Descrizione | File/Fix |
|----|-------------|----------|
| UI1 | Tab "Biglietti" mancante/rotte non funzionanti | Aggiunta label `navBiglietti` in header + implementazione click handler in `UserMainController`, `ProfiloController`, `LoginController` per navigazione a `biglietti.fxml` |
| UI2 | Logo non cliccabile per tornare alla home | Implementato metodo `handleLogoClick()` in `UserController`, `UserMainController`, `ProfiloController` per navigare a `user.fxml` |
| UI3 | Card "Biglietti" blu in home | Allineato colore della card da blu a rosso (`#800303`) in `user.fxml` |
| UI4 | Sottolineatura rossa tab Arrivi/Partenze non si aggiorna | Implementato metodo `setActiveNav()` in `UserMainController` per aggiungere/rimuovere classe `.nav-item-active` al cambio tab |
| UI5 | Filtri troppo restrittivi con compagnia (admin) | Allineata logica filter admin a standard: `aggiornaPartenzaAdmin()` e `aggiornaArrivoAdmin()` ora filtrano per provenienza **O** destinazione, non in AND con compagnia |
| UI6 | Mancanza treni per giorni successivi | Implementato metodo `expandFutureTreni(base, 14)` in `GestioneTreni` che clona treni base per i prossimi 14 giorni con orario leggermente randomizzato (±10 minuti) |
| UI7 | Treni futuri con binario/stato/ritardo valorizzati | Metodo `cloneWithDates()` genera treni futuri con binario -1 (non assegnato), stato vuoto, ritardo = 0 |
| UI8 | Treni di oggi con orario passato ancora visibili | Aggiunta logica in `setDataPartenza()` e `setDataArrivo()` che filtra treni con ora già passata nella giornata odierna |
| UI9 | Pulsanti +/− in prenotazione con colore sbagliato | Impostato stile `.plusButton, .minusButton` in `styles.css` con `-fx-background-color: #67696f` e `-fx-text-fill: #800303` |
| UI10 | Pulsanti +/− e numero non allineati a destra | Aggiunto `HBox.margin` con `left="10.0"` su tre HBox in `prenotazione.fxml` per Adulti, Bambini, Bagagli |
| UI11 | Binario -1 visibile in prenotazione | Aggiunta cell factory in `colGate` di `PrenotazioneController` che mostra testo vuoto se binario ≤ 0 |
| UI12 | Colonna "N° Treno" non centrata/formattata | Aggiunta cell factory in `colNVolo` di `PrenotazioneController` con stile: `-fx-alignment: CENTER; -fx-text-fill: #800303; -fx-font-weight: bold` |
| UI13 | Contatore "x treni" piccolo e bianco | Aggiornato stile label `lblContatore` in `userMain.fxml` con rosso, centrato, font più grande |
| UI14 | Orari arrivo/partenza invertiti nei dettagli | Invertite assegnazioni di `lblOrarioArrivo` e `lblOrarioPartenza` in `DettagliTrenoController.setTreno()` |
| UI15 | Prenotazione: non chiaro come selezionare il treno | Aggiunta label `lblTrenoSelezionato` in `prenotazione.fxml` che mostra "Treno selezionato: [codice]"; pulsante Prenota disabilitato finché non si sceglie riga |
| UI16 | Click su biglietto acquistato causa IndexOutOfBoundsException | Aggiunti controlli di validità indice nella cell factory `colPasseggeri` di `BigliettiController` prima di accedere a `getTableView().getItems().get(idx)` |

---

## 📌 Note sviluppo

- Il Singleton `GestioneTreni` e `GestioneUtenti` garantisce un'unica istanza condivisa tra tutti i controller.
- Tutti i dati sono persistiti su file flat (CSV / TXT) — nessun database esterno richiesto.
- Le liste `ObservableList` sono collegate direttamente alle `TableView` per aggiornamento reattivo.
- `synchronized` usato sulle sezioni critiche di lettura/scrittura della lista treni.
