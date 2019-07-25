# Adengine

## part 1
a java implementation of ad retrival algorithm proposed in [Indexing Boolean Expressions](http://theory.stanford.edu/~sergei/papers/vldb09-indexing.pdf)

## part 2
TODO : 
need to add a implementation of ad retrival algorithm based on searching keywords


## QuickStart

- **Requirement**: ``Java8``

- **Sample Code**

``` java
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
        
        Query query = new Query(new ConjunctionNotation().denotate("age ∈ {3} ∧ state ∈ {CA} ∧ gender ∈ {M}"));
        Set<Long> result = indexer.retrieve(query);
```

## Reference

- [1] [Indexing Boolean Expressions](http://theory.stanford.edu/~sergei/papers/vldb09-indexing.pdf)

- [2] [计算广告:互联网商业变现的市场与技术](https://book.douban.com/subject/26596778/)

- [3] [adindex4j](https://github.com/downgoon/adindex4j)

- [4] [布尔表达式检索学习笔记](https://mengxl.cn/2019/02/16/%E5%B8%83%E5%B0%94%E8%A1%A8%E8%BE%BE%E5%BC%8F%E6%A3%80%E7%B4%A2%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0/)
