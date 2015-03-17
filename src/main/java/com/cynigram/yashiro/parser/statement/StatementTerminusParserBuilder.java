package com.cynigram.yashiro.parser.statement;

import com.cynigram.yashiro.ast.StmtNode;
import com.cynigram.yashiro.parser.Parsers2;
import com.cynigram.yashiro.parser.StatementParser;
import com.google.common.base.Supplier;
import com.google.common.collect.*;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.misc.Mapper;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class StatementTerminusParserBuilder
{
    private static ListMultimap<String, StatementMatch> result ()
    {
        return Multimaps.newListMultimap(Maps.<String, Collection<StatementMatch>>newHashMap(), new Supplier<List<StatementMatch>>()
        {
            @Override
            public List<StatementMatch> get()
            {
                return new LinkedList<>();
            }
        });
    }

    private Contents contents = new Contents();

    private final StatementWithBodyParserBuilder parent;
    private final List<RepeatableStatementWithBodyParserBuilder> subordinates;

    StatementTerminusParserBuilder (StatementWithBodyParserBuilder parent, List<RepeatableStatementWithBodyParserBuilder> subordinates)
    {
        checkNotNull(parent);
        checkNotNull(subordinates);

        this.parent = parent;
        this.subordinates = Lists.newArrayList(subordinates);
    }

    public StatementTerminusParserBuilder contains (ContentParserBuilder content)
    {
        contents.setContent(content);
        return this;
    }

    public <T extends StmtNode> StatementParser mapWith (final Map<StatementMatchMap, T> map)
    {
        checkNotNull(map);

        return new StatementParser()
        {
            @Override
            public String getPackage ()
            {
                return parent.getCallables().getPackageName();
            }

            @Override
            public String getName ()
            {
                return parent.getCallables().getName();
            }

            @Override
            public Parser<? extends StmtNode> parser (Cont cont)
            {
                List<Parser<Pair<String, StatementMatch>>> parsers = Lists.newArrayListWithExpectedSize(subordinates.size() + 1);

                parsers.add(Parsers2.pair(
                        cont.name(parent.getCallables().getName()),
                        Mapper.curry(StatementMatch.class).sequence(parent.getContents().getContent().parser(), cont.cont(statementsFrom(null)))));

                ListIterator<RepeatableStatementWithBodyParserBuilder> it = subordinates.listIterator();
                while (it.hasNext()) {
                    RepeatableStatementWithBodyParserBuilder builder = it.next();

                    parsers.add(Parsers2.pair(
                            cont.name(builder.getCallables().getName()),
                            Mapper.curry(StatementMatch.class).sequence(
                                    builder.getContents().getContent().parser(),
                                    cont.cont(statementsFrom(it.previousIndex())))));
                }

                /* Reduce. */
                Parser<ListMultimap<String, StatementMatch>> parser = Parsers.or(parsers).many1()
                        .map(new Map<List<Pair<String, StatementMatch>>, ListMultimap<String, StatementMatch>>()
                        {
                            @Override
                            public ListMultimap<String, StatementMatch> map(List<Pair<String, StatementMatch>> matches)
                            {
                                ListMultimap<String, StatementMatch> map = result();
                                for (Pair<String, StatementMatch> match : matches)
                                    map.put(match.getKey(), match.getValue());

                                return map;
                            }
                        });

                /* Add final condition. */
                return Mapper.curry(StatementMatchMap.class).sequence(parser, cont.name("end" + parent.getCallables().getName()).next(contents.getContent().parser())).map(map);
            }
        };
    }

    protected Set<String> statementsFrom (Integer index)
    {
        Iterator<RepeatableStatementWithBodyParserBuilder> it = subordinates.listIterator(index == null ? 0 : index);

        /* If there are no elements in the list, we can just return with the terminus. */
        if (!it.hasNext())
            return Collections.singleton("end" + parent.getCallables().getName());

        Set<String> statements = Sets.newHashSet();

        /* Retrieve the statements at the current index. */
        RepeatableStatementWithBodyParserBuilder current = it.next();

        /* If we're at the beginning of the list or the current statement is repeatable,
         * include it here. */
        if (index == null || current.getRepeatables().isMany())
            statements.add(current.getCallables().getName());

        if (index == null && !current.getRepeatables().isOptional()) {
            /* If we're at the beginning of the list and the first item isn't optional,
             * just stop here. */
        } else if (!it.hasNext()) {
            /* If there are no more elements in the list, we now return the terminus. */
            statements.add("end" + parent.getCallables().getName());
        } else {
            /* Then we include all following statements that are optional, as well as the
             * first non-optional statement. */
            do {
                current = it.next();
                statements.add(current.getCallables().getName());
            } while (it.hasNext() && current.getRepeatables().isOptional());

            /* If we reach the end of the list now, we can add the terminus. */
            if (!it.hasNext()) {
                statements.add("end" + parent.getCallables().getName());
            }
        }

        return statements;
    }
}
