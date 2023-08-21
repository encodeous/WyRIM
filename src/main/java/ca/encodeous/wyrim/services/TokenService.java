package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.models.graph.ItemSnapshot;
import ca.encodeous.wyrim.models.graph.ItemTransaction;
import ca.encodeous.wyrim.models.state.RimToken;
import com.wynntils.core.components.Service;

import java.util.*;

public class TokenService extends Service {
    public static final RimToken ROOT = new RimToken(new UUID(0, 0));
    public TokenService() {
        super(List.of());
    }
    private final HashSet<RimToken> outstanding = new HashSet<>();
    private final HashSet<RimToken> resolved = new HashSet<>();
    private final HashMap<RimToken, ItemTransaction> transactions = new HashMap<>();
    private final HashMap<RimToken, ArrayList<ItemSnapshot>> snapshots = new HashMap<>();
    public RimToken checkout(ItemTransaction transaction){
        var token = new RimToken(UUID.randomUUID());
        transactions.put(token, transaction);
        outstanding.add(token);
        return token;
    }

    public void addSnapshot(ItemSnapshot snapshot){
        var token = snapshot.requiredTransaction.token;
        if(!snapshots.containsKey(token)){
            snapshots.put(token, new ArrayList<>());
        }

        snapshots.get(token).add(snapshot);
    }
    public RimToken reCheckout(UUID id){
        var token = new RimToken(id);
        if(resolved.contains(token) || !outstanding.contains(token)){
            throw new IllegalStateException("Tried to checkout a token that was already resolved or does not exist.");
        }
        return token;
    }

    public boolean isResolved(RimToken token){
        return resolved.contains(token);
    }

    public void resolve(RimToken token){
        if(resolved.contains(token) || !outstanding.contains(token)){
            throw new IllegalStateException("Tried to resolve a token that was already resolved or does not exist.");
        }
        resolved.add(token);
        outstanding.remove(token);
    }

    public ItemTransaction getTransaction(RimToken token){
        return transactions.get(token);
    }
    public ArrayList<ItemSnapshot> getSnapshotsBlocked(RimToken token){
        return snapshots.getOrDefault(token, new ArrayList<>());
    }
    public void destroyTokens(){
        outstanding.clear();
        resolved.clear();
        transactions.clear();
        snapshots.clear();
    }
}
