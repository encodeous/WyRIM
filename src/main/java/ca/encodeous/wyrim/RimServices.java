package ca.encodeous.wyrim;

import ca.encodeous.wyrim.models.state.RimToken;
import ca.encodeous.wyrim.services.*;

public class RimServices {
    public static final ItemCacheService Cache = new ItemCacheService();
    public static final SearchService Search = new SearchService();
    public static final InventorySessionService Session = new InventorySessionService();
    public static final ItemStorageService Storage = new ItemStorageService();
    public static final RimCoreService Core = new RimCoreService();
    public static final TokenService Token = new TokenService();
}
