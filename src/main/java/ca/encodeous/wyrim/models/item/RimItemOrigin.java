package ca.encodeous.wyrim.models.item;

import java.util.Objects;

public class RimItemOrigin {
    public final int slotId;
    public final ItemSource backingSource;

    public RimItemOrigin(int slotId, ItemSource backingSource) {
        this.slotId = slotId;
        this.backingSource = backingSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RimItemOrigin that = (RimItemOrigin) o;
        return slotId == that.slotId && backingSource == that.backingSource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotId, backingSource);
    }

    public enum ItemSource {
        BANK,
        PLAYER
    }
}
