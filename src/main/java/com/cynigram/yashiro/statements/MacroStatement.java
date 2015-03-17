package com.cynigram.yashiro.statements;

import com.cynigram.yashiro.ast.BodyListNode;
import com.cynigram.yashiro.ast.IdNode;
import com.cynigram.yashiro.ast.InvArgListNode;
import com.cynigram.yashiro.ast.StmtNode;
import com.cynigram.yashiro.parser.ExpressionParser;
import com.cynigram.yashiro.parser.StatementParser;
import com.cynigram.yashiro.parser.statement.ParserBuilders;
import com.cynigram.yashiro.parser.statement.StatementMatch;
import com.cynigram.yashiro.parser.statement.StatementMatchMap;
import com.google.common.base.Objects;
import org.codehaus.jparsec.functors.Map;

public class MacroStatement
{
    public static class FuncStmtNode extends StmtNode
    {
        private IdNode name;
        private InvArgListNode arguments;
        private BodyListNode body;

        public FuncStmtNode (IdNode name, InvArgListNode arguments, BodyListNode body)
        {
            this.name = name;
            this.arguments = arguments;
            this.body = body;
        }

        public IdNode getName()
        {
            return name;
        }

        public InvArgListNode getArguments()
        {
            return arguments;
        }

        public BodyListNode getBody()
        {
            return body;
        }

        @Override
        public int hashCode ()
        {
            return Objects.hashCode(name, arguments, body);
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

            FuncStmtNode other = (FuncStmtNode)obj;
            return Objects.equal(other.name, name) &&
                    Objects.equal(other.arguments, arguments) &&
                    Objects.equal(other.body, body);
        }

        @Override
        public String toString ()
        {
            return Objects.toStringHelper(this)
                    .add("name", name)
                    .add("arguments", arguments)
                    .add("body", body)
                    .toString();
        }
    }

    public static class FuncStmtNodeMap implements Map<StatementMatchMap, FuncStmtNode>
    {
        @Override
        public FuncStmtNode map (StatementMatchMap statementMatchMap)
        {
            StatementMatch macro = statementMatchMap.one("macro");

            return new FuncStmtNode(
                    (IdNode)macro.getContentMatchMap().one("name"),
                    (InvArgListNode)macro.getContentMatchMap().one("arguments"),
                    macro.getBody());
        }
    }

    public StatementParser getParser ()
    {
        return ParserBuilders
                .statementWithBody("com.cynigram.yashiro.statement", "macro").contains(
                        ParserBuilders.expr("name", ExpressionParser.name()).callable("arguments"))
                .mapWith(new FuncStmtNodeMap());
    }
}
