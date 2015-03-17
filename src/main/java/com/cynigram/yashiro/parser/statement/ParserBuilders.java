package com.cynigram.yashiro.parser.statement;

import com.cynigram.yashiro.ast.ExprNode;
import org.codehaus.jparsec.Parser;

public final class ParserBuilders
{
    public static StatementWithoutBodyParserBuilder statement (String packageName, String name)
    {
        return new StatementWithoutBodyParserBuilder(packageName, name);
    }

    public static StatementWithBodyParserBuilder statementWithBody (String packageName, String name)
    {
        return new StatementWithBodyParserBuilder(packageName, name);
    }

    public static ContentParserBuilder expr (String group)
    {
        return new ContentParserBuilder().expr(group);
    }

    public static ContentParserBuilder expr (String group, Parser<? extends ExprNode> parser)
    {
        return new ContentParserBuilder().expr(group, parser);
    }

    public static ContentParserBuilder id (String name)
    {
        return new ContentParserBuilder().id(name);
    }

    public static ContentParserBuilder id (String group, String name)
    {
        return new ContentParserBuilder().id(group, name);
    }

    public static ContentParserBuilder callable (String name)
    {
        return new ContentParserBuilder().callable(name);
    }

    public static ContentParserBuilder term (String term)
    {
        return new ContentParserBuilder().term(term);
    }

    public static ContentParserBuilder select (ContentParserBuilder... options)
    {
        return new ContentParserBuilder().select(options);
    }

    private ParserBuilders ()
    {}
}
