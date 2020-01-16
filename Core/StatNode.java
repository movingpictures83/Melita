
import java.io.PrintWriter;
import java.util.HashMap;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class StatNode
{
    String level;
    int cnt;
    boolean isLeaf;
    HashMap<String,StatNode> childList = new HashMap<String,StatNode>();

    public StatNode(String hier)
    {
        cnt = 0;
        int indx = hier.indexOf(";");
        level = hier.substring(0,indx);
        isLeaf = false;
        //System.out.println("Creating node: " + level);
        addChild(hier.substring(indx));
    }

    public void addChild(String hier)
    {
        cnt++;
        //System.out.println("addChild:" +  hier);
        if (hier.equals(";"))
        {
            //System.out.println("Leaf: " + isLeaf + " cnt: " + cnt);
            isLeaf = true;
            return;
        }

        hier = hier.substring(1);
        int indx = hier.indexOf(";");
        String chName = hier.substring(0,indx);
        String nextHier = hier.substring(indx);

        StatNode sN;
        if (childList.containsKey(chName))
            childList.get(chName).addChild(nextHier);
        else
            childList.put(chName, new StatNode(hier));
    }

    public void getStatsPerLevel(int nLevel, int cntLevel)
    {
        if (cntLevel > nLevel) return;
        else if(cntLevel == nLevel)
        {
            for (StatNode s: childList.values()) 
                System.out.println(s.toString()); 
        }

        for (StatNode s: childList.values())
            s.getStatsPerLevel(nLevel, cntLevel+1);
    }

    public void getStatsHier(int nLevel, int cntLevel, String offset, PrintWriter wFile)
    {
        if (cntLevel > nLevel) return;

        //if (!isLeaf)
        //{
            wFile.println(offset + level + " " + isLeaf);
            offset += "   ";
            for (StatNode s: childList.values())
                wFile.println(s.toString(offset));

            for (StatNode s: childList.values())
                s.getStatsHier(nLevel, cntLevel+1, offset, wFile);
        //}
    }

    public String toString(String offset)
    {
        return offset + level + ": " + cnt;
    }
}
