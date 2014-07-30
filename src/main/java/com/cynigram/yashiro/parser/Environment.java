package com.cynigram.yashiro.parser;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

public class Environment implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String blockStartSequence;
    private final String blockEndSequence;
    private final String variableStartSequence;
    private final String variableEndSequence;
    private final String commentStartSequence;
    private final String commentEndSequence;
    private final String lineStatementPrefixSequence;
    private final String lineCommentPrefixSequence;
    private final boolean trimBlocks;
    private final boolean lstripBlocks;
    private final boolean keepTrailingNewline;

    public Environment (String blockStartSequence, String blockEndSequence, String variableStartSequence, String variableEndSequence, String commentStartSequence, String commentEndSequence, String lineStatementPrefixSequence, String lineCommentPrefixSequence, boolean trimBlocks, boolean lstripBlocks, boolean keepTrailingNewline)
    {
        this.blockStartSequence = checkNotNull(blockStartSequence);
        this.blockEndSequence = checkNotNull(blockEndSequence);
        this.variableStartSequence = checkNotNull(variableStartSequence);
        this.variableEndSequence = checkNotNull(variableEndSequence);
        this.commentStartSequence = checkNotNull(commentStartSequence);
        this.commentEndSequence = checkNotNull(commentEndSequence);
        this.lineStatementPrefixSequence = lineStatementPrefixSequence;
        this.lineCommentPrefixSequence = lineCommentPrefixSequence;
        this.trimBlocks = trimBlocks;
        this.lstripBlocks = lstripBlocks;
        this.keepTrailingNewline = keepTrailingNewline;
    }

    public String getBlockStartSequence ()
    {
        return blockStartSequence;
    }

    public String getBlockEndSequence ()
    {
        return blockEndSequence;
    }

    public String getVariableStartSequence ()
    {
        return variableStartSequence;
    }

    public String getVariableEndSequence ()
    {
        return variableEndSequence;
    }

    public String getCommentStartSequence ()
    {
        return commentStartSequence;
    }

    public String getCommentEndSequence ()
    {
        return commentEndSequence;
    }

    public String getLineStatementPrefixSequence ()
    {
        return lineStatementPrefixSequence;
    }

    public String getLineCommentPrefixSequence ()
    {
        return lineCommentPrefixSequence;
    }

    public boolean isTrimBlocks ()
    {
        return trimBlocks;
    }

    public boolean isLstripBlocks ()
    {
        return lstripBlocks;
    }

    public boolean isKeepTrailingNewline ()
    {
        return keepTrailingNewline;
    }
}
