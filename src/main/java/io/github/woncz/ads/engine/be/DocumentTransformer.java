package io.github.woncz.ads.engine.be;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.woncz.ads.engine.be.campaign.Campaign;
import io.github.woncz.ads.engine.be.campaign.Targeting;
import io.github.woncz.ads.engine.be.config.Mapper;

/**
 * 转换器
 *
 * @author woncz
 * @date 2019/5/9.
 */
public class DocumentTransformer {

    /**
     * transfer from Campaign to Document
     *
     * @param campaign
     * @return
     */
    private static Document transfer(Campaign campaign) {
        long docId = campaign.getId();
        return new Document(docId, transfer(campaign.getTargeting()));
    }

    /**
     * transfer from Targeting to DNF
     *
     * @param targeting
     * @return
     */
    public static DNF transfer(Targeting targeting) {
        Map<String, List<Object>> targets = targeting.getConditions();
        if (targets == null) {
            return null;
        }
        Conjunction conjunction = new Conjunction();
        targets.forEach((k, v) -> {
            if (Mapper.get(k) != null) {
                k = Mapper.get(k);
            }
            Assignment assignment = new Assignment(k, true, v);
            conjunction.and(assignment);
        });
        return new DNF(conjunction);
    }

    public static void transfer(Document document, ConjunctionPartition partition) {
        long docId = document.getDocId();
        DNF dnf = document.getTargeting();
        dnf.getConjunctions().forEach(c -> {
            c.getAssignments().forEach(s -> {
                Key key = s.key();
                PostingList postingList = partition.get(key);
                if (postingList == null) {
                    List<Entry> entries = new ArrayList<>();
                    entries.add(new Entry(docId, s.getBelong()));
                    postingList = new PostingList(entries);
                    partition.append(key, postingList);
                } else {
                    postingList.getEntries().add(new Entry(docId, s.getBelong()));
                }
            });
            if (c.size() == 0) {
                PostingList postingList = partition.get(Key.ZKey);
                postingList.getEntries().add(new Entry(docId, true));
            }
        });
    }

    /**
     * 索引构建转化器
     */
    public static List<Document> transfer(Map<Long, Map<String, List<Object>>> conditions) {
        Set<Map.Entry<Long, Map<String, List<Object>>>> entries = conditions.entrySet();
        List<Document> documents = entries.stream().map(e -> {
            Long id = e.getKey();
            Map<String, List<Object>> value = e.getValue();
            Campaign campaign = new Campaign();
            campaign.setId(id);
            campaign.setTargeting(new Targeting().setConditions(value));
            return DocumentTransformer.transfer(campaign);
        }).filter(d -> d.validate()).collect(Collectors.toList());
        return documents;
    }

}
