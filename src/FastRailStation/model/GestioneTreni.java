package FastRailStation.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import FastRailStation.salvataggioDati.LeggiDati;
import FastRailStation.salvataggioDati.ScriviDati;

public class GestioneTreni
{
    private static GestioneTreni instance;

    private ObservableList<Treno> elencoTreniPartenza = FXCollections.observableArrayList();
    private ObservableList<Treno> elencoTreniArrivo = FXCollections.observableArrayList();
    private ObservableList<Treno> elencoTreniTutti = FXCollections.observableArrayList();
    private ObservableList<Treno> elencoTreniDeposito = FXCollections.observableArrayList();
    private ObservableList<Treno> elencoTreniTerra = FXCollections.observableArrayList();
    private ObservableList<Treno> elencoTreniManutenzione = FXCollections.observableArrayList();

    private static ArrayList<Boolean> binari = new ArrayList<Boolean>();

    private String compArrivi;
    private String destArrivi;
    private LocalDate dateArrivi;

    private ScriviDati scrivi;
    private LeggiDati leggi;

    public GestioneTreni()
    {
        scrivi = new ScriviDati();
        leggi = new LeggiDati();
        riempiBinari();
        caricaDati();
    }

    public void riempiBinari()
    {
        for (int i = 0; i < 70; i++)
        {
            binari.add(false);
        }
    }

    public ArrayList<Boolean> getBinari()
    {
        return binari;
    }

    public ObservableList<Treno> getElencoLista()
    {
        return elencoTreniTutti;
    }

    public ObservableList<Treno> getElencoListaArrivi()
    {
        return elencoTreniArrivo;
    }

    public ObservableList<Treno> getElencoListaPartenze()
    {
        return elencoTreniPartenza;
    }

    public ObservableList<Treno> getElencoListaTerra()
    {
        return elencoTreniTerra;
    }

    public ObservableList<Treno> getElencoListaManutenzione()
    {
        return elencoTreniManutenzione;
    }

    public static GestioneTreni getInstance()
    {
        if (instance == null)
        {
            instance = new GestioneTreni();
        }

        return instance;
    }

    public void addTreno(String modello, String provenienza, String destinazione, String compagnia, String codice,
                         int numMax, LocalDate giornoArrivo, LocalTime oraArrivo,
                         LocalDate giornoPartenza, LocalTime oraPartenza, int intervallo,
                         String stato, int ritardo, int numPosti)
    {
        LocalDate arrivo = giornoArrivo;
        LocalDate partenza = giornoPartenza;

        while (arrivo.isBefore(LocalDate.now()))
        {
            arrivo = arrivo.plusDays(intervallo);
            partenza = partenza.plusDays(intervallo);
        }

        Treno treno = new Treno(modello, provenienza, destinazione, compagnia, codice, numMax,
                arrivo, oraArrivo, partenza, oraPartenza, intervallo,
                stato, ritardo, numPosti);

        treno.setBinario(assegnaBinario());

        synchronized (elencoTreniTutti)
        {
            elencoTreniTutti.add(treno);
        }

        synchronized (elencoTreniTutti)
        {
            scrivi.scriviTreniFine(elencoTreniTutti);
        }
    }

    public void addTreno(String modello, String provenienza, String destinazione, String compagnia, String codice,
                         int numMax, LocalDate giornoArrivo, LocalTime oraArrivo,
                         LocalDate giornoPartenza, LocalTime oraPartenza, int intervallo,
                         String stato, LocalDate inizioManutenzione, LocalDate fineManutenzione,
                         String deposito, int ritardo, int numPosti)
    {
        LocalDate arrivo = giornoArrivo;
        LocalDate partenza = giornoPartenza;

        while (arrivo.isBefore(LocalDate.now()))
        {
            arrivo = arrivo.plusDays(intervallo);
            partenza = partenza.plusDays(intervallo);
        }

        Treno treno = new Treno(modello, provenienza, destinazione, compagnia, codice, numMax,
                arrivo, oraArrivo, partenza, oraPartenza, intervallo,
                stato, inizioManutenzione, fineManutenzione, deposito,
                ritardo, numPosti);

        treno.setBinario(assegnaBinario());

        synchronized (elencoTreniTutti)
        {
            elencoTreniTutti.add(treno);
        }

        synchronized (elencoTreniTutti)
        {
            scrivi.scriviTreniFine(elencoTreniTutti);
        }
    }

