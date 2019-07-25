package io.github.woncz.ads.engine.be;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author woncz
 * @date 2019/5/5.
 */
public class Key {

    /**
     * The special key for size 0
     */
    public static final Key ZKey = new Key("Z", "Z");

    private String attribute;

    private List<String> value;

    public Key(String attribute, Object value) {
        this(attribute, Collections.singletonList(value));
    }

    public Key(String attribute, List<Object> multiValue) {
        if (attribute == null || attribute.equals("")) {
            throw new IllegalArgumentException("illegal attribute of key");
        }
        if (multiValue == null) {
            multiValue = Collections.singletonList("");
        }
        this.attribute = attribute;
        this.value = new ArrayList<>(multiValue.size());
        multiValue.forEach(v -> {
            if (v != null) {
                this.value.add(v.toString());
            }
        });
        Collections.sort(this.value);
    }

    public String getAttribute() {
        return attribute;
    }

    public Object getValue() {
        return value;
    }

    public boolean equals(Key o, boolean soft) {
        if (!soft) {
            return this.equals(o);
        }
        if (this == o) return true;
        if (o == null) return false;
        if (Objects.equals(attribute, o.attribute) && !Collections.disjoint(value, o.value)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key = (Key) o;
        return Objects.equals(attribute, key.attribute) && Objects.equals(value.size(), key.value.size())
                && value.containsAll(key.value);
    }

    @Override
    public int hashCode() {
        // hash collision up
        return Objects.hash(attribute);
    }

    @Override
    public String toString() {
        return "(" + attribute + ", " + value + ")";
    }
}
