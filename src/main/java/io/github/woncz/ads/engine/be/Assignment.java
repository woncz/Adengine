package io.github.woncz.ads.engine.be;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.woncz.ads.engine.be.config.AttributeKey;
import io.github.woncz.ads.engine.be.notation.AssignmentNotation;

/**
 * @author woncz
 * @date 2019/5/5.
 */
public class Assignment {

    private static final Logger logger = LoggerFactory.getLogger(Assignment.class);

    private String attribute;

    private boolean belong;

    private List<Object> multiValues;

    public Assignment(String attribute, boolean belong, Object... multiValues) {
        this(attribute, belong, Arrays.asList(multiValues));
    }

    public Assignment(String attribute, boolean belong, List<Object> multiValues) {
        if (!AttributeKey.contains(attribute)) {
            logger.warn("unregister attribute key cannot be indexing " + "(" + attribute + ")");
            //throw new IllegalArgumentException("unregister attribute key cannot be indexing " + "(" + attribute + ")");
        }
        this.attribute = attribute;
        this.belong = belong;
        this.multiValues = multiValues;
    }

    public boolean isInclusive() {
        return belong;
    }

    public Key key() {
        /**
        List<Key> keys = new ArrayList<>(multiValues.size());
        multiValues.forEach(v -> keys.add(new Key(attribute, v)));
        return keys;
         */
        return new Key(attribute, multiValues);
    }

    public String getAttribute() {
        return attribute;
    }

    public boolean getBelong() {
        return belong;
    }

    public List<Object> getMultiValues() {
        return multiValues;
    }

    @Override
    public String toString() {
        return new AssignmentNotation().notate(this);
    }
}
