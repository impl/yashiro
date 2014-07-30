package com.cynigram.yashiro.parser.statement;

import com.cynigram.yashiro.ast.StmtNode;
import com.cynigram.yashiro.parser.StatementParser;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.codehaus.jparsec.functors.Map;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class RepeatableStatementWithBodyParserBuilder
{
    private Repeatables repeatables = new Repeatables();
    private Callables callables = new Callables();
    private Contents contents = new Contents();

    private final StatementWithBodyParserBuilder parent;
    private final List<RepeatableStatementWithBodyParserBuilder> subordinates;

    RepeatableStatementWithBodyParserBuilder (String name, StatementWithBodyParserBuilder parent, List<RepeatableStatementWithBodyParserBuilder> subordinates)
    {
        callables.setPackageName(parent.getCallables().getPackageName());
        callables.setName(name);
        checkArgument(!name.equals("end" + parent.getCallables().getName()), "name cannot be '" + name + "'");
        checkArgument(!Lists.transform(subordinates,
                new Function<RepeatableStatementWithBodyParserBuilder, String>()
                {
                    @Override
                    public String apply (RepeatableStatementWithBodyParserBuilder input)
                    {
                        return input.getCallables().getName();
                    }
                }).contains(name), "statement with name '" + name + "' already exists in this builder");

        this.parent = parent;
        this.subordinates = Lists.newArrayList(checkNotNull(subordinates));
        this.subordinates.add(this);
    }

    RepeatableStatementWithBodyParserBuilder (String name, StatementWithBodyParserBuilder parent)
    {
        this(name, parent, Lists.<RepeatableStatementWithBodyParserBuilder>newLinkedList());
    }

    public RepeatableStatementWithBodyParserBuilder contains (ContentParserBuilder content)
    {
        contents.setContent(content);
        return this;
    }

    public RepeatableStatementWithBodyParserBuilder many ()
    {
        repeatables.setMany(true);
        return this;
    }

    public RepeatableStatementWithBodyParserBuilder optional ()
    {
        repeatables.setOptional(true);
        return this;
    }

    public RepeatableStatementWithBodyParserBuilder also (String name)
    {
        return new RepeatableStatementWithBodyParserBuilder(name, parent, subordinates);
    }

    public StatementTerminusParserBuilder terminating ()
    {
        return new StatementTerminusParserBuilder(parent, subordinates);
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

    Repeatables getRepeatables()
    {
        return repeatables;
    }
}