    public void addTreno(Treno treno)
    {
        synchronized (elencoTreniTutti)
        {
            elencoTreniTutti.add(treno);
            scrivi.scriviTreniFine(elencoTreniTutti);
        }
    }

    public void aggiornaLista()
    {
        synchronized (elencoTreniTutti)
        {
            scrivi.scriviTreniFine(elencoTreniTutti);
        }
    }

    public void rimuoviTreno(Treno treno)
    {
        synchronized (elencoTreniTutti)
        {
            elencoTreniTutti.remove(treno);
        }
    }

    public int assegnaBinario()
    {
        boolean correct = false;

        while (correct == false)
        {
            int index = (int) (Math.random() * 70);

            if (binari.get(index) == false)
            {
                correct = true;
                binari.set(index, true);
                return index;
            }
        }

        return -1;
    }

    public void setDataPartenza(LocalDate data)
    {
        elencoTreniDeposito.clear();

        synchronized (elencoTreniTutti)
        {
            elencoTreniDeposito.addAll(elencoTreniTutti);
        }

        elencoTreniPartenza.clear();

        for (int i = 0; i < elencoTreniDeposito.size(); i++)
        {
            if (elencoTreniDeposito.get(i).getGiornoPartenza().isEqual(data) &&
                    (elencoTreniDeposito.get(i).getStato().equals("In partenza")
                            || elencoTreniDeposito.get(i).getStato().equals("In attesa")))
            {
                elencoTreniPartenza.add(elencoTreniDeposito.get(i));
            }
        }

        bubbleSortByOraPartenza(elencoTreniPartenza);
    }

    public void setDataPartenzaAdmin(LocalDate data)
    {
        elencoTreniDeposito.clear();

        synchronized (elencoTreniTutti)
        {
            elencoTreniDeposito.addAll(elencoTreniTutti);
        }

        elencoTreniPartenza.clear();

        for (int i = 0; i < elencoTreniDeposito.size(); i++)
        {
            if (elencoTreniDeposito.get(i).getGiornoPartenza().isEqual(data) &&
                    elencoTreniDeposito.get(i).getStato().equals("In partenza"))
            {
                elencoTreniPartenza.add(elencoTreniDeposito.get(i));
            }
        }

        bubbleSortByOraPartenza(elencoTreniPartenza);
    }

    public void setDataArrivoAdmin(LocalDate data)
    {
        elencoTreniDeposito.clear();

        synchronized (elencoTreniTutti)
        {
            elencoTreniDeposito.addAll(elencoTreniTutti);
        }

        elencoTreniArrivo.clear();

        for (int i = 0; i < elencoTreniDeposito.size(); i++)
        {
            if (elencoTreniDeposito.get(i).getGiornoArrivo().isEqual(data) &&
                    elencoTreniDeposito.get(i).getStato().equals("In arrivo"))
            {
                elencoTreniArrivo.add(elencoTreniDeposito.get(i));
            }
        }

        bubbleSortByOraArrivo(elencoTreniArrivo);
    }

    public void setDataArrivo(LocalDate data)
    {
        elencoTreniDeposito.clear();

        synchronized (elencoTreniTutti)
        {
            elencoTreniDeposito.addAll(elencoTreniTutti);
        }

        elencoTreniArrivo.clear();

        for (int i = 0; i < elencoTreniDeposito.size(); i++)
        {
            if (elencoTreniDeposito.get(i).getGiornoArrivo().isEqual(data) &&
                    (elencoTreniDeposito.get(i).getStato().equals("In arrivo")
                            || elencoTreniDeposito.get(i).getStato().equals("In attesa")))
            {
                elencoTreniArrivo.add(elencoTreniDeposito.get(i));
            }
        }

        bubbleSortByOraArrivo(elencoTreniArrivo);
    }

    public void setDataTerra(LocalDate data)
    {
        elencoTreniDeposito.clear();

        synchronized (elencoTreniTutti)
        {
            elencoTreniDeposito.addAll(elencoTreniTutti);
        }

        elencoTreniTerra.clear();

        for (int i = 0; i < elencoTreniDeposito.size(); i++)
        {
            if (elencoTreniDeposito.get(i).getGiornoArrivo().isEqual(data) &&
                    elencoTreniDeposito.get(i).getStato().equals("In attesa"))
            {
                elencoTreniTerra.add(elencoTreniDeposito.get(i));
            }
        }

        bubbleSortByOraPartenza(elencoTreniTerra);
    }

