package com.cynigram.yashiro.parser;

import com.cynigram.yashiro.ast.Node;
import com.google.common.base.Objects;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.misc.Mapper;

import java.io.Serializable;

public final class Marker implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static <T extends Node> Parser<T> mark (Parser<T> parser)
    {
        return new Mapper<T>() {
            @SuppressWarnings("unused")
            public T map (int index, T node)
            {
                node.putAnnotation(Marker.class, new Marker(index));
                return node;
            }
        }.sequence(Parsers.INDEX, parser);
    }

    private final int index;

    private Marker (int index)
    {
        this.index = index;
    }

    public int getIndex ()
    {
        return index;
    }

    @Override
    public String toString ()
    {
        return Objects.toStringHelper(this)
                .add("index", index)
                .toString();
    }
}
