package com.cynigram.yashiro.ast;

import com.google.common.base.Objects;

import java.util.List;

public class InvArgListNode extends ExprNode
{
    private final List<? extends InvArgNode> children;

    public InvArgListNode (List<? extends InvArgNode> children)
    {
        this.children = children;
    }

    public List<? extends InvArgNode> getChildren ()
    {
        return children;
    }

    @Override
    public int hashCode()
    {
        return children.hashCode();
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

        InvArgListNode other = (InvArgListNode)obj;
        return Objects.equal(other.children, children);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("children", children)
                .toString();
    }
}
