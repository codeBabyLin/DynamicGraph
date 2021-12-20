//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.neo4j.kernel.impl.store.record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.neo4j.helpers.collection.Iterables;
import org.neo4j.kernel.impl.store.NodeLabelsField;

public class NodeRecord extends PrimitiveRecord {
    private long nextRel;
    private long labels;
    private Collection<DynamicRecord> dynamicLabelRecords;
    private boolean isLight;
    private boolean dense;
    private long version;

    public NodeRecord(long id) {
        super(id);
    }

    public NodeRecord initialize(boolean inUse, long nextProp, boolean dense, long nextRel, long labels,long version) {
        this.version = version;
        return initialize(inUse,nextProp, dense, nextRel, labels);
    }
    public NodeRecord initialize(boolean inUse, long nextProp, boolean dense, long nextRel, long labels) {
        super.initialize(inUse, nextProp);
        this.nextRel = nextRel;
        this.dense = dense;
        this.labels = labels;
        this.dynamicLabelRecords = Collections.emptyList();
        this.isLight = true;
        return this;
    }

    /** @deprecated */
    @Deprecated
    public NodeRecord(long id, boolean dense, long nextRel, long nextProp) {
        this(id, false, dense, nextRel, nextProp, 0L);
    }

    /** @deprecated */
    @Deprecated
    public NodeRecord(long id, boolean inUse, boolean dense, long nextRel, long nextProp, long labels) {
        super(id, nextProp);
        this.nextRel = nextRel;
        this.dense = dense;
        this.labels = labels;
        this.setInUse(inUse);
    }

    /** @deprecated */
    @Deprecated
    public NodeRecord(long id, boolean dense, long nextRel, long nextProp, boolean inUse) {
        this(id, dense, nextRel, nextProp);
        this.setInUse(inUse);
    }

    public void clear() {
        this.initialize(false, (long)Record.NO_NEXT_PROPERTY.intValue(), false, (long)Record.NO_NEXT_RELATIONSHIP.intValue(), (long)Record.NO_LABELS_FIELD.intValue());
    }

    public long getVersion(){return this.version;}
    public void setVersion(long version){
        this.version = version;
    }

    public long getNextRel() {
        return this.nextRel;
    }

    public void setNextRel(long nextRel) {
        this.nextRel = nextRel;
    }

    public void setLabelField(long labels, Collection<DynamicRecord> dynamicRecords) {
        this.labels = labels;
        this.dynamicLabelRecords = dynamicRecords;
        this.isLight = dynamicRecords.isEmpty();
    }

    public long getLabelField() {
        return this.labels;
    }

    public boolean isLight() {
        return this.isLight;
    }

    public Collection<DynamicRecord> getDynamicLabelRecords() {
        return this.dynamicLabelRecords;
    }

    public Iterable<DynamicRecord> getUsedDynamicLabelRecords() {
        return Iterables.filter(AbstractBaseRecord::inUse, this.dynamicLabelRecords);
    }

    public boolean isDense() {
        return this.dense;
    }

    public void setDense(boolean dense) {
        this.dense = dense;
    }

    public String toString() {
        String denseInfo = (this.dense ? "group" : "rel") + "=" + this.nextRel;
        String lightHeavyInfo = this.isLight ? "light" : (this.dynamicLabelRecords.isEmpty() ? "heavy" : "heavy,dynlabels=" + this.dynamicLabelRecords);
        return "Node[" + this.getId() + ",used=" + this.inUse() + "," + denseInfo + ",prop=" + this.getNextProp() + ",labels=" + NodeLabelsField.parseLabelsField(this) + "," + lightHeavyInfo + ",secondaryUnitId=" + this.getSecondaryUnitId() + "]";
    }

    public void setIdTo(PropertyRecord property) {
        property.setNodeId(this.getId());
    }

    public NodeRecord clone() {
        NodeRecord clone = (new NodeRecord(this.getId())).initialize(this.inUse(), this.nextProp, this.dense, this.nextRel, this.labels);
        clone.isLight = this.isLight;
        if (this.dynamicLabelRecords.size() > 0) {
            List<DynamicRecord> clonedLabelRecords = new ArrayList(this.dynamicLabelRecords.size());
            Iterator var3 = this.dynamicLabelRecords.iterator();

            while(var3.hasNext()) {
                DynamicRecord labelRecord = (DynamicRecord)var3.next();
                clonedLabelRecords.add(labelRecord.clone());
            }

            clone.dynamicLabelRecords = clonedLabelRecords;
        }

        clone.setSecondaryUnitId(this.getSecondaryUnitId());
        return clone;
    }
}
