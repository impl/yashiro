package com.cynigram.yashiro.parser;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Token;
import org.codehaus.jparsec.TokenMap;
import org.codehaus.jparsec.functors.Map;

import java.io.Serializable;

import static org.codehaus.jparsec.misc.Mapper._;

public class Tag<T extends Serializable> implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final class StripTag extends Tag<Stripping>
    {
        protected StripTag ()
        {
            super("whitespace stripping instruction");
        }

        Map<String, Fragment<Stripping>> mapFromString ()
        {
            return new Map<String, Fragment<Stripping>>() {
                @Override
                public Fragment<Stripping> map (String from)
                {
                    if (from.equals("-"))
                        return new Fragment<>(Stripping.ALL, StripTag.this);
                    else if (from.equals("+"))
                        return new Fragment<>(Stripping.NONE, StripTag.this);
                    else if (from.equals("!"))
                        return new Fragment<>(Stripping.LINE, StripTag.this);

                    throw new IllegalArgumentException(from);
                }

                @Override
                public String toString ()
                {
                    return String.valueOf(this);
                }
            };
        }

        public Parser<?> parserOf (boolean expected)
        {
            return expected ? parserOf(Stripping.ALL) : parserOf(Stripping.NONE);
        }
    }

    public static final Tag<String> TERM = new Tag<>("reserved keyword");
    public static final Tag<String> NAME = new Tag<>("name");
    public static final Tag<Long> INTEGER = new Tag<>("integer");
    public static final Tag<String> DECIMAL = new Tag<>("decimal");
    public static final Tag<String> DATA = new Tag<>("data");
    public static final Tag<String> STATEMENT_BLOCK = new Tag<>("statement block");
    public static final Tag<String> VARIABLE_BLOCK = new Tag<>("variable block");
    public static final StripTag STRIP = new StripTag();

    private final String name;

    protected Tag (String name)
    {
        this.name = name;
    }

    public Fragment<T> apply (T in)
    {
        return new Fragment<>(in, this);
    }

    Map<T, Fragment<T>> map ()
    {
        return new Map<T, Fragment<T>>()
        {
            @Override
            public Fragment<T> map (T from)
            {
                return new Fragment<>(from, Tag.this);
            }

            @Override
            public String toString ()
            {
                return Tag.this.toString();
            }
        };
    }

    public Parser<T> parser ()
    {
        return Parsers.token(new TokenMap<T>()
        {
            @Override
            public T map (Token token)
            {
                if (!(token.value() instanceof Fragment<?>)) {
                    return null;
                }

                Fragment<?> frag = (Fragment<?>)token.value();
                if (frag.getTag() == Tag.this) {
                    @SuppressWarnings("unchecked")
                    T value = (T)frag.getValue();

                    return value;
                } else {
                    return null;
                }
            }

            @Override
            public String toString ()
            {
                return Tag.this.toString();
            }
        });
    }

    public <U extends T> Parser<?> parserOf (final U expected)
    {
        return _(Parsers.token(new TokenMap<U>()
        {
            @Override
            public U map (Token token)
            {
                if (!(token.value() instanceof Fragment)) {
                    return null;
                }

                Fragment<?> frag = (Fragment<?>)token.value();
                if (frag.getTag() == Tag.this && (expected == null || frag.getValue().equals(expected))) {
                    @SuppressWarnings("unchecked")
                    U value = (U)frag.getValue();

                    return value;
                } else {
                    return null;
                }
            }

            @Override
            public String toString ()
            {
                return expected == null ? Tag.this.toString() : expected.toString();
            }
        }));
    }

    @SafeVarargs
    public final <U extends T> Parser<?> parserOf (final U... expected)
    {
        if (expected.length == 0) {
            return Parsers.never();
        } else if (expected.length == 1) {
            return parserOf(expected[0]);
        }

        Parser<?>[] parsers = new Parser<?>[expected.length];
        for (int i = 0; i < expected.length; i++)
        {
            parsers[i] = parserOf(expected[i]);
        }
        return Parsers.sequence(parsers);
    }

    @Override
    public String toString ()
    {
        return this.name;
    }
}
