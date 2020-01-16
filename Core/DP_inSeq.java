
import java.util.Arrays;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */

public class DP_inSeq extends ByteFunc
{
    byte [] seqPrimer;
    int posSeq;
    int posSeqLast;
    int nErrors;
    boolean isInSeq;
    int cntInSeq;
    int cntInSeqAll;

    public DP_inSeq(byte [] in, int pos, int posLast, int nE, int cnt, int cntAll)
    {
        seqPrimer = new byte[in.length];
        seqPrimer = Arrays.copyOf(in, in.length);
        posSeq = pos;
        posSeqLast = posLast;
        nErrors = nE;
        isInSeq = nE == 0;
        cntInSeq = cnt;
        cntInSeqAll = cntAll;
    }

    public String toString()
    {
        String s = "pos: " + posSeq + " nErr: " + nErrors;
        s += " DP: " + toDegString(seqPrimer,0,seqPrimer.length);
        return s + "\n";
    }
}
