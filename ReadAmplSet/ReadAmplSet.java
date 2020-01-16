
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class ReadAmplSet
{
    int rdLength;
    TreeMap<String, ReadAmpl> readSet = new TreeMap<String, ReadAmpl>();
    TreeSet<String> uniqAmpl = new TreeSet<String> ();

    public ReadAmplSet(String filename, int rdLength) throws FileNotFoundException
    {
        this.rdLength = rdLength;
        processFile(filename);
    }

    public void processFile(String filename) throws FileNotFoundException
    {
        Scanner fileScan = new Scanner(new File(filename));
        //System.out.println("STARTING");
        String seqID = "";
        String amplicon = "";
        boolean hasAmpl = false;
        int minLength = 1600;
        int maxLength = -1;

        int seqCnt = 0;
        int amplCnt = 0;
        while (fileScan.hasNextLine()) // while not eof
        {
            String line = fileScan.nextLine();
            if (line.trim().equals("")) continue;

            if (line.substring(0,1).equals(">"))
            {
                seqCnt++;
                if (hasAmpl) 
                {
                    int l = amplicon.length();
                    if (minLength > l) minLength = l;
                    else if (maxLength < l) maxLength = l;
                    //generateReads(seqID,amplicon);
                    uniqAmpl.add(amplicon);
                }
                //System.out.println(line);
                if (line.indexOf("|") >= 0)
                   seqID = line.substring(1,line.indexOf("|"));
                else
                   seqID = line.substring(1,line.length());
                amplicon = "";
                hasAmpl = false;
            }
            else
            {
                amplCnt++;
                amplicon += line.trim();
                hasAmpl = true;
            }
        }
        //System.out.println("MinLength: " + minLength + "\tMaxLength: " + maxLength);
        //System.out.println(uniqAmpl.size() + " / " + amplCnt + " / " + seqCnt);
        fileScan.close();
    }

    public void generateReads(String seqID, String amplicon)
    {
        int start = 0;
        int end = amplicon.length()-rdLength+1;

        //System.out.println(seqID);

        for (int i = start; i < end; i++)
        {
            String rd = amplicon.substring(i,rdLength+i-1);
            ReadAmpl rA;
            if (readSet.containsKey(rd))
                rA = readSet.get(rd);
            else
                rA = new ReadAmpl();
               
            rA.addRead(seqID);
            readSet.put(rd, rA);
        }
    }

    public void getStats(String filename) throws IOException
    {
        FileWriter oFile = new FileWriter(filename);
        PrintWriter wFile = new PrintWriter(oFile);

        wFile.println("Total number of different reads: " + readSet.size());

        for (Entry<String, ReadAmpl> e: readSet.entrySet())
        {
            wFile.println(e.getKey() + " " + e.getValue().totalSeq() + " " + e.getValue().totalRds());
        }
        wFile.close();
    }


}
