import org.junit.Test
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ColorTest {
    @Test
    fun printColor() {
        val file = File("src/main/res/drawable-nodpi/ic_tagai_logo.png")
        if (file.exists()) {
            val img: BufferedImage = ImageIO.read(file)
            val rgb = img.getRGB(0, 0)
            val r = (rgb shr 16) and 0xFF
            val g = (rgb shr 8) and 0xFF
            val b = rgb and 0xFF
            println(String.format("COLOR: #%02X%02X%02X", r, g, b))
        } else {
            println("File not found")
        }
    }
}
