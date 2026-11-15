package com.flairs.notifications;

import java.time.Instant;

// OUR copy of the Task API's event. We do not share a jar with the Task API: each service owns its classes
// and only the JSON shape is shared. Unknown JSON fields are ignored, so the Task API can add fields safely.
public record TaskEvent(String type, Long taskId, String title, Long ownerId, Instant occurredAt) {
}
