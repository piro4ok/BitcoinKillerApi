public class TransIn {
    public String prev_hash;
    public int index;
    public String script_signtr;
    public String script_pub_key;

    public TransIn(){
    }

    public TransIn(String prev_hash, int index, String script_signtr, String script_pub_key){
        this.prev_hash=prev_hash;
        this.index=index;
        this.script_signtr=script_signtr;
        this.script_pub_key=script_pub_key;
    }
}
