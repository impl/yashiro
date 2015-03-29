package com.cynigram.yashiro.parser;

import com.cynigram.yashiro.ast.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.error.ParserException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;
import java.util.List;

@RunWith(JUnit4.class)
public class ExpressionParserTest
{
    protected static Parser<ExprNode> PARSER = ExpressionParser.any().from(Scanner.LINE_SCANNER);

    @Test
    public void testOperationPrecedence ()
    {
        ExprNode expected;

        expected = new OpNode.Binary(
                "or",
                new OpNode.Binary("and", new LitNode.Dec("1.0"), new IdNode("twenty_five")),
                new OpNode.Binary("**", new LitNode.Int(6), new LitNode.Int(4)));
        Assert.assertEquals(expected, PARSER.parse("1.0 and twenty_five or 6**4"));
    }

    @Test
    public void testUnaryOperators ()
    {
        ExprNode expected;

        expected = new OpNode.Unary("not", new IdNode("knot"));
        Assert.assertEquals(expected, PARSER.parse("not knot"));
    }

    @Test
    public void testIfElse ()
    {
        ExprNode expected;

        expected = new OpNode.Binary("if", new LitNode.Int(1), LitNode.Bool.TRUE);
        Assert.assertEquals(expected, PARSER.parse("1 if true"));

        expected = new OpNode.Trinary("if", new LitNode.Int(1), LitNode.Bool.FALSE, new LitNode.Int(9001));
        Assert.assertEquals(expected, PARSER.parse("1 if false else 9001"));

        expected = new OpNode.Trinary(
                "if",
                new OpNode.Binary("+", new LitNode.Int(1), new LitNode.Int(1)),
                new OpNode.Binary(
                        "and",
                        new IdNode("this_value"),
                        new OpNode.Binary(
                                "<",
                                new OpNode.Binary("<", new LitNode.Int(200), new IdNode("that_value")),
                                new LitNode.Int(400))),
                new OpNode.Binary(
                        "+",
                        new OpNode.Binary(
                            "/",
                            new OpNode.Binary("*", new LitNode.Int(6), new LitNode.Int(2)),
                            new LitNode.Int(4)),
                        new LitNode.Int(1)));
        Assert.assertEquals(expected, PARSER.parse("1+1 if this_value and 200 < that_value < 400 else 6*2/4+1"));
    }

    @Test
    public void testCompoundOperators ()
    {
        ExprNode expected;

        expected = new OpNode.Binary("not in", new LitNode.Int(1), new IdNode("test_arr"));
        Assert.assertEquals(expected, PARSER.parse("1 not in test_arr"));

        expected = new OpNode.Binary("is not", LitNode.Bool.TRUE, LitNode.Bool.FALSE);
        Assert.assertEquals(expected, PARSER.parse("true is not false"));

        expected = new OpNode.Binary("is", LitNode.Bool.TRUE, LitNode.Bool.TRUE);
        Assert.assertEquals(expected, PARSER.parse("true is true"));
    }

    @Test(expected = ParserException.class)
    public void testCompoundOperatorParsingFailsOnInvalidOperatorNames ()
    {
        PARSER.parse("true is is error");
    }

    @Test
    public void testTuples ()
    {
        ExprNode expected;

        expected = new ConsNode.Tuple(Lists.newArrayList(new LitNode.Int(1)));
        Assert.assertEquals(expected, PARSER.parse("1,"));

        expected = new OpNode.Binary(
                "in",
                new ConsNode.Tuple(Lists.newArrayList(new LitNode.Int(1), new LitNode.Int(2))),
                new ConsNode.Tuple(Lists.newArrayList(
                        new LitNode.Int(4),
                        new LitNode.Int(5),
                        new OpNode.Binary(
                                "*",
                                new PNode(new OpNode.Binary("+", new LitNode.Int(3), new LitNode.Int(2))),
                                new LitNode.Int(6)))));
        Assert.assertEquals(expected, PARSER.parse("(1,2) in (4,5,(3+2)*6)"));

        expected = new ConsNode.Tuple(Lists.newArrayList(
                new LitNode.Str("hello"),
                new OpNode.Binary("+", new LitNode.Int(1), new LitNode.Int(4)),
                new OpNode.Binary(
                        "in",
                        new OpNode.Binary("%", new LitNode.Int(6), new LitNode.Int(3)),
                        new IdNode("test"))));
        Assert.assertEquals(expected, PARSER.parse("'hello', 1 + 4, 6 % 3 in test"));

        expected = new ConsNode.Tuple(Lists.newArrayList(
                new LitNode.Int(2),
                new LitNode.Int(3),
                new ConsNode.Tuple(Lists.newArrayList(
                        new LitNode.Int(4),
                        new LitNode.Int(5),
                        new ConsNode.Tuple(Lists.newArrayList(
                                new LitNode.Int(6),
                                new LitNode.Int(7),
                                new LitNode.Int(9001)))))));
        Assert.assertEquals(expected, PARSER.parse("(2,3,(4,5,(6,7,9001)))"));
    }

