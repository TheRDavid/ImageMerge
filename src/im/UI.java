package im;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import im.FileDrop.Listener;

public class UI extends JFrame {

	private Font uiFont = new Font("Arial", Font.PLAIN, 26);
	private JPanel left = new JPanel(new BorderLayout(10, 10)), mid = new JPanel(new BorderLayout(10, 10)),
			right = new JPanel(new BorderLayout(10, 10)), controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	private JLabel leftIcon = new JLabel() {
		@Override
		protected void paintComponent(java.awt.Graphics g) {
			super.paintComponent(g);
			if (leftImage == null) {
				g.setColor(Color.BLUE);
				g.setFont(uiFont);
				String txt = "Drag image that should be merged";
				int wdth = g.getFontMetrics().stringWidth(txt);
				g.drawString(txt, leftIcon.getWidth() / 2 - wdth / 2, leftIcon.getHeight() / 2 + 12);
			}
		};
	}, rightIcon = new JLabel() {
		@Override
		protected void paintComponent(java.awt.Graphics g) {
			super.paintComponent(g);
			if (rightImage == null) {
				g.setColor(Color.BLUE);
				g.setFont(uiFont);
				String txt = "Drag image that should be merged";
				int wdth = g.getFontMetrics().stringWidth(txt);
				g.drawString(txt, leftIcon.getWidth() / 2 - wdth / 2, leftIcon.getHeight() / 2 + 12);
			}
		};
	}, midIcon = new JLabel() {
		@Override
		protected void paintComponent(java.awt.Graphics g) {
			super.paintComponent(g);
			if (midImage == null) {
				g.setColor(Color.BLUE);
				g.setFont(uiFont);
				String txt = "Drag image for reconstruction";
				int wdth = g.getFontMetrics().stringWidth(txt);
				g.drawString(txt, leftIcon.getWidth() / 2 - wdth / 2, leftIcon.getHeight() / 2 + 12);
			}
		};
	};
	private JButton mergeB = new JButton("merge"), recB = new JButton("reconstruct"), saveL = new JButton("Save"),
			saveR = new JButton("Save"), saveM = new JButton("Save"), clear = new JButton("clear");
	private BufferedImage rightImage, leftImage, midImage;
	private JFileChooser jfc = new JFileChooser();

