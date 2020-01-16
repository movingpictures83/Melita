
import java.util.Arrays;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class DP_Parms extends ByteFunc
{

    String ID;
    byte [] DP; // byte version of String DP
    String FR;  // either F or R

    int dpLength;
    long deg;
    int sDimer;
    TmParms TmP;


    public DP_Parms(byte [] bDP, String FR)
    {
        ID = "";
        this.FR = FR;
        dpLength = bDP.length;
        DP = new byte [dpLength]; DP = Arrays.copyOf(bDP, dpLength);
        initSetUp();
    }

    public DP_Parms(byte [] bDP, String FR, String ID)
    {
        this.ID = ID;
        this.FR = FR;
        dpLength = bDP.length;
        DP = new byte [dpLength]; DP = Arrays.copyOf(bDP, dpLength);
        initSetUp();
    }

    public DP_Parms(String sDP, String FR, String ID)
    {
        this.ID = ID;
        this.FR = FR;
        dpLength = sDP.length();
        DP = new byte [dpLength];
        DP = Arrays.copyOf(DegString2DegByte(sDP), dpLength);
        initSetUp();
    }

    private void initSetUp()
    {
        deg = findDegSeq(DP);
        sDimer = selfDimer(DP);
        TmP = new TmParms(DP,0,dpLength);
    }

    public boolean isTemplatePrimer()
    {
        //System.out.println(hasDegMatchStart(DP,0) + " " + hasDegMatchEnd(DP,dpLength-5) +
        //        " " + isClstPrimer());
        return hasDegMatchStart(DP,0) && hasDegMatchEnd(DP,dpLength-5) && isClstPrimer();
    }

    public boolean isClstPrimer()
    {
        boolean isSD  = (sDimer > CONSTANT.maxSelfDimer);
        boolean isTmR = TmP.isTmRange(CONSTANT.minTm, CONSTANT.maxTm, CONSTANT.rangeTm);
        boolean isRR = hasRunRepeat(DP);
        boolean isCGR = TmP.isCGRange();

        return !isSD && isTmR && !isRR && isCGR;
    }

    public boolean validStartEnd()
    {
        // start: S[S(Wxx, xWx), WSS]
        boolean ss0 = isDegMatch((byte)0x06,DP[0],0);
        boolean ss1 = isDegMatch((byte)0x06,DP[1],0);
        boolean ss2 = isDegMatch((byte)0x06,DP[2],0);
        //boolean ss3 = isDegMatch((byte)0x06,DP[3],0);
        //boolean ss4 = isDegMatch((byte)0x06,DP[4],0);

        boolean sw0 = isDegMatch((byte)0x09,DP[0],0);
        boolean sw1 = isDegMatch((byte)0x09,DP[1],0);
        boolean sw2 = isDegMatch((byte)0x09,DP[2],0);
        //boolean sw3 = isDegMatch((byte)0x09,DP[3],0);
        //boolean sw4 = isDegMatch((byte)0x09,DP[4],0);
        
        boolean startOK = (ss0 && !sw0) && (( ss1 && !sw1 && !ss2 &&  sw2) ||
                                            (!ss1 &&  sw1 &&  ss2 && !sw2));

        boolean ew0 = isDegMatch((byte)0x09,DP[dpLength-1],0);
        boolean ew1 = isDegMatch((byte)0x09,DP[dpLength-2],0);
        boolean es0 = isDegMatch((byte)0x06,DP[dpLength-1],0);
        //boolean ew2 = isDegMatch((byte)0x09,DP[dpLength-3],0);
        //boolean ew3 = isDegMatch((byte)0x09,DP[dpLength-4],0);
        //boolean ew4 = isDegMatch((byte)0x09,DP[dpLength-5],0);


        boolean endOK = (ew0 && !es0) || (es0 && ew1);
        
        return startOK && endOK;
    }

    public boolean validGC()
    {
        return (TmP.minCG >= (int)(dpLength*CONSTANT.minCGPerc/100.0)) &&
               (TmP.maxCG <= (int)Math.ceil(dpLength*CONSTANT.maxCGPerc/100.0));
    }
    
    public boolean isValid(int nSeq)
    {
        return isClstPrimer() &&  validStartEnd() && (nSeq >= CONSTANT.minSeqCntPrimer);
    }

    public int isCompatible(DP_Parms P1)
    {
        int out = 0;
        if (!TmP.isTmCompatable(P1.TmP)) out = out + 1;
        if (areDimers(DP,P1.DP,true) > CONSTANT.maxSelfDimer) out = out + 2;
        return out;
    }

    public String toStringClst(int nSeq, String comment)
    {
        String toOutput = comment + "clstPrimer = " + toDegString(DP);
        toOutput += "; deg = " + deg + "; nSeq = " + nSeq + "; sDimer = " + sDimer;
        toOutput += "; Tm = " + TmP.toStringTm(" -> ");
        toOutput += "; CG = " + TmP.toStringCG(" -> ") +";\n";

        return toOutput;
    }

    public String toString()
    {
        String toOutput = "Primer = " + toDegString(DP);
        toOutput += "; deg = " + deg + "; sDimer = " + sDimer;
        toOutput += "; Tm = " + TmP.toStringTm(" -> ");
        toOutput += "; CG = " + TmP.toStringCG(" -> ") +";\n";

        return toOutput;
    }

    public String toString(String dlmt)
    {
        String toOutput = ID + dlmt;
        if (FR.equals("R")) toOutput += toDegString(revCompl(DP));
        else                toOutput += toDegString(DP);
        toOutput += dlmt + deg + dlmt + sDimer + dlmt +
                TmP.toStringTm(dlmt) + dlmt + TmP.toStringCG(dlmt) + dlmt;

        return toOutput;
    }

    
}
