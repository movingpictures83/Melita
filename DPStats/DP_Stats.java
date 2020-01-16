
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class DP_Stats implements Comparable<DP_Stats>
{
    // read in Primers
    DP_Parms dpP;
    TreeMap<String, DP_StatDB> dpDB;

    public DP_Stats (byte [] in, String FR, String id)
    {
       dpP = new DP_Parms(in, FR, id);
       dpDB = new TreeMap<String,DP_StatDB>();
    }

    public DP_Stats (String in, String FR, String id)
    {
       dpP = new DP_Parms(in, FR, id);
       dpDB = new TreeMap<String,DP_StatDB>();
    }

    public void addDB(DP_StatDB dpStats, String DB)
    {
        dpDB.put(DB, dpStats);
    }

    public int getRefAmplLength(DP_Stats dpR)
    {
        return dpR.dpDB.get(CONSTANT.refDB).posMid + dpR.dpP.dpLength
                 - dpDB.get(CONSTANT.refDB).posMid;
    }

    public boolean isAmplLength(DP_Stats dpR)
    {
        int L = getRefAmplLength(dpR);
        return (L > CONSTANT.minAmplLength) && (L < CONSTANT.maxAmplLength);
    }

    
    public String toString(String dlmt)
    {
        String dbStats = dpP.toString(dlmt);
        for (Entry<String, DP_StatDB> e: dpDB.entrySet())
            dbStats += e.getValue().toExcel(e.getKey()) + "\n";
        return dbStats;
    }

    public int compareToOrg(DP_Stats C)
    {
        DP_StatDB objRDP  =   dpDB.get(CONSTANT.refDB);
        DP_StatDB compRDP = C.dpDB.get(CONSTANT.refDB);

        int cnt_CO = compRDP.cntUniq-objRDP.cntUniq;
        double deg_CO = (double)C.dpP.deg/dpP.deg;

        // choose C = 1
        if      (cnt_CO == 0) return (deg_CO <= 1) ? 1 : -1;
        else if (cnt_CO >  0)
        {
            if (deg_CO <= 1)  return 1;
            else              
                return (cnt_CO >= CONSTANT.cntSeqDoubleDeg*deg_CO) ? 1 : -1;
        }
        else
        {
            deg_CO = 1.0/deg_CO;
            cnt_CO = Math.abs(cnt_CO);
            if (deg_CO <= 1)  return -1;
            else
                return (cnt_CO >= CONSTANT.cntSeqDoubleDeg*deg_CO) ? -1 : 1;
            
        }
    }

    public int compareTo(DP_Stats C)
    {
        DP_StatDB objRDP  =   dpDB.get(CONSTANT.refDB);
        DP_StatDB compRDP = C.dpDB.get(CONSTANT.refDB);

        int cnt_CO = compRDP.cntUniq-objRDP.cntUniq;
        double deg_CO = (double)C.dpP.deg/dpP.deg;

        // choose C = 1
        if      (cnt_CO == 0)                    return (deg_CO <= 1) ? 1 : -1;
        else if ((C.dpP.deg<=16)&&(dpP.deg<=16)) return (cnt_CO >  0) ? 1 : -1;
        else if (cnt_CO >  0)
        {
            if (deg_CO <= 1) return 1;
            else
                return (cnt_CO >= CONSTANT.cntSeqDoubleDeg*deg_CO) ? 1 : -1;
        }
        else
        {
            deg_CO = 1.0/deg_CO;
            cnt_CO = Math.abs(cnt_CO);
            if (deg_CO <= 1)  return -1;
            else
                return (cnt_CO >= CONSTANT.cntSeqDoubleDeg*deg_CO) ? -1 : 1;

        }
    }
}
