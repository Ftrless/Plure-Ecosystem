package com.enthusiasm.plurelogger.actions;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import com.enthusiasm.plurelogger.utils.TextColorPallet;

public class BlockBreakActionType extends BlockChangeActionType {
    private static final String IDENTIFIER = "block-break";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Text getObjectMessage(ServerCommandSource source) {
        return Text.translatable(
                        Util.createTranslationKey(
                                getTranslationType(),
                                getOldObjectIdentifier()
                        )
                ).setStyle(TextColorPallet.getSecondaryVariant())
                .styled(style -> style.withHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Text.literal(getOldObjectIdentifier().toString())
                        )
                ));
    }
}
