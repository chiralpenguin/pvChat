package com.purityvanilla.pvchat.filter.stages;

import com.purityvanilla.pvchat.filter.CharSpan;
import com.purityvanilla.pvchat.filter.CharacterMappings;
import com.purityvanilla.pvchat.filter.TrackedString;

import java.util.ArrayList;
import java.util.List;

public class LeetspeakStage implements FilterStage {

    @Override
    public TrackedString apply(TrackedString input) {
        List<CharSpan> result = new ArrayList<>(input.size());

        for (CharSpan span : input.chars()) {
            if (span.ignored()) {
                result.add(span);
                continue;
            }

            char resolved = CharacterMappings.resolveLeet(span.transformed());
            result.add(span.withChar(resolved));
        }

        return new TrackedString(result);
    }
}
