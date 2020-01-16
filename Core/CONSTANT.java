/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class CONSTANT
{
    public static int seqLength = 4100;
    //public static final int seqLength = 3051;
    public static int minSeqCntPrimer = 7500;
    public static int minLengthPrimer = 17;
    public static int maxLengthPrimer = 24;
    public static int maxDegeneracy = 65; // 65; //256; //8192; // 2^13
    public static int nMisMatch = 6; // (log2(maxDegeneracy)
    public static int maxSelfDimer = 6;
    public static int minTm = 48;
    public static int maxTm = 70;
    public static int rangeTm = 7;
    public static int rangeTmFR = 8;
    public static int minCGPerc = 48;
    public static int maxCGPerc = 70;
    public static int minAmplLength = 150;
    public static int maxAmplLength = 700;
    public static int cntSeqDoubleDeg = 35;

    public static int WS_gap = -100;    // enough to eliminate gaps
    public static int WS_match = 1;     // perfect match
    public static int WS_noMatch = 0;   // counts how many
    public static int WS_degMatch = 1;  //divide by degeneracy if need be

    public static int ignoreCnt = 15;
    public static int nNoHits = 5;

    public static int nDB = 3;
    public static int idRDP   = 0;
    public static int idHMDP  = 1;
    public static int idSilva = 2;
    public static String refDB = "RDP";
    public static String pathHMDP = "../bioDB/HMDP/hmpd16SOne.gbk";
    public static String pathRDP  = "../bioDB/RDP/rdp_download_9175seqs.gen"; // 7592
    public static String pathSilva = "../bioDB/silva.bacteria/nogap.bacteria.fasta";
}
