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

public class IncludeStatement implements Statement
{
    public static class IncludeStmtNode extends StmtNode
    {
        private final ExprNode source;
        private final boolean ignoreMissing;
        private final boolean withoutContext;

        public IncludeStmtNode (ExprNode source, boolean ignoreMissing, boolean withoutContext)
        {
            this.source = source;
            this.ignoreMissing = ignoreMissing;
            this.withoutContext = withoutContext;
        }

        public ExprNode getSource ()
        {
            return source;
        }

        public boolean isIgnoreMissing ()
        {
            return ignoreMissing;
        }

        public boolean isWithoutContext ()
        {
            return withoutContext;
        }

        @Override
        public int hashCode ()
        {
            return Objects.hashCode(source, ignoreMissing, withoutContext);
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

            IncludeStmtNode other = (IncludeStmtNode)obj;
            return Objects.equal(other.source, source) &&
                    Objects.equal(other.ignoreMissing, ignoreMissing) &&
                    Objects.equal(other.withoutContext, withoutContext);
        }

        @Override
        public String toString ()
        {
            return Objects.toStringHelper(this)
                    .add("source", source)
                    .add("ignoreMissing", ignoreMissing)
                    .add("withoutContext", withoutContext)
                    .toString();
        }
    }

    public static class IncludeStmtNodeMap implements Map<ContentMatchMap, IncludeStmtNode>
    {
        @Override
        public IncludeStmtNode map (ContentMatchMap contentMatchMap)
        {
            return new IncludeStmtNode(contentMatchMap.one("source"), contentMatchMap.has("ignore_missing"), contentMatchMap.has("without_context"));
        }
    }

    public StatementParser parser ()
    {
        return ParserBuilders.statement("com.cynigram.yashiro.statements", "include")
                .contains(
                        ParserBuilders
                                .expr("source", ExpressionParser.one())
                                .select(ParserBuilders.wordWithGroup("ignore_missing", "ignore", "missing").optional())
                                .select(
                                        ParserBuilders.word("with", "context").optional(),
                                        ParserBuilders.wordWithGroup("without_context", "without", "context").optional()))
                .mapWith(new IncludeStmtNodeMap());
    }
}
