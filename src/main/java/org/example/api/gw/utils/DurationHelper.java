package org.example.api.gw.utils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationHelper {

    private static final Pattern TIMEOUT_PATTERN = Pattern.compile("^(\\d+)(\\D+)$");

    public static Duration toDuration(String timeString) {
        Matcher matcher = TIMEOUT_PATTERN.matcher(Strings.nullSanitized(timeString));
        if (matcher.find()) {
            String timeValueString = matcher.group(1);
            String timeUnitString = matcher.group(2);
            TimeAdjustment multiplier = TimeAdjustment.forString(timeUnitString);
            return Duration.of(Long.parseLong(timeValueString), multiplier.unit());
        }
        else {
            throw new IllegalStateException("Cannot parse time '" + timeString + "'");
        }
    }

    enum TimeAdjustment {

        MILLISECONDS(ChronoUnit.MILLIS, "ms"),
        SECONDS(ChronoUnit.SECONDS, "s");

        private final ChronoUnit unit;
        private final String descriptor;

        TimeAdjustment(ChronoUnit unit, String descriptor) {
            this.unit = unit;
            this.descriptor = descriptor;
        }

        public ChronoUnit unit() {
            return unit;
        }

        public static TimeAdjustment forString(String timeString) {
            return Arrays.stream(TimeAdjustment.values())
                    .filter(value -> value.descriptor.equals(timeString.toLowerCase()))
                    .findFirst()
                    .orElseThrow(
                            () -> new IllegalStateException("Cannot parse time string. Unknown time unit '" + timeString + "'"));
        }
    }
}
