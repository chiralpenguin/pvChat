package com.purityvanilla.pvchat.filter.stages;

import com.purityvanilla.pvchat.filter.CharSpan;
import com.purityvanilla.pvchat.filter.TrackedString;

import java.util.ArrayList;
import java.util.List;

public class LowercaseStage implements FilterStage {

    @Override
    public TrackedString apply(TrackedString input) {
        List<CharSpan> result = new ArrayList<>();
        for (CharSpan span : input.chars()) {
            result.add(span.withChar(Character.toLowerCase(span.transformed())));
        }
        return new TrackedString(result);
    }
}
