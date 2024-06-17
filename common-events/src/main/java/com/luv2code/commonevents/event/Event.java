package com.luv2code.commonevents.event;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Event {
    UUID getEventID();
    LocalDateTime getEventTime();
}
