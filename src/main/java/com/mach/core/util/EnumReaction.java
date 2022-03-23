package com.mach.core.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EnumReaction {

    NEEDLUCAS("Necesito las lucas", ConstantReaction.REACTION_REMEMBER),
    DONTFORGETME("No te olvides de mí...", ConstantReaction.REACTION_REMEMBER),
    WAITEDALOT("Ya esperé bastante", ConstantReaction.REACTION_REMEMBER),
    REMEMBERPAYTO("Recuerda pagarle a ", ConstantReaction.REACTION_REMEMBER),
    DONTREMEMBERTHIS("No recuerdo esto", ConstantReaction.REACTION_REJECT),
    AMOUNTDOESNTCORRESPOND("El monto no corresponde", ConstantReaction.REACTION_REJECT),
    ALREADYPAID("Esto ya lo pagué", ConstantReaction.REACTION_REJECT),
    REJECTIONWHITOUTREASON("Rechazo sin motivo", ConstantReaction.REACTION_REJECT),
    RECEIVED_THANKS("Recibido, ¡gracias!", ConstantReaction.REACTION_PAY),
    MONEY("¡Dinero $$$!", ConstantReaction.REACTION_PAY),
    HOW_FAST("¡Qué rápido!", ConstantReaction.REACTION_PAY),
    HAD_A_GOOD_TIME("¡Lo pasamos bien!", ConstantReaction.REACTION_PAY);

    private String reaction;
    private String typeReaction;

    EnumReaction(String reaction, String typeReaction) {
        this.reaction = reaction;
        this.typeReaction = typeReaction;
    }

    public String getReaction() {
        return reaction;
    }

    public String getTypeReaction() {
        return typeReaction;
    }

    public static List<String> getReactions(String typeReaction) {
        return Arrays.stream(EnumReaction.values())
                .filter(reaction -> reaction.getTypeReaction().equals(typeReaction))
                .map(EnumReaction::getReaction)
                .collect(Collectors.toList());
    }

    private static class ConstantReaction {
        private static final String REACTION_REMEMBER = "Remember";
        private static final String REACTION_REJECT = "Reject";
        private static final String REACTION_PAY = "Pay";
    }
}
