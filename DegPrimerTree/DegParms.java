/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class DegParms
{
    int s;
    int e;
    int minDeg;
    int maxDeg;
    int l;

    public DegParms(int s, int e)
    {
        this.s = s; this.e = e;
        l = e-s; if (l != 0) l++;
        this.minDeg = CONSTANT.maxDegeneracy;
        this.maxDeg = CONSTANT.maxDegeneracy*64;
    }

    public DegParms(DegParms in)
    {
        s = in.s; e = in.e; l = in.l;
        minDeg = in.minDeg;
        maxDeg = in.maxDeg;
    }

    public String toString()
    {
        return s + " " + e + " " + l + " " + minDeg  + " " + maxDeg;
    }

    public int compareTo(DegParms in)
    {
        if      (in.minDeg > minDeg) return -1;
        else if (in.minDeg < minDeg) return  1;
        else                         return  0;
    }
}
