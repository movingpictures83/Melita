
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */

public class DP_doClst extends ByteFunc
{
    private class ClstObj implements Comparable<ClstObj>
    {
        int pos;
        byte dna;
        int cnt;
        double degChange;
        //boolean isDegChange;

        public ClstObj(int pos, byte crntDNA, int addDNA, int cnt)
        {
            this.pos = pos;  
            this.dna = (byte) ((crntDNA | addDNA) & 0xFF);
            this.cnt = cnt;
            //isDegChange = !isDegMatch(crntDNA,(byte)addDNA,0);
            //System.out.println("NewDeg: " + (double)findDegByte(dna) + " "+ findDegByte(crntDNA) + " " + isDegChange);
            degChange = ((double)findDegByte(dna))/findDegByte(crntDNA);
            //System.out.println(degChange);
            //if (crntDNA == 0x00)
            //        degChange = (double)findDegByte((byte)addDNA);
            //else    degChange = (double)findDegByte(dna)/findDegByte(crntDNA);
        }

        public void printString()
        {
            System.out.println(pos + " " + toDegString(dna) + " " + cnt + " " + degChange);
        }

        public int compareTo(ClstObj C)
        {
            if (cnt == C.cnt)
            {
                // find deg change
                if (degChange < C.degChange) return -1;
                else                         return  1;
            }
            else
            {
                if (cnt < C.cnt) return  1;
                else             return -1;
            }
        }
    }

    String orgPrimer;
    byte [] degPrimer;
    byte [] clstPrimer;
    int clstSeqCnt;
    HashMap <String, Integer> primers;
    ArrayList<DP_Parms> clstPrimerList;
    //HashMap<String,ClstObj> clstDS = new HashMap<String,ClstObj>();

    // work space
    HashMap <String, Integer> pSet = new HashMap <String, Integer> ();
    int [] [] freqM;
    int nRow;
    int nCol;
    String toOutput;
    int isConserved;
    int hasPrimer;
    int primerLength;
    int nClstPrimers;
    
    public DP_doClst(String p)
    {
        orgPrimer = p;
        setup();
    }

    public DP_doClst(HashMap<String, Integer> pList)
    {
        addPrimers(pList);
        System.out.println("SecondCluster size = " + pList.size());
        setup();
        DegString2DegByte(degPrimer, orgPrimer, orgPrimer.length());
    }

    private void setup()
    {
        degPrimer = new byte [orgPrimer.length()];
        Arrays.fill(degPrimer,(byte)0x00);
        clstPrimer = new byte [orgPrimer.length()];
        Arrays.fill(clstPrimer,(byte)0x00);

        primers = new HashMap <String, Integer> ();
        clstPrimerList = new ArrayList<DP_Parms> ();
        clstSeqCnt = 0;
        toOutput = "";
    }

    public void addPrimers(HashMap<String,Integer> pL)
    {
        int maxCnt = 0;
        orgPrimer = "";
        for(Entry<String,Integer> e: pL.entrySet())
        {
            int val = e.getValue().intValue();
            String key = e.getKey();
            if (val > maxCnt)
            {
                maxCnt = val;
                orgPrimer = key;
            }
            primers.put(key,val);
        }
    }

    public void findMaxCnt()
    {
        int maxCnt = 0;
        String maxKey = "";
        for(Entry<String,Integer> e: primers.entrySet())
        {
            int val = e.getValue().intValue();
            String key = e.getKey();
            if (val > maxCnt)
            {
                maxCnt = val;
                maxKey = key;
            }
        }
        System.out.println("Most frequent sequence: " + maxKey + "; " + maxCnt);
    }

    private void addPrimer(String sP, int c)
    {
        addPrimer(sP,DegString2DegByte(sP),c);
    }

    public void addPrimer(byte [] bP)
    {
        //String sP = toDegString(bP,0,bP.length);
        addPrimer(toDegString(bP,0,bP.length),bP,1);
    }

