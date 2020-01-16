
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class DPDesign extends ByteFunc
{
    DB_IO DB;
    String FR;
    ArrayList<DP_Parms> dpDB;
    String fileIDParms;
    
    //Workspace
    String header;
    String toOutput;
    DP_doClst pC;


    public DPDesign(String idDB, boolean is35, String filename) throws FileNotFoundException
    {
        DB = new DB_IO(idDB, is35, filename);    // setup DB
        dpDB = new ArrayList<DP_Parms>();
        FR = DB.is35 ? "F" : "R";
        fileIDParms = "DP16S-" + FR 
                    + "_sC" + CONSTANT.minSeqCntPrimer
                    + "_d"  + CONSTANT.maxDegeneracy
                    + "_CG" + CONSTANT.minCGPerc + "-" + CONSTANT.maxCGPerc;
    }

    
    public void findDPFromTemplate(String filename, boolean printAll) throws IOException
    {
        // this is for RDP database only
        String inDetail = printAll ? "_inDetail" : "";
        FileWriter oFile = new FileWriter(fileIDParms+"_dsgnFlow" + inDetail + ".txt");
        PrintWriter wFile = new PrintWriter(oFile);

        for (DB_SeqParms s: DB.seqList.values())
        {
            if (s.ID.equals("S000004313")) // Ecoli
           //if (s.ID.equals("S000010427")) // Paerg
                findDPfromTemplate(s, wFile, printAll);
        }

        wFile.close();
    }

    public void findTemplateCnt()
    {
        int totalStartCnt = 0;
        int totalTempFound = 0;
        int nSeq = DB.seqList.size();
        int lenSeq = 0;
        int minStartCnt = 2000; int minLenSeq = 2000; int minTempFound = 10000;
        int maxStartCnt = -1;   int maxLenSeq = -1;   int maxTempFound = -1;
        for (DB_SeqParms pT: DB.seqList.values())
        {

            byte [] tData = pT.data;
            int sL = tData.length;
            int l =  sL - CONSTANT.maxLengthPrimer;
            lenSeq += sL;
            if (sL < minLenSeq) minLenSeq = sL;
            if (sL > maxLenSeq) maxLenSeq = sL;
        
            //for (int i=0; i<350; i++)
            int startCnt = 0;
            int templateFound = 0;
            for (int i=0; i<l; i++)
            {
                if (!hasDegMatchStart(Arrays.copyOfRange(tData,i,i+5),0))
                    continue;
                startCnt++;
                boolean foundT = false;
                for (int j=CONSTANT.minLengthPrimer; j<CONSTANT.maxLengthPrimer; j++)
                {
                    DP_Parms T = new DP_Parms(Arrays.copyOfRange(tData,i,i+j),FR);
                    int p = DB.is35 ? tData.length-i : i;

                    if(T.isTemplatePrimer())
                    {
                        //if (!foundT) templateFound++;
                        templateFound++;
                        foundT = true;
                    }
                }
            }
            if (startCnt < minStartCnt) minStartCnt = startCnt;
            if (startCnt > maxStartCnt) maxStartCnt = startCnt;
            if (templateFound < minTempFound) minTempFound = templateFound;
            if (templateFound > maxTempFound) maxTempFound = templateFound;
            totalTempFound += templateFound;
            totalStartCnt += startCnt;

        }
/*
        System.out.println("Min = " + minLenSeq + " " + minStartCnt + " " + minTempFound);
        System.out.println("Max = " + maxLenSeq + " " + maxStartCnt + " " + maxTempFound);
        System.out.println("Avg = "
                +  ((double)lenSeq/nSeq) + " " +
                +  ((double)totalStartCnt/nSeq) + " " +
                   ((double)totalTempFound/nSeq));*/
    }

    private void findDPfromTemplate(DB_SeqParms pT, PrintWriter wFile, boolean printAll)
    {
        byte [] tData = pT.data;
        int l = tData.length - CONSTANT.maxLengthPrimer;
      
        int primersFound = 0;
        int templateFound = 0;
        //for (int i=505; i<510; i++)
        for (int i=0; i<l; i++)
        {
            for (int j=CONSTANT.minLengthPrimer; j<CONSTANT.maxLengthPrimer; j++)
            {
                boolean foundT = false;
                DP_Parms T = new DP_Parms(Arrays.copyOfRange(tData,i,i+j),FR);
                int p = DB.is35 ? tData.length-i : i;
                if(T.isTemplatePrimer())
                {
                    if (!foundT) templateFound++;
                    foundT = true;
                    header = "Pos: " + p + "; " + T.toString();
                    if (findDPBestMatch(toDegString(T.DP), (double)i/l) > CONSTANT.minSeqCntPrimer)
                    {
                        createOneClstDP(false);
                        if (foundToOutput(header, wFile, printAll))
                            primersFound++;
                    }
                }
            }
        }
        System.out.println("Number of Possible Primers = " + dpDB.size());
    }

    public boolean foundToOutput(String header, PrintWriter wFile, boolean printAll)
    {
        boolean found = false;
        if (printAll) // for debug and understanding only
            wFile.print(header+ pC.toOutput);

        else if (!pC.clstPrimerList.isEmpty()) // final design
        {
            found = true;
            for (DP_Parms dp: pC.clstPrimerList)
            {
                byte [] b = dp.DP; if (DB.is35) revByte(b);
                dpDB.add(new DP_Parms(b,FR));
            }
            wFile.print(header + pC.toOutput);
        }
        return found;
    }

    public int findDPBestMatch(String inDP, double relPos)
    {
        pC = new DP_doClst(inDP);
        byte [] byteDP = DegString2DegByte(inDP);

        int cntSeq = 0;
        int minError = 25; int maxError = 0; int multCntInSeq = 0;
        int cntPrimerSeq = 0; int multPrimerInSeq = 0;
        int minPos = 2000; int maxPos = 0; int midPos = 0;

        // find closest primer in seq and add it to the cluster
        // if it is unique and has less than allowed number of mismatches
        for (DB_SeqParms s: DB.seqList.values())
        {
            
            DP_inSeq dp = s.primerDsgnBestinSeqMatch(byteDP, relPos);
            cntSeq++;
            //System.out.println(" out: " + dp.posSeq + " " + dp.nErrors + " " + printDegSeq(dp.seqPrimer));

            // collect for primer stats
            if      (minError > dp.nErrors) minError = dp.nErrors;
            else if (maxError < dp.nErrors) maxError = dp.nErrors;

            // check that it is a valid possible primer region
            if (dp.cntInSeq > 1) multCntInSeq++;
            if (dp.nErrors < 7)
            {
                //System.out.println(toDegString(dp.seqPrimer));
                if (dp.cntInSeq > 1)
                {
                    multPrimerInSeq++;
                }
                else
                {
                    pC.addPrimer(dp.seqPrimer);
                    cntPrimerSeq++;
                    midPos += dp.posSeq;
                    if (minPos > dp.posSeq) minPos = dp.posSeq;
                    else if (maxPos < dp.posSeq) maxPos = dp.posSeq;
                    //if (cntPrimerSeq < 5)
                    //    System.out.println("pos = " + minPos + ", " + maxPos);
                }
            }
            //if (cntSeq == 10) break;
        }

/*        if (cntPrimerSeq > 2000)
        {
            header += "seqCnt = " + cntPrimerSeq + "; pos = " + minPos + ", " +
                    maxPos + ", " + (midPos/cntPrimerSeq);
            header += "; multPrimer: " + multPrimerInSeq + "  Error: " + minError +
                    ", " + maxError + "; multCnt: " + multCntInSeq +"\n";
            System.out.println(header);
        }
 */

        return cntPrimerSeq;
    }


    private void createOneClstDP(boolean startOrgPrimer)
    {
        toOutput = "";
        if (pC.needCluster())
        {
            if (pC.clstFreq(startOrgPrimer))
                toOutput += pC.toOutput;
        }
    }
    
    public void toDPListFile() throws IOException
    {
        String filename = fileIDParms + "_list.txt";

        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);

        for (DP_Parms dpP: dpDB)
            wFile.println(toDegString(dpP.DP));
        wFile.close();
    }

}


           /*

        //int cntGC = 0;
        //int startCnt = 0;
        //int endCnt = 0;
        //int dimerCnt = 0;
        //int TmCnt = 0;
            if (isDegMatch((byte)0x06,tData[i],0))
            {
                cntGC++; //System.out.print(i + " " + toDegString(Ecoli,i,i+21)+"\t");
                if (hasDegMatchStart(tData,i))
                {
                    startCnt++;  //System.out.print(i + " ");
                    for (int j=CONSTANT.minLengthPrimer; j<CONSTANT.maxLengthPrimer; j++)
                    {
                        if (hasDegMatchEnd(tData,i+j-5))
                        {
                            endCnt++; //System.out.print(i + " ");

                            String sTP = toDegString(tData,i,i+j);
                            int p = i; if (DB.is35) p = tData.length-i;
                            header = "Pos: " + p + " TemplatePrimer: " + sTP + " " + "sDimer: " + selfDimer(sTP) + " "; //System.out.println(header);
                            //toOutput = "";
                            if (findDPBestMatch(sTP, (double)i/l) > CONSTANT.minSeqCntPrimer)
                            {
                                createOneClstDP(false, true);
                                if (!pC.clstPrimerList.isEmpty() && !printAll)
                                {
                                    primersFound++;
                                    for (DP_Parms dp: pC.clstPrimerList)
                                    {
                                        byte [] b = dp.DP; if (DB.is35) revByte(b);
                                        dpDB.add(new DP_Parms(b,FR));
                                        //toOutput += dp.toStringClst(p, "");
                                        //toOutput = pC.toOutput;
                                    }
                                    wFile.print(header + pC.toOutput);
                                }
                                if (printAll)
                                    wFile.print(header+ pC.toOutput);
                            }
                }}}}
             * */
        //System.out.println(cntGC + " " + startCnt + " " + endCnt + " " + dimerCnt + " " + TmCnt);
