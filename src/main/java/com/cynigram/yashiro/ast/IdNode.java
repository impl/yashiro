package com.cynigram.yashiro.ast;

import com.google.common.base.Objects;

public final class IdNode extends ExprNode
{
    public String name;

    public IdNode (String name)
    {
        this.name = name;
    }

    public String getName ()
    {
        return name;
    }

    @Override
    public int hashCode ()
    {
        return name.hashCode();
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

        IdNode other = (IdNode)obj;
        return Objects.equal(other.name, name);
    }

    @Override
    public String toString ()
    {
        return Objects.toStringHelper(this)
                .add("name", name)
                .toString();
    }
}
