package apps.player;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import player.GamePlayer;
import player.gamer.Gamer;
import player.gamer.statemachine.parametric.ParametricGamer;
import util.ui.NativeUI;
import apps.player.config.ConfigPanel;
import apps.player.detail.DetailPanel;
import apps.player.match.MatchPanel;
import apps.player.network.NetworkPanel;

/**
 * ParametricPlayerPanel is a stripped-down version of the PlayerPanel designed
 * for configuring and running "parametric" players, which are special players
 * designed to be "programmed" via configurable parameters rather than code.
 * This is designed for students who don't yet want the full complexity of the
 * regular player hosting system, and instead want to quickly get a player up
 * and running so they can run simple experiments on it. If you're not using
 * the ParametricPlayer, you won't be interested in this class.
 * 
 * @author schreib
 */
@SuppressWarnings("serial")
public final class ParametricPlayerPanel extends JPanel
{
	private static void createAndShowGUI(ParametricPlayerPanel playerPanel) {
		JFrame frame = new JFrame("Parametric Game Player");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setPreferredSize(new Dimension(1024, 768));
		frame.getContentPane().add(playerPanel);

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) throws IOException {
	    NativeUI.setNativeUI();
	    final ParametricPlayerPanel playerPanel = new ParametricPlayerPanel();
	    javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    createAndShowGUI(playerPanel);
			}
	    });
	}

	private final JButton createButton;
	private final JTabbedPane playersTabbedPane;
	
	private Integer defaultPort = 9147;
	
	public ParametricPlayerPanel()
	{
		super(new GridBagLayout());

		createButton = new JButton(createButtonMethod());
		playersTabbedPane = new JTabbedPane();

		JPanel playersPanel = new JPanel(new GridBagLayout());
		playersPanel.setBorder(new TitledBorder("Players"));

		playersPanel.add(playersTabbedPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));
		
		playersTabbedPane.add("", new IntroPanel());
		playersTabbedPane.setTabComponentAt(0,createButton);

		this.add(playersPanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));
	}
	
	public final class IntroPanel extends JPanel {
		public IntroPanel() {
			super(new GridBagLayout());
			this.add(new JLabel("Click the + button in the upper-left corner to start a player instance."), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 5, 5));
		}
	}

	
	private AbstractAction createButtonMethod() {
		return new AbstractAction("+") {
			public void actionPerformed(ActionEvent evt) {
				try {
					MatchPanel matchPanel = new MatchPanel();
					NetworkPanel networkPanel = new NetworkPanel();
					DetailPanel detailPanel = null;
					ConfigPanel configPanel = null;
					Gamer gamer = new ParametricGamer();
					detailPanel = gamer.getDetailPanel();
					configPanel = gamer.getConfigPanel();

					gamer.addObserver(matchPanel);
					gamer.addObserver(detailPanel);

					GamePlayer player = new GamePlayer(defaultPort, gamer);
					player.addObserver(networkPanel);
					player.start();					

					JTabbedPane tab = new JTabbedPane();
					tab.addTab("Configuration", configPanel);
					tab.addTab("Match", matchPanel);
					tab.addTab("Network", networkPanel);
					tab.addTab("Detail", detailPanel);
					playersTabbedPane.addTab("Port " + player.getGamerPort() + "", tab);
					playersTabbedPane.setSelectedIndex(playersTabbedPane.getTabCount()-1);
					
					defaultPort++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
}