
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class DegPrimerTreeMain
{
    public static void printPrimerPairs(ArrayList<DegFRPair> dPPList, String filename) throws IOException
    {

        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);
        //System.out.println(dPPList.size());

        for (DegFRPair p: dPPList)
            wFile.println(p.toString());

        wFile.close();
    }

    public static void main(String [] args) throws IOException
    {
        FP_IO.printHeader();
        //String infile = "/Users/melita/Documents/BioInfo/rdp_download_7618seqs.gen";
        //String infile = "../bioDB/RDP/nA_rdp_download_7600seqs.gen";//"rdp_download_7600seqs.gen";
        //String infile = "rdp_download_7592seqs.gen";//../bioDB/RDP/rdp_download_7592seqs.gen";//"rdp_download_7600seqs.gen";
        String infile = args[0];
        int indx1 = infile.indexOf("seqs");
        //int indx2 = infile.indexOf(infile)
        String count = infile.substring(indx1-4,indx1);
        System.out.println("number of sequences: " + count);
        DegPrimerTreeDriver PD = new DegPrimerTreeDriver();
        PD.createMetaData(infile, null);
        PD.BactTree.getOrgLength();
        //PD.BactTree.printLeaves("DegTreeLeaves_" + count +  ".txt");
       
        //System.out.println("Find max number of children: " + PD.BactTree.root.findMaxChild());
        //PD.BactTree.findNodeByName("Escherichi");

       
        //PD.findPrimerPair(sFP, eFP, sRP, eRP, maxDistFR, minLenghtDP, maxDegenaracy);
        PD.BactTree.createDegLeaves();
        //PD.BactTree.createDegLeaves("DegPrimerListLeaves.txt");
       
        PD.BactTree.propagateDegSeq();
        //System.out.println("STATS");
        //PD.stats();
        //PD.BactTree.cntStat();
        //System.out.println("Bacteria children: " + PD.BactTree.root.childDegPrimer.size());
        /*
        FRVXrgn VX = new FRVXrgn(1050, 1150, 1400, 1500, 550);
        ArrayList<DegFRPair> dPPListVX = new ArrayList<DegFRPair>();
        PD.BactTree.findDPPinVX(VX, dPPListVX);
        String PrimerID = count + "_" + VX.sF + "_" + VX.eR;
        PD.BactTree.printPairs("PrimerPairs_" + PrimerID + ".txt");
        PD.BactTree.corrRegionPairs("corrRegionPair_" + PrimerID + ".txt");
        //PD.BactTree.sameNodeType();
         */
        PD.BactTree.clusterChildNodes();
        FRVXrgn VX = new FRVXrgn(1050, 1150, 1400, 1500, 550);
        ArrayList<DegFRPair> dPPListVX = new ArrayList<DegFRPair>();
        PD.BactTree.findDPPinVX(VX, dPPListVX);
        //PD.BactTree.degPrimerStats("degPrimerPerNodeNoGaps_" + count, true);
        //PD.BactTree.degPrimerRegions("DPperNodeNoGapsTop_" + count + ".txt");
        //PD.BactTree.degPrimerRegionsMeta("DPperNodeNoGapsTopMeta_" + count + ".txt");

        /*
        String seq = "2761 CT-GT-CGT- C-AGCT-CGT -G-TC-GTGA ---------- ---------- GAT--GT-TG"
     +"2821 G-G--TT-AA GT--C-CC-G CA-A-CG--A -G-CGC-AAC --CCTT-A-- C-C---CG-T"
     +"2881 -AGTT-G-CC -A-G--CGcg tcatgg---- ---------- ---------- ----------"
     +"2941 ---------- ---------- ---------- ---------- ---------- ---------C"
     +"3001 G--G-G-A-A CT-C-T-A-C -G-G-GG--- -A-C-T-G-- C---CC-G-G -G---T-T--";
        seq = seq.replaceAll("[1-9]", "");
        System.out.println(seq);
       * */

    }
}
