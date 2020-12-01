import java.util.ArrayList;

public class Transaction {
    public long locktime;
    public int count_in;
    public int count_out;
    public long time;
    public ArrayList<String> auth_path;
    public ArrayList<TransIn> in;
    public ArrayList<TransOut> out;

    public Transaction() {
    }

    public Transaction(long locktime, int count_in, int count_out,
                       long time, ArrayList<String> auth_path, ArrayList<TransIn> in, ArrayList<TransOut> out) {
        this.time = time;
        this.locktime = locktime;
        this.count_out = count_out;
        this.count_in = count_in;
        this.auth_path = new ArrayList<String>();
        for (String path_elem : auth_path) {
            this.auth_path.add(path_elem);
        }
        this.in = new ArrayList<TransIn>();
        for (TransIn t_in : in) {
            TransIn temp_tin = new TransIn(t_in.prev_hash, t_in.index, t_in.script_signtr, t_in.script_pub_key);
            this.in.add(temp_tin);
        }
        this.out=new ArrayList<TransOut>();
        for(TransOut t_out:out){
            TransOut temp_tout=new TransOut(t_out.value, t_out.hashPubKey);
            this.out.add(temp_tout);
        }
    }
}
