package projekat;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dialog.ModalityType;

public class GreskaDialog extends Dialog {
	
	Button okDugme = new Button();
	Aplikacija aplikacija;
	
	public GreskaDialog(Window owner, String kodGreske, ModalityType tipModaliteta) {
		super(owner);
		setModalityType(tipModaliteta);
		setBounds(owner.getX() + owner.getHeight() / 3 + 30, owner.getY() + owner.getWidth() / 3 + 30, 0, 0);
		setTitle("Greska");
		
		if(owner instanceof Aplikacija) {
			aplikacija = (Aplikacija)owner;
		}
		
		Panel glavniPanel = new Panel(new GridLayout(0, 1));
		
		Panel porukaPanel = new Panel();
		porukaPanel.add(new Label(kodGreske));
		
		Panel dugmePanel = new Panel();
		okDugme.setLabel("OK");
		okDugme.addActionListener((ae) -> {
			aplikacija.akcijaIzvrsena();
			dispose();
		});
		dugmePanel.add(okDugme);
		
		glavniPanel.add(porukaPanel);
		glavniPanel.add(dugmePanel);
		
		add(glavniPanel);
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				aplikacija.akcijaIzvrsena();
				dispose();
			}
		});
		
		pack();
		
		setVisible(true);
		
	}

}
