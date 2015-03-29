package com.cynigram.yashiro.parser;

import com.cynigram.yashiro.ast.BodyListNode;
import com.cynigram.yashiro.ast.DataNode;
import com.cynigram.yashiro.ast.StmtNode;
import com.cynigram.yashiro.ast.VarNode;
import com.google.common.base.*;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.misc.Mapper;

import java.util.*;
import java.util.regex.Pattern;

import static com.cynigram.yashiro.parser.TagParsers.*;
import static com.google.common.base.Preconditions.checkNotNull;

public class BodyParser
{
    private ListMultimap<String, StatementParser> statementParsers = Multimaps.newListMultimap(Maps.<String, Collection<StatementParser>>newHashMap(), new Supplier<List<StatementParser>>()
    {
        @Override
        public List<StatementParser> get()
        {
            return Lists.newLinkedList();
        }
    });

    private ListMultimap<String, StatementParser> statementParsersWithShortNames = Multimaps.newListMultimap(Maps.<String, Collection<StatementParser>>newHashMap(), new Supplier<List<StatementParser>>()
    {
        @Override
        public List<StatementParser> get()
        {
            return Lists.newLinkedList();
        }
    });

    static final Parser<List<String>> NAME_PARSER = TagParsers.name().sepBy1(term("."));
    static final Parser<String> NAME_PARSER_COMBINED = NAME_PARSER.map(
            new Map<List<String>, String>() {
                @Override
                public String map (List<String> names)
                {
                    return Joiner.on('.').join(names);
                }
            });

    public BodyParser (Iterable<StatementParser> statementParsers)
    {
        checkNotNull(statementParsers);

        for (StatementParser parser : statementParsers) {
            String fullName = parser.getPackage() + "." + parser.getName();
            this.statementParsers.put(fullName, parser);
            this.statementParsersWithShortNames.put(parser.getName(), parser);
        }
    }

    private static final Pattern LINE_STRIP_PATTERN = Pattern.compile("((?m)^)\\s+$");

    Parser<DataNode> data ()
    {
        return Marker.mark(Mapper.curry(DataNode.class).sequence(
                new Mapper<String>()
                {
                    public String map(Stripping stripLeading, List<String> bodies, Stripping stripTrailing)
                    {
                        String body = Joiner.on("").join(bodies);

                        if (stripLeading == Stripping.ALL)
                            body = CharMatcher.WHITESPACE.trimLeadingFrom(body);

                        if (stripTrailing == Stripping.ALL)
                            body = CharMatcher.WHITESPACE.trimTrailingFrom(body);
                        else if (stripTrailing == Stripping.LINE)
                            body = LINE_STRIP_PATTERN.matcher(body).replaceFirst("");

                        return body;
                    }
                }.sequence(strip().optional(Stripping.NONE), Parsers.or(TagParsers.data(), Parsers.EOF.retn("")).many1(), strip().optional(Stripping.NONE))));
    }

    Parser<VarNode> variable ()
    {
        return Marker.mark(Mapper.curry(VarNode.class).sequence(ExpressionParser.any()));
    }

    Parser<StmtNode> statement (final Parser<BodyListNode> lazy)
    {
        return NAME_PARSER.peek().next(
                new Map<List<String>, Parser<? extends StmtNode>>() {
                    @Override
                    public Parser<? extends StmtNode> map (List<String> names)
                    {
                        String name = Joiner.on('.').join(names);
                        boolean useShortNames = names.size() == 1;

                        List<Parser<? extends StmtNode>> parsers = Lists.newLinkedList();

                        ListMultimap<String, StatementParser> candidateMap = useShortNames
                                ? statementParsersWithShortNames
                                : statementParsers;

                        List<StatementParser> candidates = candidateMap.get(name);
                        if (candidates != null) {
                            for (StatementParser candidate : candidates) {
                                ContImpl cont = new ContImpl(candidate, lazy, useShortNames);
                                parsers.add(Marker.mark(candidate.parser(cont)));
                            }
                        }

                        return !parsers.isEmpty()
                                ? Parsers.longest(parsers)
                                : Parsers.<StmtNode>unexpected("statement '" + name + "' is not registered");
                    }
                });
    }

    Parser<BodyListNode> body (Parser<BodyListNode> lazy, List<Parser<?>> haltingClauseParsers)
    {
        Parser<VarNode> variableParser = variable().between(variableBlock(), variableBlock());
        Parser<StmtNode> statementParser = statement(lazy).between(statementBlock(), statementBlock());

        return Mapper.curry(BodyListNode.class).sequence(
                Parsers2.filter(
                        Parsers.or(
                                data(), variableParser,
                                statementBlock().next(Parsers.or(haltingClauseParsers).peek()).retn(null),
                                statementParser).many1(),
                        Predicates.notNull()));
    }

    Parser<BodyListNode> body (Parser<BodyListNode> lazy)
    {
        return body(lazy, Collections.<Parser<?>>emptyList());
    }

    public Parser<BodyListNode> parser ()
    {
        Parser.Reference<BodyListNode> ref = Parser.newReference();
        Parser<BodyListNode> lazy = ref.lazy();

        Parser<BodyListNode> parser = body(lazy);

        ref.set(parser);
        return parser.followedBy(Parsers.EOF);
    }

    protected class ContImpl implements StatementParser.Cont
    {
        private final StatementParser parser;
        private final Parser<BodyListNode> lazy;
        private final boolean allowShortName;

        protected ContImpl (StatementParser parser, Parser<BodyListNode> lazy, boolean allowShortName)
        {
            this.parser = parser;
            this.lazy = lazy;
            this.allowShortName = allowShortName;
        }

        @Override
        public Parser<String> name ()
        {
            return NAME_PARSER_COMBINED;
        }

        @Override
        public Parser<String> name (String name)
        {
            List<String> names = Lists.newArrayList(Splitter.on('.').split(parser.getPackage()));
            names.add(name);

            Iterator<String> it = names.iterator();

            Parser<?> longNameParser = TagParsers.name(it.next());
            while (it.hasNext())
                longNameParser.next(term(".")).next(TagParsers.name(it.next()));

            Parser<String> longNameParserR = longNameParser.retn(parser.getPackage() + "." + name);

            return allowShortName ? Parsers.or(longNameParserR, TagParsers.name(name).retn(name)) : longNameParserR;
        }

        @Override
        public Parser<BodyListNode> cont (Set<String> clauseNames)
        {
            List<Parser<?>> clauseParsers = Lists.newArrayListWithExpectedSize(clauseNames.size());
            for (String clauseName : clauseNames)
                clauseParsers.add(name(clauseName));

            /* NB: This is a closing block! */
            return statementBlock().next(body(lazy, clauseParsers).optional());
        }
    }
}
