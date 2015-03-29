package com.cynigram.yashiro.statements;

import com.cynigram.yashiro.ast.*;
import com.cynigram.yashiro.parser.StatementParser;
import com.cynigram.yashiro.parser.statement.ParserBuilders;
import com.cynigram.yashiro.parser.statement.StatementMatch;
import com.cynigram.yashiro.parser.statement.StatementMatchMap;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.codehaus.jparsec.functors.Map;

import java.util.List;

public class IfStatement
{
    public static class CondNode extends Node
    {
        private final ExprNode condition;
        private final BodyListNode body;

        CondNode (ExprNode condition, BodyListNode body)
        {
            this.condition = condition;
            this.body = body;
        }

        public ExprNode getCondition ()
        {
            return this.condition;
        }

        public BodyListNode getBody ()
        {
            return this.body;
        }

        @Override
        public int hashCode ()
        {
            return Objects.hashCode(condition, body);
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

            CondNode other = (CondNode)obj;
            return Objects.equal(other.condition, condition) && Objects.equal(other.body, body);
        }

        @Override
        public String toString ()
        {
            return Objects.toStringHelper(this)
                    .add("condition", condition)
                    .add("body", body)
                    .toString();
        }
    }

    public static class IfStmtNode extends StmtNode
    {
        private final List<CondNode> conditions;

        IfStmtNode (List<CondNode> conditions)
        {
            this.conditions = conditions;
        }

        public List<CondNode> getConditions ()
        {
            return this.conditions;
        }

        @Override
        public int hashCode ()
        {
            return conditions.hashCode();
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

            IfStmtNode other = (IfStmtNode)obj;
            return Objects.equal(other.conditions, conditions);
        }

        @Override
        public String toString ()
        {
            return Objects.toStringHelper(this)
                    .add("conditions", conditions)
                    .toString();
        }
    }

    protected static class IfStmtNodeMap implements Map<StatementMatchMap, IfStmtNode>
    {
        @Override
        public IfStmtNode map (StatementMatchMap statementMatchMap)
        {
            List<CondNode> conditions = Lists.newLinkedList();

            StatementMatch ifMatch = statementMatchMap.one("if");
            conditions.add(new CondNode(ifMatch.getContentMatchMap().one("cond"), ifMatch.getBody()));

            for (StatementMatch elseIfMatch : statementMatchMap.all("elif"))
                conditions.add(new CondNode(elseIfMatch.getContentMatchMap().one("cond"), elseIfMatch.getBody()));

            StatementMatch elseMatch = statementMatchMap.oneOrNull("else");
            if (elseMatch != null)
                conditions.add(new CondNode(null, elseMatch.getBody()));

            return new IfStmtNode(conditions);
        }
    }

    public StatementParser getParser ()
    {
        return ParserBuilders
                .statementWithBody("com.cynigram.yashiro.statements", "if").contains(ParserBuilders.expr("cond"))
                .also("elif").contains(ParserBuilders.expr("cond")).many().optional()
                .also("else").optional()
                .mapWith(new IfStmtNodeMap());
    }
}
