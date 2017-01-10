package im;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * This is like... not optimized at all, you may for instance not create color
 * objects or get data via getRGB()... unless you're a lazy ass like me
 * 
 * @author David Rosenbusch
 *
 */
public class ImgTools {
	// is
	// to
	// just
	// take the
	// mid-value for
	// the lost bits
	static int xcheck = 100, ycheck = 100, fillrb = 0, fillg = 0;

	public static void main(String[] args) throws IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new UI();
	}

	public static BufferedImage[] split(BufferedImage m) {
		BufferedImage aImg = new BufferedImage(m.getWidth(), m.getHeight(), BufferedImage.TYPE_INT_ARGB);
		BufferedImage bImg = new BufferedImage(m.getWidth(), m.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int i0 = 0; i0 < m.getWidth(); i0++)
			for (int i1 = 0; i1 < m.getHeight(); i1++) {
				int pix = m.getRGB(i0, i1);

				int ar = ((pix >> 22) & 0b11000000);
				int ag = ((pix >> 18) & 0b11100000);
				int ab = ((pix >> 12) & 0b11000000);
				int aa = (pix & 0b10000000000000000000000000000000) == 0 ? 0 : 255;
				
				int br = ((pix >> 22) & 0b00110000) << 2;
				int bg = ((pix >> 18) & 0b00011100) << 3;
				int bb = ((pix >> 12) & 0b00110000) << 2;
				int ba = (pix & 0b01000000000000000000000000000000) == 0 ? 0 : 255;

				int aVals = (aa << 24) | ((ar) << 16) | ((ag) << 8) | (ab);
				int bVals = (ba << 24) | ((br) << 16) | ((bg) << 8) | (bb);

				aImg.setRGB(i0, i1, aVals);
				bImg.setRGB(i0, i1, bVals);

				if (i0 == xcheck && i1 == ycheck) {
					//System.out.println("\nCompressed layer - \tr: " + Integer.toBinaryString(col.getRed()) + "\t\tg: "
					//		+ Integer.toBinaryString(col.getGreen()) + "\t\tb: " + Integer.toBinaryString(col.getBlue())
					//		+ " \t\ta: " + Integer.toBinaryString(col.getAlpha()));
					System.out.println("\nFirst layer - \t\tr: " + Integer.toBinaryString(ar) + " \t\tg: "
							+ Integer.toBinaryString(ag) + ", \t\tb: " + Integer.toBinaryString(ab) + ", \t\ta: "
							+ Integer.toBinaryString(aa));
					System.out.println("Second layer - \t\tr: " + Integer.toBinaryString(br) + " \t\tg: "
							+ Integer.toBinaryString(bg) + ", \t\tb: " + Integer.toBinaryString(bb) + ", \t\ta: "
							+ Integer.toBinaryString(ba));
					System.out.println("layer 1 output: \t" + Integer.toBinaryString(aVals));
					System.out.println("layer 2 output: \t" + Integer.toBinaryString(bVals));

				}
			}

		return new BufferedImage[] { aImg, bImg };
	}

	public static BufferedImage merge(BufferedImage a, BufferedImage b) {
		BufferedImage mergedImage = new BufferedImage(a.getWidth(), a.getHeight(), BufferedImage.TYPE_INT_ARGB);
		if (a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight()) {
			JOptionPane.showMessageDialog(null, "Images must have same bounds", "Ugh...", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		for (int i0 = 0; i0 < a.getWidth(); i0++)
			for (int i1 = 0; i1 < a.getHeight(); i1++) {
				int apix = a.getRGB(i0, i1), bpix = b.getRGB(i0, i1);
				Color acol = new Color(apix, true), bcol = new Color(bpix, true);
				int ar = acol.getRed(), br = bcol.getRed(), ag = acol.getGreen(), bg = bcol.getGreen(),
						ab = acol.getBlue(), bb = bcol.getBlue(), aa = acol.getAlpha(), ba = bcol.getAlpha();

				if (i0 == xcheck && i1 == ycheck) {
					System.out.println("Colors @ " + xcheck + " x " + ycheck);
					System.out.println("Before compressing");
					System.out.println("First layer - \t\tr: " + Integer.toBinaryString(ar) + " \t\tg: "
							+ Integer.toBinaryString(ag) + ", \t\tb: " + Integer.toBinaryString(ab) + ", \t\ta: "
							+ Integer.toBinaryString(aa));
					System.out.println("Second layer - \t\tr: " + Integer.toBinaryString(br) + " \t\tg: "
							+ Integer.toBinaryString(bg) + ", \t\tb: " + Integer.toBinaryString(bb) + ", \t\ta: "
							+ Integer.toBinaryString(ba));

				}
				ar &= 0b11000000;
				ag &= 0b11100000;
				ab &= 0b11000000;
				aa = (aa == 0 ? 0 : 2);

				br = (br & 0b11000000) >> 2;
				bg = (bg & 0b11100000) >> 3;
				bb = (bb & 0b11000000) >> 2;
				ba = (ba == 0 ? 0 : 1);

				int nr = ar | br;
				int ng = ag | bg;
				int nb = ab | bb;
				int na = aa | ba;

				int newColor = (na << 30) | (nr << 22) | (ng << 18) | nb << 12;

				mergedImage.setRGB(i0, i1, newColor);

				if (i0 == xcheck && i1 == ycheck) {
					System.out.println("\nAfter compressing");

					System.out.println("First layer - \t\tr: " + Integer.toBinaryString(ar) + " \t\tg: "
							+ Integer.toBinaryString(ag) + ", \t\tb: " + Integer.toBinaryString(ab) + ", \t\ta: "
							+ Integer.toBinaryString(aa));
					System.out.println("Second layer - \t\tr: " + Integer.toBinaryString(br) + " \t\tg: "
							+ Integer.toBinaryString(bg) + ", \t\tb: " + Integer.toBinaryString(bb) + ", \t\ta: "
							+ Integer.toBinaryString(ba));
					System.out.println("\nMerged layer - \t\tr: " + Integer.toBinaryString(nr) + " \t\tg: "
							+ Integer.toBinaryString(ng) + ", \t\tb: " + Integer.toBinaryString(nb) + ", \t\ta: "
							+ Integer.toBinaryString(na));
					System.out.println("Merged output: \t\t" + Integer.toBinaryString(newColor));
				}
			}
		return mergedImage;
	}

}
