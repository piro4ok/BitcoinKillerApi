import java.util.List;

public class Transaction {
    public int locktime;
    public int count_in;
    public int count_out;
    public int time;
    public List<String> auth_path;
    public List<TransIn> in;
    public List<TransOut> out;
}
