package com.cynigram.yashiro.parser;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.misc.Mapper;

import java.util.Arrays;
import java.util.List;

public final class Parsers2
{
    public static <L, R> Parser<Pair<L, R>> pair (Parser<L> left, Parser<R> right)
    {
        return new Mapper<Pair<L, R>>() {
            @SuppressWarnings("unused")
            public Pair<L, R> map (L left, R right)
            {
                return Pair.of(left, right);
            }
        }.sequence(left, right);
    }

    public static <T> Parser<T> index (final int n)
    {
        return Parsers.INDEX.next(new Map<Integer, Parser<T>>()
        {
            @Override
            public Parser<T> map(Integer index)
            {
                return index == n ? Parsers.<T> always() : Parsers.<T> never();
            }
        });
    }

    @SafeVarargs
    public static <T> Parser<List<T>> list (Parser<? extends T>... parsers)
    {
        return Parsers.list(Arrays.asList(parsers));
    }

    @SafeVarargs
    public static <T> Parser<List<T>> collectN1 (Parser<T> parser1, Parser<List<T>> parsers, Parser<T>... parsers2)
    {
        return collect(list(parser1), parsers, list(parsers2));
    }

    @SafeVarargs
    public static <T> Parser<List<T>> collectN2 (Parser<T> parser1, Parser<T> parser2, Parser<List<T>> parsers, Parser<T>... parsers2)
    {
        return collect(list(parser1, parser2), parsers, list(parsers2));
    }

    @SafeVarargs
    public static <T> Parser<List<T>> collectN (Parser<List<T>> parsers, Parser<T>... parsers1)
    {
        return collect(parsers, list(parsers1));
    }

    @SafeVarargs
    public static <T> Parser<List<T>> collect (Parser<List<T>>... parsers)
    {
        return flatten(list(parsers));
    }

    public static <T> Parser<List<T>> flatten (Parser<List<List<T>>> parser)
    {
        return parser.map(new Map<List<List<T>>, List<T>>() {
            @Override
            public List<T> map (List<List<T>> from)
            {
                return Lists.newArrayList(Iterables.concat(Iterables.filter(from, Predicates.notNull())));
            }
        });
    }

    public static <T> Parser<List<T>> filter (Parser<List<T>> parser, final Predicate<T> pred)
    {
        return parser.map(new Map<List<T>, List<T>>() {
            @Override
            public List<T> map (List<T> from)
            {
                return Lists.newArrayList(Iterables.filter(from, pred));
            }
        });
    }

    public static <T> Parser<List<T>> flipped (Parser<List<T>> parser)
    {
        return parser.map(new Map<List<T>, List<T>>() {
            @Override
            public List<T> map (List<T> from)
            {
                return Lists.reverse(from);
            }
        });
    }

    @SafeVarargs
    public static <T> Parser<List<T>> flipped (Parser<T>... parsers)
    {
        return flipped(list(parsers));
    }

    private Parsers2 () {}
}
