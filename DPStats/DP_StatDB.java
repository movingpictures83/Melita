/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class DP_StatDB
{

    int posPaerg, posEcoli, posMin, posMax, posMid;
    int cntUniq, cntMult, cntNone;

    public DP_StatDB(int posPaerg, int posEcoli, int posMin, int posMax, int posMid,
                int cntUniq, int cntMult, int cntNone)
    {
            this.posPaerg = posPaerg;
            this.posEcoli = posEcoli;
            this.posMin   = posMin;
            this.posMax   = posMax;
            this.posMid   = posMid;
            this.cntUniq  = cntUniq;
            this.cntMult  = cntMult;
            this.cntNone  = cntNone;
    }

    public String toString(String DB)
    {
        String s = "  " + DB + ": ";
        s += "cnt = [u: " + cntUniq + ", m: " + cntMult + ", n:" + cntNone + "]; ";   
        s += "pos = [" + posMin + ", " + posMax + ", " + posMid + "]; ";
        if (DB == "RDP") s += "[Paerg: " + posPaerg + ", Ecoli: " + posEcoli + "]; ";
        return s + "\n";
    }

    public String toExcel(String DB)
    {
        String s = cntUniq + "\t" + cntMult + "\t";
        if (DB == "RDP") s += posEcoli + "\t";

        return s;


    }
}
