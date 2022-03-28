package cn.DynamicGraph.graphdb;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;

//to do
public interface VersionGraphDatabaseService {

    //DynamicGraph
    //*******************************

    long [] listAllVersions();
    long getCurrentVersion();
    void seekVersion(long version);
    long getNextVersion();
    long []getNextVersions(long versionCount);
    boolean commitVersions(long[] versions);
    boolean commitVersion(long version, boolean isInuse);
    Transaction beginTx(long version);



    //DynamicGraph
    //*******************************


    ResourceIterable<Node> getAllNodesInSingleVersion(long version);

    ResourceIterable<Relationship> getAllRelationshipsInSingleVersion(long version);



    ResourceIterable<Node> getAllNodesInVersionDelta(long startVersion,long endVersion);

    ResourceIterable<Relationship> getAllRelationshipsInVersionDelta(long startVersion,long endVersion);


    ResourceIterable<Node> getAllNodesInVersions(long startVersion,long endVersion);

    ResourceIterable<Relationship> getAllRelationshipsInVersions(long startVersion,long endVersion);


}
