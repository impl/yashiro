package com.cynigram.yashiro.statements;

import com.cynigram.yashiro.ast.BodyListNode;
import com.cynigram.yashiro.ast.IdNode;
import com.cynigram.yashiro.ast.StmtNode;
import com.cynigram.yashiro.parser.ExpressionParser;
import com.cynigram.yashiro.parser.Statement;
import com.cynigram.yashiro.parser.StatementParser;
import com.cynigram.yashiro.parser.statement.ParserBuilders;
import com.cynigram.yashiro.parser.statement.StatementMatch;
import com.cynigram.yashiro.parser.statement.StatementMatchMap;
import com.google.common.base.Objects;
import org.codehaus.jparsec.functors.Map;

public class SetStatementWithBody implements Statement
{

    public static class SetStmtWithBodyNode extends StmtNode
    {
        private IdNode name;
        private BodyListNode body;

        public SetStmtWithBodyNode (IdNode name, BodyListNode body)
        {
            this.name = name;
            this.body = body;
        }

        public IdNode getName()
        {
            return name;
        }

        public BodyListNode getBody()
        {
            return body;
        }

        @Override
        public int hashCode ()
        {
            return Objects.hashCode(name, body);
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

            SetStmtWithBodyNode other = (SetStmtWithBodyNode)obj;
            return Objects.equal(other.name, name) && Objects.equal(other.body, body);
        }

        @Override
        public String toString ()
        {
            return Objects.toStringHelper(this)
                    .add("name", name)
                    .add("body", body)
                    .toString();
        }
    }

    public static class SetStmtWithBodyNodeMap implements Map<StatementMatchMap, SetStmtWithBodyNode>
    {
        @Override
        public SetStmtWithBodyNode map (StatementMatchMap statementMatchMap)
        {
            StatementMatch set = statementMatchMap.one("set");

            return new SetStmtWithBodyNode(
                    (IdNode)set.getContentMatchMap().one("name"),
                    set.getBody());
        }
    }

    public StatementParser parser ()
    {
        return ParserBuilders.statementWithBody("com.cynigram.yashiro.statements", "set")
                .contains(ParserBuilders.expr("name", ExpressionParser.name()))
                .mapWith(new SetStmtWithBodyNodeMap());
    }
}
