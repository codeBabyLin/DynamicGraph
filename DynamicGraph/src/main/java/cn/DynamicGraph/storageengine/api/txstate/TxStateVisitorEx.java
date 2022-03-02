package cn.DynamicGraph.storageengine.api.txstate;

import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.set.primitive.LongSet;
import org.neo4j.internal.kernel.api.exceptions.schema.ConstraintValidationException;
import org.neo4j.internal.kernel.api.exceptions.schema.CreateConstraintFailureException;
import org.neo4j.internal.kernel.api.schema.constraints.ConstraintDescriptor;
import org.neo4j.storageengine.api.StorageProperty;
import org.neo4j.storageengine.api.schema.IndexDescriptor;
import org.neo4j.storageengine.api.txstate.TxStateVisitor;

import java.util.Iterator;
import java.util.function.Function;

public interface TxStateVisitorEx extends AutoCloseable{

    TxStateVisitorEx EMPTY = new TxStateVisitorEx.Adapter();
    TxStateVisitorEx.Decorator NO_DECORATION = (txStateVisitor) -> {
        return txStateVisitor;
    };

    void visitNodeVersionChange(long var1,long var2);
    void visitCreatedNode(long var1);

    void visitDeletedNode(long var1);

    void visitCreatedRelationship(long var1, int var3, long var4, long var6) throws ConstraintValidationException;

    void visitDeletedRelationship(long var1);

    void visitNodePropertyChanges(long var1, Iterator<StorageProperty> var3, Iterator<StorageProperty> var4, IntIterable var5) throws ConstraintValidationException;

    void visitRelPropertyChanges(long var1, Iterator<StorageProperty> var3, Iterator<StorageProperty> var4, IntIterable var5) throws ConstraintValidationException;

    void visitGraphPropertyChanges(Iterator<StorageProperty> var1, Iterator<StorageProperty> var2, IntIterable var3);

    void visitNodeLabelChanges(long var1, LongSet var3, LongSet var4) throws ConstraintValidationException;

    void visitAddedIndex(IndexDescriptor var1);

    void visitRemovedIndex(IndexDescriptor var1);

    void visitAddedConstraint(ConstraintDescriptor var1) throws CreateConstraintFailureException;

    void visitRemovedConstraint(ConstraintDescriptor var1);

    void visitCreatedLabelToken(long var1, String var3);

    void visitCreatedPropertyKeyToken(long var1, String var3);

    void visitCreatedRelationshipTypeToken(long var1, String var3);

    void close();

    public interface Decorator extends Function<TxStateVisitorEx, TxStateVisitorEx> {
    }

    public static class Delegator implements TxStateVisitorEx {
        private final TxStateVisitorEx actual;

        public Delegator(TxStateVisitorEx actual) {
            assert actual != null;

            this.actual = actual;
        }

        @Override
        public void visitNodeVersionChange(long var1, long var2) {
            this.actual.visitNodeVersionChange(var1,var2);
        }

        public void visitCreatedNode(long id) {
            this.actual.visitCreatedNode(id);
        }

        public void visitDeletedNode(long id) {
            this.actual.visitDeletedNode(id);
        }

        public void visitCreatedRelationship(long id, int type, long startNode, long endNode) throws ConstraintValidationException {
            this.actual.visitCreatedRelationship(id, type, startNode, endNode);
        }

        public void visitDeletedRelationship(long id) {
            this.actual.visitDeletedRelationship(id);
        }

        public void visitNodePropertyChanges(long id, Iterator<StorageProperty> added, Iterator<StorageProperty> changed, IntIterable removed) throws ConstraintValidationException {
            this.actual.visitNodePropertyChanges(id, added, changed, removed);
        }

        public void visitRelPropertyChanges(long id, Iterator<StorageProperty> added, Iterator<StorageProperty> changed, IntIterable removed) throws ConstraintValidationException {
            this.actual.visitRelPropertyChanges(id, added, changed, removed);
        }

        public void visitGraphPropertyChanges(Iterator<StorageProperty> added, Iterator<StorageProperty> changed, IntIterable removed) {
            this.actual.visitGraphPropertyChanges(added, changed, removed);
        }

        public void visitNodeLabelChanges(long id, LongSet added, LongSet removed) throws ConstraintValidationException {
            this.actual.visitNodeLabelChanges(id, added, removed);
        }

        public void visitAddedIndex(IndexDescriptor index) {
            this.actual.visitAddedIndex(index);
        }

        public void visitRemovedIndex(IndexDescriptor index) {
            this.actual.visitRemovedIndex(index);
        }

        public void visitAddedConstraint(ConstraintDescriptor constraint) throws CreateConstraintFailureException {
            this.actual.visitAddedConstraint(constraint);
        }

        public void visitRemovedConstraint(ConstraintDescriptor constraint) {
            this.actual.visitRemovedConstraint(constraint);
        }

        public void visitCreatedLabelToken(long id, String name) {
            this.actual.visitCreatedLabelToken(id, name);
        }

        public void visitCreatedPropertyKeyToken(long id, String name) {
            this.actual.visitCreatedPropertyKeyToken(id, name);
        }

        public void visitCreatedRelationshipTypeToken(long id, String name) {
            this.actual.visitCreatedRelationshipTypeToken(id, name);
        }

        public void close() {
            this.actual.close();
        }
    }

    public static class Adapter implements TxStateVisitorEx {
        public Adapter() {
        }

        @Override
        public void visitNodeVersionChange(long var1, long var2) {

        }

        public void visitCreatedNode(long id) {
        }

        public void visitDeletedNode(long id) {
        }

        public void visitCreatedRelationship(long id, int type, long startNode, long endNode) {
        }

        public void visitDeletedRelationship(long id) {
        }

        public void visitNodePropertyChanges(long id, Iterator<StorageProperty> added, Iterator<StorageProperty> changed, IntIterable removed) {
        }

        public void visitRelPropertyChanges(long id, Iterator<StorageProperty> added, Iterator<StorageProperty> changed, IntIterable removed) {
        }

        public void visitGraphPropertyChanges(Iterator<StorageProperty> added, Iterator<StorageProperty> changed, IntIterable removed) {
        }

        public void visitNodeLabelChanges(long id, LongSet added, LongSet removed) {
        }

        public void visitAddedIndex(IndexDescriptor index) {
        }

        public void visitRemovedIndex(IndexDescriptor index) {
        }

        public void visitAddedConstraint(ConstraintDescriptor element) throws CreateConstraintFailureException {
        }

        public void visitRemovedConstraint(ConstraintDescriptor element) {
        }

        public void visitCreatedLabelToken(long id, String name) {
        }

        public void visitCreatedPropertyKeyToken(long id, String name) {
        }

        public void visitCreatedRelationshipTypeToken(long id, String name) {
        }

        public void close() {
        }
    }
}
