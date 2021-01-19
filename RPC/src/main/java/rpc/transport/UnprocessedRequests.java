package rpc.transport;

import rpc.pojo.RPCResponse;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * @author Lzs
 * @date 2021/1/14 20:43
 * @description
 */
public class UnprocessedRequests {
    private HashMap<String, CompletableFuture> requestMap;

    public UnprocessedRequests() {
        this.requestMap = new HashMap<>();
    }

    public void put(String key, CompletableFuture completableFuture) {
        requestMap.put(key, completableFuture);
    }

    public CompletableFuture get(String key) {
        return requestMap.get(key);
    }

    public void complete(RPCResponse response) {
        String key = response.getId();
        requestMap.get(key).complete(response);
        remove(key);
    }

    public void remove(String key) {
        requestMap.remove(key);
    }
}