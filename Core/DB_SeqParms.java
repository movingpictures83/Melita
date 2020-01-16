
import java.util.Arrays;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class DB_SeqParms extends ByteFunc
{
    String ID;
    String phylHier;
    String source;
    int length;
    byte [] data;
    int cntHier;
    int cntData;
    //int deg;
    //int posEcoli;

    public DB_SeqParms(String id, String pH, String src, String seq)
    {
        ID = id;
        phylHier = pH;
        addSeq(seq);
        //deg = 0;
        source = src;
        cntHier = 1;
        cntData = 1;
    }
    
    public void addSpecie(String hier, String seq)
    {
        //System.out.println("adding to " + hier);
        if (hier.equals(phylHier+";"+source)) cntHier++;
        if (seq.equals(toDegString(data))) cntData++;
    }

    public void init(DB_SeqParms FP)
    {
        ID = FP.ID;
        phylHier = FP.phylHier;
        data = FP.data; 
        //deg = FP.deg;
        source = FP.source;
        length = FP.length;
    }

    private void addSeq(String seq)
    {
        //String s = cleanGenBankSeq(seq);
        length = seq.length();
        data = new byte[length];
        Arrays.fill(data,(byte)0x00);
        DegString2DegByte(data,seq,length);
    }

    public String fullHier()
    {
        return phylHier + ";" + source +  ";";
    }

    public DP_inSeq primerDsgnBestinSeqMatch(byte [] inDP, double relPos)
    {
        int dpL = inDP.length;
        int srchS = (int)((relPos-0.15)*length); if  (srchS < 0) srchS = 0;
        int srchE = (int)((relPos+0.15)*length); if  (srchE > length) srchE = length;

        //int sL = seq.length - dpL + 1;
        int srchL = srchE-srchS-dpL+1;
        //int sL = eSeq - dpL + 1;
        //System.out.println("S E L: " + srchS + ", " + srchE + ", " + srchL);

        int [] score = new int [srchL];
        Arrays.fill(score,0);

        int maxScore = 0;
        for (int i = 0; i < srchL; i++)
        {
            int s = 0;
            int k = i+srchS;

            if      (!hasDegMatchStart(data,k))     score[i] = 0;
            else if (!hasDegMatchEnd(data,k+dpL-5)) score[i] = 0;
            else
            {
                for (int j = 0; j < dpL; j++)
                {
                    if (isDegMatch(inDP[j],data[k],2)) s++;  // 0 is a dummy
                    k++;
                }

                if (s > maxScore) maxScore = s;
                score[i] = s;
            }
        }
        int maxScoreCnt = 0;
        int posFirst = -1;
        int posLast = -1;
        int cntFound = 0;
        for (int i = 0; i < srchL; i++)
        {
            if (score[i] <= CONSTANT.nMisMatch)
                cntFound++;
            if (score[i] == maxScore)
            {
                maxScoreCnt++;
                posLast = i;
                if (posFirst < 0) posFirst = i;
            }
        }

        byte [] b = new byte [dpL];
        System.arraycopy(data,posFirst+srchS,b,0,dpL);

        return new DP_inSeq(b,posFirst+srchS,posLast+srchS,dpL-maxScore,
                maxScoreCnt,cntFound);

    }

    public DP_inSeq ExactSeqDPMatch(byte [] inDP)
    {
        int dpL = inDP.length;
        int sL = length - dpL + 1;

        int matchCnt = 0;
        int matchPos = 0;
        int matchPosLast = 0;
        int nErrors  = 1;
        for (int i = 0; i < sL; i++)
        {
            if (isDegMatch(inDP,Arrays.copyOfRange(data, i, i+dpL),2))
            {
                matchCnt++; matchPosLast = i;
                if (matchPos == 0) { matchPos = i; nErrors = 0; }
            }
        }

       byte [] b = new byte [dpL];
       System.arraycopy(data,matchPos,b,0,dpL);

       //System.out.println("matchCnt: " + matchCnt + " matchPos: " + matchPos + " nErrors: " + nErrors);
       return new DP_inSeq(b, matchPos, matchPosLast, nErrors, matchCnt, -1);
    }

    public String printMultCnt()
    {
        String s = "";
        if ((cntHier != 1) || (cntData != 1))
            s = "[" + cntData + ", " + cntHier + "]: " + phylHier+";"+source + "\n";
        return s;
    }

    public int nHier()
    {
        String [] hier = phylHier.split(";");
        if (hier.length < 6)
        {
            System.out.println(phylHier);
        }
        return hier.length;
    }

    //public int getDegenaracy()
    //{
    //    if (deg == 0)
    //        deg = findDegSeq(data);

    //    return deg;
    //}




}
