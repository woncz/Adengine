package io.github.woncz.ads.engine.be;

import io.github.woncz.ads.engine.be.notation.ConjunctionNotation;
import io.github.woncz.ads.engine.be.notation.DNFNotation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author woncz
 * @date 2019/5/9.
 */
public class BEIndexerTest {

    private static final Logger logger = LoggerFactory.getLogger(BEIndexerTest.class);

    @Test
    public void testRetrieve() {

        Timestamp t = new Timestamp(29551660356L);

        long start = System.currentTimeMillis();
        BEIndexer indexer = new BEIndexer();

        AtomicLong id = new AtomicLong(1);
        Document doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("age ∈ {3} ∧ state ∈ {NY }"));
        indexer.append(doc);
        doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("age ∈ {3} ∧ gender ∈ {F}"));
        indexer.append(doc);
        doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("age ∈ {3} ∧ gender ∈ {M} ∧ state ∉ {CA}"));
        indexer.append(doc);
        doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("state ∈ {CA} ∧ gender ∈ {M}"));
        indexer.append(doc);
        doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("age ∈ {3| 4}"));
        indexer.append(doc);
        doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("state ∉ {CA|NY }"));
        indexer.append(doc);
        doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("subscription ∈ {30|40|50}∧ state ∈ {NY }"));
        indexer.append(doc);
        doc = new Document(id.getAndAdd(1_000_000L), new DNFNotation().denotate("channel ∈ {30|40|50}∧ gender ∈ {F}"));
        indexer.append(doc);

        long end = System.currentTimeMillis();

        System.out.println(indexer);

        System.out.println("indexing costs " + ((end - start)) + "ms");

        //System.out.println(new Gson().toJson(indexer));

        start = System.currentTimeMillis();
        Query query = new Query(new ConjunctionNotation().denotate("age ∈ {3} ∧ state ∈ {CA} ∧ gender ∈ {M}"));
        Set<Long> result = indexer.retrieve(query);
        end = System.currentTimeMillis();
        System.out.println("query costs " + ((end - start)) + "ms");

        //System.out.println(result);

        Set<Long> s = new HashSet<>();
        s.add(4L);
        s.add(5L);
        Assert.assertEquals(s, result);

        start = System.currentTimeMillis();
        query = new Query(new ConjunctionNotation().denotate("gender ∈ {M} ∧ channel ∈ {30|40|50}"));
        result = indexer.retrieve(query);
        end = System.currentTimeMillis();
        System.out.println("query costs " + ((end - start)) + "ms");

        //System.out.println(result);
        Assert.assertTrue(result.contains(6L));

        start = System.currentTimeMillis();
        query = new Query(new ConjunctionNotation().denotate("gender ∈ {F} ∧ channel ∈ {30|40|50}"));
        result = indexer.retrieve(query);
        end = System.currentTimeMillis();
        System.out.println("query costs " + ((end - start)) + "ms");

        //System.out.println(result);
        Assert.assertTrue(result.contains(8L));

        for (int i = 0; i < 1_000_000; i++) {
            doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("subscription ∈ {30|40|50}"));
            indexer.append(doc);
        }

        Assert.assertEquals(2, result.size());

        List<Document> documents = new ArrayList<>();
        for (int k = 0; k < 1_0; k++) {
            documents.clear();
            doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("age ∈ {3} ∧ state ∈ {NY }"));
            documents.add(doc);
            doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("age ∈ {3} ∧ gender ∈ {F}"));
            documents.add(doc);
            doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("age ∈ {3} ∧ gender ∈ {M} ∧ state ∉ {CA}"));
            documents.add(doc);
            doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("state ∈ {CA} ∧ gender ∈ {M}"));
            documents.add(doc);
            doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("age ∈ {3| 4}"));
            documents.add(doc);
            doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("state ∉ {CA|NY }"));
            documents.add(doc);
            doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("subscription ∈ {30|40|50}"));
            documents.add(doc);

            for (int j = 0; j < 1_000_000; j++) {
                doc = new Document(id.getAndAdd(1L), new DNFNotation().denotate("subscription ∈ {30|40|50}"));
                documents.add(doc);
            }

            indexer.rebuild(documents);

            query = new Query(new ConjunctionNotation().denotate("age ∈ {3} ∧ state ∈ {NY}"));
            result = indexer.retrieve(query);
            result.forEach(r -> {
                System.out.print(r);
                r = (r % 1_000_000) % 7;
                System.out.println("-" + r);
                Assert.assertTrue((r == 5 || r == 1));
            });

            System.out.println("i=" + k);
        }

        IntStream.range(0, 10).forEach(i-> {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
