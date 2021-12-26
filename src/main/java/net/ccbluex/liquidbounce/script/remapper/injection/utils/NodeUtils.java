package net.ccbluex.liquidbounce.script.remapper.injection.utils;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public class NodeUtils {
    public static final NodeUtils INSTANCE = new NodeUtils();

    @NotNull
    public final InsnList toNodes(AbstractInsnNode... nodes) {
        InsnList insnList = new InsnList();
        for (AbstractInsnNode node : nodes) {
            insnList.add(node);
        }
        return insnList;
    }

}
