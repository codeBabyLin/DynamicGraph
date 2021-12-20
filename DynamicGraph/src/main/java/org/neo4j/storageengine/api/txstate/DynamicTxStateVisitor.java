package org.neo4j.storageengine.api.txstate;



public interface DynamicTxStateVisitor extends TxStateVisitor {
    void visitNodeVersionChanges(long nodeId, long version);
}
