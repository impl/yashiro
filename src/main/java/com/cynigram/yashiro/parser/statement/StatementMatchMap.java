package com.cynigram.yashiro.parser.statement;

import com.google.common.collect.ListMultimap;

import java.util.Collections;
import java.util.List;

public class StatementMatchMap
{
    private final ListMultimap<String, StatementMatch> matches;
    private final ContentMatchMap terminus;

    public StatementMatchMap (ListMultimap<String, StatementMatch> matches, ContentMatchMap terminus)
    {
        this.matches = matches;
        this.terminus = terminus;
    }

    public List<StatementMatch> all (String name)
    {
        return Collections.unmodifiableList(matches.get(name));
    }

    public StatementMatch one (String name)
    {
        StatementMatch node = oneOrNull(name);
        if (node == null)
            throw new IndexOutOfBoundsException();

        return node;
    }

    public StatementMatch oneOrNull (String name)
    {
        List<StatementMatch> all = all(name);
        if (all.isEmpty())
            return null;

        return all.get(0);
    }

    public boolean has (String name)
    {
        return !all(name).isEmpty();
    }

    public ContentMatchMap terminus ()
    {
        return terminus;
    }
}
