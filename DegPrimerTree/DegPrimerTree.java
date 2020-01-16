
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
public class DegPrimerTree
{
    DegPrimerNode root;
    public static String tName = "Bacteria";

    public DegPrimerTree() { root = new DegPrimerNode(tName); }
    
    public void addNode (String n, String seq, String ID)
    {
        root.addChild(isBacteria(n), seq, ID);
    }

    public boolean findNode(String n)
    {
        return findNode(isBacteria(n), root);
    }

    private boolean findNode(String n, DegPrimerNode dP)
    {
        if (n.equals(";")) return true;

        // get next stage
        int indx = n.indexOf(";");
        String tn = n.substring(0,indx);

        //check it exists
        DegPrimerNode next = dP.childDegPrimer.get(tn);
        if (next == null) return false;
        return findNode(tn, next);
    }

    public void findDPPinVX(FRVXrgn rgnFR, ArrayList<DegFRPair> dPPList)
    {
        //ArrayList<DegFRPair> dPPList = new ArrayList<DegFRPair>();
        root.findDPPinVX(dPPList, rgnFR);
    }

    public void printPairs(String filename) throws IOException
    {
        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);

        root.printPairs(wFile, "", 0);

        wFile.close();
    }

    public void corrRegionPairs(String filename) throws IOException
    {
        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);

        root.corrRegionPairs(wFile, "");

        wFile.close();
    }


    private String isBacteria(String n)
    {
        int indx = n.indexOf(";");
        String tn = n.substring(0,indx);
        if (!tn.equals(tName))
            throw new IllegalArgumentException("Not a Bacteria Tree.");
        return n.substring(indx);
    }

    public void printLeaves(String filename) throws IOException
    {
        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);

        root.findLeaves(wFile, "");

        wFile.close();
    }

    public void degPrimerRegions(String filename) throws IOException
    {
        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);

        root.degPrimerRegions(wFile, "");

        wFile.close();
    }

    public void degPrimerRegionsMeta(String filename) throws IOException
    {
        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);

        root.degPrimerRegionsMeta(wFile, "");

        wFile.close();
    }

    public void findNodeByName(String name)
    {
        root.findNode(name);
    }

    public void createDegLeaves()
    {
        root.createDegLeaves();
    }

    public void clusterChildNodes()
    {
        //root.clusterDS();
    }

    public void propagateDegSeq()
    {
        root.propagateDegSeq();
    }

    public void stats()
    {
        TreeMap<Integer,Integer> tS = new TreeMap<Integer,Integer> ();
        root.stats(tS);

    }

    public void getOrgLength()
    {
        TreeMap<Integer,Integer> tM = new TreeMap<Integer,Integer>();

        root.getOrgLength(tM);
        int min = tM.firstKey();
        int max = tM.lastKey();

        //System.out.println("OrigLength: " + min + " to " + max  + " " + tM.size());
        //System.out.println(tM.toString());
    }

    public void cntDPLeaf()
    {
        TreeMap<Integer,Integer> tS = new TreeMap<Integer,Integer> ();
        root.cntDPLeaf(tS);

        System.out.println("cntDpLeaf: " + tS.toString());
        //cntDpLeaf: {1=7077, 2=25, 3=17, 4=12, 5=8, 6=5, 7=4, 8=3, 9=4,
        //            10=2, 11=3, 12=1, 17=1, 21=1, 22=2, 26=1}
    }

    public void sameNodeType()
    {
        root.sameNodeType();
    }

    public void cntStat()
    {
        StatClass sC = new StatClass();
        root.findCnt(0, sC);
        System.out.println("NodeCnt: " + sC.cnt + " " + sC.cntLeaf + " "
                + (sC.cnt - sC.cntLeaf));
    }

    public void degPrimerStats(String filename, boolean wLeaves) throws IOException
    {
        if (wLeaves) filename += "wLeaves";
        filename += ".txt";
        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);

        root.degPrimerStats(wFile, "", wLeaves);

        wFile.close();
    }
  
}
