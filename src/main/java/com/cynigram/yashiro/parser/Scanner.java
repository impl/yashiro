package com.cynigram.yashiro.parser;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Ordering;
import org.codehaus.jparsec.*;
import org.codehaus.jparsec.pattern.Pattern;
import org.codehaus.jparsec.pattern.Patterns;

import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

import static com.cynigram.yashiro.parser.TemplateTerminals.fragment;

class Scanner
{
    static class ScannerTag {
        private final Parser<Void> lookahead;
        private final Parser<List<Token>> parser;

        ScannerTag (Parser<Void> lookahead, Parser<List<Token>> parser)
        {
            this.lookahead = lookahead;
            this.parser = parser;
        }

        public Parser<Void> getLookahead()
        {
            return lookahead;
        }

        public Parser<List<Token>> getParser()
        {
            return parser;
        }
    }

    static final Parser<Void> NAME_SCANNER = Scanners
            .pattern(Patterns.isChar(CharPredicates2.IS_IDENTIFIER_START).next(Patterns.isChar(CharPredicates2.IS_IDENTIFIER_CONTINUE).many()), "name");

    static final String[] OPERATORS = new String[] {
        "/", "//", "*", "%", "**", "~",
        "==", "!=", ">", ">=", "<", "<=",
        "=", ".", ":", "|", ",", ";",
    };

    static final String[] MATCHING_OPERATORS = new String[] {
        "+", "-", "[", "]", "(", ")", "{", "}",
    };

    static final Terminals TERMS = Terminals.caseSensitive(
            NAME_SCANNER.source(),
            ObjectArrays.concat(OPERATORS, MATCHING_OPERATORS, String.class),
            new String[] {});

    static final java.util.regex.Pattern SINGLE_QUOTE_STRING_PATTERN = java.util.regex.Pattern.compile("((\\\\.)|[^'\\\\])*", java.util.regex.Pattern.DOTALL);
    static final Parser<String> SINGLE_QUOTE_STRING_SCANNER = Scanners.pattern(Patterns.regex(SINGLE_QUOTE_STRING_PATTERN), "string").source().between(Scanners.isChar('\''), Scanners.isChar('\''));

    static final java.util.regex.Pattern DOUBLE_QUOTE_STRING_PATTERN = java.util.regex.Pattern.compile("((\\\\.)|[^\"\\\\])*", java.util.regex.Pattern.DOTALL);
    static final Parser<String> DOUBLE_QUOTE_STRING_SCANNER = Scanners.pattern(Patterns.regex(DOUBLE_QUOTE_STRING_PATTERN), "string").source().between(Scanners.isChar('"'), Scanners.isChar('"'));

    static final Parser<?> TOKENIZER = Parsers.or(
            Parsers.longer(Terminals.LongLiteral.TOKENIZER, Terminals.DecimalLiteral.TOKENIZER),
            Scanners.notAmong("+-[](){}", Joiner.on(", ").join(OPERATORS)).peek().next(TERMS.tokenizer()),
            SINGLE_QUOTE_STRING_SCANNER.map(Maps2.UNESCAPE_MAP),
            DOUBLE_QUOTE_STRING_SCANNER.map(Maps2.UNESCAPE_MAP));

    static final Parser<Void> LINE_WHITESPACE_SCANNER = Scanners.pattern(Patterns.many1(CharPredicates2.IS_LINE_WHITESPACE), "single-line whitespaces");
    static final Parser<Void> WHITESPACE_SCANNER = Scanners.WHITESPACES;

    static final Parser<List<Token>> LINE_SCANNER;

    static Parser<List<Token>> blockScanner0 (String end)
    {
        Parser<Void> endParser = end != null ? Scanners.string("%}") : Parsers.<Void>never();

        /* Tokenizer for block data. */
        Parser.Reference<List<Token>> ref = Parser.newReference();
        Parser<List<Token>> lazy = ref.lazy();

        Parser<List<Token>> matchingBlockTokenizer = Parsers2.flatten(
                Parsers.or(
                        Parsers2.collectN1(tokened("{").followedBy(WHITESPACE_SCANNER.optional()), lazy.optional(), WHITESPACE_SCANNER.optional().next(tokened("}"))),
                        Parsers2.collectN1(tokened("[").followedBy(WHITESPACE_SCANNER.optional()), lazy.optional(), WHITESPACE_SCANNER.optional().next(tokened("]"))),
                        Parsers2.collectN1(tokened("(").followedBy(WHITESPACE_SCANNER.optional()), lazy.optional(), WHITESPACE_SCANNER.optional().next(tokened(")"))),
                        Parsers2.collectN1(tokened("+").followedBy(WHITESPACE_SCANNER.optional()), lazy),
                        Parsers2.collectN1(tokened("-").followedBy(WHITESPACE_SCANNER.optional()), lazy),
                        endParser.not().next(TOKENIZER.token()).sepBy1(WHITESPACE_SCANNER.optional())).sepBy1(WHITESPACE_SCANNER.optional()));

        ref.set(matchingBlockTokenizer);

        return matchingBlockTokenizer.optional();
    }

