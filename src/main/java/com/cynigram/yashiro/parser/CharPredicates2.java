package com.cynigram.yashiro.parser;

import org.codehaus.jparsec.pattern.CharPredicate;

public final class CharPredicates2
{
    public static final CharPredicate IS_IDENTIFIER_START = new CharPredicate() {
        @Override
        public boolean isChar (char c)
        {
            return Character.isJavaIdentifierStart((int)c);
        }

        @Override
        public String toString ()
        {
            return "Java identifier start";
        }
    };

    public static final CharPredicate IS_IDENTIFIER_CONTINUE = new CharPredicate() {
        @Override
        public boolean isChar (char c)
        {
            return Character.isJavaIdentifierPart((int)c);
        }

        @Override
        public String toString ()
        {
            return "Java identifier continuation";
        }
    };

    public static final CharPredicate IS_LINE_WHITESPACE = new CharPredicate() {
        @Override
        public boolean isChar (char c)
        {
            return c != '\r' && c != '\n' && Character.isWhitespace((int)c);
        }

        @Override
        public String toString ()
        {
            return "single-line whitespaces";
        }
    };

    private CharPredicates2 ()
    {
    }
}
