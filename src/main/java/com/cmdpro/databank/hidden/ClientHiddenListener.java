package com.cmdpro.databank.hidden;

import java.util.ArrayList;
import java.util.List;

public interface ClientHiddenListener {
    List<ClientHiddenListener> HIDDEN_LISTENERS = new ArrayList<>();
    default void onHide(Hidden hidden) {}
    default void onUnhide(Hidden unlocked) {}
    default void onHide(List<Hidden> hidden) {}
    default void onUnhide(List<Hidden> unlocked) {}
}
