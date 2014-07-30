package com.cynigram.yashiro.parser.statement;

import static com.google.common.base.Preconditions.checkNotNull;

class Contents
{
    private ContentParserBuilder content;

    public ContentParserBuilder getContent ()
    {
        return content != null
                ? content
                : new ContentParserBuilder();
    }

    public void setContent (ContentParserBuilder content)
    {
        this.content = checkNotNull(content);
    }
}
