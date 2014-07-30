package com.cynigram.yashiro.ast;

import com.google.common.base.Objects;

public class DataNode extends BodyNode
{
    private final String content;

    public DataNode (String content)
    {
        this.content = content;
    }

    public String getContent ()
    {
        return content;
    }

    @Override
    public int hashCode()
    {
        return content.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (obj.getClass() != getClass()) {
            return false;
        }

        DataNode other = (DataNode)obj;
        return Objects.equal(other.content, content);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("content", content)
                .toString();
    }
}
