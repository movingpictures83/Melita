
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeSet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mjari001
 */
public class DB_IO extends ByteFunc
{
    String DB;
    boolean is35;
    public HashMap<String, DB_SeqParms> seqList = new HashMap<String,DB_SeqParms> ();
    public HashMap<String, Integer> genusList = new HashMap<String,Integer>();
    public HashMap<String, Integer> speciesList = new HashMap<String,Integer>();
    public HashMap<String, Integer> phylumList = new HashMap<String,Integer>();
    // workspace
    //String toOutput;
    //private HashMap<String, ArrayList<String>> statPhyloMap = new HashMap<String,ArrayList<String>>();
    //private ArrayList<String> statPhylo = new ArrayList<String>();
    
    public DB_IO(String DB, boolean is35, String filename) throws FileNotFoundException
    {
        setDB(DB, is35, filename);
    }

    public void setDB(String DB, boolean is35, String filename) throws FileNotFoundException
    {
        this.DB = DB;
        this.is35 = is35;

        String pathDB = filename;//"";
        //if      (DB.equals("Silva")) pathDB = CONSTANT.pathSilva;
        //else if (DB.equals("RDP"))   pathDB = CONSTANT.pathRDP;
        //else if (DB.equals("HMDP"))  pathDB = CONSTANT.pathHMDP;
        int fid = (pathDB.indexOf("fasta") > 0) ? 1 : 0;
        //System.out.println(DB + ": " + fid + ": " + pathDB);

        if      (fid==0) readGenBank(pathDB);
        else if (fid==1) readFasta(pathDB);
    }

    public void setNewDB(String DB, boolean is35, String filename) throws FileNotFoundException
    {
        seqList.clear();
        setDB(DB, is35, filename);
    }



    public void readFasta(String filename) throws FileNotFoundException
    {
        Scanner fileScan = new Scanner(new File(filename));
        
        String LocusID = "";
        int seqCnt = 0;
        while (fileScan.hasNextLine()) // while not eof
        {
            String line = fileScan.nextLine();
            if (line.substring(0,1).equals(">"))
            {
                //int index = line.indexOf("|");
                LocusID = line.substring(1).trim();
                if (seqList.containsKey(LocusID))
                    System.out.println("HMMM: Not unique ID: " + LocusID);
            }
            else 
            {
                //String data = line;
                int l = line.length(); 
                seqList.put(LocusID, new DB_SeqParms(LocusID,"Bacteria;"+LocusID+";;",LocusID,line.trim()));
                //if (l < min) min = l;
                //else if (l > max) max = l;
                seqCnt++;
                //System.out.println(seqCnt + " " + l + " " + LocusID);
                //if(seqCnt == 10) break;
            }
        }

        fileScan.close();
        //System.out.println("Number of seqeunces: " + seqCnt); // 7600
        //System.out.println("Length: " + min + " to " + max);
    
    }
    
    public void readGenBank(String filename) throws FileNotFoundException
    {
        String s = "";
        Scanner fileScan = new Scanner(new File(filename));
        boolean isMetaData = true;
        int i = 0;

        String seqMeta = "";
        String seqData = "";
        while (fileScan.hasNextLine()) // while not eof
        {
            String line = fileScan.nextLine();
            if (line.trim().equals("")) continue;

            StringTokenizer tLine = new StringTokenizer(line);
            String cmd = tLine.nextToken();
            if (cmd.equals("ORIGIN"))
            {
                //getMetaData(seqMeta);
                isMetaData = false;
                seqData = line;
            }
            else if (cmd.equals("//"))  // && !isMetaData)
            {
                //System.out.println(seqData.substring(0,100));
                parseGenBank(seqMeta,seqData);
                isMetaData = true;
                seqMeta = line;

                //i++;
                //if (i == 75)
                //  break;
            }
            else
            {
                if (isMetaData) seqMeta += line;
                else            seqData += line;
            }
        }

        fileScan.close();
        //System.out.println("Number of seqeunces: " + seqList.size()); // 7600
    }

