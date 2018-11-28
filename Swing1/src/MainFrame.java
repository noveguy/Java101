import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class MainFrame extends JFrame {
	private JButton btn;
	private TextPanel textPanel;
	private ToolBar toolBar;
	
	public MainFrame() {
		super("Hello World");
		
		setLayout(new BorderLayout());
		
		btn = new JButton("Click Me!");
		textPanel = new TextPanel();
		toolBar = new ToolBar();
		
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				textPanel.appendText("Hello\n");
			}
			
		});
		
		add(btn, BorderLayout.SOUTH);
		add(textPanel, BorderLayout.CENTER);
		add(toolBar, BorderLayout.NORTH);
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 500);
	}
}
