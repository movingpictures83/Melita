
import java.io.FileNotFoundException;
import java.util.HashMap;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class TestMain
{

    public static void main (String [] args) throws FileNotFoundException
    {
        //HashMap<String,String> t  = new HashMap<String,String>();
        //t.put("A", "AA");
        //System.out.println(t.toString());
        //t.put("B", "BB");
        //System.out.println(t.toString());
        //t.put("A", "AB");
        //System.out.println(t.toString());

        ByteFunc BF = new ByteFunc();

        byte [] b = BF.DegString2DegByte("CGTTCYCRGRYCTTGTACA");
        BF.revByte(b);
        System.out.println(BF.toDegString(b));

        b = BF.DegString2DegByte("CGGACGGGTGAGTAATGCCT");
        System.out.println(BF.hasDegMatchEnd(b,b.length-5));

        b = BF.DegString2DegByte("CCCGTCAATTCMTTTRAGT");
        System.out.println(BF.toDegString(b) + "\n"+ BF.toDegString(BF.revCompl(b)));

        //System.out.println(BF.selfDimer("TGCCAGCAGCCGCGGTA"));
        //DB_IO DB = new DB_IO("RDP", false);
        DP_Parms dpP = new DP_Parms(BF.DegString2DegByte("CGCAAGGTTAAAACTCA"), "F");
        System.out.println(dpP.toString());
        System.out.println(dpP.sDimer > CONSTANT.maxSelfDimer);
        System.out.println(dpP.TmP.isTmRange(CONSTANT.minTm, CONSTANT.maxTm, CONSTANT.rangeTm));
        System.out.println(BF.hasRunRepeat(dpP.DP));
        System.out.println(BF.hasDegMatchStart(dpP.DP, 0));
        //DP_StatFunc SF = new DP_StatFunc("RDP",false);
        //System.out.println(SF.statDPExact(dpP.DP).toString("RDP"));
        //System.out.println(SF.statDPExact(BF.DegString2DegByte("CTGCCCGCCACACATGTT")).toString("RDP"));
        //SF.DB.setNewDB("RDP",false);
        //System.out.println(SF.statDPExact(BF.DegString2DegByte("TTGTACACACCGCCCGTC")).toString("RDP"));
        //SGCAAGDNTRAAACTCAAA

        /*
        System.out.println(BF.isDegMatch(BF.DegString2DegByte("CGTTCYCRGRYCTTGTACA"),
                BF.DegString2DegByte("CGTCCCCGGGCCTTGTACA"), 1));
        System.out.println(BF.isDegMatch(BF.DegString2DegByte("CGTTCY"),
                BF.DegString2DegByte("CGTCCC"), 1));
        System.out.println(BF.isDegMatch(BF.DegString2DegByte("CRG"),
                BF.DegString2DegByte("CGG"), 1));
        System.out.println(BF.isDegMatch(BF.DegString2DegByte("YCTTGTACA"),
                BF.DegString2DegByte("CCTTGTACA"), 1));
        DP_Parms dpP1 = new DP_Parms(BF.DegString2DegByte("CGTCCCCGGGCCTTGTACA"), "F");
        System.out.println(dpP1.toString());
        DP_Parms dpP2 = new DP_Parms(BF.DegString2DegByte("CGTYCYCRGRYCTTGTACA"), "F");
        System.out.println(dpP2.toString());
         */

        //System.out.println(BF.getTm("AGTSCNVYAACGARCGCAACC"));
        //System.out.println(BF.getTm("TSCNVYAACGARCGCAACC"));
        //System.out.println(BF.getTm("SCNVYAACGARCGCAACC"));
        //BF.getTm("AGTSCNVYAACGARCGCAACC");
        //System.out.println(Math.log(2));
        //double [] p = {0.499, 0.001, 0.499, 0.001};
        //int [] d = {3790, 3790, 10, 10};
        //int [] d = {6,1};
        //double [] p = new double [d.length]; //{0.999, 0.001};
        //BF.cnt2prob(d,p);
        //for (int i = 0; i < d.length; i++)
        //    System.out.println(d[i] + " " + p[i] + " " + p[i]*Math.log(p[i])/Math.log(2.0));
        //System.out.println(BF.getEntropy(p));
    }
}
/*
 * Primer = CGCAAGGTTAAAACTCA; deg = 1; sDimer = 4; Tm = 42.23 -> 42.23; CG = 0.41 -> 0.41;
falsetruetrue
Primer = CGCAAGGTTAAAACTCAA; deg = 1; sDimer = 4; Tm = 43.49 -> 43.49; CG = 0.39 -> 0.39;
falsetruetrue
Primer = CGCAAGGTTAAAACTCAAA; deg = 1; sDimer = 4; Tm = 44.62 -> 44.62; CG = 0.37 -> 0.37;
falsetruetrue
Primer = CGCAAGGTTAAAACTCAAAT; deg = 1; sDimer = 4; Tm = 45.63 -> 45.63; CG = 0.35 -> 0.35;
falsetruetrue
Primer = CGCAAGGTTAAAACTCAAATG; deg = 1; sDimer = 4; Tm = 48.50 -> 48.50; CG = 0.38 -> 0.38;
falsetruefalse
Primer = CGCAAGGTTAAAACTCAAATGA; deg = 1; sDimer = 4; Tm = 49.25 -> 49.25; CG = 0.36 -> 0.36;
falsetruetrue
Primer = CGCAAGGTTAAAACTCAAATGAA; deg = 1; sDimer = 4; Tm = 49.93 -> 49.93; CG = 0.35 -> 0.35;
falsetruetrue
 */