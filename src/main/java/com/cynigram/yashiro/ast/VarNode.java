package com.cynigram.yashiro.ast;

import com.google.common.base.Objects;

public class VarNode extends BlkNode
{
    private final ExprNode expression;

    public VarNode (ExprNode expression)
    {
        this.expression = expression;
    }

    public ExprNode getExpression ()
    {
        return expression;
    }

    @Override
    public int hashCode ()
    {
        return expression.hashCode();
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

        VarNode other = (VarNode)obj;
        return Objects.equal(other.expression, this.expression);
    }

    @Override
    public String toString ()
    {
        return Objects.toStringHelper(this)
                .add("expression", expression)
                .toString();
    }
}
