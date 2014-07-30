package com.cynigram.yashiro.ast;

import com.google.common.base.Objects;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;

public class ConsNode<T> extends ExprNode
{
    public static final class Tuple extends ConsNode<ExprNode>
    {
        public Tuple (java.util.List<? extends ExprNode> elements)
        {
            super(elements);
        }
    }

    public static final class List extends ConsNode<ExprNode>
    {
        public List (java.util.List<? extends ExprNode> elements)
        {
            super(elements);
        }
    }

    public static final class Dict extends ConsNode<Pair<? extends ExprNode, ? extends ExprNode>>
    {
        public Dict (java.util.List<Pair<? extends ExprNode, ? extends ExprNode>> elements)
        {
            super(elements);
        }
    }

    public static final class Set extends ConsNode<ExprNode>
    {
        public Set (java.util.List<? extends ExprNode> elements)
        {
            super(elements);
        }
    }

    private java.util.List<T> elements;

    protected ConsNode (java.util.List<? extends T> elements)
    {
        this.elements = Collections.unmodifiableList(elements);
    }

    public java.util.List<? extends T> getElements ()
    {
        return this.elements;
    }

    @Override
    public int hashCode()
    {
        return elements.hashCode();
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

        ConsNode<?> other = (ConsNode<?>)obj;
        return Objects.equal(other.elements, elements);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("elements", elements)
                .toString();
    }
}
