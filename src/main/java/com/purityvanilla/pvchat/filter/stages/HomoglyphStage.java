package com.purityvanilla.pvchat.filter.stages;

import com.ibm.icu.text.SpoofChecker;
import com.purityvanilla.pvchat.filter.CharSpan;
import com.purityvanilla.pvchat.filter.TrackedString;

import java.util.ArrayList;
import java.util.List;

public class HomoglyphStage implements FilterStage {
    private final SpoofChecker spoofChecker;

    public HomoglyphStage() {
        this.spoofChecker = new SpoofChecker.Builder().build();
    }

    @Override
    public TrackedString apply(TrackedString input) {
        List<CharSpan> result = new ArrayList<>(input.size());

        for (CharSpan span : input.chars()) {
            if (span.ignored()) {
                result.add(span);
                continue;
            }

            String skeleton = spoofChecker.getSkeleton(String.valueOf(span.transformed()));

            if (skeleton.length() == 1) {
                result.add(span.withChar(skeleton.charAt(0)));
            } else if (skeleton.isEmpty()) {
                result.add(span.withIgnored(true));
            } else {
                for (int j = 0; j < skeleton.length(); j++) {
                    result.add(new CharSpan(
                            skeleton.charAt(j),
                            span.origStart(),
                            span.origEnd(),
                            false
                    ));
                }
            }
        }

        return new TrackedString(result);
    }
}
