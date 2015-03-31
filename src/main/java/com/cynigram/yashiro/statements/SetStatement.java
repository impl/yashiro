package com.cynigram.yashiro.statements;

import com.cynigram.yashiro.ast.ExprNode;
import com.cynigram.yashiro.ast.StmtNode;
import com.cynigram.yashiro.parser.ExpressionParser;
import com.cynigram.yashiro.parser.Statement;
import com.cynigram.yashiro.parser.StatementParser;
import com.cynigram.yashiro.parser.statement.ContentMatchMap;
import com.cynigram.yashiro.parser.statement.ParserBuilders;
import com.google.common.base.Objects;
import org.codehaus.jparsec.functors.Map;

public class SetStatement implements Statement
{
    public static class SetStmtNode extends StmtNode
    {
        private final ExprNode names;
        private final ExprNode value;

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

    public StatementParser parser ()
    {
        return ParserBuilders.statement("com.cynigram.yashiro.statements", "set")
                .contains(
                        ParserBuilders
                                .expr("names", ExpressionParser.anyOf(ExpressionParser.name()))
                                .sym("=").expr("expression", ExpressionParser.any()))
                .mapWith(new SetStmtNodeMap());
    }
}
