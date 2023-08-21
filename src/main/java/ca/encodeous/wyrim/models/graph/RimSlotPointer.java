package ca.encodeous.wyrim.models.graph;

import java.util.Objects;

public class RimSlotPointer {
    public int backingSlotId;

    public RimSlotPointer(int backingSlotId, ItemSource backingSource) {
        this.backingSlotId = backingSlotId;
        this.backingSource = backingSource;
    }

    public ItemSource backingSource;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RimSlotPointer that = (RimSlotPointer) o;
        return backingSlotId == that.backingSlotId && backingSource == that.backingSource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(backingSlotId, backingSource);
    }

    public enum ItemSource {
        BANK,
        PLAYER
    }
}
