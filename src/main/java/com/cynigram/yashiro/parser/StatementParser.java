package com.cynigram.yashiro.parser;

import com.cynigram.yashiro.ast.BodyListNode;
import com.cynigram.yashiro.ast.StmtNode;
import org.codehaus.jparsec.Parser;

import java.util.Set;

public interface StatementParser
{
    public static interface Cont
    {
        public Parser<String> name ();
        public Parser<String> name (String name);
        public Parser<BodyListNode> cont (Set<String> clauseNames);
    }

    public String getPackage ();
    public String getName ();

    public Parser<? extends StmtNode> parser (Cont cont);
}
