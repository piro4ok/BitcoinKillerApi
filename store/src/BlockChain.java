import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

//Класс для взаимодейтсвия с цепочкой блокчейна
//Для работы предварительно необходимо установить CouchBase Server для хранения блокчейна
//Задать там логин и пароль, после чего создать кластер в UI через браузер
//Этот логин и пароль нужно использовать для доступа к блокчейну(можно задать пустой пароль)
//В будущем это всё будет автоматизировано, просто пока так :)
public class BlockChain {
    private Cluster cluster = CouchbaseCluster.create("127.0.0.1");
    private Bucket bucket;

    public BlockChain(String user_name, String pass) {
        cluster.authenticate(user_name, pass);
        bucket = cluster.openBucket("BIIKCoin");
    }

    public GetTransResult getTransaction(String trans_hash) {
        N1qlQuery query;
        N1qlQueryResult result;
        Bucket meta_bucket = cluster.openBucket("BIIKCoinMetaData");
        boolean addMetaData = false;
        try {
            JsonDocument trans_meta_info = meta_bucket.get(trans_hash);
            if (trans_meta_info != null) {
                query = N1qlQuery.simple("SELECT VALUE(transactions." + trans_hash +
                        ") FROM `BIIKCoin` USE KEYS \"" + trans_meta_info.content().get("block_hash") +
                        "\" WHERE transactions." + trans_hash + " IS VALUED");
                result = meta_bucket.query(query);
            } else {
                addMetaData = true;
                query = N1qlQuery.simple("SELECT META().id, transactions." + trans_hash
                        + " FROM `BIIKCoin` WHERE transactions." + trans_hash + " IS VALUED;");
                result = bucket.query(query);
            }
            if (result.allRows().size() != 1) {
                return new GetTransResult(false, null,
                        "Транзаккция с таким значением хэша не найдена");
            } else {
                Gson gson_parser = new Gson();
                Transaction transaction;
                com.google.gson.JsonObject found_trans = JsonParser.parseString(result.allRows().get(0).toString()).getAsJsonObject();
                if (addMetaData) {
                    String block_hash = found_trans.getAsJsonPrimitive("id").getAsString();
                    meta_bucket.upsert(JsonDocument.create(trans_hash, JsonObject.empty().put("block_hash", block_hash)));
                    transaction = gson_parser.fromJson(found_trans.get(trans_hash).toString(), Transaction.class);
                } else {
                    transaction = gson_parser.fromJson(found_trans.toString(), Transaction.class);
                }
                return new GetTransResult(true, transaction, null);
            }
        } catch (Exception exception) {
            return new GetTransResult(false, null, exception.getMessage());
        } finally {
            meta_bucket.close();
        }

    }

    public GetBlockResult getBlock(String block_hash) {
        JsonDocument found_block = bucket.get(block_hash);
        if (found_block != null) {
            Gson gson = new Gson();
            Block block = gson.fromJson(found_block.content().toString(), Block.class);
            return new GetBlockResult(true, block, null);
        } else {
            return new GetBlockResult(false, null, "Такого блока нет");
        }
    }

    public boolean AddBlock(Block block){
        return true;
    }

    public boolean Disconnect() {
        bucket.close();
        return cluster.disconnect();
    }
}
