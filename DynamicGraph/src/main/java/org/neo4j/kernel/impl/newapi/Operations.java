//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.neo4j.kernel.impl.newapi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.internal.kernel.api.CursorFactory;
import org.neo4j.internal.kernel.api.ExplicitIndexRead;
import org.neo4j.internal.kernel.api.ExplicitIndexWrite;
import org.neo4j.internal.kernel.api.IndexQuery;
import org.neo4j.internal.kernel.api.IndexReference;
import org.neo4j.internal.kernel.api.Locks;
import org.neo4j.internal.kernel.api.NodeLabelIndexCursor;
import org.neo4j.internal.kernel.api.Procedures;
import org.neo4j.internal.kernel.api.Read;
import org.neo4j.internal.kernel.api.SchemaRead;
import org.neo4j.internal.kernel.api.SchemaWrite;
import org.neo4j.internal.kernel.api.Token;
import org.neo4j.internal.kernel.api.Write;
import org.neo4j.internal.kernel.api.IndexQuery.ExactPredicate;
import org.neo4j.internal.kernel.api.exceptions.EntityNotFoundException;
import org.neo4j.internal.kernel.api.exceptions.KernelException;
import org.neo4j.internal.kernel.api.exceptions.TransactionFailureException;
import org.neo4j.internal.kernel.api.exceptions.explicitindex.AutoIndexingKernelException;
import org.neo4j.internal.kernel.api.exceptions.explicitindex.ExplicitIndexNotFoundKernelException;
import org.neo4j.internal.kernel.api.exceptions.schema.ConstraintValidationException;
import org.neo4j.internal.kernel.api.exceptions.schema.CreateConstraintFailureException;
import org.neo4j.internal.kernel.api.exceptions.schema.IndexNotApplicableKernelException;
import org.neo4j.internal.kernel.api.exceptions.schema.IndexNotFoundKernelException;
import org.neo4j.internal.kernel.api.exceptions.schema.SchemaKernelException;
import org.neo4j.internal.kernel.api.exceptions.schema.ConstraintValidationException.Phase;
import org.neo4j.internal.kernel.api.exceptions.schema.SchemaKernelException.OperationContext;
import org.neo4j.internal.kernel.api.schema.IndexProviderDescriptor;
import org.neo4j.internal.kernel.api.schema.LabelSchemaDescriptor;
import org.neo4j.internal.kernel.api.schema.RelationTypeSchemaDescriptor;
import org.neo4j.internal.kernel.api.schema.SchemaDescriptor;
import org.neo4j.internal.kernel.api.schema.constraints.ConstraintDescriptor;
import org.neo4j.kernel.api.SilentTokenNameLookup;
import org.neo4j.kernel.api.exceptions.index.IndexEntryConflictException;
import org.neo4j.kernel.api.exceptions.schema.AlreadyConstrainedException;
import org.neo4j.kernel.api.exceptions.schema.AlreadyIndexedException;
import org.neo4j.kernel.api.exceptions.schema.DropConstraintFailureException;
import org.neo4j.kernel.api.exceptions.schema.DropIndexFailureException;
import org.neo4j.kernel.api.exceptions.schema.IndexBelongsToConstraintException;
import org.neo4j.kernel.api.exceptions.schema.IndexBrokenKernelException;
import org.neo4j.kernel.api.exceptions.schema.NoSuchConstraintException;
import org.neo4j.kernel.api.exceptions.schema.NoSuchIndexException;
import org.neo4j.kernel.api.exceptions.schema.RepeatedPropertyInCompositeSchemaException;
import org.neo4j.kernel.api.exceptions.schema.UnableToValidateConstraintException;
import org.neo4j.kernel.api.exceptions.schema.UniquePropertyValueValidationException;
import org.neo4j.kernel.api.explicitindex.AutoIndexing;
import org.neo4j.kernel.api.schema.constraints.ConstraintDescriptorFactory;
import org.neo4j.kernel.api.schema.constraints.IndexBackedConstraintDescriptor;
import org.neo4j.kernel.api.schema.constraints.NodeKeyConstraintDescriptor;
import org.neo4j.kernel.api.schema.constraints.UniquenessConstraintDescriptor;
import org.neo4j.kernel.api.txstate.ExplicitIndexTransactionState;
import org.neo4j.kernel.api.txstate.TransactionState;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.impl.api.KernelTransactionImplementation;
import org.neo4j.kernel.impl.api.index.IndexingService;
import org.neo4j.kernel.impl.api.state.ConstraintIndexCreator;
import org.neo4j.kernel.impl.constraints.ConstraintSemantics;
import org.neo4j.kernel.impl.index.IndexEntityType;
import org.neo4j.kernel.impl.locking.ResourceTypes;
import org.neo4j.kernel.impl.newapi.IndexTxStateUpdater.LabelChangeType;
import org.neo4j.storageengine.api.EntityType;
import org.neo4j.storageengine.api.StorageReader;
import org.neo4j.storageengine.api.lock.ResourceType;
import org.neo4j.storageengine.api.schema.IndexDescriptor;
import org.neo4j.storageengine.api.schema.IndexDescriptorFactory;
import org.neo4j.storageengine.api.schema.IndexDescriptor.Type;
import org.neo4j.values.storable.Value;
import org.neo4j.values.storable.Values;

public class Operations implements Write, ExplicitIndexWrite, SchemaWrite {
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private final KernelTransactionImplementation ktx;
    private final AllStoreHolder allStoreHolder;
    private final KernelToken token;
    private final StorageReader statement;
    private final AutoIndexing autoIndexing;
    private final IndexTxStateUpdater updater;
    private final DefaultCursors cursors;
    private final ConstraintIndexCreator constraintIndexCreator;
    private final ConstraintSemantics constraintSemantics;
    private final IndexingService indexingService;
    private final Config config;
    private DefaultNodeCursor nodeCursor;
    private DefaultPropertyCursor propertyCursor;
    private DefaultRelationshipScanCursor relationshipCursor;

