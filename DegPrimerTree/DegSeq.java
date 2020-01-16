
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import java.util.TreeSet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class DegSeq extends ByteFunc
{
    private class orgSeq
    {
        String seqID;
        int origLength;
    }

    String grpName;
    byte [] degByte;
    HashSet<String> seqID;
    int startSeq;
    int endSeq;
    ArrayList<DegParms> degList;
    int origLength;
    int conservedLength;
   
    public DegSeq(String gName)
    {
        grpName = gName;
        seqID = new HashSet<String>();
        degByte = new byte [CONSTANT.seqLength];
        Arrays.fill(degByte,(byte)0x00);

        startSeq = 0;
        endSeq = CONSTANT.seqLength - 1;
        degList = new ArrayList<DegParms> ();

        origLength = 0;
        conservedLength = 0;
    }

    public void setParms(DegSeq in)
    {
        addDegSeq(in.degByte);
        startSeq = in.startSeq;
        endSeq = in.endSeq;
        conservedLength = in.conservedLength;
        degList.addAll(in.degList);
    }

    public void findOrigLength()
    {
        origLength = 0;
        for (int i = 0; i < degByte.length; i++)//CONSTANT.seqLength; i++)
            if (degByte[i] != 0x10) origLength++;
    }
    
    public int findOrigSeq(byte [] orgSeq)
    {
        int k = 0;
        for (int i = startSeq; i < endSeq; i++)
            if (degByte[i] != 0x10) 
                orgSeq[k++] = degByte[i];
            
        return k;
    }
/*
    public boolean findSubSeq(String ss)
    {
        int sizeSS = ss.length();

        byte [] ssByte = new byte [ss.length()];
        Arrays.fill(ssByte,(byte)0x00);
        toDegByte(ssByte,ss);

        //byte [] orgSeq = new byte [CONSTANT.seqLength];
        //int orgLen = findOrigSeq(orgSeq);

        int i = startSeq;
        int j = 0;
        int next = 0;
        int end = endSeq - sizeSS - 1;
        while (i < end)
        {
            next = i+1;
            while (j < sizeSS)
            {

                while (degByte[i] >= 0x10) i++;
                if ((degByte[i] & ssByte[j]) != 0))
                {
                    i++; j++;
                while (j < sizeSS)
                {
                    if (degByte[i] != 0x10)

                }
                i = next;
                j = 0;
            }
            i = next;
        }


        return (j == sizeSS);
    }
*/
    public void addSeq(String seq, String ID)
    {
        if (!seqID.contains(ID)) {  // Avoid duplicates
        seqID.add(ID);
        if (seqID.size() == 1)
          DegString2DegByte(degByte, cleanSeq(seq),CONSTANT.seqLength);
        }
    }

    public void addDegSeq(byte [] seq)
    {
        degByte = toDegByte(degByte, seq);
    }

    private String cleanSeq(String seq)
    {
        seq = seq.replaceAll("[0-9]", "")
                 .replace(" ", "").trim()
                 .toUpperCase();
        if (seq.length() != CONSTANT.seqLength)
            System.out.println("ERROR: SeqLength is not " + CONSTANT.seqLength);
        return seq;
    }

    public void createDP()
    {
        pruneX();
        if (degList.isEmpty())
        {
            degList.add(new DegParms(0,0));
        }
        else 
        {
            findPrimerDeg();
            if (degList.isEmpty())
            {
                degList.add(new DegParms(0,0));
            }
            for (DegParms dp: degList)
                conservedLength += dp.l;
        }
    }




    private void pruneX()
    {
        //int eS = degByte.length;
        if (degByte.length/*endSeq*/ - startSeq < CONSTANT.minLengthPrimer)
        {
            System.out.println(grpName + " : no Primer, start");
            //degList.add(new DegParms(0, 0));
        }
        else
        {
            int i = startSeq;
            while (degByte[i] > 0x0F) i++;
            startSeq = i; 

            i = degByte.length-1;//endSeq; 
            while (degByte[i] > 0x0F) i--;
            endSeq = i;

            int state = 0;
            int s = 0;

            for (i = startSeq; i<= endSeq; i++)
            {
                if (state == 1)
                {
                    if (degByte[i] > 0x10)
                    {
                        if ((i-s) >= CONSTANT.minLengthPrimer)
                            degList.add(new DegParms(s, i-1));
                        state = 0;
                    }
                    //else if (degByte[i] == 0x10)
                    //{
                    //    ; // ignore gap
                    //}
                }
                else if (degByte[i] < 0x11) // remove X 
                {
                     s = i;
                     state= 1;
                }
            }
            if (state == 1)
            {
                 if ((i-s) >= CONSTANT.minLengthPrimer)
                    degList.add(new DegParms(s, i-1));
                 state = 0;
            }
        }
    }

    public boolean hasPrimerRgn(DegFRPair FR, FRVXrgn VX, String name)
    {
        boolean found = false;
        DegParms fDP = new DegParms(0,0);
        DegParms rDP = new DegParms(0,0);
        boolean hasF = findPrimerRgn(fDP, VX.sF, VX.eF);
        boolean hasR = findPrimerRgn(rDP, VX.sR, VX.eR);
        found = hasF && hasR && (fDP.e + VX.maxD >= rDP.s);
        if (found)
        {
            FR.setParms(fDP.s, fDP.e, rDP.s, rDP.e, degByte);
        }

        return found;
    }

    private boolean findPrimerRgn(DegParms DP, int sRgn, int eRgn)
    {
       //System.out.print("\t\tfindPrimerRgn ");
       //System.out.print(sRgn + " " + endSeq + " " + eRgn + " " + startSeq + " ");
       if ((degList.get(0).s == degList.get(0).e) ||
            (sRgn > endSeq) || (eRgn < startSeq))  return false;

       int s = Math.max(startSeq,sRgn);
       int e = Math.min(endSeq,eRgn);
       int i = s;
       while (degByte[i] > 0x0E) // cannot start with X,Gap,N
       {
           i++; if (i==e) return false;
       }
       DP.s = i; 

       i = e; 
       while (degByte[i] > 0x0E) i--; // cannot end with X,Gap,N
       DP.e = i; 

       return (DP.s + CONSTANT.minLengthPrimer) <= DP.e;
    }


    private void findPrimerDeg()
    {
       int size = degList.size();
       int [] pMinSeq = new int [CONSTANT.minLengthPrimer];
       int k = 0;
       while (k < size)
       {
            DegParms dp = degList.get(k);
            int s = dp.s;
            int e = dp.e;
            if (s == e) System.out.println("Why am I here?");

            Arrays.fill(pMinSeq,0);
            long deg = 1;
            int gapCnt = 0;
            int l = 0;  // length
            int j = 0;  // primerArrayCnt
            int i = s;  // seqcnt
            while ((j < CONSTANT.minLengthPrimer) && (i <= e))
            {
                if (degByte[i] < 0x10)
                {
                    int dbyte = findDegByte(degByte[i]);
                    pMinSeq[j++] = dbyte;
                    deg = deg*dbyte;
                }
                else gapCnt++;

                i++;
            }


            if (j != CONSTANT.minLengthPrimer)
            {
                degList.remove(k);
                size--;
            }
            else
            {
            //for (int i = s; i < s+CONSTANT.minLengthPrimer; i++)
            //{
            //    int dbyte = findDegByte(degByte[i]);
            //    pMinSeq[j++] = dbyte;
            //    deg = deg*dbyte;
            //}
                long minDeg = deg;
                long maxDeg = deg;

                j = 0;
                //for (int i = s+CONSTANT.minLengthPrimer; i <= e; i++)
                while (i <= e)
                {
                    if (degByte[i] < 0x10)
                    {
                        int dbyte = findDegByte(degByte[i]);
                        int d_pL = pMinSeq[CONSTANT.minLengthPrimer-j-1];
                        pMinSeq[j++] = dbyte;
                        deg = deg*dbyte/d_pL;

                        if      (deg < minDeg) minDeg = deg;
                        else if (deg > maxDeg) maxDeg = deg;
                        if (j == CONSTANT.minLengthPrimer) j = 0;
                    }
                    else gapCnt++;

                    i++;
                }


                if (minDeg > CONSTANT.maxDegeneracy)
                {
                    degList.remove(k); size--;
                }
                else
                {
                    dp.minDeg = (int) minDeg;
                    dp.maxDeg = (int) maxDeg;
                    dp.l = e-s+1-gapCnt;
                    degList.set(k, dp);
                
                    k++;
                }
            }
       }
    }
  
    public String toString(String organism)
    {
        String s = organism + ";" + grpName + " \n" + "seqCnt = " + seqID.size() + "; ";
        s += seqID.toString() + "\n";

        String degSeq = toDegString(degByte);
        //int x = (CONSTANT.seqLength/60)*60;
        int x = (degSeq.length()/60)*60;
        for (int i = 0; i<x; i = i+60)
        {
            s += i + "  " + degSeq.substring(i,i+60) + "\n";
        }
        //s += x + "  " + degSeq.substring(x,CONSTANT.seqLength) + "\n\n";
        s += x + "  " + degSeq.substring(x,degSeq.length()) + "\n\n";
        return s;
    }

    public String printDegList(String organism)
    {
        String s = organism + ";" + grpName + "\n";
        //int indx = organism.indexOf("B");
        String offset = " ";
        //if (indx > 0) offset = s.substring(0,indx);
        //s += seqID.toString() + "\n";

        for (DegParms p: degList)
        {
            s += offset + p.toString() + "\n";
        }

        return s;// + printDegSeq(300, 400);
    }
}

