//import com.google.gson.*;


import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        // write your code here
//        String get_by_key_res
//                =bucket.get("block_hash1").content().toString();
//        Gson g=new Gson();
//        Map myMap = g.fromJson(get_by_key_res, Map.class);
        BlockChain blockChain
                = new BlockChain("Administrator", "KBRS007");
        GetBlockResult get_block = blockChain.getBlock("block2_hash");
        Block block = new Block() {
            {
                prev_hash = "block1_hash";
                time = 0;
                mining_info = new ArrayList<Integer>() {
                    {
                        add(1);
                    }
                };
                merkle_root = "adas";
                trans_count = 2;
                transactions = new LinkedTreeMap<String, Transaction>();
                transactions.put("trans7_hash", new Transaction() {
                    {
                        locktime = 0;
                        count_in = 1;
                        count_out = 1;
                        time = 1;
                        auth_path = new ArrayList<String>() {
                            {
                                add("some");
                            }
                        };
                        in = new ArrayList<TransIn>() {
                            {
                                add(new TransIn() {
                                    {
                                        prev_hash = "trans1_hash";
                                        index = 0;
                                        script_signtr = "asdn";
                                        script_pub_key = "ovqanr";
                                    }
                                });
                            }
                        };
                        out = new ArrayList<TransOut>() {
                            {
                                add(new TransOut() {
                                    {
                                        value = 1.300;
                                        hashPubKey = "poaniv";
                                    }
                                });
                            }
                        };
                    }
                });

            }
        };
        AddBlockResult result = blockChain.addBlock(block, "block2_hash");
        System.out.println(result.getError_message());
    }
}
