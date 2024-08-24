package com.enthusiasm.plurecore.utils.text;

import java.util.stream.Collector;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class TextJoiner {
    private final MutableText prepend;
    private final MutableText append;
    private final MutableText join;

    private MutableText result;

    private TextJoiner(Text prepend, Text append, Text join) {
        this.prepend = prepend.copy();
        this.append = append.copy();
        this.join = join.copy();
    }

    private TextJoiner(Text prepend, Text append, Text join, MutableText result) {
        this.prepend = prepend.copy();
        this.append = append.copy();
        this.join = join.copy();
        this.result = result;
    }

    private void accumulator(Text text) {
        if (result == null) {
            result = prepend.copy()
                    .append(text)
                    .append(append);

            return;
        }

        result = result
                .append(join)
                .append(prepend)
                .append(text)
                .append(append);
    }

    private TextJoiner combiner(TextJoiner b) {
        return new TextJoiner(
                prepend,
                append,
                join,
                result.append(join).append(b.result)
        );
    }

    private MutableText finisher() {
        return result;
    }

    public static Collector<Text, ?, MutableText> collector(Text prepend, Text append, Text join) {
        return Collector.of(
                () -> new TextJoiner(prepend, append, join),
                TextJoiner::accumulator,
                TextJoiner::combiner,
                TextJoiner::finisher
        );
    }
}