    private void parseGenBank(String metaData, String seqData)
    {
        int indxDef = metaData.indexOf("DEFINITION");
        int indxSrc = metaData.indexOf("SOURCE");
        int indxOrg = metaData.indexOf("ORGANISM");
        int indxRef = metaData.indexOf("REFERENCE");

        // Find LOCUS ID
        String LocusLine = metaData.substring(0,indxDef);
        StringTokenizer tLocus = new StringTokenizer(LocusLine);
        tLocus.nextToken();
        String LocusID = tLocus.nextToken().trim();
        //CONSTANT.seqLength = Integer.parseInt(tLocus.nextToken().trim());

        // Find Source
        String Source = metaData.substring(indxSrc,indxOrg);
        Source = Source.replace("SOURCE", "").trim();
        //System.out.println(Source);

        // Find Organism Tree
        String Organism = metaData.substring(indxOrg, indxRef);
        int indxBact = Organism.indexOf("Bacteria");
        int indxDot  = Organism.lastIndexOf(".");
        Organism = Organism.substring(indxBact,indxDot).trim();
        Organism = Organism.replace(" ", "");

        // clean up dataSeq
        seqData = seqData.replace("ORIGIN", "").trim();
        seqData = cleanGenBankSeq(seqData);
        byte [] b = DegString2DegByte(seqData);
        if (is35) revByte(b);
        seqData = toDegString(b);
        //if (is35) seqData = new StringBuffer(seqData).reverse().toString();
        //System.out.println(seqData.substring(0,100));

        if (seqList.containsKey(LocusID))
            System.out.println("HMMM: Not unique ID: " + LocusID);

        seqList.put(LocusID, new DB_SeqParms(LocusID,Organism,Source,seqData));
    }

    private void toGenusDB(HashMap<String, TreeSet<String>> seqMap) throws IOException
    {
        String filename = "TaxobLists/" + DB + "_genus_" + seqMap.size() + ".fasta";
        PrintWriter wFile = new PrintWriter(new FileWriter(filename));

        for (Entry<String, TreeSet<String>> e: seqMap.entrySet())
        {
            TreeSet t = e.getValue();
            String header = "> " + t.size() ;
            Iterator it = t.iterator();
            while (it.hasNext())
                header += "+" + it.next();

            wFile.println(header + "\n" + e.getKey());
        }
        wFile.close();

    }


    public void getSeqCntStats(boolean toFile) throws IOException
    {
        int cnt = 0;
        HashMap<String, TreeSet<String>> seqMap = new HashMap<String, TreeSet<String>>();
        HashMap<String, DB_SeqParms> specieMap = new HashMap<String, DB_SeqParms> ();
        HashMap<String, DB_SeqParms> genusMap  = new HashMap<String, DB_SeqParms> ();
        for (DB_SeqParms pS: seqList.values())
        {
            String s = toDegString(pS.data);
            s = s.substring(0, s.length()-0);
            if (!seqMap.containsKey(s))
                seqMap.put(s,new TreeSet<String>());

            String name = pS.phylHier + ";" + pS.source;
            seqMap.get(s).add(name);

            cnt = 0;
            if (specieMap.containsKey(name))
                specieMap.get(name).addSpecie(name, s);
            else
                specieMap.put(name,new DB_SeqParms("",pS.phylHier, pS.source,s));

            cnt = 0;
            if (genusMap.containsKey(pS.phylHier))
                genusMap.get(pS.phylHier).addSpecie(pS.phylHier, s);
            else
                genusMap.put(pS.phylHier,new DB_SeqParms("",pS.phylHier, pS.source,s));

        }
        
        //30: 387, 20: 388, 10: 388, 0: 388

        // print duplicate species
        multCnt2File(specieMap,"specie");
        multCnt2File(genusMap,"genus");
        

        if (toFile) toGenusDB(seqMap);

        int c = 0;
        for (TreeSet t: seqMap.values())
        {
            if (t.size() == 1)
            {
                //Iterator it = t.iterator();
                //while (it.hasNext())
                //    System.out.println(it.next());
                //System.out.println();
                c++;
            }
        }

        System.out.println("cntUniqSeq: "    + seqMap.size());
        System.out.println("cntUniqSpecie: " + specieMap.size());
        System.out.println("cntUniqGenus: "  + genusMap.size());
        System.out.println("cntDistSeq: " + c);
    }

