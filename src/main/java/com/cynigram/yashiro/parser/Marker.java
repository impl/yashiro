package com.cynigram.yashiro.parser;

import java.io.Serializable;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.misc.Mapper;

import com.cynigram.yashiro.ast.Node;

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
}
