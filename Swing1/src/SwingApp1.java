import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class SwingApp1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				JFrame frame = new JFrame("Hello World");
				
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(600, 500);
				
			}
		});
	}
}
