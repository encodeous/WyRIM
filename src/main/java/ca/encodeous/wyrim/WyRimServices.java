package ca.encodeous.wyrim;

import ca.encodeous.wyrim.services.*;

public class WyRimServices {
    public static final ItemCacheService Cache = new ItemCacheService();
    public static final SearchService Search = new SearchService();
    public static final InventorySessionService Session = new InventorySessionService();
    public static final ItemStorageService Storage = new ItemStorageService();
    public static final WyRimCoreService Core = new WyRimCoreService();
}
