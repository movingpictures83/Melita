/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class Locus
{
    String ID;
    String Source;
    String Strain;
    String Organism;

    public Locus(String ID, String Definition, String Organism)
    {
        this.ID = ID;
        this.Organism = Organism;
        Strain = "";
        createSS(Definition);
    }

    private void createSS(String Definition)
    {
        Definition = Definition.replace(".", "");
        String [] s = Definition.split(";");
        Source = s[0].trim();
        if (s.length > 1)
            Strain = s[1].trim();
    }

    public String toString()
    {
        String s = "ID: " + ID + "\n  Source: " + Source + "\n  Strain: " + Strain;
        s += "\n  Organism: " + Organism;
        return  s;
    }

}
