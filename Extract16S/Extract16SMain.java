
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
public class Extract16SMain extends ByteFunc
{
    public static void main (String [] args) throws FileNotFoundException, IOException
    {
        FP_IO.printHeader();
        String p = "GACTCCTACGGGRSGCWGCAG";
        Extract16S ET = new Extract16S();
        //System.out.println(p);
        //System.out.println(ET.rev(p));
        
        //Extract16S E = new Extract16S("all_gbk_20110315\\filesD.txt");
        //E.toFile("output\\D_hmpd16S.gbk");

        Extract16S E = new Extract16S(args[0]);
        E.toFile(args[1], true);
    }
}
