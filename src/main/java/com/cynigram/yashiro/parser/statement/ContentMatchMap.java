package com.cynigram.yashiro.parser.statement;

import com.cynigram.yashiro.ast.ExprNode;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import java.util.List;

public final class ContentMatchMap
{
    private static final ListMultimap<String, ExprNode> EMPTY_MULTIMAP = ImmutableListMultimap.<String, ExprNode>builder().build();

    private final ListMultimap<String, ExprNode> matches;

    ContentMatchMap(ListMultimap<String, ExprNode> matches)
    {
        this.matches = matches != null
                ? Multimaps.unmodifiableListMultimap(matches)
                : EMPTY_MULTIMAP;
    }

    public ListMultimap<String, ExprNode> all ()
    {
        return matches;
    }

    public List<ExprNode> all (String name)
    {
        return matches.get(name);
    }

    public ExprNode one (String name)
    {
        ExprNode node = oneOrNull(name);
        if (node == null)
            throw new IndexOutOfBoundsException();

        return node;
    }

    public ExprNode oneOrNull (String name)
    {
        List<ExprNode> all = all(name);
        if (all.isEmpty())
            return null;

        return all.get(0);
    }

    public boolean has (String name)
    {
        return !all(name).isEmpty();
    }
}