    private void addPrimer(String sP, byte [] bP, int c)
    {
        int cnt = 0;
        if (primers.containsKey(sP))
           cnt = primers.get(sP).intValue();
        primers.put(sP,cnt+c);

        toDegByte(degPrimer,bP,degPrimer.length);
    }

    public void findConservedRgnRange(int nConservedNt, int minSNPfreq)
    {
         //System.out.println("START LOOKING FOR BOUNDERIES");
         initClstFreq();
         //createFreqMatrix();
         findConsRgnRange(nConservedNt, minSNPfreq);
         //printConsRgn();
         //System.out.println("FOUND BOUNDERIES");
    }
    
    private void findConsRgnRange(int nConstervedNt, int minSNPfreq)
    {
         //freqM[row][i] += cnt
        int [] cntSNP = new int[nCol];  
        for (int i = 0; i < nCol; i++)
        {
            int max0 = freqM[0][i];
            int max1 = freqM[1][i];
            if (freqM[0][i] < freqM[1][i]) 
            {
                max0 = freqM[1][i];
                max1 = freqM[0][i];
            }
            for (int j=2; j< nRow; j++)
            {
                int f = freqM[j][i];
                if (f > max0) {max1 = max0; max0 = f; }
                else if (f > max1) max1 = f;
            }
            cntSNP[i] = max0+max1;
        }
        printConsRgn(cntSNP);
    }
        
    private void printConsRgn(int [] cntSNP)
    {
        double [] prob  = new double [4];
        int    [] cnt   = new int [4];
        System.out.println ("\tA\tC\tG\tT\tEntropy\n");
        for (int i = 0; i < nCol; i++) // seq size
        {
            System.out.print(i + "\t");
            for (int j = 0; j < nRow; j++) // 4
            {
                System.out.print(freqM[j][i] + "\t");
                cnt[j] = freqM[j][i];
            }
            cnt2prob(cnt,prob);
            System.out.print(getEntropy(prob));
            System.out.println("    " + cntSNP[i]);
        }     
    }
    
    public boolean needCluster()
    {
        long d = findDegSeq(degPrimer);
        //System.out.println("Degenaracy: " + d + " for " + toDegString(degPrimer, 0, degPrimer.length) );
        return  (d <= 0) || (d > CONSTANT.maxDegeneracy);
    }

    public boolean clstFreq(boolean startOrgPrimer)
    {
        return clstFreq(startOrgPrimer, CONSTANT.maxDegeneracy);
    }

    public boolean clstFreq(boolean startOrgPrimer, int maxDeg)
    {
         toOutput = ""; //System.out.println("START CLUSTERING");
         initClstFreq(); 
         if (findInitPrimer(startOrgPrimer))
            return runClstFreq();
         
         return false;
         //System.out.println("END CLUSTERING");
    }

    
    private void initClstFreq()
    {
        nRow = 4;
        nCol = orgPrimer.length();

        pSet.clear();
        for(Entry<String,Integer> e: primers.entrySet())
            pSet.put(e.getKey(),e.getValue());

        Arrays.fill(clstPrimer,(byte)0x00);
        createFreqMatrix();
        //System.out.println("InitPRimer = " + toDegString(clstPrimer,0,nCol) +"; "+ toDegString(degPrimer,0,nCol));
    }

