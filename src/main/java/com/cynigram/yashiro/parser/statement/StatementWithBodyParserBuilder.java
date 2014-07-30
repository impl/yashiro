package com.cynigram.yashiro.parser.statement;

import com.cynigram.yashiro.ast.StmtNode;
import com.cynigram.yashiro.parser.StatementParser;
import org.codehaus.jparsec.functors.Map;

import java.util.Collections;

public final class StatementWithBodyParserBuilder
{
    private Callables callables = new Callables();
    private Contents contents = new Contents();

    StatementWithBodyParserBuilder (String packageName, String name)
    {
        callables.setPackageName(packageName);
        callables.setName(name);
    }

    public StatementWithBodyParserBuilder contains (ContentParserBuilder content)
    {
        contents.setContent(content);
        return this;
    }

    public RepeatableStatementWithBodyParserBuilder also (String name)
    {
        return new RepeatableStatementWithBodyParserBuilder(name, this);
    }

    public StatementTerminusParserBuilder terminating ()
    {
        return new StatementTerminusParserBuilder(this, Collections.<RepeatableStatementWithBodyParserBuilder>emptyList());
    }

    public <T extends StmtNode> StatementParser mapWith (Map<StatementMatchMap, T> map)
    {
        return terminating().mapWith(map);
    }

    Callables getCallables()
    {
        return callables;
    }

    Contents getContents()
    {
        return contents;
    }
}
