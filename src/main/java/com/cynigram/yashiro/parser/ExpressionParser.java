package com.cynigram.yashiro.parser;

import com.cynigram.yashiro.ast.*;
import com.google.common.base.Predicates;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jparsec.OperatorTable;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.functors.Binary;
import org.codehaus.jparsec.functors.Unary;
import org.codehaus.jparsec.misc.Mapper;

import java.util.Collections;
import java.util.List;

import static com.cynigram.yashiro.parser.TemplateTerminals.id;
import static com.cynigram.yashiro.parser.TemplateTerminals.term;

public final class ExpressionParser
{
    static Parser<LitNode.Bool> BOOLEAN_PARSER = Parsers.or(id("true").retn(LitNode.Bool.TRUE), id("false").retn(LitNode.Bool.FALSE));
    static Parser<LitNode.Null> NULL_PARSER = id("none").retn(LitNode.Null.NULL);
    static Parser<LitNode.Dec> DECIMAL_PARSER = Mapper.curry(LitNode.Dec.class).sequence(Terminals.DecimalLiteral.PARSER);
    static Parser<LitNode.Int> INTEGER_PARSER = Mapper.curry(LitNode.Int.class).sequence(Terminals.LongLiteral.PARSER);
    static Parser<IdNode> NAME_PARSER = Mapper.curry(IdNode.class).sequence(Terminals.Identifier.PARSER);
    static Parser<LitNode.Str> STRING_PARSER = Mapper.curry(LitNode.Str.class).sequence(Parsers.tokenType(String.class, "string"));

    static Parser<Binary<ExprNode>> binary (String operator, Parser<?> token)
    {
        return token.next(Mapper.<ExprNode> curry(OpNode.Binary.class, operator).binary());
    }

    static Parser<Unary<ExprNode>> unary (String operator, Parser<?> token)
    {
        return token.next(Mapper.<ExprNode>curry(OpNode.Unary.class, operator).unary());
    }

    static <T extends ExprNode> Parser<Binary<ExprNode>> ifElse (Parser<T> e)
    {
        return Mapper
                .<ExprNode>curry(OpNode.Trinary.class, "if").infix(id("if"), e, id("else"))
                .or(binary("if", id("if")));
    }

    static <T extends ExprNode> Parser<ExprNode> paren (Parser<T> e)
    {
        return new Mapper<ExprNode>() {
            @SuppressWarnings("unused")
            public ExprNode map (T node)
            {
                ExprNode r = node;

                if (r == null) {
                    r = new ConsNode.Tuple(Collections.<ExprNode>emptyList());
                } else if (!(r instanceof ConsNode.Tuple) && !(r instanceof PNode)) {
                    r = new PNode(node);
                }

                return r;
            }
        }.sequence(any(e).optional().between(term("("), term(")")));
    }

    static <T extends ExprNode> Parser<ConsNode.List> list (Parser<T> e)
    {
        return Mapper.curry(ConsNode.List.class).sequence(e.sepEndBy(term(",")).between(term("["), term("]")));
    }

    static <T extends ExprNode> Parser<ConsNode.Dict> dict (Parser<T> e)
    {
        Parser<Pair<T, T>> entry = Parsers2.pair(e.followedBy(term(":")), e);
        return Mapper.curry(ConsNode.Dict.class).sequence(entry.sepEndBy(term(",")).between(term("{"), term("}")));
    }

    static <T extends ExprNode> Parser<ConsNode.Set> set (Parser<T> e)
    {
        return Mapper.curry(ConsNode.Set.class).sequence(e.sepEndBy(term(",")).between(term("{"), term("}")));
    }

    static Parser<Unary<ExprNode>> qualified ()
    {
        return Mapper.<ExprNode>curry(OpNode.Binary.class, ".").postfix(term("."), NAME_PARSER);
    }

    static <T extends ExprNode> Parser<InvArgListNode> argumentList (Parser<T> e)
    {
        Parser<InvArgNode> namedArg = Mapper.<InvArgNode>curry(InvArgNode.Named.class).sequence(NAME_PARSER, term("="), e);
        Parser<InvArgNode> arg = namedArg.or(Mapper.curry(InvArgNode.Positional.class).sequence(e));

        Parser<InvArgNode> manyArgs = Mapper.<InvArgNode>curry(InvArgNode.ManyArgs.class).sequence(term("*"), e);
        Parser<InvArgNode> keywordArgs = Mapper.<InvArgNode>curry(InvArgNode.KeywordArgs.class).sequence(term("**"), e);

        /* Gleefully stolen from the Python grammar file. */
        Parser<List<InvArgNode>> parser;
        parser = Parsers2.collect(
                arg.sepEndBy(term(",")),
                Parsers.or(
                        Parsers2.collectN1(manyArgs, term(",").next(namedArg.sepBy1(term(","))).atomic().optional(), term(",").next(keywordArgs).optional()),
                        Parsers2.list(keywordArgs),
                        Parsers2.list(arg.followedBy(term(",")).optional())));
        parser = Parsers2.filter(parser, Predicates.<InvArgNode>notNull());

        return Mapper.curry(InvArgListNode.class).sequence(parser);
    }

