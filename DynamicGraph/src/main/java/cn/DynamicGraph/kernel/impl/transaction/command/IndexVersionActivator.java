package cn.DynamicGraph.kernel.impl.transaction.command;

import org.neo4j.internal.kernel.api.exceptions.schema.IndexNotFoundKernelException;
import org.neo4j.kernel.api.exceptions.index.IndexActivationFailedKernelException;
import org.neo4j.kernel.api.exceptions.index.IndexPopulationFailedKernelException;
import org.neo4j.kernel.impl.api.index.IndexingService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class IndexVersionActivator implements AutoCloseable{

    private final IndexingService indexingService;
    private Set<Long> indexesToActivate;

    public IndexVersionActivator(IndexingService indexingService) {
        this.indexingService = indexingService;
    }

    public void close() {
        if (this.indexesToActivate != null) {
            Iterator var1 = this.indexesToActivate.iterator();

            while(var1.hasNext()) {
                long indexId = (Long)var1.next();

                try {
                    this.indexingService.activateIndex(indexId);
                } catch (IndexActivationFailedKernelException | IndexPopulationFailedKernelException | IndexNotFoundKernelException var5) {
                    throw new IllegalStateException("Unable to enable constraint, backing index is not online.", var5);
                }
            }
        }

    }

    public void activateIndex(long indexId) {
        if (this.indexesToActivate == null) {
            this.indexesToActivate = new HashSet();
        }

        this.indexesToActivate.add(indexId);
    }

    public void indexDropped(long indexId) {
        if (this.indexesToActivate != null) {
            this.indexesToActivate.remove(indexId);
        }

    }
}
