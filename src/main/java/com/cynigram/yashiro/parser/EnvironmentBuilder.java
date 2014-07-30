package com.cynigram.yashiro.parser;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

public class EnvironmentBuilder implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String blockStartSequence = "{%";
    private String blockEndSequence = "%}";
    private String variableStartSequence = "{{";
    private String variableEndSequence = "}}";
    private String commentStartSequence = "{#";
    private String commentEndSequence = "#}";
    private String lineStatementPrefixSequence = null;
    private String lineCommentPrefixSequence = null;
    private boolean trimBlocks = false;
    private boolean lstripBlocks = false;
    private boolean keepTrailingNewline = false;

    public EnvironmentBuilder blockStartSequence (String blockStartSequence)
    {
        this.blockStartSequence = checkNotNull(blockStartSequence);
        return this;
    }

    public EnvironmentBuilder blockEndSequence (String blockEndSequence)
    {
        this.blockEndSequence = checkNotNull(blockEndSequence);
        return this;
    }

    public EnvironmentBuilder variableStartSequence (String variableStartSequence)
    {
        this.variableStartSequence = checkNotNull(variableStartSequence);
        return this;
    }

    public EnvironmentBuilder variableEndSequence (String variableEndSequence)
    {
        this.variableEndSequence = checkNotNull(variableEndSequence);
        return this;
    }

    public EnvironmentBuilder commentStartSequence (String commentStartSequence)
    {
        this.commentStartSequence = checkNotNull(commentStartSequence);
        return this;
    }

    public EnvironmentBuilder commentEndSequence (String commentEndSequence)
    {
        this.commentEndSequence = checkNotNull(commentEndSequence);
        return this;
    }

    public EnvironmentBuilder lineStatementPrefixSequence (String lineStatementPrefixSequence)
    {
        this.lineStatementPrefixSequence = lineStatementPrefixSequence;
        return this;
    }

    public EnvironmentBuilder noLineStatementPrefixSequence ()
    {
        lineStatementPrefixSequence = null;
        return this;
    }

    public EnvironmentBuilder lineCommentPrefixSequence (String lineCommentPrefixSequence)
    {
        this.lineCommentPrefixSequence = lineCommentPrefixSequence;
        return this;
    }

    public EnvironmentBuilder noLineCommentPrefixSequence ()
    {
        lineCommentPrefixSequence = null;
        return this;
    }

    public EnvironmentBuilder trimBlocks ()
    {
        trimBlocks = true;
        return this;
    }

    public EnvironmentBuilder noTrimBlocks ()
    {
        trimBlocks = false;
        return this;
    }

    public EnvironmentBuilder lstripBlocks ()
    {
        lstripBlocks = true;
        return this;
    }

    public EnvironmentBuilder noLstripBlocks ()
    {
        lstripBlocks = false;
        return this;
    }

    public EnvironmentBuilder keepTrailingNewline ()
    {
        keepTrailingNewline = true;
        return this;
    }

    public EnvironmentBuilder noKeepTrailingNewline ()
    {
        keepTrailingNewline = false;
        return this;
    }

    public Environment build ()
    {
        return new Environment(
                blockStartSequence, blockEndSequence,
                variableStartSequence, variableEndSequence,
                commentStartSequence, commentEndSequence,
                lineStatementPrefixSequence,
                lineCommentPrefixSequence,
                trimBlocks, lstripBlocks,
                keepTrailingNewline);
    }
}