    private boolean runClstFreq()
    {
        clstSeqCnt = 0;
        int cntNotFound = 0;
        boolean newFM = true;
        boolean done = false;
        
        PriorityQueue<ClstObj> pQ = new PriorityQueue<ClstObj> ();

        while (!done)
        {
            Set sKeys = pSet.keySet();
            Iterator it = sKeys.iterator();

            //toOutput += "clstPrimer = " + toDegString(clstPrimer,0,nCol) + "; ";
            //toOutput += "deg = " + findDegSeq(clstPrimer, 0, primerLength) + "; ";
            while (it.hasNext())
            {
                String s = (String) it.next();
                //System.out.println(s);
                byte [] b = DegString2DegByte(s);
                if (isDegMatch(clstPrimer,b,2))
                //if (isDegMatch(clstPrimer,b,0))
                {
                    int c = pSet.get(s).intValue();
                    clstSeqCnt += c;
                    //System.out.println("AfterRemoving: " + s + " "+ clstSeqCnt);
                    //primers.remove(s);
                    it.remove();
                    newFM = true;
                }
            }

            DP_Parms dp = new DP_Parms(clstPrimer, "X");
            if (dp.isValid(clstSeqCnt))
            {
                toOutput += dp.toStringClst(clstSeqCnt, "");
                clstPrimerList.add(dp);
            }
            
            if (newFM) 
            { 
                createFreqMatrix();
                createUpdatePrimerQueue(pQ);
                newFM = false; 
                //cntNotFound = 0;
            }
            //else         cntNotFound++;
            done = !updateClstPrimer(pQ, CONSTANT.maxDegeneracy);
            //System.out.println("clstPrimer = " + toDegString(clstPrimer,0,nCol) +
            //        " clstSeqCnt = " + clstSeqCnt + "  cntNotFound = " + cntNotFound +
            //        " deg = " + findDegSeq(clstPrimer));
        }
        //System.out.println(toOutput);
        return (clstSeqCnt > CONSTANT.minSeqCntPrimer);
    }

    private void createFreqMatrix()
    {   
        freqM = new int [nRow] [nCol];

        for (int i = 0; i < nRow; i++)
            Arrays.fill(freqM[i], 0);

        for (Entry<String,Integer> p: pSet.entrySet())
        {
            String s = p.getKey();
            int cnt = p.getValue();
           
            for (int i = 0; i<nCol; i++)
            {
                char c = s.charAt(i); 
                int row = -1;
                if      (c == 'A')  row = 0;
                else if (c == 'C')  row = 1;
                else if (c == 'G')  row = 2;
                else if (c == 'T')  row = 3;

                if (!isDegMatch(clstPrimer[i], (byte)(1<<row), 0) && (row >= 0))
                    freqM[row][i] += cnt;
            }
        }
    }

    private boolean findInitPrimer(boolean startOrgPrimer)
    {
        //System.out.println("size of clstPrimer: " + clstPrimer.length);
        if (startOrgPrimer)
        {
            System.arraycopy(DegString2DegByte(orgPrimer),0,clstPrimer,0,degPrimer.length);
            return true;
        }
        toOutput += printFreqMatrix(); //System.out.println(printFreqMatrix());

        // Prune
            // first must be C/G
        int startCG = freqM[1][0] + freqM[2][0]; //System.out.print("Prune: " + startCG);
        if (startCG < CONSTANT.minSeqCntPrimer) return false;
        
            // last must be A/T and one more of the next 3 must also be A/T
        
        primerLength = nCol-1;
        int pL = primerLength;
        int pL1 = primerLength-1;
        int pL2 = primerLength-2;
        int pL3 = primerLength-3;
        //System.out.println("Pruning A/T at the end " + pL + " " + pL1);
        if (!((freqM[0][pL]  + freqM[3][pL])  >= CONSTANT.minSeqCntPrimer)) return false;
        //if (!(((freqM[0][pL]  + freqM[3][pL])  >= CONSTANT.minSeqCntPrimer) ||
        //     ((freqM[0][pL1] + freqM[3][pL1]) >= CONSTANT.minSeqCntPrimer))) return false;
        /*
        if (((freqM[0][primerLength] + freqM[3][primerLength]) < CONSTANT.minSeqCntPrimer) ||
            (!(((freqM[0][pL1] + freqM[3][pL1]) >= CONSTANT.minSeqCntPrimer) || 
               ((freqM[0][pL2] + freqM[3][pL2]) >= CONSTANT.minSeqCntPrimer) || 
               ((freqM[0][pL3] + freqM[3][pL3]) >= CONSTANT.minSeqCntPrimer)))) 
            return false;
        */
        // Find InitPrimer
        //System.out.print("Finding init primer ");
        primerLength++; 
        int conservedNt = 0;
        for (int j = 0; j < primerLength; j++)
        {
            int mCnt = freqM[0][j];
            int mRow = 0;
            for (int i = 1; i < nRow; i++)
                if (freqM[i][j] > mCnt) { mCnt = freqM[i][j]; mRow = i; }
            if (mCnt > CONSTANT.minSeqCntPrimer) conservedNt++;
            freqM[mRow][j] = 0;             //System.out.println(j + ": " + mRow);
            clstPrimer[j] = (byte) ((1<<mRow) & 0xFF);
        }
        // if not enough conserved Nt will not be able to find primer  
        // because degeneracy will be too high
        int sD = selfDimer(clstPrimer);
        //System.out.println(toDegString(clstPrimer) + ", " + conservedNt + ", " + sD);
        DP_Parms dp = new DP_Parms(clstPrimer, "X");
        //return (conservedNt > 10) && ( sD <= CONSTANT.maxSelfDimer);
        return (conservedNt > 10) && dp.isClstPrimer();
    }

