package com.cynigram.yashiro.statements;

import com.cynigram.yashiro.ast.*;
import com.cynigram.yashiro.parser.ExpressionParser;
import com.cynigram.yashiro.parser.Statement;
import com.cynigram.yashiro.parser.StatementParser;
import com.cynigram.yashiro.parser.statement.ContentMatchMap;
import com.cynigram.yashiro.parser.statement.ParserBuilders;
import com.google.common.base.Objects;
import org.codehaus.jparsec.functors.Map;

public class ExtendsStatement implements Statement
{
    public static class ExtendsStmtNode extends StmtNode
    {
        private final ExprNode name;

        public ExtendsStmtNode (ExprNode name)
        {
            this.name = name;
        }

        public ExprNode getName ()
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

            ExtendsStmtNode other = (ExtendsStmtNode)obj;
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

    public static class ExtendsStmtNodeMap implements Map<ContentMatchMap, ExtendsStmtNode>
    {
        @Override
        public ExtendsStmtNode map (ContentMatchMap contentMatchMap)
        {
            return new ExtendsStmtNode(contentMatchMap.one("name"));
        }
    }

    public StatementParser parser ()
    {
        return ParserBuilders.statement("com.cynigram.yashiro.statements", "extends")
                .contains(ParserBuilders.expr("name", ExpressionParser.one()))
                .mapWith(new ExtendsStmtNodeMap());
    }
}
