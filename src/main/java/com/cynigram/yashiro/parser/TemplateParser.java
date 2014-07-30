package com.cynigram.yashiro.parser;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Token;

import java.util.List;

import static com.cynigram.yashiro.parser.TemplateTerminals.term;

public class TemplateParser
{
    private final Environment environment;
    private final Parser<List<Token>> scanner;

    public TemplateParser (Environment environment)
    {
        /* Terms aren't allowed for now. */
        boolean valid = true;

        try {
            term(environment.getBlockEndSequence());
            valid = false;
        } catch (IllegalArgumentException iae) {}

        if (!valid) {
            throw new IllegalArgumentException("Block end sequence '" + environment.getBlockEndSequence() + "' cannot be used");
        }

        try {
            term(environment.getVariableEndSequence());
            valid = false;
        } catch (IllegalArgumentException iae) {}

        if (!valid) {
            throw new IllegalArgumentException("Variable end sequence '" + environment.getVariableEndSequence() + "' cannot be used");
        }

        try {
            term(environment.getCommentEndSequence());
            valid = false;
        } catch (IllegalArgumentException iae) {}

        if (!valid) {
            throw new IllegalArgumentException("Comment end sequence '" + environment.getCommentEndSequence() + "' cannot be used");
        }

        this.environment = environment;

        /* From the environment. */
        scanner = new Scanner(environment).getScanner();
    }
}
