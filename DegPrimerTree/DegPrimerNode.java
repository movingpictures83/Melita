
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.lang.Integer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class DegPrimerNode
{
    String grpName;
    HashMap<String,DegPrimerNode> childDegPrimer = new HashMap<String, DegPrimerNode> ();
    boolean isLeaf;
    DegSeq DS;
    HashMap<String,DegSeq> clstDS = new HashMap<String,DegSeq>();
    DegFRPair FR;
    int seqCnt;

    public DegPrimerNode(String gName)
    {
        isLeaf = false;
        grpName = gName;
        seqCnt = 0;
        DS = new DegSeq(grpName);
        FR = new DegFRPair(grpName);
    }
   
    public DegPrimerNode(String gName, String chName, String seq, String ID)
    {
        //isLeaf = false;
        //grpName = gName;
        //DS = new DegSeq(grpName);
        //FR = new DegFRPair(grpName);
        this(gName);
        addChild(chName, seq, ID);
    }

    public void addChild(String n, String seq, String ID)
    {
        n = n.replaceFirst(";","");
        if (n.equals(";"))
        {
            isLeaf = true;
            
            DS.addSeq(seq, ID);
            return;
        }

        int indx = n.indexOf(";");
        String chName = n.substring(0,indx);
        
        DegPrimerNode dN;
        if (childDegPrimer.containsKey(chName)) {
            childDegPrimer.get(chName).addChild(n.substring(indx), seq, ID);
        }
        else {
            childDegPrimer.put(chName, new DegPrimerNode(chName,n.substring(indx), seq, ID));
        }
    }

    public void addDegSeq(byte [] seq)
    {
        DS.addDegSeq(seq);
    }

    public void createDegLeaves()
    {
        if (isLeaf) DS.createDP();

        for (DegPrimerNode dN: childDegPrimer.values())
            dN.createDegLeaves();
    }


    public void propagateDegSeq()
    {
        if (isLeaf)
        {
            seqCnt = DS.seqID.size();
            return;
        }
        for (DegPrimerNode dN: childDegPrimer.values())
            dN.propagateDegSeq();
        combineDegSets();
    }

    public void combineDegSets()
    {
        int s = childDegPrimer.size();
        TreeSet<String> nSet = new TreeSet<String>();
        nSet.addAll(childDegPrimer.keySet());

        if (s==1)
        {
            DegPrimerNode x = childDegPrimer.get(nSet.iterator().next());
            DS.setParms(x.DS);
            seqCnt = x.seqCnt;
            if (DS.conservedLength == 0)
            {
                clstDS.putAll(x.clstDS);
                System.out.println(grpName + ": clstDS size = " + clstDS.size());
            }
        }
        else
        {
            constructDS();
            if (DS.conservedLength == 0)
            {
                clusterDS();
                System.out.println(grpName + ": clstDS size = " + clstDS.size());
            }
        }
    }

    public void constructDS()
    {
        int size = childDegPrimer.size();
        int [] starts = new int [size];
        int [] ends = new int [size];
        int cntOnes = 0;
        for (DegPrimerNode dN: childDegPrimer.values())
        {
            //System.out.println(dN.DS.printDegList(grpName));
            int sizeDL = dN.DS.degList.size();
            starts[cntOnes] = dN.DS.degList.get(0).s;
            ends[cntOnes] = dN.DS.degList.get(sizeDL-1).e;
            DS.addDegSeq(dN.DS.degByte);
        //addDegSeq(in.degByte);

            //System.out.println(cntOnes + ": combineDP: PruneX " + DS.printDegList(grpName));
            cntOnes++;
            seqCnt += dN.seqCnt;
        }
        Arrays.sort(starts);
        Arrays.sort(ends);
        DS.startSeq = starts[cntOnes-1];
        DS.endSeq = ends[0];
     
        DS.createDP();   
    }


    public int combineDPOnes()
    {
        int size = childDegPrimer.size();
        int [] starts = new int [size];
        int [] ends = new int [size];
        int cntOnes = 0;
        for (DegPrimerNode dN: childDegPrimer.values())
        {
            if (dN.DS.degList.size() == 1)
            {
                starts[cntOnes] = dN.DS.degList.get(0).s;
                ends[cntOnes] = dN.DS.degList.get(0).e;
                DS.addDegSeq(dN.DS.degByte);
                cntOnes++;
            }
        }
        Arrays.sort(starts);
        Arrays.sort(ends);
        DS.startSeq = starts[cntOnes-1];
        DS.endSeq = ends[0];
      
        DS.createDP();

        return size - cntOnes;
    }

    
    public void combineDegSets(DegPrimerNode A, DegPrimerNode B)
    {
        int aLength = A.DS.degList.size();
        int bLength = B.DS.degList.size();

        int aCnt = 0;
        int aStart = A.DS.degList.get(aCnt).s;
        int aEnd = A.DS.degList.get(aCnt).e;

        int bCnt = 0;
        int bStart = B.DS.degList.get(bCnt).s;
        int bEnd = B.DS.degList.get(bCnt).e;


        boolean aNext = false;
        boolean bNext = false;
        int start = 0;
        int end = 0;
        while (aCnt < aLength && bCnt < bLength)
        {
            if (aNext)
            {
                aStart = A.DS.degList.get(aCnt).s;
                aEnd = A.DS.degList.get(aCnt).e;
            }
            if (bNext)
            {
                bStart = B.DS.degList.get(bCnt).s;
                bEnd = B.DS.degList.get(bCnt).e;
            }


            if((aStart > bEnd) || (bStart > aEnd)) // no overlap
            {
                if (aEnd < bEnd) aNext = true;
                else             bNext = true;
            }
            else
            {
                if (aStart < bStart) start = bStart;
                else                 start = aStart;

                if (aEnd == bEnd)
                {
                    end = aEnd; aNext = true; bNext = true; aCnt++; bCnt++;
                }
                else if (aEnd < bEnd)
                {
                    end = aEnd; aNext = true; bStart = aEnd + 1; aCnt++;
                }
                else
                {
                    end = bEnd; bNext = true; aStart = bEnd + 1; bCnt++;
                }
                DS.degList.add(new DegParms(start,end));
            }
        }
    }

    public void findDPPinVX(ArrayList<DegFRPair>dPPList, FRVXrgn rgnVX)
    {
        System.out.println("GROUP: "+grpName);
        //System.out.println("IN FINDPPINVX");
        if (DS.hasPrimerRgn(FR, rgnVX, grpName) && foundDPP(rgnVX.maxD))
        {
            //System.out.println("GROUP "+grpName+": FOUND");
            dPPList.add(FR); return;
        }
        else
        {
            //System.out.println("NO PRIMER FOUND, TRYING CHILDREN");
            //System.out.println("GROUP "+grpName+": NOT FOUND");
            for (DegPrimerNode dN: childDegPrimer.values())
                    dN.findDPPinVX(dPPList, rgnVX);
        }
        //System.out.println("RETURNING FROM FINDPPINVX");
    }

    private boolean foundDPP(int maxD)
    {
        //System.out.println("FOUNDDPP CALLED");
        FR.createPrimers();
        //System.out.println("CALLING CREATEPAIR");
        return FR.createPair(maxD);
        //return true;
    }


    // ----------------------------------------------------------------
    // ---------------- CLUSTER -----------------------------------------
    // ----------------------------------------------------------------
    public void needCluster()
    {
        for (DegPrimerNode dN: childDegPrimer.values())
            if (dN.DS.degList.get(0).l == 0) dN.needCluster();

        clusterDS();
    }

    public void clusterDS()
    {
        System.out.println("------Clustering-------");
        // initial
        HashMap<String,Integer> simDS = new HashMap<String,Integer>();
        initSimilarity(simDS);
        //System.out.println("Size of clstDS: " + clstDS.size());

        // cluster
        while (!simDS.isEmpty())
        {
            // find new cluster pair
            //System.out.print("MaxSim = ");
            String sPair = findMaxSim(simDS);
            String [] s = sPair.split("%");
            //System.out.println(" sPair: " + sPair);

            //generate new DegSeq
            String keyNew = sPair.replace("%", "+");
            DegSeq dsNew = new DegSeq(keyNew);
            interceptDS(clstDS.get(s[0]), clstDS.get(s[1]), dsNew);
            clstDS.remove(s[0]);
            clstDS.remove(s[1]);
            //System.out.println("clstDS size = " + clstDS.size() + ": " + clstDS.keySet().toString());

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
            //System.out.println("simDS: After removing  " + simDS.size() +  ", " + s[0] + ", " + s[1]);

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
            //System.out.println("\tnew clst entry: " + dsNew.printDegList(keyNew + ";;"));
            clstDS.put(keyNew, dsNew);
        }
    }

    private void initSimilarity(HashMap<String,Integer> simDS)
    {
        ArrayList<DegSeq> a = new ArrayList<DegSeq>();
        for (DegPrimerNode dN: childDegPrimer.values())
        {
            if (dN.clstDS.size() != 0)
            {
                for (DegSeq dS: dN.clstDS.values())
                {
                    //dS.grpName = grpName + ";" + dS.grpName;
                    a.add(dS);
                }
            }
            else
            {
                a.add(dN.DS);
            }

        }

        int nCluster = a.size(); //System.out.println("nClusters: " + nCluster);
        DegSeq [] aChild = new DegSeq[nCluster];
        a.toArray(aChild);

        // initial
        for (int i = 0; i < nCluster; i++)
        {
            String keyI = aChild[i].grpName + "%";
            DegSeq A = new DegSeq(aChild[i].grpName);
            A.setParms(aChild[i]);
            clstDS.put(aChild[i].grpName, A);
            for (int j = i+1; j < nCluster; j++)
            {
                DegSeq ds = new DegSeq("");
                interceptDS(aChild[i],aChild[j], ds);
                if (ds.conservedLength != 0)
                    simDS.put(keyI + aChild[j].grpName, ds.conservedLength);
            }
        }

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

        return pair;
    }

    private void interceptDS(DegSeq A, DegSeq B, DegSeq C)
    {
        C.addDegSeq(A.degByte);
        C.addDegSeq(B.degByte);
        C.createDP();
    }

   
    // ----------------------------------------------------------------
    // ---------------- PRINT -----------------------------------------
    // ----------------------------------------------------------------

    public void findLeaves(PrintWriter wFile, String name)
    {
            if (isLeaf)
            {
                //if (grpName.contains("Escherichi"))
                //        System.out.println(grpName + DS.origLength);
                wFile.println(DS.toString(name+grpName));
            }
            else
            {
                for (DegPrimerNode dN: childDegPrimer.values())
                    dN.findLeaves(wFile, name + grpName + ";");
            }
    }

    public void degPrimerRegionsMeta(PrintWriter wFile, String name)
    {
        boolean noPR = (DS.degList.size() == 1) && (DS.degList.get(0).l == 0);
        int nR = noPR ? 0 : DS.degList.size();
        int indx = name.indexOf("B");
        String n = name;
        if (indx > 0) n = name.substring(0,indx);
        String s = n+grpName;
        s += " nRgn: " + nR + " nSeq: " + seqCnt;
        s += " nCons: " + DS.conservedLength + " nClst: " + clstDS.size() + "\n";
        wFile.println(s);
        if (noPR)
        {
            for (DegPrimerNode dN: childDegPrimer.values())
            {
                dN.degPrimerRegionsMeta(wFile, "\t"+ name + grpName + ";");
            }
        }
    }

    public void degPrimerRegions(PrintWriter wFile, String name)
    {

        wFile.println(DS.printDegList(name + grpName));
        if ((DS.degList.size() == 1) && (DS.degList.get(0).l == 1))
        {
            
            //for (DegPrimerNode dN: childDegPrimer.values())
            //    dN.degPrimerRegionsMeta(wFile, "\t"+ name + grpName + ";");
            
            for (DegPrimerNode dN: childDegPrimer.values())
            {
                dN.degPrimerRegions(wFile, "\t"+ name + grpName + ";");
            }
        }
    }

    public void findNode(String name)
    {
        if (grpName.contains(name))
            System.out.println(grpName + DS.origLength);

    
        for (DegPrimerNode dN: childDegPrimer.values())
            dN.findNode(name);
        

    }

    public int printPairs(PrintWriter wFile, String name, int cnt)
    {
            if (FR.foundPair)
            {
                cnt++;
                wFile.println(cnt + ": ");
                wFile.println(FR.toString(name));
            }
            else
            {
                for (DegPrimerNode dN: childDegPrimer.values())
                    cnt = dN.printPairs(wFile, name + grpName + ";", cnt);
            }

            return cnt;
    }

    public void corrRegionPairs(PrintWriter wFile, String name)
    {
        wFile.println(DS.printDegList(name + grpName));
            if (FR.foundPair)
            {
                wFile.println(FR.toString(name));
            }
            else
            {
                
                for (DegPrimerNode dN: childDegPrimer.values())
                    dN.corrRegionPairs(wFile, name + grpName + ";");
            }
    }

    public void printDegListBFS(PrintWriter wFile, String name)
    {
        if (isLeaf) return;   

        wFile.println(DS.printDegList(name + grpName));
     
        for (DegPrimerNode dN: childDegPrimer.values())
            wFile.println(dN.DS.printDegList(name + grpName + ";"));

        for (DegPrimerNode dN: childDegPrimer.values())
            dN.printDegListBFS( wFile, name + grpName + ";");
            
    }


    /*
    public void printDegList(PrintWriter wFile, String name)
    {
        if (isLeaf)
        {
            wFile.println(DS.printDegList(name + grpName));
        }
        else
            {
                for (DegPrimerNode dN: childDegPrimer.values())
                    dN.printDegList( wFile, name + grpName + ";");
            }
    }
*/
    public void degPrimerStats(PrintWriter wFile, String name, boolean wLeaves)
    {

        //wFile.println(DS.printDegList(name + grpName));
        if (!isLeaf)
        {
            wFile.println(DS.printDegList(name + grpName));

            // start commenting
            for (DegPrimerNode dN: childDegPrimer.values())
                if (!(wLeaves && dN.isLeaf))
                    wFile.println(dN.DS.printDegList(name + grpName + ";" + dN.grpName));
           // end commenting
            
            for (DegPrimerNode dN: childDegPrimer.values())
                dN.degPrimerStats( wFile, name + grpName + ";", wLeaves);
        }
        
    }

    public String printClustDS()
    {

        String s = "printClustDS for " + grpName + "\n";
        for (DegSeq ds: clstDS.values())
        {
            s += ds.printDegList("");
        }

        return s;
    }


    // ----------------------------------------------------------------
    // ---------------- STATS -----------------------------------------
    // ----------------------------------------------------------------
    public int findMaxChild()
    {

        if (childDegPrimer.isEmpty()) return 0;
        int m  = 0;

        for (DegPrimerNode dp: childDegPrimer.values())
        {
            m = Math.max(m, dp.childDegPrimer.size());
            m = Math.max(m, dp.findMaxChild());
        }
        return m;
    }


    //public void findCnt(int level)
    //{
    //    StatClass sC = new StatClass();
    //    findCnt(level, sC);
    //}


    public void findCnt(int level, StatClass sC)
    {
        if (isLeaf) sC.cntLeaf++;
        if (!childDegPrimer.isEmpty())
        {
            for (DegPrimerNode dp: childDegPrimer.values())
                dp.findCnt(level+1, sC);
            
            sC.cnt += childDegPrimer.size();
        }

    }

    public void getOrgLength(TreeMap<Integer,Integer> tM)
    {
        if (isLeaf)
        {
            DS.findOrigLength();

            int x = 0;
            if (tM.containsKey(DS.origLength))
                x = tM.get(DS.origLength).intValue();

            tM.put(DS.origLength,x+1);
        }

        for (DegPrimerNode dN: childDegPrimer.values())
            dN.getOrgLength(tM);
    }

    public void stats(TreeMap<Integer,Integer> tS)
    {        
        for (DegPrimerNode dN: childDegPrimer.values())
        {
            int s = dN.childDegPrimer.size();

            if (tS.containsKey(s))
            {
                //int a = ((Integer)tS.get(s)).intValue();
                int a = tS.get(s).intValue();
                tS.put(s,a+1);
            }
            else
                tS.put(s,1);

            dN.stats(tS);

        }

    }

    public void cntDPLeaf(TreeMap<Integer,Integer> tS)
    {
        for (DegPrimerNode dN: childDegPrimer.values())
        {
            if (dN.isLeaf)
            {

                int s = dN.DS.degList.size();

                if (tS.containsKey(s))
                {
                    //int a = ((Integer)tS.get(s)).intValue();
                    int a = tS.get(s).intValue();
                    tS.put(s,a+1);
                }
                else
                tS.put(s,1);

            }
            else
                dN.cntDPLeaf(tS);
        }
    }

    public void sameNodeType()
    {
        boolean hasLeaf = false;
        boolean notLeaf = false;

        for (DegPrimerNode dN: childDegPrimer.values())
        {
            if (dN.isLeaf)  hasLeaf = true;
            else            notLeaf = true;
            dN.sameNodeType();
        }
        if (hasLeaf && notLeaf)
            System.out.println(grpName + " has both type of nodes");
    }



}
