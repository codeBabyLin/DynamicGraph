//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.neo4j.storageengine.api;

public interface StorageNodeCursor extends StorageEntityScanCursor {
    long[] labels();

    boolean hasLabel(int var1);

    long relationshipGroupReference();

    long allRelationshipsReference();

    void setCurrent(long var1);

    long nodeVersion();

    boolean isDense();
}
