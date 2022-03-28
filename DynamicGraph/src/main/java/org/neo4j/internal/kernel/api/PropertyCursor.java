//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.neo4j.internal.kernel.api;

import java.util.regex.Pattern;
import org.neo4j.values.storable.Value;
import org.neo4j.values.storable.ValueGroup;
import org.neo4j.values.storable.ValueWriter;

public interface PropertyCursor extends Cursor {
    int propertyKey();

    ValueGroup propertyType();

    Value propertyValue();
    Value propertyValue(long version);
    Value propertyValue(long version,boolean isReal);

    <E extends Exception> void writeTo(ValueWriter<E> var1);

    boolean booleanValue();

    String stringValue();

    long longValue();

    double doubleValue();

    boolean valueEqualTo(long var1);

    boolean valueEqualTo(double var1);

    boolean valueEqualTo(String var1);

    boolean valueMatches(Pattern var1);

    boolean valueGreaterThan(long var1);

    boolean valueGreaterThan(double var1);

    boolean valueLessThan(long var1);

    boolean valueLessThan(double var1);

    boolean valueGreaterThanOrEqualTo(long var1);

    boolean valueGreaterThanOrEqualTo(double var1);

    boolean valueLessThanOrEqualTo(long var1);

    boolean valueLessThanOrEqualTo(double var1);
}
