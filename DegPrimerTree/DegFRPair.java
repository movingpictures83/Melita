
import java.util.ArrayList;
import java.util.PriorityQueue;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class DegFRPair extends ByteFunc
{
    private class Pairs implements Comparable<Pairs>
    {
        int sF, eF, dF;
        int sR, eR, dR;

        public Pairs(int sF, int eF, int dF,
                     int sR, int eR, int dR)
        {
            this.sF = sF;  this.eF = eF;  this.dF = dF;
            this.sR = sR;  this.eR = eR;  this.dR = dR;
        }

        public int compareTo(Pairs P)
        {
            if ((dF <  P.dF) && (dR <  P.dR))    return -1;
            if ((dF <  P.dF) && (dR == P.dR))    return -1;
            if ((dF == P.dF) && (dR <  P.dR))    return -1;

            if ((dF >  P.dF) && (dR >  P.dR))    return  1;
            if ((dF >  P.dF) && (dR == P.dR))    return  1;
            if ((dF == P.dF) && (dR >  P.dR))    return  1;

            if ((dF == P.dF) && (dR == P.dR))
            {
                if (((eF - sF) >  (P.eF - P.sF)) &&
                    ((eR - sR) >= (P.eR - P.sR)))   return -1;
                if (((eF - sF) <  (P.eF - P.sF)) &&
                    ((eR - sR) <= (P.eR - P.sR)))   return  1;
                if ((sR-eF) <= (P.sR -P.eF))        return -1;
                else                                return  1;
            }
            if (Math.max(dF,dR) > Math.max(P.dF, P.dR))
                 return  1;
            else return -1;
            
        }

        public String toString()
        {
            return " " + sF + " " + eF + " " + sR + " " + eR + " ";
        }
    }

    String grpName;
    ArrayList<DegParms> fPL;
    ArrayList<DegParms> rPL;

    int sF;
    int sR;
    int eF;
    int eR;
    int lF;
    int lR;
    byte [] fByte;
    byte [] rByte;
    byte [] ForwardDP;
    byte [] ReverseDP;
    boolean foundPair;

    public DegFRPair(String name)
    {
        grpName = name;
        fPL = new ArrayList<DegParms>();
        rPL = new ArrayList<DegParms>();
        foundPair = false;
    }

   
    public void setParms(int sF, int eF, int sR, int eR, byte [] degByte)
    {
        this.sF = sF;  this.eF = eF;    fPL.clear();
        int sizeF = eF-sF+1;
        fByte = new byte[sizeF];
        System.arraycopy(degByte,sF,fByte,0,sizeF);

        this.sR = sR;  this.eR = eR;    rPL.clear();
        int sizeR = eR-sR+1;
        rByte = new byte[sizeR];
        System.arraycopy(degByte,sR,rByte,0,sizeR);
    }

    public void createPrimers()
    {
        int fIndx = eF-sF;
        System.out.println("FORWARD PRIMER: "+toDegString(fByte,0,eF-sF+1));
        while (fIndx >= CONSTANT.minLengthPrimer)
        {
            fIndx = createForwardPrimers(fIndx);

        }
        System.out.println("Number of forward primers: " + fPL.size());

        int rIndx = 0;
        System.out.println("REVERSE PRIMER: "+toDegString(rByte,0,eR-sR+1));
        while ( rIndx <= eR-sR-CONSTANT.minLengthPrimer+1)
        {
            rIndx = createReversePrimers(rIndx);
        }
        System.out.println("Number of reverse primers: " + rPL.size());
    }

     private int createForwardPrimers(int sIndx)
     {
        //int sSeq = CONSTANT.minLengthPrimer - 1;

        // get first minSeq
        int indxSeq = sIndx;
        int endDNA = indxSeq;
        int lengthDNA = 0;
        int state = 0;
        int deg = 1;
        int degB = 0;
        while (state < 2)
        {
            if (state == 0)
            {
                if (fByte[indxSeq] < 0xE)
                {
                    degB = findDegByte(fByte[indxSeq]);      
                    lengthDNA++;
                    deg = degB;
                    state = 1;
                    endDNA = indxSeq;
                }
            }
            else if (state == 1)
            {
                if (fByte[indxSeq] >= 0x11) return indxSeq;
                else if (fByte[indxSeq] < 0x10)
                {
                    degB = findDegByte(fByte[indxSeq]);
                    lengthDNA++;
                    deg = deg*degB;
                }
            }
            
            if (deg > CONSTANT.maxDegeneracy) return endDNA-1;
            if ((lengthDNA >= CONSTANT.minLengthPrimer) && (degB != 4))
                state = 2;
            indxSeq--;
            if (indxSeq < 0) return 0;
        }

        while ((indxSeq >= 0) && findDegByte(fByte[indxSeq]) == 1)
            indxSeq--;
        DegParms dP = new DegParms(indxSeq+1+sF,endDNA+sF);
        dP.minDeg = deg;
        fPL.add(dP);
        return endDNA-1;

     }

    private int createReversePrimers(int sIndx)
            //(ArrayList<DegParms> fPL, byte [] fByte, int sSeq, int eSeq)
    {
        // create longest possible primers for each pos that is not N/-
        // most deg will be max, but keep track of min as well
        //int eSeq = eR - sR - CONSTANT.minLengthPrimer + 1;

        // get first minSeq
        int offset = eR-sR;
        int indxSeq = sIndx;
        int startDNA = sIndx;
        int lengthDNA = 0;
        int state = 0;
        int deg = 1;
        int degB = 0;
        while (state < 2)
        {
            if (state == 0)
            {
                if (rByte[indxSeq] < 0xE)
                {
                    degB = findDegByte(rByte[indxSeq]);
                    lengthDNA++;
                    deg = degB;
                    state = 1;
                    startDNA = indxSeq;
                }
            }
            else if (state == 1)
            {
                if (rByte[indxSeq] >= 0x11) return indxSeq;
                else if (rByte[indxSeq] < 0x10)
                {
                    degB = findDegByte(rByte[indxSeq]);
                    lengthDNA++;
                    deg = deg*degB;
                }
            }
            
            if (deg > CONSTANT.maxDegeneracy) return startDNA+1;
            if ((lengthDNA >= CONSTANT.minLengthPrimer) && (degB != 4))
                state = 2;
            indxSeq++;
            if (indxSeq > offset) return offset;
        }

        while ((indxSeq <= offset) && findDegByte(rByte[indxSeq]) == 1)
            indxSeq++;
        DegParms dP = new DegParms(startDNA+sR,indxSeq-1+sR);
        dP.minDeg = deg;
        rPL.add(dP);
        return startDNA+1;
    }

    public boolean createPair(int maxD)
    {
            //System.out.println("START OF CREATEPAIR");
        if (fPL.isEmpty() || rPL.isEmpty()) return false;


        PriorityQueue<Pairs> pQ = new PriorityQueue<Pairs>();
        for (DegParms f: fPL)
        {
            for (DegParms r: rPL)
            {
                if (f.e + maxD >= r.s)
                {
                    Pairs p = new Pairs(f.s,f.e,f.minDeg,r.s,r.e,r.minDeg);
                    pQ.add(p);
                }
            }
        }

        if (pQ.isEmpty()) return false;

        // get the primer with smallest degeneracy
        
            Pairs P= pQ.remove();
            //System.out.println(grpName);
            int k = 0;
            int s = P.sF-sF;
            int l = P.eF-P.sF+1;
            int e = s+l;
            ForwardDP = new byte[l];
            for (int i = s; i < e; i++)
            {
                if (fByte[i] < 0x10)
                    ForwardDP[k++] = fByte[i];
            }
            lF = k;
            //System.out.println("ForwardPrimer: " + toDegString(ForwardDP,0,lF));



            k = 0;
            s = P.sR-sR;
            l = P.eR-P.sR+1;
            e = s+l;
            ReverseDP = new byte[l];
            for (int i = s; i < e; i++)
            {
                if (rByte[i] < 0x10)
                    ReverseDP[k++] = rByte[i];
            }
            lR = k;
            //System.out.println("ReversePrimer: " + toDegString(ReverseDP,0,lR));
            foundPair = true;
            //System.out.println("END OF CREATEPAIR: "+foundPair);
            return foundPair;
    }

    public String toString(String name)
    {
        String s = name + grpName + "\n\t";
        s += sF + " " + eF + " " + sR + " " + eR + "\n";
        s += "\tForwardPrimer: " + toDegString(ForwardDP,0,lF-1) + "\n";
        s += "\tReversePrimer: " + toDegString(ReverseDP,0,lR-1) + "\n";
        return s;
    }

    /*
     *
    public boolean XXX(ArrayList<DegParms> nuclP, FRVXrgn VX)
    {
        int size = degList.size();
        if (degList.get(0).e == 0) return false;
        //int sF = VX.sR - VX.maxD - CONSTANT.minLengthPrimer;
        //if (sF < VX.sF) sF = VX.sF;
        //int eR = VX.sR - VX.maxD - CONSTANT.minLengthPrimer;
        //if (sF < VX.sF) sF = VX.sF;
        boolean foundForward = false;
        boolean foundReverse = false;
        boolean doneForward = false;
        boolean doneReverse = false;
        int k = 0;
        while (k < size && !doneForward & !doneReverse)
        {
            DegParms dp = degList.get(k);

            // check if possible region
            if ((VX.sF + CONSTANT.minLengthPrimer) > dp.e)
            {
                k++; continue;
            }
            if (VX.eR < (dp.s + CONSTANT.minLengthPrimer))
            {
                 break;
            }

            // find forward primer possible region

            if (VX.SF > dp.s)
            else if ((VX.sF >= dp.s) && (VX.eF <= dp.e)) // fP within rgn
                {
                    foundForward = pruneGap(nuclP, VX.sF, VX.eF);
                    k++;
                }
            else
            {
                doneForward = true;
                k++;
            }





            if ((VX.sF >= s) & (VX.eF <= e)) // fP within rgn
            {
                foundReverse = pruneGap(nuclP, VX.sF, VX.eF);
                doneReverse = true;
            }

            k++;
        }

        return foundForward && foundReverse && checkDistance(nuclP);
    }

    public boolean pruneGap(ArrayList<DegParms> nuclP, int s, int e)
    {


                int cntNucl = 0;
                for (int i = s; i < e; i++)
                    if (degByte[i] < 0x10) cntNucl++;
                if (cntNucl > CONSTANT.minLengthPrimer)
                {

                }
            return true;
    }
     */

    
}