    static {
        Parser<List<Token>> matchingBlockTokenizer = blockScanner0(null);

        LINE_SCANNER = Parsers2.flatten(
                Parsers.or(
                        Parsers2.collectN1(tokened("{").followedBy(WHITESPACE_SCANNER.optional()), matchingBlockTokenizer.optional(), WHITESPACE_SCANNER.optional().next(tokened("}"))),
                        Parsers2.collectN1(tokened("[").followedBy(WHITESPACE_SCANNER.optional()), matchingBlockTokenizer.optional(), WHITESPACE_SCANNER.optional().next(tokened("]"))),
                        Parsers2.collectN1(tokened("(").followedBy(WHITESPACE_SCANNER.optional()), matchingBlockTokenizer.optional(), WHITESPACE_SCANNER.optional().next(tokened(")"))),
                        Parsers2.collectN1(tokened("+").followedBy(WHITESPACE_SCANNER.optional()), matchingBlockTokenizer),
                        Parsers2.collectN1(tokened("-").followedBy(WHITESPACE_SCANNER.optional()), matchingBlockTokenizer),
                        TOKENIZER.token().sepBy1(LINE_WHITESPACE_SCANNER.optional())
                ).sepBy(LINE_WHITESPACE_SCANNER.optional())).between(LINE_WHITESPACE_SCANNER.optional(), LINE_WHITESPACE_SCANNER.optional());
    }

    static Parser<Token> stripScanner (boolean stripByDefault)
    {
        return Scanners.among("+-", "whitespace stripping identifiers").source().optional(stripByDefault ? "-" : "+").map(fragment(TemplateTag.STRIP)).token();
    }

    static Parser<List<Token>> blockScanner (String start, String end, TemplateTag tag, boolean lstripBlocksByDefault, boolean trimBlocksByDefault)
    {
        Parser<List<Token>> startParser = Parsers2.flipped(
                Scanners.string(start).source().map(fragment(tag)).token(),
                stripScanner(lstripBlocksByDefault).followedBy(WHITESPACE_SCANNER.optional()));
        Parser<List<Token>> endParser = Parsers2.flipped(
                WHITESPACE_SCANNER.optional().next(stripScanner(trimBlocksByDefault)),
                Scanners.string(end).source().map(fragment(tag)).token());

        return Parsers2.collect(startParser, blockScanner0(end), endParser);
    }

    static Parser<List<Token>> rawScanner (String start, String end, boolean lstripBlocksByDefault, boolean trimBlocksByDefault)
    {
        /* Ghetto level: maximal. */
        return Parsers2.filter(Parsers2.collect(
                /* {% raw %} */
                Parsers2.flipped(
                    Scanners.string(start).source().map(fragment(TemplateTag.STATEMENT_BLOCK)).token(),
                    stripScanner(lstripBlocksByDefault)),
                Parsers2.list(
                    Scanners.string("raw")
                            .between(WHITESPACE_SCANNER.optional(), WHITESPACE_SCANNER.optional())
                            .retn(Tokens.identifier("raw"))
                            .token()),
                Parsers2.flipped(
                    stripScanner(trimBlocksByDefault),
                    Scanners.string(end).source().map(fragment(TemplateTag.STATEMENT_BLOCK)).token()),

                /* Data. */
                Parsers2.list(
                    Scanners.string(start)
                            .next(Scanners.among("+-").optional())
                            .next(WHITESPACE_SCANNER.optional())
                            .next(Scanners.string("endraw"))
                            .next(WHITESPACE_SCANNER.optional())
                            .next(Scanners.among("+-").optional())
                            .next(Scanners.string(end))
                            .not()
                            .next(Scanners.ANY_CHAR).many().source().map(fragment(TemplateTag.DATA)).token()),

                /* {% endraw %} */
                Parsers2.flipped(
                    Scanners.string(start).source().map(fragment(TemplateTag.STATEMENT_BLOCK)).token(),
                    stripScanner(lstripBlocksByDefault)),
                Parsers2.list(
                    Scanners.string("endraw")
                        .between(WHITESPACE_SCANNER.optional(), WHITESPACE_SCANNER.optional())
                        .retn(Tokens.identifier("endraw"))
                        .token()),
                Parsers2.flipped(
                    stripScanner(trimBlocksByDefault),
                    Scanners.string(end).source().map(fragment(TemplateTag.STATEMENT_BLOCK)).token())),
                Predicates.<Token>notNull());
    }

    private static Parser<Token> tokened (String s)
    {
        Parser<?> scanner = s.length() == 1 ? Scanners.isChar(s.charAt(0)) : Scanners.string(s);
        return scanner.retn(Tokens.reserved(s)).token();
    }

