package com.cynigram.yashiro.parser;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jparsec.functors.Map;

public final class Maps2
{
    public static final Map<String, String> UNESCAPE_MAP = new Map<String, String>() {
        @Override
        public String map (String from)
        {
            return StringEscapeUtils.unescapeJava(from);
        }

        @Override
        public String toString ()
        {
            return "unescaper for quoted strings";
        }
    };

    private Maps2 () {}
}
