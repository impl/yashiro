package com.cynigram.yashiro.statements;

import com.cynigram.yashiro.ast.BodyListNode;
import com.cynigram.yashiro.ast.ExprNode;
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

public class BlockStatement implements Statement
{
    public static class BlockStmtNode extends StmtNode
    {
        private final ExprNode name;
        private final BodyListNode body;
        private final boolean scoped;

        public BlockStmtNode (IdNode name, BodyListNode body, boolean scoped)
        {
            this.name = name;
            this.body = body;
            this.scoped = scoped;
        }

        public ExprNode getName ()
        {
            return name;
        }

        public BodyListNode getBody ()
        {
            return body;
        }

        public boolean isScoped ()
        {
            return scoped;
        }

        @Override
        public int hashCode ()
        {
            return Objects.hashCode(name, body, scoped);
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

            BlockStmtNode other = (BlockStmtNode)obj;
            return Objects.equal(other.name, name) &&
                    Objects.equal(other.body, body) &&
                    Objects.equal(other.scoped, scoped);
        }

        @Override
        public String toString ()
        {
            return Objects.toStringHelper(this)
                    .add("name", name)
                    .add("body", body)
                    .add("scoped", scoped)
                    .toString();
        }
    }

    public static class BlockStmtNodeMap implements Map<StatementMatchMap, BlockStmtNode>
    {
        @Override
        public BlockStmtNode map (StatementMatchMap statementMatchMap)
        {
            StatementMatch blockMatch = statementMatchMap.one("block");

            // Check name equivalence.
            IdNode name = (IdNode)blockMatch.getContentMatchMap().one("name");
            IdNode terminusName = (IdNode)statementMatchMap.terminus().oneOrNull("name");
            if (terminusName != null && !terminusName.equals(name))
            {
                throw new RuntimeException("Block end tag name '" + terminusName.getName() + "' must match block start tag name '" + name.getName() + "'");
            }

            return new BlockStmtNode(name, blockMatch.getBody(), blockMatch.getContentMatchMap().has("scoped"));
        }
    }

    public StatementParser parser ()
    {
        return ParserBuilders.statementWithBody("com.cynigram.yashiro.statements", "block")
                .contains(ParserBuilders.expr("name", ExpressionParser.name()).select(ParserBuilders.wordWithGroup("scoped", "scoped").optional()))
                .terminating().contains(ParserBuilders.expr("name", ExpressionParser.name()).optional())
                .mapWith(new BlockStmtNodeMap());
    }
}
