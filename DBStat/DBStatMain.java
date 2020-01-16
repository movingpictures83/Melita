
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class DBStatMain extends ByteFunc
{
    public static void main(String [] argv) throws FileNotFoundException, IOException
    {
        FP_IO.printHeader();
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters = FP_IO.readKeywordValuePairs(argv[0]);
        CONSTANT.pathSilva = parameters.get("Silva");//"nogap.bacteria.fasta";
        CONSTANT.pathHMDP = parameters.get("HMDP");//"hmpd16SOne.gbk";
        ByteFunc BF = new ByteFunc();
        DB_IO DB = new DB_IO("RDP",false,parameters.get("RDP"));
        //System.out.println("RDP");
        //DB.getSeqCntStats(false);
        //DB.getHierHist();
        DB.createPhylumList(false);
        //DB.createGenusHierList();
        DB.createGenusList(false);
        //DB.createSpeciesHierList();
        DB.createSpeciesList(false);
        //DB.findGenus("inputFiles/ErbGenusList.txt");

        DB.setNewDB("Silva", true, parameters.get("Silva"));
        System.out.println("Silva");
        DB.getSeqCntStats(false);
        DB.setNewDB("HMDP", true, parameters.get("HMDP"));
        System.out.println("HMDP");
        DB.getSeqCntStats(false);
/*
RDP: 0: ../bioDB/RDP/rdp_download_9175seqs.gen
STARTING
Number of seqeunces: 9175
RDP
cntUniqSeq: 8853
cntUniqSpecie: 8376
cntUniqGenus: 1779
cntDistSeq: 8829
         *
         *
         *
RDP: 0: ../bioDB/RDP/nA_rdp_download_7592seqs.gen
STARTING
Number of seqeunces: 9175
RDP
cntUniqSeq: 8853
cntDistSeq: 8829
Number of seqeunces: 7592
RDP
cntUniqSeq: 7440
cntDistSeq: 7417
Silva: 1: ../bioDB/silva.bacteria/nogap.bacteria.fasta
STARTING
Number of seqeunces: 14956
Silva
cntUniqSeq: 14855
cntDistSeq: 14758
HMDP: 0: ../bioDB/HMDP/hmpd16SOne.gbk
STARTING
Number of seqeunces: 756
HMDP
cntUniqSeq: 388
cntDistSeq: 371
         */
         
/*
        HashMap<String, HashMap<String, Integer>> indxName =
                new HashMap<String, HashMap<String,Integer>>();

        for (FP_PrimerSeq pS: HMDP.seqList.values())
        {
            String name = pS.seq.phylHier + ";" + pS.seq.source;
            if (!indxName.containsKey(name))
                indxName.put(name, new HashMap<String,Integer>());
            String s = BF.toDegString(pS.seq.data);
            s = s.substring(30, s.length()-30);
            HashMap<String, Integer> sL = indxName.get(name);
            if (!sL.containsKey(s))
                sL.put(s, 0);
            sL.put(s, sL.get(s)+1);
        }

        int cntM = 0;
        int cntU = 0;
        int cntD = 0;
        for (Entry<String,HashMap<String,Integer>> e: indxName.entrySet())
        {
            HashMap<String, Integer> sL = e.getValue();
            if (sL.size() > 1)
            {
                System.out.println("> 1" + e.getKey());
                cntM += sL.size();
            }
            else 
            {
                for (Integer i: sL.values())
                {
                    System.out.println(" > " + i + e.getKey());
                    if (i==1) cntU++;
                    else cntD += i;
                }
            }
        }
        //Cnt = [91, 40, 234]
        System.out.println("Cnt = [" + cntU + ", " + cntM + ", " + cntD  + "]");
    */
    }

}
