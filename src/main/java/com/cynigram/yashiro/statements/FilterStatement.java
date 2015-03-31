package com.cynigram.yashiro.statements;

import com.cynigram.yashiro.ast.*;
import com.cynigram.yashiro.parser.ExpressionParser;
import com.cynigram.yashiro.parser.Statement;
import com.cynigram.yashiro.parser.StatementParser;
import com.cynigram.yashiro.parser.statement.ParserBuilders;
import com.cynigram.yashiro.parser.statement.StatementMatch;
import com.cynigram.yashiro.parser.statement.StatementMatchMap;
import com.google.common.base.Objects;
import org.codehaus.jparsec.functors.Map;

public class FilterStatement implements Statement
{
    public static class FilterStmtNode extends StmtNode
    {
        private final ExprNode filter;
        private final BodyListNode body;

        public FilterStmtNode (ExprNode filter, BodyListNode body)
        {
            this.filter = filter;
            this.body = body;
        }

        public ExprNode getFilter ()
        {
            return filter;
        }

        public BodyListNode getBody ()
        {
            return body;
        }

        @Override
        public int hashCode ()
        {
            return Objects.hashCode(filter, body);
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

            FilterStmtNode other = (FilterStmtNode)obj;
            return Objects.equal(other.filter, filter) &&
                    Objects.equal(other.body, body);
        }

        @Override
        public String toString ()
        {
            return Objects.toStringHelper(this)
                    .add("filter", filter)
                    .add("body", body)
                    .toString();
        }
    }

    public static class FilterStmtNodeMap implements Map<StatementMatchMap, FilterStmtNode>
    {
        @Override
        public FilterStmtNode map (StatementMatchMap statementMatchMap)
        {
            StatementMatch filterMatch = statementMatchMap.one("filter");

            return new FilterStmtNode(filterMatch.getContentMatchMap().one("filter"), filterMatch.getBody());
        }
    }

    public StatementParser parser ()
    {
        return ParserBuilders
                .statementWithBody("com.cynigram.yashiro.statements", "filter").contains(ParserBuilders.expr("filter", ExpressionParser.one()))
                .mapWith(new FilterStmtNodeMap());
    }
}