    @Test
    public void testCollections ()
    {
        ExprNode expected;

        List<Pair<? extends ExprNode, ? extends ExprNode>> items = Lists.newArrayList();
        items.add(Pair.of(
                new LitNode.Str("hello"),
                new ConsNode.List(Lists.newArrayList(
                        new LitNode.Int(1),
                        new LitNode.Int(2),
                        new LitNode.Int(3)))));
        items.add(Pair.of(
                new LitNode.Str("goodbye"),
                new ConsNode.Set(Lists.newArrayList(
                        new LitNode.Int(4),
                        new LitNode.Int(5),
                        new LitNode.Int(5)))));
        expected = new ConsNode.Dict(items);
        Assert.assertEquals(expected, PARSER.parse("{'hello':[1,2,3],'goodbye':{4,5,5}}"));

        expected = new ConsNode.Tuple(Lists.newArrayList(
                new ConsNode.Tuple(Collections.<ExprNode>emptyList()),
                new ConsNode.List(Collections.<ExprNode>emptyList()),
                new ConsNode.Dict(Collections.<Pair<? extends ExprNode, ? extends ExprNode>>emptyList())
        ));
        Assert.assertEquals(expected, PARSER.parse("(), [], {}"));
    }

    @Test
    public void testMethodInvocation ()
    {
        ExprNode expected;

        expected = new InvNode(
                new OpNode.Binary(".", new IdNode("hello"), new IdNode("false")),
                new InvArgListNode(Lists.newArrayList(
                        new InvArgNode.Positional(new OpNode.Binary("+", new LitNode.Int(2), new LitNode.Int(2))),
                        new InvArgNode.Named(new IdNode("false"), LitNode.Bool.FALSE),
                        new InvArgNode.ManyArgs(new IdNode("flax")),
                        new InvArgNode.KeywordArgs(new IdNode("kwargs"))
                ))
        );
        Assert.assertEquals(expected, PARSER.parse("hello.false(2+2, false=false, *flax, **kwargs)"));

        expected = new InvNode(
                new IdNode("eval"),
                new InvArgListNode(Lists.newArrayList(
                        new InvArgNode.Positional(new IdNode("a")),
                        new InvArgNode.Positional(new OpNode.Binary("*", new IdNode("b"), new IdNode("c"))))));
        Assert.assertEquals(expected, PARSER.parse("eval(a, b *c)"));

        expected = new OpNode.Binary(
                ".",
                new InvNode(
                        new OpNode.Binary(
                                ".",
                                new InvNode(
                                        new OpNode.Binary(".", new IdNode("foo"), new IdNode("bar")),
                                        new InvArgListNode(Lists.newArrayList(
                                                new InvArgNode.Positional(
                                                    new OpNode.Binary(
                                                            "+",
                                                            new LitNode.Int(2),
                                                            new InvNode(
                                                                    new OpNode.Binary(".", new IdNode("i"), new IdNode("x")),
                                                                    new InvArgListNode(Collections.<InvArgNode>emptyList()))))))),
                                new IdNode("baz")),
                        new InvArgListNode(Collections.<InvArgNode>emptyList())),
                new IdNode("quux"));
        Assert.assertEquals(expected, PARSER.parse("foo.bar(2+i.x()).baz().quux"));
    }

    @Test(expected = ParserException.class)
    public void testMethodInvocationWithInvalidSeparatorSyntaxFails ()
    {
        PARSER.parse("invalid(a, b, , *c)");
    }

    @Test
    public void testParenthesizedExpressions ()
    {
        ExprNode expected;

        expected = new OpNode.Binary(
                "*",
                new PNode(new OpNode.Binary("+", new LitNode.Int(1), new LitNode.Int(5))),
                new LitNode.Int(3));
        Assert.assertEquals(expected, PARSER.parse("(1 + 5) * 3"));

        expected = new OpNode.Binary(
                "==",
                new PNode(new OpNode.Binary("<", new LitNode.Int(1), new LitNode.Int(3))),
                LitNode.Bool.TRUE);
        Assert.assertEquals(expected, PARSER.parse("(1 < 3) == true"));
    }

    @Test
    public void testFilters ()
    {
        ExprNode expected;

        expected = new OpNode.Binary(
                "|",
                new OpNode.Binary(
                        "|",
                        new OpNode.Binary(".", new IdNode("user"), new IdNode("variable")),
                        new IdNode("e")),
                new IdNode("lolhtml_or_something"));
        Assert.assertEquals(expected, PARSER.parse("user.variable |e |lolhtml_or_something"));
    }

    @Test
    public void testStrings ()
    {
        Assert.assertEquals(new LitNode.Str("test"), PARSER.parse("'test'"));
        Assert.assertEquals(new LitNode.Str("test\n"), PARSER.parse("'test\\n'"));
        Assert.assertEquals(new LitNode.Str("test'"), PARSER.parse("'test\\''"));
        Assert.assertEquals(new LitNode.Str("test'"), PARSER.parse("\"test'\""));
    }
}
