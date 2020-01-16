
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeSet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class AmplFileStat
{
    HashMap<String,TreeSet<String>> id_data;
    public AmplFileStat()
    {
        id_data = new HashMap<String,TreeSet<String>>();
    }

    public void distinguishGenusSpecie(String fileList, String genusList, String iFolder, String ofilename) throws IOException
    {
        //String ofilename = "SpeciePerGenus"  + "V35_V58" + ".txt";
        PrintWriter wFile = new PrintWriter(new FileWriter(ofilename));

        Scanner fileScan = new Scanner(new File(fileList));
        //System.out.println("Reading " + fileList);

        HashMap<String, HashSet<String>> speciePerGenus = new HashMap<String, HashSet<String>> ();
        setupGenusList(genusList, speciePerGenus);

        while (fileScan.hasNextLine()) // while not eof
        {
            String line = fileScan.nextLine();
            String f = iFolder + "/" + line.trim();
            //System.out.println("Reading " + f);
            distinguishGenusSpecie(f,speciePerGenus);
        }

        for (Entry<String, HashSet<String>> e: speciePerGenus.entrySet())
        {
            wFile.println(e.getValue().size() + ";" + e.getKey());
            Iterator it = e.getValue().iterator();
        }

        wFile.println();

        for (Entry<String, HashSet<String>> e: speciePerGenus.entrySet())
        {
            wFile.println(e.getValue().size() + ";" + e.getKey());
            Iterator it = e.getValue().iterator();

            while (it.hasNext())
                wFile.println(";;" + it.next());
        }
        wFile.close();
    }

    public void distinguishGenusSpecie(String filename,
            HashMap<String, HashSet<String>> speciePerGenus) throws FileNotFoundException
    {
        Scanner fileScan = new Scanner(new File(filename));
        //System.out.println("STARTING");

        String LocusID = "";
        int seqCnt = 0;
        int sCnt = 1;
        String allSpecies = "";
        while (fileScan.hasNextLine()) // while not eof
        {
            String line = fileScan.nextLine();
            if (line.substring(0,1).equals(">"))
            {
                if (seqCnt == 1)
                {
                    String [] sG = allSpecies.split(";");
                    if (speciePerGenus.containsKey(sG[0]))
                       speciePerGenus.get(sG[0]).add(sG[1]);
                }
                else if (seqCnt > 1)
                {
                    String [] sG = allSpecies.split(";");
                    if (speciePerGenus.containsKey(sG[0]))
                    {
                        boolean uniqGenus = true;
                        for (int i=2; i<sG.length; i=i+2)
                            if (sG[i-2].equals(sG[i]))
                            {
                                uniqGenus = false;
                                break;
                            }
                        if (uniqGenus)
                        {
                            boolean uniqSpecie = true;
                            for (int i=2; i<sG.length; i=i+2)
                                if (sG[i-2].equals(sG[i]))
                                {
                                    uniqSpecie = false;
                                    break;
                                }
                            if (uniqSpecie)
                              speciePerGenus.get(sG[0]).add(sG[1]);
                        }
                    }
                }
                seqCnt = Integer.parseInt(line.substring(1).trim());
                allSpecies = "";
            }
            else
            {
                int indx = line.trim().lastIndexOf(";");
                String specie = line.substring(indx+1);
                String genusHier = line.substring(0,indx);
                indx = genusHier.trim().lastIndexOf(";");
                String genus = line.substring(indx+1);

                allSpecies += genus + ";" + specie + ";";
                //if (line.indexOf("Pseudomonas") > 0)
                    //System.out.println(line);// + "\n");// + allSpecies);


            }
        }
    }

    private void setupGenusList(String filename, HashMap<String, HashSet<String>> speciePerGenus) throws FileNotFoundException
    {
        Scanner fileScan = new Scanner(new File(filename));
        //System.out.println("Reading " + filename);

        while (fileScan.hasNextLine()) // while not eof
        {
            String line = fileScan.nextLine();
            speciePerGenus.put(line.trim(), new HashSet<String> ());
        }

    }


    public void findIfMissing(String genusToFind, String genusMiss, String iFolder) throws FileNotFoundException
    {
        TreeSet<String> genusList = new TreeSet<String>();
        Scanner fileGF = new Scanner(new File(genusToFind));
        while (fileGF.hasNextLine()) // while not eof
        {
            String line = fileGF.nextLine().trim();
            if (line.isEmpty()) continue;
            genusList.add(line);
        }
        //System.out.println("Looking for " + genusList.size() + " genuses");

        Scanner fileGMList = new Scanner(new File(genusMiss));
        while (fileGMList.hasNextLine()) // while not eof
        {
            String line = fileGMList.nextLine().trim();
            if (line.isEmpty()) continue;

            //System.out.println("Reading " + line);
            String fileID = line.replace("_missGenus.txt", "");
            if (!id_data.containsKey(fileID))
                id_data.put(fileID, new TreeSet<String>());

            Scanner fileGM = new Scanner (new File(iFolder + "/" + line));

            //System.out.println(line);
            while(fileGM.hasNext())
            {
                String genus = fileGM.nextLine().trim();
                if (genus.isEmpty()) continue;

                genus = genus.substring(genus.lastIndexOf(";")+1);
                if (genusList.contains(genus))
                    id_data.get(fileID).add(genus);
                    //System.out.println("\t" + genus);
            }
        }
    }


    public void findIfExist(String genusToFind, String genusExist, String iFolder) throws FileNotFoundException
    {
        TreeSet<String> genusList = new TreeSet<String>();
        Scanner fileGF = new Scanner(new File(genusToFind));
        while (fileGF.hasNextLine()) // while not eof
        {
            String line = fileGF.nextLine().trim();
            if (line.isEmpty()) continue;
            genusList.add(line);
        }
        //System.out.println("Looking for " + genusList.size() + " genuses");

        Scanner fileGMList = new Scanner(new File(genusExist));
        while (fileGMList.hasNextLine()) // while not eof
        {
            String line = fileGMList.nextLine().trim();
            if (line.isEmpty()) continue;

            //System.out.print("Reading " + line);
            String fileID = line.replace("_amplSeqID.txt", "");
            if (!id_data.containsKey(fileID))
            {
                //System.out.println("new id: " + fileID);
                id_data.put(fileID, new TreeSet<String>());
            }

            Scanner fileGM = new Scanner (new File(iFolder + "/" + line));
            TreeSet<String> existGenus  = new TreeSet<String>();

            while(fileGM.hasNext())
            {
                String genusH = fileGM.nextLine().trim();
                if (genusH.isEmpty() || genusH.substring(0,1).equals(">")) continue;

                genusH = genusH.substring(0,genusH.lastIndexOf(";"));
                existGenus.add(genusH.substring(genusH.lastIndexOf(";")+1));
            }
            //System.out.println( ": " + existGenus.size());

            //System.out.println(line);
            for (String s: genusList)
            {
                if (!existGenus.contains(s))
                {
                   /*if (s.indexOf("Flavimonas") >= 0)
                        System.out.print("\t" + s + " does not exist " +
                                id_data.get(fileID).size());*/
                   id_data.get(fileID).add(s);
                   //System.out.println("\t" + id_data.get(fileID).size());
                }
                   //System.out.println("\t" + s);
            }
        }
    }

    public void missGenusToFile(String filename) throws IOException
    {
        PrintWriter wFile = new PrintWriter(new FileWriter(filename));
        for (Entry<String,TreeSet<String>> e: id_data.entrySet())
        {
            wFile.println(e.getKey());
            for (String s: e.getValue())
                wFile.println("\t" + s);
        }
        wFile.close();

    }

}
