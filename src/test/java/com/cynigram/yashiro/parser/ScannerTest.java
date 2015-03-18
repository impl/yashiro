package com.cynigram.yashiro.parser;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Tokens;
import org.codehaus.jparsec.error.ParserException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.cynigram.yashiro.parser.TemplateTerminals.*;

@RunWith(JUnit4.class)
public class ScannerTest
{
    protected static final Environment ENVIRONMENT = new EnvironmentBuilder().build();
    protected static final Scanner SCANNER = new Scanner(ENVIRONMENT);

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
    public void testTokenizerParsesManyTerms ()
    {
        assertScanOne(
                "for i in hello.world:",
                id("for"), id("i"), id("in"), id("hello"), term("."), id("world"), term(":"));
    }

    @Test
    public void testTokenizerParsesIntegers ()
    {
        Assert.assertEquals((long)1, Scanner.TOKENIZER.parse("1"));
    }

    @Test
    public void testTokenizerParsesDecimals ()
    {
        Assert.assertEquals(Tokens.decimalLiteral("1.0"), Scanner.TOKENIZER.parse("1.0"));
    }

    @Test
    public void testScannerParsesVariableBlocks ()
    {
        assertScanBody("{{ expr }}",
                data(), strip(), variableBlock(), id("expr"), variableBlock(), strip());
    }

    @Test
    public void testScannerParsersStatementBlocks ()
    {
        assertScanBody("{% stmt %}",
                data(), strip(), statementBlock(), id("stmt"), statementBlock(), strip());
    }

    @Test
    public void testScannerParsersRawBlocks ()
    {
        assertScanBody("{% raw %}{% notastatement %}{% endraw %}",
                data(), strip(), statementBlock(), id("raw"), statementBlock(), strip(),
                data() /* i.e., %{ notastatement %} */,
                strip(), statementBlock(), id("endraw"), statementBlock(), strip());
    }

    @Test
    public void testScannerParsesWhitespaceQualifiers ()
    {
        assertScanBody("{%+ stmt -%}",
                data(), strip(false), statementBlock(), id("stmt"), statementBlock(), strip(true));
    }

    @Test
    public void testEnvironmentIsUsedByScanner ()
    {
        Assert.assertEquals(SCANNER.getEnvironment(), ENVIRONMENT);
    }

    protected static void assertScanOne (String test, Parser<?>... expected)
    {
        try {
            make(expected).from(Scanner.LINE_SCANNER).parse(test);
        } catch (ParserException pe) {
            Assert.fail("Scanning failed: " + pe.getMessage());
        }
    }

    protected static void assertScanBody (String test, Parser<?>... expected)
    {
        try {
            make(expected).from(SCANNER.scanner()).parse(test);
        } catch (ParserException pe) {
            Assert.fail("Scanning failed: " + pe.getMessage());
        }
    }

    protected static Parser<?> make (Parser<?>... sequence)
    {
        return Parsers.sequence(sequence).next(Parsers.EOF);
    }
}
