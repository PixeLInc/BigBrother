package io.pixelinc.bigbrother.events;

import io.pixelinc.bigbrother.BigBrother;

public class BasicEvent {

    protected BigBrother plugin;

    public BasicEvent(BigBrother plugin) {
        this.plugin = plugin;
    }

}