    private void createUpdatePrimerQueue(PriorityQueue<ClstObj> pQ)
    {
        pQ.clear();
        for (int i = 0; i < nRow; i++)
            for (int j = 0; j < nCol; j++)
                if (freqM[i][j] != 0)
                    pQ.add(new ClstObj(j,clstPrimer[j],1<<i,freqM[i][j]));
    }
    
    private boolean updateClstPrimer(PriorityQueue<ClstObj> pQ, int maxDeg)
    {
        // find max cnt for each column change in degeneracy
        // use prioriry Queue the first number and regenerate the priority queue
     
        //System.out.println("PQ size: " + pQ.size());
        if ((findDegSeq(clstPrimer, 0, primerLength) >= maxDeg)) return false;
        
        int cntSD = 0;    
        ClstObj C = pQ.remove();
        boolean isValid = testPrimer(C);
        while(!isValid && (C.cnt > CONSTANT.ignoreCnt) && (cntSD < 10))
        {
            C = pQ.remove();
            isValid = testPrimer(C);
            cntSD++;
        }
        
        clstPrimer[C.pos] = C.dna;
        return isValid && (C.cnt > CONSTANT.ignoreCnt) &&
               (findDegSeq(clstPrimer, 0, primerLength) <= maxDeg);
        
    }

    private boolean testPrimer(ClstObj C)
    {
        byte [] testPrimer = new byte [clstPrimer.length];
        testPrimer = Arrays.copyOf(clstPrimer, clstPrimer.length);
        testPrimer[C.pos] = C.dna;
        
        DP_Parms dp = new DP_Parms(testPrimer, "X");
        return dp.isClstPrimer();
    }

    private String printFreqMatrix()
    {
        //String s = "orgPrimer: " + orgPrimer + "\n \t";
        String s = "\t";
        for (int i = 0; i < nCol; i++)
            s += String.format("%7d",i);

        for (int j = 0; j < nRow; j++)
        {
            s += "\n" + toDegString(1<<j) + "\t";
            for (int i = 0; i < nCol; i++)
                s += String.format("%7d", freqM[j][i]);
        }
        s += "\n";
        return s;
        //System.out.println(s);
    }
}


