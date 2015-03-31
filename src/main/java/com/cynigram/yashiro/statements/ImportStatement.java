package com.cynigram.yashiro.statements;

import com.cynigram.yashiro.ast.ExprNode;
import com.cynigram.yashiro.ast.IdNode;
import com.cynigram.yashiro.ast.StmtNode;
import com.cynigram.yashiro.parser.ExpressionParser;
import com.cynigram.yashiro.parser.Statement;
import com.cynigram.yashiro.parser.StatementParser;
import com.cynigram.yashiro.parser.statement.ContentMatchMap;
import com.cynigram.yashiro.parser.statement.ParserBuilders;
import com.google.common.base.Objects;
import org.codehaus.jparsec.functors.Map;

public class ImportStatement implements Statement
{
    public static class ImportStmtNode extends StmtNode
    {
        private final ExprNode source;
        private final IdNode name;
        private final boolean withoutContext;

        public ImportStmtNode (ExprNode source, IdNode name, boolean withoutContext)
        {
            this.source = source;
            this.name = name;
            this.withoutContext = withoutContext;
        }

        public ExprNode getSource ()
        {
            return source;
        }

        public IdNode getName ()
        {
            return name;
        }

        public boolean isWithoutContext ()
        {
            return withoutContext;
        }

        @Override
        public int hashCode ()
        {
            return Objects.hashCode(source, name, withoutContext);
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

            ImportStmtNode other = (ImportStmtNode)obj;
            return Objects.equal(other.source, source) &&
                    Objects.equal(other.name, name) &&
                    Objects.equal(other.withoutContext, withoutContext);
        }

        @Override
        public String toString ()
        {
            return Objects.toStringHelper(this)
                    .add("source", source)
                    .add("name", name)
                    .add("withoutContext", withoutContext)
                    .toString();
        }
    }

    public static class ImportStmtNodeMap implements Map<ContentMatchMap, ImportStmtNode>
    {
        @Override
        public ImportStmtNode map (ContentMatchMap contentMatchMap)
        {
            return new ImportStmtNode(
                    contentMatchMap.one("source"),
                    (IdNode)contentMatchMap.one("name"),
                    contentMatchMap.has("without_context"));
        }
    }

    public StatementParser parser ()
    {
        return ParserBuilders
                .statement("com.cynigram.yashiro.statements", "import")
                .contains(
                        ParserBuilders
                                .expr("source", ExpressionParser.one()).word("as").expr("name", ExpressionParser.name())
                                .select(
                                        ParserBuilders.word("with", "context").optional(),
                                        ParserBuilders.wordWithGroup("without_context", "without", "context").optional()))
                .mapWith(new ImportStmtNodeMap());
    }
}
