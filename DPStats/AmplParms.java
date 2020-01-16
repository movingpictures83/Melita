
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;
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
public class AmplParms extends ByteFunc
{
    private class AmplCnt
    {
        int total;
        int uSource;
        int uGenus;
        int uPhylum;
        int tSource;
        int tGenus;
        int tPhylum;
        int cntSG;
        TreeSet<String> uniqTaxID;
        TreeSet<String> missGenus;

        public AmplCnt()
        {
            setStats(0,0,0,0,0,0,0);
            uniqTaxID = new TreeSet<String>();
            missGenus = new TreeSet<String>();
        }
        
        public AmplCnt (int t, int uPh, int tPh, int uG, int tG, int uS, int tS)
        {
            setStats(t, uPh, tPh, uG, tG, uS, tS);
            uniqTaxID = new TreeSet<String>();
            missGenus = new TreeSet<String>();
        }

        public void setStats(int t, int uPh, int tPh, int uG, int tG, int uS, int tS)
        {
            total = t; 
            uSource = uS; uGenus = uG; uPhylum = uPh;
            tSource = tS; tGenus = tG; tPhylum = tPh;
        }

        public String toString()
        {
            return "[" + total + ", " + tSource + ", " + tGenus + ", " + tPhylum
                    + ", " + uSource + ", " + uGenus + ", " + uPhylum + "];";
        }

        public String toExcel()
        {
            return total + "\t" + tSource + "\t" + tGenus + "\t" + tPhylum + "\t"
                    + uSource + "\t" + uGenus + "\t" + uPhylum + "\t";
        }
    }

    DP_Stats fP;
    DP_Stats rP;
    String ID;
    HashMap<String,AmplCnt> amplDB;
    HashMap<String,TreeMap<Integer,Integer>> amplLengthDB;

    public AmplParms (DP_Stats fP, DP_Stats rP, String ID)
    {
        this.fP = fP;
        this.rP = rP;
        this.ID = ID;
        amplDB = new HashMap<String,AmplCnt> ();
        amplLengthDB = new HashMap<String,TreeMap<Integer,Integer>>();
    }

    public String getID(boolean withPos)
    {
        String x = ID;
        if (!x.isEmpty() && !withPos) return x;
        if (!x.isEmpty()) x += "_";
        if (fP.dpDB.containsKey(CONSTANT.refDB)) 
        {
            int fpos = fP.dpDB.get(CONSTANT.refDB).posEcoli;
            if (fpos == -1)
                fpos = fP.dpDB.get(CONSTANT.refDB).posMid+27;
            x += "F" + fpos;
        }

        if (rP.dpDB.containsKey(CONSTANT.refDB))
        {
            int rpos = rP.dpDB.get(CONSTANT.refDB).posEcoli;
            if (rpos == -1)
                rpos = rP.dpDB.get(CONSTANT.refDB).posMid+27;
            x += "_R" + rpos;
        }
        return x;

    }

    public boolean checkTemp()
    {
        return fP.dpP.TmP.isTmCompatable(rP.dpP.TmP);
    }

    public int getRefAmplLength()
    {
        return rP.dpDB.get(CONSTANT.refDB).posMid + rP.dpP.dpLength
             - fP.dpDB.get(CONSTANT.refDB).posMid;
    }


    public void addDB(String DB)
    {
        amplDB.put(DB, new AmplCnt());
        amplLengthDB.put(DB, new TreeMap<Integer,Integer>());
    }

    public void addDBStat(String DB, int t, 
            int uPh, int tPh, int uG, int tG, int uS, int tS)
    {
        amplDB.get(DB).setStats(t, uPh, tPh, uG, tG, uS, tS);
    }
    public void addDBLength(String DB, TreeMap<Integer,Integer> l)
    {
        amplLengthDB.put(DB,l);
    }

