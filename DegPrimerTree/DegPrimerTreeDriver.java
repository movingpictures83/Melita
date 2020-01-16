/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.FileWriter ;
import java.io.PrintWriter ;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;


public class DegPrimerTreeDriver
{
    //String inFileName;
    //String outSeqLeaf;
    //String outSeqMeta;
    int treeHeight;
    int minHeight;
    int seqCnt;
    String maxLeaf;
    String minLeaf;
    public HashMap<String, Locus> metaLOCUS = new HashMap<String,Locus> ();
    //public HashMap<String,DegSeq> OrganismSeq = new HashMap<String,DegSeq> ();
    public DegPrimerTree BactTree = new DegPrimerTree();

    public DegPrimerTreeDriver()
    {
        //inFileName = in;
        //outSeqLeaf = seq;
        //outSeqMeta = meta;
        treeHeight = 0;
        minHeight = 20;
        seqCnt =0;
    }

    public void createMetaData(String inFileName, String outSeqMeta) throws IOException
    {
        String s = "";
        Scanner fileScan = new Scanner(new File(inFileName));
        //System.out.println("STARTING");
        boolean isMetaData = true;
        int i = 0;

        String seqMeta = "";
        String seqData = "";
        while (fileScan.hasNextLine()) // while not eof
        {
            String line = fileScan.nextLine();

            StringTokenizer tLine = new StringTokenizer(line);
            String cmd = tLine.nextToken();
            if (cmd.equals("ORIGIN"))
            {
                //getMetaData(seqMeta);
                isMetaData = false;
                seqData = line;
            }
            else if (cmd.equals("//"))  // && !isMetaData)
            {
                procSeq(seqMeta,seqData);
                isMetaData = true;
                seqMeta = line;

                //i++;
                //if (i == 1000)
                //  break;
            }
            else
            {
                if (isMetaData) seqMeta += line;
                else            seqData += line;
            }
            
        }

        /*
        for (Locus l: metaLOCUS.values())
        {
            System.out.println(l.toString());
        }
        for (DegSeq o: OrganismSeq.values())
        {
            System.out.println(o.degSeq.length());
            o.printDegSeq();
        }
*/
        fileScan.close();
        //System.out.println("Number of seqeunces in the tree: " + metaLOCUS.size()); // 7618
        //System.out.println("Number of leaves in the tree: " + OrganismSeq.size()); // 1569
        //System.out.println("Organism Tree maxHeight: " + treeHeight + ", " + maxLeaf);
        //System.out.println("Organism Tree minHeight: " + minHeight + ", " + minLeaf);
        //System.out.println("SeqLength = " + CONSTANT.seqLength);
    }

    

    private void procSeq(String metaData, String seqData)
    {
        int indxDef = metaData.indexOf("DEFINITION");
        int indxAcc = metaData.indexOf("ACCESSION");
        int indxOrg = metaData.indexOf("ORGANISM");
        int indxRef = metaData.indexOf("REFERENCE");

        // Find LOCUS ID
        String LocusLine = metaData.substring(0,indxDef);
        StringTokenizer tLocus = new StringTokenizer(LocusLine);
        tLocus.nextToken();
        String LocusID = tLocus.nextToken().trim();
        CONSTANT.seqLength = Integer.parseInt(tLocus.nextToken().trim());
        

        // Find Source and Strain
        String Definition = metaData.substring(indxDef, indxAcc)
                                    .replace("DEFINITION", "")
                                    .trim();

        // Find Organism Tree
        String Organism = metaData.substring(indxOrg, indxRef);
        int indxRoot = Organism.indexOf("Root");
        int indxDot  = Organism.indexOf(".");
        Organism = Organism.substring(indxRoot+5,indxDot).trim();
        Organism = Organism.replace(" ", "");
        

        // clean up dataSeq
        seqData = seqData.replace("ORIGIN", "");

        if (metaLOCUS.containsKey(LocusID))
            System.out.println("HMMM: Not unique ID: " + LocusID);
        //else if (!Organism.contains("unclassified"))
        //{
            metaLOCUS.put(LocusID, new Locus(LocusID, Definition, Organism));
            String Source = Organism +";"+ metaLOCUS.get(LocusID).Source + ";;";
            //Source = Organism +";"+ ";";

            //System.out.println(Source);
            //if (OrganismSeq.containsKey(Source))
            //{
            //    DegSeq x = OrganismSeq.get(Source);
            //    x.addSeq(seqData, LocusID);
            //}
            //else
            //{
                //OrganismSeq.put(Source, new DegSeq(Source,seqData,LocusID,4100));
                
                updateTreeHeight(Source);
                BactTree.addNode(Source, seqData, LocusID);
            seqCnt++;
                
                //dN.addDegSeq(seqData, LocusID);
            //}


        //}
        
        //System.out.println("MetaData\n" + data);

    }

    private void updateTreeHeight(String org)
    {
        String [] s = org.split(";");
        if (treeHeight < s.length) 
        { 
            treeHeight = s.length; maxLeaf = org;
        }

        else if (minHeight > s.length)
        {
            minHeight = s.length; minLeaf = org;
        }
    }

    public void printLeaves(String outFile) throws IOException
    {
        BactTree.printLeaves(outFile);
    }
    

    public void stats()
    {
        BactTree.stats();
    }


    
        /**** Print into a file *****/
        /*
        FileWriter oFile = new FileWriter(outFile);
        PrintWriter wFile = new PrintWriter(oFile);

        String s = "";
        int i = 0;
        int j = 0;
        for (DegSeq o: OrganismSeq.values())
        {
            s += o.toString();
            if (i == 500)
            {
                wFile.print(s);
                i = 0;
                s = "";
                System.out.println("j = " + j);
                j++;
            }

        }
         
         */
        //for (DegSeq o: OrganismSeq.values())
        //{
        //    wFile.print(o.toString());
        //}
        //wFile.prin
        //wFile.close();
    //}
}
