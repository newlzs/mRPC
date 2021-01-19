package rpc.loadBalancer.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.loadBalancer.LoadBalancer;
import rpc.pojo.RPCRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Lzs
 * @date 2021/1/19 12:22
 * @description 一致性哈希负载均衡
 * https://cloud.tencent.com/developer/article/1404016
 */
public class KetamaConsistentHashLoadBalancer implements LoadBalancer {
    private static final Logger logger = LoggerFactory.getLogger(KetamaConsistentHashLoadBalancer.class);
    private static MessageDigest md5Digest;
    static {
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 不被支持");
        }
    }
    // 虚拟节点的个数
    private final static int VIRTUAL_NODE_SIZE = 12;
    private final static String VIRTUAL_NODE_SUFFIX = "_";

    @Override
    public Instance select(List<Instance> instances, RPCRequest request) {
        long requestHashCode = getHashCode(request.getHashKey());
        TreeMap<Long, Instance> ring = buildConsistentHashRing(instances);
        Instance instance = locate(ring, requestHashCode);
        return instance;
    }

    private Instance locate(TreeMap<Long, Instance> ring, Long requestHashCode) {
        // 向右找到第一个 key, ceilingEntry返回指定的Key大于或等于的最小值的元素，如果没有，则返回null
        Map.Entry<Long, Instance> locateEntry = ring.ceilingEntry(requestHashCode);
        if(locateEntry == null) {
            locateEntry = ring.firstEntry();
        }
        return locateEntry.getValue();
    }

    private TreeMap<Long, Instance> buildConsistentHashRing(List<Instance> instances) {
        TreeMap<Long, Instance> virtualNodeRing = new TreeMap<>();
        for (Instance instance : instances) {
            for (int i = 0; i < VIRTUAL_NODE_SIZE / 4; i++) {
                byte[] digest = computeMd5(instance.getIp() + ":" + instance.getPort() + VIRTUAL_NODE_SUFFIX + i);
                for (int h = 0; h < 4; h++) {
                    Long k = ((long) (digest[3 + h * 4] & 0xFF) << 24)
                            | ((long) (digest[2 + h * 4] & 0xFF) << 16)
                            | ((long) (digest[1 + h * 4] & 0xFF) << 8)
                            | (digest[h * 4] & 0xFF);
                    virtualNodeRing.put(k, instance);
//                    System.out.printf("十六进制输出"+"%08x\n", k);
                }
            }
        }
        return virtualNodeRing;
    }

    private long getHashCode(String origin) {
        byte[] bkey = computeMd5(origin);
        long rv = ((long)(bkey[3] & 0xff) << 24)
                | ((long)(bkey[2] & 0xff) << 16)
                | ((long)(bkey[1] & 0xff) << 8)
                | ((long)(bkey[0] & 0xff));
        return rv;
    }

    private static byte[] computeMd5(String k) {
        MessageDigest md5;
        try {
            md5 = (MessageDigest) md5Digest.clone();
        } catch (CloneNotSupportedException e) {
            logger.error("不支持md5的克隆");
            throw new RuntimeException("clone of MD5 not supported", e);
        }
        md5.update(k.getBytes());
        return md5.digest();
    }
}