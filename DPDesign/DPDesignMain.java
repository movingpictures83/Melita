
import java.io.FileNotFoundException;
import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class DPDesignMain
{
    public static void main(String [] argv) throws FileNotFoundException, IOException
    {
        //String dir = "Ideal/";
        FP_IO.printHeader();
        System.out.println("******* FORWARD PRIMERS *******");
        DPDesign DP_f = new DPDesign("RDP",true, argv[0]);
        DP_f.findTemplateCnt();
        DP_f.findDPFromTemplate("", false);
        DP_f.toDPListFile();
        DP_f.DB.seqList.clear();
        System.out.println("*******************************");
 
        System.out.println("******* REVERSE PRIMERS *******");
        DPDesign DP_r = new DPDesign("RDP",false,argv[0]);
        DP_r.findTemplateCnt();
        DP_r.findDPFromTemplate("", false);
        DP_r.toDPListFile();
        DP_r.DB.seqList.clear();
        System.out.println("*******************************");
 /*
 
        DP_StatFunc SF = new DP_StatFunc("RDP",false, "");
        //SF.addDP(DP_f.dpDB);
        //SF.addDP(DP_r.dpDB);
        SF.addDP(dir+"DP16S-F_sC7500_d65_CG48-70_list.txt",false);
        SF.addDP(dir+"DP16S-R_sC7500_d65_CG48-70_list.txt",false);
        //SF.addDP("inputFiles/testTm-F.txt",false);
        //SF.addDP("inputFiles/testTm-R.txt",false);
        
        SF.statDPExact();
        //SF.toStatFile(dir+"DesignPrimersStatsAllHD650.txt", "\t");
        SF.sortPerRgn();
        SF.toStatFile(dir + "DP_designedAll_stats.txt", "\t");
        SF.toStatRgnFile(dir + "DP_perRgn_designedAll.txt", "\t");
        SF.matchFrwdRevPrimers(dir);
        //SF.toAmplSeqFile(dir+"amplSeq/", false);
        //SF.toAmplStatFile(dir + "amplStatsRDP_designedHD800.txt", "\t");
        //SF.combineTwoAmpl2(dir + "combine2Ampl_designedHD800.txt");
        //SF.printTaxonMap(dir);

        //SF.DB.setNewDB("HMDP",false);
        //SF.statDPExact();
        //SF.toAmplSeqFile();

 */
/*
        SF.DB.setNewDB("Silva",false);
        SF.statDPExact();
        SF.toAmplSeqFile();
        SF.toStatFile("DP_designed_stats.txt");
        SF.toStatRgnFile("DP_perRgn_designed.txt");
        SF.toAmplStatFile("amplStats/amplStats_designed.txt");
        SF.combineTwoAmpl("amplStats/combine2Ampl_designed.txt");
        //SF.findDPRgns();
*/
        //342, 510, 551, 779, 1060, 1088, 1367
        //1351, 1052, 1015, 869, 749, 469
    }
}
