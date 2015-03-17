package com.cynigram.yashiro.parser;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Tokens;
import org.codehaus.jparsec.error.ParserException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.cynigram.yashiro.parser.TemplateTerminals.id;
import static com.cynigram.yashiro.parser.TemplateTerminals.term;

@RunWith(JUnit4.class)
public class ScannerTest
{
    @Test
    public void testNameScannerParsesUsualNames ()
    {
        Assert.assertEquals("hello", Scanner.NAME_SCANNER.source().parse("hello"));
        Assert.assertEquals("_something", Scanner.NAME_SCANNER.source().parse("_something"));
    }

    @Test(expected = ParserException.class)
    public void testNameScannerFailsOnBadNames () throws ParserException
    {
        Scanner.NAME_SCANNER.source().parse("\0");
    }

    @Test
    public void testNameScannerParsesUnicodeXidNames ()
    {
        Assert.assertEquals("Δt", Scanner.NAME_SCANNER.source().parse("Δt"));
    }

    @Test
    public void testTokenizerParsesOneTerm ()
    {
        Assert.assertEquals(Tokens.reserved("~"), Scanner.TOKENIZER.parse("~"));
        Assert.assertEquals(Tokens.identifier("for"), Scanner.TOKENIZER.parse("for"));
    }

    @Test
    public void testTokenizerParsersManyTerms ()
    {
        assertScan(
                "for i in hello.world:",
                id("for"), id("i"), id("in"), id("hello"), term("."), id("world"), term(":"));
    }

    @Test
    public void testNullMethodTernaryCast ()
    {
        Assert.assertEquals(null, new X().retn(false));
    }

    public static class X
    {
        public Integer retn (boolean stmt)
        {
            return stmt ? (Integer)42 : null;
        }
    }

    protected static void assertScan (String test, Parser<?>... expected)
    {
        try {
            make(expected).from(Scanner.LINE_SCANNER).parse(test);
        } catch (ParserException pe) {
            Assert.fail("Scanning failed with " + pe.getMessage());
        }
    }

    protected static Parser<?> make (Parser<?>... sequence)
    {
        return Parsers.sequence(sequence).next(Parsers.EOF);
    }
}
