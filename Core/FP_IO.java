
import java.io.IOException;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class FP_IO extends ByteFunc
{
    String DB;
    boolean is35;
    public HashMap<String, DB_SeqParms> seqList = new HashMap<String,DB_SeqParms> ();
    public HashMap<String, DP_doClst> primerList = new HashMap<String, DP_doClst> ();
    
    
    // workspace
    String header;
    String toOutput;
    private HashMap<String, ArrayList<String>> statPhyloMap = new HashMap<String,ArrayList<String>>();
    private ArrayList<String> statPhylo = new ArrayList<String>();
    
    public FP_IO(String filename, String DB, int fid, boolean is35) throws FileNotFoundException
    {
        this.DB = DB;
        this.is35 = is35;
        //if (fid==0)readGenBank(filename);
        //else if (fid==1) readFasta(filename);
    }

  

    public String statDPTaxon (String inDP, String seqHier)
    {
        String out = "";
        for (DB_SeqParms s: seqList.values())
        {
            if (s.phylHier.contains(seqHier))
            {
                DP_inSeq dp = s.ExactSeqDPMatch(DegString2DegByte(inDP));
                //System.out.println(dp.toString());
                out += s.phylHier + "; " + s.ID + ": " + dp.toString();
            }
        }
        if (out.equals("")) out = "-2";
        return out;   // did not find the seqID
    }

    public int posDPSeqID (String inDP, String seqID)
    {
        for (DB_SeqParms s: seqList.values())
        {
            if (s.ID.equalsIgnoreCase(seqID))
            {
                DP_inSeq dp = s.ExactSeqDPMatch(DegString2DegByte(inDP));
                //System.out.println(dp.toString());
                if (dp.nErrors == 0) return dp.posSeq;
                else                 return -1;
            }
        }
        return -2;   // did not find the seqID
    }


    public void statTaxon(String filename) throws IOException
    {
        statPhylo.clear();
        for (DB_SeqParms seq: seqList.values())
            statPhylo.add(seq.fullHier());

        StatTree BactTree = new StatTree();
        for (String s: statPhylo)
            BactTree.addNode(s); //System.out.println(i + ": " + s); i++
        
        // collect stats
        BactTree.getStatsHier(5, filename);
    }

   
    public void statsPerLevel(int nLevel)
    {
        String inDP = "GTGCCAGCAVMYGCGRWDA";
        anlzStatPhylo(inDP, nLevel);
    }

    private void anlzStatPhylo(String inDP, int nLevel)
    {   
        ArrayList<String> sL = statPhyloMap.get(inDP);
        System.out.println("Number of elements in the tree: " + sL.size());

        int i = 0;
        StatTree BactTree = new StatTree();
        for (String s: sL)
            BactTree.addNode(s); //System.out.println(i + ": " + s); i++;
 
        // collect stats
        BactTree.getStatsPerLevel(nLevel);
    }
    
    public void statSeqLength()
    {
        int min = 2000;
        int max = 0;
        int avg = 0;
        for (DB_SeqParms s: seqList.values())
        {
            int l = s.length;
            if (l < min) min = l;
            else if (l > max) max = l;
            avg += l;
        }
        System.out.println(DB + " Length(min, max, avg) = " + min + ", " + max + ", " + (avg/seqList.size()));
    }

    public void findConservedRgn(String inDP, int nConservedNt, int minSNPfreq)
    {
        DP_doClst pC = primerList.get(inDP);
        pC.findConservedRgnRange(nConservedNt, minSNPfreq);
    }
    
    public void findEcoliPaergRDP() 
    {
               // this is for RDP database only
        String Ecoli;
        for (DB_SeqParms s: seqList.values())
        {
           if (s.ID.equals("S000004313"))
           {
               Ecoli = toDegString(s.data);
               //String rev = new StringBuffer(Ecoli).reverse().toString();
               System.out.println("Ecoli Length: " + Ecoli.length());
               System.out.println(printDegSeq(Ecoli));
               //System.out.println(printDegSeq(rev));
           }
           else if (s.ID.equals("S000010427"))
           {
               Ecoli = toDegString(s.data);
               System.out.println("Paerg Length: " + Ecoli.length());
               System.out.println(printDegSeq(Ecoli));
           }
        }
    }
    

    private void createSecondDP(String inDP, int maxDeg, boolean startOrgPrimer)
    {
        System.out.println("Second Clustering: First Start");
        DP_doClst pC = primerList.get(inDP);
        pC.clstFreq(false, maxDeg);
        System.out.println("Second Clustering: First End");
        System.out.println("Second Clustering: Second Start");
        System.out.println("Size of pSet: " + pC.pSet.size());
        DP_doClst pC2 = new DP_doClst(pC.pSet);
        pC2.clstFreq(startOrgPrimer);
        System.out.println("Second Clustering: Second end");
        toOutput = pC2.toOutput;
    }

    public void getStatAmplList(String filename) throws IOException
    {
        StatTree BactTree = new StatTree();
        System.out.println(statPhylo.size());
        for (String s: statPhylo)
        {
            //System.out.println(i + ": " + s);
            BactTree.addNode(s);
            //i++;
        }

        // collect stats
        BactTree.getStatsHier(5, filename);
    }

/*
    public String findAmplicons(AmpParms amp, String filename) throws IOException
    {
        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);
        statPhylo.clear();
        HashMap<Integer,Integer> amplLength = new HashMap <Integer, Integer>();
        
        int cnt = 0;
        for (FP_PrimerSeq s: seqList.values())
        {
            String header = "> " + s.seq.ID + "|" + s.seq.source + "|" + s.seq.phylHier + "\n";
            // find
            int fL = amp.fP.dpP.dpLength;
            int rL = amp.rP.dpP.dpLength;
            DP_inSeq dpF = s.ExactSeqDPMatch(amp.fP.dpP.DP);
            DP_inSeq dpR = s.ExactSeqDPMatch(amp.rP.dpP.DP);

            //if (dpR.posSeq < dpF.posSeq)
            //        System.out.println("WARNING " + s.seq.ID + ": Reverse Primer is before Forward Primer");

            int dist = dpR.posSeq - dpF.posSeq - fL;
            if (dpF.nErrors != 0 || dpR.nErrors != 0 || dist < amp.minDist() || dist > amp.maxDist())
            {
                wFile.print(header+"\n");
                //System.out.print(header + "******NODATA*****\n");
                cnt++;
            }
            else
            {
                int ampL = fL + rL + dist;
                byte [] ampSeq = new byte [ampL];
                System.arraycopy(s.seq.data, dpF.posSeq, ampSeq, 0, ampL);
                String d = header + toDegString(ampSeq);
                wFile.println(d);
                int c = 0;
                if (amplLength.containsKey(ampL))
                    c = amplLength.get(ampL);
                amplLength.put(ampL,c+1);
                statPhylo.add(s.seq.phylHier+";"+s.seq.source+";");
                //System.out.println(d);
            }
        }

        String toLog =  "\nAmplicons: {";
        toLog += "numSeq " + statPhylo.size();
        //System.out.println("Num of seq missing amplicon: " + cnt);
        toLog += ", diffLength: " + amplLength.size() + "}\n" + amplLength.toString();
        wFile.close();


        return toLog;
    }

  */

    public static void printHeader() {
       /////////////////////////////////////////////////////////////////////////////////////////////////////////////
       // Display opening screen.
   System.out.println( "***********************************************************************************" );
   System.out.println( "*                                                                                 *" );
   System.out.println( "*   M           M    EEEEEEEEEEE   L             I   TTTTTTTTTTT        A         *" );
   System.out.println( "*   MM         MM    E             L             I        T            A A        *" );
   System.out.println( "*   M M       M M    E             L             I        T           A   A       *" );
   System.out.println( "*   M  M     M  M    E             L             I        T          A     A      *" );
   System.out.println( "*   M   M   M   M    E             L             I        T         A       A     *" );
   System.out.println( "*   M    M M    M    EEEEEEEEEEE   L             I        T        A         A    *" );
   System.out.println( "*   M     M     M    E             L             I        T       AAAAAAAAAAAAA   *" );
   System.out.println( "*   M           M    E             L             I        T       A           A   *" );
   System.out.println( "*   M           M    E             L             I        T       A           A   *" );
   System.out.println( "*   M           M    E             L             I        T       A           A   *" );
   System.out.println( "*   M           M    EEEEEEEEEEE   LLLLLLLLLLL   I        T       A           A   *" );
   System.out.println( "*                                                                                 *" );
   System.out.println( "*                     Degenerate Primer Analysis Pipeline Suite                   *" );
   System.out.println( "* (C) 2013, 2020 Bioinformatics Research Group, Florida International University  *" );
   System.out.println( "*    Under MIT License From Open Source Initiative (OSI), All Rights Reserved.    *" );
   System.out.println( "*                                                                                 *" );
   System.out.println( "*    Any professionally published work using Melita should cite the following:    *" );
   System.out.println( "* M. Jaric, J. Segal, E. Silva-Herzong, L. Schneper, K. Mathee and G. Narasimhan. *" );
   System.out.println( "*               Better Primer Design for Metagenomics Applications                *" );
   System.out.println( "*                    By Increasing Taxonomic Distinguishability                   *" );
   System.out.println( "*                         BMC Proceedings 8(S7):S4, 2013.                         *" );
   System.out.println( "*                                                                                 *" );
   System.out.println( "*                           In memory of Melita Jaric.                            *" );
   System.out.println( "***********************************************************************************" );

    }

    public static HashMap<String, String> readKeywordValuePairs(String inputfile) throws IOException {
        File myFile = new File(inputfile);
        Scanner scan = new Scanner(myFile);
        HashMap<String, String> parameters = new HashMap<String, String>(); 
       
        while (scan.hasNextLine()) {
           String line = scan.nextLine();
           String[] contents = line.split("\t");
           parameters.put(contents[0], contents[1]);
        }

        return parameters;
    }

    public void toFile(String filename) throws IOException
    {
        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);

        wFile.println(toOutput);

        wFile.close();
    }

}

    /*
    public boolean hasDegMatchEnd(byte [] seq, int start)
    {
        boolean s15 = isDegMatch((byte)0x09,seq[start],0);
        boolean s16 = isDegMatch((byte)0x09,seq[start+1],0);
        boolean s17 = isDegMatch((byte)0x09,seq[start+2],0);
        boolean s18 = isDegMatch((byte)0x09,seq[start+3],0);
        boolean s19 = isDegMatch((byte)0x09,seq[start+4],0);
        boolean s20 = isDegMatch((byte)0x09,seq[start+5],0);
        boolean s21 = isDegMatch((byte)0x09,seq[start+6],0);

        if ((s17&s18) || (s18&s19) || (s19&s20) || (s20&s21) || (s18&s16&s15))
            return true;
        return false;
    }
    */

