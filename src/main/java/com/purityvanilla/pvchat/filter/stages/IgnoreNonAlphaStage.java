package com.purityvanilla.pvchat.filter.stages;

import com.purityvanilla.pvchat.filter.CharSpan;
import com.purityvanilla.pvchat.filter.TrackedString;

import java.util.ArrayList;
import java.util.List;

public class IgnoreNonAlphaStage implements FilterStage {

    @Override
    public TrackedString apply(TrackedString input) {
        List<CharSpan> result = new ArrayList<>();
        for (CharSpan span : input.chars()) {
            boolean ignored = !Character.isLetter(span.transformed());
            result.add(span.withIgnored(ignored));
        }
        return new TrackedString(result);
    }
}
