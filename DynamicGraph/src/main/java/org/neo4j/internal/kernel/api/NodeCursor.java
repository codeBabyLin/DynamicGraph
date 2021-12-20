//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.neo4j.internal.kernel.api;

public interface NodeCursor extends Cursor {
    long nodeReference();

    LabelSet labels();

    boolean hasLabel(int var1);

    void relationships(RelationshipGroupCursor var1);

    void allRelationships(RelationshipTraversalCursor var1);

    long nodeVersion();

    void properties(PropertyCursor var1);

    long relationshipGroupReference();

    long allRelationshipsReference();

    long propertiesReference();

    boolean isDense();
}
