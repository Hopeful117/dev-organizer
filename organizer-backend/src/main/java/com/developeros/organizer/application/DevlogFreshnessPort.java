package com.developeros.organizer.application;

import com.developeros.organizer.domain.DevlogProjectReference;

import java.util.List;

public interface DevlogFreshnessPort {
    List<DevlogFreshnessObservation> fetch(DevlogProjectReference project);
}
