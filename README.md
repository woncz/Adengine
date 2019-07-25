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
