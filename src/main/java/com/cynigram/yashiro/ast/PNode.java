package com.cynigram.yashiro.ast;

import com.google.common.base.Objects;

public final class PNode extends ExprNode
{
    private final ExprNode child;

    public PNode (ExprNode child)
    {
        this.child = child;
    }

    public ExprNode getChild ()
    {
        return child;
    }

    @Override
    public int hashCode ()
    {
        return child.hashCode();
    }

    @Override
    public boolean equals (Object obj)
    {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (obj.getClass() != getClass()) {
            return false;
        }

        PNode other = (PNode)obj;
        return Objects.equal(other.child, child);
    }

    @Override
    public String toString ()
    {
        return Objects.toStringHelper(this)
                .add("child", child)
                .toString();
    }
}
