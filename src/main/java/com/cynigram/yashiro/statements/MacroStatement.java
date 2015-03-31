package com.cynigram.yashiro.statements;

import com.cynigram.yashiro.ast.*;
import com.cynigram.yashiro.parser.ExpressionParser;
import com.cynigram.yashiro.parser.Parsers2;
import com.cynigram.yashiro.parser.Statement;
import com.cynigram.yashiro.parser.StatementParser;
import com.cynigram.yashiro.parser.statement.ParserBuilders;
import com.cynigram.yashiro.parser.statement.StatementMatch;
import com.cynigram.yashiro.parser.statement.StatementMatchMap;
import com.google.common.base.Objects;
import com.google.common.base.Predicates;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.misc.Mapper;

import java.util.List;

import static com.cynigram.yashiro.parser.TagParsers.term;

public class MacroStatement implements Statement
{
    public static class FuncStmtNode extends StmtNode
    {
        private final IdNode name;
        private final InvArgListNode arguments;
        private final BodyListNode body;

        public FuncStmtNode (IdNode name, InvArgListNode arguments, BodyListNode body)
        {
            this.name = name;
            this.arguments = arguments;
            this.body = body;
        }

        public IdNode getName()
        {
            return name;
        }

        public InvArgListNode getArguments()
        {
            return arguments;
        }

        public BodyListNode getBody()
        {
            return body;
        }

        @Override
        public int hashCode ()
        {
            return Objects.hashCode(name, arguments, body);
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

            FuncStmtNode other = (FuncStmtNode)obj;
            return Objects.equal(other.name, name) &&
                    Objects.equal(other.arguments, arguments) &&
                    Objects.equal(other.body, body);
        }

        @Override
        public String toString ()
        {
            return Objects.toStringHelper(this)
                    .add("name", name)
                    .add("arguments", arguments)
                    .add("body", body)
                    .toString();
        }
    }

    public static class FuncStmtNodeMap implements Map<StatementMatchMap, FuncStmtNode>
    {
        @Override
        public FuncStmtNode map (StatementMatchMap statementMatchMap)
        {
            StatementMatch macro = statementMatchMap.one("macro");

            return new FuncStmtNode(
                    (IdNode)macro.getContentMatchMap().one("name"),
                    (InvArgListNode)macro.getContentMatchMap().one("arguments"),
                    macro.getBody());
        }
    }

    public StatementParser parser ()
    {
        // The syntax for declaring a function is slightly different than the function for calling
        // a function, and we must restrict parameter declarations to simple names (instead of any
        // expression).
        Parser<List<InvArgNode>> positionalParser = Mapper.<InvArgNode>curry(InvArgNode.Positional.class)
                .sequence(ExpressionParser.name().notFollowedBy(term("=")).atomic())
                .sepBy1(term(","));
        Parser<List<InvArgNode>> namedParser = Mapper.<InvArgNode>curry(InvArgNode.Named.class)
                .sequence(ExpressionParser.name(), term("=").next(ExpressionParser.one()))
                .sepBy1(term(","));
        Parser<InvArgNode> manyArgsParser = Mapper.<InvArgNode>curry(InvArgNode.ManyArgs.class)
                .sequence(term("*").next(ExpressionParser.name()));
        Parser<InvArgNode> keywordArgsParser = Mapper.<InvArgNode>curry(InvArgNode.KeywordArgs.class)
                .sequence(term("**").next(ExpressionParser.name()));

        Parser<List<InvArgNode>> argsParser = Parsers2.filter(
                Parsers.or(
                        Parsers2.list(keywordArgsParser),
                        Parsers2.list(manyArgsParser, term(",").next(keywordArgsParser).optional()),
                        Parsers2.collectN(
                                namedParser,
                                term(",").next(manyArgsParser).atomic().optional(),
                                term(",").next(keywordArgsParser).atomic().optional()
                        ),
                        Parsers2.collect(
                                positionalParser,
                                term(",").next(namedParser).atomic().optional(),
                                term(",").next(Parsers2.list(manyArgsParser)).atomic().optional(),
                                term(",").next(Parsers2.list(keywordArgsParser)).atomic().optional()
                        )),
                Predicates.<InvArgNode>notNull());
        Parser<InvArgListNode> argListParser = Mapper.curry(InvArgListNode.class).sequence(argsParser.between(term("("), term(")")));

        return ParserBuilders
                .statementWithBody("com.cynigram.yashiro.statements", "macro").contains(
                        ParserBuilders.expr("name", ExpressionParser.name()).expr("arguments", argListParser))
                .mapWith(new FuncStmtNodeMap());
    }
}
