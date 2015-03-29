package com.cynigram.yashiro.statements;

import com.cynigram.yashiro.ast.BodyListNode;
import com.cynigram.yashiro.ast.ExprNode;
import com.cynigram.yashiro.ast.StmtNode;
import com.cynigram.yashiro.parser.ExpressionParser;
import com.cynigram.yashiro.parser.StatementParser;
import com.cynigram.yashiro.parser.statement.ParserBuilders;
import com.cynigram.yashiro.parser.statement.StatementMatch;
import com.cynigram.yashiro.parser.statement.StatementMatchMap;
import com.google.common.base.Objects;
import org.codehaus.jparsec.functors.Map;

public class ForStatement
{
    public static class ForStmtNode extends StmtNode
    {
        private final ExprNode item;
        private final ExprNode sequence;
        private final BodyListNode forBody;
        private final BodyListNode elseBody;
        private final boolean recursive;

        public ForStmtNode (ExprNode item, ExprNode sequence, BodyListNode forBody, BodyListNode elseBody, boolean recursive)
        {
            this.item = item;
            this.sequence = sequence;
            this.forBody = forBody;
            this.elseBody = elseBody;
            this.recursive = recursive;
        }

        public ExprNode getItem ()
        {
            return item;
        }

        public ExprNode getSequence ()
        {
            return sequence;
        }

        public BodyListNode getForBody ()
        {
            return forBody;
        }

        public BodyListNode getElseBody ()
        {
            return elseBody;
        }

        public boolean isRecursive ()
        {
            return recursive;
        }

        @Override
        public int hashCode ()
        {
            return Objects.hashCode(item, sequence, forBody, elseBody, recursive);
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

            ForStmtNode other = (ForStmtNode)obj;
            return Objects.equal(other.item, item) &&
                    Objects.equal(other.sequence, sequence) &&
                    Objects.equal(other.forBody, forBody) &&
                    Objects.equal(other.elseBody, elseBody) &&
                    Objects.equal(other.recursive, recursive);
        }

        @Override
        public String toString ()
        {
            return Objects.toStringHelper(this)
                    .add("item", item)
                    .add("sequence", sequence)
                    .add("forBody", forBody)
                    .add("elseBody", elseBody)
                    .add("recursive", recursive)
                    .toString();
        }
    }

    protected static class ForStmtNodeMap implements Map<StatementMatchMap, ForStmtNode>
    {
        @Override
        public ForStmtNode map (StatementMatchMap statementMatchMap)
        {
            StatementMatch forMatch = statementMatchMap.one("for");
            StatementMatch elseMatch = statementMatchMap.oneOrNull("else");

            return new ForStmtNode(
                    forMatch.getContentMatchMap().one("item"),
                    forMatch.getContentMatchMap().one("sequence"),
                    forMatch.getBody(),
                    elseMatch != null ? elseMatch.getBody() : null,
                    forMatch.getContentMatchMap().has("recursive"));
        }
    }

    public StatementParser getParser ()
    {
        return ParserBuilders
                .statementWithBody("com.cynigram.yashiro.statements", "for")
                .contains(ParserBuilders
                        .expr("item", ExpressionParser.anyOf(ExpressionParser.name()))
                        .word("in").expr("sequence")
                        .select(ParserBuilders.wordWithGroup("recursive", "recursive").optional()))
                .also("else").optional()
                .mapWith(new ForStmtNodeMap());
    }
}
