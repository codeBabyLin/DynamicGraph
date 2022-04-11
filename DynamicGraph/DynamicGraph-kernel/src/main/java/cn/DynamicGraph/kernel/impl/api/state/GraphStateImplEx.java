package cn.DynamicGraph.kernel.impl.api.state;


import org.neo4j.kernel.impl.util.collection.CollectionsFactory;
import org.neo4j.storageengine.api.txstate.GraphState;

public class GraphStateImplEx extends PropertyContainerStateImplEx implements GraphState {
    GraphStateImplEx(CollectionsFactory collectionsFactory) {
        super(-1L, collectionsFactory);
    }
}
