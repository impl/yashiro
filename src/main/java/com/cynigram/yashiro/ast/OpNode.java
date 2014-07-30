package com.cynigram.yashiro.ast;

import com.google.common.base.Objects;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OpNode extends ExprNode
{
    /* Wow much curry. */
    public static final class Unary extends OpNode
    {
        public Unary (String operator, ExprNode operand)
        {
            super(operator, operand);
        }
    }

    public static final class Binary extends OpNode
    {
        public Binary (String operator, ExprNode operand1, ExprNode operand2)
        {
            super(operator, operand1, operand2);
        }
    }

    public static final class Trinary extends OpNode
    {
        public Trinary (String operator, ExprNode operand1, ExprNode operand2, ExprNode operand3)
        {
            super(operator, operand1, operand2, operand3);
        }
    }

    private final String operator;
    private final List<ExprNode> operands;

    public OpNode (String operator, List<ExprNode> operands)
    {
        this.operator = operator;
        this.operands = Collections.unmodifiableList(operands);
    }

    public OpNode (String operator, ExprNode... operands)
    {
        this(operator, Arrays.asList(operands));
    }

    public String getOperator ()
    {
        return operator;
    }

    public List<ExprNode> getOperands ()
    {
        return operands;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(operator, operands);
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

        OpNode other = (OpNode)obj;
        return Objects.equal(other.operator, operator) && Objects.equal(other.operands, operands);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("operator", operator)
                .add("operands", operands)
                .toString();
    }
}
