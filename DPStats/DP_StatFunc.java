
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class DP_StatFunc extends ByteFunc
{
    DB_IO DB;

    ArrayList<DP_Stats> dpStatsF;
    ArrayList<DP_Stats> dpStatsR; 
    TreeMap<Integer,ArrayList<DP_Stats>> dpPerRgnF;
    TreeMap<Integer,ArrayList<DP_Stats>> dpPerRgnR;

    HashMap<String,int []> PhylumXMap;
    HashMap<String,int []> GenusXMap;
    HashMap<String,int []> SpeciesXMap;
    String [] amplIDList;

    String idTest;

    ArrayList<AmplParms> amplStats;
   // Ampl_Stats [] amlpStats;

    public DP_StatFunc() {}

    public DP_StatFunc(String sDB, boolean is35, String idDataSet, String filename) throws FileNotFoundException
    {
        DB = new DB_IO(sDB, is35, filename);    // setup DB
        idTest = sDB + "_" + idDataSet;

        dpStatsF = new ArrayList<DP_Stats>();
        dpStatsR = new ArrayList<DP_Stats>();
        dpPerRgnF = new TreeMap<Integer,ArrayList<DP_Stats>>();
        dpPerRgnR = new TreeMap<Integer,ArrayList<DP_Stats>>();

        amplStats = new ArrayList<AmplParms> ();
    }

    public void addDP (String filename, boolean startNew) throws FileNotFoundException
    {
        addDP(filename, startNew, false);
    }

    public void addDP (String filename, boolean startNew, boolean addReverse) throws FileNotFoundException
    {
        if (startNew) { dpStatsF.clear(); dpStatsR.clear(); }
        String FR = filename.contains("-R") ? "R" : "F";

        Scanner fileScan = new Scanner(new File(filename));
        //System.out.println("Reading in Primers");

        while (fileScan.hasNextLine()) // while not eof
        {
            String line = fileScan.nextLine();
            String id = "";
            int indx = line.indexOf(";");
            if (indx > 0)
            {
                id = line.substring(0,indx);
                line = line.substring(indx+1);
            }
            byte [] b = DegString2DegByte(line.trim());
            addDP(b, FR, id);
            if (addReverse) addDP(revCompl(b), FR, id);
            //addDP(revCompl(b), FR);
        }
    }

    public void addDP (ArrayList<DP_Parms> dpList)
    {
        for (DP_Parms dpP: dpList)
            addDP(dpP.DP,dpP.FR,"");
    }

    public void addDP (byte [] in, String FR, String id)
    {
        if (FR.equalsIgnoreCase("F"))
            dpStatsF.add(new DP_Stats(in,FR,id));
        else
            dpStatsR.add(new DP_Stats(in,FR,id));
    }

    
    public void sortPerRgn() throws IOException
    {
        sortPerRgn(dpStatsF, dpPerRgnF);
        sortPerRgn(dpStatsR, dpPerRgnR);
    }

    public void sortPerRgn(ArrayList<DP_Stats> dpStats,
            TreeMap<Integer,ArrayList<DP_Stats>> dpPerRgn) throws IOException
    {
        System.out.println("SortPerRegion");
        int posRgn = -CONSTANT.maxLengthPrimer;
        String rgnID = "";
        TreeSet<DP_Stats> PQ = new TreeSet<DP_Stats>();
        for (DP_Stats dpS: dpStats)
        {
            int posMid = dpS.dpDB.get("RDP").posEcoli;
            //if ((Math.abs(posMid - posRgn) >= CONSTANT.minLengthPrimer))
            //if (posMid != posRgn)
            if ((Math.abs(posMid - posRgn) >= 5))
            {

                if (!PQ.isEmpty())
                {
                    dpPerRgn.get(posRgn).addAll(PQ);
                    PQ.clear();
                }

                //ArrayList<DP_Stats> a = new ArrayList<DP_Stats>();
                //while (!PQ.isEmpty()) a.add(PQ.poll());
                //if (!a.isEmpty()) dpPerRgn.put(posRgn, a);
                posRgn = posMid;
                dpPerRgn.put(posRgn, new ArrayList<DP_Stats>());
                
                
            }
            PQ.add(dpS);
            PrintWriter wFile = new PrintWriter(new FileWriter("DesignFlow.txt"));
            for (DP_Stats dpX: PQ)
                wFile.println(dpX.toString());
            wFile.println("***");
        }
        dpPerRgn.get(posRgn).addAll(PQ);
        PQ.clear();
    }

    public void matchFrwdRevPrimers(String dir) throws IOException
    {
        PrintWriter wFile = new PrintWriter(new FileWriter(dir+"PairPerRgn.txt"));
        System.out.println("matchFrwdREvPrimers");
        for (Entry<Integer,ArrayList<DP_Stats>> eF: dpPerRgnF.entrySet())
        {
            for (Entry<Integer,ArrayList<DP_Stats>> eR: dpPerRgnR.entrySet())
            {
                int aL = eR.getKey()-eF.getKey();
                //if ((aL > CONSTANT.minAmplLength) && (aL < CONSTANT.maxAmplLength))
                if ((aL > 100) && (aL < 1700))
                {
                
                    ArrayList<DP_Stats> fSList = eF.getValue();
                    ArrayList<DP_Stats> rSList = eR.getValue();
                    boolean found = false;
                    int fI = 0; int fIndx = 0; int fSize = fSList.size();
                    int rI = 0; int rIndx = 0; int rSize = rSList.size();
                    int cI = 0; int cIndx = 0;
                    //for (int fIndx=0; fIndx<fSList.size(); fIndx++)
                    while (fIndx<fSize)
                    {
                        //boolean nextF = false;
                        DP_Stats fS = fSList.get(fIndx);
                        DP_Stats rS = rSList.get(rIndx);
                        while (fS.dpP.isCompatible(rS.dpP) > 0)
                        {
                            rIndx++; if (rIndx == rSize) break;
                            rS = rSList.get(rIndx);
                        }
                        if ((eR.getKey() == 1006) && (eF.getKey() == 480))
                        System.out.println(fIndx + " " + rIndx + " " + 
                                fS.dpP.TmP.toStringTm(",") + " " + rS.dpP.TmP.toStringTm(","));

                        if ((fIndx == 0) && (rIndx == 0))
                        {
                            found = true;
                            break;
                        }
                        else if (rIndx == rSize)
                        {
                            fIndx++;
                            rIndx = 0;
                        }
                        else if (found)
                        {
                            cIndx = Math.min(fS.dpDB.get(CONSTANT.refDB).cntUniq,
                                             rS.dpDB.get(CONSTANT.refDB).cntUniq);
                            if ((rIndx >= rI) || (cIndx == cI))
                                break;
                            else if (cIndx > cI)
                            {
                                rI = rIndx;
                                fI = fIndx;
                                cI = cIndx;
                                break;
                            }
                            else
                            {
                                fIndx++;
                                rIndx = 0;
                            }
                        }
                        else
                        {
                            rI = rIndx;
                            fI = fIndx;
                            cI = Math.min(fS.dpDB.get(CONSTANT.refDB).cntUniq,
                                          rS.dpDB.get(CONSTANT.refDB).cntUniq);
                            fIndx++;
                            rIndx = 0;
                            found = true;
                        }
                    }
                    if ((eR.getKey() == 1006) && (eF.getKey() == 480))
                        System.out.println(fI + " " + rI);
                    if (found)
                    {
                        amplStats.add(new AmplParms(fSList.get(fI), rSList.get(rI), ""));
                        wFile.println(fSList.get(fI)+" "+rSList.get(rI) + "F"+eF.getKey()+"-R"+eR.getKey());
                    }
                    else
                    {
                        System.out.println("NO amplicons for: " + eF.getKey() + ", "+ eR.getKey()
                       + " " + fSList.get(0).dpP.isCompatible(rSList.get(0).dpP));
                    }
                }
            }
        }
        wFile.close();
    }

    public void statDPExact()
    {
        for (DP_Stats dpS: dpStatsF)
            dpS.addDB(statDPExact(dpS.dpP.DP,true),DB.DB);
        for (DP_Stats dpS: dpStatsR)
            dpS.addDB(statDPExact(dpS.dpP.DP,false),DB.DB);
    }

    public void statDPExact(int i)
    {
        for (AmplParms aP: amplStats)
        {
            aP.fP.addDB(statDPExact(aP.fP.dpP.DP,true),DB.DB);
            aP.rP.addDB(statDPExact(aP.rP.dpP.DP,false),DB.DB);
        }
    }


    public DP_StatDB statDPExact(byte [] inDP, boolean isF)
    {
        //String oneDP = String.format("%25s", inDP) + "\n";
        //System.out.println(toDegString(inDP));
        int notMatch = 0; int cntSeq = 0; int cntMult = 0; int cntUniq = 0;
        int posMin = 2000; int posMax = -1; int posMid = 0;
        int Ecoli = -1; int Paerg = -1;

        for (DB_SeqParms s: DB.seqList.values())
        {
            // find
            DP_inSeq dp = s.ExactSeqDPMatch(inDP);
            //toOutput += toDegString(dp,0,dp.length) + "\n";
            cntSeq++;

            if      (dp.nErrors > 0)  notMatch++;
            else if (dp.cntInSeq > 1) cntMult++;
            else
            {
                cntUniq++;
                posMid += dp.posSeq;
                if      (dp.posSeq < posMin) posMin = dp.posSeq;
                else if (dp.posSeq > posMax) posMax = dp.posSeq;

                if (DB.DB.equals("RDP"))
                {
                    //statPhyloMap.get(inDP).add(s.seq.fullHier());

                    if (s.ID.equals("S000010427"))
                        Paerg = dp.posSeq;
                    else if (s.ID.equals("S000004313"))
                        Ecoli = dp.posSeq;
                }
            }
        }
        if (cntUniq != 0) posMid = posMid/cntUniq;
        if (isF) Ecoli += inDP.length;
        return new DP_StatDB(Paerg,Ecoli,posMin,posMax,posMid,cntUniq,cntMult,notMatch);
        //oneDP += String.format(" seqMatch = %3d", (cntSeq - notMatch)) + ";";
        //oneDP += String.format(" multHits = %3d", cntMult) + ";";
        //oneDP += String.format(" deg = %3d", findDegSeq(byteDP)) + ";";
        //oneDP += " Tm: " + getTm(byteDP,0,byteDP.length) + ";";
        //oneDP += " sDimer: " + selfDimer(byteDP) + ";";
        //if (DB.equals("RDP"))
        //{
        //    oneDP += " Ecoli: " + Ecoli + " to " + (Ecoli+inDP.length()-1) + ";";
        //    oneDP += " Paerg: " + Paerg + " to " + (Paerg+inDP.length()-1) + ";";
        //}
        //oneDP += "\n";

        //toOutput += oneDP;
        //System.out.println(oneDP);
    }


    public void addAmpl(String filename, boolean startNew, boolean revR) throws FileNotFoundException
    {
        if (startNew) { amplStats.clear(); }

        Scanner fileScan = new Scanner(new File(filename));
        //System.out.println("Reading in Primers for Amplicons");

        while (fileScan.hasNextLine()) // while not eof
        {
            String line = fileScan.nextLine().trim();
            String [] primers = line.split(" ");
            String amplID = "";
            if (primers.length == 3) amplID = primers[2];
            //System.out.println("ID " + amplID);

            byte [] pF = DegString2DegByte(primers[0]);
            byte [] pR = DegString2DegByte(primers[1]);

            if (revR)
                amplStats.add(new AmplParms(new DP_Stats(pF, "F", ""),
                      new DP_Stats(revCompl(pR), "R", ""), amplID));
            else
                amplStats.add(new AmplParms(new DP_Stats(pF, "F", ""),
                      new DP_Stats(pR, "R", ""), amplID));
           
            //addDP(revCompl(b), FR);
        }
    }



    public void toAmplSeqFile(String dir, boolean toFile) throws IOException
    {
        DB.createGenusList(false);
        DB.createSpeciesList(false);
        DB.createPhylumList(false);

        int nAmpl = amplStats.size();
        //System.out.println("nAmpl = " + nAmpl);
        amplIDList = new String [nAmpl];

        PhylumXMap = new HashMap<String, int []> ();
        GenusXMap = new HashMap<String, int []> ();
        SpeciesXMap = new HashMap<String, int []> ();

        for (String s: DB.phylumList.keySet())
        {
            PhylumXMap.put(s, new int [nAmpl]);
            for (int i=0; i<nAmpl; i++) PhylumXMap.get(s)[i] = 0;
        }
        for (String s: DB.genusList.keySet())
        {
            GenusXMap.put(s, new int [nAmpl]);
            for (int i=0; i<nAmpl; i++) GenusXMap.get(s)[i] = 0;
        }
        for (String s: DB.speciesList.keySet())
        {
            SpeciesXMap.put(s, new int [nAmpl]);
            for (int i=0; i<nAmpl; i++) SpeciesXMap.get(s)[i] = 0;
        }

        int indx = 0;
        while(indx < amplStats.size())
        {
            DB.rstTaxonLists();
            AmplParms p = amplStats.get(indx);
            if (!findAmplicons(p, indx, dir, toFile)) amplStats.remove(indx);
            else indx++;
        }
    }

    public boolean findAmplicons(AmplParms ampl, int indx, String dir, boolean toFile) throws IOException
    {

        HashMap<String,ArrayList<String>> uniqAmpl = new HashMap<String,ArrayList<String>>();
        for (DB_SeqParms s: DB.seqList.values())
        {
            String header = s.ID + "|" + s.phylHier + ";" + s.source;
            
            // Find Frwd and check
            DP_inSeq dpF = s.ExactSeqDPMatch(ampl.fP.dpP.DP);
            if ((dpF.nErrors > 0) || (dpF.cntInSeq > 1)) continue;

            // Find Rev and check
            DP_inSeq dpR = s.ExactSeqDPMatch(ampl.rP.dpP.DP);
            if ((dpR.nErrors > 0) || (dpR.cntInSeq > 1)) continue;

            // check
            int amplL = dpR.posSeq +  ampl.rP.dpP.dpLength - dpF.posSeq;
            if (amplL < CONSTANT.minAmplLength || amplL > CONSTANT.maxAmplLength)
                continue;

            byte [] amplByte = new byte [amplL];
            System.arraycopy(s.data, dpF.posSeq, amplByte, 0, amplL);
            String amplSeq = toDegString(amplByte);
            
            if (!uniqAmpl.containsKey(amplSeq))
                uniqAmpl.put(amplSeq, new ArrayList<String>());
            uniqAmpl.get(amplSeq).add(header);
        }
        //System.out.println(uniqAmpl.size());
        if (uniqAmpl.size() < 100) return false;
        amplIDList[indx] = ampl.getID(false);
        amplToFile(dir, "", ampl, uniqAmpl, indx, toFile);
        return true;
    }

    
    private void amplToFile(String dir, String fileID, AmplParms ampl,
            HashMap<String,ArrayList<String>> uniqAmpl, int indx, boolean toFile) throws IOException
    {
        
        String filename = dir + DB.DB + "_" + ampl.getID(false) +
                "_amplSeqID" + fileID + ".txt";
        PrintWriter wFile = new PrintWriter(new FileWriter(filename));

        int uniqSpecies = 0;
        int uniqGenus = 0;
        int uniqPhylum = 0;
        int cntAmpl = 0;
        int cntGenus = 0;
        int cntPhylum = 0;
        TreeMap<Integer,Integer> lengthHist = new TreeMap<Integer,Integer>();
        ampl.addDB(DB.DB);
        for (Entry<String,ArrayList<String>> e: uniqAmpl.entrySet())
        {
            int l = e.getKey().length();
            if (!lengthHist.containsKey(l))
                lengthHist.put(l,0);
            lengthHist.put(l, lengthHist.get(l)+1);

            ArrayList<String> h = e.getValue();
            int sz = h.size();
            //System.out.println(">" + sz);
            cntAmpl += sz;
            wFile.println(">" + sz);

            if (h.size()==1) 
            {
                uniqSpecies++;
                ampl.addUniqTax(DB.DB, h.get(0));
                //System.out.println(uniqSpecies + " " + h.get(0));
                wFile.println(h.get(0));
                String h1 [] = extractTaxons(h.get(0));
                // System.out.println(h1[0] + " " + h1[1] + " " + h1[2]);
                //if (!DB.genusList.containsKey(h1))
                //    System.out.println("Why is it not there: " + h1);
                storeTaxons(h1,3);
            }
            
            else
            {
                boolean isUniqGenus = true;
                boolean isUniqPhylum = true;
                int x = 0;
                String h1 [] = extractTaxons(h.get(0));
                storeTaxons(h1,x);
                String sout = "> " + sz + "\n";
                for (String s: h)
                {
                    String [] h2 = extractTaxons(s); // System.out.println(h2);
                    storeTaxons(h2,x);
                    if (!h1[1].equalsIgnoreCase(h2[1])) 
                        isUniqGenus = false;
                    if (!h1[0].equalsIgnoreCase(h2[0])) 
                        isUniqPhylum = false;
                    wFile.println(s);

                }
                if (isUniqPhylum) { cntPhylum += sz; uniqPhylum++; x=1;}
                if (isUniqGenus)  { cntGenus  += sz; uniqGenus++;  x=2;}
                storeTaxons(h1,x);
            }
          
            /*
            if (uniqSpecies <= 10)
                {
                    int [] ttS = taxonDiffampl();
                    System.out.print("TaxonInfo: ");
                    for (int i=0; i<6; i++)
                        System.out.print(ttS[i] + " ");
                    System.out.println();
                }
                */
        }

        //ampl.addDBStat(DB.DB, cntAmpl, uniqSource, uniqSource + uniqGenus);
        int [] tS = taxonDiffampl(indx);
        String sp = " ";
        //System.out.println(tS[0] + sp + tS[1] + sp + tS[2] + sp +
        //        tS[3] + sp + tS[4] + sp + tS[5]);
        ampl.addDBStat(DB.DB, cntAmpl, tS[0],tS[1],tS[2],tS[3],tS[4],tS[5]);
        ampl.addDBLength(DB.DB, lengthHist);
        wFile.close();
        missTaxonToFile(filename, ampl);
    }


    private String [] extractTaxons(String h)
    {
        String [] t = new String [3];
        //String g = h.substring(h.indexOf("|")+1,h.lastIndexOf(";"));
        String ht = h.substring(h.indexOf("|")+1);
        String [] aHT = ht.split(";");
        t[0] = aHT[1].trim();
        t[1] = aHT[aHT.length-2].trim();
        t[2] = aHT[aHT.length-1].trim();
        return t;
    }

    private void storeTaxons(String [] h1, int x )
    {
        //String s = " ";
        //System.out.println(x + s + h1[0] + s + h1[1] + s + h1[2]);
        if (x==3)
        {
            DB.phylumList.put(h1[0], 1);
            DB.genusList.put(h1[1], 1);
            DB.speciesList.put(h1[2],1);
        }
        else if (x==2)
        {
            DB.phylumList.put(h1[0], 1);
            DB.genusList.put(h1[1], 1);
            if (DB.speciesList.get(h1[2]) != 1)
                DB.speciesList.put(h1[2], 2);
        }
        else if (x==1)
        {
            DB.phylumList.put(h1[0], 1);
            if (DB.speciesList.get(h1[2]) != 1)
                DB.speciesList.put(h1[2], 2);
            if (DB.genusList.get(h1[1]) != 1)
                DB.genusList.put(h1[1], 2);
        }
        else
        {
            if (DB.speciesList.get(h1[2]) != 1)
                DB.speciesList.put(h1[2], 2);
            if (DB.genusList.get(h1[1]) != 1)
                DB.genusList.put(h1[1], 2);
            if (DB.phylumList.get(h1[0]) != 1)
                DB.phylumList.put(h1[0], 2);
        }

    }

    private int [] taxonDiffampl(int indx)
    {
        int [] x = new int [6];
        for (int i=0; i<6; i++) x[i]=0;

        int l1 = 0; int l2 = 1;
        for (Entry<String,Integer> e: DB.phylumList.entrySet())
        {
            PhylumXMap.get(e.getKey())[indx] = e.getValue();
            if (e.getValue()==1) { x[l1]++; x[l2]++;}
            else if (e.getValue()==2) x[l2]++;
        }

        l1=2; l2=3;
        for (Entry<String,Integer> e: DB.genusList.entrySet())
        {
            GenusXMap.get(e.getKey())[indx] = e.getValue();
            if (e.getValue()==1) { x[l1]++; x[l2]++;}
            else if (e.getValue()==2) x[l2]++;
        }

        l1=4; l2=5;
        for (Entry<String,Integer> e: DB.speciesList.entrySet())
        {
            SpeciesXMap.get(e.getKey())[indx] = e.getValue();
            if (e.getValue()==1) { x[l1]++; x[l2]++;}
            else if (e.getValue()==2) x[l2]++;
        }

        return x;
    }

    public void combineTwoAmpl(String filename) throws IOException
    {
        PrintWriter wFile = new PrintWriter(new FileWriter(filename));

        int nPP = amplStats.size();
        for (int i = 0; i < nPP-1; i++)
        {
            AmplParms cA = amplStats.get(i);
            for (int j = i+1; j < nPP; j++)
            {
                AmplParms nA = amplStats.get(j);
                //if (!cA.isOverlap(nA, CONSTANT.refDB))
                {
                    String fileID = "";
                    String mGfilename = "amplSeqPaper/" + cA.getID(false) + "_" + nA.getID(false)
                            + "_missGenus" + idTest + fileID + ".txt";
                    PrintWriter missFile = new PrintWriter(new FileWriter(mGfilename));
                    wFile.println(cA.combineTwoAmpl(nA, missFile));
                    missFile.close();
                }
            }
        }
        wFile.close();
    }

    public void combineTwoAmpl2(String filename) throws IOException
    {
        //System.out.println("CombineTwoAmplicons");
        PrintWriter wFile = new PrintWriter(new FileWriter(filename));

        String header = "PrimerPair_1\t" + "PrimerPair_2\t"+ "OverlapID\t"+
                "DiffSpecies1\tDiffSPecies2\tDiffSpeciesC\t" +
                "DiffGenus1\tDiffGenus2\tDiffGenusC\t" +
                "DiffPhylum1\tDiffPhylum2\tDiffPhylumC\t" +
                "TotalSpecies1\tTotalSPecies2\tTotalSpeciesC\t" +
                "TotalGenus1\tTotalGenus2\tTotalGenusC\t" +
                "TotalPhylum1\tTotalPhylum2\tTotalPhylumC\t";

        wFile.println(header);
        int nPP = amplStats.size();
        for (int i = 0; i < nPP-1; i++)
        {
            AmplParms cA = amplStats.get(i);
            for (int j = i+1; j < nPP; j++)
            {
                AmplParms nA = amplStats.get(j);
                int overlapID = cA.isOverlap(nA, CONSTANT.refDB);
                
                    //System.out.println(cA.getID(false) + " " + nA.getID(false));
                    int [] indx = {i,j}; //indx[0] = i; indx[1] = j;
                    int [] aph = combNampl(indx, PhylumXMap);
                    int [] agn = combNampl(indx, GenusXMap);
                    int [] asp = combNampl(indx, SpeciesXMap);
                    String s = cA.getID(false)+"\t"+nA.getID(false)+"\t"+overlapID+"\t";
                    for (int k=0; k<3; k++) s += asp[k] + "\t";
                    for (int k=0; k<3; k++) s += agn[k] + "\t";
                    for (int k=0; k<3; k++) s += aph[k] + "\t";
                    for (int k=3; k<6; k++) s += asp[k] + "\t";
                    for (int k=3; k<6; k++) s += agn[k] + "\t";
                    for (int k=3; k<6; k++) s += aph[k] + "\t";
                    wFile.println(s);
                

            }
        }
        wFile.close();
    }

    private int [] combNampl(int [] indx, HashMap<String, int []> M)
    {
        int nAmpl = indx.length;
        int sTotal = nAmpl+1;
        int sz = 2*sTotal;
        int [] x = new int[2*sTotal];
        for (int i=0; i<sz; i++) x[i]=0;
        for (Entry<String, int []> e: M.entrySet())
        {
            int total = 0;
            int diff = 0;
            int [] a = e.getValue();
            for (int i=0; i<nAmpl; i++)
            {
                if (a[indx[i]] == 1)
                {
                    x[i] += 1;        diff = 1;
                    x[sTotal+i] += 1; total = 1;
                }
                else if (a[indx[i]] != 0)
                {
                    x[i] += 0;
                    x[sTotal+i] += 1; total = 1;
                }
               
                //int addTotal = (a[indx[i]] != 0) || (a[indx2] != 0) ? 1 : 0;
                //int addDiff  = (a[indx[i]] == 1) || (a[indx2] == 1) ? 1 : 0;
            }
            x[nAmpl] += diff;
            x[sTotal+nAmpl] += total;
        }
        return x;
    }

    public void toStatFile(String filename, String dlmt) throws IOException
    {
        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);

        for (DP_Stats dpS: dpStatsF)
            wFile.print(dpS.toString(dlmt));
        for (DP_Stats dpS: dpStatsR)
            wFile.print(dpS.toString(dlmt));

        wFile.close();
    }

    public void printBarcode(String filename) throws IOException
    {
        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);

        boolean both = false;
        String bc = "";
        int cnt = 0;
        int totalCnt = 0;
        for (DP_Stats dpS: dpStatsF)
        {
            if (both)
            {
                cnt = (dpS.dpDB.get("RDP").cntUniq + dpS.dpDB.get("RDP").cntMult);
                totalCnt += cnt;
                bc += cnt + ";" + totalCnt;
                both = false;
                wFile.println(bc);
            }
            else
            {
                cnt = (dpS.dpDB.get("RDP").cntUniq + dpS.dpDB.get("RDP").cntMult);
                totalCnt = cnt;
                bc = dpS.dpP.ID + ";" + toDegString(dpS.dpP.DP) + ";" + cnt + ";";
                both = true;
            }
        }

        wFile.close();
    }



    public void toStatRgnFile(String filename, String dlmt) throws IOException
    {
        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);

        for (Entry<Integer,ArrayList<DP_Stats>> e: dpPerRgnF.entrySet())
        {
            wFile.println("F" + e.getKey().toString());
            for (DP_Stats dpS: e.getValue())
                wFile.println(dpS.toString(dlmt));
        }
        for (Entry<Integer,ArrayList<DP_Stats>> e: dpPerRgnR.entrySet())
        {
            wFile.println("R" + e.getKey().toString());
            for (DP_Stats dpS: e.getValue())
                wFile.println(dpS.toString(dlmt));
        }

        wFile.close();
    }

    public void toPrintAmplPrimers(String dlmt) throws IOException
    {
        for (AmplParms a: amplStats)
        {

            System.out.print(a.ID + " " + a.fP.toString(dlmt));
            System.out.println(a.ID + " " + a.rP.toString(dlmt));
        }
    }

    public void toAmplStatFile(String filename, String dlmt) throws IOException
    {
        PrintWriter wFile1 = new PrintWriter(new FileWriter(filename));
        String file2 = filename.replaceAll("amplStats_", "amplStatsNoFR_");
        PrintWriter wFile2 = new PrintWriter(new FileWriter(file2));

        for (AmplParms a: amplStats)
        {
            wFile1.println(a.toString(dlmt, false));
            wFile2.print(a.toShortString(dlmt, true));
        }

        wFile1.close();
        wFile2.close();
    }


    public void findDPRgns()
    {
        findDPRgns(dpStatsF);
        findDPRgns(dpStatsR);
    }

    public void findDPRgns(ArrayList<DP_Stats> dpStats)
    {
        ArrayList<Integer> idRgn = new ArrayList<Integer>();
        int posRgn = -CONSTANT.maxLengthPrimer;
        for (DP_Stats dpS: dpStats)
        {
            int posMid = dpS.dpDB.get("RDP").posMid;
            if (Math.abs(posMid - posRgn) >= CONSTANT.maxLengthPrimer)
            {
                System.out.println(posMid);
                idRgn.add(posMid);
                posRgn = posMid;
            }
        }
    }

    
    private void missTaxonToFile(String filename, AmplParms ampl) throws IOException
    {

        //System.out.println("TaxonList sizes: " +
        //        DB.phylumList.size() + " " + DB.genusList.size() + " " + DB.speciesList.size());

        String gfileNE = filename.replace("_amplSeqID", "_GenusNE");
        String gfileND = filename.replace("_amplSeqID", "_GenusND");
        //System.out.println(gfilename);
        PrintWriter gFileNE = new PrintWriter(new FileWriter(gfileNE));
        PrintWriter gFileND = new PrintWriter(new FileWriter(gfileND));
        for (Entry<String,Integer> e: DB.genusList.entrySet())
        {
            if (e.getValue() != 1) gFileND.println(e.getKey());
            if (e.getValue() == 0) gFileNE.println(e.getKey());
        }
        gFileNE.close();
        gFileND.close();

        String phfileNE = filename.replace( "_amplSeqID", "_PhylumNE");
        String phfileND = filename.replace( "_amplSeqID", "_PhylumND");
        //System.out.println(phfilename);
        PrintWriter phFileND = new PrintWriter(new FileWriter(phfileND));
        PrintWriter phFileNE = new PrintWriter(new FileWriter(phfileNE));
        for (Entry<String,Integer> e: DB.phylumList.entrySet())
        {
            
            if (e.getValue() == 0) phFileNE.println(e.getKey());
            else if (e.getValue() != 1) phFileND.println(e.getKey());
        }
        phFileNE.close();
        phFileND.close();

        String sfileNE = filename.replace( "_amplSeqID", "_SpeciesNE");
        String sfileND = filename.replace( "_amplSeqID", "_SpeciesND");
        //System.out.println(sfilename);
        PrintWriter sFileND = new PrintWriter(new FileWriter(sfileND));
        PrintWriter sFileNE = new PrintWriter(new FileWriter(sfileNE));
        for (Entry<String,Integer> e: DB.speciesList.entrySet())
        {
            if (e.getValue() != 1) sFileND.println(e.getKey());
            if (e.getValue() == 0) sFileNE.println(e.getKey());
        }
        sFileNE.close();
        sFileND.close();

    }

    public void printTaxonMap(String dir) throws IOException
    {
        String header = "Taxon/Amplicon\t";
        for (int i=0; i<amplIDList.length; i++)
                header += amplIDList[i] + "\t";
               

        String phFile = dir + "PhylumAmpliconMap_" + idTest + ".txt";
        PrintWriter phF = new PrintWriter(new FileWriter(phFile));
        phF.println(header);
        for (Entry<String, int []> e: PhylumXMap.entrySet())
        {
            int [] a = e.getValue();
            int n = a.length;
            String s = e.getKey() + "\t";
            for (int i=0; i<n; i++) s += a[i] + "\t";
            phF.println(s);
        }
        phF.close();

        String gFile = dir + "GenusAmpliconMap_" + idTest + ".txt";
        PrintWriter gF = new PrintWriter(new FileWriter(gFile));
        gF.println(header);
        for (Entry<String, int []> e: GenusXMap.entrySet())
        {
            int [] a = e.getValue();
            int n = a.length;
            String s = e.getKey() + "\t";
            for (int i=0; i<n; i++) s += a[i] + "\t";
            gF.println(s);
        }
        gF.close();

        String spFile = dir + "SpeciesAmpliconMap_" + idTest + ".txt";
        PrintWriter spF = new PrintWriter(new FileWriter(spFile));
        spF.println(header);
        for (Entry<String, int []> e: SpeciesXMap.entrySet())
        {
            int [] a = e.getValue();
            int n = a.length;
            String s = e.getKey() + "\t";
            for (int i=0; i<n; i++) s += a[i] + "\t";
            spF.println(s);
        }
        spF.close();
    }

    public void printTaxonsNew (String dir, int nOld) throws IOException
    {
        String header = "Taxon/Amplicon\t";
        for (int i=0; i<amplIDList.length; i++)
                header += amplIDList[i] + "\t";


        String phFile = dir + "PhylumNew_" + idTest + ".txt";
        PrintWriter phF = new PrintWriter(new FileWriter(phFile));
        phF.println(header);
        for (Entry<String, int []> e: PhylumXMap.entrySet())
        {
            int [] a = e.getValue();
            int n = a.length;
            String s = e.getKey() + "\t";
            boolean diffOld = false;
            boolean hasOld = false;
            boolean diffNew = false;
            boolean hasNew = false;
            for (int i=0; i<nOld; i++)
            {
                if (a[i] == 1) { hasOld = true; diffOld = true;}
                if (a[i] == 2) { hasOld = true;}
            }
            for (int i=nOld; i<n; i++)
            {
                if (a[i] == 1) { hasNew = true; diffNew = true;}
                if (a[i] == 2) { hasNew = true;}
            }
            int idFound = 0;
            if (diffNew & !diffOld) idFound = 3;
            else if (diffNew & !hasOld) idFound = 2;
            else if (!diffNew & diffOld) idFound = 1;
            if (diffNew & !hasOld)
            {
                //System.out.println("Phylum: " + e.getKey());
                phF.println(e.getKey() + "\t" + 11);
            }
            else if (diffNew & hasOld & !diffOld)
                phF.println(e.getKey() + "\t" + 22);
            else if (diffOld & !hasNew)
                phF.println(e.getKey() + "\t" + 33);
            else if (diffOld & hasNew & !diffNew)
                phF.println(e.getKey() + "\t" + 44);
        }
        phF.close();

        String gFile = dir + "GenusNew_" + idTest + ".txt";
        PrintWriter gF = new PrintWriter(new FileWriter(gFile));
        gF.println(header);
        for (Entry<String, int []> e: GenusXMap.entrySet())
        {
            int [] a = e.getValue();
            int n = a.length;
            String s = e.getKey() + "\t";
            boolean diffOld = false;
            boolean hasOld = false;
            boolean diffNew = false;
            boolean hasNew = false;
            for (int i=0; i<nOld; i++)
            {
                if (a[i] == 1) { hasOld = true; diffOld = true;}
                if (a[i] == 2) { hasOld = true;}
            }
            for (int i=nOld; i<n; i++)
            {
                if (a[i] == 1) { hasNew = true; diffNew = true;}
                if (a[i] == 2) { hasNew = true;}
            }
            int idFound = 0;
            if (diffNew & !diffOld) idFound = 3;
            else if (diffNew & !hasOld) idFound = 2;
            else if (!diffNew & diffOld) idFound = 1;
            if (diffNew & !hasOld)
             {
                //System.out.println("Genus: " + e.getKey());
                gF.println(e.getKey() + "\t" + 11);
            }
            else if (diffNew & hasOld & !diffOld)
                gF.println(e.getKey() + "\t" + 22);
            else if (diffOld & !hasNew)
                gF.println(e.getKey() + "\t33\t" + a[0] + a[1] + a[2] + a[3]);
            else if (diffOld & hasNew & !diffNew)
                gF.println(e.getKey() + "\t44\t" + a[0] + a[1] + a[2] + a[3]);
        }
        gF.close();

        String spFile = dir + "SpeciesNew_" + idTest + ".txt";
        PrintWriter spF = new PrintWriter(new FileWriter(spFile));
        spF.println(header);
        for (Entry<String, int []> e: SpeciesXMap.entrySet())
        {
            int [] a = e.getValue();
            int n = a.length;
            String s = e.getKey() + "\t";
            boolean diffOld = false;
            boolean hasOld = false;
            boolean diffNew = false;
            boolean hasNew = false;
            for (int i=0; i<nOld; i++)
            {
                if (a[i] == 1) { hasOld = true; diffOld = true;}
                if (a[i] == 2) { hasOld = true;}
            }
            for (int i=nOld; i<n; i++)
            {
                if (a[i] == 1) { hasNew = true; diffNew = true;}
                if (a[i] == 2) { hasNew = true;}
            }
            int idFound = 0;
            if (diffNew & !diffOld) idFound = 3;
            else if (diffNew & !hasOld) idFound = 2;
            else if (!diffNew & diffOld) idFound = 1;
            if (diffNew & !hasOld)
            {
                //System.out.println("Species: " + e.getKey());
                spF.println(e.getKey() + "\t11");
            }
            else if (diffNew & hasOld & !diffOld)
                spF.println(e.getKey() + "\t22");
            else if (diffOld & !hasNew)
                spF.println(e.getKey() + "\t33\t"+ a[0] + a[1] + a[2] + a[3]);
            else if (diffOld & hasNew & !diffNew)
                spF.println(e.getKey() + "\t44\t" + a[0] + a[1] + a[2] + a[3]);
        }
        spF.close();
    }


    public void findStartEndDiversity(String idRgn) throws IOException
    {
        if (idRgn.equals("s"))
        {
            for (AmplParms p: amplStats)
                findStartDiversity(p);
        }
    }

    public void findStartDiversity(AmplParms ampl) throws IOException
    {
        HashMap<String,ArrayList<String>> uniqAmpl = new HashMap<String,ArrayList<String>>();

        for (DB_SeqParms s: DB.seqList.values())
        {
            String header = s.ID + "|" + s.phylHier + ";" + s.source;

         // Find Rev and check
            DP_inSeq dpR = s.ExactSeqDPMatch(ampl.rP.dpP.DP);
            if ((dpR.nErrors > 0) || (dpR.cntInSeq > 1)) continue;

            int amplL = dpR.posSeq + ampl.rP.dpP.dpLength;
            byte [] amplByte = new byte [amplL];
            System.arraycopy(s.data, 0, amplByte, 0, amplL);
            String amplSeq = toDegString(amplByte);

            if (!uniqAmpl.containsKey(amplSeq))
            {
                uniqAmpl.put(amplSeq, new ArrayList<String>());
                uniqAmpl.get(amplSeq).add(header);
            }
            else
            {
                boolean found = false;
                for (String sh: uniqAmpl.get(amplSeq))
                    if (!sh.equals(header)) { found = true; break; }
                if (!found) uniqAmpl.get(amplSeq).add(header);
            }
        }
        amplToFile("","_SD", ampl, uniqAmpl, 0, false);

    }


   /*
    public void DB_String2Int(String DB)
    {
        if      (DB.equals("RDP"))   idDB =  0;
        else if (DB.equals("HMDP"))  idDB =  1;
        else if (DB.equals("Silva")) idDB =  2;
        else                         idDB = -1;
    }
*/
}
