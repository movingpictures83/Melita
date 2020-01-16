
import java.util.HashMap;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author melita
 */
public class ReadAmpl
{
    HashMap<String, Integer> rdCnt = new HashMap<String, Integer>();

   // public ReadAmpl (String id) { rdCnt.put(id,0); }

    public void addRead(String idSeq)
    {
        int cnt = 0;
        if (rdCnt.containsKey(idSeq))
            cnt = rdCnt.get(idSeq);
        rdCnt.put(idSeq, cnt+1);
    }

    public int totalSeq() { return rdCnt.size(); }

    public int totalRds()
    {
        int total = 0;
        for (Integer i: rdCnt.values())
            total += i;

        return total;
    }

    public String histRds()
    {
        return rdCnt.toString();
    }
}
