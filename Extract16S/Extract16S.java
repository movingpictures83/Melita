
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class Extract16S extends ByteFunc
{
    private class Pos16S
    {
        int start;
        int end;
        boolean isComplement;
        String data;

        public Pos16S (int s, int e, boolean compl)
        {
            start = s; end = e; isComplement = compl; data = "";
        }
    }

    private class GenBankSeq
    {
        String LocusID;
        String Organism;
        String Source;
        int orgLength;
        String gbData;
        ArrayList<Pos16S> pos;
        
        public GenBankSeq(String ID, String Src, String Org, int length)
        {
            LocusID = ID;
            Source = Src;
            Organism = Org;
            orgLength = length;
            pos = new ArrayList<Pos16S>();
        }

        private String toStringGB(Pos16S p, String indx)
        {
            //Pos16S p = pos.get(i);
            int e = p.end;
            int s = p.start-1;
                
            // construct genbank entry
            String gb = "LOCUS   \t" + LocusID + indx + "\t" + (e-s) + " " + p.isComplement + " \n";
            //gb += "LOCUS   \t" + LocusID + "\t" + (e-s) + " \n";
            gb += "DEFINITION\t\n";
            gb += "SOURCE  \t" + Source + "\n";
            gb += "ORGANISM\t" + Source + "\n        \t" + Organism + ".\n";
            gb += "REFERENCE\t\n";
            gb += "ORIGIN  \t\n" + p.data + "\n//\n";

            return gb;
        }

        public String toGenBank(boolean onlyOne)
        {
            if (onlyOne) return toGenBankOne();
            else         return toGenBankAll();
        }
        
        private String toGenBankOne()
        {

            //findIndex
            int size = pos.size();
            int indx = 0;

            if (size != 1)
            {
                int minDiff = pos.get(0).end - pos.get(0).start - 1500;
                //System.out.print( minDiff + " ");
                for (int i = 1; i < size; i++)
                {
                    Pos16S p = pos.get(i);
                    int diff = Math.abs(p.end - p.start - 1500);
                    //System.out.print( diff + " ");
                    if (diff < minDiff)
                    {
                        minDiff = diff; indx = i;
                    }
                }
            }
            return toStringGB(pos.get(indx),"");
        }

        public String toGenBankAll()
        {
            String gb = "";

            //for (int i = 0; i< pos.size(); i++)
            int i = 0;
            for (Pos16S p: pos)
            {
                gb += toStringGB(p, "-"+i);
                i++;
            }
            if (pos.size() > 1) theSame();
                    
            return gb;
        }

        public void setData()
        {
            for (Pos16S p: pos)
            {
                String data = p.data.trim();
                int indxData = data.indexOf(" ");
                int start = Integer.parseInt(data.substring(0,indxData));

                String seq = cleanGenBankSeq(data);
                seq = seq.substring(p.start-start,p.end-start+1);
                if (p.isComplement)
                    seq = toRevCompString(DegString2DegByte(seq));
                p.data = seq;
            }
        }

        private void theSame()
        {
            gbData = pos.get(0).data;
            for (int i = 1; i < pos.size(); i++)
            {
                Pos16S p = pos.get(i);
                int e = p.end;
                int s = p.start-1;
                int l = e-s;
                if (l != gbData.length())
                {
                    continue;
                }
                String data = p.data;

                int end = (gbData.length()/20)*20;
                if(!gbData.equals(data))
                {
                    System.out.println("****looking for mismathes*****");
                    for (int j = 0; j < end; j = j+20)
                    {
                        String s1 = gbData.substring(j,j+20);
                        String s2 = data.substring(j,j+20);
                        if (!s1.equals(s2))
                            System.out.println("mismatch@" + j + ": " + s1 + " " + s2);
                    }
                    System.out.println(gbData.length() + " " + 
                            gbData.substring(0,20) + " " +
                            gbData.substring(l-10,l));
                    System.out.println(data.length() + " " + 
                            data.substring(0,20) + " " +
                            data.substring(l-20,l));
                    //throw new IllegalArgumentException("ERROR in " + LocusID + ": 16S are not the same");
                }
            }
        }

        //public void theSameLength()
        //{
        //    if (pos.size() > 1)
        //    {
        //        Pos16S p = pos.get(0);
        //        int orgLength = p.end - p.start;
        //    }
        //}
    }

    HashMap<String,GenBankSeq> list16S = new HashMap<String,GenBankSeq>();


    public Extract16S(String inFilename) throws FileNotFoundException // file with all filenames
    {
        parseFileList(inFilename);
    }

    public Extract16S() { }

    private void parseFileList(String filename) throws FileNotFoundException
    {
        Scanner fileScan = new Scanner(new File(filename));
        int indx = filename.lastIndexOf("/");
        String dir = "";
        if (indx>0) dir = filename.substring(0,indx+1);
        while (fileScan.hasNextLine()) // while not eof
        {
            parseFile(dir + fileScan.nextLine());
        }

        fileScan.close();
    }

    private void parseFile(String filename) throws FileNotFoundException
    {
        Scanner fileScan = new Scanner(new File(filename));

        boolean isMetaData = true;
        boolean has16S = false;
        boolean parseMeta = false;
        String seqMeta = "";
        String seqData = "";
        String id = "";
        while (fileScan.hasNextLine()) // while not eof
        {
            String line = fileScan.nextLine();
            //System.out.println(line);
            if (line.trim().equals("")) continue;

            StringTokenizer tLine = new StringTokenizer(line);
            String cmd = tLine.nextToken();
            //System.out.println(cmd);
            if (cmd.equals("ORIGIN"))
            {
                id = parseMetaData(seqMeta);
                has16S = !id.equals("");
                isMetaData = false;
                parseMeta = false;
                seqData = line.substring(6);  // remove ORIGIN
                //System.out.println(line);
            }
            else if (cmd.equals("//"))  // end of one seq
            {
                // clean up extracted data
                if (has16S)
                    list16S.get(id).setData();

                // init to look in another seq
                isMetaData = true;
                parseMeta = false;
                has16S = false;
                seqMeta = line;
                id = "";
                
            }
            else
            {
                if (isMetaData)
                {
                    if (cmd.equals("FEATURES") || cmd.equals("CDS") || cmd.equals("gene"))
                    {
                        seqMeta += line;
                        parseMeta = true;
                        //System.out.println(line);
                    }
                    else if (!parseMeta)
                    {
                        seqMeta += line;
                    }
                    else if (cmd.equals("rRNA"))
                    {
                            seqMeta += line;
                            parseMeta = false;
                            //System.out.println(line);
                    }
                }
                else if (has16S)
                {    parseSeqLine(line,id);
                     //System.out.println(line);
                }
            }
        }

        fileScan.close();
    }

    private String parseMetaData(String seqMeta)
    {
        int indxDef = seqMeta.indexOf("DEFINITION");
        int indxSrc = seqMeta.indexOf("SOURCE");
        int indxOrg = seqMeta.indexOf("ORGANISM");
        int indxRef = seqMeta.indexOf("REFERENCE");
        int indxFtr = seqMeta.indexOf("FEATURES");

        // Find LOCUS ID
        String LocusLine = seqMeta.substring(0,indxDef);
        StringTokenizer tLocus = new StringTokenizer(LocusLine);
        tLocus.nextToken();
        String LocusID = tLocus.nextToken().trim();
        //System.out.println(LocusID);
        int seqLength = Integer.parseInt(tLocus.nextToken().trim());

        // Find Source
        String Source = seqMeta.substring(indxSrc,indxOrg);
        Source = Source.replace("SOURCE", "").trim();

        // Find Organism Tree
        String Organism = seqMeta.substring(indxOrg, indxRef);
        //System.out.println(Organism);
        int indxBact = Organism.indexOf("Bacteria");
        if (indxBact < 0) return ""; // NOT Bacteria
        int indxDot  = Organism.lastIndexOf(".");
        Organism = Organism.substring(indxBact,indxDot).trim();
        Organism = Organism.replace(" ", "");

        //Find rRNA
        String features = seqMeta.substring(indxFtr);
        int indx = features.indexOf("/product=\"16S ribosomal RNA\"");
        boolean has16S = false;
        GenBankSeq gbSeq = new GenBankSeq(LocusID,Source,Organism,seqLength);
        while (indx > 0)
        {
            //if( indx != features.lastIndexOf("/product=\"16S ribosomal RNA\""))
            //    throw new IllegalArgumentException("ERROR: Multiple 16S rRNA in " + LocusID);
            
            boolean isComplement = false;

            // find location of 16S
            String rRNA = features.substring(0,indx);
            features = features.substring(indx+20);
            indx = rRNA.lastIndexOf("rRNA");
            rRNA = rRNA.substring(indx);

            //System.out.println(rRNA);
            indx = rRNA.indexOf("/");
            rRNA = rRNA.substring(4,indx).replace(">","")
                                         .replace("<","")
                                         .replace(".","X").trim();

            isComplement = rRNA.contains("complement");
            //System.out.println("isComplement: " + isComplement);
            if (isComplement)
                rRNA = rRNA.replace("complement", "").replaceAll("[()]","").trim();
            //System.out.print(rRNA);
            String [] pos = rRNA.split("XX");
            if (pos.length != 2)
                throw new IllegalArgumentException("ERROR for " + LocusID + ": Wrong assumption about relation between rRNA and product = 16S...");
            //System.out.println(": " + pos[0] + "  "+ pos[1]);

            // store location of 16S
            int start16S = Integer.parseInt(pos[0]);
            int end16S = Integer.parseInt(pos[1]);
            int len = end16S - start16S + 1;
            //System.out.println("SeqLength = " + len);
            if (len > 1200)
            {
                gbSeq.pos.add(new Pos16S(start16S,end16S,isComplement));
                has16S = true;
            }

            // look for another 16S
            indx = features.indexOf("/product=\"16S ribosomal RNA\"");
           
        }

        // store data
        if (has16S)
        {
            list16S.put(LocusID, gbSeq);
            return LocusID;
            //gbSeq.theSameLength();
        }
        
        // 16S not in this sequence
        return "";         
    }

    private boolean parseSeqLine(String line, String id)
    {
        
        StringTokenizer tLine = new StringTokenizer(line);
        String sPos = tLine.nextToken().trim();
        int pos = Integer.parseInt(sPos);
        
        GenBankSeq gbSeq  = list16S.get(id);
        //int nSeq = gbSeq.pos.size();

        for (Pos16S p: gbSeq.pos)
        {
            int s = p.start;
            int e = p.end; 

            if ((pos+60 > s) && (pos <= e))
            {
                p.data += line;
                //System.out.println(s+","+e+": " + line);
                return true;
            }
        }
        return false;
    }

    public void toFile(String filename, boolean onlyOne) throws IOException
    {
        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);

        for (GenBankSeq s: list16S.values())
            wFile.println(s.toGenBank(onlyOne));

        wFile.close();
    }

    public String rev(String p)
    {
        return toRevCompString(DegString2DegByte(p));
    }
}
