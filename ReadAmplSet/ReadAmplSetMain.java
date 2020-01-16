
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
public class ReadAmplSetMain
{
    public static void main(String [] args) throws FileNotFoundException, IOException
    {
        FP_IO.printHeader();
        int rdLength = 200;
        String infile = args[0];//"amplSeq/amplOutRDP67IMPROVED.txt";
        ReadAmplSet rS = new ReadAmplSet(infile, rdLength);
        String outfile = args[1];//infile.replace("amplOut", "readSet");
        rS.getStats(outfile);

        //infile = "amplSeq/amplOutRDP78IMPROVED.txt";
        //System.out.println(infile);
        //ReadAmplSet rS1 = new ReadAmplSet(infile, rdLength);
        //outfile = infile.replace("/amplOut", "/readSet");
        //rS.getStats(outfile);

        //infile = "amplSeq/amplOutRDP45IMPROVED.txt";
        //System.out.println(infile);
        //ReadAmplSet rS2 = new ReadAmplSet(infile, rdLength);
        //outfile = infile.replace("/amplOut", "/readSet");
        //rS.getStats(outfile);

    }
}