	public UI() {
		left.setPreferredSize(new Dimension(640, 640));
		right.setPreferredSize(new Dimension(640, 640));
		mid.setPreferredSize(new Dimension(640, 640));
		right.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
		mid.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
		left.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
		clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				leftImage = null;
				rightImage = null;
				midImage = null;
				leftIcon.setIcon(null);
				rightIcon.setIcon(null);
				midIcon.setIcon(null);
				leftIcon.repaint();
				rightIcon.repaint();
				midIcon.repaint();

			}
		});

		mergeB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (rightImage == null || leftImage == null) {
					JOptionPane.showMessageDialog(UI.this, "Needs 2 Images");
					return;
				}
				midImage = Start.merge(leftImage, rightImage);
				midIcon.setIcon(new ImageIcon(midImage.getScaledInstance(640,
						(int) (midImage.getHeight() / ((double) midImage.getWidth() / 640)), Image.SCALE_DEFAULT)));
				midIcon.repaint();
			}
		});

		recB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (midImage == null) {
					JOptionPane.showMessageDialog(UI.this, "No Source");
					return;
				}
				BufferedImage[] recs = Start.split(midImage);

				rightImage = recs[1];
				rightIcon.setIcon(new ImageIcon(rightImage.getScaledInstance(640,
						(int) (rightImage.getHeight() / ((double) rightImage.getWidth() / 640)), Image.SCALE_DEFAULT)));
				rightIcon.repaint();
				leftImage = recs[0];
				leftIcon.setIcon(new ImageIcon(leftImage.getScaledInstance(640,
						(int) (leftImage.getHeight() / ((double) leftImage.getWidth() / 640)), Image.SCALE_DEFAULT)));
				leftIcon.repaint();
			}
		});

		saveL.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				save(leftImage);
			}
		});
		saveR.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				save(rightImage);
			}
		});
		saveM.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				save(midImage);
			}
		});

		left.add(leftIcon, BorderLayout.CENTER);
		mid.add(midIcon, BorderLayout.CENTER);
		right.add(rightIcon, BorderLayout.CENTER);

		left.add(saveL, BorderLayout.SOUTH);
		mid.add(saveM, BorderLayout.SOUTH);
		right.add(saveR, BorderLayout.SOUTH);

		new FileDrop(left, new Listener() {

			@Override
			public void filesDropped(File[] files) {
				if (files.length > 2) {
					JOptionPane.showMessageDialog(UI.this, "Max 2 Files!");
					return;
				}
				if (files.length >= 1) {
					leftImage = read(files[0]);
					leftIcon.setIcon(new ImageIcon(leftImage.getScaledInstance(640,
							(int) (leftImage.getHeight() / ((double) leftImage.getWidth() / 640)),
							Image.SCALE_DEFAULT)));
					leftIcon.repaint();
				}

				if (files.length == 2) {
					rightImage = read(files[1]);
					rightIcon.setIcon(new ImageIcon(rightImage.getScaledInstance(640,
							(int) (rightImage.getHeight() / ((double) rightImage.getWidth() / 640)),
							Image.SCALE_DEFAULT)));
					rightIcon.repaint();

					midImage = Start.merge(rightImage, leftImage);
					midIcon.setIcon(new ImageIcon(midImage.getScaledInstance(640,
							(int) (midImage.getHeight() / ((double) midImage.getWidth() / 640)), Image.SCALE_DEFAULT)));
					midIcon.repaint();
				}

			}
		});
		new FileDrop(right, new Listener() {

			@Override
			public void filesDropped(File[] files) {
				if (files.length > 2) {
					JOptionPane.showMessageDialog(UI.this, "Max 2 Files!");
					return;
				}
				if (files.length >= 1) {
					rightImage = read(files[0]);
					rightIcon.setIcon(new ImageIcon(rightImage.getScaledInstance(640,
							(int) (rightImage.getHeight() / ((double) rightImage.getWidth() / 640)),
							Image.SCALE_DEFAULT)));
					rightIcon.repaint();
				}

				if (files.length == 2) {
					leftImage = read(files[1]);
					leftIcon.setIcon(new ImageIcon(leftImage.getScaledInstance(640,
							(int) (leftImage.getHeight() / ((double) leftImage.getWidth() / 640)),
							Image.SCALE_DEFAULT)));
					leftIcon.repaint();

					midImage = Start.merge(rightImage, leftImage);
					midIcon.setIcon(new ImageIcon(midImage.getScaledInstance(640,
							(int) (midImage.getHeight() / ((double) midImage.getWidth() / 640)), Image.SCALE_DEFAULT)));
					midIcon.repaint();
				}

			}
		});
		new FileDrop(mid, new Listener() {

			@Override
			public void filesDropped(File[] files) {
				if (files.length > 2) {
					JOptionPane.showMessageDialog(UI.this, "Max 2 Files!");
					return;
				}
				if (files.length == 1) {
					midImage = read(files[0]);
					midIcon.setIcon(new ImageIcon(midImage.getScaledInstance(640,
							(int) (midImage.getHeight() / ((double) midImage.getWidth() / 640)), Image.SCALE_DEFAULT)));
					midIcon.repaint();
				}

				if (files.length == 2) {
					leftImage = read(files[0]);
					leftIcon.setIcon(new ImageIcon(leftImage.getScaledInstance(640,
							(int) (leftImage.getHeight() / ((double) leftImage.getWidth() / 640)),
							Image.SCALE_DEFAULT)));
					leftIcon.repaint();
					rightImage = read(files[1]);
					rightIcon.setIcon(new ImageIcon(rightImage.getScaledInstance(640,
							(int) (rightImage.getHeight() / ((double) rightImage.getWidth() / 640)),
							Image.SCALE_DEFAULT)));
					rightIcon.repaint();
				}

			}
		});
		add(left, BorderLayout.WEST);
		add(mid, BorderLayout.CENTER);
		add(right, BorderLayout.EAST);
		add(controls, BorderLayout.SOUTH);

		controls.add(mergeB);
		controls.add(recB);
		controls.add(clear);

		setSize(1920, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationByPlatform(true);
		setTitle("HI");
		setVisible(true);
	}

	private void save(BufferedImage img) {
		if (img == null) {
			JOptionPane.showMessageDialog(UI.this, "No Image");
			return;
		}
		try {
			jfc.showSaveDialog(UI.this);
			if (!jfc.getSelectedFile().getAbsolutePath().endsWith(".png"))
				jfc.setSelectedFile(new File(jfc.getSelectedFile().getAbsolutePath() + ".png"));
			if (jfc.getSelectedFile().exists()
					&& JOptionPane.showConfirmDialog(UI.this, "File already exists! Continue?", "Wait a sec",
							JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
				return;
			}
			ImageIO.write(img, "png", jfc.getSelectedFile());
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(UI.this, "Could not save " + jfc.getSelectedFile().getName());
			ex.printStackTrace();
		}
	}

	private BufferedImage read(File f) {
		BufferedImage a = null;
		try {
			a = ImageIO.read(f);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(UI.this, "Failed to read " + f.getName());
			return null;
		}

		return a;
	}
}
