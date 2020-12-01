import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


//при закрытии приложения необходимо вызвать функцию Disconnect()
public class BlockChain {
    private static Cluster cluster = CouchbaseCluster.create("127.0.0.1");
    private static Bucket main_bucket;
    private static Bucket meta_bucket;

    public BlockChain(String user_name, String pass) {
        cluster.authenticate(user_name, pass);
    }

    public GetTransResult getTransaction(String trans_hash) {
        try {
            main_bucket = cluster.openBucket("BIIKCoin");
            N1qlQuery query;
            N1qlQueryResult result;
            meta_bucket = cluster.openBucket("BIIKCoinMetaData");
            boolean addMetaData = false;
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
                result = main_bucket.query(query);
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
            String error_message = exception.getMessage() + "\n";
            try {
                FileWriter file = new FileWriter("error_log.txt", true);
                PrintWriter writer = new PrintWriter(file);
                writer.println(System.currentTimeMillis());
                writer.println(exception.getStackTrace().toString());
                writer.println(error_message);
                writer.close();
            } catch (IOException e) {
                error_message += e.getStackTrace().toString() + "\n" + e.getMessage();
            }
            return new GetTransResult(false, null, error_message);
        } finally {
            meta_bucket.close();
            main_bucket.close();
        }

    }

    public GetBlockResult getBlock(String block_hash) {
        try {
            main_bucket = cluster.openBucket("BIIKCoin");
            JsonDocument found_block = main_bucket.get(block_hash);
            if (found_block != null) {
                Gson gson = new Gson();
                Block block = gson.fromJson(found_block.content().toString(), Block.class);
                return new GetBlockResult(true, block, null);
            } else {
                return new GetBlockResult(false, null, "Такого блока нет");
            }
        } catch (Exception exception) {
            String error_message = exception.getMessage() + "\n";
            try {
                FileWriter file = new FileWriter("error_log.txt", true);
                PrintWriter writer = new PrintWriter(file);
                writer.println(System.currentTimeMillis());
                writer.println(exception.getStackTrace().toString());
                writer.println(error_message);
                writer.close();
            } catch (IOException e) {
                error_message += e.getStackTrace().toString() + "\n" + e.getMessage();
            }
            return new GetBlockResult(false, null, error_message);
        } finally {
            main_bucket.close();
        }
    }


    public AddBlockResult addBlock(Block p_block, String new_block_hash) {
        try {
            main_bucket = cluster.openBucket("BIIKCoin");
            meta_bucket = cluster.openBucket("BIIKCoinMetaData");
            JsonDocument found_block = main_bucket.get(new_block_hash);
            if (found_block != null) {
                return new AddBlockResult(false, "Блок с таким хэш-значением уже существует!!!");
            }
            Block block = new Block(p_block);
            found_block = main_bucket.get(block.prev_hash);
            if (found_block != null) {
                Gson gson = new Gson();
                String json_block = gson.toJson(block, Block.class);
                main_bucket.insert(JsonDocument.create(new_block_hash, JsonObject.fromJson(json_block)));
                for (String trans_hash : block.transactions.keySet()) {
                    JsonObject trans_meta = JsonObject.create();
                    trans_meta.put("block_hash", new_block_hash);
                    meta_bucket.upsert(JsonDocument.create(trans_hash, trans_meta));
                }
                return new AddBlockResult(true, null);
            } else {
                return new AddBlockResult(false, "Хэш предыдущего блока никуда не указывает");
            }
        } catch (Exception exception) {
            String error_message = exception.getMessage() + "\n";
            try {
                FileWriter file = new FileWriter("error_log.txt", true);
                PrintWriter writer = new PrintWriter(file);
                writer.println(System.currentTimeMillis());
                writer.println(exception.getStackTrace().toString());
                writer.println(error_message);
                writer.close();
            } catch (IOException e) {
                error_message += e.getStackTrace().toString() + "\n" + e.getMessage();
            }
            return new AddBlockResult(false, exception.getMessage());
        } finally {
            main_bucket.close();
            meta_bucket.close();
        }
    }

    public boolean Disconnect() {
        return cluster.disconnect();
    }
}

//Класс для взаимодейтсвия с цепочкой блокчейна
//Для работы предварительно необходимо установить CouchBase Server для хранения блокчейна
//Задать там логин и пароль, после чего создать кластер в UI через браузер
//Этот логин и пароль нужно использовать для доступа к блокчейну(можно задать пустой пароль)
//В будущем это всё будет автоматизировано, просто пока так :)