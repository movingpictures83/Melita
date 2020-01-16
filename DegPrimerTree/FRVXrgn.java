/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class FRVXrgn
{
    int sF;
    int eF;
    int sR;
    int eR;
    int maxD; // maxDistance allowed between F-R Primers

    public FRVXrgn(int sF, int eF, int sR, int eR, int maxD)
    {
        this.sF = sF;
        this.eF = eF;
        this.sR = sR;
        this.eR = eR;
        this.maxD = maxD;
    }
}
