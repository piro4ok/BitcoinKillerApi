import java.util.List;
import java.util.Map;

public class Block
{
    //
    public String prev_hash;
    public int time;
    public List<Integer> mining_info;
    public String merkle_root;
    public int trans_count;
    public Map<String,Transaction> transactions;
}
