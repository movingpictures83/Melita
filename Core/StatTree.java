
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class StatTree
{
    StatNode root;
    public static String tName = "Bacteria";

    public StatTree() { root = new StatNode(tName + ";"); root.isLeaf = false; }

    public void addNode (String n)
    {
        root.addChild(isBacteria(n));
    }


    private boolean findNode(String n, StatNode dP)
    {
        if (n.equals(";")) return true;

        // get next stage
        int indx = n.indexOf(";");
        String tn = n.substring(0,indx);

        //check if exists
        StatNode next = dP.childList.get(tn);
        if (next == null) return false;
        return findNode(tn, next);
    }

    private String isBacteria(String n)
    {
        //System.out.println("isBacteria: " + n);
        int indx = n.indexOf(";");
        String tn = n.substring(0,indx);
        if (!tn.equals(tName))
            throw new IllegalArgumentException("Not a Bacteria Tree.");
        return n.substring(indx);
    }

    public void getStatsPerLevel(int nLevel)
    {
        root.getStatsPerLevel(nLevel, 0);
    }

    public void getStatsHier(int nLevel, String filename) throws IOException
    {
        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);

        root.getStatsHier(nLevel, 0, "", wFile);

        wFile.close();
    }


}
