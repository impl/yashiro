package com.cynigram.yashiro.parser;

import org.codehaus.jparsec.Parser;

public final class TagParsers
{
    public static Parser<?> term (String name)
    {
        return Tag.TERM.parserOf(name);
    }

    public static Parser<String> name ()
    {
        return Tag.NAME.parser();
    }

    public static Parser<?> name (final String name)
    {
        return Tag.NAME.parserOf(name);
    }

    public static Parser<?> name (final String... name)
    {
        return Tag.NAME.parserOf(name);
    }

    public static Parser<Long> integer ()
    {
        return Tag.INTEGER.parser();
    }

    public static Parser<String> decimal ()
    {
        return Tag.DECIMAL.parser();
    }

    public static Parser<String> data ()
    {
        return Tag.DATA.parser();
    }

    public static Parser<String> statementBlock ()
    {
        return Tag.STATEMENT_BLOCK.parser();
    }

    public static Parser<String> variableBlock ()
    {
        return Tag.VARIABLE_BLOCK.parser();
    }

    public static Parser<Stripping> strip ()
    {
        return Tag.STRIP.parser();
    }

    public static Parser<?> strip (Stripping expected)
    {
        return Tag.STRIP.parserOf(expected);
    }

    public static Parser<?> strip (boolean expected)
    {
        return Tag.STRIP.parserOf(expected);
    }

    private TagParsers ()
    {}
}
