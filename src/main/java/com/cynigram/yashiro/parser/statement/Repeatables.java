package com.cynigram.yashiro.parser.statement;

class Repeatables
{
    private boolean optional = false;
    private boolean many = false;

    public boolean isOptional ()
    {
        return optional;
    }

    public void setOptional (boolean optional)
    {
        this.optional = optional;
    }

    public boolean isMany ()
    {
        return many;
    }

    public void setMany (boolean many)
    {
        this.many = many;
    }
}