    static <T extends ExprNode> Parser<Unary<ExprNode>> invocationWithParens (Parser<T> e)
    {
        return Mapper.<ExprNode>curry(InvNode.class).postfix(argumentList(e).between(term("("), term(")")));
    }

    static Parser<ExprNode> any (Parser<? extends ExprNode> parser)
    {
        return new Mapper<ExprNode>() {
            @SuppressWarnings("unused")
            public ExprNode map (List<? extends ExprNode> expressions, boolean force)
            {
                if (expressions.size() == 1 && !force) {
                    return expressions.get(0);
                } else {
                    return new ConsNode.Tuple(expressions);
                }
            }
        }.sequence(parser.sepBy1(term(",")), term(",").succeeds().optional(false));
    }

    public static Parser<ExprNode> one ()
    {
        Parser.Reference<ExprNode> ref = Parser.newReference();
        Parser<ExprNode> lazy = ref.lazy();

        Parser<ExprNode> parser;
        parser = Parsers.or(BOOLEAN_PARSER, NULL_PARSER, DECIMAL_PARSER, INTEGER_PARSER, NAME_PARSER, STRING_PARSER);
        parser = Parsers.or(paren(lazy), list(lazy), dict(lazy), set(lazy), parser);

        parser = new OperatorTable<ExprNode>()
                .postfix(qualified(), 120)
                .postfix(invocationWithParens(lazy), 120)
                .infixl(binary("|", term("|")), 110)
                .infixr(binary("**", term("**")), 100)
                .infixl(binary("*", term("*")), 90)
                .infixl(binary("/", term("/")), 90)
                .infixl(binary("//", term("//")), 90)
                .infixl(binary("%", term("%")), 90)
                .infixl(binary("+", term("+")), 80)
                .infixl(binary("-", term("-")), 80)
                .infixl(binary("~", term("~")), 70)
                .infixl(binary("not in", id("not", "in")), 60)
                .infixl(binary("in", id("in")), 60)
                .infixl(binary("is not", id("is", "not")), 60)
                .infixl(binary("is", id("is")), 60)
                .infixl(binary("<", term("<")), 60)
                .infixl(binary("<=", term("<=")), 60)
                .infixl(binary(">", term(">")), 60)
                .infixl(binary(">=", term(">=")), 60)
                .infixl(binary("==", term("==")), 60)
                .infixl(binary("!=", term("!=")), 60)
                .prefix(unary("not", id("not")), 50)
                .infixl(binary("and", id("and")), 40)
                .infixl(binary("or", id("or")), 30)
                .infixl(ifElse(lazy), 20)
                .build(parser);

        ref.set(parser);

        return Marker.mark(parser);
    }

    public static Parser<LitNode<?>> reserved ()
    {
        return Marker.mark(Parsers.or(BOOLEAN_PARSER, NULL_PARSER));
    }

    public static Parser<LitNode.Dec> decimal ()
    {
        return Marker.mark(DECIMAL_PARSER);
    }

    public static Parser<LitNode.Int> integer ()
    {
        return Marker.mark(INTEGER_PARSER);
    }

    public static Parser<LitNode<? extends Number>> number ()
    {
        return Parsers.<LitNode<? extends Number>>or(decimal(), integer());
    }

    public static Parser<LitNode.Str> string ()
    {
        return Marker.mark(STRING_PARSER);
    }

    public static Parser<LitNode<?>> literal ()
    {
        return Parsers.or(reserved(), number(), string());
    }

    public static Parser<IdNode> name ()
    {
        return Marker.mark(NAME_PARSER);
    }

    public static Parser<ConsNode.List> list ()
    {
        return Marker.mark(list(one()));
    }

    public static Parser<ConsNode.Dict> dict ()
    {
        return Marker.mark(dict(one()));
    }

    public static Parser<ConsNode.Set> set ()
    {
        return Marker.mark(set(one()));
    }

    public static Parser<InvArgListNode> argumentList ()
    {
        return Marker.mark(argumentList(one()));
    }

    public static Parser<ExprNode> anyOf (Parser<? extends ExprNode> parser)
    {
        return Marker.mark(any(parser));
    }

    public static Parser<ExprNode> any ()
    {
        return anyOf(one());
    }

    private ExpressionParser ()
    {}
}
