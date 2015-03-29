package com.cynigram.yashiro.parser.statement;

import com.cynigram.yashiro.ast.ExprNode;
import com.cynigram.yashiro.parser.ExpressionParser;
import com.google.common.base.Supplier;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.misc.Mapper;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.cynigram.yashiro.parser.TagParsers.name;
import static com.cynigram.yashiro.parser.TagParsers.term;

public final class ContentParserBuilder
{
    private static ListMultimap<String, ExprNode> result ()
    {
        return Multimaps.newListMultimap(Maps.<String, Collection<ExprNode>>newHashMap(), new Supplier<List<ExprNode>>() {
            @Override
            public List<ExprNode> get ()
            {
                return new LinkedList<>();
            }
        });
    }

    private Repeatables repeatables = new Repeatables();

    /* We can't use #constant(result()) here, because it won't regenerate the list every
     * time the parser runs. */
    private List<Parser<ListMultimap<String, ExprNode>>> out = Lists.newArrayList();

    ContentParserBuilder()
    {
        out.add(Parsers.always().map(new Map<Object, ListMultimap<String, ExprNode>>() {
            @Override
            public ListMultimap<String, ExprNode> map (Object o)
            {
                return result();
            }
        }));
    }

    public <T extends ExprNode> ContentParserBuilder expr (final String group, Parser<T> parser)
    {
        for (int i = 0; i < out.size(); i++) {
            out.set(i, new Mapper<ListMultimap<String, ExprNode>>() {
                @SuppressWarnings("unused")
                public ListMultimap<String, ExprNode> map (ListMultimap<String, ExprNode> in, ExprNode expr)
                {
                    in.put(group, expr);
                    return in;
                }
            }.sequence(out.get(i), parser));
        }

        return this;
    }

    public ContentParserBuilder expr (String group)
    {
        return expr(group, ExpressionParser.one());
    }

    public ContentParserBuilder word (String... name)
    {
        for (int i = 0; i < out.size(); i++) {
            out.set(i, out.get(i).followedBy(name(name)));
        }

        return this;
    }

    public ContentParserBuilder wordWithGroup (final String group, String... name)
    {
        for (int i = 0; i < out.size(); i++) {
            out.set(i, out.get(i).followedBy(name(name)).map(new Map<ListMultimap<String, ExprNode>, ListMultimap<String, ExprNode>>() {
                @Override
                public ListMultimap<String, ExprNode> map (ListMultimap<String, ExprNode> from)
                {
                    from.put(group, null);
                    return from;
                }
            }));
        }

        return this;
    }

    public ContentParserBuilder callable (String group)
    {
        return expr(group, ExpressionParser.argumentList().between(term("("), term(")")));
    }

    public ContentParserBuilder sym (String symbol)
    {
        for (int i = 0; i < out.size(); i++) {
            out.set(i, out.get(i).followedBy(term(symbol)));
        }

        return this;
    }

    public ContentParserBuilder select (ContentParserBuilder... options)
    {
        /* Cartesian product, recreate output parser. */
        List<Parser<ListMultimap<String, ExprNode>>> result = Lists.newArrayListWithExpectedSize(options.length * out.size());

        for (Parser<ListMultimap<String, ExprNode>> out1 : out) {
            for (ContentParserBuilder option : options) {
                result.add(new Mapper<ListMultimap<String, ExprNode>>() {
                    @SuppressWarnings("unused")
                    public ListMultimap<String, ExprNode> map (ListMultimap<String, ExprNode> in, ContentMatchMap option)
                    {
                        in.putAll(option.all());
                        return in;
                    }
                }.sequence(out1, option.parser()));
            }
        }

        out = result;

        return this;
    }

    public ContentParserBuilder many ()
    {
        repeatables.setMany(true);
        return this;
    }

    public ContentParserBuilder optional ()
    {
        repeatables.setOptional(true);
        return this;
    }

    public Parser<ContentMatchMap> parser ()
    {
        Parser<ListMultimap<String, ExprNode>> parser = Parsers.longest(out);

        if (repeatables.isMany())
        {
            parser = parser.many1().map(new Map<List<ListMultimap<String, ExprNode>>, ListMultimap<String, ExprNode>>() {
                @Override
                public ListMultimap<String, ExprNode> map (List<ListMultimap<String, ExprNode>> from)
                {
                    ListMultimap<String, ExprNode> r = result();
                    for (ListMultimap<String, ExprNode> from1 : from) {
                        r.putAll(from1);
                    }
                    return r;
                }
            });
        }

        if (repeatables.isOptional()) {
            parser = parser.optional();
        }

        return parser.map(new Map<ListMultimap<String, ExprNode>, ContentMatchMap>()
        {
            @Override
            public ContentMatchMap map (ListMultimap<String, ExprNode> result)
            {
                return new ContentMatchMap(result);
            }
        });
    }
}
