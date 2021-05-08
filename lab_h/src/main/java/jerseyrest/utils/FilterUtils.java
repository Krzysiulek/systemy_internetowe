package jerseyrest.utils;

import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.filters.RegexFilter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterUtils {

    public static RegexFilter containsFilter(String fieldName, String text) {
        var regex = Pattern.compile(Pattern.quote(StringUtils.defaultIfBlank(text, "")));

        RegexFilter filter = Filters.regex(fieldName)
                                    .pattern(regex)
                                    .caseInsensitive();

        return filter;
    }
}
