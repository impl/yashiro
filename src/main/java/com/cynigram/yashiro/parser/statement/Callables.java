package com.cynigram.yashiro.parser.statement;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

class Callables
{
    private String name;
    private String packageName;

    public String getPackageName ()
    {
        return packageName;
    }

    public void setPackageName (String packageName)
    {
        Preconditions.checkNotNull(packageName, "packageName must not be null");
        Preconditions.checkArgument(!packageName.isEmpty(), "packageName must not be an empty string");
        for (String check : Splitter.on('.').split(packageName)) {
            Preconditions.checkArgument(!check.isEmpty(), "every packageName component must not be an empty string");
            Preconditions.checkArgument(Character.isJavaIdentifierStart((int)check.charAt(0)), "every packageName component must be a valid Java identifier (could not parse '" + check + "')");
            Preconditions.checkArgument(Iterables.all(Lists.charactersOf(check.substring(1)), new Predicate<Character>() {
                @Override
                public boolean apply (Character ch)
                {
                    return Character.isJavaIdentifierPart((int)ch);
                }
            }), "every packageName component must be a valid Java identifier (could not parse '" + check + "')");
        }

        this.packageName = packageName;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        Preconditions.checkNotNull(name, "name must not be null");
        Preconditions.checkArgument(!name.isEmpty(), "name must not be an empty string");
        Preconditions.checkArgument(Character.isJavaIdentifierStart((int)name.charAt(0)), "name must be a valid Java identifier");
        Preconditions.checkArgument(Iterables.all(Lists.charactersOf(name.substring(1)), new Predicate<Character>() {
            @Override
            public boolean apply (Character ch)
            {
                return Character.isJavaIdentifierPart((int)ch);
            }
        }), "name must be a valid Java identifier");

        this.name = name;
    }
}
