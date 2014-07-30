package com.cynigram.yashiro.ast;

import java.io.Serializable;

import com.google.common.collect.MutableClassToInstanceMap;

public abstract class Node implements Serializable
{
    private static final long serialVersionUID = 1L;

    private MutableClassToInstanceMap<Serializable> annotations = MutableClassToInstanceMap.create();

    public <T extends Serializable> T getAnnotation (Class<T> type)
    {
        return annotations.getInstance(type);
    }

    public <T extends Serializable> T putAnnotation (Class<T> type, T value)
    {
        return annotations.putInstance(type, value);
    }
}
