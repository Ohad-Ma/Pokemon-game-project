package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a very simple GUI class to present a game on a graph -
 * you are welcome to use this class - yet keep in mind that the code is not
 * well written in order to force you improve the code and not to take it "as
 * is".
 */
public class MyFrame extends JFrame {
	private static int bottom = 100, top = 10, delta = 10;
	private Arena _ar;
	private gameClient.util.Range2Range _w2f;
	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static int width = screenSize.width;
	private static int height = screenSize.height;
	private LoginPanel signIn;


	MyFrame(String a) {
		super(a);
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void update(Arena ar) {
		this._ar = ar;
		updateFrame();

	}

	private void updateFrame() {
		Range rx = new Range(20, this.getWidth() - 20);
		Range ry = new Range(this.getHeight() - 10, 150);
		Range2D frame = new Range2D(rx, ry);
		if (_ar != null) {
			directed_weighted_graph g = _ar.getGraph();
			_w2f = Arena.w2f(g, frame);
			setVisible(true);
		}
	}

	public void paint(Graphics g) {
		updateFrame();
		Image image;
		Graphics graphics;
		image = createImage(width, height);
		graphics = image.getGraphics();
		graphics.clearRect(0, 0, width, height);
		drawComponents(graphics);
		g.drawImage(image, 0, 0, this);
		//setVisible(true);
	}


	private void drawInfo(Graphics g) {
		if (_ar != null) {
			java.util.List<String> str = _ar.get_info();
			String dt = "none";
			for (int i = 0; i < str.size(); i++) {
				g.drawString(str.get(i) + " dt: " + dt, 100, 60 + i * 20);
			}
		}
	}

	private void drawPokemons(Graphics g) {
		if(_ar != null) {
			java.util.List<CL_Pokemon> fs = _ar.getPokemons();
			BufferedImage image = null;
			if (fs != null) {
				Iterator<CL_Pokemon> itr = fs.iterator();
				while (itr.hasNext()) {
					CL_Pokemon f = itr.next();
					Point3D c = f.getLocation();
					int r = 18;
					try {
						image = ImageIO.read(new File("data\\Pika.png"));
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (f.getType() < 0) {
						try {
							image = ImageIO.read(new File("data\\Mew.png"));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (c != null) {
						geo_location fp = this._w2f.world2frame(c);
						g.drawImage(image, (int) fp.x() - r, (int) fp.y() - r, this);
					}
				}
			}
		}
	}

	private void drawAgents(Graphics g) {
		if (_ar != null) {
			List<CL_Agent> rs = _ar.getAgents();
			int i = 0;
			while (rs != null && i < rs.size()) {
				geo_location c = rs.get(i).getLocation();
				i++;
				if (c != null) {
					geo_location fp = this._w2f.world2frame(c);
					try {
						BufferedImage image = ImageIO.read(new File("data\\Shrek.png"));
						g.drawImage(image, (int) fp.x() - 20, (int) fp.y() - 20, this);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}
	}

	private void timer(Graphics g) {
		g.setColor(new Color(0xFFFFFF));
		g.drawString("time remains:  ", 600, 110);
		g.setColor(new Color(0xFFFFFF));
		g.drawString("" + Ex2.time, 750, 110);
	}
	public void drawComponents(Graphics g) {
		super.paintComponents(g);
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("data\\Pokemon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Graphics2D gfx = (Graphics2D) g;
		gfx.drawImage(image, 0, 0, width, height, new Color(0x264B07), this);
		drawGraph(gfx);
		drawPokemons(gfx);
		drawAgents(gfx);
		drawInfo(gfx);
		timer(gfx);
	}

	private void drawNode(node_data n, int r, Graphics g) {
		geo_location pos = n.getLocation();
		geo_location fp = this._w2f.world2frame(pos);
		g.setColor(Color.orange);
		g.fillOval((int) fp.x() - r, (int) fp.y() - r, 3 * r, 3 * r);
		g.setFont(new Font("Type1_FONT", Font.TYPE1_FONT, 20));
		g.drawString("" + n.getKey(), (int) fp.x(), (int) fp.y() - 4 * r);
	}

	private void drawGraph(Graphics g) {
		if (_ar != null) {
			directed_weighted_graph gg = _ar.getGraph();
			Iterator<node_data> iter = gg.getV().iterator();
			while (iter.hasNext()) {
				node_data n = iter.next();
				g.setColor(Color.black);
				drawNode(n, 5, g);
				Iterator<edge_data> itr = gg.getE(n.getKey()).iterator();
				while (itr.hasNext()) {
					edge_data e = itr.next();
					g.setColor(Color.gray);
					drawEdge(e, g);

				}
			}
		}
	}

	private void drawEdge(edge_data e, Graphics g) {
		directed_weighted_graph gg = _ar.getGraph();
		geo_location s = gg.getNode(e.getSrc()).getLocation();
		geo_location d = gg.getNode(e.getDest()).getLocation();
		geo_location s0 = this._w2f.world2frame(s);
		geo_location d0 = this._w2f.world2frame(d);
		Graphics2D g1 = (Graphics2D) g;
		g1.setStroke(new BasicStroke(2));
		g1.setColor(Color.green);
		g1.drawLine((int) s0.x(), (int) s0.y(), (int) d0.x(), (int) d0.y());
		g.drawLine((int) s0.x(), (int) s0.y(), (int) d0.x(), (int) d0.y());
	}

	public void LoginPanel() {
		signIn = new LoginPanel();
		this.add(signIn);
		this.setVisible(true);
		this.setResizable(false);
		signIn.setVisible(false);
		this.setVisible(false);
	}

	public static class LoginPanel extends Component implements ActionListener {
		private JFrame frame;
		private JPanel panel;
		private JLabel idLabel;
		private JTextField id_text;
		private JLabel levelLabel;
		private JTextField level_text;
		private JButton login_button;
		private JButton about_button;
		private JButton exit_button;
		boolean flag;

		LoginPanel() {
			Font f = new Font("bold", Font.ROMAN_BASELINE, 25);
			flag = false;
			frame = new JFrame();
			panel = new JPanel();
			frame.setTitle("Pokemon game");
			frame.setSize(450, 200);
			frame.setResizable(false);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.add(panel);

			panel.setLayout(null);
			idLabel = new JLabel("USER ID :");
			idLabel.setFont(new Font("PLAIN", Font.BOLD, 13));
			idLabel.setForeground(new Color(0x47FF00));

			idLabel.setBounds(90, 20, 80, 25);
			panel.add(idLabel);

			id_text = new JTextField(10);
			id_text.setBounds(150, 20, 165, 25);
			panel.add(id_text);

			levelLabel = new JLabel("LEVEL :");
			levelLabel.setForeground(new Color(0x47FF00));
			levelLabel.setBounds(90, 50, 80, 25);
			panel.add(levelLabel);

			level_text = new JTextField("");
			level_text.setBounds(150, 50, 165, 25);
			panel.add(level_text);

			login_button = new JButton("START");
			login_button.setBounds(185, 90, 80, 25);
			login_button.setFont(new Font("PLAIN", Font.BOLD, 13));
			panel.add(login_button);
			login_button.addActionListener(this);
			drawBackGround();

			about_button = new JButton("ABOUT");
			about_button.setBounds(100, 90, 80, 25);
			about_button.setFont(new Font("PLAIN", Font.BOLD, 13));
			panel.add(about_button);
			about_button.addActionListener(this);

			exit_button = new JButton("EXIT");
			exit_button.setBounds(270, 90, 80, 25);
			exit_button.setFont(new Font("PLAIN", Font.BOLD, 13));
			panel.add(exit_button);
			exit_button.addActionListener(this);
			drawBackGround();

			frame.setVisible(true);

		}


		private void drawBackGround() {
			JLabel temp = new JLabel();
			temp.setIcon(new ImageIcon("data\\shrekl.png"));
			temp.setBounds(0, 0, 450, 200);
			this.panel.add(temp);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String id = id_text.getText();
			String level = level_text.getText();
			if (e.getSource() == exit_button){
				System.exit(0);
			}else if (e.getSource() == about_button){
				JOptionPane.showMessageDialog(this, "Authors of the game Saeed Esawi and Ohad Maday. Hope you enjoy the game!", "About", JOptionPane.INFORMATION_MESSAGE);
			}else {
				if (!numberOrNot(level) && !numberOrNot(id)) {
					JOptionPane.showMessageDialog(this, "Both fields are incorrect! Please enter a number", "Error", JOptionPane.ERROR_MESSAGE);
				} else if (!numberOrNot(id)) {
					JOptionPane.showMessageDialog(this, "The 'User id' field is a incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
				} else if (!numberOrNot(level)) {
					JOptionPane.showMessageDialog(this, "The 'Level' field is incorrect! Please enter a number", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					if ((!checkNum(Integer.parseInt(id)) || Integer.parseInt(id) < 0) || (!checkNum(Integer.parseInt(id)) && Integer.parseInt(id) < 0)) {
						JOptionPane.showMessageDialog(this, "number does not fit enough to be an id", "Error", JOptionPane.ERROR_MESSAGE);
					}
					else {
						Ex2.setGameLevel(Integer.parseInt(level));
						Ex2.client.start();
						this.setVisible(false);
						panel.setVisible(false);
					}


				}
			}

		}
		/**
		 * Checks whether the num is longer than 9 digits or not (used for the login id)
		 * @param id
		 * @return
		 */
		private static boolean checkNum(long id){
			long x;
			List<Long> list = new ArrayList<>();
			long y = id;
			while (y > 0){
				x = id%10;
				y = y/10;
				list.add(x);
			}
			if (list.size() != 9)
				return false;

			return true;
		}
		/**
		 * Check whether the input string contains only numbers or not
		 * @param input
		 * @return boolean
		 */
		private boolean numberOrNot(String input) {
			try {
				Integer.parseInt(input);
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}
	}
}