/*
    public void findDPBactRDP(String fPrimers, String fOutput, boolean isExact) throws FileNotFoundException, IOException
    {
        FileWriter oFile = new FileWriter(fOutput);
        PrintWriter wFile = new PrintWriter(oFile);
        toOutput = "Dataset: " + DB + "; numSeq: " + seqList.size();

        Scanner fileScan = new Scanner(new File(fPrimers));
        while (fileScan.hasNextLine()) // while not eof
        {
            String line = fileScan.nextLine();
            StringTokenizer tLine = new StringTokenizer(line);
            String cmd = tLine.nextToken();
            //System.out.println(cmd);

            boolean isUpper = true;
            if (cmd.equals("rgn"))
            {
                String rgn = tLine.nextToken();
                String pos = tLine.nextToken();
                if (rgn.compareTo("45") < 0) isUpper = false;
                toOutput += "\nREGION: V" + rgn + ";  pos: " + pos + ";  " + isUpper + "\n";
            }
            else
            {
                String inDP = line;
                if (isExact)    statDPExact(inDP);
                else
                {
                    findDPBestMatch(inDP,0); // must fix 0 here
                    createFirstDP(inDP, false);
                }
                wFile.println(toOutput);
                toOutput = "";
                primerList.clear();
            }
        }
       wFile.close();
    }
*/