    static ScannerTag blockScannerTag (String start, String end, boolean lstripBlocksByDefault, boolean trimBlocksByDefault)
    {
        return new ScannerTag(Scanners.string(start),
                rawScanner(start, end, lstripBlocksByDefault, trimBlocksByDefault)
                        .or(blockScanner(start, end, TemplateTag.STATEMENT_BLOCK, lstripBlocksByDefault, trimBlocksByDefault)));
    }

    static ScannerTag variableScannerTag (String start, String end, boolean lstripBlocksByDefault, boolean trimBlocksByDefault)
    {
        return new ScannerTag(Scanners.string(start), blockScanner(start, end, TemplateTag.VARIABLE_BLOCK, lstripBlocksByDefault, trimBlocksByDefault));
    }

    static ScannerTag commentScannerTag (String start, String end)
    {
        return new ScannerTag(Scanners.string(start), Scanners.blockComment(start, end).retn(Collections.<Token>emptyList()));
    }

    static ScannerTag lineCommentScannerTag (String prefix)
    {
        return new ScannerTag(Scanners.string(prefix), Scanners.pattern(Patterns.string(prefix).next(Patterns.regex(".*")), "line comment").retn(Collections.<Token>emptyList()));
    }

    static ScannerTag lineStatementScannerTag (String prefix)
    {
        Pattern linePattern = Patterns.or(Patterns.string("\r\n"), Patterns.among("\r\n"));

        Parser<Void> startParser = Parsers.or(Parsers2.index(0), Scanners.pattern(linePattern, "newline"))
                .next(LINE_WHITESPACE_SCANNER.optional())
                .next(Scanners.pattern(Patterns.string(prefix), "line statement"));
        Parser<Void> endParser = Scanners.pattern(Patterns.or(Patterns.EOF, linePattern), "end of line statement");

        return new ScannerTag(
                startParser,
                Parsers2.collectN2(
                        Parsers.constant("!").map(fragment(TemplateTag.STRIP)).token(),
                        startParser.retn(prefix).map(fragment(TemplateTag.STATEMENT_BLOCK)).token(),
                        LINE_SCANNER,
                        endParser.retn("").map(fragment(TemplateTag.STATEMENT_BLOCK)).token(),
                        Parsers.constant("+").map(fragment(TemplateTag.STRIP)).token()
                )
        );
    }

    private final Environment environment;

    private Parser<List<Token>> scanner = null;

    protected Parser<List<Token>> scanner ()
    {
        /* Sorted by the length of the start character, effectively. */
        SortedMap<String, ScannerTag> tags = Maps.newTreeMap(Ordering.natural().reverse());

        /* {% ... %} */
        tags.put(environment.getBlockStartSequence(), blockScannerTag(environment.getBlockStartSequence(), environment.getBlockEndSequence(), environment.isLstripBlocks(), environment.isTrimBlocks()));

        /* {{ ... }} */
        tags.put(environment.getVariableStartSequence(), variableScannerTag(environment.getVariableStartSequence(), environment.getVariableEndSequence(), environment.isLstripBlocks(), environment.isTrimBlocks()));

        /* {# ... #} */
        tags.put(environment.getCommentStartSequence(), commentScannerTag(environment.getCommentStartSequence(), environment.getCommentEndSequence()));

        /* # ... */
        if (environment.getLineCommentPrefixSequence() != null)
            tags.put(environment.getLineCommentPrefixSequence(), lineCommentScannerTag(environment.getLineCommentPrefixSequence()));

        /* ## ... */
        if (environment.getLineStatementPrefixSequence() != null) {
            tags.put(environment.getLineStatementPrefixSequence(), lineStatementScannerTag(environment.getLineStatementPrefixSequence()));
        }

        Parser<List<Token>> anyTag = Parsers.or(Iterables.transform(tags.values(), new Function<ScannerTag, Parser<List<Token>>>()
        {
            @Override
            public Parser<List<Token>> apply (ScannerTag input)
            {
                return input.getParser();
            }
        }));

        Parser<Token> anyData = Parsers.or(Iterables.transform(tags.values(), new Function<ScannerTag, Parser<Void>>() {
            @Override
            public Parser<Void> apply(ScannerTag input)
            {
                return input.getLookahead();
            }
        })).not().next(Scanners.ANY_CHAR).many().source().map(fragment(TemplateTag.DATA)).token();

        return Parsers2.flatten(Parsers2.collectN1(anyData, anyTag.optional()).many());
    }

    Scanner (Environment environment)
    {
        this.environment = environment;
    }

    public Environment getEnvironment ()
    {
        return environment;
    }

    public Parser<List<Token>> getScanner ()
    {
        if (scanner == null) {
            scanner = scanner();
        }

        return scanner;
    }
}
