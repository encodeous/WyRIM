package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.models.ui.client.RimSession;
import ca.encodeous.wyrim.models.ui.server.BankSession;
import com.wynntils.core.components.Service;

import java.util.List;

public class InventorySessionService extends Service {
    public InventorySessionService() {
        super(List.of());
    }

    public boolean isActive(){
        return active;
    }

    public void endSession(){
        active = false;
    }

    public BankSession getBacking() {
        return backing;
    }

    public void setBacking(BankSession backing) {
        this.backing = backing;
        active = true;
    }

    public RimSession getFront() {
        return front;
    }

    public void setFront(RimSession front) {
        this.front = front;
    }

    private BankSession backing;
    private RimSession front;

    private boolean active = false;
}