//-------------------------------------------------------------
    //public void addPrimer(byte [] in)
    //{
    //    int cnt = 0;
    //    String sIn = toDegString(in, 0, in.length);
    //    if (primers.containsKey(sIn))
    //        cnt = primers.get(sIn).intValue();
        //else System.out.println("Adding new byte");

        //System.out.println(printByte(in) + ": " + sIn);
        //System.out.println(toDegString(in,0,in.length));
    //    toDegByte(degPrimer, in, degPrimer.length);
        //System.out.println(toDegString(degPrimer, 0, in.length) + "\n");

    //    primers.put(sIn, cnt + 1);
    //}

   /*
    public void clstPrimers()
    {
        System.out.println("------Clustering-------");
        // initial
        HashMap<String,Integer> simDS = new HashMap<String,Integer>();
        initSimilarity(simDS);
        System.out.println("Size of clstDS: " + clstDS.size());

        // cluster
        while (!simDS.isEmpty())
        {
            // find new cluster pair
            System.out.print("MaxSim = ");
            String sPair = findMaxSim(simDS);
            String [] s = sPair.split("%");
            System.out.println(" sPair: " + sPair);

            //generate new DegSeq
            String keyNew = sPair.replace("%", "+");
            DegSeq dsNew = new DegSeq(keyNew);
            findSim(clstDS.get(s[0]), clstDS.get(s[1]), dsNew);
            clstDS.remove(s[0]);
            clstDS.remove(s[1]);
            System.out.println("clstDS size = " + clstDS.size() + ": " + clstDS.keySet().toString());

            // update similarity
            //  --- first remove similarities associated with sPair
            Set sKeys = simDS.keySet();
            Iterator it = sKeys.iterator();
            while (it.hasNext())
            {
                String sKey = (String) it.next();
                if (sKey.contains(s[0]) || sKey.contains(s[1]))
                    it.remove();
            }
            System.out.println("simDS: After removing  " + simDS.size() +  ", " + s[0] + ", " + s[1]);

            //  --- then calculate similarities between new DS and the old ones
            for (DegSeq dsClst: clstDS.values())
            {
                DegSeq dsOut = new DegSeq("");
                interceptDS(dsClst,dsNew,dsOut);
                if (dsOut.conservedLength != 0)
                    simDS.put(dsClst.grpName + "%" + keyNew, dsOut.conservedLength);
            }

            // update cluster - must be the last one,
            // otherwise the previous step would need if statement
            System.out.println("\tnew clst entry: " + dsNew.printDegList(keyNew + ";;"));
            clstDS.put(keyNew, dsNew);
        }
    }

    private void initSimilarity(HashMap<String,Integer> simDS)
    {
        ArrayList<ClstObj> a = new ArrayList<ClstObj>();
        for (Entry<String, Integer> p: primers.entrySet())
            a.add(new ClstObj(p.getKey(),p.getValue()));

        int nCluster = a.size(); System.out.println("nClusters: " + nCluster);
        ClstObj [] aChild = new ClstObj[nCluster];
        a.toArray(aChild);

        // initial
        for (int i = 0; i < nCluster; i++)
        {
            ClstObj C = new DegSeq(aChild[i].grpName);
            //A.addDegSeq(aChild[i].degByte);
            A.setParms(aChild[i]);
            clstDS.put(aChild[i].grpName, A);
            for (int j = i+1; j < nCluster; j++)
            {
                ClstObj ds = new ClstObj(aChild[i].dp,aChild[j].dp,aChild[i].cnt,aChild[j].cnt);
                //System.out.println("initIntercept");
                findSim(aChild[i],aChild[j], ds);
                if (ds.conservedLength != 0)
                    simDS.put(keyI + aChild[j].grpName, ds.conservedLength);
            }
        }

        System.out.println(simDS.toString());
    }

    private String findMaxSim(HashMap<String,Integer> simDS)
    {
        String pair = "";
        int max = 0;
        for (Entry<String,Integer> e: simDS.entrySet())
        {
            int i = e.getValue().intValue();
            if (i > max)
            {
                max = i;
                pair = e.getKey();
            }
        }

        System.out.print(max);
        return pair;
    }

    private void findSim(DegSeq A, DegSeq B, DegSeq C)
    {
        //System.out.println("Intercept: " + C.grpName);
        C.addDegSeq(A.degByte);
        C.addDegSeq(B.degByte);
        //System.out.println("adding A & B: \n" +  C.toString(grpName));
        C.createDP();
        //System.out.println(C.toString(grpName));
        //System.out.println("doneIntercept " + C.conservedLength);
        //System.out.println(C.printDegList(grpName));
    }

    
   */
