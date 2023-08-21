package ca.encodeous.wyrim.models.state;

import java.util.UUID;

import static ca.encodeous.wyrim.RimServices.Token;

public record RimToken(UUID id){
    public boolean isResolved(){
        return Token.isResolved(this);
    }
    public void resolve(){
        Token.resolve(this);
    }
}
