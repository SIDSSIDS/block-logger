package com.kolychev.utils.blocklogger;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class Main {
    
    public static class TestMarker implements Marker {
        
        private final String name;
        private Map<String, Marker> references;
        
        private String indent;

        public TestMarker(String name) {
            this.name = name;
        }

        public String getIndent() {
            return indent;
        }

        public void setIndent(String indent) {
            this.indent = indent;
        }
        
        private Map<String, Marker> references() {
            if (references == null) {
                references = new HashMap<>();
            }
            return references;
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
            final TestMarker other = (TestMarker) obj;
            return Objects.equal(this.name, other.name);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("name", name)
                    .toString();
        }
        
    }
    
    public static void main(String[] args) {
        
        //TODO
        // use Layout + Marker technology to add identity
        // Converter
        
        Logger logger = LoggerFactory.getLogger(Main.class);
        
        TestMarker m = new TestMarker("test-marker");
        m.setIndent("[+]");
        logger.error(m, "Hello, world!");
    }

}