    private void multCnt2File(HashMap<String, DB_SeqParms> hierMap, String idHier) throws IOException
    {
        String filename = DB + "_multSeqIDPer" + idHier + hierMap.size() + ".txt";
        PrintWriter wFile = new PrintWriter(new FileWriter(filename));

        for (DB_SeqParms sP: hierMap.values())
            wFile.print(sP.printMultCnt());
        wFile.close();
    }


    public void getHierHist()
    {
        int [] cnt = new int [10];
        for (int i=0; i<10; i++) cnt [i] = 0;
        for (DB_SeqParms pS: seqList.values())
            cnt[pS.nHier()]++;
        
        for (int i=0; i<10; i++) System.out.print(cnt[i] + " ");
        System.out.println();

    }

    public void createTaxonLists()
    {

    }
    public void createGenusHierList() throws IOException
    {
        genusList= new HashMap<String,Integer>();
        for (DB_SeqParms pS: seqList.values())
        {
            String g = pS.phylHier;
            genusList.put(g,0);
        }


        String filename = DB + "_GenusHierList_" + genusList.size()+ ".txt";
        PrintWriter wFile = new PrintWriter(new FileWriter(filename));

        for (String s: genusList.keySet())
            wFile.println(s);
        wFile.close();

    }

    public void createSpeciesHierList() throws IOException
    {
        genusList= new HashMap<String,Integer>();
        for (DB_SeqParms pS: seqList.values())
        {
            String g = pS.phylHier+";"+pS.source;
            genusList.put(g,0);
        }


        String filename = DB + "_SpeciesHierList_" + genusList.size()+ ".txt";
        PrintWriter wFile = new PrintWriter(new FileWriter(filename));

        for (String s: genusList.keySet())
            wFile.println(s);
        wFile.close();

    }

    public void createPhylumList(boolean toFile) throws IOException
    {
        phylumList = new HashMap<String,Integer>();
        for (DB_SeqParms pS: seqList.values())
        {
            String g = pS.phylHier.replaceAll("Bacteria;", "");
            g = g.substring(0,g.indexOf(";"));
            phylumList.put(g,0);
        }

        if (toFile)
        {
            String filename = DB + "_PhylumList_" + phylumList.size()+ ".txt";
            PrintWriter wFile = new PrintWriter(new FileWriter(filename));

            for (String s: phylumList.keySet())
                wFile.println(s);
            wFile.close();
        }
    }

    public void createGenusList(boolean toFile) throws IOException
    {
        genusList = new HashMap<String,Integer>();
        for (DB_SeqParms pS: seqList.values())
        {
            String g = pS.phylHier.substring(pS.phylHier.lastIndexOf(";")+1);
            genusList.put(g,0);
        }

        if (toFile)
        {
            String filename = DB + "_GenusList_" + genusList.size()+ ".txt";
            PrintWriter wFile = new PrintWriter(new FileWriter(filename));

            for (String s: genusList.keySet())
                wFile.println(s);
            wFile.close();
        }
    }

     public void createSpeciesList(boolean toFile) throws IOException
    {
        speciesList = new HashMap<String,Integer>();
        for (DB_SeqParms pS: seqList.values())
        {
            String g = pS.source;
            speciesList.put(g,0);
        }

        if (toFile)
        {
            String filename = DB + "_SpeciesList_" + speciesList.size()+ ".txt";
            PrintWriter wFile = new PrintWriter(new FileWriter(filename));

            for (String s: speciesList.keySet())
                wFile.println(s);
            wFile.close();
        }
    }

    public void rstTaxonLists()
    {
        for (Entry<String, Integer> e: genusList.entrySet())
            genusList.put(e.getKey(), 0);
        for (Entry<String, Integer> e: speciesList.entrySet())
            speciesList.put(e.getKey(), 0);
        for (Entry<String, Integer> e: phylumList.entrySet())
            phylumList.put(e.getKey(), 0);
    }

    public void findGenus(String filename) throws FileNotFoundException
    {
        Scanner rdFile = new Scanner (new File(filename));

        while (rdFile.hasNext())
        {
            String inGenus = rdFile.nextLine().trim();
            if(inGenus.isEmpty()) continue;

            boolean found = false;
            for (DB_SeqParms sP: seqList.values())
            {
                if (sP.phylHier.indexOf(inGenus) > 0)
                {
                    found = true;
                    break;
                }
            }
            if (!found)
                System.out.println(inGenus + " is not in DB");
        }
    }
}
