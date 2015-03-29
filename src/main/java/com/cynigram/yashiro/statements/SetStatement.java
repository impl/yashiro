package com.cynigram.yashiro.statements;

import com.cynigram.yashiro.ast.*;
import com.cynigram.yashiro.parser.ExpressionParser;
import com.cynigram.yashiro.parser.StatementParser;
import com.cynigram.yashiro.parser.statement.ContentMatchMap;
import com.cynigram.yashiro.parser.statement.ParserBuilders;
import com.cynigram.yashiro.parser.statement.StatementMatch;
import com.cynigram.yashiro.parser.statement.StatementMatchMap;
import com.google.common.base.Objects;
import org.codehaus.jparsec.functors.Map;

public class SetStatement
{
    public static class SetStmtNode extends StmtNode
    {
        private ExprNode names;
        private ExprNode value;

        public SetStmtNode (ExprNode names, ExprNode value)
        {
            this.names = names;
            this.value = value;
        }

        public ExprNode getNames()
        {
            return names;
        }

        public ExprNode getValue()
        {
            return value;
        }

        @Override
        public int hashCode ()
        {
            return Objects.hashCode(names, value);
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

            SetStmtNode other = (SetStmtNode)obj;
            return Objects.equal(other.names, names) && Objects.equal(other.value, value);
        }

        @Override
        public String toString ()
        {
            return Objects.toStringHelper(this)
                    .add("names", names)
                    .add("value", value)
                    .toString();
        }
    }

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

    public static class SetStmtNodeMap implements Map<ContentMatchMap, SetStmtNode>
    {
        @Override
        public SetStmtNode map (ContentMatchMap contentMatchMap)
        {
            return new SetStmtNode(
                    contentMatchMap.one("names"),
                    contentMatchMap.one("expression"));
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

    public StatementParser getParser ()
    {
        return ParserBuilders.statement("com.cynigram.yashiro.statements", "set")
                .contains(
                        ParserBuilders
                                .expr("names", ExpressionParser.anyOf(ExpressionParser.name()))
                                .sym("=").expr("expression", ExpressionParser.any()))
                .mapWith(new SetStmtNodeMap());
    }

    public StatementParser getParserWithBody ()
    {
        return ParserBuilders.statementWithBody("com.cynigram.yashiro.statements", "set")
                .contains(ParserBuilders.expr("name", ExpressionParser.name()))
                .mapWith(new SetStmtWithBodyNodeMap());
    }
}
