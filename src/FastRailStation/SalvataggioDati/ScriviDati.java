package FastRailStation.SalvataggioDati;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import javafx.collections.ObservableList;
import FastRailStation.model.Treno;
import FastRailStation.model.Utente;

public class ScriviDati
{
    // Costruttore
    public ScriviDati()
    {

    }

    // Metodo per scrivere i dati degli utenti su file
    public void scriviUtenti(ArrayList<Utente> utenti)
    {
        try (BufferedWriter fileDaScrivere = new BufferedWriter(
                new FileWriter("./src/FastRailStation/SalvataggioDati/utenti.txt")))
        {
            for (Utente utente : utenti)
            {
                fileDaScrivere.write(utente.getNome() + "+");
                fileDaScrivere.write(utente.getCognome() + "+");
                fileDaScrivere.write(utente.getMail() + "+");
                fileDaScrivere.write(utente.getNascita() + "+");
                fileDaScrivere.write(utente.getPassword() + "+");
                fileDaScrivere.write(utente.getNumeroCellulare() + "+");
                fileDaScrivere.write(utente.getNazioneResideza() + "+");
                fileDaScrivere.write(utente.getCittaResidenza() + "+");
                fileDaScrivere.write(utente.getViaResidenza() + "+");
                fileDaScrivere.write(utente.getCodiceCarta() + "+");
                fileDaScrivere.write(utente.getScadenza() + "+");
                fileDaScrivere.newLine();
            }

            fileDaScrivere.flush();

            System.out.println("Dati scritti con successo.");
        }
        catch (IOException e)
        {
            System.err.println("Errore nella scrittura dei dati su file: " + e.getMessage());
        }
    }

    public void scriviTreni(ObservableList<Treno> treni)
    {
        try (FileWriter writer = new FileWriter("./src/FastRailStation/SalvataggioDati/treni.csv"))
        {
            writer.write("Modello,Provenienza,Destinazione,Compagnia,Codice,NumMax,GiornoArrivo,OraArrivo,GiornoPartenza,OraPartenza,Intervallo\n");

            for (Treno treno : treni)
            {
                writer.write(String.format("%s,%s,%s,%s,%s,%d,%s,%s,%s,%s,%d\n",
                        treno.getModello(),
                        treno.getProvenienza(),
                        treno.getDestinazione(),
                        treno.getCompagnia(),
                        treno.getCodice(),
                        treno.getPostiMassimi(),
                        treno.getGiornoArrivoString(),
                        treno.getOraArrivoString(),
                        treno.getGiornoPartenzaString(),
                        treno.getOraPartenzaString(),
                        treno.getIntervallo()));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void scriviTreniFine(ObservableList<Treno> treni)
    {
        try (FileWriter writer = new FileWriter("./src/mainFolder/salvataggioDati/treni.csv"))
        {
            writer.write("Modello,Provenienza,Destinazione,Compagnia,Codice,NumMax,GiornoArrivo,OraArrivo,GiornoPartenza,OraPartenza,Intervallo,Stato,PostiOccupati,Ritardo,InizioManutenzione,FineManutenzione,Deposito\n");

            for (Treno treno : treni)
            {
                if (treno.getInizioManutenzione() != null && treno.getFineManutenzione() != null && treno.getDeposito() != null)
                {
                    String formatString = "%s,%s,%s,%s,%s,%d,%s,%s,%s,%s,%d,%s,%d,%d,%s,%s,%s";

                    writer.write(String.format(formatString + "\n",
                            treno.getModello(),
                            treno.getProvenienza(),
                            treno.getDestinazione(),
                            treno.getCompagnia(),
                            treno.getCodice(),
                            treno.getPostiMassimi(),
                            treno.getGiornoArrivoString(),
                            treno.getOraArrivoString(),
                            treno.getGiornoPartenzaString(),
                            treno.getOraPartenzaString(),
                            treno.getIntervallo(),
                            treno.getStato(),
                            treno.getNumeroPostiOccupati(),
                            treno.getRitardo(),
                            treno.getInizioManutenzione().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            treno.getFineManutenzione().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            treno.getDeposito()));
                }
                else
                {
                    String formatString = "%s,%s,%s,%s,%s,%d,%s,%s,%s,%s,%d,%s,%d,%d";

                    writer.write(String.format(formatString + "\n",
                            treno.getModello(),
                            treno.getProvenienza(),
                            treno.getDestinazione(),
                            treno.getCompagnia(),
                            treno.getCodice(),
                            treno.getPostiMassimi(),
                            treno.getGiornoArrivoString(),
                            treno.getOraArrivoString(),
                            treno.getGiornoPartenzaString(),
                            treno.getOraPartenzaString(),
                            treno.getIntervallo(),
                            treno.getStato(),
                            treno.getNumeroPostiOccupati(),
                            treno.getRitardo()));
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
