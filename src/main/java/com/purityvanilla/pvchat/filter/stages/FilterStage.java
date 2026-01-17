package com.purityvanilla.pvchat.filter.stages;

import com.purityvanilla.pvchat.filter.TrackedString;

@FunctionalInterface
public interface FilterStage {
    TrackedString apply(TrackedString input);
}
