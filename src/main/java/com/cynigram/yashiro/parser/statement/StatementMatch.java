package com.cynigram.yashiro.parser.statement;

import com.cynigram.yashiro.ast.BodyListNode;

public class StatementMatch
{
    private final ContentMatchMap contentMatchMap;
    private final BodyListNode body;

    public StatementMatch (ContentMatchMap contentMatchMap, BodyListNode body)
    {
        this.contentMatchMap = contentMatchMap;
        this.body = body;
    }

    public ContentMatchMap getContentMatchMap()
    {
        return contentMatchMap;
    }

    public BodyListNode getBody()
    {
        return body;
    }
}