    public void setDataManutenzione(LocalDate data)
    {
        elencoTreniDeposito.clear();

        synchronized (elencoTreniTutti)
        {
            elencoTreniDeposito.addAll(elencoTreniTutti);
        }

        elencoTreniManutenzione.clear();

        for (int i = 0; i < elencoTreniDeposito.size(); i++)
        {
            LocalDate inizioM = elencoTreniDeposito.get(i).getInizioManutenzione();
            LocalDate fineM = elencoTreniDeposito.get(i).getFineManutenzione();

            if (elencoTreniDeposito.get(i).getStato().equals("In manutenzione"))
            {
                if (inizioM != null && fineM != null &&
                        data.isAfter(inizioM) && data.isBefore(fineM))
                {
                    elencoTreniManutenzione.add(elencoTreniDeposito.get(i));
                }
            }
        }

        bubbleSortByOraPartenza(elencoTreniManutenzione);
    }

    public void aggiornaArrivo(String parola)
    {
        elencoTreniDeposito.clear();

        for (int i = 0; i < elencoTreniArrivo.size(); i++)
        {
            elencoTreniDeposito.add(elencoTreniArrivo.get(i));
        }

        elencoTreniArrivo.clear();

        for (Treno treno : elencoTreniDeposito)
        {
            if (treno.getCompagnia().toLowerCase().contains(parola.toLowerCase()) ||
                    treno.getProvenienza().toLowerCase().contains(parola.toLowerCase()))
            {
                elencoTreniArrivo.add(treno);
            }
        }
    }

    public void aggiornaPartenza(String parola)
    {
        elencoTreniDeposito.clear();

        for (int i = 0; i < elencoTreniPartenza.size(); i++)
        {
            elencoTreniDeposito.add(elencoTreniPartenza.get(i));
        }

        elencoTreniPartenza.clear();

        for (Treno treno : elencoTreniDeposito)
        {
            if (treno.getCompagnia().toLowerCase().contains(parola.toLowerCase()) ||
                    treno.getDestinazione().toLowerCase().contains(parola.toLowerCase()))
            {
                elencoTreniPartenza.add(treno);
            }
        }
    }

    public void aggiornaArrivoAdmin(String parola, String compagnia)
    {
        elencoTreniDeposito.clear();

        for (int i = 0; i < elencoTreniArrivo.size(); i++)
        {
            elencoTreniDeposito.add(elencoTreniArrivo.get(i));
        }

        elencoTreniArrivo.clear();

        for (Treno treno : elencoTreniDeposito)
        {
            if (treno.getCompagnia().toLowerCase().contains(compagnia.toLowerCase()) &&
                    treno.getProvenienza().toLowerCase().contains(parola.toLowerCase()))
            {
                elencoTreniArrivo.add(treno);
            }
        }
    }

    public void aggiornaPartenzaAdmin(String parola, String compagnia)
    {
        elencoTreniDeposito.clear();

        for (int i = 0; i < elencoTreniPartenza.size(); i++)
        {
            elencoTreniDeposito.add(elencoTreniPartenza.get(i));
        }

        elencoTreniPartenza.clear();

        for (Treno treno : elencoTreniDeposito)
        {
            if (treno.getCompagnia().toLowerCase().contains(compagnia.toLowerCase()) &&
                    treno.getDestinazione().toLowerCase().contains(parola.toLowerCase()))
            {
                elencoTreniPartenza.add(treno);
            }
        }
    }

    public void aggiornaTerraAdmin(String parola, String compagnia)
    {
        elencoTreniDeposito.clear();

        for (int i = 0; i < elencoTreniTerra.size(); i++)
        {
            elencoTreniDeposito.add(elencoTreniTerra.get(i));
        }

        elencoTreniTerra.clear();

        for (Treno treno : elencoTreniDeposito)
        {
            if (treno.getCompagnia().toLowerCase().contains(compagnia.toLowerCase()) &&
                    treno.getDestinazione().toLowerCase().contains(parola.toLowerCase()))
            {
                elencoTreniTerra.add(treno);
            }
        }
    }

