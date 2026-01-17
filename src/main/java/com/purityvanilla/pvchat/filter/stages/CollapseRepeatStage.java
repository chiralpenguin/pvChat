package com.purityvanilla.pvchat.filter.stages;

import com.purityvanilla.pvchat.filter.CharSpan;
import com.purityvanilla.pvchat.filter.TrackedString;

import java.util.ArrayList;
import java.util.List;

public class CollapseRepeatStage implements FilterStage {

    @Override
    public TrackedString apply(TrackedString input) {
        List<CharSpan> result = new ArrayList<>();

        for (CharSpan span : input.chars()) {
            if (span.ignored()) {
                result.add(span);
                continue;
            }

            // Find last non-ignored span in result
            CharSpan lastNonIgnored = findLastNonIgnored(result);

            if (lastNonIgnored != null && lastNonIgnored.transformed() == span.transformed()) {
                // Merge: extend the previous non-ignored span
                int lastIndex = findLastNonIgnoredIndex(result);
                result.set(lastIndex, new CharSpan(
                        lastNonIgnored.transformed(),
                        lastNonIgnored.origStart(),
                        span.origEnd(),
                        false
                ));
            } else {
                result.add(span);
            }
        }

        return new TrackedString(result);
    }

    private CharSpan findLastNonIgnored(List<CharSpan> spans) {
        for (int i = spans.size() - 1; i >= 0; i--) {
            if (!spans.get(i).ignored()) {
                return spans.get(i);
            }
        }
        return null;
    }

    private int findLastNonIgnoredIndex(List<CharSpan> spans) {
        for (int i = spans.size() - 1; i >= 0; i--) {
            if (!spans.get(i).ignored()) {
                return i;
            }
        }
        return -1;
    }
}