    public Operations(AllStoreHolder allStoreHolder, IndexTxStateUpdater updater, StorageReader statement, KernelTransactionImplementation ktx, KernelToken token, DefaultCursors cursors, AutoIndexing autoIndexing, ConstraintIndexCreator constraintIndexCreator, ConstraintSemantics constraintSemantics, IndexingService indexingService, Config config) {
        this.token = token;
        this.autoIndexing = autoIndexing;
        this.allStoreHolder = allStoreHolder;
        this.ktx = ktx;
        this.statement = statement;
        this.updater = updater;
        this.cursors = cursors;
        this.constraintIndexCreator = constraintIndexCreator;
        this.constraintSemantics = constraintSemantics;
        this.indexingService = indexingService;
        this.config = config;
    }

    public KernelTransactionImplementation getKtx(){
        return this.ktx;
    }
    public AllStoreHolder getAllStoreHolder(){
        return this.allStoreHolder;
    }

    public DefaultCursors getCursors(){
        return this.cursors;
    }

    public void initialize() {
        this.nodeCursor = this.cursors.allocateNodeCursor();
        this.propertyCursor = this.cursors.allocatePropertyCursor();
        this.relationshipCursor = this.cursors.allocateRelationshipScanCursor();
    }

    public long nodeCreate() {
        this.ktx.assertOpen();
        long nodeId = this.statement.reserveNode();
        this.ktx.txState().nodeDoCreate(nodeId);
        return nodeId;
    }

