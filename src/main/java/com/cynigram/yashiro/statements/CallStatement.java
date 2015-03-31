package com.cynigram.yashiro.statements;

import com.cynigram.yashiro.ast.BodyListNode;
import com.cynigram.yashiro.ast.IdNode;
import com.cynigram.yashiro.ast.InvArgListNode;
import com.cynigram.yashiro.ast.StmtNode;
import com.cynigram.yashiro.parser.ExpressionParser;
import com.cynigram.yashiro.parser.Statement;
import com.cynigram.yashiro.parser.StatementParser;
import com.cynigram.yashiro.parser.statement.ParserBuilders;
import com.cynigram.yashiro.parser.statement.StatementMatch;
import com.cynigram.yashiro.parser.statement.StatementMatchMap;
import com.google.common.base.Objects;
import org.codehaus.jparsec.functors.Map;

public class CallStatement implements Statement
{
    public static class InvStmtNode extends StmtNode
    {
        private final InvArgListNode callerArguments;
        private final IdNode callable;
        private final InvArgListNode callableArguments;
        private final BodyListNode body;

        public InvStmtNode (InvArgListNode callerArguments, IdNode callable, InvArgListNode callableArguments, BodyListNode body)
        {
            this.callerArguments = callerArguments;
            this.callable = callable;
            this.callableArguments = callableArguments;
            this.body = body;
        }

        public InvArgListNode getCallerArguments ()
        {
            return callerArguments;
        }

        public IdNode getCallable ()
        {
            return callable;
        }

        public InvArgListNode getCallableArguments ()
        {
            return callableArguments;
        }

        public BodyListNode getBody ()
        {
            return body;
        }

        @Override
        public int hashCode ()
        {
            return Objects.hashCode(callerArguments, callable, callableArguments, body);
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

            InvStmtNode other = (InvStmtNode)obj;
            return Objects.equal(other.callerArguments, callerArguments) &&
                    Objects.equal(other.callable, callable) &&
                    Objects.equal(other.callableArguments, callableArguments) &&
                    Objects.equal(other.body, body);
        }

        @Override
        public String toString ()
        {
            return Objects.toStringHelper(this)
                    .add("callerArguments", callerArguments)
                    .add("callable", callable)
                    .add("callableArguments", callableArguments)
                    .add("body", body)
                    .toString();
        }
    }

    public static class InvStmtNodeMap implements Map<StatementMatchMap, InvStmtNode>
    {
        @Override
        public InvStmtNode map (StatementMatchMap statementMatchMap)
        {
            StatementMatch callMatch = statementMatchMap.one("call");

            return new InvStmtNode(
                    (InvArgListNode)callMatch.getContentMatchMap().oneOrNull("caller-arguments"),
                    (IdNode)callMatch.getContentMatchMap().one("callable"),
                    (InvArgListNode)callMatch.getContentMatchMap().one("callable-arguments"),
                    callMatch.getBody());
        }
    }

    public StatementParser parser ()
    {
        return ParserBuilders
                .statementWithBody("com.cynigram.yashiro.statements", "call").contains(
                        ParserBuilders
                                .select(ParserBuilders.callable("caller-arguments").optional())
                                .expr("callable", ExpressionParser.name()).callable("callable-arguments"))
                .mapWith(new InvStmtNodeMap());
    }
}
