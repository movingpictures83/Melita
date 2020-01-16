/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class TmParms extends ByteFunc
{

    byte [] in;
    double minTm;
    double maxTm;
    double rangeTm;
    int minCG;
    int maxCG;
    int l;

    public TmParms(byte [] in, int start, int end) //(int minCG, int maxCG, int l)
    {
        l = end - start;
        findCGcnt(in, start, end);
        minTm = getTm(minCG, l);
        maxTm = getTm(maxCG, l);
        rangeTm = maxTm-minTm;
    }


    private void findCGcnt(byte [] in, int start, int end)
    {
        maxCG = 0;
        minCG = 0;
        for (int i = start; i < end; i++)
        {
            if ((in[i]&0x06) != 0) maxCG++;
            if ((in[i]&0x09) != 0) minCG++; // really maxAT
        }
        minCG = l-minCG;
    }

    private double getTm(int cntCG, int l)
    {
        return 64.9+(41*(cntCG - 16.4)/l);
    }

    public boolean isTmRange(double min, double max, double range)
    {
        return (minTm > min) && (maxTm < max) && (rangeTm < range);
    }

    public boolean isTmCompatable(TmParms T)
    {
        //System.out.println(toStringTm(",") + " " + T.toStringTm(",") +  " ");
        return Math.abs(Math.max(maxTm,T.maxTm) - Math.min(minTm,T.minTm))
                < CONSTANT.rangeTmFR;
    }

    public boolean isCGRange()
    {
        return ((100*minCG/l) >= CONSTANT.minCGPerc) &&
               ((100*maxCG/l) <= CONSTANT.maxCGPerc);
    }

    public String toStringTm(String dlmt)
    {
        return String.format("%4.2f", minTm) + "->" + String.format("%4.2f", maxTm);
    }

    public String toStringCG(String dlmt)
    {
        return String.format("%4.2f", minCG/(double)l) + "->" + String.format("%4.2f", maxCG/(double)l);
    }
}
/*
  public String getTm(String p)
    {
        //System.out.println(p.length() + " " + p);
        return getTm(DegString2DegByte(p),0,p.length()).toString();
    }

    public String getTmPP(String pF, String pR)
    {
        return getTm(pF) + ", " + getTm(pR);
    }

 */