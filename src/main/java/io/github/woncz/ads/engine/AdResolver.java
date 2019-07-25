package io.github.woncz.ads.engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.woncz.ads.engine.be.BEIndexer;
import io.github.woncz.ads.engine.be.DNF;
import io.github.woncz.ads.engine.be.DocumentTransformer;
import io.github.woncz.ads.engine.be.Query;
import io.github.woncz.ads.engine.be.campaign.Targeting;
import io.github.woncz.ads.engine.be.config.AttributeKey;
import io.github.woncz.ads.engine.be.config.Mapper;

/**
 * 广告检索解析服务(在线)
 *
 * @author woncz
 * @date 2019/4/23.
 */
public class AdResolver {

    private static final Logger logger = LoggerFactory.getLogger(AdResolver.class);

    private static BEIndexer indexer = new BEIndexer();

    /**
     * 初始化广告搜索引擎
     */
    public static synchronized void init() {
        doService();
    }

    /**
     * 数据同步
     */
    private static void doService() {
        indexing();

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("AdResolverIndexSyncThreadPool-%d").build();
        ExecutorService executorService = Executors.newSingleThreadExecutor(threadFactory);
        executorService.execute(() -> {
            int counter = 0;
            while (true) {
                logger.debug("counter = " + counter++);
                try {
                    Thread.sleep(60_000);
                    indexing();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                    // clean up state...
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private static void indexing() {
        // 从广告库取定向条件数据
        indexer.rebuild(DocumentTransformer.transfer(new HashMap<>()));
    }

    public static List<Long> retrieve(Map<String, List<Object>> profile) {
        rewrite(profile);
        String position = getPosition(profile);
        int page = 0;
        // 1.检索
        Set<Long> result = indexer.retrieve(toQuery(profile));
        logger.info("result ads=" + result);
        // 2.过滤
        Set<Long> filtered = indexer.filter(result);
        logger.info("filtered ads=" + filtered);
        // 3.排序
        List<Long> real = indexer.ranking(position, page, filtered, 1);
        return real;
    }

    private static Query toQuery(Map<String, List<Object>> profile) {
        DNF dnf = DocumentTransformer.transfer(new Targeting().setConditions(profile));
        if (dnf == null || dnf.getConjunctions().size() != 1) {
            String msg = "invalid profile " + profile;
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return new Query(dnf.getConjunctions().get(0));
    }

    /**
     * rewrite the necessary attributes
     *
     * @param profile
     */
    private static void rewrite(Map<String, List<Object>> profile) {
        if (!profile.containsKey(AttributeKey.LOCATION.getName())) {
            profile.put(AttributeKey.LOCATION.getName(), Collections.emptyList());
        }
        if (!profile.containsKey(AttributeKey.GENDER.getName())) {
            profile.put(AttributeKey.GENDER.getName(), Collections.emptyList());
        }
        if (!profile.containsKey(AttributeKey.AD_POSITION.getName())) {
            String msg = "lack of ad_position";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    private static String getPosition(Map<String, List<Object>> profile) {
        String position = profile.getOrDefault(AttributeKey.AD_POSITION.getName(), Collections.singletonList("")).get(0).toString();
        return position;
    }

    /**
     * 键值标准化
     *
     * @param before
     * @return
     */
    public static Map normalize(Map<String, List<Object>> before) {
        Map<String, List<Object>> after = new HashMap<>(before.size());
        before.entrySet().forEach(e -> {
            String k = Mapper.get(e.getKey());
            if (k != null) {
                after.put(k, e.getValue());
            } else {
                after.put(e.getKey(), e.getValue());
            }
        });
        return after;
    }

    public static String monitor() {
        return indexer.toString();
    }

    public static void main(String[] args) {
        Map<Long, Map<String, List<Object>>> m = new HashMap<>();
        m.put(1L, null);
        DocumentTransformer.transfer(m);
        //init();

        Map<String, List<Object>> map = new HashMap<>();
        map.put("page", Arrays.asList("1"));
        map.put("ad_position", Arrays.asList("FEEDS"));
        getPosition(map);
    }
}