    public void addUniqTax(String DB, String hier)
    {
        if (amplDB.get(DB).uniqTaxID.contains(hier))
            System.out.println("Why is Tax NOT UNIQ????????");
        amplDB.get(DB).uniqTaxID.add(hier);
    }

    public void addMissGenus(String DB, String hier)
    {
        amplDB.get(DB).missGenus.add(hier);
    }

    public int isOverlap(AmplParms A, String DB)
    {
        int s1 = fP.dpDB.get(DB).posMid;
        int e1 = rP.dpDB.get(DB).posMid;
        int s2 = A.fP.dpDB.get(DB).posMid;
        int e2 = A.rP.dpDB.get(DB).posMid;

        if (((e1-50)<s2) || ((e2-50)<s1))
            return 0; // no overlap
        else if ((Math.abs(s2-s1)<100 && Math.abs(e2-e1)<100))
            return 1; // span the same regions
        else if ((s2-s1>=0 && e1-e2>=0) || (s1-s2>=0 && e2-e1>=0))
            return 2;
        else
            return 3;

         
    }

    public String combineTwoAmpl(AmplParms A, PrintWriter wFile)
    {
        String s = "";
        for (String db: amplDB.keySet())
            s += combineTwoAmpl(A,db,wFile);
        return s;
    }

    public String combineTwoAmpl(AmplParms A, String DB, PrintWriter wFile)
    {
        int uS = amplDB.get(DB).uniqTaxID.size();
        int mG = 0;
        for (String s: A.amplDB.get(DB).uniqTaxID)
            if (!amplDB.get(DB).uniqTaxID.contains(s)) uS++;
        for (String s: A.amplDB.get(DB).missGenus)
        {
            if (amplDB.get(DB).missGenus.contains(s))
            {
                String [] sHier = s.split(";");
                int nH = sHier.length;
                wFile.println(sHier[1]+";"+sHier[nH-2]+";"+sHier[nH-1]);
                mG++;
            }
        }

        String s = DB + ": " +  uS + ";" + mG + "\t" +
                toAmplString(DB) + ";    " + A.toAmplString(DB) + "\n";
        return s;
    }



    public String toAmplString(String DB)
    {
        return getID(true)+amplDB.get(DB).toString();
    }

    public String toShortString(String dlmt, boolean withPos)
    {
        double tmpRange = Math.max(fP.dpP.TmP.maxTm, rP.dpP.TmP.maxTm) -
                          Math.min(fP.dpP.TmP.minTm, rP.dpP.TmP.minTm);
        String  s = getID(withPos) + dlmt + String.format("%4.2f", tmpRange) + dlmt +
                areDimers(fP.dpP.DP, rP.dpP.DP) + dlmt +
                amplDB.get("RDP").toExcel() + "\n";
        return s;
    }

    public String toString(String dlmt, boolean printL)
    {
        
        boolean withPos = false;
        String s = fP.toString(dlmt) + rP.toString(dlmt);
        s += dlmt + toShortString(dlmt, withPos);
        if (printL)
        {
            for (Entry<String, AmplCnt> e: amplDB.entrySet())
            {
                s += "  " + e.getKey() + ":\t cnt = " + e.getValue().toString() + "\n";
                s += "\tLengthStats: ";
                int i = 0;
                for (Entry<Integer,Integer> e1: amplLengthDB.get(e.getKey()).entrySet())
                {
                    s += e1.getKey() + "="+ e1.getValue() + "  ";
                    i++;
                    if (i == 10) {s += "\n\t\t"; i=0;}
                }
                s += "\n";
            }
        }
        return s;
    }
/*
    public int minDist(int DB)
    {
        return (int)((rP.dpDB[DB].posPaerg - fP.dpDB[DB].posPaerg)*0.5);
    }

    public int maxDist(int DB)
    {
        return (int)((rP.dpDB[DB].posPaerg - fP.dpDB[DB].posPaerg)*1.5);
    }
*/
}