    public void aggiornaManutenzioneAdmin(String parola, String compagnia)
    {
        elencoTreniDeposito.clear();

        for (int i = 0; i < elencoTreniManutenzione.size(); i++)
        {
            elencoTreniDeposito.add(elencoTreniManutenzione.get(i));
        }

        elencoTreniManutenzione.clear();

        for (Treno treno : elencoTreniDeposito)
        {
            if (treno.getCompagnia().toLowerCase().contains(compagnia.toLowerCase()) &&
                    treno.getDestinazione().toLowerCase().contains(parola.toLowerCase()))
            {
                elencoTreniManutenzione.add(treno);
            }
        }
    }

    private void bubbleSortByOraPartenza(ObservableList<Treno> list)
    {
        int n = list.size();

        for (int i = 0; i < n - 1; i++)
        {
            for (int j = 0; j < n - i - 1; j++)
            {
                if (list.get(j).getOraPartenza().isAfter(list.get(j + 1).getOraPartenza()))
                {
                    Treno temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    private void bubbleSortByOraArrivo(ObservableList<Treno> list)
    {
        int n = list.size();

        for (int i = 0; i < n - 1; i++)
        {
            for (int j = 0; j < n - i - 1; j++)
            {
                if (list.get(j).getOraArrivo().isAfter(list.get(j + 1).getOraArrivo()))
                {
                    Treno temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    private void caricaDati()
    {
        // QUI DEVI CONTROLLARE IL METODO GIUSTO
        // Perché "leggiAereiNuovo()" è sicuramente sbagliato.
        for (Treno treno : leggi.leggiTreni())
        {
            addTreno(treno);
        }

        scrivi.scriviTreniFine(elencoTreniTutti);
    }

    public ObservableList<Treno> getFilteredPartenze()
    {
        return elencoTreniDeposito;
    }

    public ObservableList<Treno> getFilteredArrivi()
    {
        return elencoTreniDeposito;
    }

    public void resetPartenze()
    {
        elencoTreniPartenza.clear();

        synchronized (elencoTreniTutti)
        {
            elencoTreniPartenza.addAll(elencoTreniTutti);
        }

        bubbleSortByOraPartenza(elencoTreniPartenza);
    }

    public void resetArrivi()
    {
        elencoTreniArrivo.clear();

        synchronized (elencoTreniTutti)
        {
            elencoTreniArrivo.addAll(elencoTreniTutti);
        }

        bubbleSortByOraArrivo(elencoTreniArrivo);
    }

    public void resetFilter()
    {
        resetArrivi();
        resetPartenze();
    }

    public void setTreniInArrivo(String comp, String dest, LocalDate date)
    {
        compArrivi = comp;
        destArrivi = dest;
        dateArrivi = date;
    }

    public String getCompArrivi()
    {
        return compArrivi;
    }

    public String getDestArrivi()
    {
        return destArrivi;
    }

    public LocalDate getDateArrivi()
    {
        return dateArrivi;
    }

    public void setTreniInPartenze(String comp, String dest, LocalDate date)
    {
        compArrivi = comp;
        destArrivi = dest;
        dateArrivi = date;
    }

    public String getCompPartenze()
    {
        return compArrivi;
    }

    public String getDestPartenze()
    {
        return destArrivi;
    }

    public LocalDate getDatePartenze()
    {
        return dateArrivi;
    }

    public void setTreniInTerra(String comp, String dest, LocalDate date)
    {
        compArrivi = comp;
        destArrivi = dest;
        dateArrivi = date;
    }

    public String getCompTerra()
    {
        return compArrivi;
    }

    public String getDestTerra()
    {
        return destArrivi;
    }

    public LocalDate getDateTerra()
    {
        return dateArrivi;
    }

    public void setTreniInManutenzione(String comp, String dest, LocalDate date)
    {
        compArrivi = comp;
        destArrivi = dest;
        dateArrivi = date;
    }

    public String getCompManutenzione()
    {
        return compArrivi;
    }

    public String getDestManutenzione()
    {
        return destArrivi;
    }

    public LocalDate getDateManutenzione()
    {
        return dateArrivi;
    }

    public void scriviDati()
    {
        scrivi.scriviTreniFine(elencoTreniTutti);
    }
}
