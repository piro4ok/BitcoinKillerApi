import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

public class Block {
    public String prev_hash;
    public long time;
    public ArrayList<Integer> mining_info;
    public String merkle_root;
    public int trans_count;
    public LinkedTreeMap<String, Transaction> transactions;

    public Block() {

    }

    public Block(Block block) {
        this.prev_hash = block.prev_hash;
        this.trans_count = block.trans_count;
        this.mining_info = new ArrayList<Integer>();
        for (Integer integer : block.mining_info) {
            mining_info.add(integer);
        }
        this.merkle_root = block.merkle_root;
        this.trans_count = block.trans_count;
        this.time = block.time;
        this.transactions = new LinkedTreeMap<String, Transaction>();
        for (String key : block.transactions.keySet()) {
            Transaction block_t = block.transactions.get(key);
            Transaction transaction = new Transaction(block_t.locktime, block_t.count_in,
                    block_t.count_out, block_t.time, block_t.auth_path, block_t.in, block_t.out);
            transactions.put(key, transaction);
        }
    }
}
