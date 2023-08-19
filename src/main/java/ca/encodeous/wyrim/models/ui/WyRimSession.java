package ca.encodeous.wyrim.models.ui;

import ca.encodeous.wyrim.models.ui.client.RimSession;
import ca.encodeous.wyrim.models.ui.server.BankSession;

public class WyRimSession {
    public BankSession serverSession;
    public RimSession clientSession;
}
