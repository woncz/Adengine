package io.github.woncz.ads.engine;

import java.util.List;
import java.util.Set;

import io.github.woncz.ads.engine.be.Document;
import io.github.woncz.ads.engine.be.Query;

/**
 * 索引器
 *
 * @author woncz
 * @date 2019/5/20.
 */
public interface Indexer {

    /**
     * 追加
     *
     * @param document
     */
    void append(Document document);

    /**
     * 重建
     *
     * @param documents
     */
    void rebuild(List<Document> documents);

    /**
     * 检索
     *
     * @param query
     * @return
     */
    Set<Long> retrieve(Query query);

    /**
     * 过滤
     *
     * @param result
     * @return
     */
    Set<Long> filter(Set<Long> result);

    /**
     * 排序
     *
     * @param position
     * @param page
     * @param result
     * @param topK
     * @return
     */
    List<Long> ranking(String position, int page, Set<Long> result, int topK);

}
