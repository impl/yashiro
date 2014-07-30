package com.cynigram.yashiro.ast;

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class InvArgNode extends ExprNode
{
    public static final class Named extends InvArgNode
    {
        private final IdNode name;

        public Named (IdNode name, ExprNode value)
        {
            super(value);
            this.name = name;
        }

        public IdNode getName ()
        {
            return name;
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(name, getValue());
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

            Named other = (Named)obj;
            return Objects.equal(other.name, name) && Objects.equal(other.getValue(), getValue());
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this)
                    .add("name", name)
                    .add("value", getValue())
                    .toString();
        }
    }

    public static final class Positional extends InvArgNode
    {
        public Positional (ExprNode value)
        {
            super(value);
        }
    }

    public static final class ManyArgs extends InvArgNode
    {
        public ManyArgs (ExprNode value)
        {
            super(value);
        }
    }

    public static final class KeywordArgs extends InvArgNode
    {
        public KeywordArgs (ExprNode value)
        {
            super(value);
        }
    }

    private final ExprNode value;

    protected InvArgNode (ExprNode value)
    {
        this.value = checkNotNull(value);
    }

    public ExprNode getValue ()
    {
        return value;
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

        InvArgNode other = (InvArgNode)obj;
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
