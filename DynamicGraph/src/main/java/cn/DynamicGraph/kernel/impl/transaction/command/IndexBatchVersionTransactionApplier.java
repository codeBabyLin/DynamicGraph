//package cn.DynamicGraph.kernel.impl.transaction.command;
//
//import cn.DynamicGraph.kernel.impl.store.NodeVersionStore;
//import cn.DynamicGraph.kernel.impl.transaction.state.OnlineVersionIndexUpdates;
//import org.neo4j.kernel.api.labelscan.LabelScanWriter;
//import org.neo4j.kernel.api.labelscan.NodeLabelUpdate;
//import org.neo4j.kernel.impl.api.TransactionApplier;
//import org.neo4j.kernel.impl.api.index.IndexingService;
//import org.neo4j.kernel.impl.api.index.IndexingUpdateService;
//import org.neo4j.kernel.impl.api.index.PropertyCommandsExtractor;
//import org.neo4j.kernel.impl.api.index.PropertyPhysicalToLogicalConverter;
//import org.neo4j.kernel.impl.store.*;
//import org.neo4j.kernel.impl.store.record.ConstraintRule;
//import org.neo4j.kernel.impl.store.record.NodeRecord;
//import org.neo4j.kernel.impl.transaction.command.*;
//import org.neo4j.kernel.impl.transaction.state.IndexUpdates;
//import org.neo4j.storageengine.api.CommandsToApply;
//import org.neo4j.storageengine.api.schema.SchemaRule;
//import org.neo4j.storageengine.api.schema.StoreIndexDescriptor;
//import org.neo4j.util.concurrent.AsyncApply;
//import org.neo4j.util.concurrent.WorkSync;
//import org.neo4j.kernel.impl.api.BatchTransactionApplier.Adapter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//import java.util.function.Supplier;
//
//public class IndexBatchVersionTransactionApplier extends Adapter{
//    private final IndexingService indexingService;
//    private final WorkSync<Supplier<LabelScanWriter>, LabelUpdateWork> labelScanStoreSync;
//    private final WorkSync<IndexingUpdateService, IndexUpdatesWork> indexUpdatesSync;
//    private final IndexBatchVersionTransactionApplier.SingleTransactionApplier transactionApplier;
//    private final IndexVersionActivator indexActivator;
//    private final PropertyStore propertyStore;
//    private List<NodeLabelUpdate> labelUpdates;
//    private IndexUpdates indexUpdates;
//    private long txId;
//
//    public IndexBatchVersionTransactionApplier(IndexingService indexingService, WorkSync<Supplier<LabelScanWriter>, LabelUpdateWork> labelScanStoreSync, WorkSync<IndexingUpdateService, IndexUpdatesWork> indexUpdatesSync, NodeVersionStore nodeStore, RelationshipStore relationshipStore, PropertyStore propertyStore, IndexActivator indexActivator) {
//        this.indexingService = indexingService;
//        this.labelScanStoreSync = labelScanStoreSync;
//        this.indexUpdatesSync = indexUpdatesSync;
//        this.propertyStore = propertyStore;
//        this.transactionApplier = new IndexBatchVersionTransactionApplier.SingleTransactionApplier(nodeStore, relationshipStore);
//        this.indexActivator = new IndexVersionActivator(indexActivator.getIndexingService());
//    }
//
//    public TransactionApplier startTx(CommandsToApply transaction) {
//        this.txId = transaction.transactionId();
//        return this.transactionApplier;
//    }
//
//    private void applyPendingLabelAndIndexUpdates() throws IOException {
//        AsyncApply labelUpdatesApply = null;
//        if (this.labelUpdates != null) {
//            labelUpdatesApply = this.labelScanStoreSync.applyAsync(new LabelUpdateWork(this.labelUpdates));
//            this.labelUpdates = null;
//        }
//
//        if (this.indexUpdates != null && this.indexUpdates.hasUpdates()) {
//            try {
//                this.indexUpdatesSync.apply(new IndexUpdatesWork(this.indexUpdates));
//            } catch (ExecutionException var4) {
//                throw new IOException("Failed to flush index updates", var4);
//            }
//
//            this.indexUpdates = null;
//        }
//
//        if (labelUpdatesApply != null) {
//            try {
//                labelUpdatesApply.await();
//            } catch (ExecutionException var3) {
//                throw new IOException("Failed to flush label updates", var3);
//            }
//        }
//
//    }
//
//    public void close() throws Exception {
//        this.applyPendingLabelAndIndexUpdates();
//    }
//
//    private class SingleTransactionApplier extends org.neo4j.kernel.impl.api.TransactionApplier.Adapter {
//        private final NodeVersionStore nodeStore;
//        private RelationshipStore relationshipStore;
//        private final PropertyCommandsExtractor indexUpdatesExtractor = new PropertyCommandsExtractor();
//        private List<StoreIndexDescriptor> createdIndexes;
//
//        SingleTransactionApplier(NodeVersionStore nodeStore, RelationshipStore relationshipStore) {
//            this.nodeStore = nodeStore;
//            this.relationshipStore = relationshipStore;
//        }
//
//        public void close() throws Exception {
//            if (this.indexUpdatesExtractor.containsAnyEntityOrPropertyUpdate()) {
//                this.indexUpdates().feed(this.indexUpdatesExtractor.getNodeCommands(), this.indexUpdatesExtractor.getRelationshipCommands());
//                this.indexUpdatesExtractor.close();
//            }
//
//            if (this.createdIndexes != null) {
//                IndexBatchVersionTransactionApplier.this.indexingService.createIndexes((StoreIndexDescriptor[])this.createdIndexes.toArray(new StoreIndexDescriptor[0]));
//                this.createdIndexes = null;
//            }
//
//        }
//
//        private IndexUpdates indexUpdates() {
//            if (IndexBatchVersionTransactionApplier.this.indexUpdates == null) {
//                IndexBatchVersionTransactionApplier.this.indexUpdates = new OnlineVersionIndexUpdates(this.nodeStore, this.relationshipStore, IndexBatchVersionTransactionApplier.this.indexingService, new PropertyPhysicalToLogicalConverter(IndexBatchVersionTransactionApplier.this.propertyStore));
//            }
//
//            return IndexBatchVersionTransactionApplier.this.indexUpdates;
//        }
//
//        public boolean visitNodeCommand(Command.NodeCommand command) {
//            NodeRecord before = (NodeRecord)command.getBefore();
//            NodeRecord after = (NodeRecord)command.getAfter();
//            NodeLabels labelFieldBefore = NodeLabelsField.parseLabelsField(before);
//            NodeLabels labelFieldAfter = NodeLabelsField.parseLabelsField(after);
//            if (!labelFieldBefore.isInlined() || !labelFieldAfter.isInlined() || before.getLabelField() != after.getLabelField()) {
//                long[] labelsBefore = labelFieldBefore.getIfLoaded();
//                long[] labelsAfter = labelFieldAfter.getIfLoaded();
//                if (labelsBefore != null && labelsAfter != null) {
//                    if (IndexBatchVersionTransactionApplier.this.labelUpdates == null) {
//                        IndexBatchVersionTransactionApplier.this.labelUpdates = new ArrayList();
//                    }
//
//                    IndexBatchVersionTransactionApplier.this.labelUpdates.add(NodeLabelUpdate.labelChanges(command.getKey(), labelsBefore, labelsAfter, IndexBatchVersionTransactionApplier.this.txId));
//                }
//            }
//
//            return this.indexUpdatesExtractor.visitNodeCommand(command);
//        }
//
//        public boolean visitRelationshipCommand(Command.RelationshipCommand command) {
//            return this.indexUpdatesExtractor.visitRelationshipCommand(command);
//        }
//
//        public boolean visitPropertyCommand(Command.PropertyCommand command) {
//            return this.indexUpdatesExtractor.visitPropertyCommand(command);
//        }
//
//        public boolean visitSchemaRuleCommand(Command.SchemaRuleCommand command) throws IOException {
//            SchemaRule schemaRule = command.getSchemaRule();
//            if (command.getSchemaRule() instanceof StoreIndexDescriptor) {
//                StoreIndexDescriptor indexRule = (StoreIndexDescriptor)schemaRule;
//                IndexBatchVersionTransactionApplier.this.applyPendingLabelAndIndexUpdates();
//                switch(command.getMode()) {
//                    case UPDATE:
//                        if (indexRule.canSupportUniqueConstraint()) {
//                            IndexBatchVersionTransactionApplier.this.indexActivator.activateIndex(schemaRule.getId());
//                        }
//                        break;
//                    case CREATE:
//                        this.createdIndexes = (List)(this.createdIndexes == null ? new ArrayList() : this.createdIndexes);
//                        this.createdIndexes.add(indexRule);
//                        break;
//                    case DELETE:
//                        IndexBatchVersionTransactionApplier.this.indexingService.dropIndex(indexRule);
//                        IndexBatchVersionTransactionApplier.this.indexActivator.indexDropped(schemaRule.getId());
//                        break;
//                    default:
//                        throw new IllegalStateException(command.getMode().name());
//                }
//            } else if (schemaRule instanceof ConstraintRule) {
//                ConstraintRule constraintRule = (ConstraintRule)schemaRule;
//                switch(command.getMode()) {
//                    case UPDATE:
//                    case CREATE:
//                        IndexBatchVersionTransactionApplier.this.indexingService.putConstraint(constraintRule);
//                        break;
//                    case DELETE:
//                        IndexBatchVersionTransactionApplier.this.indexingService.removeConstraint(constraintRule.getId());
//                        break;
//                    default:
//                        throw new IllegalStateException(command.getMode().name());
//                }
//            }
//
//            return false;
//        }
//    }
//}
