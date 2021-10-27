package net.ccbluex.liquidinstruction

import net.ccbluex.liquidbounce.LiquidBounce
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.WindowConstants

fun main() {
    // Setup instruction frame
    val frame = JFrame("LiquidSense | Instruction.")
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.layout = BorderLayout()
    frame.isResizable = false
    frame.isAlwaysOnTop = true

    // Add instruction as label
    val label = JLabel(LiquidBounce::class.java.getResourceAsStream("/instructions.html").reader().readText())
    frame.add(label, BorderLayout.CENTER)

    // Pack frame
    frame.pack()

    // Set location to center of screen
    frame.setLocationRelativeTo(null)

    // Display instruction frame
    frame.isVisible = true
}