package com.cynigram.yashiro.parser;

import com.google.common.base.Objects;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

public class Fragment<T extends Serializable> implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final T value;
    private final Tag<? super T> tag;

    public Fragment (T value, Tag<? super T> tag)
    {
        this.value = checkNotNull(value);
        this.tag = checkNotNull(tag);
    }

    public T getValue ()
    {
        return value;
    }

    public Tag<? super T> getTag ()
    {
        return tag;
    }

    @Override
    public int hashCode ()
    {
        return Objects.hashCode(value, tag);
    }

    @Override
    public boolean equals (Object obj)
    {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (obj.getClass() != getClass()) {
            return false;
        }

        Fragment<?> other = (Fragment<?>)obj;
        return Objects.equal(other.value, value) && Objects.equal(other.tag, tag);
    }

    @Override
    public String toString ()
    {
        return value.toString();
    }
}