    public long nodeCreateWithLabels(int[] labels) throws ConstraintValidationException {
        if (labels != null && labels.length != 0) {
            this.ktx.assertOpen();
            long[] lockingIds = SchemaDescriptor.schemaTokenLockingIds(labels);
            Arrays.sort(lockingIds);
            this.ktx.statementLocks().optimistic().acquireShared(this.ktx.lockTracer(), ResourceTypes.LABEL, lockingIds);
            long nodeId = this.statement.reserveNode();
            this.ktx.txState().nodeDoCreate(nodeId);
            this.nodeCursor.single(nodeId, this.allStoreHolder);
            this.nodeCursor.next();
            int prevLabel = -1;
            long[] var6 = lockingIds;
            int var7 = lockingIds.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                long lockingId = var6[var8];
                int label = (int)lockingId;
                if (label != prevLabel) {
                    this.checkConstraintsAndAddLabelToNode(nodeId, label);
                    prevLabel = label;
                }
            }

            return nodeId;
        } else {
            return this.nodeCreate();
        }
    }

    public boolean nodeDelete(long node) throws AutoIndexingKernelException {
        this.ktx.assertOpen();
        return this.nodeDelete(node, true);
    }

    public int nodeDetachDelete(long nodeId) throws KernelException {
        MutableInt count = new MutableInt();
        TwoPhaseNodeForRelationshipLocking locking = new TwoPhaseNodeForRelationshipLocking((relId) -> {
            this.ktx.assertOpen();
            if (this.relationshipDelete(relId, false)) {
                count.increment();
            }

        }, this.ktx.statementLocks().optimistic(), this.ktx.lockTracer());
        locking.lockAllNodesAndConsumeRelationships(nodeId, this.ktx, this.ktx.ambientNodeCursor());
        this.ktx.assertOpen();
        this.nodeDelete(nodeId, false);
        return count.intValue();
    }

    public long relationshipCreate(long sourceNode, int relationshipType, long targetNode) throws EntityNotFoundException {
        this.ktx.assertOpen();
        this.sharedSchemaLock(ResourceTypes.RELATIONSHIP_TYPE, relationshipType);
        this.lockRelationshipNodes(sourceNode, targetNode);
        this.assertNodeExists(sourceNode);
        this.assertNodeExists(targetNode);
        long id = this.statement.reserveRelationship();
        this.ktx.txState().relationshipDoCreate(id, relationshipType, sourceNode, targetNode);
        return id;
    }

    public boolean relationshipDelete(long relationship) throws AutoIndexingKernelException {
        this.ktx.assertOpen();
        return this.relationshipDelete(relationship, true);
    }

    public boolean nodeAddLabel(long node, int nodeLabel) throws EntityNotFoundException, ConstraintValidationException {
        this.sharedSchemaLock(ResourceTypes.LABEL, nodeLabel);
        this.acquireExclusiveNodeLock(node);
        this.ktx.assertOpen();
        this.singleNode(node);
        if (this.nodeCursor.hasLabel(nodeLabel)) {
            return false;
        } else {
            this.checkConstraintsAndAddLabelToNode(node, nodeLabel);
            return true;
        }
    }

    private void checkConstraintsAndAddLabelToNode(long node, int nodeLabel) throws UniquePropertyValueValidationException, UnableToValidateConstraintException {
        int[] existingPropertyKeyIds = this.loadSortedPropertyKeyList();
        if (existingPropertyKeyIds.length > 0) {
            Iterator var5 = this.indexingService.getRelatedUniquenessConstraints(new long[]{(long)nodeLabel}, existingPropertyKeyIds, EntityType.NODE).iterator();

            while(var5.hasNext()) {
                IndexBackedConstraintDescriptor uniquenessConstraint = (IndexBackedConstraintDescriptor)var5.next();
                ExactPredicate[] propertyValues = this.getAllPropertyValues(uniquenessConstraint.schema(), -1, Values.NO_VALUE);
                if (propertyValues != null) {
                    this.validateNoExistingNodeWithExactValues(uniquenessConstraint, propertyValues, node);
                }
            }
        }

        this.ktx.txState().nodeDoAddLabel((long)nodeLabel, node);
        this.updater.onLabelChange(nodeLabel, existingPropertyKeyIds, this.nodeCursor, this.propertyCursor, LabelChangeType.ADDED_LABEL);
    }

    private int[] loadSortedPropertyKeyList() {
        this.nodeCursor.properties(this.propertyCursor);
        if (!this.propertyCursor.next()) {
            return EMPTY_INT_ARRAY;
        } else {
            int[] propertyKeyIds = new int[4];
            int cursor = 0;

            do {
                if (cursor == propertyKeyIds.length) {
                    propertyKeyIds = Arrays.copyOf(propertyKeyIds, cursor * 2);
                }

                propertyKeyIds[cursor++] = this.propertyCursor.propertyKey();
            } while(this.propertyCursor.next());

            if (cursor != propertyKeyIds.length) {
                propertyKeyIds = Arrays.copyOf(propertyKeyIds, cursor);
            }

            Arrays.sort(propertyKeyIds);
            return propertyKeyIds;
        }
    }

    private boolean nodeDelete(long node, boolean lock) throws AutoIndexingKernelException {
        this.ktx.assertOpen();
        if (this.ktx.hasTxStateWithChanges()) {
            if (this.ktx.txState().nodeIsAddedInThisTx(node)) {
                this.autoIndexing.nodes().entityRemoved(this, node);
                this.ktx.txState().nodeDoDelete(node);
                return true;
            }

            if (this.ktx.txState().nodeIsDeletedInThisTx(node)) {
                return false;
            }
        }

        if (lock) {
            this.ktx.statementLocks().optimistic().acquireExclusive(this.ktx.lockTracer(), ResourceTypes.NODE, new long[]{node});
        }

        this.allStoreHolder.singleNode(node, this.nodeCursor);
        if (this.nodeCursor.next()) {
            this.acquireSharedNodeLabelLocks();
            this.autoIndexing.nodes().entityRemoved(this, node);
            this.ktx.txState().nodeDoDelete(node);
            return true;
        } else {
            return false;
        }
    }

    private long[] acquireSharedNodeLabelLocks() {
        long[] labels = this.nodeCursor.labels().all();
        this.ktx.statementLocks().optimistic().acquireShared(this.ktx.lockTracer(), ResourceTypes.LABEL, labels);
        return labels;
    }

    private boolean relationshipDelete(long relationship, boolean lock) throws AutoIndexingKernelException {
        this.allStoreHolder.singleRelationship(relationship, this.relationshipCursor);
        if (this.relationshipCursor.next()) {
            if (lock) {
                this.lockRelationshipNodes(this.relationshipCursor.sourceNodeReference(), this.relationshipCursor.targetNodeReference());
                this.acquireExclusiveRelationshipLock(relationship);
            }

            if (!this.allStoreHolder.relationshipExists(relationship)) {
                return false;
            } else {
                this.ktx.assertOpen();
                this.autoIndexing.relationships().entityRemoved(this, relationship);
                TransactionState txState = this.ktx.txState();
                if (txState.relationshipIsAddedInThisTx(relationship)) {
                    txState.relationshipDoDeleteAddedInThisTx(relationship);
                } else {
                    txState.relationshipDoDelete(relationship, this.relationshipCursor.type(), this.relationshipCursor.sourceNodeReference(), this.relationshipCursor.targetNodeReference());
                }

                return true;
            }
        } else {
            return false;
        }
    }

    private void singleNode(long node) throws EntityNotFoundException {
        this.allStoreHolder.singleNode(node, this.nodeCursor);
        if (!this.nodeCursor.next()) {
            throw new EntityNotFoundException(EntityType.NODE, node);
        }
    }

    private void singleRelationship(long relationship) throws EntityNotFoundException {
        this.allStoreHolder.singleRelationship(relationship, this.relationshipCursor);
        if (!this.relationshipCursor.next()) {
            throw new EntityNotFoundException(EntityType.RELATIONSHIP, relationship);
        }
    }

    private ExactPredicate[] getAllPropertyValues(SchemaDescriptor schema, int changedPropertyKeyId, Value changedValue) {
        int[] schemaPropertyIds = schema.getPropertyIds();
        ExactPredicate[] values = new ExactPredicate[schemaPropertyIds.length];
        int nMatched = 0;
        this.nodeCursor.properties(this.propertyCursor);

        int k;
        while(this.propertyCursor.next()) {
            k = this.propertyCursor.propertyKey();
            int l = ArrayUtils.indexOf(schemaPropertyIds, k);
            if (l >= 0) {
                if (l != -1) {
                    values[l] = IndexQuery.exact(k, this.propertyCursor.propertyValue());
                }

                ++nMatched;
            }
        }

        if (changedPropertyKeyId != -1) {
            k = ArrayUtils.indexOf(schemaPropertyIds, changedPropertyKeyId);
            if (k >= 0) {
                values[k] = IndexQuery.exact(changedPropertyKeyId, changedValue);
                ++nMatched;
            }
        }

        if (nMatched < values.length) {
            return null;
        } else {
            return values;
        }
    }

    private void validateNoExistingNodeWithExactValues(IndexBackedConstraintDescriptor constraint, ExactPredicate[] propertyValues, long modifiedNode) throws UniquePropertyValueValidationException, UnableToValidateConstraintException {
        IndexDescriptor schemaIndexDescriptor = constraint.ownedIndexDescriptor();
        IndexReference indexReference = this.allStoreHolder.indexGetCapability(schemaIndexDescriptor);

        try {
            DefaultNodeValueIndexCursor valueCursor = this.cursors.allocateNodeValueIndexCursor();
            Throwable var8 = null;

            try {
                IndexReaders indexReaders = new IndexReaders(indexReference, this.allStoreHolder);
                Throwable var10 = null;

                try {
                    this.assertIndexOnline(schemaIndexDescriptor);
                    int labelId = schemaIndexDescriptor.schema().keyId();
                    this.ktx.statementLocks().optimistic().acquireExclusive(this.ktx.lockTracer(), ResourceTypes.INDEX_ENTRY, new long[]{ResourceTypes.indexEntryResourceId((long)labelId, propertyValues)});
                    this.allStoreHolder.nodeIndexSeekWithFreshIndexReader(valueCursor, indexReaders.createReader(), propertyValues);
                    if (valueCursor.next() && valueCursor.nodeReference() != modifiedNode) {
                        throw new UniquePropertyValueValidationException(constraint, Phase.VALIDATION, new IndexEntryConflictException(valueCursor.nodeReference(), -1L, IndexQuery.asValueTuple(propertyValues)));
                    }
                } catch (Throwable var35) {
                    var10 = var35;
                    throw var35;
                } finally {
                    if (indexReaders != null) {
                        if (var10 != null) {
                            try {
                                indexReaders.close();
                            } catch (Throwable var34) {
                                var10.addSuppressed(var34);
                            }
                        } else {
                            indexReaders.close();
                        }
                    }

                }
            } catch (Throwable var37) {
                var8 = var37;
                throw var37;
            } finally {
                if (valueCursor != null) {
                    if (var8 != null) {
                        try {
                            valueCursor.close();
                        } catch (Throwable var33) {
                            var8.addSuppressed(var33);
                        }
                    } else {
                        valueCursor.close();
                    }
                }

            }

        } catch (IndexBrokenKernelException | IndexNotApplicableKernelException | IndexNotFoundKernelException var39) {
            throw new UnableToValidateConstraintException(constraint, var39);
        }
    }

    private void assertIndexOnline(IndexDescriptor descriptor) throws IndexNotFoundKernelException, IndexBrokenKernelException {
        switch(this.allStoreHolder.indexGetState(descriptor)) {
            case ONLINE:
                return;
            default:
                throw new IndexBrokenKernelException(this.allStoreHolder.indexGetFailure(descriptor));
        }
    }

    public boolean nodeRemoveLabel(long node, int labelId) throws EntityNotFoundException {
        this.acquireExclusiveNodeLock(node);
        this.ktx.assertOpen();
        this.singleNode(node);
        if (!this.nodeCursor.hasLabel(labelId)) {
            return false;
        } else {
            this.sharedSchemaLock(ResourceTypes.LABEL, labelId);
            this.ktx.txState().nodeDoRemoveLabel((long)labelId, node);
            if (this.indexingService.hasRelatedSchema(labelId, EntityType.NODE)) {
                this.updater.onLabelChange(labelId, this.loadSortedPropertyKeyList(), this.nodeCursor, this.propertyCursor, LabelChangeType.REMOVED_LABEL);
            }

            return true;
        }
    }

    public Value nodeSetProperty(long node, int propertyKey, Value value) throws EntityNotFoundException, ConstraintValidationException, AutoIndexingKernelException {
        this.acquireExclusiveNodeLock(node);
        this.ktx.assertOpen();
        this.singleNode(node);
        long[] labels = this.acquireSharedNodeLabelLocks();
        Value existingValue = this.readNodeProperty(propertyKey);
        int[] existingPropertyKeyIds = null;
        boolean hasRelatedSchema = this.indexingService.hasRelatedSchema(labels, propertyKey, EntityType.NODE);
        if (hasRelatedSchema) {
            existingPropertyKeyIds = this.loadSortedPropertyKeyList();
        }

        if (hasRelatedSchema && !existingValue.equals(value)) {
            Collection<IndexBackedConstraintDescriptor> uniquenessConstraints = this.indexingService.getRelatedUniquenessConstraints(labels, propertyKey, EntityType.NODE);
            NodeSchemaMatcher.onMatchingSchema(uniquenessConstraints.iterator(), propertyKey, existingPropertyKeyIds, (uniquenessConstraint) -> {
                this.validateNoExistingNodeWithExactValues(uniquenessConstraint, this.getAllPropertyValues(uniquenessConstraint.schema(), propertyKey, value), node);
            });
        }

        if (existingValue == Values.NO_VALUE) {
            this.autoIndexing.nodes().propertyAdded(this, node, propertyKey, value);
            this.ktx.txState().nodeDoAddProperty(node, propertyKey, value);
            if (hasRelatedSchema) {
                this.updater.onPropertyAdd(this.nodeCursor, this.propertyCursor, labels, propertyKey, existingPropertyKeyIds, value);
            }

            return Values.NO_VALUE;
        } else {
            this.autoIndexing.nodes().propertyChanged(this, node, propertyKey, existingValue, value);
            if (propertyHasChanged(value, existingValue)) {
                this.ktx.txState().nodeDoChangeProperty(node, propertyKey, value);
                if (hasRelatedSchema) {
                    this.updater.onPropertyChange(this.nodeCursor, this.propertyCursor, labels, propertyKey, existingPropertyKeyIds, existingValue, value);
                }
            }

            return existingValue;
        }
    }

    public Value nodeRemoveProperty(long node, int propertyKey) throws EntityNotFoundException, AutoIndexingKernelException {
        this.acquireExclusiveNodeLock(node);
        this.ktx.assertOpen();
        this.singleNode(node);
        Value existingValue = this.readNodeProperty(propertyKey);
        if (existingValue != Values.NO_VALUE) {
            long[] labels = this.acquireSharedNodeLabelLocks();
            this.autoIndexing.nodes().propertyRemoved(this, node, propertyKey);
            this.ktx.txState().nodeDoRemoveProperty(node, propertyKey);
            if (this.indexingService.hasRelatedSchema(labels, propertyKey, EntityType.NODE)) {
                this.updater.onPropertyRemove(this.nodeCursor, this.propertyCursor, labels, propertyKey, this.loadSortedPropertyKeyList(), existingValue);
            }
        }

        return existingValue;
    }

    public Value relationshipSetProperty(long relationship, int propertyKey, Value value) throws EntityNotFoundException, AutoIndexingKernelException {
        this.acquireExclusiveRelationshipLock(relationship);
        this.ktx.assertOpen();
        this.singleRelationship(relationship);
        Value existingValue = this.readRelationshipProperty(propertyKey);
        if (existingValue == Values.NO_VALUE) {
            this.autoIndexing.relationships().propertyAdded(this, relationship, propertyKey, value);
            this.ktx.txState().relationshipDoReplaceProperty(relationship, propertyKey, Values.NO_VALUE, value);
            return Values.NO_VALUE;
        } else {
            this.autoIndexing.relationships().propertyChanged(this, relationship, propertyKey, existingValue, value);
            if (propertyHasChanged(existingValue, value)) {
                this.ktx.txState().relationshipDoReplaceProperty(relationship, propertyKey, existingValue, value);
            }

            return existingValue;
        }
    }

    public Value relationshipRemoveProperty(long relationship, int propertyKey) throws EntityNotFoundException, AutoIndexingKernelException {
        this.acquireExclusiveRelationshipLock(relationship);
        this.ktx.assertOpen();
        this.singleRelationship(relationship);
        Value existingValue = this.readRelationshipProperty(propertyKey);
        if (existingValue != Values.NO_VALUE) {
            this.autoIndexing.relationships().propertyRemoved(this, relationship, propertyKey);
            this.ktx.txState().relationshipDoRemoveProperty(relationship, propertyKey);
        }

        return existingValue;
    }

    public Value graphSetProperty(int propertyKey, Value value) {
        this.ktx.statementLocks().optimistic().acquireExclusive(this.ktx.lockTracer(), ResourceTypes.GRAPH_PROPS, new long[]{ResourceTypes.graphPropertyResource()});
        this.ktx.assertOpen();
        Value existingValue = this.readGraphProperty(propertyKey);
        if (!existingValue.equals(value)) {
            this.ktx.txState().graphDoReplaceProperty(propertyKey, existingValue, value);
        }

        return existingValue;
    }

    public Value graphRemoveProperty(int propertyKey) {
        this.ktx.statementLocks().optimistic().acquireExclusive(this.ktx.lockTracer(), ResourceTypes.GRAPH_PROPS, new long[]{ResourceTypes.graphPropertyResource()});
        this.ktx.assertOpen();
        Value existingValue = this.readGraphProperty(propertyKey);
        if (existingValue != Values.NO_VALUE) {
            this.ktx.txState().graphDoRemoveProperty(propertyKey);
        }

        return existingValue;
    }

    public void nodeAddToExplicitIndex(String indexName, long node, String key, Object value) throws ExplicitIndexNotFoundKernelException {
        this.ktx.assertOpen();
        this.allStoreHolder.explicitIndexTxState().nodeChanges(indexName).addNode(node, key, value);
    }

    public void nodeRemoveFromExplicitIndex(String indexName, long node) throws ExplicitIndexNotFoundKernelException {
        this.ktx.assertOpen();
        this.allStoreHolder.explicitIndexTxState().nodeChanges(indexName).remove(node);
    }

    public void nodeRemoveFromExplicitIndex(String indexName, long node, String key, Object value) throws ExplicitIndexNotFoundKernelException {
        this.ktx.assertOpen();
        this.allStoreHolder.explicitIndexTxState().nodeChanges(indexName).remove(node, key, value);
    }

    public void nodeRemoveFromExplicitIndex(String indexName, long node, String key) throws ExplicitIndexNotFoundKernelException {
        this.ktx.assertOpen();
        this.allStoreHolder.explicitIndexTxState().nodeChanges(indexName).remove(node, key);
    }

    public void nodeExplicitIndexCreate(String indexName, Map<String, String> customConfig) {
        this.ktx.assertOpen();
        this.allStoreHolder.explicitIndexTxState().createIndex(IndexEntityType.Node, indexName, customConfig);
    }

    public void nodeExplicitIndexCreateLazily(String indexName, Map<String, String> customConfig) {
        this.ktx.assertOpen();
        this.allStoreHolder.getOrCreateNodeIndexConfig(indexName, customConfig);
    }

    public void nodeExplicitIndexDrop(String indexName) throws ExplicitIndexNotFoundKernelException {
        this.ktx.assertOpen();
        ExplicitIndexTransactionState txState = this.allStoreHolder.explicitIndexTxState();
        txState.nodeChanges(indexName).drop();
        txState.deleteIndex(IndexEntityType.Node, indexName);
    }

    public String nodeExplicitIndexSetConfiguration(String indexName, String key, String value) throws ExplicitIndexNotFoundKernelException {
        this.ktx.assertOpen();
        return this.allStoreHolder.explicitIndexStore().setNodeIndexConfiguration(indexName, key, value);
    }

    public String nodeExplicitIndexRemoveConfiguration(String indexName, String key) throws ExplicitIndexNotFoundKernelException {
        this.ktx.assertOpen();
        return this.allStoreHolder.explicitIndexStore().removeNodeIndexConfiguration(indexName, key);
    }

    public void relationshipAddToExplicitIndex(String indexName, long relationship, String key, Object value) throws ExplicitIndexNotFoundKernelException, EntityNotFoundException {
        this.ktx.assertOpen();
        this.allStoreHolder.singleRelationship(relationship, this.relationshipCursor);
        if (this.relationshipCursor.next()) {
            this.allStoreHolder.explicitIndexTxState().relationshipChanges(indexName).addRelationship(relationship, key, value, this.relationshipCursor.sourceNodeReference(), this.relationshipCursor.targetNodeReference());
        } else {
            throw new EntityNotFoundException(EntityType.RELATIONSHIP, relationship);
        }
    }

    public void relationshipRemoveFromExplicitIndex(String indexName, long relationship, String key, Object value) throws ExplicitIndexNotFoundKernelException {
        this.ktx.assertOpen();
        this.allStoreHolder.explicitIndexTxState().relationshipChanges(indexName).remove(relationship, key, value);
    }

    public void relationshipRemoveFromExplicitIndex(String indexName, long relationship, String key) throws ExplicitIndexNotFoundKernelException {
        this.ktx.assertOpen();
        this.allStoreHolder.explicitIndexTxState().relationshipChanges(indexName).remove(relationship, key);
    }

    public void relationshipRemoveFromExplicitIndex(String indexName, long relationship) throws ExplicitIndexNotFoundKernelException {
        this.ktx.assertOpen();
        this.allStoreHolder.explicitIndexTxState().relationshipChanges(indexName).remove(relationship);
    }

    public void relationshipExplicitIndexCreate(String indexName, Map<String, String> customConfig) {
        this.ktx.assertOpen();
        this.allStoreHolder.explicitIndexTxState().createIndex(IndexEntityType.Relationship, indexName, customConfig);
    }

    public void relationshipExplicitIndexCreateLazily(String indexName, Map<String, String> customConfig) {
        this.ktx.assertOpen();
        this.allStoreHolder.getOrCreateRelationshipIndexConfig(indexName, customConfig);
    }

    public void relationshipExplicitIndexDrop(String indexName) throws ExplicitIndexNotFoundKernelException {
        this.ktx.assertOpen();
        ExplicitIndexTransactionState txState = this.allStoreHolder.explicitIndexTxState();
        txState.relationshipChanges(indexName).drop();
        txState.deleteIndex(IndexEntityType.Relationship, indexName);
    }

    private Value readNodeProperty(int propertyKey) {
        this.nodeCursor.properties(this.propertyCursor);
        Value existingValue = Values.NO_VALUE;

        while(this.propertyCursor.next()) {
            if (this.propertyCursor.propertyKey() == propertyKey) {
                existingValue = this.propertyCursor.propertyValue();
                break;
            }
        }

        return existingValue;
    }

    private Value readRelationshipProperty(int propertyKey) {
        this.relationshipCursor.properties(this.propertyCursor);
        Value existingValue = Values.NO_VALUE;

        while(this.propertyCursor.next()) {
            if (this.propertyCursor.propertyKey() == propertyKey) {
                existingValue = this.propertyCursor.propertyValue();
                break;
            }
        }

        return existingValue;
    }

    private Value readGraphProperty(int propertyKey) {
        this.allStoreHolder.graphProperties(this.propertyCursor);
        Value existingValue = Values.NO_VALUE;

        while(this.propertyCursor.next()) {
            if (this.propertyCursor.propertyKey() == propertyKey) {
                existingValue = this.propertyCursor.propertyValue();
                break;
            }
        }

        return existingValue;
    }

    public CursorFactory cursors() {
        return this.cursors;
    }

    public Procedures procedures() {
        return this.allStoreHolder;
    }

    public void release() {
        if (this.nodeCursor != null) {
            this.nodeCursor.close();
            this.nodeCursor = null;
        }

        if (this.propertyCursor != null) {
            this.propertyCursor.close();
            this.propertyCursor = null;
        }

        if (this.relationshipCursor != null) {
            this.relationshipCursor.close();
            this.relationshipCursor = null;
        }

        this.cursors.assertClosed();
        this.cursors.release();
    }

    public Token token() {
        return this.token;
    }

    public ExplicitIndexRead indexRead() {
        return this.allStoreHolder;
    }

    public SchemaRead schemaRead() {
        return this.allStoreHolder;
    }

    public Read dataRead() {
        return this.allStoreHolder;
    }

    public DefaultNodeCursor nodeCursor() {
        return this.nodeCursor;
    }

    public DefaultRelationshipScanCursor relationshipCursor() {
        return this.relationshipCursor;
    }

    public DefaultPropertyCursor propertyCursor() {
        return this.propertyCursor;
    }

    public IndexReference indexCreate(SchemaDescriptor descriptor) throws SchemaKernelException {
        return this.indexCreate(descriptor, (String)this.config.get(GraphDatabaseSettings.default_schema_provider), Optional.empty());
    }

    public IndexReference indexCreate(SchemaDescriptor descriptor, Optional<String> indexName) throws SchemaKernelException {
        return this.indexCreate(descriptor, (String)this.config.get(GraphDatabaseSettings.default_schema_provider), indexName);
    }

    public IndexReference indexCreate(SchemaDescriptor descriptor, String provider, Optional<String> name) throws SchemaKernelException {
        this.exclusiveSchemaLock(descriptor);
        this.ktx.assertOpen();
        assertValidDescriptor(descriptor, OperationContext.INDEX_CREATION);
        this.assertIndexDoesNotExist(OperationContext.INDEX_CREATION, descriptor, name);
        IndexProviderDescriptor providerDescriptor = this.indexingService.indexProviderByName(provider);
        IndexDescriptor index = IndexDescriptorFactory.forSchema(descriptor, name, providerDescriptor);
        index = this.indexingService.getBlessedDescriptorFromProvider(index);
        this.ktx.txState().indexDoAdd(index);
        return index;
    }

    public IndexDescriptor indexUniqueCreate(SchemaDescriptor schema, String provider) throws SchemaKernelException {
        IndexProviderDescriptor providerDescriptor = this.indexingService.indexProviderByName(provider);
        IndexDescriptor index = IndexDescriptorFactory.uniqueForSchema(schema, Optional.empty(), providerDescriptor);
        index = this.indexingService.getBlessedDescriptorFromProvider(index);
        this.ktx.txState().indexDoAdd(index);
        return index;
    }

    public void indexDrop(IndexReference indexReference) throws SchemaKernelException {
        assertValidIndex(indexReference);
        IndexDescriptor index = (IndexDescriptor)indexReference;
        SchemaDescriptor schema = index.schema();
        this.exclusiveSchemaLock(schema);
        this.ktx.assertOpen();

        try {
            IndexDescriptor existingIndex = this.allStoreHolder.indexGetForSchema(schema);
            if (existingIndex == null) {
                throw new NoSuchIndexException(schema);
            }

            if (existingIndex.type() == Type.UNIQUE && this.allStoreHolder.indexGetOwningUniquenessConstraintId(existingIndex) != null) {
                throw new IndexBelongsToConstraintException(schema);
            }
        } catch (NoSuchIndexException | IndexBelongsToConstraintException var5) {
            throw new DropIndexFailureException(schema, var5);
        }

        this.ktx.txState().indexDoDrop(index);
    }

    public ConstraintDescriptor uniquePropertyConstraintCreate(SchemaDescriptor descriptor) throws SchemaKernelException {
        return this.uniquePropertyConstraintCreate(descriptor, (String)this.config.get(GraphDatabaseSettings.default_schema_provider));
    }

    public ConstraintDescriptor uniquePropertyConstraintCreate(SchemaDescriptor descriptor, String provider) throws SchemaKernelException {
        this.exclusiveSchemaLock(descriptor);
        this.ktx.assertOpen();
        assertValidDescriptor(descriptor, OperationContext.CONSTRAINT_CREATION);
        UniquenessConstraintDescriptor constraint = ConstraintDescriptorFactory.uniqueForSchema(descriptor);
        this.assertConstraintDoesNotExist(constraint);
        this.assertIndexDoesNotExist(OperationContext.CONSTRAINT_CREATION, descriptor, Optional.empty());
        this.indexBackedConstraintCreate(constraint, provider);
        return constraint;
    }

    public ConstraintDescriptor nodeKeyConstraintCreate(LabelSchemaDescriptor descriptor) throws SchemaKernelException {
        return this.nodeKeyConstraintCreate(descriptor, (String)this.config.get(GraphDatabaseSettings.default_schema_provider));
    }

    public ConstraintDescriptor nodeKeyConstraintCreate(LabelSchemaDescriptor descriptor, String provider) throws SchemaKernelException {
        this.exclusiveSchemaLock(descriptor);
        this.ktx.assertOpen();
        assertValidDescriptor(descriptor, OperationContext.CONSTRAINT_CREATION);
        NodeKeyConstraintDescriptor constraint = ConstraintDescriptorFactory.nodeKeyForSchema(descriptor);
        this.assertConstraintDoesNotExist(constraint);
        this.assertIndexDoesNotExist(OperationContext.CONSTRAINT_CREATION, descriptor, Optional.empty());
        NodeLabelIndexCursor nodes = this.cursors.allocateNodeLabelIndexCursor();
        Throwable var5 = null;

        try {
            this.allStoreHolder.nodeLabelScan(descriptor.getLabelId(), nodes);
            this.constraintSemantics.validateNodeKeyConstraint(nodes, this.nodeCursor, this.propertyCursor, descriptor);
        } catch (Throwable var14) {
            var5 = var14;
            throw var14;
        } finally {
            if (nodes != null) {
                if (var5 != null) {
                    try {
                        nodes.close();
                    } catch (Throwable var13) {
                        var5.addSuppressed(var13);
                    }
                } else {
                    nodes.close();
                }
            }

        }

        this.indexBackedConstraintCreate(constraint, provider);
        return constraint;
    }

    public ConstraintDescriptor nodePropertyExistenceConstraintCreate(LabelSchemaDescriptor descriptor) throws SchemaKernelException {
        this.exclusiveSchemaLock(descriptor);
        this.ktx.assertOpen();
        assertValidDescriptor(descriptor, OperationContext.CONSTRAINT_CREATION);
        ConstraintDescriptor constraint = ConstraintDescriptorFactory.existsForSchema(descriptor);
        this.assertConstraintDoesNotExist(constraint);
        NodeLabelIndexCursor nodes = this.cursors.allocateNodeLabelIndexCursor();
        Throwable var4 = null;

        try {
            this.allStoreHolder.nodeLabelScan(descriptor.getLabelId(), nodes);
            this.constraintSemantics.validateNodePropertyExistenceConstraint(nodes, this.nodeCursor, this.propertyCursor, descriptor);
        } catch (Throwable var13) {
            var4 = var13;
            throw var13;
        } finally {
            if (nodes != null) {
                if (var4 != null) {
                    try {
                        nodes.close();
                    } catch (Throwable var12) {
                        var4.addSuppressed(var12);
                    }
                } else {
                    nodes.close();
                }
            }

        }

        this.ktx.txState().constraintDoAdd(constraint);
        return constraint;
    }

    public ConstraintDescriptor relationshipPropertyExistenceConstraintCreate(RelationTypeSchemaDescriptor descriptor) throws SchemaKernelException {
        this.exclusiveSchemaLock(descriptor);
        this.ktx.assertOpen();
        assertValidDescriptor(descriptor, OperationContext.CONSTRAINT_CREATION);
        ConstraintDescriptor constraint = ConstraintDescriptorFactory.existsForSchema(descriptor);
        this.assertConstraintDoesNotExist(constraint);
        this.allStoreHolder.relationshipTypeScan(descriptor.getRelTypeId(), this.relationshipCursor);
        this.constraintSemantics.validateRelationshipPropertyExistenceConstraint(this.relationshipCursor, this.propertyCursor, descriptor);
        this.ktx.txState().constraintDoAdd(constraint);
        return constraint;
    }

    public String relationshipExplicitIndexSetConfiguration(String indexName, String key, String value) throws ExplicitIndexNotFoundKernelException {
        this.ktx.assertOpen();
        return this.allStoreHolder.explicitIndexStore().setRelationshipIndexConfiguration(indexName, key, value);
    }

    public String relationshipExplicitIndexRemoveConfiguration(String indexName, String key) throws ExplicitIndexNotFoundKernelException {
        this.ktx.assertOpen();
        return this.allStoreHolder.explicitIndexStore().removeRelationshipIndexConfiguration(indexName, key);
    }

    public void constraintDrop(ConstraintDescriptor descriptor) throws SchemaKernelException {
        SchemaDescriptor schema = descriptor.schema();
        this.exclusiveOptimisticLock(schema.keyType(), (long)schema.keyId());
        this.ktx.assertOpen();

        try {
            this.assertConstraintExists(descriptor);
        } catch (NoSuchConstraintException var4) {
            throw new DropConstraintFailureException(descriptor, var4);
        }

        this.ktx.txState().constraintDoDrop(descriptor);
    }

    private void assertIndexDoesNotExist(OperationContext context, SchemaDescriptor descriptor, Optional<String> name) throws AlreadyIndexedException, AlreadyConstrainedException {
        IndexDescriptor existingIndex = this.allStoreHolder.indexGetForSchema(descriptor);
        if (existingIndex == null && name.isPresent()) {
            IndexReference indexReference = this.allStoreHolder.indexGetForName((String)name.get());
            if (indexReference != IndexReference.NO_INDEX) {
                existingIndex = (IndexDescriptor)indexReference;
            }
        }

        if (existingIndex != null) {
            if (existingIndex.type() != Type.UNIQUE) {
                throw new AlreadyIndexedException(descriptor, context);
            }

            if (context != OperationContext.CONSTRAINT_CREATION || this.constraintIndexHasOwner(existingIndex)) {
                throw new AlreadyConstrainedException(ConstraintDescriptorFactory.uniqueForSchema(descriptor), context, new SilentTokenNameLookup(this.token));
            }
        }

    }

    private void exclusiveOptimisticLock(ResourceType resource, long resourceId) {
        this.ktx.statementLocks().optimistic().acquireExclusive(this.ktx.lockTracer(), resource, new long[]{resourceId});
    }

    private void acquireExclusiveNodeLock(long node) {
        if (!this.ktx.hasTxStateWithChanges() || !this.ktx.txState().nodeIsAddedInThisTx(node)) {
            this.ktx.statementLocks().optimistic().acquireExclusive(this.ktx.lockTracer(), ResourceTypes.NODE, new long[]{node});
        }

    }

    private void acquireExclusiveRelationshipLock(long relationshipId) {
        if (!this.ktx.hasTxStateWithChanges() || !this.ktx.txState().relationshipIsAddedInThisTx(relationshipId)) {
            this.ktx.statementLocks().optimistic().acquireExclusive(this.ktx.lockTracer(), ResourceTypes.RELATIONSHIP, new long[]{relationshipId});
        }

    }

    private void sharedSchemaLock(ResourceType type, int tokenId) {
        this.ktx.statementLocks().optimistic().acquireShared(this.ktx.lockTracer(), type, new long[]{(long)tokenId});
    }

    private void exclusiveSchemaLock(SchemaDescriptor schema) {
        long[] lockingIds = SchemaDescriptor.schemaTokenLockingIds(schema);
        this.ktx.statementLocks().optimistic().acquireExclusive(this.ktx.lockTracer(), schema.keyType(), lockingIds);
    }

    private void lockRelationshipNodes(long startNodeId, long endNodeId) {
        this.acquireExclusiveNodeLock(Math.min(startNodeId, endNodeId));
        if (startNodeId != endNodeId) {
            this.acquireExclusiveNodeLock(Math.max(startNodeId, endNodeId));
        }

    }

    private static boolean propertyHasChanged(Value lhs, Value rhs) {
        return lhs.getClass() != rhs.getClass() || !lhs.equals(rhs);
    }

    private void assertNodeExists(long sourceNode) throws EntityNotFoundException {
        if (!this.allStoreHolder.nodeExists(sourceNode)) {
            throw new EntityNotFoundException(EntityType.NODE, sourceNode);
        }
    }

    private boolean constraintIndexHasOwner(IndexDescriptor descriptor) {
        return this.allStoreHolder.indexGetOwningUniquenessConstraintId(descriptor) != null;
    }

    private void assertConstraintDoesNotExist(ConstraintDescriptor constraint) throws AlreadyConstrainedException {
        if (this.allStoreHolder.constraintExists(constraint)) {
            throw new AlreadyConstrainedException(constraint, OperationContext.CONSTRAINT_CREATION, new SilentTokenNameLookup(this.token));
        }
    }

    public Locks locks() {
        return this.allStoreHolder;
    }

    private void assertConstraintExists(ConstraintDescriptor constraint) throws NoSuchConstraintException {
        if (!this.allStoreHolder.constraintExists(constraint)) {
            throw new NoSuchConstraintException(constraint);
        }
    }

    private static void assertValidDescriptor(SchemaDescriptor descriptor, OperationContext context) throws RepeatedPropertyInCompositeSchemaException {
        int numUnique = Arrays.stream(descriptor.getPropertyIds()).distinct().toArray().length;
        if (numUnique != descriptor.getPropertyIds().length) {
            throw new RepeatedPropertyInCompositeSchemaException(descriptor, context);
        }
    }

    private void indexBackedConstraintCreate(IndexBackedConstraintDescriptor constraint, String provider) throws CreateConstraintFailureException {
        LabelSchemaDescriptor descriptor = constraint.schema();

        try {
            if (this.ktx.hasTxStateWithChanges() && this.ktx.txState().indexDoUnRemove(constraint.ownedIndexDescriptor())) {
                if (!this.ktx.txState().constraintDoUnRemove(constraint)) {
                    this.ktx.txState().constraintDoAdd(constraint, this.ktx.txState().indexCreatedForConstraint(constraint));
                }
            } else {
                Iterator it = this.allStoreHolder.constraintsGetForSchema(descriptor);

                while(it.hasNext()) {
                    if (((ConstraintDescriptor)it.next()).equals(constraint)) {
                        return;
                    }
                }

                long indexId = this.constraintIndexCreator.createUniquenessConstraintIndex(this.ktx, descriptor, provider);
                if (!this.allStoreHolder.constraintExists(constraint)) {
                    this.ktx.txState().constraintDoAdd(constraint, indexId);
                }
            }

        } catch (TransactionFailureException | AlreadyConstrainedException | UniquePropertyValueValidationException var7) {
            throw new CreateConstraintFailureException(constraint, var7);
        }
    }

    private static void assertValidIndex(IndexReference index) throws NoSuchIndexException {
        if (index == IndexReference.NO_INDEX) {
            throw new NoSuchIndexException(index.schema());
        }
    }
}
