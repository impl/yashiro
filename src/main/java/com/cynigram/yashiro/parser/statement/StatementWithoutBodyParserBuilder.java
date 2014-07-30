package com.cynigram.yashiro.parser.statement;

import com.cynigram.yashiro.ast.StmtNode;
import com.cynigram.yashiro.parser.StatementParser;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.functors.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public final class StatementWithoutBodyParserBuilder
{
    private Callables callables = new Callables();
    private Contents contents = new Contents();

    StatementWithoutBodyParserBuilder (String packageName, String name)
    {
        callables.setPackageName(packageName);
        callables.setName(name);
    }

    public StatementWithoutBodyParserBuilder contains (ContentParserBuilder content)
    {
        contents.setContent(content);
        return this;
    }

    public <T extends StmtNode> StatementParser mapWith (final Map<ContentMatchMap, T> map)
    {
        checkNotNull(map);

        return new StatementParser()
        {
            @Override
            public String getPackage()
            {
                return callables.getPackageName();
            }

            @Override
            public String getName ()
            {
                return callables.getName();
            }

            @Override
            public Parser<T> parser (Cont cont)
            {
                ContentParserBuilder content = contents.getContent();
                return cont.name().next(content.parser().map(map));
            }
        };
    }
}
