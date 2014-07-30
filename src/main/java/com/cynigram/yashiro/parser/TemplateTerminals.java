package com.cynigram.yashiro.parser;

import org.codehaus.jparsec.*;
import org.codehaus.jparsec.Tokens.Fragment;
import org.codehaus.jparsec.Tokens.Tag;
import org.codehaus.jparsec.functors.Map;

import static org.codehaus.jparsec.misc.Mapper._;

public final class TemplateTerminals
{
    public static final Parser<?> term (String name)
    {
        return equiv(Tag.RESERVED, name);
    }

    public static final Parser<?> term (String... name)
    {
        if (name.length == 0) {
            return Parsers.never();
        } else if (name.length == 1) {
            return id(name[0]);
        }

        Parser<?>[] parsers = new Parser<?>[name.length];
        for (int i = 0; i < name.length; i++)
        {
            parsers[i] = term(name[i]);
        }
        return Parsers.sequence(parsers);
    }

    public static final Parser<?> id (final String name)
    {
        return equiv(Tag.IDENTIFIER, name);
    }

    public static final Parser<?> id (final String... name)
    {
        if (name.length == 0) {
            return Parsers.never();
        } else if (name.length == 1) {
            return id(name[0]);
        }

        Parser<?>[] parsers = new Parser<?>[name.length];
        for (int i = 0; i < name.length; i++)
        {
            parsers[i] = id(name[i]);
        }
        return Parsers.sequence(parsers);
    }

    public static final Parser<TemplateTag> statementBlock ()
    {
        return Terminals.fragment(TemplateTag.STATEMENT_BLOCK).retn(TemplateTag.STATEMENT_BLOCK);
    }

    public static final Parser<TemplateTag> variableBlock ()
    {
        return Terminals.fragment(TemplateTag.VARIABLE_BLOCK).retn(TemplateTag.VARIABLE_BLOCK);
    }

    public static final Parser<?> strip (boolean strip)
    {
        return equiv(TemplateTag.STRIP, strip ? "-" : "+");
    }

    public static final Parser<Stripping> strip ()
    {
        return Terminals.fragment(TemplateTag.STRIP).map(new Map<String, Stripping>()
        {
            @Override
            public Stripping map(String s)
            {
                if (s.equals("-"))
                    return Stripping.ALL;
                else if (s.equals("+"))
                    return Stripping.NONE;
                else if (s.equals("!"))
                    return Stripping.LINE;

                throw new IllegalArgumentException(s);
            }
        });
    }

    public static final Parser<String> data ()
    {
        return Terminals.fragment(TemplateTag.DATA);
    }

    static final Parser<?> equiv (final Object tag, final String name)
    {
        return _(Parsers.token(new TokenMap<String>() {
            @Override
            public String map (Token token)
            {
                if (!(token.value() instanceof Fragment)) {
                    return null;
                }

                Fragment frag = (Fragment)token.value();
                if (frag.tag() == tag && (name == null || frag.text().equals(name))) {
                    return frag.text();
                } else {
                    return null;
                }
            }

            @Override
            public String toString ()
            {
                return name == null ? tag.toString() : name;
            }
        }));
    }

    static Map<String, Fragment> fragment (final Object tag)
    {
        return new Map<String, Fragment>() {
            @Override
            public Fragment map (String from)
            {
                return new Fragment(from, tag);
            }

            @Override
            public String toString ()
            {
                return String.valueOf(tag);
            }
        };
    }

    private TemplateTerminals ()
    {}
}
