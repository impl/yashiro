package com.cynigram.yashiro.ast;

import com.google.common.base.Objects;

public final class InvNode extends ExprNode
{
    private final ExprNode callable;
    private final InvArgListNode arguments;

    public InvNode (ExprNode callable, InvArgListNode arguments)
    {
        this.callable = callable;
        this.arguments = arguments;
    }

    public ExprNode getCallable ()
    {
        return callable;
    }

    public InvArgListNode getArguments ()
    {
        return arguments;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(callable, arguments);
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

        InvNode other = (InvNode)obj;
        return Objects.equal(other.callable, callable) && Objects.equal(other.arguments, arguments);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("callable", callable)
                .add("arguments", arguments)
                .toString();
    }
}
