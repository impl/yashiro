package com.cynigram.yashiro.ast;

import com.google.common.base.Objects;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

public class LitNode<T> extends ExprNode
{
    public static final class Null extends LitNode<Void>
    {
        public static final Null NULL = new Null();

        private Null ()
        {
            super(null);
        }
    }

    public static final class Bool extends LitNode<Boolean>
    {
        public static final Bool TRUE = new Bool(true);
        public static final Bool FALSE = new Bool(false);

        private Bool (boolean value)
        {
            super(value);
        }
    }

    public static final class Dec extends LitNode<BigDecimal>
    {
        public Dec (String value)
        {
            super(new BigDecimal(value));
        }
    }

    public static final class Int extends LitNode<Long>
    {
        public Int (long value)
        {
            super(value);
        }
    }

    public static final class Str extends LitNode<String>
    {
        public Str (String value)
        {
            super(checkNotNull(value));
        }
    }

    private T value;

    protected LitNode (T value)
    {
        this.value = value;
    }

    public T getValue ()
    {
        return this.value;
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
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

        LitNode<?> other = (LitNode<?>)obj;
        return Objects.equal(other.value, value);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("value", value)
                .toString();
    }
}
