package net.ccbluex.liquidbounce.script.remapper.injection.utils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

/**
 * A bytecode class reader and writer util
 *
 * @author CCBlueX
 */
public class ClassUtils {
    /**
     * Read bytes to class node
     *
     * @param bytes ByteArray of class
     */
    public static ClassNode toClassNode(byte[] bytes) {
        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        return classNode;
    }

    /**
     * Write class node to bytes
     *
     * @param classNode ClassNode of class
     */
    public static byte[] toBytes(ClassNode classNode) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);

        return classWriter.toByteArray();
    }
}
