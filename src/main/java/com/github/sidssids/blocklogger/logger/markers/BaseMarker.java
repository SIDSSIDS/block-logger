package com.github.sidssids.blocklogger.logger.markers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Marker;

public class BaseMarker implements Marker {
    
    private final String name;
    private final String title;
    
    private Map<String, Marker> references;

    public BaseMarker(String name, String title) {
        this.name  = name;
        this.title = title;
    }

    private Map<String, Marker> references() {
        if (references == null) {
            references = new HashMap<>();
        }
        return references;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void add(Marker reference) {
        references().put(reference.getName(), reference);
    }

    @Override
    public boolean remove(Marker reference) {
        if (references == null) {
            return false;
        } else {
            return references().remove(reference.getName(), reference);
        }
    }

    @Override
    public boolean hasChildren() {
        return references != null && !references().isEmpty();
    }

    @Override
    public boolean hasReferences() {
        return references != null && !references().isEmpty();
    }

    @Override
    public Iterator<Marker> iterator() {
        if (references == null) {
            return Collections.emptyIterator();
        } else {
            return references().values().iterator();
        }
    }

    @Override
    public boolean contains(Marker other) {
        if (references == null) {
            return false;
        } else {
            return references().containsValue(other);
        }
    }

    @Override
    public boolean contains(String name) {
        if (references == null) {
            return false;
        } else {
            return references().containsKey(name);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final BaseMarker other = (BaseMarker) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return name;
    }

}
