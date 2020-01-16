
import java.util.Arrays;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class ByteFunc
{

    public int findDegByte(byte b)
    {
        int d = 0;
        int ib = (int)b & 0x1F;
        for (int i = 0; i < 5; i++)
        {
            d = d + (ib&0x1);
            ib = ib>>1;
        }
        return d;
    }

    public long findDegSeq(byte [] b)
    {
        return findDegSeq(b,0,b.length);
    }
    
    public long findDegSeq(byte [] b, int s, int e)
    {
        int d = 1;
        for (int i = s; i < e; i++)
        {
            d = d*findDegByte(b[i]);
        }
        return d;
    }

    // b1 is the template to compare to
    public boolean isDegMatch(byte b1, byte b2, int nMatches)
    {             
        //if      (sel == 1)  return  ((b1 & b2) != 0); // b1 intersection b2 is not empty set
        if      (nMatches == 0)  return !((b1 | b2) > b1); // b2 is subset of b1
        else if (nMatches <= 4)  return (((b1 & b2) != 0) & (findDegByte(b2) <= nMatches)); // allow SNP only
        else                     return false;
                    
    }

    public boolean isDegMatch(byte [] b1, byte b2 [], int sel)
    {
        int l = b1.length;
        for (int i = 0; i < l; i++)
            if (!isDegMatch(b1[i],b2[i],sel)) return false;
        
        return true;
    }

    public boolean hasDegMatchStart(byte [] seq, int start)
    {
        boolean s0 = isDegMatch((byte)0x06,seq[start],0);
        boolean s1 = isDegMatch((byte)0x06,seq[start+1],0);
        boolean s2 = isDegMatch((byte)0x06,seq[start+2],0);
        boolean s3 = isDegMatch((byte)0x06,seq[start+3],0);
        boolean s4 = isDegMatch((byte)0x06,seq[start+4],0);

        // S (SWSW,SWWS,WSSW,WSSW,  SSWW,SWWW,WSWW)
        //if (s0 && (((s1^s2) & (s3^s4)) || ((s1 || s2) & !s3 & !s4))) return true;
        // S (SSW, SW, WS)
        //return (s0 & ((s1^s2) || (s1&!s3)));
        // S (SW,WS) (SS SW WS)
        return (s0 && (s1^s2) && (s3 || s4)) 
                || (s0 && s1 && !(s3 || s4));
        //return true;
    }

    public boolean hasDegMatchEnd(byte [] seq, int start)
    {
        boolean s0 = isDegMatch((byte)0x09,seq[start],0);
        boolean s1 = isDegMatch((byte)0x09,seq[start+1],0);
        boolean s2 = isDegMatch((byte)0x09,seq[start+2],0);
        boolean s3 = isDegMatch((byte)0x09,seq[start+3],0);
        boolean s4 = isDegMatch((byte)0x09,seq[start+4],0);

        // W (W,SWW,SWSW)
        // return (s4 & (s3 || s2&s1 || s2&s0));
        // xxxWW, x[WS,SW]SW]
        return (s4&&!s3&&(s2^s1)) || (s4&&s3);
        //return true;
        
    }

    public String cleanGenBankSeq(String seq)
    {
        seq = seq.replaceAll("[0-9]", "")
                 .replace(" ", "").trim()
                 .toUpperCase();
        return seq;
    }


/*
    public void toDegByte(byte [] degByte, String in)
    {
        toDegByte(degByte,in,CONSTANT.seqLength);
    }

    public void toDegByte(byte [] degByte, String in, int l)
    {
        for (int i = 0; i < l; i++)
        {
            if (degByte[i] > 16) continue;
            char c = in.charAt(i);
            switch (c)
            {
                case 'A': { degByte[i] = (byte)(degByte[i] | 0x01); break; }
                case 'C': { degByte[i] = (byte)(degByte[i] | 0x02); break; }
                case 'G': { degByte[i] = (byte)(degByte[i] | 0x04); break; }
                case 'T': { degByte[i] = (byte)(degByte[i] | 0x08); break; }
                default:  { degByte[i] = (byte)(degByte[i] | 0x10); break; }
            }
        }
    }
*/

    public void toDegByte(byte [] degByte, byte [] in, int length)
    {
        for (int i = 0; i < length; i++)
        {
            degByte[i] = (byte) ((degByte[i] | in[i]) & 0xFF);
        }
    }


    public byte [] DegString2DegByte(String s)
    {
        int l = s.length();
        byte [] sByte = new byte [l];
        Arrays.fill(sByte,(byte)0x00);
        DegString2DegByte(sByte,s,l);
        return sByte;
    }
            
    public void DegString2DegByte(byte [] degByte, String in, int l)
    {
        //System.out.println("LENGTH: "+degByte.length+" OTHER LENGTH: "+l);
        for (int i = 0; i < l; i++)
        {
            if (degByte[i] > 16) continue;
            char c = in.charAt(i);
            switch (c)
            {
                case 'A': { degByte[i] = (byte)(degByte[i] | 0x01); break; }
                case 'C': { degByte[i] = (byte)(degByte[i] | 0x02); break; }
                case 'G': { degByte[i] = (byte)(degByte[i] | 0x04); break; }
                case 'T': { degByte[i] = (byte)(degByte[i] | 0x08); break; }
                case 'M': { degByte[i] = (byte)(degByte[i] | 0x03); break; }
                case 'R': { degByte[i] = (byte)(degByte[i] | 0x05); break; }
                case 'S': { degByte[i] = (byte)(degByte[i] | 0x06); break; }
                case 'W': { degByte[i] = (byte)(degByte[i] | 0x09); break; }
                case 'Y': { degByte[i] = (byte)(degByte[i] | 0x0A); break; }
                case 'K': { degByte[i] = (byte)(degByte[i] | 0x0C); break; }
                case 'V': { degByte[i] = (byte)(degByte[i] | 0x07); break; }
                case 'H': { degByte[i] = (byte)(degByte[i] | 0x0B); break; }
                case 'D': { degByte[i] = (byte)(degByte[i] | 0x0D); break; }
                case 'B': { degByte[i] = (byte)(degByte[i] | 0x0E); break; }
                case 'N': { degByte[i] = (byte)(degByte[i] | 0x0F); break; }
                default:  { degByte[i] = (byte)(degByte[i] | 0x10); break; }

            }
        }
    }

    public byte[] toDegByte(byte [] degByte, byte [] in)
    {
        byte[] retval = new byte[Math.min(degByte.length, in.length)];
        for (int i = 0; i < retval.length; i++)
        {
            retval[i] = (byte) ((degByte[i] | in[i]) & 0xFF);
            //System.out.println("[toDegByte] RETVAL AT "+i+": "+degByte[i]);
        }
        return retval;
    }

    public String toDegString(byte [] degByte)
    {
        return toDegString(degByte, 0, degByte.length);
    }

    // [ACGT][AC]A[CT]GA[AG]CG[AC]A[AC]CC[CT] = NMAYGARCGMAMCCY
    public String toDegString(int degDNA)
    {
        switch(degDNA)
            {
                case  1: { return "A"; }    // A
                case  2: { return "C"; }    // C
                case  8: { return "T"; }    // T
                case  4: { return "G"; }    // G
                
                case  3: { return "M"; }    // CA
                case  5: { return "R"; }    // GA
                case  6: { return "S"; }    // GC
                case  7: { return "V"; }    // GCA

                case  9: { return "W"; }    // TA
                case 10: { return "Y"; }    // TC
                case 11: { return "H"; }    // TCA
                case 12: { return "K"; }    // TG
                case 13: { return "D"; }    // TGA
                case 14: { return "B"; }    // TGC
                case 15: { return "N"; }    // TGCA
                case 16: { return "-"; }    // all -
                default: { return "X";}    // - & A/C/G/T
            }

    }

    public String toDegString(byte [] degByte, int s, int e)
    {
        String degSeq = "";
        for (int i = s; i < e; i++)
        {
            int degDNA = (int)degByte[i] & 0xFF;
            switch(degDNA)
            {
                case  1: { degSeq += "A"; break;}    // A
                case  2: { degSeq += "C"; break;}    // C
                case  3: { degSeq += "M"; break;}    // CA
                case  4: { degSeq += "G"; break;}    // G
                case  5: { degSeq += "R"; break;}    // GA
                case  6: { degSeq += "S"; break;}    // GC
                case  7: { degSeq += "V"; break;}    // GCA
                case  8: { degSeq += "T"; break;}    // T
                case  9: { degSeq += "W"; break;}    // TA
                case 10: { degSeq += "Y"; break;}    // TC
                case 11: { degSeq += "H"; break;}    // TCA
                case 12: { degSeq += "K"; break;}    // TG
                case 13: { degSeq += "D"; break;}    // TGA
                case 14: { degSeq += "B"; break;}    // TGC
                case 15: { degSeq += "N"; break;}    // TGCA
                case 16: { degSeq += "-"; break;}    // all -
                default: { degSeq += "X"; break;}    // - & A/C/G/T
            }
             //System.out.println(degSeq);
        }
        //System.out.println(degSeq.substring(0,5));
        return degSeq;
    }

    public String toRevCompString(byte [] degByte)
    {
        String degSeq = "";
        int size = degByte.length;
        for (int i = 0; i < size; i++)
        {
            int degDNA = (int)degByte[i] & 0xFF;
            switch(degDNA)
            {
                case  1: { degSeq = "T" + degSeq; break;}    // A -> T
                case  2: { degSeq = "G" + degSeq; break;}    // C -> G
                case  4: { degSeq = "C" + degSeq; break;}    // G -> C
                case  8: { degSeq = "A" + degSeq; break;}    // T -> A

                case  3: { degSeq = "K" + degSeq; break;}    // CA -> GT
                case  5: { degSeq = "Y" + degSeq; break;}    // GA -> CT
                case  6: { degSeq = "S" + degSeq; break;}    // GC -> GC
                case  7: { degSeq = "B" + degSeq; break;}    // GCA -> GCT
                
                case  9: { degSeq = "W" + degSeq; break;}    // TA -> TA
                case 10: { degSeq = "R" + degSeq; break;}    // TC -> AG
                case 11: { degSeq = "D" + degSeq; break;}    // TCA -> AGT
                case 12: { degSeq = "M" + degSeq; break;}    // TG -> AC
                case 13: { degSeq = "H" + degSeq; break;}    // TGA -> ACT
                case 14: { degSeq = "V" + degSeq; break;}    // TGC -> ACG
                case 15: { degSeq = "N" + degSeq; break;}    // TGCA
                case 16: { degSeq = "-" + degSeq; break;}    // all -
                default: { degSeq = "X" + degSeq; break;}    // - & A/C/G/T
            }
             //System.out.println(degSeq);
        }
        //System.out.println(degSeq.substring(0,5));
        return degSeq;
    }

    public String toCompString(byte [] degByte)
    {
        String degSeq = "";
        int size = degByte.length;
        for (int i = 0; i < size; i++)
        {
            int degDNA = (int)degByte[i] & 0xFF;
            switch(degDNA)
            {
                case  1: { degSeq += "T"; break;}    // A -> T
                case  2: { degSeq += "G"; break;}    // C -> G
                case  4: { degSeq += "C"; break;}    // G -> C
                case  8: { degSeq += "A"; break;}    // T -> A

                case  3: { degSeq += "K"; break;}    // CA -> GT
                case  5: { degSeq += "Y"; break;}    // GA -> CT
                case  6: { degSeq += "S"; break;}    // GC -> GC
                case  7: { degSeq += "B"; break;}    // GCA -> GCT

                case  9: { degSeq += "W"; break;}    // TA -> TA
                case 10: { degSeq += "R"; break;}    // TC -> AG
                case 11: { degSeq += "D"; break;}    // TCA -> AGT
                case 12: { degSeq += "M"; break;}    // TG -> AC
                case 13: { degSeq += "H"; break;}    // TGA -> ACT
                case 14: { degSeq += "V"; break;}    // TGC -> ACG
                case 15: { degSeq += "N"; break;}    // TGCA
                case 16: { degSeq += "-"; break;}    // all -
                default: { degSeq += "X"; break;}    // - & A/C/G/T
            }
             //System.out.println(degSeq);
        }
        //System.out.println(degSeq.substring(0,5));
        return degSeq;
    }

    public String printDegSeq(byte [] degByte)
    {
       return printDegSeq(degByte, 0, degByte.length);
    }
    
    public String printDegSeq(byte [] degByte, int b, int e)
    {
        String out = "DegSeq from " + b + " to " + e + "\n";
        return out + printDegSeq(toDegString(degByte, b, e));
    }
    
    public String printDegSeq(String degSeq)
    {    
        String out = "";
        int l = degSeq.length();
        int x = ((l-1)/60)*60;
        for (int i = 0; i < x; i = i+60)
        {
            out += i + "\t" + degSeq.substring(i,i+60) + "\n";
        }
        out += x + "\t" + degSeq.substring(x,l) + "\n\n";
        return out;
    }

    public String printByte(byte [] in)
    {
        String s = "";
        for (int i = 0; i < in.length; i++)
            s += Integer.toString(in[i]);
        return s;
    }

 
    public double getEntropy(double [] in)
    {
        int l = in.length;
        double e = 0.0;
        for (int i=0; i<l; i++)
        {
            double d  = in[i];
            e += d*Math.log(d);
        }   
        e = -e/Math.log(2);
        return e;
    }
    
    public void cnt2prob(int [] in, double [] out)
    {
        int l = in.length;
        int total = 0;
        for (int i=0; i<l; i++)
            total += in[i];
        cnt2prob(in,out,total);
    }
    
    public void cnt2prob(int [] in, double [] out, int total)
    {
        int l = in.length;
        double t = (double) total;
        for (int i=0; i<l; i++)
        {
            out[i] = (in[i]+0.5)/(t+1);
        }
    }


    public byte [] revCompl(byte [] in)
    {
        int l = in.length;
        byte [] rc = new byte [l];
        Arrays.fill(rc,(byte)0x00);
        
        int indx = l-1;
        for (int i=0; i<l; i++)
        {
            rc[indx] += ((int)in[i]&0x1) << 3;
            rc[indx] += ((int)in[i]&0x2) << 1;
            rc[indx] += ((int)in[i]&0x4) >> 1;
            rc[indx] += ((int)in[i]&0x8) >> 3;
            indx--;
        }
        return rc;
    }

    public byte [] compl(byte [] in)
    {
        int l = in.length;
        byte [] rc = new byte [l];
        Arrays.fill(rc,(byte)0x00);

        for (int i=0; i<l; i++)
        {
            rc[i] += ((int)in[i]&0x1) << 3;
            rc[i] += ((int)in[i]&0x2) << 1;
            rc[i] += ((int)in[i]&0x4) >> 1;
            rc[i] += ((int)in[i]&0x8) >> 3;
        }
        return rc;
    }

    public void revByte(byte [] in)
    {
        int l = in.length;
        int mid = l/2;
        l--;
        for (int i=0; i<mid; i++)
        {
            byte t = in[i];
            in[i] = in[l-i];
            in[l-i] = t;
        }
    }
    
    public int selfDimer(String in)
    {  
        return selfDimer(DegString2DegByte(in));
    }
    
    public int selfDimer(byte [] in, int s, int size)
    {
        byte [] inRange = Arrays.copyOfRange(in, s, s+size);
        return selfDimer(inRange);
    }
    
    public int selfDimer(byte [] org, byte dna, int pos)
    {
        byte [] temp = Arrays.copyOf(org, org.length);
        temp[pos]= dna;
        return selfDimer(temp);
    }
   
    public int selfDimer(byte [] in)
    {
        // find LCS between in and reverse complement of in
        //System.out.println("Find reverse complement");
        
        int l = in.length;
        byte [] rc = revCompl(in);
        
        int lM = l+1; 
        int [] mT = new int [lM];  Arrays.fill(mT, 0);
        int sDimer = 0;
        for (int i=0; i<l; i++)
        {
            for (int j=l; j>0; j--)
            {
                if (isDegMatch(rc[j-1],in[i],4))
                {
                    mT[j] = mT[j-1]+1; 
                    if(mT[j] > sDimer) sDimer = mT[j];
                }
                else mT[j] = 0;
            }
        }
        //System.out.println("selfDimer for " + toDegString(in) + " is " + sDimer);
        return sDimer;
    }

    public int areDimers(byte [] in1, byte [] in2, boolean isCR)
    {
        if (isCR) return areDimers(in1,in2);
        else      return areDimers(in1,revCompl(in2));
    }
    public int areDimers(byte [] in1, byte [] in2)
    {

        int lM = Math.max(in1.length,in2.length)+1;
        int [] mT = new int [lM];  Arrays.fill(mT, 0);
        int dimer = 0;
        for (int i=0; i<in1.length; i++)
        {
            for (int j=in2.length; j>0; j--)
            {
                if (isDegMatch(in2[j-1],in1[i],4))
                {
                    mT[j] = mT[j-1]+1;
                    if(mT[j] > dimer) dimer = mT[j];
                }
                else mT[j] = 0;
            }
        }
        //System.out.println("selfDimer for " + toDegString(in) + " is " + sDimer);
        return dimer;
    }

    public boolean hasRunRepeat(byte [] in)
    {
        int l = in.length;
        for (int k=0; k<4; k++)
        {
            int cnt = 0;
            for (int i=0; i<l; i++)
            {
                byte b = (byte)(1<<k);
                if ((in[i] & b) == 0) cnt = 0;
                else
                {
                    cnt++;
                    if (cnt == 5) return true;
                }
            }
        }
        return false;
    }

    public boolean isProteinMatch(byte [] p1, byte [] p2)
    {
        return true;
    }

}
