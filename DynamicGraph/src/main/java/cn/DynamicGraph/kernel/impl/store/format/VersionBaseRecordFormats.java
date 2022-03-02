package cn.DynamicGraph.kernel.impl.store.format;

import org.neo4j.helpers.ArrayUtil;
import org.neo4j.kernel.impl.store.format.Capability;
import org.neo4j.kernel.impl.store.format.CapabilityType;
import org.neo4j.kernel.impl.store.format.RecordFormat;
import org.neo4j.kernel.impl.store.format.RecordFormats;
import org.neo4j.kernel.impl.store.format.standard.MetaDataRecordFormat;
import org.neo4j.kernel.impl.store.record.MetaDataRecord;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class VersionBaseRecordFormats implements VersionRecordFormats{
    private final int generation;
    private final Capability[] capabilities;
    private final String storeVersion;
    private final String introductionVersion;

    protected VersionBaseRecordFormats(String storeVersion, String introductionVersion, int generation, Capability... capabilities) {
        this.storeVersion = storeVersion;
        this.generation = generation;
        this.capabilities = capabilities;
        this.introductionVersion = introductionVersion;
    }

    public String storeVersion() {
        return this.storeVersion;
    }

    public String introductionVersion() {
        return this.introductionVersion;
    }

    public RecordFormat<MetaDataRecord> metaData() {
        return new MetaDataRecordFormat();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof RecordFormats)) {
            return false;
        } else {
            RecordFormats other = (RecordFormats)obj;
            return this.node().equals(other.node()) && this.relationship().equals(other.relationship()) && this.relationshipGroup().equals(other.relationshipGroup()) && this.property().equals(other.property()) && this.labelToken().equals(other.labelToken()) && this.relationshipTypeToken().equals(other.relationshipTypeToken()) && this.propertyKeyToken().equals(other.propertyKeyToken()) && this.dynamic().equals(other.dynamic());
        }
    }

    public int hashCode() {
        int hashCode1 = 17;
        int hashCode = 31 * hashCode1 + this.node().hashCode();
        hashCode = 31 * hashCode + this.relationship().hashCode();
        hashCode = 31 * hashCode + this.relationshipGroup().hashCode();
        hashCode = 31 * hashCode + this.property().hashCode();
        hashCode = 31 * hashCode + this.labelToken().hashCode();
        hashCode = 31 * hashCode + this.relationshipTypeToken().hashCode();
        hashCode = 31 * hashCode + this.propertyKeyToken().hashCode();
        hashCode = 31 * hashCode + this.dynamic().hashCode();
        return hashCode;
    }

    public String toString() {
        return "RecordFormat:" + this.getClass().getSimpleName() + "[" + this.storeVersion() + "]";
    }

    public int generation() {
        return this.generation;
    }

    public Capability[] capabilities() {
        return this.capabilities;
    }

    public boolean hasCapability(Capability capability) {
        return ArrayUtil.contains(this.capabilities(), capability);
    }

    public static boolean hasCompatibleCapabilities(VersionRecordFormats one, VersionRecordFormats other, CapabilityType type) {
        Set<Capability> myFormatCapabilities = (Set) Stream.of(one.capabilities()).filter((capability) -> {
            return capability.isType(type);
        }).collect(Collectors.toSet());
        Set<Capability> otherFormatCapabilities = (Set)Stream.of(other.capabilities()).filter((capability) -> {
            return capability.isType(type);
        }).collect(Collectors.toSet());
        if (myFormatCapabilities.equals(otherFormatCapabilities)) {
            return true;
        } else {
            boolean capabilitiesNotRemoved = otherFormatCapabilities.containsAll(myFormatCapabilities);
            otherFormatCapabilities.removeAll(myFormatCapabilities);
            boolean allAddedAreAdditive = otherFormatCapabilities.stream().allMatch(Capability::isAdditive);
            return capabilitiesNotRemoved && allAddedAreAdditive;
        }
    }

    public boolean hasCompatibleCapabilities(VersionRecordFormats other, CapabilityType type) {
        return hasCompatibleCapabilities(this, other, type);
    }
}
