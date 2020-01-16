import java.util.Scanner;
import java.io.File;
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
public class AmplStatMain
{
    public static void main(String [] args) throws IOException
    {
        AmplFileStat SA = new AmplFileStat();
        /*File myFile = new File(args[0]);
        Scanner scan = new Scanner(myFile);
        HashMap<String, String> parameters = new HashMap<String, String>(); 
       
        while (scan.hasNextLine()) {
           String line = scan.nextLine();
           String[] contents = line.split("\t");
           parameters.put(contents[0], contents[1]);
        }*/

        FP_IO.printHeader();
        HashMap<String, String> parameters = FP_IO.readKeywordValuePairs(args[0]);

        SA.findIfMissing(parameters.get("target"), parameters.get("missing"), parameters.get("inputfolder"));
        SA.findIfExist(parameters.get("target"), parameters.get("existing"), parameters.get("inputfolder"));
        SA.missGenusToFile(parameters.get("outputmissing"));
        SA.distinguishGenusSpecie(
                parameters.get("inputcounts"),
                parameters.get("target"), parameters.get("inputfolder"),
                parameters.get("outputcounts"));
    }
}


//Number of seqeunces: 9175
//Flavimonas is not in DB
