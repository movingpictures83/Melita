
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class DPStatsMain
{
    public static void main(String [] argv) throws FileNotFoundException, IOException
    {
        FP_IO.printHeader();
        /*
        String primerListID = "";
        primerListID = "NossaPrimers";
        primerListID = "UniversalPrimers";
        //primerListID = "Barcodes";
        DP_StatFunc SFP = new DP_StatFunc("RDP", false);
        SFP.addDP("inputFiles/" + primerListID + ".txt", true, true);
        SFP.statDPExact();
        SFP.toStatFile(primerListID + "_stats.txt");
        //SFP.printBarcode(primerListID + "_stats.txt");
       */
        //ByteFunc BF = new ByteFunc();
        //DP_StatFunc SFP = new DP_StatFunc("RDP", false);
        //System.out.println(SFP.statDPExact(BF.DegString2DegByte("TGCATGGWWGTCGTCAGC")).toString("RDP"));
        //System.out.println(SFP.statDPExact(BF.DegString2DegByte("TGCATGGYYGTCGTCAGC")).toString("RDP"));
        HashMap<String, String> parameters = FP_IO.readKeywordValuePairs(argv[0]);
        String idDS = "selPP"; //"election"; //PaperMonOct28"; //"wikiU"; //"PaperTue" ; //"exp2"; //"HMP" ; //V35T";
        String DB = "RDP";
        //String dir = "Ideal1/";
        String testID = DB + "_" + idDS;
        DP_StatFunc SFA = new DP_StatFunc(DB,false,idDS, parameters.get("sequencefile"));
        SFA.addAmpl(parameters.get("primerfile"), true, true);
        /*if (idDS.equals("Exp2"))
            SFA.addAmpl("inputFiles/amplExp2All.txt", true, true);
        else if (idDS.equals("Nossa"))
            SFA.addAmpl("inputFiles/NossaPrimerPair.txt", true, true);
        else if (idDS.equals("HMP"))
            SFA.addAmpl("inputFiles/HMPPrimerPair.txt", true, true);
        else if (idDS.equals("V35T"))
            SFA.addAmpl("inputFiles/V35PrimerPair.txt", true, true);
        else if (idDS.equals("exp2"))
            SFA.addAmpl("inputFiles/exp2DesignAmplList.txt", true, true);
        else if (idDS.equals("PaperTue"))
            SFA.addAmpl("inputFiles/TuePaperPrimers.txt", true, true);
        else if (idDS.equals("PaperMonOct28"))
            SFA.addAmpl("inputFiles/MonOct28PaperPrimers.txt", true, true);
        else if (idDS.equals("wikiU"))
            SFA.addAmpl("inputFiles/wikiU.txt", true, true);
        else if (idDS.equals("election"))
            SFA.addAmpl("inputFiles/PrimerPairsElection.txt", true, true);
        else if (idDS.equals("PPNov17"))
            SFA.addAmpl("inputFiles/PrimerPairNov17.txt", true, true);
        else if (idDS.equals("selPP"))
            SFA.addAmpl(dir+"SelectPP.txt", true, true);*/

        SFA.statDPExact(0);
        //SFA.toPrintAmplPrimers("; ");
        SFA.toAmplSeqFile(argv[1]+"/", false);
        SFA.toAmplStatFile("amplStats_" + testID + ".txt", "\t");
        //if (idDS.equals("Universal"))
        //{
        //    SFA.statDPExact();
        //    SFA.toStatFile(dir+"UniversalPrimersStatsAll.txt", "\t");
        //}
        //else
        //{
            
        //}
        SFA.combineTwoAmpl2("combine2Ampl_" + testID + ".txt");
        SFA.printTaxonMap(argv[1]+"/");
        SFA.printTaxonsNew(argv[1]+"/", 4);
 

/*
        DP_StatFunc SFD = new DP_StatFunc("RDP",false);
        SFD.addAmpl("inputFiles/startDiversity.txt", true);
        SFD.statDPExact(0);
        SFD.findStartEndDiversity("s");
        SFD.toAmplStatFile("amplStats/amplStatsRDP_startDiversity.txt");

 */

        /* ampliconss
        SFA.addAmpl("inputFiles/DesignedPrimerPair.txt", true);
        SFA.statDPExact(0); // 0 is dummy
        SFA.toAmplSeqFile();
        SFA.toAmplStatFile("amplStats/amplDesignedPStats.txt");
        SFA.combineTwoAmpl("amplStats/amplDesigned_combine2.txt");
        */

        //SF.combineTwoAmpl("amplStats/combine2Ampl_lisa.txt");
        //DP_StatFunc SF = new DP_StatFunc("RDP",false);
        //SF.addDP("inputFiles/barcodeList.txt", true, true);
        //SF.statDPExact();
        //SF.toStatFile("barcodeList_stats.txt");



        //SF.addDP("inputFiles/barcodeList.txt", true, true);
        //SF.statDPExact();
        //SF.toStatFile("barcodeList_stats.txt");
        //SF.matchFrwdRevPrimers();
        //SF.toAmplSeqFile();
        //SF.toAmplStatFile("amplStats/amplStatsSilva_wiki.txt");
        //SF.addAmpl("inputFiles/lisa_FR-DPmatch.txt", true);
        //SF.statDPExact(0); // 0 is dummy
        //SF.toAmplSeqFile();
        //SF.toAmplStatFile("amplStats/amplStatsRDP_lisa.txt");
        //SF.combineTwoAmpl("amplStats/combine2Ampl_lisa.txt");

        //SF.DB.setNewDB("HMDP", false);
        //SF.statDPExact(0); // 0 is dummy
        //SF.toAmplSeqFile();
        //SF.toAmplStatFile("amplStats/amplStatsHMDP_lisa.txt");


    }
}
